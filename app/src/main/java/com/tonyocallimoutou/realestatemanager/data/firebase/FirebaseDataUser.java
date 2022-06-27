package com.tonyocallimoutou.realestatemanager.data.firebase;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.DatabaseUserHandler;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.util.Utils;
import com.tonyocallimoutou.realestatemanager.util.UtilsProfilePictureManager;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

import java.util.List;

public class FirebaseDataUser {

    private final String COLLECTION_NAME;

    private User currentUser;

    private static volatile FirebaseDataUser instance;


    private FirebaseDataUser(Context context) {
        COLLECTION_NAME = context.getString(R.string.COLLECTION_NAME_USER);
    }

    public static FirebaseDataUser getInstance(Context context ) {
        synchronized (FirebaseDataUser.class) {
            if (instance == null) {
                instance = new FirebaseDataUser(context);
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

    public void createUser(Activity activity, ViewModelUser viewModelUser, DatabaseUserHandler database) {
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
                                if (workmate.getEmail().equals(user.getEmail())) {
                                    isAlreadyExisting = true;
                                    currentUser = workmate;
                                }
                            }
                        }
                        if (!isAlreadyExisting) {

                            String picture = Utils.convertDrawableResourcesToUri(activity.getApplicationContext(), R.drawable.ic_no_image_available).toString();

                            String username = user.getDisplayName();
                            String email = user.getEmail();

                            if (database.getCurrentUser(user.getEmail()) != null) {
                                currentUser = database.getCurrentUser(user.getEmail());
                            }
                            else {
                                currentUser = new User(username, picture, email);
                                UtilsProfilePictureManager.createAlertDialog(activity, viewModelUser, currentUser);
                            }
                            currentUser.setEmailVerify(user.isEmailVerified());

                            getUsersCollection().document(currentUser.getEmail()).set(currentUser);

                        }

                        database.createUser(currentUser);
                    }
                });
    }

    public void sendVerifyEmail(Context context, OnCompleteListener<Void> listener) {
        FirebaseUser user = getCurrentFirebaseUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            AuthUI.getInstance().signOut(context).addOnCompleteListener(listener);
                        }
                    }
                });
    }

    public void setCurrentUserPicture(String picture, DatabaseUserHandler database) {

        Uri pictureUri = Uri.parse(picture);

        StorageReference ref = getFirebaseStorage().getReference(currentUser.getEmail()).child(pictureUri.getLastPathSegment());
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
                    database.setCurrentUserPicture(currentUser.getEmail(), downloadUri.toString());
                }
            }
        });

    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }


    public Task<Void> deleteUser() {
        getUsersCollection().document(currentUser.getEmail()).delete();
        currentUser = null;
        return getCurrentFirebaseUser().delete();
    }

    public void syncCurrentUser(User user, DatabaseUserHandler database) {
        currentUser = user;

        if (user.getUrlPicture().contains("content://")) {
            setCurrentUserPicture(user.getUrlPicture(), database);
        }
        else {
            getUsersCollection().document(user.getEmail()).set(user);
        }
    }
}
