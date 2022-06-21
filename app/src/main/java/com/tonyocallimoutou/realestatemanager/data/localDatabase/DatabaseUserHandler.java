package com.tonyocallimoutou.realestatemanager.data.localDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tonyocallimoutou.realestatemanager.model.User;

import java.util.List;

public class DatabaseUserHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "userDatabase";
    private static final int DB_VERSION = 1;
    private static final String TABLE_USER_NAME = "users";
    public static final String UID_COL = "uid";
    public static final String USERNAME_COL = "username";
    public static final String PICTURE_REFERENCE_COL = "picture_reference";
    public static final String EMAIL_COL = "email";
    public static final String PHONE_NUMBER_COL = "phoneNumber";
    public static final String MY_REAL_ESTATE_ID_COL = "myRealEstateId";

    private static final int NUM_COL_UID = 0;
    private static final int NUM_COL_USERNAME = 1;
    private static final int NUM_COL_URL_PICTURE = 2;
    private static final int NUM_COL_EMAIL = 3;
    private static final int NUM_COL_PHONE_NUMBER_ = 4;
    private static final int NUM_COL_MY_REAL_ESTATE_ID = 5;

    public DatabaseUserHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_USER_NAME + " ("
                + UID_COL + " TEXT PRIMARY KEY,"
                + USERNAME_COL + " TEXT NOT NULL,"
                + PICTURE_REFERENCE_COL + " TEXT NOT NULL,"
                + EMAIL_COL + " TEXT NOT NULL,"
                + PHONE_NUMBER_COL + " TEXT,"
                + MY_REAL_ESTATE_ID_COL + " TEXT NOT NULL)";

        // at last we are calling a exec sql
        // method to execute above sql query
        sqLiteDatabase.execSQL(query);

    }

    public void createUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        String listMyRealEstate = StringListConverter.fromList(user.getMyRealEstateId());


        values.put(UID_COL, user.getUid());
        values.put(USERNAME_COL, user.getUsername());
        values.put(PICTURE_REFERENCE_COL, user.getUrlPicture());
        values.put(EMAIL_COL, user.getEmail());
        values.put(PHONE_NUMBER_COL, user.getPhoneNumber());
        values.put(MY_REAL_ESTATE_ID_COL, listMyRealEstate);

        db.insertWithOnConflict(TABLE_USER_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }


    private User cursorToUser(Cursor c) {
        if (c.getCount() == 0)
            return null;

        User user = new User();
        List<String> myRealEstateId = StringListConverter.fromString(c.getString(NUM_COL_MY_REAL_ESTATE_ID));

        user.setUid(c.getString(NUM_COL_UID));
        user.setUsername(c.getString(NUM_COL_USERNAME));
        user.setUrlPicture(c.getString(NUM_COL_URL_PICTURE));
        user.setEmail(c.getString(NUM_COL_EMAIL));
        user.setPhoneNumber(c.getString(NUM_COL_PHONE_NUMBER_));
        user.setMyRealEstateId(myRealEstateId);

        return user;
    }

    public void deleteUser(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = UID_COL + " = " + "\"" + getCurrentUser(id).getUid() + "\"";
        db.delete(TABLE_USER_NAME, query,null);
        db.close();
    }

    public void setNameOfCurrentUser(String id, String name) {
        User user = getCurrentUser(id);
        user.setUsername(name);

        createUser(user);
    }
    public void setCurrentUserPicture(String id, String picture) {
        User user = getCurrentUser(id);
        user.setUrlPicture(picture);

        createUser(user);
    }
    public void setPhoneNumberOfCurrentUser(String id,String phoneNumber) {
        User user = getCurrentUser(id);
        user.setPhoneNumber(phoneNumber);

        createUser(user);
    }

    public User getCurrentUser(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER_NAME + " WHERE " + UID_COL + " = :" + id, null);

        cursor.moveToFirst();
        User user = cursorToUser(cursor);

        cursor.close();

        return user;
    }

    public void signOut(String id) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        String listMyRealEstate = StringListConverter.fromList(getCurrentUser(id).getMyRealEstateId());

        values.put(UID_COL, getCurrentUser(id).getUid());
        values.put(USERNAME_COL, getCurrentUser(id).getUsername());
        values.put(PICTURE_REFERENCE_COL, getCurrentUser(id).getUrlPicture());
        values.put(EMAIL_COL, getCurrentUser(id).getEmail());
        values.put(PHONE_NUMBER_COL, getCurrentUser(id).getPhoneNumber());
        values.put(MY_REAL_ESTATE_ID_COL, listMyRealEstate);

        db.insertWithOnConflict(TABLE_USER_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_NAME);
        onCreate(sqLiteDatabase);
    }
}
