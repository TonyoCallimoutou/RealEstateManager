package com.tonyocallimoutou.realestatemanager.viewmodel;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.repository.UserRepository;

public class ViewModelUser extends ViewModel {

    private final UserRepository userRepository;

    private ViewModelUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static ViewModelUser getInstance(UserRepository userRepository) {
        return new ViewModelUser(userRepository);
    }

    public boolean isCurrentLogged() {
        return userRepository.isCurrentLogged();
    }

    public void signOut() {
        userRepository.signOut();
    }

    public void createUser(Activity activity) {
        userRepository.createUser(activity, this);
    }

    public void setCurrentUserPicture(String picture) {
        userRepository.setCurrentUserPicture(picture);
    }

    public Task<Void> deleteUser(){
        return userRepository.deleteUser();
    }

    public void setNameOfCurrentUser(String name) {
        userRepository.setNameOfCurrentUser(name);
    }

    public void setPhoneNumberOfCurrentUser(String phoneNumber) {
        userRepository.setPhoneNumberOfCurrentUser(phoneNumber);
    }

    public LiveData<User> getCurrentUserLiveData() {
        return userRepository.getCurrentUserLiveData();
    }

    public void setCurrentUser(User user) {
        userRepository.setCurrentUser(user);
    }

}
