package com.tonyocallimoutou.realestatemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.repository.RealEstateRepository;
import com.tonyocallimoutou.realestatemanager.repository.UserRepository;

import java.util.List;

public class ViewModelRealEstate extends ViewModel {

    private final RealEstateRepository realEstateRepository;
    private final UserRepository userRepository;

    private final MutableLiveData<List<RealEstate>> listRealEstateLiveData = new MutableLiveData<>();

    public ViewModelRealEstate(RealEstateRepository realEstateRepository, UserRepository userRepository) {
        this.realEstateRepository = realEstateRepository;
        this.userRepository = userRepository;
    }

    public void createRealEstate(RealEstate realEstate) {
        realEstateRepository.createRealEstate(realEstate);
        userRepository.createRealEstate(realEstate);
    }

    public void setListRealEstate() {
        realEstateRepository.getAllRealEstates(listRealEstateLiveData);
    }

    public void editRealEstate(RealEstate actual, RealEstate modify) {
        if (userRepository.getCurrentUser().getUid().equals(actual.getUserId())) {
            realEstateRepository.editRealEstate(actual, modify);
        }
    }

    public LiveData<List<RealEstate>> getAllRealEstateLiveData() {
        return listRealEstateLiveData;
    }

    public RealEstate soldRealEstate(RealEstate realEstate) {
        if (userRepository.getCurrentUser().getUid().equals(realEstate.getUserId())) {
            return realEstateRepository.soldRealEstate(realEstate);
        }
        return null;
    }
}
