package com.tonyocallimoutou.realestatemanager.data.room;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.List;

@Dao
public interface RealEstateDao {

    @Insert (onConflict = REPLACE)
    void createRealEstate(RealEstate realEstate);

    @Query("SELECT * FROM RealEstate WHERE real_estate_is_synchro = 1")
    List<RealEstate> getRealEstates();

    @Query("SELECT * FROM RealEstate WHERE real_estate_is_synchro = 0")
    List<RealEstate> getDraftRealEstates();

    @Query("SELECT * FROM RealEstate WHERE real_estate_is_synchro = 0")
    LiveData<List<RealEstate>> getDraftRealEstatesLiveData();
}
