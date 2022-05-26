package com.tonyocallimoutou.realestatemanager.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tonyocallimoutou.realestatemanager.repository.UserRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;
    private static ViewModelFactory factory;

    public static ViewModelFactory getInstance(Context context) {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory(context);
                }
            }
        }
        return factory;
    }

    private ViewModelFactory(Context context) {
        userRepository = UserRepository.getInstance(context);
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ViewModelUser.class)) {
            return (T) new ViewModelUser(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
