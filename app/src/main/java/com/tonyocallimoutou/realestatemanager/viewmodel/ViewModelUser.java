package com.tonyocallimoutou.realestatemanager.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.repository.UserRepository;

import java.util.List;

public class ViewModelUser extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();

    private final MutableLiveData<List<User>> listUserLiveData = new MutableLiveData<>();

    private ViewModelUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static ViewModelUser getInstance(UserRepository userRepository) {
        return new ViewModelUser(userRepository);
    }

    public boolean isCurrentLogged() {
        return userRepository.isCurrentLogged();
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }

    public void createUser(Activity activity) {
        userRepository.createUser(activity, this);
    }

    public void setCurrentUserPicture(String picture) {
        userRepository.setCurrentUserPicture(picture);
    }

    public Task<Void> deleteUser(Context context){
        return userRepository.deleteUser(context);
    }

    public void setNameOfCurrentUser(String name) {
        userRepository.setNameOfCurrentUser(name);
    }

    public void setPhoneNumberOfCurrentUser(String phoneNumber) {
        userRepository.setPhoneNumberOfCurrentUser(phoneNumber);
    }

    public void setCurrentUserLiveData() {
        userRepository.setCurrentUserLivedata(currentUserLiveData);
    }

    public LiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

    public User getCurrentUser() {
        return userRepository.getCurrentUser();
    }

}
