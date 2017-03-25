package com.liquidchoco.contact;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public interface MainActivityPresenter {
    void showLoading();

    void hideLoading();

    void showErrorMessage(String errorString);
}