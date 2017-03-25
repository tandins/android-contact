package com.liquidchoco.contact;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.liquidchoco.contact.adapter.ContactListAdapter;
import com.liquidchoco.contact.model.serverResponse.ContactResponse;
import com.liquidchoco.contact.singleton.AppController;
import com.liquidchoco.contact.singleton.InterfaceManager;
import com.liquidchoco.contact.singleton.ServerManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MainActivity extends Activity implements MainActivityPresenter {
    @BindView(R.id.activity_main_rootFrameLayout)
    FrameLayout rootFrameLayout;

    @BindView(R.id.activity_main_recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.activity_main_emptyTextView)
    TextView emptyTextView;

    Realm realm;
    ContactListAdapter contactListAdapter;
    LinearLayoutManager linearLayoutManager;
    boolean isHasLocalData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        realm = AppController.getInstance().realm;

        contactListAdapter = new ContactListAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contactListAdapter);

        reloadLocalData();
    }

    public void reloadLocalData(){
        isHasLocalData = false;
        recyclerView.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
        ContactResponse contactResponse = realm.where(ContactResponse.class).equalTo("id", "0").findFirst();
        if(contactResponse!=null) {
            if(contactResponse.getContactRealmList().size()>0) {
                isHasLocalData = true;
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);

                contactListAdapter.updateAdapter(contactResponse.getContactRealmList());
            }
        }

        showLoading();
        ServerManager.getInstance().getContacts(contactPresenter);
    }

    ContactPresenter contactPresenter = new ContactPresenter() {
        @Override
        public void onSuccess(ContactResponse contactResponse) {
            hideLoading();
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            if (contactResponse != null) {
                if(contactResponse.getContactRealmList().size()>0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyTextView.setVisibility(View.GONE);

                    contactListAdapter.updateAdapter(contactResponse.getContactRealmList());
                }
            }
        }

        @Override
        public void onFailed(String errorString) {
            hideLoading();
            showErrorMessage(errorString);
            if(isHasLocalData) {
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            }else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void showLoading() {
        InterfaceManager.sharedInstance().showLoading(rootFrameLayout, this);
    }

    @Override
    public void hideLoading() {
        InterfaceManager.sharedInstance().hideLoading();
    }

    @Override
    public void showErrorMessage(String errorString) {
        InterfaceManager.sharedInstance().showErrorMessage(this, errorString);
    }
}
