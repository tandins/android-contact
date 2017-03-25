package com.liquidchoco.contact.singleton;

import com.liquidchoco.contact.model.Contact;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public interface ServiceInterface {
    @GET("/contacts.json")
    void getContacts(Callback<List<Contact>> callback);

    @POST("/contacts.json")
    void postContacts(@Header("Content-Type") String contentType,
                      @Body HashMap<String, Object> object,
                      Callback<Contact> callback);
}
