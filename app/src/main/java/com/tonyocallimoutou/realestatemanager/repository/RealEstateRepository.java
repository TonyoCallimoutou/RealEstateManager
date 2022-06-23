package com.tonyocallimoutou.realestatemanager.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.tonyocallimoutou.realestatemanager.data.NearbyPlace;
import com.tonyocallimoutou.realestatemanager.data.RetrofitPlace;
import com.tonyocallimoutou.realestatemanager.data.firebase.FirebaseDataRealEstate;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.DatabaseRealEstateHandler;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.RealEstateLocation;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.util.Filter;
import com.tonyocallimoutou.realestatemanager.util.UtilNotification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RealEstateRepository {

    private static volatile RealEstateRepository instance;

    private final FirebaseDataRealEstate firebaseDataRealEstate;
    private final DatabaseRealEstateHandler database;
    private static boolean isConnected;

    private List<Filter> filters = new ArrayList<>();

    private final MediatorLiveData<List<RealEstate>> mediatorLiveData = new MediatorLiveData<>();

    private final Context context;

    private final RetrofitPlace retrofitPlace;


    private RealEstateRepository(Context context) {
        this.firebaseDataRealEstate = FirebaseDataRealEstate.getInstance(context);
        this.context = context;
        this.database = new DatabaseRealEstateHandler(context);
        this.retrofitPlace = RetrofitPlace.retrofit.create(RetrofitPlace.class);
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
                Log.d("TAG", "onChanged: MEDIATOR ");
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

    public void verifyNearbyPlace(RealEstateLocation place){


        String location = place.getLat() + "," + place.getLng();

        retrofitPlace.getNearbySchool(location).enqueue(new Callback<NearbyPlace>() {
            @Override
            public void onResponse(Call<NearbyPlace> call, Response<NearbyPlace> response) {
                if (response.body() != null && !response.body().getResults().isEmpty()) {
                    place.setNextToSchool(true);
                }
            }

            @Override
            public void onFailure(Call<NearbyPlace> call, Throwable t) {
                Log.e("TAG", "onFailure: ", t);
            }
        });

        retrofitPlace.getNearbyPark(location).enqueue(new Callback<NearbyPlace>() {
            @Override
            public void onResponse(Call<NearbyPlace> call, Response<NearbyPlace> response) {
                if (response.body() != null && !response.body().getResults().isEmpty()) {
                    place.setNextToPark(true);
                }
            }

            @Override
            public void onFailure(Call<NearbyPlace> call, Throwable t) {
                Log.e("TAG", "onFailure: ", t);
            }
        });

        retrofitPlace.getNearbyStore(location).enqueue(new Callback<NearbyPlace>() {
            @Override
            public void onResponse(Call<NearbyPlace> call, Response<NearbyPlace> response) {
                if (response.body() != null && !response.body().getResults().isEmpty()) {
                    place.setNextToStore(true);
                }
            }

            @Override
            public void onFailure(Call<NearbyPlace> call, Throwable t) {
                Log.e("TAG", "onFailure: ", t);
            }
        });

    }

    public void firstConnection() {
        if (isConnected) {
            firebaseDataRealEstate.setListRealEstates(database);
        }
    }

    public static void ConnectionChanged(boolean result) {
        if (result && instance!= null && !isConnected) {
            Log.d("TAG", "ConnectionChanged: ");
            syncFirebase();
        }
        isConnected = result;
    }

    public static void syncFirebase() {
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
