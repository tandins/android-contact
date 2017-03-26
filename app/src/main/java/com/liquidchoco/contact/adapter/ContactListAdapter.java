package com.liquidchoco.contact.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liquidchoco.contact.R;
import com.liquidchoco.contact.model.Contact;
import com.liquidchoco.contact.singleton.InterfaceManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmList;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class ContactListAdapter extends RecyclerView.Adapter{
    private Context context;
    private RealmList<Contact> contactRealmList = new RealmList<>();
    private int totalFavorite = 0;
    private ListenerInterface listenerInterface;
    private Set<String> initialSet = new HashSet<>();
    private Map<Integer, String> alphabetHashMap = new HashMap<>();

    public ContactListAdapter(Context context, ListenerInterface listenerInterface) {
        this.context = context;
        this.listenerInterface = listenerInterface;
    }

    public interface ListenerInterface{
        void onItemTapped(Contact contact);
    }

    public void updateAdapter(RealmList<Contact> contactRealmList){
        this.contactRealmList = new RealmList<>();
        this.initialSet = new HashSet<>();
        this.alphabetHashMap = new HashMap<>();

//        ADD FAVORITE
        RealmList<Contact> favoriteRealmList = new RealmList<>();
        Set<Integer> favoriteContactIdSet = new HashSet<>();
        for(Contact contact : contactRealmList) {
            if(contact.isFavorite()) {
                favoriteContactIdSet.add(contact.getId());
                favoriteRealmList.add(contact);
            }
        }
//        SORT FAVORITE ALPHABETICALLY
        Collections.sort(favoriteRealmList, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return getName(lhs).compareToIgnoreCase(getName(rhs));
            }
        });
        this.contactRealmList = favoriteRealmList;

//        ADD UNFAVORITE
        totalFavorite = favoriteContactIdSet.size();
        RealmList<Contact> unfavoriteRealmList = new RealmList<>();
        for(Contact contact : contactRealmList) {
            if(!favoriteContactIdSet.contains(contact.getId())){
                unfavoriteRealmList.add(contact);
            }
        }
//        SORT UNFAVORITE ALPHABETICALLY
        Collections.sort(unfavoriteRealmList, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return getName(lhs).compareToIgnoreCase(getName(rhs));
            }
        });
        this.contactRealmList.addAll(favoriteRealmList.size(), unfavoriteRealmList);

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ItemHolder itemHolder = (ItemHolder) holder;
        final Contact contact = contactRealmList.get(position);

        itemHolder.initialFrameLayout.setBackground(InterfaceManager.sharedInstance().setTint(InterfaceManager.sharedInstance().getDrawable(context, context.getResources(), R.drawable.circle_green), getRandomBackgroundColor()));

        if(position >= totalFavorite) {
            if (!initialSet.contains(InterfaceManager.sharedInstance().getInitialName(contact.getFirstName()))) {
                initialSet.add(InterfaceManager.sharedInstance().getInitialName(contact.getFirstName()));
                alphabetHashMap.put(position, InterfaceManager.sharedInstance().getInitialName(contact.getFirstName()));
            }
        }

        if(position<totalFavorite && position==0) {
            itemHolder.initialTextView.setVisibility(View.INVISIBLE);
            itemHolder.favoriteIconImageView.setVisibility(View.VISIBLE);
            itemHolder.favoriteIconImageView.setImageDrawable(InterfaceManager.sharedInstance().getDrawable(context, context.getResources(), R.drawable.ic_star));
        }else if(alphabetHashMap.containsKey(position)) {
            itemHolder.initialTextView.setVisibility(View.VISIBLE);
            itemHolder.initialTextView.setText(alphabetHashMap.get(position));
            itemHolder.favoriteIconImageView.setVisibility(View.INVISIBLE);
        } else {
            itemHolder.initialTextView.setVisibility(View.INVISIBLE);
            itemHolder.favoriteIconImageView.setVisibility(View.INVISIBLE);
        }

        Glide.with(context).load(contact.getProfilePic()).into(itemHolder.contactProfilePictureCircleImageView);
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
        FrameLayout initialFrameLayout;
        TextView initialTextView;
        CircleImageView contactProfilePictureCircleImageView;
        ImageView favoriteIconImageView;
        TextView contactInitialTextView;
        TextView contactNameTextView;

        public ItemHolder(View itemView) {
            super(itemView);
            rootLinearLayout = (LinearLayout) itemView.findViewById(R.id.item_contact_rootLinearLayout);
            initialFrameLayout = (FrameLayout) itemView.findViewById(R.id.item_contact_initialFrameLayout);
            initialTextView = (TextView) itemView.findViewById(R.id.item_contact_initialTextView);
            contactProfilePictureCircleImageView = (CircleImageView) itemView.findViewById(R.id.item_contact_profileCircleImageView);
            favoriteIconImageView = (ImageView) itemView.findViewById(R.id.item_contact_favoriteIconImageView);
            contactInitialTextView = (TextView) itemView.findViewById(R.id.item_contact_contactInitialTextView);
            contactNameTextView = (TextView) itemView.findViewById(R.id.item_contact_contactNameTextView);
        }
    }

    public String getName (Contact contact){
        return contact.getFirstName() + " " + contact.getLastName();
    }

    public int getRandomBackgroundColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
    }
}
