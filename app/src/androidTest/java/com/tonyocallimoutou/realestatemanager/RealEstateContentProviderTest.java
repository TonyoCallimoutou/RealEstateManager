package com.tonyocallimoutou.realestatemanager;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.test.platform.app.InstrumentationRegistry;

import com.tonyocallimoutou.realestatemanager.data.localDatabase.DatabaseRealEstateHandler;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.DateConverter;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.PhotoListConverter;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.StringListConverter;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.provider.RealEstateContentProvider;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RealEstateContentProviderTest {

    private ContentResolver mContentResolver;

    // DATA SET FOR TEST

    private static long REAL_ESTATE_ID = 1;
    private long dateLong;

    @Before

    public void setUp() {

        mContentResolver = InstrumentationRegistry.getInstrumentation().getContext()

                .getContentResolver();

        dateLong = DateConverter.fromDate(new Date());

    }

    @Test

    public void insertAndGetItem() {
        // BEFORE : Adding demo item

        final Uri userUri = mContentResolver.insert(RealEstateContentProvider.URI_REAL_ESTATE, generateRealEstate());

        // TEST

        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_REAL_ESTATE, REAL_ESTATE_ID), null, null, null, null);

        assertThat(cursor, notNullValue());

        cursor.moveToFirst();

        Photo photo = new Photo("android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available",null);
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);
        String photosString = PhotoListConverter.fromList(photos);

        List<String> emptyList = new ArrayList<>();
        String myRealList = StringListConverter.fromList(emptyList);

        assertThat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.ID_COL)), is("ID"));
        assertThat(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.CREATION_COL)), is(dateLong));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.PRICE_USD_COL)), is(1000));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.TYPE_ID_COL)), is(1));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.DESCRIPTION_COL)), is("description"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.PHOTOS_COL)), is(photosString));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.NUMBER_PHOTOS_COL)), is(2));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.MAIN_PICTURE_POSITION_COL)), is(0));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.SURFACE_COL)), is(233));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.NUMBER_OF_ROOM_COL)), is(2));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.NUMBER_OF_BATHROOM_COL)), is(3));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.NUMBER_OF_BEDROOM_COL)), is(4));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.IS_SYNC_COL)), is(1));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.IS_SOLD_COL)), is(1));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.IS_DRAFT_COL)), is(0));
        assertThat(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.SOLD_DATE_COL)), is(dateLong));


        assertThat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.USER_USERNAME_COL)), is("username"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.USER_URL_PICTURE_COL)), is(photo.getReference()));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.USER_PHONE_NUMBER_COL)), is("phone_number"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.USER_EMAIL_COL)), is("user_email"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.USER_REAL_ESTATE_COL)), is(myRealList));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseRealEstateHandler.PLACE_KEY)), is("placeId"));

    }


    private ContentValues generateRealEstate(){

        final ContentValues values = new ContentValues();

        Photo photo = new Photo("android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available",null);
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);

        String photosString = PhotoListConverter.fromList(photos);

        List<String> emptyList = new ArrayList<>();
        String myRealList = StringListConverter.fromList(emptyList);

        values.put(DatabaseRealEstateHandler.ID_COL, "ID");
        values.put(DatabaseRealEstateHandler.CREATION_COL, dateLong);
        values.put(DatabaseRealEstateHandler.PRICE_USD_COL, 1000);
        values.put(DatabaseRealEstateHandler.TYPE_ID_COL, 1);
        values.put(DatabaseRealEstateHandler.DESCRIPTION_COL, "description");
        values.put(DatabaseRealEstateHandler.PHOTOS_COL, photosString);
        values.put(DatabaseRealEstateHandler.NUMBER_PHOTOS_COL, 2);
        values.put(DatabaseRealEstateHandler.MAIN_PICTURE_POSITION_COL, 0);
        values.put(DatabaseRealEstateHandler.SURFACE_COL, 233);
        values.put(DatabaseRealEstateHandler.NUMBER_OF_ROOM_COL, 2);
        values.put(DatabaseRealEstateHandler.NUMBER_OF_BATHROOM_COL, 3);
        values.put(DatabaseRealEstateHandler.NUMBER_OF_BEDROOM_COL, 4);
        values.put(DatabaseRealEstateHandler.IS_SYNC_COL, true);
        values.put(DatabaseRealEstateHandler.IS_SOLD_COL, true);
        values.put(DatabaseRealEstateHandler.IS_DRAFT_COL, false);
        values.put(DatabaseRealEstateHandler.SOLD_DATE_COL, dateLong);


        values.put(DatabaseRealEstateHandler.USER_USERNAME_COL, "username");
        values.put(DatabaseRealEstateHandler.USER_URL_PICTURE_COL, photo.getReference());
        values.put(DatabaseRealEstateHandler.USER_PHONE_NUMBER_COL, "phone_number");
        values.put(DatabaseRealEstateHandler.USER_EMAIL_COL, "user_email");
        values.put(DatabaseRealEstateHandler.USER_REAL_ESTATE_COL, myRealList);
        values.put(DatabaseRealEstateHandler.USER_IS_EMAIL_VERIFY_COL, true);
        values.put(DatabaseRealEstateHandler.PLACE_KEY,"placeId");

        return values;

    }
}
