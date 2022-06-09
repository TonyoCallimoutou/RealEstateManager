package com.tonyocallimoutou.realestatemanager.data.room;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tonyocallimoutou.realestatemanager.model.User;

@Dao
public interface UserDao {

    @Insert (onConflict = REPLACE)
    void createUser (User user);

    @Query("SELECT * FROM User WHERE uid = :id")
    LiveData<User> getCurrentUserLiveData(String id);

    @Query("DELETE FROM User WHERE uid = :id")
    void deleteUser(String id);

    @Query("UPDATE User SET user_name = :name WHERE uid = :id")
    void setNameOfCurrentUser(String id, String name);


}
