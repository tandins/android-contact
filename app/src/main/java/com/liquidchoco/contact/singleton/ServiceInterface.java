package com.liquidchoco.contact.singleton;

import com.liquidchoco.contact.model.Contact;
import com.liquidchoco.contact.model.serverResponse.ContactResponse;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public interface ServiceInterface {
    @GET("/contacts.json")
    void getContacts(Callback<List<Contact>> callback);
}
