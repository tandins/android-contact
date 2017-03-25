package com.liquidchoco.contact;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.liquidchoco.contact.model.Contact;
import com.liquidchoco.contact.singleton.AppController;
import com.liquidchoco.contact.singleton.InterfaceManager;
import com.liquidchoco.contact.singleton.ServerManager;
import com.liquidchoco.contact.singleton.SettingsManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.realm.Realm;

/**
 * Created by dss-10 on 3/25/17.
 */

public class ContactDetailActivity extends Activity implements ContactDetailActivityPresenter {
    @BindView(R.id.activity_contact_detail_rootFrameLayout)
    FrameLayout rootFrameLayout;
    @BindView(R.id.activity_contact_detail_contactImageView)
    ImageView contactImageView;
    @BindView(R.id.activity_contact_detail_overlayView)
    View overlayView;
    @BindView(R.id.activity_contact_detail_contactNameTextView)
    TextView contactNameTextView;
    @BindView(R.id.activity_contact_detail_phoneTextView)
    TextView phoneTextView;
    @BindView(R.id.activity_contact_detail_emailTextView)
    TextView emailTextView;

    @BindView(R.id.activity_contact_detail_favoriteImageView)
    ImageView favoriteImageView;

    Contact contact;
    boolean isFavorited = false;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
        ButterKnife.bind(this);

        realm = AppController.getInstance().realm;

        contact = SettingsManager.getInstance().getContact();
        isFavorited = contact.isFavorite();

        reloadLocalData();
    }

    @OnClick(R.id.activity_contact_detail_favoriteImageView)
    public void favoriteTapped() {
        isFavorited = !isFavorited;

        realm.beginTransaction();
        contact.setFavorite(isFavorited);
        realm.copyToRealmOrUpdate(contact);
        realm.commitTransaction();
        populateData(contact);
        ServerManager.getInstance().updateContact(contact, contactDetailPresenter);
    }

    @OnClick (R.id.activity_contact_detail_editImageView)
    public void editTapped() {
        SettingsManager.getInstance().setContact(contact);
        Intent intent = new Intent(this, AddContactActivity.class);
        intent.putExtra("state", "editExistingContact");
        startActivity(intent);
    }

    @OnClick (R.id.activity_contact_detail_moreImageView)
    public void moreTapped() {

    }

    @OnClick (R.id.activity_contact_detail_messageIconImageView)
    public void messageTapped() {
        Intent messageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"+ contact.getPhoneNumber()));
        startActivity(messageIntent);
    }

    @OnClick (R.id.activity_contact_detail_phoneLinearLayout)
    public void callTapped() {
        boolean isEligibileToMakePhoneCall = false;

        if(isTelephonyEnabled()) {
            ArrayList<String> permissionArrayList = new ArrayList<>();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                permissionArrayList.add(android.Manifest.permission.CALL_PHONE);
            }

            if (permissionArrayList.size() > 0) {
                isEligibileToMakePhoneCall = false;
                String[] permissions = permissionArrayList.toArray(new String[permissionArrayList.size()]);
                ActivityCompat.requestPermissions(this, permissions, 1);
            }else {
                isEligibileToMakePhoneCall = true;
            }

            if(isEligibileToMakePhoneCall) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+contact.getPhoneNumber()));
                startActivity(intent);
            }
        }else {
            showErrorMessage("Sorry, your device is not supported to do phone call");
        }
    }

    @OnLongClick (R.id.activity_contact_detail_phoneLinearLayout)
    public boolean copyPhoneNumber() {
        ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(phoneTextView.getText().toString());

        Toast.makeText(this, "Phone number copied to clipboard", Toast.LENGTH_SHORT).show();
        return true;
    }

    @OnClick (R.id.activity_contact_detail_emailLinearLayout)
    public void emailTapped() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", contact.getEmail(), null));
        startActivity(emailIntent);
    }

    @OnLongClick (R.id.activity_contact_detail_emailLinearLayout)
    public boolean copyEmail() {
        ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(emailTextView.getText().toString());

        Toast.makeText(this, "Email copied to clipboard", Toast.LENGTH_SHORT).show();
        return true;
    }

    private void reloadLocalData(){
        Contact theContact = realm.where(Contact.class).equalTo("id", contact.getId()).findFirst();
        if(theContact!=null) {
            populateData(theContact);
        }

        showLoading();
        ServerManager.getInstance().getContactDetail(contact.getId(), contactDetailPresenter);
    }

    private void populateData(Contact theContact) {
        this.contact = theContact;
        isFavorited = contact.isFavorite();

        Glide.with(this).load(contact.getProfilePic()).placeholder(R.drawable.ic_profile_large).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                contactImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                overlayView.setVisibility(View.GONE);
                return false;
            }
        }).into(contactImageView);

        contactNameTextView.setText(contact.getFirstName() + " " + contact.getLastName());
        phoneTextView.setText(contact.getPhoneNumber());
        emailTextView.setText(contact.getEmail());

        if(contact.isFavorite()) {
            favoriteImageView.setImageDrawable(InterfaceManager.sharedInstance().getDrawable(this, getResources(), R.drawable.ic_favourite_filled));
        }else {
            favoriteImageView.setImageDrawable(InterfaceManager.sharedInstance().getDrawable(this, getResources(), R.drawable.ic_favourite));
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

    ContactDetailPresenter contactDetailPresenter = new ContactDetailPresenter() {
        @Override
        public void onSuccess(Contact contact) {
            hideLoading();
            populateData(contact);
        }

        @Override
        public void onFailed(String errorString) {
            hideLoading();
            showErrorMessage(errorString);
        }
    };

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
            callTapped();
        }

        return;
    }

    private boolean isTelephonyEnabled(){
        TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        return tm != null && tm.getSimState()==TelephonyManager.SIM_STATE_READY;
    }
}
