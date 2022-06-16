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

    @Query("SELECT * FROM RealEstate")
    List<RealEstate> getRealEstates();

    @Query("SELECT * FROM RealEstate")
    LiveData<List<RealEstate>> getRealEstatesLiveData();

    @Query("SELECT * FROM RealEstate WHERE real_estate_is_synchro = 0 AND real_estate_is_draft = 0")
    List<RealEstate> getNotSyncRealEstates();

    @Query("DELETE FROM RealEstate WHERE realEstate_id = :id AND real_estate_is_draft = 1")
    void deleteDraft(String id);
}
