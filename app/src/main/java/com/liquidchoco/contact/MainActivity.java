package com.liquidchoco.contact;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.liquidchoco.contact.adapter.ContactListAdapter;
import com.liquidchoco.contact.model.Contact;
import com.liquidchoco.contact.model.serverResponse.ContactResponse;
import com.liquidchoco.contact.singleton.AppController;
import com.liquidchoco.contact.singleton.InterfaceManager;
import com.liquidchoco.contact.singleton.ServerManager;
import com.liquidchoco.contact.singleton.SettingsManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

public class MainActivity extends Activity implements Presenter {
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
    int contactIndex = 0;
    RealmList<Contact> contactRealmList = new RealmList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        realm = AppController.getInstance().realm;

        contactListAdapter = new ContactListAdapter(this, listenerInterface);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(contactListAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

                showLoading();
                contactListAdapter.updateAdapter(contactResponse.getContactRealmList());
                hideLoading();
            }
        }

        if (SettingsManager.getInstance().isFirstFetchData()) {
            SettingsManager.getInstance().setFirstFetchData(false);
            showLoading();
            ServerManager.getInstance().getContacts(contactPresenter);
        }
    }

    ContactPresenter contactPresenter = new ContactPresenter() {
        @Override
        public void onSuccess(ContactResponse contactResponse) {
//            hideLoading();
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            if (contactResponse != null) {
                if(contactResponse.getContactRealmList().size()>0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyTextView.setVisibility(View.GONE);

                    /*

                    DO THIS BASED ON SERVER RESPONSE,
                    SERVER DOESN'T RETURN FAVORITE BUT URL
                    SO IT WILL TOOK AWHILE TO LOAD CONTACT LIST
                    SINCE IT CALL DIFFERENT SERVER API

                     */

                    getContactDetail(0, contactResponse.getContactRealmList());

                    /*

                    DO THIS BASED ON API DOCUMENTATION
                    contactListAdapter.updateAdapter(contactResponse.getContactRealmList());

                    */

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
    public boolean validated() {
        return false;
    }

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

    @OnClick(R.id.activity_main_addContactImageView)
    public void addContactTapped(){
        startActivity(new Intent(this, AddContactActivity.class));
    }

    ContactListAdapter.ListenerInterface listenerInterface = new ContactListAdapter.ListenerInterface() {
        @Override
        public void onItemTapped(Contact contact) {
            onPause();
            SettingsManager.getInstance().setContact(contact);
            startActivity(new Intent(MainActivity.this, ContactDetailActivity.class));
        }
    };

    private void getContactDetail(int contactIndex, RealmList<Contact> contactRealmList) {
        this.contactRealmList = contactRealmList;
        this.contactIndex = contactIndex;

        if(contactIndex<contactRealmList.size()) {
            ServerManager.getInstance().getContactDetail(contactRealmList.get(contactIndex).getId(), contactDetailPresenter);
        }else {
            hideLoading();
            reloadLocalData();
        }
    }

    ContactDetailPresenter contactDetailPresenter = new ContactDetailPresenter() {
        @Override
        public void onSuccess(Contact contact) {
            getContactDetail(++contactIndex, contactRealmList);
        }

        @Override
        public void onFailed(String errorString) {
            showErrorMessage(errorString);
        }
    };
}
