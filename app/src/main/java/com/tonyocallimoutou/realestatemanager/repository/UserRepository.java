package com.tonyocallimoutou.realestatemanager.repository;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.util.Utils;
import com.tonyocallimoutou.realestatemanager.util.UtilsProfilePictureManager;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final String COLLECTION_NAME;

    private User currentUser;

    private static volatile UserRepository instance;


    private UserRepository(Context context) {
        COLLECTION_NAME = context.getString(R.string.COLLECTION_NAME_USER);
    }

    public static UserRepository getInstance(Context context ) {
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository(context);
            }
            return instance;
        }
    }

    // My Firestore Collection

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Storage

    private FirebaseStorage getFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    private FirebaseUser getCurrentFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean isCurrentLogged() {
        return this.getCurrentFirebaseUser() != null;
    }

    public void createUser(Activity activity, ViewModelUser viewModelUser) {
        FirebaseUser user = getCurrentFirebaseUser();

        getUsersCollection().get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        boolean isAlreadyExisting = false;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot document : list) {
                                User workmate = document.toObject(User.class);
                                if (workmate.getUid().equals(user.getUid())) {
                                    isAlreadyExisting = true;
                                    currentUser = workmate;
                                }
                            }
                        }
                        if ( ! isAlreadyExisting) {

                            String picture = Utils.convertDrawableResourcesToUri(activity.getApplicationContext(), R.drawable.ic_no_image_available).toString();

                            String username = user.getDisplayName();
                            String uid = user.getUid();
                            String email = user.getEmail();

                            currentUser = new User(uid, username, picture,email);
                            getUsersCollection().document(currentUser.getUid()).set(currentUser);

                            UtilsProfilePictureManager.createAlertDialog(activity, viewModelUser);
                        }
                    }
                });
    }

    public void setCurrentUserPicture(String picture) {

        Uri pictureUri = Uri.parse(picture);

        StorageReference ref = getFirebaseStorage().getReference(currentUser.getUid()).child(pictureUri.getLastPathSegment());
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
                    currentUser.setUrlPicture(downloadUri.toString());
                    getUsersCollection().document(currentUser.getUid()).set(currentUser);
                }
            }
        });

    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }


    public Task<Void> deleteUser(Context context) {
        getUsersCollection().document(currentUser.getUid()).delete();
        currentUser = null;
        return signOut(context);
    }

    public void setNameOfCurrentUser(String name) {
        currentUser.setUsername(name);
        getUsersCollection().document(currentUser.getUid()).set(currentUser);
    }

    public void setPhoneNumberOfCurrentUser(String phoneNumber) {
        currentUser.setPhoneNumber(phoneNumber);
        getUsersCollection().document(currentUser.getUid()).set(currentUser);
    }

    public void setCurrentUserLivedata(MutableLiveData<User> liveData) {
        getUsersCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                for (DocumentSnapshot document : value) {
                    User user = document.   toObject(User.class);
                    if (getCurrentFirebaseUser()!=null) {
                        if (user.getUid().equals(getCurrentFirebaseUser().getUid())) {
                            currentUser = user;
                            liveData.setValue(user);
                        }
                    }

                }
            }
        });
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void getAllUser(MutableLiveData<List<User>> liveData) {

        getUsersCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                List<User> workmatesList = new ArrayList<>();
                for (DocumentSnapshot document : value) {
                    User user = document.toObject(User.class);
                    workmatesList.add(user);
                }

                liveData.setValue(workmatesList);
            }
        });
    }


    // Real Estate

    public void createRealEstate(RealEstate realEstate) {
        currentUser.addRealEstateToMyList(realEstate);
        getUsersCollection().document(currentUser.getUid()).set(currentUser);
    }

}
