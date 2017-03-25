package com.liquidchoco.contact.singleton;

import android.content.Context;
import android.util.Log;

import com.liquidchoco.contact.ContactPresenter;
import com.liquidchoco.contact.model.Contact;
import com.liquidchoco.contact.model.serverResponse.ContactResponse;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class ServerManager{
    private static ServerManager instance;
    private RestAdapter host;
    String accept = "application/json";

    private Realm realm;
    private Context context;

    /**
     * Returns singleton class instance
     */
    public static ServerManager getInstance() {
        if (instance == null) {
            synchronized (ServerManager.class) {
                if (instance == null) {
                    instance = new ServerManager();
                }
            }
        }
        return instance;
    }

    public ServerManager() {
        realm = AppController.getInstance().realm;
        context = AppController.getAppContext();

        OkHttpClient client = new OkHttpClient();
        client.setReadTimeout(30, TimeUnit.SECONDS);
        client.setConnectTimeout(30, TimeUnit.SECONDS);

        host = new RestAdapter.Builder().setEndpoint("http://gojek-contacts-app.herokuapp.com")
                .setLogLevel(RestAdapter.LogLevel.NONE).setClient(new OkClient(client)).build();
    }

    public void getContacts(final ContactPresenter presenter){
        ServiceInterface serviceInterface = host.create(ServiceInterface.class);
        serviceInterface.getContacts(new Callback<List<Contact>>() {
            @Override
            public void success(List<Contact> contacts, Response response) {
                ContactResponse contactResponse = new ContactResponse(contacts);

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(contactResponse);
                realm.commitTransaction();

                presenter.onSuccess(contactResponse);
            }

            @Override
            public void failure(RetrofitError error) {
                presenter.onFailed("Unable to contact the server");
            }
        });
    }

}
