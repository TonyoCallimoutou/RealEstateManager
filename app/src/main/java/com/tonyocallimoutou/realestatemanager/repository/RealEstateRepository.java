package com.tonyocallimoutou.realestatemanager.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    private List<Filter> filters = new ArrayList<>();

    private final MutableLiveData<List<RealEstate>> listRealEstateLiveData = new MutableLiveData<>();


    private RealEstateRepository(Context context, RealEstateDao realEstateDao, Executor executor) {
        firebaseDataRealEstate = FirebaseDataRealEstate.getInstance(context);
        this.realEstateDao = realEstateDao;
        this.executor = executor;
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
        firebaseDataRealEstate.createRealEstate(realEstate);
    }

    public void editRealEstate(RealEstate actual, RealEstate modify) {
        firebaseDataRealEstate.editRealEstate(actual,modify);
    }

    public void setFilterList(List<Filter> list) {
        filters = list;
        firebaseDataRealEstate.getListWithFilter(filters,listRealEstateLiveData);
    }

    public LiveData<List<RealEstate>> getListWithFilter() {
        firebaseDataRealEstate.getListWithFilter(filters,listRealEstateLiveData);
        return listRealEstateLiveData;
    }

    public RealEstate soldRealEstate(RealEstate realEstate) {
        return firebaseDataRealEstate.soldRealEstate(realEstate);
    }

    public void setMyRealEstates(User user) {
        firebaseDataRealEstate.setMyRealEstates(user);
    }

}
