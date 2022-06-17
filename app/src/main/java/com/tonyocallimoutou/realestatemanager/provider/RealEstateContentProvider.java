package com.tonyocallimoutou.realestatemanager.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tonyocallimoutou.realestatemanager.data.room.LocalDatabase;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;

public class RealEstateContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.tonyocallimoutou.realestatemanager";

    public static final String TABLE_NAME = RealEstate.class.getSimpleName();

    public static final Uri URI_REAL_ESTATE = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);


    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        if (getContext() != null) {

            String id = String.valueOf(ContentUris.parseId(uri));

            final Cursor cursor = LocalDatabase.getInstance(getContext()).realEstateDao().getRealEstateWithCursor(id);

            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;

        }

        throw new IllegalArgumentException("Failed to query row for uri " + uri);

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        return "vnd.android.cursor.item/" + AUTHORITY + "." + TABLE_NAME;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        if (getContext() != null && contentValues != null) {

            final long id = LocalDatabase.getInstance(getContext()).realEstateDao().createRealEstate(RealEstate.fromContentValues(contentValues));

            if (id != 0) {

                getContext().getContentResolver().notifyChange(uri, null);

                return ContentUris.withAppendedId(uri, id);

            }

        }

        throw new IllegalArgumentException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        throw new IllegalArgumentException("Failed to delete row into " );
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {

        if (getContext() != null && contentValues != null) {

            final int count = LocalDatabase.getInstance(getContext()).realEstateDao().updateRealEstate(RealEstate.fromContentValues(contentValues));

            getContext().getContentResolver().notifyChange(uri, null);

            return count;

        }

        throw new IllegalArgumentException("Failed to update row into " + uri);
    }
}
