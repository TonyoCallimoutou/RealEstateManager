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


        if (values.containsKey(DatabaseRealEstateHandler.ID_COL)) {
            realEstate.setId(values.getAsString(DatabaseRealEstateHandler.ID_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.CREATION_COL)) {
            realEstate.setCreationDate(DateConverter.fromString(values.getAsLong(DatabaseRealEstateHandler.CREATION_COL)));
        }

        if (values.containsKey(DatabaseRealEstateHandler.PRICE_USD_COL)) {
            realEstate.setPriceUSD(values.getAsInteger(DatabaseRealEstateHandler.PRICE_USD_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.DESCRIPTION_COL)) {
            realEstate.setDescription(values.getAsString(DatabaseRealEstateHandler.DESCRIPTION_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.TYPE_ID_COL)) {
            realEstate.setTypeId(values.getAsInteger(DatabaseRealEstateHandler.TYPE_ID_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.NUMBER_PHOTOS_COL)) {
            realEstate.setNumberOfPhotos(values.getAsInteger(DatabaseRealEstateHandler.NUMBER_PHOTOS_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.MAIN_PICTURE_POSITION_COL)) {
            realEstate.setMainPicturePosition(values.getAsInteger(DatabaseRealEstateHandler.MAIN_PICTURE_POSITION_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.SURFACE_COL)) {
            realEstate.setSurface(values.getAsInteger(DatabaseRealEstateHandler.SURFACE_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.NUMBER_OF_ROOM_COL)) {
            realEstate.setNumberOfRooms(values.getAsInteger(DatabaseRealEstateHandler.NUMBER_OF_ROOM_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.NUMBER_OF_BATHROOM_COL)) {
            realEstate.setNumberOfBathrooms(values.getAsInteger(DatabaseRealEstateHandler.NUMBER_OF_BATHROOM_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.NUMBER_OF_BEDROOM_COL)) {
            realEstate.setNumberOfBedrooms(values.getAsInteger(DatabaseRealEstateHandler.NUMBER_OF_BEDROOM_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.IS_SOLD_COL)) {
            realEstate.setSold(values.getAsBoolean(DatabaseRealEstateHandler.IS_SOLD_COL));
            realEstate.setSoldDate(new Date());
            if (values.containsKey(DatabaseRealEstateHandler.SOLD_DATE_COL)) {
                realEstate.setSoldDate(DateConverter.fromString(values.getAsLong(DatabaseRealEstateHandler.SOLD_DATE_COL)));
            }
        }

        if (values.containsKey(DatabaseRealEstateHandler.IS_SYNC_COL)) {
            realEstate.setSync(values.getAsBoolean(DatabaseRealEstateHandler.IS_SYNC_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.IS_DRAFT_COL)) {
            realEstate.setDraft(values.getAsBoolean(DatabaseRealEstateHandler.IS_DRAFT_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.PHOTOS_COL)) {
            photos = (PhotoListConverter.fromString(values.getAsString(DatabaseRealEstateHandler.PHOTOS_COL)));
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


        if (values.containsKey(DatabaseRealEstateHandler.USER_UID_COL)) {
            user.setUid(values.getAsString(DatabaseRealEstateHandler.USER_UID_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.USER_USERNAME_COL)) {
            user.setUsername(values.getAsString(DatabaseRealEstateHandler.USER_USERNAME_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.USER_PHONE_NUMBER_COL)) {
            user.setPhoneNumber(values.getAsString(DatabaseRealEstateHandler.USER_PHONE_NUMBER_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.USER_EMAIL_COL)) {
            user.setEmail(values.getAsString(DatabaseRealEstateHandler.USER_EMAIL_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.USER_URL_PICTURE_COL)) {
            user.setUrlPicture(values.getAsString(DatabaseRealEstateHandler.USER_URL_PICTURE_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.USER_REAL_ESTATE_COL)) {
            user.setMyRealEstateId(StringListConverter.fromString(values.getAsString(DatabaseRealEstateHandler.USER_REAL_ESTATE_COL)));
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


        if (values.containsKey(DatabaseRealEstateHandler.PLACE_PLACE_ID_COL)) {
            location.setPlaceId(values.getAsString(DatabaseRealEstateHandler.PLACE_PLACE_ID_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.PLACE_NAME_COL)) {
            location.setName(values.getAsString(DatabaseRealEstateHandler.PLACE_NAME_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.PLACE_LAT_COL)) {
            location.setLat(values.getAsDouble(DatabaseRealEstateHandler.PLACE_LAT_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.PLACE_LNG_COL)) {
            location.setLng(values.getAsDouble(DatabaseRealEstateHandler.PLACE_LNG_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.PLACE_ADDRESS_COL)) {
            location.setAddress(values.getAsString(DatabaseRealEstateHandler.PLACE_ADDRESS_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.PLACE_COUNTRY_COL)) {
            location.setCountry(values.getAsString(DatabaseRealEstateHandler.PLACE_COUNTRY_COL));
        }

        if (values.containsKey(DatabaseRealEstateHandler.PLACE_CITY_COL)) {
            location.setCity(values.getAsString(DatabaseRealEstateHandler.PLACE_CITY_COL));
        }
        if (values.containsKey(DatabaseRealEstateHandler.PLACE_IS_NEXT_TO_SCHOOL_COL)) {
            location.setNextToSchool(values.getAsBoolean(DatabaseRealEstateHandler.PLACE_IS_NEXT_TO_SCHOOL_COL));
        }
        if (values.containsKey(DatabaseRealEstateHandler.PLACE_IS_NEXT_TO_PARK_COL)) {
            location.setNextToSchool(values.getAsBoolean(DatabaseRealEstateHandler.PLACE_IS_NEXT_TO_PARK_COL));
        }
        if (values.containsKey(DatabaseRealEstateHandler.PLACE_IS_NEXT_TO_STORE_COL)) {
            location.setNextToSchool(values.getAsBoolean(DatabaseRealEstateHandler.PLACE_IS_NEXT_TO_STORE_COL));
        }

        realEstate.setPlace(location);

        return realEstate;

    }
}
