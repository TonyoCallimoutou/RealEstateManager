package com.tonyocallimoutou.realestatemanager.model;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class User implements Serializable {

    private String uid;
    private String username;
    private String urlPicture;
    private String email;
    @Nullable
    private String phoneNumber;
    private List<String> myRealEstateId = new ArrayList<>();


    public User() {}

    public User(String uid, String username, String urlPicture, String email) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.email = email;
        this.myRealEstateId = new ArrayList<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getMyRealEstateId() {
        return myRealEstateId;
    }

    public void setMyRealEstateId(List<String> myRealEstateId) {
        this.myRealEstateId = myRealEstateId;
    }

    public void addRealEstateToMyList(RealEstate realEstate) {
        myRealEstateId.add(realEstate.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return uid.equals(user.uid) && Objects.equals(username, user.username) && Objects.equals(urlPicture, user.urlPicture) && Objects.equals(email, user.email) && Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, urlPicture, email, phoneNumber);
    }
}
