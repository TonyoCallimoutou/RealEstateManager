package com.tonyocallimoutou.realestatemanager.viewmodel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.tonyocallimoutou.realestatemanager.data.room.LocalDatabase;
import com.tonyocallimoutou.realestatemanager.repository.RealEstateRepository;
import com.tonyocallimoutou.realestatemanager.repository.UserRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final UserRepository userRepository;
    private final RealEstateRepository realEstateRepository;
    private final Executor executor;

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
        LocalDatabase database = LocalDatabase.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();

        userRepository = UserRepository.getInstance(context, database.userDao(), executor);
        realEstateRepository = RealEstateRepository.getInstance(context, database.realEstateDao(), executor);
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ViewModelUser.class)) {
            return (T) ViewModelUser.getInstance(userRepository);
        }
        if (modelClass.isAssignableFrom(ViewModelRealEstate.class)) {
            return (T) ViewModelRealEstate.getInstance(realEstateRepository,userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
