package com.liquidchoco.contact.singleton;

import android.content.Context;

import com.liquidchoco.contact.ContactDetailPresenter;
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
    private String contentType = "application/json";
    private String errorString = "Unable to contact the server";

    private Realm realm;
    private Context context;
    private ServiceInterface serviceInterface;

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

        serviceInterface = host.create(ServiceInterface.class);
    }

    public void getContacts(final ContactPresenter presenter){
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
                presenter.onFailed(errorString);
            }
        });
    }

    public void postContacts(String firstName, String lastName, String phoneNumber, String email, final ContactDetailPresenter presenter) {
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("first_name", firstName);
        parameter.put("last_name", lastName);
        parameter.put("phone_number", phoneNumber);
        parameter.put("email", email);
        parameter.put("favorite", false);

        serviceInterface.postContact(contentType, parameter, new Callback<Contact>() {
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
                presenter.onSuccess(contact);
            }

            @Override
            public void failure(RetrofitError error) {
                presenter.onFailed(errorString);
            }
        });
    }

    public void getContactDetail(int contactId, final ContactDetailPresenter presenter){
        serviceInterface.getContactDetail(contactId, new Callback<Contact>() {
            @Override
            public void success(Contact contact, Response response) {
                presenter.onSuccess(contact);
            }

            @Override
            public void failure(RetrofitError error) {
                presenter.onFailed(errorString);
            }
        });
    }

    public void updateContact(Contact contact, final ContactDetailPresenter presenter){
        HashMap<String, Object> parameter = new HashMap<>();
        parameter.put("first_name", contact.getFirstName());
        parameter.put("last_name", contact.getLastName());
        parameter.put("phone_number", contact.getPhoneNumber());
        parameter.put("email", contact.getEmail());
        parameter.put("profile_pic", contact.getProfilePic());
        parameter.put("favorite", contact.isFavorite());

        serviceInterface.putContact(contact.getId(), contentType, parameter, new Callback<Contact>() {
            @Override
            public void success(Contact contact, Response response) {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(contact);
                realm.commitTransaction();

                presenter.onSuccess(contact);
            }

            @Override
            public void failure(RetrofitError error) {
                presenter.onFailed(errorString);
            }
        });

    }
}
