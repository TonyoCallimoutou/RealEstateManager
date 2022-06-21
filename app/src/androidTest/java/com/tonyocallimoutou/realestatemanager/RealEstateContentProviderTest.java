package com.tonyocallimoutou.realestatemanager;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.tonyocallimoutou.realestatemanager.provider.RealEstateContentProvider;

import org.junit.Before;
import org.junit.Test;

public class RealEstateContentProviderTest {

    private ContentResolver mContentResolver;

    // DATA SET FOR TEST

    private static long REAL_ESTATE_ID = 1;

    @Before

    public void setUp() {

        Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().getContext()
                ,
                LocalDatabase.class)
                .allowMainThreadQueries()
                .build();


        mContentResolver = InstrumentationRegistry.getInstrumentation().getContext()

                .getContentResolver();

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

        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("realEstate_id")), is("1"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("creationDate")), is("creationDate"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("priceUSD")), is("1000"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("description")), is("description"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("surface")), is("233"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("numberOfRooms")), is("2"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("numberOfBathrooms")), is("3"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("numberOfBedrooms")), is("4"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("isSold")), is("1"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("soldDate")), is("soldDate"));

        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("user_uid")), is("uid"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("user_user_name")), is("username"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("user_user_phone_number")), is("phone_number"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("user_email")), is("user_email"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_placeId")),is("placeId"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_name")), is("place_name"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_lat")), is("10"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_lng")), is("20"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_address")), is("place_address"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_country")), is("place_country"));
        assertThat(cursor.getString(cursor.getColumnIndexOrThrow("place_city")), is("place_city"));


    }


    private ContentValues generateRealEstate(){

        final ContentValues values = new ContentValues();

        values.put("id", REAL_ESTATE_ID);
        values.put("creationDate", "creationDate");
        values.put("priceUSD", 1000);
        values.put("description", "description");
        values.put("surface", 233);
        values.put("numberOfRooms", 2);
        values.put("numberOfBathrooms", 3);
        values.put("numberOfBedrooms", 4);
        values.put("isSold", true);
        values.put("soldDate", "soldDate");

        values.put("user_uid", "uid");
        values.put("user_user_name", "username");
        values.put("user_user_phone_number", "phone_number");
        values.put("user_email", "user_email");
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
