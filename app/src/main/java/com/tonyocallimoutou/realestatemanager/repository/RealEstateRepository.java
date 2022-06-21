package com.tonyocallimoutou.realestatemanager.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.tonyocallimoutou.realestatemanager.data.firebase.FirebaseDataRealEstate;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.DatabaseRealEstateHandler;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.util.Filter;
import com.tonyocallimoutou.realestatemanager.util.UtilNotification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RealEstateRepository {

    private static volatile RealEstateRepository instance;

    private final FirebaseDataRealEstate firebaseDataRealEstate;
    private final DatabaseRealEstateHandler database;
    private static boolean isConnected;

    private List<Filter> filters = new ArrayList<>();

    private MediatorLiveData<List<RealEstate>> mediatorLiveData = new MediatorLiveData<>();

    private SupportSQLiteQuery query;
    private final Context context;


    private RealEstateRepository(Context context) {
        firebaseDataRealEstate = FirebaseDataRealEstate.getInstance(context);
        this.context = context;
        this.database = new DatabaseRealEstateHandler(context);
    }

    public static RealEstateRepository getInstance(Context context) {
        synchronized (RealEstateRepository.class) {
            if (instance == null) {
                instance = new RealEstateRepository(context);
            }
            return instance;
        }
    }

    public void createRealEstate(RealEstate realEstate) {
        realEstate.setSync(false);

        database.createRealEstate(realEstate);

        if (isConnected) {
            UtilNotification.createNotification(instance.context, realEstate);
            firebaseDataRealEstate.savePicture(context,realEstate, database);
        }
    }

    public void saveAsDraft(RealEstate realEstate) {
        realEstate.setDraft(true);

        database.createRealEstate(realEstate);
    }

    public void deleteDraft(RealEstate realEstate) {

        database.deleteDraft(realEstate);
    }

    public void setFilterList(List<Filter> list) {
        filters = list;
        initListWithFilter();
    }

    public LiveData<List<RealEstate>> getListWithFilter() {
        initListWithFilter();
        return mediatorLiveData;
    }

    private void initListWithFilter() {

        mediatorLiveData.removeSource(database.getRealEstateLiveData(filters));

        mediatorLiveData.addSource(database.getRealEstateLiveData(filters), new Observer<List<RealEstate>>() {
            @Override
            public void onChanged(List<RealEstate> realEstates) {
                mediatorLiveData.setValue(realEstates);
            }
        });
    }


    public RealEstate soldRealEstate(RealEstate realEstate) {
        realEstate.setSync(false);

        if (realEstate.isSold()) {
            realEstate.setSold(false);
            realEstate.setSoldDate(null);
        }
        else {
            realEstate.setSold(true);
            realEstate.setSoldDate(new Date());
        }

        if (isConnected) {
            firebaseDataRealEstate.editRealEstate(realEstate);
        }

        else {
            database.createRealEstate(realEstate);
        }

        return realEstate;

    }

    public void setMyRealEstates(User user) {

        if (isConnected) {
            firebaseDataRealEstate.setMyRealEstates(user);
        }
        else {
            List<RealEstate> realEstates = database.getRealEstates();

            for (RealEstate realEstate : realEstates) {
                if (realEstate.getUser().getUid().equals(user.getUid())) {
                    realEstate.setSync(false);
                    realEstate.setUser(user);
                    database.createRealEstate(realEstate);
                }
            }

        }
    }

    public static void ConnectionChanged(boolean result) {
        if (result && instance!= null && !isConnected) {
            Log.d("TAG", "ConnectionChanged: ");
            syncFirebase();
        }
        isConnected = result;
    }

    private static void syncFirebase() {
        instance.firebaseDataRealEstate.setListRealEstates(instance.database);

        List<RealEstate> realEstates = instance.database.getNotSyncRealEstates();

        for (RealEstate realEstate : realEstates) {
            UtilNotification.createNotification(instance.context, realEstate);
            if (realEstate.getProgressSync() == 100) {
                instance.firebaseDataRealEstate.editRealEstate(realEstate);
            } else {
                instance.firebaseDataRealEstate.savePicture(instance.context, realEstate, instance.database);

            }
        }
    }

}
