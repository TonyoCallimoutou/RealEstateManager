package com.tonyocallimoutou.realestatemanager.data.localDatabase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.libraries.places.api.model.Place;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.RealEstateLocation;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.provider.RealEstateContentProvider;
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
    public static final String IS_SOLD_COL = "isSold";
    public static final String SOLD_DATE_COL = "soldDate";
    public static final String IS_SYNC_COL = "isSync";
    public static final String IS_DRAFT_COL = "isDraft";
    public static final String PLACE_KEY = "place_key";

    // PLACE
    public static final String TABLE_PLACE_NAME = "place";
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



    private static DatabaseRealEstateHandler instance;

    private final Context context;


    private final MutableLiveData<List<RealEstate>> realEstatesLiveData = new MutableLiveData<>();

    private List<Filter> filters = new ArrayList<>();

    public DatabaseRealEstateHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
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
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryEstate = "CREATE TABLE " + TABLE_REAL_ESTATE_NAME + " ("
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
                + IS_SOLD_COL + " INTEGER NOT NULL,"
                + SOLD_DATE_COL + " TEXT,"
                + IS_SYNC_COL + " INTEGER NOT NULL,"
                + IS_DRAFT_COL + " INTEGER NOT NULL,"
                + PLACE_KEY + " TEXT )";

        String queryPlace = "CREATE TABLE " + TABLE_PLACE_NAME + " ("
                + PLACE_PLACE_ID_COL + " TEXT PRIMARY KEY,"
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
                + PLACE_IS_NEXT_TO_STORE_COL + " INTEGER)";

        // at last we are calling a exec sql
        // method to execute above sql query
        sqLiteDatabase.execSQL(queryEstate);
        sqLiteDatabase.execSQL(queryPlace);
    }

    public void createRealEstate(RealEstate realEstate) {

        ContentValues values = new ContentValues();

        String listPhoto = PhotoListConverter.fromList(realEstate.getPhotos());
        String listMyRealEstate = StringListConverter.fromList(realEstate.getUser().getMyRealEstateId());

        // SAVE Place

        if (realEstate.getPlace() != null) {
            ContentValues placeValues = new ContentValues();
            placeValues.put(PLACE_PLACE_ID_COL, realEstate.getPlace().getPlaceId());
            placeValues.put(PLACE_NAME_COL, realEstate.getPlace().getName());
            placeValues.put(PLACE_ADDRESS_COL, realEstate.getPlace().getAddress());
            placeValues.put(PLACE_COUNTRY_COL, realEstate.getPlace().getCountry());
            placeValues.put(PLACE_CITY_COL, realEstate.getPlace().getCity());
            placeValues.put(PLACE_IS_NEXT_TO_SCHOOL_COL, realEstate.getPlace().isNextToSchool());
            placeValues.put(PLACE_IS_NEXT_TO_PARK_COL, realEstate.getPlace().isNextToPark());
            placeValues.put(PLACE_IS_NEXT_TO_STORE_COL, realEstate.getPlace().isNextToStore());


            double latitude = realEstate.getPlace().getLat();
            double longitude = realEstate.getPlace().getLng();
            placeValues.put(PLACE_LAT_COL, latitude);
            placeValues.put(PLACE_LNG_COL, longitude);
            placeValues.put(PLACE_COS_LAT_COL, Math.cos(MathUtil.deg2rad(latitude)));
            placeValues.put(PLACE_SIN_LAT_COL, Math.sin(MathUtil.deg2rad(latitude)));
            placeValues.put(PLACE_COS_LNG_COL, Math.cos(MathUtil.deg2rad(longitude)));
            placeValues.put(PLACE_SIN_LNG_COL, Math.sin(MathUtil.deg2rad(longitude)));

            SQLiteDatabase db = this.getReadableDatabase();

            db.insertWithOnConflict(TABLE_PLACE_NAME, null, placeValues,SQLiteDatabase.CONFLICT_REPLACE);
            db.close();

            values.put(PLACE_KEY, realEstate.getPlace().getPlaceId());
        }

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
        values.put(IS_SOLD_COL, realEstate.isSold());
        if (realEstate.getSoldDate() != null) {
            values.put(SOLD_DATE_COL, realEstate.getSoldDate().getTime());
        }
        values.put(IS_SYNC_COL, realEstate.isSync());
        values.put(IS_DRAFT_COL, realEstate.isDraft());

        context.getContentResolver().insert(RealEstateContentProvider.URI_REAL_ESTATE,values);

        realEstatesLiveData.postValue(getRealEstates());
    }

    public LiveData<List<RealEstate>> getRealEstateLiveData(List<Filter> filters) {
        this.filters = filters;
        realEstatesLiveData.postValue(getRealEstates());
        return realEstatesLiveData;
    }

    public List<RealEstate> getRealEstates() {

        List<RealEstate> realEstates = new ArrayList<>();

        String strQuery = null;
        if (! filters.isEmpty()) {
            strQuery = " ";

            for (int i = 0; i < filters.size(); i++) {
                if (i != 0) {
                    strQuery += " AND ";
                }
                strQuery += filters.get(i).getQueryStr();
            }
        }

        Cursor cursor = context.getContentResolver().query(
                RealEstateContentProvider.URI_REAL_ESTATE,
                null,strQuery,
                null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RealEstate realEstate = cursorToRealEstate(cursor);
            realEstates.add(realEstate);
            cursor.moveToNext();
        }


        cursor.close();

        return realEstates;
    }

    @SuppressLint("Range")
    private RealEstate cursorToRealEstate(Cursor c) {
        if (c.getCount() == 0)
            return null;

        RealEstate realEstate = new RealEstate();

        realEstate.setId(c.getString(c.getColumnIndex(ID_COL)));
        realEstate.setCreationDate(new Date(c.getLong(c.getColumnIndex(CREATION_COL))));

        realEstate.setUser(cursorToUser(c));

        realEstate.setPriceUSD(c.getInt(c.getColumnIndex(PRICE_USD_COL)));
        realEstate.setTypeId(c.getInt(c.getColumnIndex(TYPE_ID_COL)));
        List<Photo> photos = PhotoListConverter.fromString(c.getString(c.getColumnIndex(PHOTOS_COL)));
        realEstate.setPhotos(photos);
        realEstate.setNumberOfPhotos(c.getInt(c.getColumnIndex(NUMBER_PHOTOS_COL)));
        realEstate.setMainPicturePosition(c.getInt(c.getColumnIndex(MAIN_PICTURE_POSITION_COL)));
        realEstate.setDescription(c.getString(c.getColumnIndex(DESCRIPTION_COL)));
        realEstate.setSurface(c.getInt(c.getColumnIndex(SURFACE_COL)));
        realEstate.setNumberOfRooms(c.getInt(c.getColumnIndex(NUMBER_OF_ROOM_COL)));
        realEstate.setNumberOfBathrooms(c.getInt(c.getColumnIndex(NUMBER_OF_BATHROOM_COL)));
        realEstate.setNumberOfBedrooms(c.getInt(c.getColumnIndex(NUMBER_OF_BEDROOM_COL)));


        RealEstateLocation location = new RealEstateLocation();
        if (c.getString(c.getColumnIndex(PLACE_KEY)) != null) {
            location.setPlaceId(c.getString(c.getColumnIndex(PLACE_PLACE_ID_COL)));
            location.setName(c.getString(c.getColumnIndex(PLACE_NAME_COL)));
            location.setLat(c.getDouble(c.getColumnIndex(PLACE_LAT_COL)));
            location.setLng(c.getDouble(c.getColumnIndex(PLACE_LNG_COL)));
            location.setAddress(c.getString(c.getColumnIndex(PLACE_ADDRESS_COL)));
            location.setCountry(c.getString(c.getColumnIndex(PLACE_COUNTRY_COL)));
            location.setCity(c.getString(c.getColumnIndex(PLACE_CITY_COL)));
            if (c.getInt(c.getColumnIndex(PLACE_IS_NEXT_TO_SCHOOL_COL)) == 1) {
                location.setNextToSchool(true);
            }
            if (c.getInt(c.getColumnIndex(PLACE_IS_NEXT_TO_PARK_COL)) == 1) {
                location.setNextToPark(true);
            }
            if (c.getInt(c.getColumnIndex(PLACE_IS_NEXT_TO_STORE_COL)) == 1) {
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

        if (c.getInt(c.getColumnIndex(IS_SOLD_COL)) == 1){
            realEstate.setSold(true);
            realEstate.setSoldDate(new Date(c.getLong(c.getColumnIndex(SOLD_DATE_COL))));
        }
        if (c.getInt(c.getColumnIndex(IS_SYNC_COL)) == 1) {
            realEstate.setSync(true);
        }
        if (c.getInt(c.getColumnIndex(IS_DRAFT_COL)) == 1) {
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

            String query = ID_COL + " = " + "\"" + realEstate.getId() + "\"";

            context.getContentResolver().delete(RealEstateContentProvider.URI_REAL_ESTATE,query,null);
        }
    }

    public List<RealEstate> getNotSyncRealEstates () {
        List<RealEstate> realEstates = new ArrayList<>();

        String query = IS_SYNC_COL +" = 0 AND " + IS_DRAFT_COL + " = 0";
        Cursor cursor = context.getContentResolver().query(RealEstateContentProvider.URI_REAL_ESTATE,null, query, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RealEstate realEstate = cursorToRealEstate(cursor);
            realEstates.add(realEstate);
            cursor.moveToNext();
        }

        cursor.close();

        return realEstates;
    }

    public User getUserWithUid(String uid) {

        User user = null;
        SQLiteDatabase db = this.getReadableDatabase();

        String[] str = new String[] {"*"};

        Cursor cursor = db.query(TABLE_REAL_ESTATE_NAME,str,USER_UID_COL + " = \"" + uid + "\"",null,null,null,null);

        if (cursor.moveToFirst()) {
            user = cursorToUser(cursor);
        }



        cursor.close();

        return user;
    }

    @SuppressLint("Range")
    private User cursorToUser(Cursor cursor) {
        User user = new User();

        user.setUid(cursor.getString(cursor.getColumnIndex(USER_UID_COL)));
        user.setUsername(cursor.getString(cursor.getColumnIndex(USER_USERNAME_COL)));
        user.setUrlPicture(cursor.getString(cursor.getColumnIndex(USER_URL_PICTURE_COL)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(USER_EMAIL_COL)));
        user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(USER_PHONE_NUMBER_COL)));
        List<String> myRealEstateId = StringListConverter.fromString(cursor.getString(cursor.getColumnIndex(USER_REAL_ESTATE_COL)));
        user.setMyRealEstateId(myRealEstateId);

        return user;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REAL_ESTATE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACE_NAME);
        onCreate(sqLiteDatabase);
    }
}
