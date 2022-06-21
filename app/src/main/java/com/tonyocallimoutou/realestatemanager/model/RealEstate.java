package com.tonyocallimoutou.realestatemanager.model;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.PhotoListConverter;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.StringListConverter;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RealEstate implements Serializable {

    @NonNull
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
        this.id = user.getUid()+"_"+ user.getMyRealEstateId().size();
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

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
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

    // Provider

    public static RealEstate fromContentValues(ContentValues values) {

        final RealEstate realEstate = new RealEstate();



        // REAL ESTATE
        realEstate.setSync(true);
        realEstate.setCreationDate(new Date());

        List<Photo> photos = new ArrayList<>();
        photos.add(new Photo("android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available",null));

        realEstate.setDescription("");


        if (values.containsKey("id")) {
            realEstate.setId(values.getAsString("id"));
        }

        if (values.containsKey("priceUSD")) {
            realEstate.setPriceUSD(values.getAsInteger("priceUSD"));
        }

        if (values.containsKey("description")) {
            realEstate.setDescription(values.getAsString("description"));
        }

        if (values.containsKey("mainPicturePosition")) {
            realEstate.setMainPicturePosition(values.getAsInteger("mainPicturePosition"));
        }

        if (values.containsKey("surface")) {
            realEstate.setSurface(values.getAsInteger("surface"));
        }

        if (values.containsKey("numberOfRooms")) {
            realEstate.setNumberOfRooms(values.getAsInteger("numberOfRooms"));
        }

        if (values.containsKey("numberOfBathrooms")) {
            realEstate.setNumberOfBathrooms(values.getAsInteger("numberOfBathrooms"));
        }

        if (values.containsKey("numberOfBedrooms")) {
            realEstate.setNumberOfBedrooms(values.getAsInteger("numberOfBedrooms"));
        }

        if (values.containsKey("isSold")) {
            realEstate.setSold(values.getAsBoolean("isSold"));
            realEstate.setSoldDate(new Date());
        }

        if (values.containsKey("photos")) {
            photos = (PhotoListConverter.fromString(values.getAsString("photos")));
        }

        for (Photo photo : photos) {
            photo.setSync(true);
        }

        realEstate.setPhotos(photos);

        // USER

        User user = new User();
        user.setUrlPicture("android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available");

        user.setUid("null");
        user.setUsername("null");
        user.setPhoneNumber("null");
        user.setMyRealEstateId(new ArrayList<>());


        if (values.containsKey("user_uid")) {
            user.setUid(values.getAsString("user_uid"));
        }

        if (values.containsKey("user_user_name")) {
            user.setUsername(values.getAsString("user_user_name"));
        }

        if (values.containsKey("user_user_phone_number")) {
            user.setPhoneNumber(values.getAsString("user_user_phone_number"));
        }

        if (values.containsKey("user_email")) {
            user.setEmail(values.getAsString("user_email"));
        }

        if (values.containsKey("user_myRealEstateId")) {
            user.setMyRealEstateId(StringListConverter.fromString(values.getAsString("user_myRealEstateId")));
        }


        realEstate.setUser(user);


        // LOCATION
        RealEstateLocation location = new RealEstateLocation();
        location.setPlaceId("null");
        location.setName("null");
        location.setAddress("null");
        location.setLng(0);
        location.setLat(0);
        location.setCountry("null");
        location.setCity("null");


        if (values.containsKey("place_placeId")) {
            location.setPlaceId(values.getAsString("place_placeId"));
        }

        if (values.containsKey("place_name")) {
            location.setName(values.getAsString("place_name"));
        }

        if (values.containsKey("place_lat")) {
            location.setLat(values.getAsDouble("place_lat"));
        }

        if (values.containsKey("place_lng")) {
            location.setLng(values.getAsDouble("place_lng"));
        }

        if (values.containsKey("place_address")) {
            location.setAddress(values.getAsString("place_address"));
        }

        if (values.containsKey("place_country")) {
            location.setCountry(values.getAsString("place_country"));
        }

        if (values.containsKey("place_city")) {
            location.setCity(values.getAsString("place_city"));
        }

        realEstate.setPlace(location);

        return realEstate;

    }
}
