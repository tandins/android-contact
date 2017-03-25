package com.liquidchoco.contact;

import com.liquidchoco.contact.model.Contact;

/**
 * Created by dss-10 on 3/25/17.
 */

public interface ContactDetailPresenter {
    void onSuccess(Contact contact);

    void onFailed(String errorString);
}
