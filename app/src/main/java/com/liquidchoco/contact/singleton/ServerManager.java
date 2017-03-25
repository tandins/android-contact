package com.liquidchoco.contact.singleton;

import android.content.Context;

import com.liquidchoco.contact.AddContactPresenter;
import com.liquidchoco.contact.ContactPresenter;
import com.liquidchoco.contact.model.Contact;
import com.liquidchoco.contact.model.serverResponse.ContactResponse;
import com.squareup.okhttp.OkHttpClient;

import java.util.HashMap;
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

    public void postContacts(String firstName, String lastName, String phoneNumber, String email, final AddContactPresenter presenter) {
        ServiceInterface serviceInterface = host.create(ServiceInterface.class);

        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("first_name", firstName);
        parameter.put("last_name", lastName);
        parameter.put("phone_number", phoneNumber);
        parameter.put("email", email);
        parameter.put("favorite", false);

        serviceInterface.postContacts("application/json", parameter, new Callback<Contact>() {
            @Override
            public void success(Contact contact, Response response) {
                realm.beginTransaction();
                RealmList<Contact> contactRealmList = new RealmList<Contact>();
                ContactResponse contactResponse = realm.where(ContactResponse.class).equalTo("id", "0").findFirst();
                if(contactResponse!=null) {
                    if (!contactResponse.getContactRealmList().contains(contact)) {
                        contactRealmList = contactResponse.getContactRealmList();
                        contactRealmList.add(contact);
                    }
                    contactResponse.setContactRealmList(contactRealmList);
                    realm.copyToRealmOrUpdate(contactResponse);

                }
                realm.commitTransaction();
                presenter.onSuccess();
            }

            @Override
            public void failure(RetrofitError error) {
                presenter.onFailed("Unable to contact the server");
            }
        });
    }
}
