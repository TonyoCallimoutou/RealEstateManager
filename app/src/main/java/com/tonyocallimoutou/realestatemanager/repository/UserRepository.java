package com.tonyocallimoutou.realestatemanager.repository;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
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
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.User;

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

    public CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    private FirebaseUser getCurrentFirebaseUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public boolean isCurrentLogged() {
        return this.getCurrentFirebaseUser() != null;
    }

    public void createUser() {
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
                                }
                            }
                        }
                        if ( ! isAlreadyExisting) {

                            String urlPicture = (user.getPhotoUrl() != null) ?
                                    user.getPhotoUrl().toString()
                                    : null;
                            String username = user.getDisplayName();
                            String uid = user.getUid();
                            String email = user.getEmail();

                            currentUser = new User(uid, username, urlPicture,email);
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

    public void setCurrentUserLivedata(MutableLiveData<User> liveData) {
        getUsersCollection().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                for (DocumentSnapshot document : value) {
                    User user = document.toObject(User.class);
                    if (getCurrentFirebaseUser()!=null) {
                        if (user.getUid().equals(getCurrentFirebaseUser().getUid())) {
                            liveData.setValue(user);
                        }
                    }

                }
            }
        });
    }

}
