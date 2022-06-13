package com.tonyocallimoutou.realestatemanager.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tonyocallimoutou.realestatemanager.FAKE.FakeData;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.data.firebase.FirebaseDataRealEstate;
import com.tonyocallimoutou.realestatemanager.data.room.RealEstateDao;
import com.tonyocallimoutou.realestatemanager.data.room.UserDao;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.util.Filter;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

public class RealEstateRepository {

    private static volatile RealEstateRepository instance;

    private final FirebaseDataRealEstate firebaseDataRealEstate;
    private final RealEstateDao realEstateDao;
    private final Executor executor;
    private final SharedPreferences sharedPreferences;

    private final String currentUserId;

    private static boolean isConnected;

    private List<Filter> filters = new ArrayList<>();

    private final MutableLiveData<List<RealEstate>> realEstateFilterLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<RealEstate>> allRealEstateSyncLiveData = new MutableLiveData<>();
    private List<RealEstate> allRealEstates = new ArrayList<>();


    private RealEstateRepository(Context context, RealEstateDao realEstateDao, Executor executor) {
        firebaseDataRealEstate = FirebaseDataRealEstate.getInstance(context);
        this.realEstateDao = realEstateDao;
        this.executor = executor;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentUserId = sharedPreferences.getString(context.getString(R.string.shared_preference_user_uid), "");
    }

    public static RealEstateRepository getInstance(Context context,RealEstateDao realEstateDao, Executor executor) {
        synchronized (RealEstateRepository.class) {
            if (instance == null) {
                instance = new RealEstateRepository(context, realEstateDao, executor);
            }
            return instance;
        }
    }

    public void createRealEstate(RealEstate realEstate) {

        if (isConnected) {
            firebaseDataRealEstate.savePicture(realEstate, realEstateDao, executor);
        }
        else {
            executor.execute(() -> {
                realEstate.setSync(false);
                realEstateDao.createRealEstate(realEstate);
            });
        }
    }

    public void editRealEstate(RealEstate actual, RealEstate modify) {
        actual.setPhotos(modify.getPhotos());
        actual.setPriceUSD(modify.getPriceUSD());
        actual.setMainPicturePosition(modify.getMainPicturePosition());
        actual.setNumberOfBathrooms(modify.getNumberOfBathrooms());
        actual.setNumberOfRooms(modify.getNumberOfRooms());
        actual.setNumberOfBedrooms(modify.getNumberOfBedrooms());
        actual.setSurface(modify.getSurface());
        actual.setDescription(modify.getDescription());
        actual.setPlace(modify.getPlace());
        actual.setType(modify.getType());
        actual.setSync(false);


        if (isConnected) {
            firebaseDataRealEstate.savePicture(actual, realEstateDao, executor);
        }
        else {
            executor.execute(() -> {
                realEstateDao.createRealEstate(actual);
            });
        }
    }

    public LiveData<List<RealEstate>> getAllRealEstate() {
        if (isConnected) {
            firebaseDataRealEstate.getListRealEstates(allRealEstateSyncLiveData);
        }
        else {
            executor.execute(() -> {
                allRealEstateSyncLiveData.postValue(realEstateDao.getRealEstates());
            });
        }

        return allRealEstateSyncLiveData;
    }

    public LiveData<List<RealEstate>> getDraftRealEstate() {
        return realEstateDao.getDraftRealEstatesLiveData();
    }

    public void setFilterList(List<Filter> list) {
        filters = list;
        initListWithFilter();
    }

    public LiveData<List<RealEstate>> getListWithFilter() {
        initListWithFilter();
        return realEstateFilterLiveData;
    }

    private void initListWithFilter() {

        List<RealEstate> newList = new ArrayList<>(allRealEstates);

        for (int i = 0; i < filters.size(); i++) {
            newList = filters.get(i).modifyList(newList);
        }

        realEstateFilterLiveData.setValue(newList);
    }

    public RealEstate soldRealEstate(RealEstate realEstate) {
        realEstate.setSync(false);

        if (realEstate.isSold()) {
            realEstate.setSold(false);
            realEstate.setSoldDate(null);
        }
        else {
            realEstate.setSold(true);
            realEstate.setSoldDate(Utils.getTodayDate());
        }

        if (isConnected) {
            firebaseDataRealEstate.editRealEstate(realEstate);
        }

        else {
            executor.execute(() -> {
                realEstateDao.createRealEstate(realEstate);
            });
        }

        return realEstate;

    }

    public void setMyRealEstates(User user) {

        if (isConnected) {
            firebaseDataRealEstate.setMyRealEstates(user);
        }
        else {
            executor.execute(() -> {
                List<RealEstate> realEstates = realEstateDao.getRealEstates();

                for (RealEstate realEstate : realEstates) {
                    if (realEstate.getUser().getUid().equals(user.getUid())) {
                        realEstate.setSync(false);
                        realEstate.setUser(user);
                    }
                }
            });
        }
    }

    public void syncInFirebase(RealEstate realEstate) {
        if (isConnected) {
            Log.d("TAG", "syncInFirebase: ");
            firebaseDataRealEstate.editRealEstate(realEstate);
        }
    }

    public void setRealEstatesList(List<RealEstate> realEstates) {
        allRealEstates.clear();
        allRealEstates.addAll(realEstates);
        initListWithFilter();
        if (isConnected) {
            for (RealEstate realEstate : realEstates) {
                executor.execute(() -> {
                    realEstateDao.createRealEstate(realEstate);
                });
            }
        }
    }

    public static void ConnectionChanged(boolean result) {
        if (result && instance!= null) {
            syncFirebase();
            instance.firebaseDataRealEstate.getListRealEstates(instance.allRealEstateSyncLiveData);
        }
        isConnected = result;
    }

    private static void syncFirebase() {

        instance.executor.execute(()-> {
            List<RealEstate> realEstates = instance.realEstateDao.getDraftRealEstates();

            for (RealEstate realEstate : realEstates) {
                if (realEstate.getProgressSync() == 100) {
                    instance.firebaseDataRealEstate.editRealEstate(realEstate);
                }
                else {
                    instance.firebaseDataRealEstate.savePicture(realEstate, instance.realEstateDao, instance.executor);
                }
            }
        });
    }

}
