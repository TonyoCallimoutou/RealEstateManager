package com.tonyocallimoutou.realestatemanager.data.firebase;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.tonyocallimoutou.realestatemanager.data.localDatabase.DatabaseRealEstateHandler;
import com.tonyocallimoutou.realestatemanager.model.Photo;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.util.UtilNotification;

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


    public void savePicture(Context context, RealEstate realEstate, DatabaseRealEstateHandler database) {

        for (int i=0; i<realEstate.getPhotos().size(); i++) {

            Photo photo = realEstate.getPhotos().get(i);

            if ( !photo.isSync()) {

                Uri pictureUri = Uri.parse(photo.getReference());

                String id = realEstate.getUser().getEmail() + " : " + realEstate.getId();

                StorageReference ref = getFirebaseStorage().getReference(id).child(pictureUri.getLastPathSegment());

                UploadTask uploadTask = ref.putFile(pictureUri);

                int finalI = i;
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

                            Photo newPhoto = new Photo(downloadUri.toString(), photo.getDescription());
                            newPhoto.setSync(true);

                            List<Photo> list = realEstate.getPhotos();
                            list.set(finalI, newPhoto);

                            realEstate.setPhotos(list);

                            database.createRealEstate(realEstate);
                            UtilNotification.createNotification(context, realEstate);

                            if (realEstate.getProgressSync() == 100) {
                                editRealEstate(realEstate);
                            }
                        }
                    }
                });
            }
        }
    }

    public void editRealEstate(RealEstate realEstate) {
        realEstate.setSync(true);
        String id = realEstate.getUser().getEmail() + " : " + realEstate.getId();
        getRealEstateCollection().document(id).set(realEstate);
    }

    public void setListRealEstates(DatabaseRealEstateHandler database) {
        getRealEstateCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                for (DocumentSnapshot document : value) {
                    RealEstate realEstate = document.toObject(RealEstate.class);

                    database.createRealEstate(realEstate);
                }

            }
        });
    }

    public void setMyRealEstates(User user) {

        getRealEstateCollection().get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {

                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot document : list) {
                        RealEstate realEstates = document.toObject(RealEstate.class);

                        if (realEstates.getUser().getEmail().equals(user.getEmail())) {
                            realEstates.setUser(user);
                            editRealEstate(realEstates);
                        }
                    }
                }
            }
        });
    }

}
