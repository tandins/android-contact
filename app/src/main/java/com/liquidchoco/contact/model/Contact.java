package com.liquidchoco.contact.model;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class Contact extends RealmObject{
    @PrimaryKey
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String profilePic;
    private boolean favorite;
//    private Date createdAt;
//    private Date updatedAt;

    public Contact() {
    }

    public Contact(JSONObject object) {
        try {
            if(object.has("id")) {
                setId(object.getInt("id"));
            }

            if(object.has("first_name")) {
                setFirstName(object.getString("first_name"));
            }
            if(object.has("last_name")) {
                setLastName(object.getString("last_name"));
            }
            if(object.has("email")) {
                setEmail(object.getString("email"));
            }
            if(object.has("phone_number")) {
                setPhoneNumber(object.getString("phone_number"));
            }
            if(object.has("profile_pic")) {
                setProfilePic(object.getString("profile_pic"));
            }
            if(object.has("favorite")) {
                setFavorite(object.getBoolean("favorite"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

//    public Date getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(Date createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public Date getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(Date updatedAt) {
//        this.updatedAt = updatedAt;
//    }
}
