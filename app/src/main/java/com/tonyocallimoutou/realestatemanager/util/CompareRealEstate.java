package com.tonyocallimoutou.realestatemanager.util;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.List;

public class CompareRealEstate {

    public static boolean compareListForMapIsEqual(List<RealEstate> expected, List<RealEstate> actual) {
        if (expected.size() != actual.size()) {
            return false;
        }

        for (int i=0; i< expected.size();  i++) {
            if (!(expected.get(i).getPlace() == actual.get(i).getPlace())) {
                return false;
            }
        }

        return true;
    }
}
