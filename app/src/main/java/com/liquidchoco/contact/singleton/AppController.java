package com.liquidchoco.contact.singleton;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class AppController extends Application {
    private static AppController mInstance = null;
    private static Context context;
    public Realm realm;

    public static AppController getInstance() {
        if (mInstance == null) {
            mInstance = new AppController();
        }
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppController.context = getApplicationContext();
        mInstance = this;

        if(context != null) {
            Log.v("AppController","context available");
            realm = buildDatabase();
            SettingsManager.getInstance().getAllPref();
        }else{
            Log.v("AppController","context null");
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Realm buildDatabase() {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();

        try {
            return Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e) {
            try {
                Realm.deleteRealm(realmConfiguration);
                //Realm file has been deleted.
                return Realm.getInstance(realmConfiguration);
            } catch (Exception ex) {
                throw ex;
                //No Realm file to remove.
            }
        }
    }

    public static Context getAppContext() {
        return AppController.context;
    }

}
