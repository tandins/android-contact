package com.liquidchoco.contact;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity implements MainActivityPresenter {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showErrorMessage(String errorString) {

    }
}
