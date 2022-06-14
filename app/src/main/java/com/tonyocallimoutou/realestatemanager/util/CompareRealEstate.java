package com.tonyocallimoutou.realestatemanager.util;

import android.util.Log;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.List;

public class CompareRealEstate {

    public static boolean compareListForMapIsEqual(List<RealEstate> expected, List<RealEstate> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }

        for (int i=0; i< expected.size();  i++) {
            if (expected.get(i).getPlace() != null && actual.get(i).getPlace() != null) {
                if (!(expected.get(i).getPlace().getPlaceId().equals(actual.get(i).getPlace().getPlaceId()))) {
                    return false;
                }
            }
        }

        return true;
    }
}
