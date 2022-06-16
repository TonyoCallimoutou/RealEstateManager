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

    public RealEstate soldRealEstate(RealEstate realEstate) {
        if (userRepository.getCurrentUser().getUid().equals(realEstate.getUser().getUid())) {
            return realEstateRepository.soldRealEstate(realEstate);
        }
        return null;
    }

    public LiveData<List<RealEstate>> getSyncRealEstatesLiveData() {
        return realEstateRepository.getSyncRealEstate();
    }

    public void setSyncRealEstates(List<RealEstate> realEstates) {
        realEstateRepository.setSyncRealEstatesList(realEstates);
    }

    public void saveAsDraft(RealEstate realEstate) {
        realEstateRepository.saveAsDraft(realEstate);
        userRepository.createRealEstate(realEstate);
    }

    public void deleteDraft(RealEstate realEstate) {
        realEstateRepository.deleteDraft(realEstate);
    }

    public LiveData<List<RealEstate>> getFilterListLiveData() {
        return realEstateRepository.getListWithFilter();
    }

    public void setMyRealEstates(User currentUser) {
        if (! userRepository.getCurrentUser().equals(currentUser)) {
            realEstateRepository.setMyRealEstates(currentUser);
        }
    }

    public void setFilterList(List<Filter> filters) {
        realEstateRepository.setFilterList(filters);
    }
}
