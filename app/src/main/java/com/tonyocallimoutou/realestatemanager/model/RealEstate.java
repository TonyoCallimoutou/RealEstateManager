package com.tonyocallimoutou.realestatemanager.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.libraries.places.api.model.Place;
import com.tonyocallimoutou.realestatemanager.data.room.PhotoConverter;
import com.tonyocallimoutou.realestatemanager.data.room.StringListConverter;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
public class RealEstate implements Serializable {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "realEstate_id")
    private String id;
    private String creationDate;
    @Embedded(prefix = "user_")
    private User user;
    private int priceUSD;
    private String type;
    @TypeConverters({PhotoConverter.class})
    private List<Photo> photos;
    private int mainPicturePosition;
    private String description;
    private int surface;
    private int numberOfRooms;
    private int numberOfBathrooms;
    private int numberOfBedrooms;
    @Embedded(prefix = "place_")
    @Nullable
    private RealEstateLocation place;
    private boolean isSold;
    @Nullable
    private String soldDate;
    @ColumnInfo(name = "real_estate_is_synchro")
    private boolean isSync;
    @ColumnInfo(name = "real_estate_is_draft")
    private boolean isDraft;

    @Ignore
    public RealEstate(){}

    public RealEstate(int priceUSD,
                     User user,
                     String type,
                     List<Photo> photos,
                     int mainPicturePosition,
                     String description,
                     int surface,
                     int numberOfRooms,
                     int numberOfBathrooms,
                     int numberOfBedrooms,
                     @Nullable RealEstateLocation place) {
        this.id = user.getUid()+"_"+ user.getMyRealEstateId().size();
        this.creationDate = Utils.getTodayDate();
        this.priceUSD = priceUSD;
        this.user = user;
        this.type = type;
        this.photos = photos;
        this.mainPicturePosition = mainPicturePosition;
        this.description = description;
        this.surface = surface;
        this.numberOfRooms = numberOfRooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.numberOfBedrooms = numberOfBedrooms;
        this.place = place;
        this.isSold = false;
        this.isSync = false;
        this.isDraft = false;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPriceUSD() {
        return priceUSD;
    }

    public String getStringPriceUSD(Context context) {
        return Utils.getStringOfPriceWithActualMoney(context,priceUSD);
    }

    public void setPriceUSD(int priceUSD) {
        this.priceUSD = priceUSD;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public int getMainPicturePosition() {
        return mainPicturePosition;
    }

    public void setMainPicturePosition(int mainPicturePosition) {
        this.mainPicturePosition = mainPicturePosition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSurface() {
        return surface;
    }

    public String getStringSurface() {
        return surface + "m2";
    }

    public void setSurface(int surface) {
        this.surface = surface;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public String getStringNumberOfRooms() {
        return ""+ numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getNumberOfBathrooms() {
        return numberOfBathrooms;
    }

    public String getStringNumberOfBathrooms() {
        return ""+ numberOfBathrooms;
    }

    public void setNumberOfBathrooms(int numberOfBathrooms) {
        this.numberOfBathrooms = numberOfBathrooms;
    }

    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public String getStringNumberOfBedrooms() {
        return ""+numberOfBedrooms;
    }

    public void setNumberOfBedrooms(int numberOfBedrooms) {
        this.numberOfBedrooms = numberOfBedrooms;
    }
    @Nullable
    public RealEstateLocation getPlace() {
        return place;
    }

    public void setPlace(@Nullable RealEstateLocation place) {
        this.place = place;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    @Nullable
    public String getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(@Nullable String soldDate) {
        this.soldDate = soldDate;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public double getProgressSync() {
        int count =0;
        assert photos != null;
        for (Photo photo : photos) {
            if (photo.isSync()) {
                count ++;
            }
        }
        return (count/(double)photos.size())*100;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }
}
