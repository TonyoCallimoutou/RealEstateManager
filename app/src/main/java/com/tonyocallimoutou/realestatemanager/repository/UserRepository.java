package com.tonyocallimoutou.realestatemanager.repository;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
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

    private final MediatorLiveData<User> mediator = new MediatorLiveData<>();

    private User currentUser;


    private UserRepository(Context context) {
        firebaseDataUser = FirebaseDataUser.getInstance(context);
        this.context = context;
        this.database = new DatabaseUserHandler(context);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public static UserRepository getInstance(Context context) {
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository(context);
            }
            return instance;
        }
    }

    private String getCurrentUserEmail () {
        return sharedPreferences.getString(context.getString(R.string.shared_preference_user_email), "");
    }

    public boolean isCurrentLogged() {
        if (isConnected) {
            return firebaseDataUser.isCurrentLogged();
        } else {
            return !getCurrentUserEmail().isEmpty();
        }
    }

    public void createUser(Activity activity, ViewModelUser viewModelUser) {
        if (getCurrentUserEmail().isEmpty()) {
            firebaseDataUser.createUser(activity, viewModelUser, database);
        }
        else {
            if (currentUser == null) {
                database.initLiveData(getCurrentUserEmail());
            }
        }
    }

    public void setCurrentUserPicture(String picture) {
        database.setCurrentUserPicture(getCurrentUserEmail(), picture);
    }

    public void signOut() {
        sharedPreferences
                .edit()
                .putString(context.getString(R.string.shared_preference_user_email), "")
                .apply();
        firebaseDataUser.signOut(context);
    }


    public Task<Void> deleteUser() {
        database.deleteUser(getCurrentUserEmail());

        sharedPreferences
                .edit()
                .putString(context.getString(R.string.shared_preference_user_email), "")
                .apply();


        return firebaseDataUser.deleteUser();

    }

    public void setNameOfCurrentUser(String name) {
        database.setNameOfCurrentUser(getCurrentUserEmail(), name);
    }

    public void setPhoneNumberOfCurrentUser(String phoneNumber) {
        database.setPhoneNumberOfCurrentUser(getCurrentUserEmail(), phoneNumber);
    }

    public LiveData<User> getCurrentUserLiveData() {
        mediator.removeSource(database.getCurrentUserLiveData());
        mediator.addSource(database.getCurrentUserLiveData(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (isConnected) {
                    instance.firebaseDataUser.syncCurrentUser(user, database );
                }

                currentUser = user;
                mediator.postValue(user);
            }
        });

        return mediator;
    }

    // Real Estate

    public void createRealEstate(RealEstate realEstate) {
        currentUser.addRealEstateToMyList(realEstate);
        database.createUser(currentUser);
    }

    public User getCurrentUser() {
        return currentUser;
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