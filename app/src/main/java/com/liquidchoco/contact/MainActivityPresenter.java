package com.liquidchoco.contact;

/**
 * Created by dss-10 on 3/25/17.
 */

public interface MainActivityPresenter {
    void showLoading();

    void hideLoading();

    void showErrorMessage(String errorString);
}