package com.liquidchoco.contact.singleton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.liquidchoco.contact.R;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class InterfaceManager {
    private static InterfaceManager INTERFACEMANAGER = null;
    private Boolean isErrMsgShown = false;
    Snackbar snackbar;
    View loadingFrameLayout;

    public static InterfaceManager sharedInstance() {
        if (INTERFACEMANAGER == null) {
            INTERFACEMANAGER = new InterfaceManager();

        }
        return INTERFACEMANAGER;
    }

    public InterfaceManager() {

    }

    public boolean isOnline() {
        try {
            ConnectivityManager cm = (ConnectivityManager) AppController.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    public void showErrorMessage(Context ctx, String errorMessage) {
        if (isErrMsgShown == false) {
            isErrMsgShown = true;
            AlertDialog.Builder alrt;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alrt = new AlertDialog.Builder(ctx, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                alrt = new AlertDialog.Builder(ctx);
            }
            alrt.setTitle("Network Error").setMessage(errorMessage);
            alrt.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    isErrMsgShown = false;
                }
            });
            AlertDialog alertDialog = alrt.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button positiveButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                    Button negativeButton = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);

                    positiveButton.setTextColor(Color.parseColor("#8BC34A"));
                    negativeButton.setTextColor(Color.parseColor("#8BC34A"));
                }
            });

            alertDialog.show();
        } else {

        }
    }

    public void showLoading(FrameLayout rootFrameLayout, Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loadingFrameLayout = inflater.inflate(R.layout.item_loading, null);
        ProgressBar progressBar = (ProgressBar) loadingFrameLayout.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        rootFrameLayout.addView(loadingFrameLayout);
        loadingFrameLayout.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        if (loadingFrameLayout != null) {
            loadingFrameLayout.setVisibility(View.GONE);
        }
    }

    public Typeface getRobotoRegularTypeFace() {
        Typeface type = Typeface.createFromAsset(AppController.getAppContext().getAssets(), "fonts/Roboto-Regular.ttf");
        return type;
    }

    public Typeface getRobotoMediumTypeFace() {
        Typeface type = Typeface.createFromAsset(AppController.getAppContext().getAssets(), "fonts/Roboto-Medium.ttf");
        return type;
    }

    public Drawable getDrawable(Context context, Resources resources, int id){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(id, context.getTheme());
        } else {
            return resources.getDrawable(id);
        }
    }

    public Drawable setTint(Drawable drawable, int color){
        final Drawable newDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(newDrawable, color);
        return  newDrawable;
    }

    public String getFirstLetterCapitalized(String string){
        if(string!=null) {
            String[] nameCollection = string.split(" ");
            string = "";
            for (int i = 0; i < nameCollection.length; i++) {
                if(nameCollection[i].length()==1){
                    nameCollection[i] = nameCollection[i].substring(0, 1).toUpperCase();
                }else {
                    nameCollection[i] = nameCollection[i].substring(0, 1).toUpperCase() + nameCollection[i].substring(1, nameCollection[i].length()).toLowerCase();
                }

                if (i == 0) {
                    string += nameCollection[i];
                } else {
                    string += " " + nameCollection[i];
                }
            }
            return string;
        }else {
            return "";
        }
    }

    public String getInitialName(String string){
        if(string!=null) {
            String[] nameCollection = string.split(" ");
            if(nameCollection.length>0) {
                string = nameCollection[0].substring(0, 1).toUpperCase();
            }
            return string;
        }else {
            return "";
        }
    }
}
