package com.tonyocallimoutou.realestatemanager.repository;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.ArrayList;
import java.util.List;

public class RealEstateRepository {

    private final String COLLECTION_NAME;

    private static volatile RealEstateRepository instance;


    private RealEstateRepository(Context context) {
        COLLECTION_NAME = context.getString(R.string.COLLECTION_NAME_REAL_ESTATE);
    }

    public static RealEstateRepository getInstance(Context context ) {
        synchronized (RealEstateRepository.class) {
            if (instance == null) {
                instance = new RealEstateRepository(context);
            }
            return instance;
        }
    }

    // My Firestore Collection

    private CollectionReference getRealEstateCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void createRealEstate(RealEstate realEstate) {
        getRealEstateCollection().document(realEstate.getId()).set(realEstate);
    }

    public void getAllRealEstates(MutableLiveData<List<RealEstate>> liveData) {
        getRealEstateCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                List<RealEstate> realEstates = new ArrayList<>();
                for (DocumentSnapshot document : value) {
                    RealEstate realEstate = document.toObject(RealEstate.class);
                    realEstates.add(realEstate);
                }

                liveData.setValue(realEstates);

                liveData.setValue(FakeData.getFakeList());
            }
        });
    }

}
