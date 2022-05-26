package com.tonyocallimoutou.realestatemanager.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.repository.UserRepository;

public class ViewModelUser extends ViewModel {

    private final UserRepository userRepository;

    private final MutableLiveData<User> currentUserLiveData = new MutableLiveData<>();

    public ViewModelUser(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isCurrentLogged() {
        return userRepository.isCurrentLogged();
    }

    public Task<Void> signOut(Context context) {
        return userRepository.signOut(context);
    }

    public void createUser() {
        userRepository.createUser();
    }

    public Task<Void> deleteUser(Context context){
        return userRepository.deleteUser(context);
    }

    public void setNameOfCurrentUser(String name) {
        userRepository.setNameOfCurrentUser(name);
    }

    public void setCurrentUserLiveData() {
        userRepository.setCurrentUserLivedata(currentUserLiveData);
    }

    public LiveData<User> getCurrentUserLiveData() {
        return currentUserLiveData;
    }

}
