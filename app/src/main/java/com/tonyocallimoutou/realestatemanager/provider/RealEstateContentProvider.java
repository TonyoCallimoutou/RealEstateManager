package com.tonyocallimoutou.realestatemanager.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tonyocallimoutou.realestatemanager.data.localDatabase.DatabaseRealEstateHandler;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;

public class RealEstateContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.tonyocallimoutou.realestatemanager";

    public static final String TABLE_NAME = RealEstate.class.getSimpleName();

    public static final Uri URI_REAL_ESTATE = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    private SQLiteDatabase database;


    @Override
    public boolean onCreate() {
        DatabaseRealEstateHandler helper = new DatabaseRealEstateHandler(getContext());
        database = helper.getWritableDatabase();
        return true;

    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        if (getContext() != null) {

            String sql = "SELECT * from " +  DatabaseRealEstateHandler.TABLE_REAL_ESTATE_NAME +
                    " LEFT JOIN " + DatabaseRealEstateHandler.TABLE_PLACE_NAME +
                    " ON " + DatabaseRealEstateHandler.PLACE_KEY + " = " + DatabaseRealEstateHandler.PLACE_PLACE_ID_COL;

            if (s != null) {
                sql += " WHERE " + s;
            }


            sql += " ORDER BY " +  DatabaseRealEstateHandler.CREATION_COL + " DESC";

            Cursor cursor = database.rawQuery(sql, null);

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
        long id = database.insertWithOnConflict(DatabaseRealEstateHandler.TABLE_REAL_ESTATE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        if (id > 0) {
            Uri _uri = ContentUris.withAppendedId(URI_REAL_ESTATE, id);
            getContext().getContentResolver().notifyChange(_uri, null);

            return _uri;
        }
        throw new SQLException("Insertion Failed for URI :" + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int delCount = 0;
        delCount = database.delete(DatabaseRealEstateHandler.TABLE_REAL_ESTATE_NAME,s,strings);
        getContext().getContentResolver().notifyChange(uri, null);
        return delCount;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int updCount = 0;
        updCount = database.update(DatabaseRealEstateHandler.TABLE_REAL_ESTATE_NAME, contentValues, s, strings);
        getContext().getContentResolver().notifyChange(uri, null);
        return updCount;
    }
}
