package com.tonyocallimoutou.realestatemanager.data.firebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.repository.RealEstateRepository;
import com.tonyocallimoutou.realestatemanager.util.Filter;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDataRealEstate {

    private final String COLLECTION_NAME;

    private static volatile FirebaseDataRealEstate instance;


    private FirebaseDataRealEstate(Context context) {
        COLLECTION_NAME = context.getString(R.string.COLLECTION_NAME_REAL_ESTATE);
    }

    public static FirebaseDataRealEstate getInstance(Context context ) {
        synchronized (FirebaseDataRealEstate.class) {
            if (instance == null) {
                instance = new FirebaseDataRealEstate(context);
            }
            return instance;
        }
    }

    // My Firestore Collection

    private CollectionReference getRealEstateCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Storage

    private FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }


    public void createRealEstate(RealEstate realEstate) {
        List<Photo> newList = new ArrayList<>();
        for (Photo photo : realEstate.getPhotos()) {

            Uri pictureUri = Uri.parse(photo.getReference());

            StorageReference ref = getFirebaseStorage().getReference(realEstate.getId()).child(pictureUri.getLastPathSegment());
            UploadTask uploadTask = ref.putFile(pictureUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Photo newPhoto = new Photo(downloadUri.toString(),photo.getDescription());

                        newList.add(newPhoto);

                        if (newList.size() == realEstate.getPhotos().size()) {
                            realEstate.setPhotos(newList);
                            getRealEstateCollection().document(realEstate.getId()).set(realEstate);
                        }
                    }
                }
            });
        }
        getRealEstateCollection().document(realEstate.getId()).set(realEstate);
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

        getRealEstateCollection().document(actual.getId()).set(actual);
    }

    public void getListWithFilter(List<Filter> filters, MutableLiveData<List<RealEstate>> liveData) {
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


                for (int i = 0; i < filters.size(); i++) {
                    realEstates = filters.get(i).modifyList(realEstates);
                }

                liveData.setValue(realEstates);
            }
        });
    }

    public RealEstate soldRealEstate(RealEstate realEstate) {
        if (realEstate.isSold()) {
            realEstate.setSold(false);
            realEstate.setSoldDate(null);
        }
        else {
            realEstate.setSold(true);
            realEstate.setSoldDate(Utils.getTodayDate());
        }
        getRealEstateCollection().document(realEstate.getId()).set(realEstate);

        return realEstate;
    }

    public void setMyRealEstates(User user) {

        getRealEstateCollection().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot document : list) {
                        RealEstate realEstates = document.toObject(RealEstate.class);

                        if (realEstates.getUser().getUid().equals(user.getUid())) {
                            realEstates.setUser(user);
                            getRealEstateCollection().document(realEstates.getId()).set(realEstates);
                        }
                    }
                }
            }
        });
    }

}
