package com.tonyocallimoutou.realestatemanager.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String uid;
    private String username;
    private String urlPicture;
    private String email;
    private List<RealEstate> myRealEstate = new ArrayList<>();


    public User() {}

    public User(String uid, String username, String urlPicture, String email) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.email = email;
        this.myRealEstate = new ArrayList<>();
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

    public String getPicture() {
        return urlPicture;
    }

    public void setPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<RealEstate> getMyRealEstate() {
        return myRealEstate;
    }

    public void setMyRealEstate(List<RealEstate> myRealEstate) {
        this.myRealEstate = myRealEstate;
    }

    public void addRealEstateToMyList(RealEstate realEstate) {
        myRealEstate.add(realEstate);
    }

}
