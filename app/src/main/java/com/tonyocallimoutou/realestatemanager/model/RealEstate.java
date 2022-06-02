package com.tonyocallimoutou.realestatemanager.model;

import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.model.Place;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class RealEstate implements Serializable {


    private String id;
    private Date creationDate;
    private String userId;
    private int priceUSD;
    private String type;
    private List<Photo> photos;
    private int mainPicturePosition;
    private String description;
    private int surface;
    private int numberOfRooms;
    private int numberOfBathrooms;
    private int numberOfBedrooms;
    private RealEstateLocation place;
    private boolean isSold;
    private Date soldDate;

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
                      RealEstateLocation place) {
        this.id = user.getUid()+"_"+ user.getMyRealEstateId().size();
        this.creationDate = new Date();
        this.priceUSD = priceUSD;
        this.userId = user.getUid();
        this.type = type;
        this.photos = photos;
        this.mainPicturePosition = mainPicturePosition;
        this.description = description;
        this.surface = surface;
        this.numberOfRooms = numberOfRooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.numberOfBedrooms = numberOfBedrooms;
        this.place = place;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getPriceUSD() {
        return priceUSD;
    }

    public String getStringPriceUSD() {
        return Utils.getStringOfPriceWithActualMoney(priceUSD);
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

    public RealEstateLocation getPlace() {
        return place;
    }

    public void setPlace(RealEstateLocation place) {
        this.place = place;
    }

    public boolean isSold() {
        return isSold;
    }

    public void setSold(boolean sold) {
        isSold = sold;
    }

    public Date getSoldDate() {
        return soldDate;
    }

    public void setSoldDate(Date soldDate) {
        this.soldDate = soldDate;
    }
}
