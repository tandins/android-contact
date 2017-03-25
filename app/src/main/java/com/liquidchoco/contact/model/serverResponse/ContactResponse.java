package com.liquidchoco.contact.model.serverResponse;

import com.liquidchoco.contact.model.Contact;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class ContactResponse extends RealmObject {
    @PrimaryKey
    private String id;
    private RealmList<Contact> contactRealmList = new RealmList<>();

    public ContactResponse() {
    }

    public ContactResponse(List<Contact> contacts) {
        setId("0");
        RealmList<Contact> theContactRealmList = new RealmList<>();
        for(Contact contact : contacts) {
            theContactRealmList.add(contact);
        }
        setContactRealmList(theContactRealmList);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmList<Contact> getContactRealmList() {
        return contactRealmList;
    }

    public void setContactRealmList(RealmList<Contact> contactRealmList) {
        this.contactRealmList = contactRealmList;
    }
}
