package com.tonyocallimoutou.realestatemanager.data.localDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.libraries.places.api.model.Place;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.RealEstateLocation;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.repository.RealEstateRepository;
import com.tonyocallimoutou.realestatemanager.util.Filter;
import com.tonyocallimoutou.realestatemanager.util.MathUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseRealEstateHandler extends SQLiteOpenHelper {


    private static final String DB_NAME = "realEstateDatabase";
    private static final int DB_VERSION = 1;
    public static final String TABLE_REAL_ESTATE_NAME = "realEstate";
    public static final String ID_COL = "id";
    public static final String CREATION_COL = "creationDate";
    public static final String USER_UID_COL = "user_uid";
    public static final String USER_USERNAME_COL = "user_username";
    public static final String USER_URL_PICTURE_COL = "user_urlPicture";
    public static final String USER_EMAIL_COL = "user_email";
    public static final String USER_PHONE_NUMBER_COL = "user_phoneNumber";
    public static final String USER_REAL_ESTATE_COL = "user_myRealEstateId";
    public static final String PRICE_USD_COL = "priceUSD";
    public static final String TYPE_ID_COL = "typeId";
    public static final String PHOTOS_COL = "photos_reference";
    public static final String NUMBER_PHOTOS_COL = "numberOfPhotos";
    public static final String MAIN_PICTURE_POSITION_COL = "mainPicturePosition";
    public static final String DESCRIPTION_COL = "description";
    public static final String SURFACE_COL = "surface";
    public static final String NUMBER_OF_ROOM_COL = "numberOfRooms";
    public static final String NUMBER_OF_BATHROOM_COL = "numberOfBathrooms";
    public static final String NUMBER_OF_BEDROOM_COL = "numberOfBedrooms";
    public static final String PLACE_PLACE_ID_COL = "place_placeId";
    public static final String PLACE_NAME_COL = "place_name";
    public static final String PLACE_LAT_COL = "place_lat";
    public static final String PLACE_LNG_COL = "place_lng";
    public static final String PLACE_COS_LAT_COL = "place_cos_lat";
    public static final String PLACE_SIN_LAT_COL = "place_sin_lat";
    public static final String PLACE_COS_LNG_COL = "place_cos_lng";
    public static final String PLACE_SIN_LNG_COL = "place_sin_lng";
    public static final String PLACE_ADDRESS_COL = "place_address";
    public static final String PLACE_COUNTRY_COL = "place_country";
    public static final String PLACE_CITY_COL = "place_city";
    public static final String PLACE_IS_NEXT_TO_SCHOOL_COL = "next_to_school";
    public static final String PLACE_IS_NEXT_TO_PARK_COL = "next_to_park";
    public static final String PLACE_IS_NEXT_TO_STORE_COL = "next_to_store";
    public static final String IS_SOLD_COL = "isSold";
    public static final String SOLD_DATE_COL = "soldDate";
    public static final String IS_SYNC_COL = "isSync";
    public static final String IS_DRAFT_COL = "isDraft";

    public static final String[] ALL_COLUMNS = {
            ID_COL, CREATION_COL, USER_UID_COL, USER_USERNAME_COL, USER_URL_PICTURE_COL, USER_EMAIL_COL,
            USER_PHONE_NUMBER_COL, USER_REAL_ESTATE_COL, PRICE_USD_COL, TYPE_ID_COL, PHOTOS_COL, NUMBER_PHOTOS_COL,
            MAIN_PICTURE_POSITION_COL, DESCRIPTION_COL, SURFACE_COL, NUMBER_OF_ROOM_COL, NUMBER_OF_BATHROOM_COL,
            NUMBER_OF_BEDROOM_COL, PLACE_PLACE_ID_COL, PLACE_NAME_COL, PLACE_LAT_COL, PLACE_LNG_COL,
            PLACE_COS_LAT_COL, PLACE_SIN_LAT_COL, PLACE_COS_LNG_COL, PLACE_SIN_LNG_COL, PLACE_ADDRESS_COL,
            PLACE_COUNTRY_COL, PLACE_CITY_COL, PLACE_IS_NEXT_TO_SCHOOL_COL, PLACE_IS_NEXT_TO_PARK_COL,
            PLACE_IS_NEXT_TO_STORE_COL, IS_SOLD_COL, SOLD_DATE_COL, IS_SYNC_COL, IS_DRAFT_COL };

    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_CREATION = 1;
    private static final int NUM_COL_USER_UID = 2;
    private static final int NUM_COL_USER_USERNAME = 3;
    private static final int NUM_COL_USER_URL_PICTURE = 4;
    private static final int NUM_COL_USER_EMAIL = 5;
    private static final int NUM_COL_USER_PHONE_NUMBER = 6;
    private static final int NUM_COL_USER_REAL_ESTATE = 7;
    private static final int NUM_COL_PRICE_USD = 8;
    private static final int NUM_COL_TYPE_ID = 9;
    private static final int NUM_COL_PHOTOS = 10;
    private static final int NUM_COL_NUMBER_PHOTOS = 11;
    private static final int NUM_COL_MAIN_PICTURE_POSITION = 12;
    private static final int NUM_COL_DESCRIPTION = 13;
    private static final int NUM_COL_SURFACE = 14;
    private static final int NUM_COL_NUMBER_OF_ROOM = 15;
    private static final int NUM_COL_NUMBER_OF_BATHROOM = 16;
    private static final int NUM_COL_NUMBER_OF_BEDROOM = 17;
    private static final int NUM_COL_PLACE_PLACE_ID = 18;
    private static final int NUM_COL_PLACE_NAME = 19;
    private static final int NUM_COL_PLACE_LAT = 20;
    private static final int NUM_COL_PLACE_LNG = 21;
    private static final int NUM_COL_PLACE_COS_LAT = 22;
    private static final int NUM_COL_PLACE_SIN_LAT = 23;
    private static final int NUM_COL_PLACE_COS_LNG = 24;
    private static final int NUM_COL_PLACE_SIN_LNG = 25;
    private static final int NUM_COL_PLACE_ADDRESS = 26;
    private static final int NUM_COL_PLACE_COUNTRY = 27;
    private static final int NUM_COL_PLACE_CITY = 28;
    private static final int NUM_COL_PLACE_IS_NEXT_TO_SCHOOL =29;
    private static final int NUM_COL_PLACE_IS_NEXT_TO_PARK = 30;
    private static final int NUM_COL_PLACE_IS_NEXT_TO_STORE = 31;
    private static final int NUM_COL_IS_SOLD = 32;
    private static final int NUM_COL_SOLD_DATE = 33;
    private static final int NUM_COL_IS_SYNC = 34;
    private static final int NUM_COL_IS_DRAFT = 35;

    private static DatabaseRealEstateHandler instance;


    private final MutableLiveData<List<RealEstate>> realEstatesLiveData = new MutableLiveData<>();

    private List<Filter> filters = new ArrayList<>();

    public DatabaseRealEstateHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static DatabaseRealEstateHandler getInstance(Context context) {
        synchronized (RealEstateRepository.class) {
            if (instance == null) {
                instance = new DatabaseRealEstateHandler(context);
            }
            return instance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_REAL_ESTATE_NAME + " ("
                + ID_COL + " TEXT PRIMARY KEY,"
                + CREATION_COL + " TEXT NOT NULL,"
                + USER_UID_COL + " TEXT NOT NULL,"
                + USER_USERNAME_COL + " TEXT NOT NULL,"
                + USER_URL_PICTURE_COL + " TEXT NOT NULL,"
                + USER_EMAIL_COL + " TEXT NOT NULL,"
                + USER_PHONE_NUMBER_COL + " TEXT,"
                + USER_REAL_ESTATE_COL + " TEXT NOT NULL,"
                + PRICE_USD_COL + " INTEGER NOT NULL,"
                + TYPE_ID_COL + " INTEGER NOT NULL,"
                + PHOTOS_COL + " TEXT NOT NULL,"
                + NUMBER_PHOTOS_COL + " INTEGER NOT NULL,"
                + MAIN_PICTURE_POSITION_COL + " INTEGER NOT NULL,"
                + DESCRIPTION_COL + " TEXT NOT NULL,"
                + SURFACE_COL + " INTEGER NOT NULL,"
                + NUMBER_OF_ROOM_COL + " INTEGER NOT NULL,"
                + NUMBER_OF_BATHROOM_COL + " INTEGER NOT NULL,"
                + NUMBER_OF_BEDROOM_COL + " INTEGER NOT NULL,"
                + PLACE_PLACE_ID_COL + " TEXT,"
                + PLACE_NAME_COL + " TEXT,"
                + PLACE_LAT_COL + " INTEGER,"
                + PLACE_LNG_COL + " INTEGER,"
                + PLACE_COS_LAT_COL + " INTEGER,"
                + PLACE_SIN_LAT_COL + " INTEGER,"
                + PLACE_COS_LNG_COL + " INTEGER,"
                + PLACE_SIN_LNG_COL + " INTEGER,"
                + PLACE_ADDRESS_COL + " TEXT,"
                + PLACE_COUNTRY_COL + " TEXT,"
                + PLACE_CITY_COL + " TEXT,"
                + PLACE_IS_NEXT_TO_SCHOOL_COL + " INTEGER,"
                + PLACE_IS_NEXT_TO_PARK_COL + " INTEGER,"
                + PLACE_IS_NEXT_TO_STORE_COL + " INTEGER,"
                + IS_SOLD_COL + " INTEGER NOT NULL,"
                + SOLD_DATE_COL + " TEXT,"
                + IS_SYNC_COL + " INTEGER NOT NULL,"
                + IS_DRAFT_COL + " INTEGER NOT NULL)";

        // at last we are calling a exec sql
        // method to execute above sql query
        sqLiteDatabase.execSQL(query);
    }

    public void createRealEstate(RealEstate realEstate) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        String listPhoto = PhotoListConverter.fromList(realEstate.getPhotos());
        String listMyRealEstate = StringListConverter.fromList(realEstate.getUser().getMyRealEstateId());

        values.put(ID_COL, realEstate.getId());
        values.put(CREATION_COL, realEstate.getCreationDate().getTime());
        values.put(USER_UID_COL, realEstate.getUser().getUid());
        values.put(USER_USERNAME_COL, realEstate.getUser().getUsername());
        values.put(USER_URL_PICTURE_COL, realEstate.getUser().getUrlPicture());
        values.put(USER_EMAIL_COL, realEstate.getUser().getEmail());
        values.put(USER_PHONE_NUMBER_COL, realEstate.getUser().getPhoneNumber());
        values.put(USER_REAL_ESTATE_COL, listMyRealEstate);
        values.put(PRICE_USD_COL, realEstate.getPriceUSD());
        values.put(TYPE_ID_COL, realEstate.getTypeId());
        values.put(PHOTOS_COL, listPhoto);
        values.put(NUMBER_PHOTOS_COL, realEstate.getNumberOfPhotos());
        values.put(MAIN_PICTURE_POSITION_COL, realEstate.getMainPicturePosition());
        values.put(DESCRIPTION_COL, realEstate.getDescription());
        values.put(SURFACE_COL, realEstate.getSurface());
        values.put(NUMBER_OF_ROOM_COL, realEstate.getNumberOfRooms());
        values.put(NUMBER_OF_BATHROOM_COL, realEstate.getNumberOfBathrooms());
        values.put(NUMBER_OF_BEDROOM_COL, realEstate.getNumberOfBedrooms());
        if (realEstate.getPlace() != null) {
            values.put(PLACE_PLACE_ID_COL, realEstate.getPlace().getPlaceId());
            values.put(PLACE_NAME_COL, realEstate.getPlace().getName());
            values.put(PLACE_ADDRESS_COL, realEstate.getPlace().getAddress());
            values.put(PLACE_COUNTRY_COL, realEstate.getPlace().getCountry());
            values.put(PLACE_CITY_COL, realEstate.getPlace().getCity());
            values.put(PLACE_IS_NEXT_TO_SCHOOL_COL, realEstate.getPlace().isNextToSchool());
            values.put(PLACE_IS_NEXT_TO_PARK_COL, realEstate.getPlace().isNextToPark());
            values.put(PLACE_IS_NEXT_TO_STORE_COL, realEstate.getPlace().isNextToStore());


            double latitude = realEstate.getPlace().getLat();
            double longitude = realEstate.getPlace().getLng();
            values.put(PLACE_LAT_COL, latitude);
            values.put(PLACE_LNG_COL, longitude);
            values.put(PLACE_COS_LAT_COL, Math.cos(MathUtil.deg2rad(latitude)));
            values.put(PLACE_SIN_LAT_COL, Math.sin(MathUtil.deg2rad(latitude)));
            values.put(PLACE_COS_LNG_COL, Math.cos(MathUtil.deg2rad(longitude)));
            values.put(PLACE_SIN_LNG_COL, Math.sin(MathUtil.deg2rad(longitude)));
        }
        values.put(IS_SOLD_COL, realEstate.isSold());
        if (realEstate.getSoldDate() != null) {
            values.put(SOLD_DATE_COL, realEstate.getSoldDate().getTime());
        }
        values.put(IS_SYNC_COL, realEstate.isSync());
        values.put(IS_DRAFT_COL, realEstate.isDraft());

        db.insertWithOnConflict(TABLE_REAL_ESTATE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        realEstatesLiveData.postValue(getRealEstates());
    }

    public LiveData<List<RealEstate>> getRealEstateLiveData(List<Filter> filters) {
        this.filters = filters;
        realEstatesLiveData.postValue(getRealEstates());
        return realEstatesLiveData;
    }

    public List<RealEstate> getRealEstates() {

        List<RealEstate> realEstates = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder strQuery = new StringBuilder(" ");
        String distanceQueryStart = "";

        for (int i = 0; i < filters.size(); i++) {
            if (i==0) {
                strQuery.append("WHERE ");
            }
            else {
                strQuery.append(" AND ");
            }
            strQuery.append(filters.get(i).getQueryStr());

        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REAL_ESTATE_NAME + strQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RealEstate realEstate = cursorToRealEstate(cursor);
            realEstates.add(realEstate);
            cursor.moveToNext();
        }


        cursor.close();

        return realEstates;
    }

    private RealEstate cursorToRealEstate(Cursor c) {
        if (c.getCount() == 0)
            return null;

        RealEstate realEstate = new RealEstate();

        realEstate.setId(c.getString(NUM_COL_ID));
        realEstate.setCreationDate(new Date(c.getLong(NUM_COL_CREATION)));

        User user = new User();
        user.setUid(c.getString(NUM_COL_USER_UID));
        user.setUsername(c.getString(NUM_COL_USER_USERNAME));
        user.setUrlPicture(c.getString(NUM_COL_USER_URL_PICTURE));
        user.setEmail(c.getString(NUM_COL_USER_EMAIL));
        user.setPhoneNumber(c.getString(NUM_COL_USER_PHONE_NUMBER));
        List<String> myRealEstateId = StringListConverter.fromString(c.getString(NUM_COL_USER_REAL_ESTATE));
        user.setMyRealEstateId(myRealEstateId);

        realEstate.setUser(user);

        realEstate.setPriceUSD(c.getInt(NUM_COL_PRICE_USD));
        realEstate.setTypeId(c.getInt(NUM_COL_TYPE_ID));
        List<Photo> photos = PhotoListConverter.fromString(c.getString(NUM_COL_PHOTOS));
        realEstate.setPhotos(photos);
        realEstate.setNumberOfPhotos(c.getInt(NUM_COL_NUMBER_PHOTOS));
        realEstate.setMainPicturePosition(c.getInt(NUM_COL_MAIN_PICTURE_POSITION));
        realEstate.setDescription(c.getString(NUM_COL_DESCRIPTION));
        realEstate.setSurface(c.getInt(NUM_COL_SURFACE));
        realEstate.setNumberOfRooms(c.getInt(NUM_COL_NUMBER_OF_ROOM));
        realEstate.setNumberOfBathrooms(c.getInt(NUM_COL_NUMBER_OF_BATHROOM));
        realEstate.setNumberOfBedrooms(c.getInt(NUM_COL_NUMBER_OF_BEDROOM));


        RealEstateLocation location = new RealEstateLocation();
        if (c.getString(NUM_COL_PLACE_PLACE_ID) != null) {
            location.setPlaceId(c.getString(NUM_COL_PLACE_PLACE_ID));
            location.setName(c.getString(NUM_COL_PLACE_NAME));
            location.setLat(c.getDouble(NUM_COL_PLACE_LAT));
            location.setLng(c.getDouble(NUM_COL_PLACE_LNG));
            location.setAddress(c.getString(NUM_COL_PLACE_ADDRESS));
            location.setCountry(c.getString(NUM_COL_PLACE_COUNTRY));
            location.setCity(c.getString(NUM_COL_PLACE_CITY));
            if (c.getInt(NUM_COL_PLACE_IS_NEXT_TO_SCHOOL) == 1) {
                location.setNextToSchool(true);
            }
            if (c.getInt(NUM_COL_PLACE_IS_NEXT_TO_PARK) == 1) {
                location.setNextToPark(true);
            }
            if (c.getInt(NUM_COL_PLACE_IS_NEXT_TO_STORE) == 1) {
                location.setNextToStore(true);
            }
        }
        else {
            location.setPlaceId("null");
            location.setName("null");
            location.setAddress("null");
            location.setLng(0);
            location.setLat(0);
            location.setCountry("null");
            location.setCity("null");
        }

        realEstate.setPlace(location);

        if (c.getInt(NUM_COL_IS_SOLD) == 1){
            realEstate.setSold(true);
            realEstate.setSoldDate(new Date(c.getLong(NUM_COL_SOLD_DATE)));
        }
        if (c.getInt(NUM_COL_IS_SYNC) == 1) {
            realEstate.setSync(true);
        }
        if (c.getInt(NUM_COL_IS_DRAFT) == 1) {
            realEstate.setDraft(true);
        }

        return realEstate;
    }

    public static String conditionDistanceQuery(float distance, Place place) {

        String str = "(" + PLACE_CITY_COL + " = " + "\"" + place.getName() + "\"";

        if (distance != 0) {

            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;
            double cos_lat_rad = Math.cos(MathUtil.deg2rad(latitude));
            double sin_lat_rad = Math.sin(MathUtil.deg2rad(latitude));
            double cos_lon_rad = Math.cos(MathUtil.deg2rad(longitude));
            double sin_lon_rad = Math.sin(MathUtil.deg2rad(longitude));

            String dis = String.valueOf(Math.cos(distance / (float) 6380));

            return str + " OR (" + sin_lat_rad + " * \"place_sin_lat\" + " + cos_lat_rad + " * \"place_cos_lat\" * (+" + sin_lon_rad + " * \"place_sin_lng\" + " + cos_lon_rad + " * \"place_cos_lng\")) > " + dis + ")";
        }
        return str + ")";
    }

    public void deleteDraft(RealEstate realEstate) {
        if (realEstate.isDraft()) {
            Log.d("TAG", "deleteDraft: ");
            SQLiteDatabase db = this.getWritableDatabase();

            String query = ID_COL + " = " + "\"" + realEstate.getId() + "\"";

            db.delete(TABLE_REAL_ESTATE_NAME, query,null);
            db.close();
        }
    }

    public List<RealEstate> getNotSyncRealEstates () {
        List<RealEstate> realEstates = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REAL_ESTATE_NAME
                + " WHERE "+ IS_SYNC_COL +" = 0 AND "
                + IS_DRAFT_COL + " = 0"
                , null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RealEstate realEstate = cursorToRealEstate(cursor);
            realEstates.add(realEstate);
            cursor.moveToNext();
        }

        cursor.close();

        return realEstates;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REAL_ESTATE_NAME);
        onCreate(sqLiteDatabase);
    }
}
