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

    public void getItemsWhenNoItemInserted() {

        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_REAL_ESTATE, REAL_ESTATE_ID), null, null, null, null);

        assertThat(cursor, notNullValue());

        assertThat(cursor.getCount(), is(0));

        cursor.close();

    }

    @Test

    public void insertAndGetItem() {

        // BEFORE : Adding demo item

        final Uri userUri = mContentResolver.insert(RealEstateContentProvider.URI_REAL_ESTATE, generateRealEstate());

        // TEST

        final Cursor cursor = mContentResolver.query(ContentUris.withAppendedId(RealEstateContentProvider.URI_REAL_ESTATE, REAL_ESTATE_ID), null, null, null, null);

        assertThat(cursor, notNullValue());

        assertThat(cursor.getCount(), is(1));

        assertThat(cursor.moveToFirst(), is(true));



        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("id")), is("1"));
        assertThat(cursor.getLong(cursor.getColumnIndexOrThrow("creationDate")), is(dateLong));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("priceUSD")), is(1000));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("description")), is("description"));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("surface")), is(233));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("numberOfRooms")), is(2));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("numberOfBathrooms")), is(3));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("numberOfBedrooms")), is(4));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("isSold")), is(1));
        assertThat(cursor.getLong(cursor.getColumnIndexOrThrow("soldDate")), is(dateLong));

        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("user_uid")), is("uid"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("user_username")), is("username"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("user_phoneNumber")), is("phone_number"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("user_email")), is("user_email"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_placeId")),is("placeId"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_name")), is("place_name"));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("place_lat")), is(10));
        assertThat(cursor.getInt(cursor.getColumnIndexOrThrow("place_lng")), is(20));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_address")), is("place_address"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_country")), is("place_country"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_city")), is("place_city"));


    }


    private ContentValues generateRealEstate(){

        final ContentValues values = new ContentValues();

        Photo photo = new Photo("android.resource://com.tonyocallimoutou.realestatemanager/drawable/ic_no_image_available",null);
        List<Photo> photos = new ArrayList<>();
        photos.add(photo);

        String photosString = PhotoListConverter.fromList(photos);

        List<String> emptyList = new ArrayList<>();
        String myRealList = StringListConverter.fromList(emptyList);

        values.put("id", REAL_ESTATE_ID);
        values.put("creationDate", dateLong);
        values.put("priceUSD", 1000);
        values.put("typeId", 1);
        values.put("description", "description");
        values.put("photos_reference", photosString);
        values.put("numberOfPhotos", 2);
        values.put("mainPicturePosition", 0);
        values.put("surface", 233);
        values.put("numberOfRooms", 2);
        values.put("numberOfBathrooms", 3);
        values.put("numberOfBedrooms", 4);
        values.put("isSync", true);
        values.put("isSold", true);
        values.put("isDraft", false);
        values.put("soldDate", dateLong);

        values.put("user_uid", "uid");
        values.put("user_username", "username");
        values.put("user_urlPicture", photo.getReference());
        values.put("user_phoneNumber", "phone_number");
        values.put("user_email", "user_email");
        values.put("user_myRealEstateId", myRealList);
        values.put("place_placeId","placeId");
        values.put("place_name", "place_name");
        values.put("place_lat", 10);
        values.put("place_lng", 20);
        values.put("place_address", "place_address");
        values.put("place_country", "place_country");
        values.put("place_city", "place_city");

        return values;

    }
}
