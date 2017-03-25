package com.liquidchoco.contact;

import com.liquidchoco.contact.model.serverResponse.ContactResponse;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public interface ContactPresenter {
    void onSuccess(ContactResponse contactResponse);

    void onFailed(String errorString);
}
