package com.tonyocallimoutou.realestatemanager.viewmodel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.repository.RealEstateRepository;
import com.tonyocallimoutou.realestatemanager.repository.UserRepository;
import com.tonyocallimoutou.realestatemanager.util.Filter;

import java.util.ArrayList;
import java.util.List;

public class ViewModelRealEstate extends ViewModel {

    private final RealEstateRepository realEstateRepository;
    private final UserRepository userRepository;

    private ViewModelRealEstate(RealEstateRepository realEstateRepository, UserRepository userRepository) {
        this.realEstateRepository = realEstateRepository;
        this.userRepository = userRepository;
    }

    public static ViewModelRealEstate getInstance(RealEstateRepository realEstateRepository, UserRepository userRepository) {
        return new ViewModelRealEstate(realEstateRepository,userRepository);
    }


    public void createRealEstate(RealEstate realEstate) {
        realEstateRepository.createRealEstate(realEstate);
        userRepository.createRealEstate(realEstate);
    }

    public void editRealEstate(RealEstate actual, RealEstate modify) {
        if (userRepository.getCurrentUser().getUid().equals(actual.getUser().getUid())) {
            realEstateRepository.editRealEstate(actual, modify);
        }
    }

    public RealEstate soldRealEstate(RealEstate realEstate) {
        if (userRepository.getCurrentUser().getUid().equals(realEstate.getUser().getUid())) {
            return realEstateRepository.soldRealEstate(realEstate);
        }
        return null;
    }

    public LiveData<List<RealEstate>> getFilterListLiveData() {
        return realEstateRepository.getListWithFilter();
    }

    public void setMyRealEstates(User currentUser) {
        realEstateRepository.setMyRealEstates(currentUser);
    }

    public void setFilterList(List<Filter> filters) {
        realEstateRepository.setFilterList(filters);
    }
}
