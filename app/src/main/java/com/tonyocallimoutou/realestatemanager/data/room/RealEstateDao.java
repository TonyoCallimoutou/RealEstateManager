package com.tonyocallimoutou.realestatemanager.data.room;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.List;

@Dao
public interface RealEstateDao {

    @Query("SELECT * FROM RealEstate")
    LiveData<List<RealEstate>> getAllRealEstates();

    @Insert (onConflict = REPLACE)
    void createOrReplaceRealEstate(RealEstate realEstate);

}
