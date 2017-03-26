package com.liquidchoco.contact.singleton;

import com.liquidchoco.contact.model.Contact;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public interface ServiceInterface {
    @GET("/contacts.json")
    Observable<List<Contact>> getContacts();

    @POST("/contacts.json")
    Observable<Contact> postContact(@Header("Content-Type") String contentType,
                     @Body HashMap<String, Object> object);

    @GET("/contacts/{id}.json")
    Observable<Contact> getContactDetail(@Path("id") int contactId);

    @PUT("/contacts/{id}.json")
    Observable<Contact> putContact(@Path("id") int contactId,
                    @Header("Content-Type") String contentType,
                    @Body HashMap<String, Object> object);
}
