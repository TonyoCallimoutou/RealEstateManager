package com.tonyocallimoutou.realestatemanager.model;

import androidx.annotation.Nullable;

import java.util.List;

public class RealEstate {


    private String id;
    private User user;
    private int priceUSD;
    private String type;
    @Nullable
    private List<Photo> photos;
    private String description;
    private int surface;
    private int numberOfRooms;
    private int numberOfBathrooms;
    private int numberOfBedrooms;

    public RealEstate(){}

    public RealEstate(int priceUSD,
                     User user,
                     String type,
                     @Nullable List<Photo> photos,
                     String description,
                     int surface,
                     int numberOfRooms,
                     int numberOfBathrooms,
                     int numberOfBedrooms) {
        this.id = user.getUid()+" : "+ user.getMyRealEstate().size();
        this.priceUSD = priceUSD;
        this.user = user;
        this.type = type;
        this.photos = photos;
        this.description = description;
        this.surface = surface;
        this.numberOfRooms = numberOfRooms;
        this.numberOfBathrooms = numberOfBathrooms;
        this.numberOfBedrooms = numberOfBedrooms;
    }

    public int getPriceUSD() {
        return priceUSD;
    }

    public String getStringPriceUSD() {
        return priceUSD+" USD";
    }

    public void setPriceUSD(int priceUSD) {
        this.priceUSD = priceUSD;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Nullable
    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(@Nullable List<Photo> photos) {
        this.photos = photos;
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
}
