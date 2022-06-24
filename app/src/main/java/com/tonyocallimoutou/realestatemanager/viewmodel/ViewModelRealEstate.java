package com.tonyocallimoutou.realestatemanager.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.model.RealEstateLocation;
import com.tonyocallimoutou.realestatemanager.model.User;
import com.tonyocallimoutou.realestatemanager.repository.RealEstateRepository;
import com.tonyocallimoutou.realestatemanager.repository.UserRepository;
import com.tonyocallimoutou.realestatemanager.util.Filter;

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
        if (! currentUser.equals(realEstateRepository.getUserOfRealEstate(currentUser))) {
            Log.d("TAG", "setMyRealEstates: " + currentUser.getUsername());
            Log.d("TAG", "setMyRealEstates: " + realEstateRepository.getUserOfRealEstate(currentUser).getUsername());
            realEstateRepository.setMyRealEstates(currentUser);
        }
    }

    public void verifyNearbyPlace(RealEstateLocation place) {
        realEstateRepository.verifyNearbyPlace(place);
    }

    public void setFilterList(List<Filter> filters) {
        realEstateRepository.setFilterList(filters);
    }

    public void syncFirebase() {
        realEstateRepository.firstConnection();
    }
}
