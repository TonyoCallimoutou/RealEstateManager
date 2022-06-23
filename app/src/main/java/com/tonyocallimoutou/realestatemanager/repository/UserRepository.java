package com.tonyocallimoutou.realestatemanager.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.Task;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.data.firebase.FirebaseDataUser;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.DatabaseUserHandler;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

public class UserRepository {

    private static volatile UserRepository instance;

    private final FirebaseDataUser firebaseDataUser;
    private final Context context;
    private final DatabaseUserHandler database;
    private final SharedPreferences sharedPreferences;

    private static boolean isConnected;

    private final MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();

    private User currentUser;

    private final String currentUserId;


    private UserRepository(Context context) {
        firebaseDataUser = FirebaseDataUser.getInstance(context);
        this.context = context;
        this.database = new DatabaseUserHandler(context);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        currentUserId = sharedPreferences.getString(context.getString(R.string.shared_preference_user_uid), "");

    }

    public static UserRepository getInstance(Context context) {
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository(context);
            }
            return instance;
        }
    }

    public boolean isCurrentLogged() {
        if (isConnected) {
            return firebaseDataUser.isCurrentLogged();
        } else {
            // WARNING Auth without connection
            return !currentUserId.isEmpty();
        }
    }

    public void createUser(Activity activity, ViewModelUser viewModelUser) {
        if (isConnected) {
            firebaseDataUser.createUser(activity, viewModelUser, database);
        }
    }

    public void setCurrentUserPicture(String picture) {

        if (isConnected) {
            firebaseDataUser.setCurrentUserPicture(picture, database);
        }
        else {
            database.setCurrentUserPicture(currentUserId, picture);
        }
    }

    public void signOut() {
        sharedPreferences
                .edit()
                .putString(context.getString(R.string.shared_preference_user_uid), "")
                .apply();
        firebaseDataUser.signOut(context);
    }


    public Task<Void> deleteUser() {
        sharedPreferences
                .edit()
                .putString(context.getString(R.string.shared_preference_user_uid), "")
                .apply();

        database.deleteUser(currentUserId);

        return firebaseDataUser.deleteUser(context);
    }

    public void setNameOfCurrentUser(String name) {
        if (isConnected) {
            firebaseDataUser.setNameOfCurrentUser(name);
        }
        else {
            database.setNameOfCurrentUser(currentUserId, name);
            initLiveDataUser();
        }
    }

    public void setPhoneNumberOfCurrentUser(String phoneNumber) {
        if (isConnected) {
            firebaseDataUser.setPhoneNumberOfCurrentUser(phoneNumber);
        }
        else {
            database.setPhoneNumberOfCurrentUser(currentUserId, phoneNumber);
            initLiveDataUser();
        }
    }

    public LiveData<User> getCurrentUserLiveData() {
        if (isConnected) {
            firebaseDataUser.setCurrentUserLivedata(currentUserLiveData);
        }
        else {
            initLiveDataUser();
        }
        return currentUserLiveData;
    }

    private void initLiveDataUser() {
        currentUserLiveData.postValue(database.getCurrentUser(currentUserId));
    }

    // Real Estate

    public void createRealEstate(RealEstate realEstate) {

        if (isConnected) {
            firebaseDataUser.createRealEstate(realEstate);
        }
        else {
            currentUser.addRealEstateToMyList(realEstate);
            database.createUser(currentUser);
            initLiveDataUser();
        }


    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        if (! user.equals(currentUser)) {
            currentUser = user;
            database.createUser(user);

            if (isConnected) {
                instance.firebaseDataUser.syncCurrentUser(instance.currentUser, instance.database);
            }

        }
    }


    public static void ConnectionChanged(boolean result) {
        if (result && instance!= null && !isConnected) {
            if (instance.currentUser != null) {
                instance.firebaseDataUser.syncCurrentUser(instance.currentUser, instance.database);
            }
        }
        isConnected = result;
    }
}