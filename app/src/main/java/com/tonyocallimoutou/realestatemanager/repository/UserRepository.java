package com.tonyocallimoutou.realestatemanager.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

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
import com.tonyocallimoutou.realestatemanager.data.firebase.FirebaseDataUser;
import com.tonyocallimoutou.realestatemanager.data.room.UserDao;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.util.Utils;
import com.tonyocallimoutou.realestatemanager.util.UtilsProfilePictureManager;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class UserRepository {

    private static volatile UserRepository instance;

    private final FirebaseDataUser firebaseDataUser;
    private final UserDao userDao;
    private final Executor executor;
    private final Context context;
    private final SharedPreferences sharedPreferences;

    private final MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();

    private String currentUserId;


    private UserRepository(Context context, UserDao userDao, Executor executor) {
        firebaseDataUser = FirebaseDataUser.getInstance(context);
        this.context = context;
        this.userDao = userDao;
        this.executor = executor;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentUserId = sharedPreferences.getString(context.getString(R.string.shared_preference_user_uid), "");

    }

    public static UserRepository getInstance(Context context, UserDao userDao, Executor executor) {
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository(context, userDao, executor);
            }
            return instance;
        }
    }

    public boolean isCurrentLogged() {
        if (Utils.isInternetAvailable(context)) {
            return firebaseDataUser.isCurrentLogged();
        }
        else {
            return !currentUserId.isEmpty();
        }
    }

    public void createUser(Activity activity, ViewModelUser viewModelUser) {
        firebaseDataUser.createUser(activity,viewModelUser, userDao, executor);
    }

    public void setCurrentUserPicture(String picture) {
        firebaseDataUser.setCurrentUserPicture(picture);

    }

    public void signOut() {
        firebaseDataUser.signOut(context);
        sharedPreferences
                .edit()
                .putString(context.getString(R.string.shared_preference_user_uid),"")
                .apply();
    }


    public Task<Void> deleteUser() {
        sharedPreferences
                .edit()
                .putString(context.getString(R.string.shared_preference_user_uid),"")
                .apply();

        executor.execute(() -> {
            userDao.deleteUser(currentUserId);
        });

        return firebaseDataUser.deleteUser(context);
    }

    public void setNameOfCurrentUser(String name) {
        if (Utils.isInternetAvailable(context)) {
            firebaseDataUser.setNameOfCurrentUser(name);
        }
        else {
            !!
            executor.execute(()-> {
                userDao.setNameOfCurrentUser(currentUserId,name);
            });
        }
    }

    public void setPhoneNumberOfCurrentUser(String phoneNumber) {
        firebaseDataUser.setPhoneNumberOfCurrentUser(phoneNumber);
    }

    public LiveData<User> getCurrentUserLiveData() {

        if (Utils.isInternetAvailable(context)) {

            firebaseDataUser.setCurrentUserLivedata(currentUserLiveData);

            return currentUserLiveData;
        }

        else {
            return userDao.getCurrentUserLiveData(currentUserId);
        }

    }

    // Real Estate

    public void createRealEstate(RealEstate realEstate) {
        firebaseDataUser.createRealEstate(realEstate);
    }

    public User getCurrentUser() {
        return firebaseDataUser.getCurrentUser();
    }

}
