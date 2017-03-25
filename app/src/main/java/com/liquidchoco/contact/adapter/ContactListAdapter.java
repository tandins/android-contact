package com.liquidchoco.contact.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liquidchoco.contact.R;
import com.liquidchoco.contact.model.Contact;
import com.liquidchoco.contact.singleton.InterfaceManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import io.realm.RealmList;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class ContactListAdapter extends RecyclerView.Adapter {
    private Context context;
    private RealmList<Contact> contactRealmList = new RealmList<>();
    private int totalFavorite = 0;
    private ListenerInterface listenerInterface;

    public ContactListAdapter(Context context, ListenerInterface listenerInterface) {
        this.context = context;
        this.listenerInterface = listenerInterface;
    }

    public interface ListenerInterface{
        void onItemTapped(Contact contact);
    }

    public void updateAdapter(RealmList<Contact> contactRealmList){
        this.contactRealmList = new RealmList<>();

        RealmList<Contact> favoriteRealmList = new RealmList<>();
        Set<Integer> favoriteContactIdSet = new HashSet<>();
        for(Contact contact : contactRealmList) {
            if(contact.isFavorite()) {
                favoriteContactIdSet.add(contact.getId());
                favoriteRealmList.add(contact);
            }
        }

        Collections.sort(favoriteRealmList, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return getName(lhs).compareToIgnoreCase(getName(rhs));
            }
        });

        this.contactRealmList = favoriteRealmList;

        totalFavorite = favoriteContactIdSet.size();

        RealmList<Contact> unfavoriteRealmList = new RealmList<>();
        for(Contact contact : contactRealmList) {
            if(!favoriteContactIdSet.contains(contact.getId())){
                unfavoriteRealmList.add(contact);
            }
        }

        Collections.sort(unfavoriteRealmList, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return getName(lhs).compareToIgnoreCase(getName(rhs));
            }
        });

        this.contactRealmList.addAll(favoriteRealmList.size(), unfavoriteRealmList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        final Contact contact = contactRealmList.get(position);

        if(position<totalFavorite && position==0) {
            itemHolder.initialTextView.setVisibility(View.INVISIBLE);
            itemHolder.favoriteIconImageView.setVisibility(View.VISIBLE);
            itemHolder.favoriteIconImageView.setImageDrawable(InterfaceManager.sharedInstance().setTint(InterfaceManager.sharedInstance().getDrawable(context, context.getResources(), R.drawable.ic_favourite_filled), Color.parseColor("#8BC34A")));
        }else if(position == totalFavorite) {
            itemHolder.initialTextView.setVisibility(View.VISIBLE);
            itemHolder.initialTextView.setText(InterfaceManager.sharedInstance().getInitialName(contact.getFirstName()));
            itemHolder.favoriteIconImageView.setVisibility(View.INVISIBLE);
        }else {
            itemHolder.initialTextView.setVisibility(View.INVISIBLE);
            itemHolder.favoriteIconImageView.setVisibility(View.INVISIBLE);
        }

        itemHolder.contactNameTextView.setText(contact.getFirstName() + " " + contact.getLastName());
//        itemHolder.contactNameTextView.setText(InterfaceManager.sharedInstance().getFirstLetterCapitalized(contact.getFirstName()) + " " + InterfaceManager.sharedInstance().getFirstLetterCapitalized(contact.getLastName()));
        itemHolder.contactInitialTextView.setText(InterfaceManager.sharedInstance().getInitialName(contact.getFirstName()));

        itemHolder.rootLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerInterface.onItemTapped(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactRealmList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        LinearLayout rootLinearLayout;
        TextView initialTextView;
        ImageView favoriteIconImageView;
        TextView contactInitialTextView;
        TextView contactNameTextView;

        public ItemHolder(View itemView) {
            super(itemView);
            rootLinearLayout = (LinearLayout) itemView.findViewById(R.id.item_contact_rootLinearLayout);
            initialTextView = (TextView) itemView.findViewById(R.id.item_contact_initialTextView);
            favoriteIconImageView = (ImageView) itemView.findViewById(R.id.item_contact_favoriteIconImageView);
            contactInitialTextView = (TextView) itemView.findViewById(R.id.item_contact_contactInitialTextView);
            contactNameTextView = (TextView) itemView.findViewById(R.id.item_contact_contactNameTextView);
        }
    }

    public String getName (Contact contact){
        return contact.getFirstName() + " " + contact.getLastName();
    }
}
