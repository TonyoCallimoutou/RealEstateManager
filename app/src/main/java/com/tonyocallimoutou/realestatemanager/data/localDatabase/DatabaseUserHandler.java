package com.tonyocallimoutou.realestatemanager.data.localDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.tonyocallimoutou.realestatemanager.model.User;

import java.util.List;

public class DatabaseUserHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "userDatabase";
    private static final int DB_VERSION = 1;
    private static final String TABLE_USER_NAME = "users";
    public static final String EMAIL_COL = "email";
    public static final String USERNAME_COL = "username";
    public static final String PICTURE_REFERENCE_COL = "picture_reference";
    public static final String PHONE_NUMBER_COL = "phoneNumber";
    public static final String MY_REAL_ESTATE_ID_COL = "myRealEstateId";
    public static final String IS_EMAIL_VERIFY_ID_COL = "isEmailVerify";


    private static final int NUM_COL_EMAIL = 0;
    private static final int NUM_COL_USERNAME = 1;
    private static final int NUM_COL_URL_PICTURE = 2;
    private static final int NUM_COL_PHONE_NUMBER_ = 3;
    private static final int NUM_COL_MY_REAL_ESTATE_ID = 4;
    private static final int NUM_COL_IS_EMAIL_VERIFY = 5;

    private final MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();

    public DatabaseUserHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_USER_NAME + " ("
                + EMAIL_COL + " TEXT PRIMARY KEY NOT NULL,"
                + USERNAME_COL + " TEXT NOT NULL,"
                + PICTURE_REFERENCE_COL + " TEXT NOT NULL,"
                + PHONE_NUMBER_COL + " TEXT,"
                + MY_REAL_ESTATE_ID_COL + " TEXT NOT NULL,"
                + IS_EMAIL_VERIFY_ID_COL + " INTEGER NOT NULL)";

        // at last we are calling a exec sql
        // method to execute above sql query
        sqLiteDatabase.execSQL(query);

    }

    public void createUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        String listMyRealEstate = StringListConverter.fromList(user.getMyRealEstateId());



        values.put(EMAIL_COL, user.getEmail());
        values.put(USERNAME_COL, user.getUsername());
        values.put(PICTURE_REFERENCE_COL, user.getUrlPicture());
        values.put(PHONE_NUMBER_COL, user.getPhoneNumber());
        values.put(MY_REAL_ESTATE_ID_COL, listMyRealEstate);
        values.put(IS_EMAIL_VERIFY_ID_COL, user.isEmailVerify());


        db.insertWithOnConflict(TABLE_USER_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        currentUserLiveData.postValue(getCurrentUser(user.getEmail()));
    }


    private User cursorToUser(Cursor c) {
        if (c.getCount() == 0)
            return null;

        User user = new User();
        List<String> myRealEstateId = StringListConverter.fromString(c.getString(NUM_COL_MY_REAL_ESTATE_ID));


        user.setEmail(c.getString(NUM_COL_EMAIL));
        user.setUsername(c.getString(NUM_COL_USERNAME));
        user.setUrlPicture(c.getString(NUM_COL_URL_PICTURE));
        user.setPhoneNumber(c.getString(NUM_COL_PHONE_NUMBER_));
        user.setMyRealEstateId(myRealEstateId);
        if (c.getInt(NUM_COL_IS_EMAIL_VERIFY) == 1) {
            user.setEmailVerify(true);
        }

        return user;
    }

    public void deleteUser(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = EMAIL_COL + " = " + "\"" + getCurrentUser(id).getEmail() + "\"";
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

    public LiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public void initLiveData(String id) {
        currentUserLiveData.postValue(getCurrentUser(id));
    }

    public User getCurrentUser(String id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER_NAME + " WHERE " + EMAIL_COL + " = \""+ id + "\"", null);

        cursor.moveToFirst();
        User user = cursorToUser(cursor);

        cursor.close();

        return user;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_NAME);
        onCreate(sqLiteDatabase);
    }
}
