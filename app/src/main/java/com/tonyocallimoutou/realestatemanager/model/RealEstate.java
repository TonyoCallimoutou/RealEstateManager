package com.tonyocallimoutou.realestatemanager.model;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.DatabaseRealEstateHandler;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.DateConverter;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.PhotoListConverter;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.StringListConverter;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class RealEstate implements Serializable {

    private String id;
    private Date creationDate;
    private User user;
    private int priceUSD;
    private int typeId;
    private List<Photo> photos;
    private int numberOfPhotos;
    private int mainPicturePosition;
    private String description;
    private int surface;
    private int numberOfRooms;
    private int numberOfBathrooms;
    private int numberOfBedrooms;
    @Nullable
    private RealEstateLocation place;
    private boolean isSold;
    @Nullable
    private Date soldDate;
    private boolean isSync;
    private boolean isDraft;


    public RealEstate(){}

    public RealEstate(int priceUSD,
                     User user,
                     int typeId,
                     List<Photo> photos,
                     int mainPicturePosition,
                     String description,
                     int surface,
                     int numberOfRooms,
                     int numberOfBathrooms,
                     int numberOfBedrooms,
                     @Nullable RealEstateLocation place) {
        this.id = user.getEmail()+"_"+ user.getMyRealEstateId().size();
        this.creationDate = new Date();
        this.priceUSD = priceUSD;
        this.user = user;
        this.typeId = typeId;
        this.photos = photos;
        this.numberOfPhotos = photos.size();
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getStringCreationDate() {
        return Utils.getStringOfDate(creationDate);
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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getStringType(Context context) {
        String[] str = context.getResources().getStringArray(R.array.SpinnerTypeOfResidence);
        return str[typeId];
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
        this.numberOfPhotos = photos.size();
    }

    public int getNumberOfPhotos() {
        return numberOfPhotos;
    }

    public void setNumberOfPhotos(int numberOfPhotos) {
        this.numberOfPhotos = numberOfPhotos;
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
    public Date getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(@Nullable Date soldDate) {
        this.soldDate = soldDate;
    }

    public String getStringSoldDate() {
        return Utils.getStringOfDate(soldDate);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RealEstate that = (RealEstate) o;
        return priceUSD == that.priceUSD && typeId == that.typeId && numberOfPhotos == that.numberOfPhotos && mainPicturePosition == that.mainPicturePosition && surface == that.surface && numberOfRooms == that.numberOfRooms && numberOfBathrooms == that.numberOfBathrooms && numberOfBedrooms == that.numberOfBedrooms && isSold == that.isSold && isSync == that.isSync && isDraft == that.isDraft && id.equals(that.id) && Objects.equals(creationDate, that.creationDate) && Objects.equals(user, that.user) && Objects.equals(photos, that.photos) && Objects.equals(description, that.description) && Objects.equals(place, that.place) && Objects.equals(soldDate, that.soldDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, user, priceUSD, typeId, photos, numberOfPhotos, mainPicturePosition, description, surface, numberOfRooms, numberOfBathrooms, numberOfBedrooms, place, isSold, soldDate, isSync, isDraft);
    }
}
