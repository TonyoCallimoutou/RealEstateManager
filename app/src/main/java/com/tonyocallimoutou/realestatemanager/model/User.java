package com.tonyocallimoutou.realestatemanager.model;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.tonyocallimoutou.realestatemanager.data.room.StringListConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class User implements Serializable {

    @PrimaryKey
    @NonNull
    private String uid;
    @ColumnInfo(name = "user_name")
    private String username;
    @ColumnInfo(name = "user_picture")
    private String urlPicture;
    private String email;
    @Nullable
    @ColumnInfo(name = "user_phone_number")
    private String phoneNumber;
    @TypeConverters({StringListConverter.class})
    private List<String> myRealEstateId = new ArrayList<>();

    @Ignore
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
        return uid.equals(user.uid) && Objects.equals(username, user.username) && Objects.equals(urlPicture, user.urlPicture) && Objects.equals(email, user.email) && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(myRealEstateId, user.myRealEstateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, username, urlPicture, email, phoneNumber, myRealEstateId);
    }
}
