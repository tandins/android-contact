package com.liquidchoco.contact;

import com.liquidchoco.contact.model.Contact;
import com.liquidchoco.contact.model.serverResponse.ContactResponse;

import io.realm.RealmList;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public interface Presenter {
    boolean validated();

    void showLoading();

    void hideLoading();

    void showErrorMessage(String errorString);

    public interface ContactPresenter {
        void onSuccess(ContactResponse contactResponse);

        void onFailed(String errorString);
    }

    public interface ContactDetailPresenter {
        void onSuccess(Contact contact);

        void onFailed(String errorString);
    }
}
