package com.tonyocallimoutou.realestatemanager.data.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;

@Database(entities = {User.class, RealEstate.class},version = 1, exportSchema = false)
@TypeConverters({StringListConverter.class})
public abstract class LocalDatabase extends RoomDatabase {

    // Singleton
    private static volatile LocalDatabase INSTANCE;

    // DAO
    public abstract UserDao userDao();
    public abstract RealEstateDao realEstateDao();

    // Instance
    public static LocalDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LocalDatabase.class,
                            "MyDatabase.db")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

}
