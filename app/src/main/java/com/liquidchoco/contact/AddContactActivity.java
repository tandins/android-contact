package com.liquidchoco.contact;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v13.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.liquidchoco.contact.model.Contact;
import com.liquidchoco.contact.singleton.InterfaceManager;
import com.liquidchoco.contact.singleton.ServerManager;
import com.liquidchoco.contact.singleton.SettingsManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class AddContactActivity extends Activity implements AddContactActivityPresenter {
    private Boolean isEligible = false;
    private File photoFile = null;
    private File croppedPhotoFile = null;
    private Uri photoUri;
    private List<String> invalidFields = new ArrayList<>();
    private String state = "addNewContact";

    @BindView(R.id.activity_add_contact_rootFrameLayout)
    FrameLayout rootFrameLayout;

    @BindView(R.id.activity_add_contact_titleTextView)
    TextView titleTextView;

    @BindView(R.id.activity_add_contact_contactImageView)
    ImageView contactImageView;

    @BindView(R.id.activity_add_contact_nameEditText)
    EditText nameEditText;

    @BindView(R.id.activity_add_contact_phoneEditText)
    EditText phoneEditText;

    @BindView(R.id.activity_add_contact_emailEditText)
    EditText emailEditText;

    @BindView(R.id.activity_add_contact_invalidNameTextView)
    TextView invalidNameTextView;

    @BindView(R.id.activity_add_contact_invalidPhoneTextView)
    TextView invalidPhoneTextView;

    @BindView(R.id.activity_add_contact_invalidEmailTextView)
    TextView invalidEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);

        contactImageView.getLayoutParams().height = contactImageView.getLayoutParams().width * 17 / 10;

        titleTextView.setText("Add New Contact");
        if(getIntent().hasExtra("state")) {
            if(getIntent().getStringExtra("state").equalsIgnoreCase("editExistingContact")) {
                state = getIntent().getStringExtra("state");
                titleTextView.setText("Edit Contact");
                populateInitView(SettingsManager.getInstance().getContact());
            }
        }
    }

    @OnClick(R.id.activity_add_contact_backImageView)
    public void backTapped(){
        int difference = 0;
        if(state.equalsIgnoreCase("addNewContact")) {
            if(nameEditText.getText().toString().length()>0) {
                difference++;
            }

            if(phoneEditText.getText().toString().length()>0) {
                difference++;
            }

            if(emailEditText.getText().toString().length()>0) {
                difference++;
            }
        }else {
            Contact currentContact = SettingsManager.getInstance().getContact();
            if(!nameEditText.getText().toString().equalsIgnoreCase(currentContact.getFirstName() + " " + currentContact.getLastName())) {
                difference++;
            }

            if(!phoneEditText.getText().toString().equalsIgnoreCase(currentContact.getPhoneNumber())) {
                difference++;
            }

            if(!emailEditText.getText().toString().equalsIgnoreCase(currentContact.getEmail())) {
                difference++;
            }
        }

        if(difference>0) {
            InterfaceManager.sharedInstance().showPopUpAlert(this, "Are you sure want to discard changes?");
        }else {
            finish();
        }
    }

    @OnClick (R.id.activity_add_contact_saveImageView)
    public void saveTapped(){
        invalidNameTextView.setVisibility(View.GONE);
        invalidPhoneTextView.setVisibility(View.GONE);
        invalidEmailTextView.setVisibility(View.GONE);

        if(validated()){
            String name = nameEditText.getText().toString();
            String[] names = name.split(" ");
            String firstName = "";
            String lastName = "";
            for(int i=0; i<names.length; i++) {
                if(i==0) {
                    firstName = names[i];
                }else {
                    if(i == 1) {
                        lastName = names[i];
                    }else {
                        lastName += names[i];
                    }
                }
            }

            String phone = phoneEditText.getText().toString();
            String email = emailEditText.getText().toString();

            showLoading();
            ServerManager.getInstance().postContacts(firstName, lastName, phone, email, addContactPresenter);

        }else {
            if(invalidFields.size()>0) {
                if(invalidFields.contains("name")) {
                    invalidNameTextView.setVisibility(View.VISIBLE);
                }

                if(invalidFields.contains("phone")) {
                    invalidPhoneTextView.setVisibility(View.VISIBLE);
                }

                if(invalidFields.contains("email")) {
                    invalidEmailTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @OnClick (R.id.activity_add_contact_addPhotoImageView)
    public void addPhotoTapped(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence options[] = new CharSequence[]{"Take a Picture", "Import from Gallery"};

        builder.setTitle("Add Profile Picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                photoFile = null;
                try{
                    photoFile = createImageFile();

                }catch (IOException ex){
                    Log.e("err",ex.toString());

                }
                takePhoto(which);
            }
        });
        builder.show();
    }

    private void takePhoto(int which){
        ArrayList<String> permissionArrayList = new ArrayList<>();
        switch (which){
            case 0:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    permissionArrayList.add(android.Manifest.permission.CAMERA);
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionArrayList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                if (permissionArrayList.size() > 0) {
                    String[] permissions = permissionArrayList.toArray(new String[permissionArrayList.size()]);
                    ActivityCompat.requestPermissions(this, permissions, 0);
                } else {
                    isEligible = true;
                }

                if(isEligible) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (photoFile != null) {
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        startActivityForResult(cameraIntent, 0);
                    }
                }

                break;
            case 1:
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permissionArrayList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                if (permissionArrayList.size() > 0) {
                    String[] permissions = permissionArrayList.toArray(new String[permissionArrayList.size()]);
                    ActivityCompat.requestPermissions(this, permissions, 1);
                } else {
                    isEligible = true;
                }

                if(isEligible){
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 1);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        int totalGranted = 0;
        for(int i=0; i<permissions.length; i++) {
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                totalGranted++;
            }
        }

        if(permissions.length == totalGranted) {
            isEligible = true;
            takePhoto(requestCode);
        }

        return;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName, ".jpeg", storageDir
        );
        return image;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final int CAMERA_PHOTO = 0;
        final int GALLERY_PHOTO = 1;
        final int CROP_PHOTO = 2;

        final android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(photoFile!=null) {
                    photoUri = Uri.fromFile(photoFile);
                }

                String imagePath = null;
                if (resultCode == RESULT_OK && data!=null) {
                    if (requestCode == CAMERA_PHOTO) {
                        cropCapturedImage(Uri.fromFile(photoFile));
                    } else if (requestCode == GALLERY_PHOTO) {
                        cropCapturedImage(data.getData());
                    } else if (requestCode == CROP_PHOTO){
                        Uri selectedImageURI = Uri.fromFile(croppedPhotoFile);
                        Bitmap croppedBitmap ;
                        try {
                            if (photoUri != null) {
                                croppedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageURI);
                            } else {
                                croppedBitmap = (Bitmap) data.getExtras().getParcelable("data");
                            }
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            contactImageView.setImageBitmap(croppedBitmap);
                            contactImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            photoUri = getImageUri(croppedBitmap);
                        }catch (IOException e){
                            Log.d("error", " IOException");
                        }
                    }

                } else if(resultCode == RESULT_OK && photoUri!=null){
                    if (requestCode == CAMERA_PHOTO) {
                        cropCapturedImage(Uri.fromFile(photoFile));
                    } else if (requestCode == GALLERY_PHOTO) {
                        cropCapturedImage(data.getData());
                    } else if (requestCode == CROP_PHOTO){
                        photoUri = Uri.fromFile(croppedPhotoFile);
                        Bitmap croppedBitmap ;
                        try {
                            if (photoUri != null) {
                                croppedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                            } else {
                                croppedBitmap = (Bitmap) data.getExtras().getParcelable("data");
                            }
                            if(croppedBitmap!=null) {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                contactImageView.setImageBitmap(croppedBitmap);
                                contactImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                photoUri = getImageUri(croppedBitmap);
                            }
                        }catch (IOException e){
                            Log.d("error", " IOException");
                        }
                    }
                }
            }
        },1000);
    }

    public void cropCapturedImage(Uri picUri){
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 17);
            cropIntent.putExtra("aspectY", 10);
            cropIntent.putExtra("outputX", 1700);
            cropIntent.putExtra("outputY", 1000);
            cropIntent.putExtra("scale", true);
            cropIntent.putExtra("return-data", false);
            croppedPhotoFile = createImageFile();
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(croppedPhotoFile));
            startActivityForResult(cropIntent, 2);
        }
        catch (ActivityNotFoundException anfe) {
            Toast.makeText(this, "Device not supported", Toast.LENGTH_SHORT).show();
        }catch (IOException e){

        }
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean validated() {
        String name = nameEditText.getText().toString();
        String phone = phoneEditText.getText().toString();
        String email = emailEditText.getText().toString();

        List<String> invalidFields = new ArrayList<>();

        if(!name.contains(" ")) {
            invalidFields.add("name");
        }else{
            String[] nameArray = name.split(" ");
            for(int i=0; i<nameArray.length; i++) {
                if((i==0 || i ==1) && nameArray[i].length() <= 3){
                    invalidFields.add("name");
                }
            }
        }

        if(phone.length()<10) {
            invalidFields.add("phone");
        }

        if(!isValidEmail(email)){
            invalidFields.add("email");
        }

        this.invalidFields = invalidFields;

        if(invalidFields.size()!=0) {
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void showLoading() {
        InterfaceManager.sharedInstance().showLoading(rootFrameLayout, this);
    }

    @Override
    public void hideLoading() {
        InterfaceManager.sharedInstance().hideLoading();
    }

    @Override
    public void showErrorMessage(String errorString) {
        InterfaceManager.sharedInstance().showErrorMessage(this, errorString);
    }

    public Boolean isValidEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    AddContactPresenter addContactPresenter = new AddContactPresenter() {
        @Override
        public void onSuccess() {
            hideLoading();
            finish();
        }

        @Override
        public void onFailed(String errorString) {
            hideLoading();
            showErrorMessage(errorString);
        }
    };

    private void populateInitView(Contact contact) {
        Glide.with(this).load(contact.getProfilePic()).placeholder(R.drawable.ic_profile_large).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                contactImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return false;
            }
        }).into(contactImageView);

        nameEditText.setText(contact.getFirstName() + " " + contact.getLastName());
        phoneEditText.setText(contact.getPhoneNumber());
        emailEditText.setText(contact.getEmail());
    }
}
