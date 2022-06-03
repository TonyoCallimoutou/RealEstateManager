package com.tonyocallimoutou.realestatemanager.util;

import android.util.Log;

import androidx.annotation.Nullable;

import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Filter {

    public static String TYPE_MIN_PRICE = "TYPE_MIN_PRICE";
    public static String TYPE_MAX_PRICE = "TYPE_MAX_PRICE";

    private String type;
    private Integer minPrice;
    private Integer maxPrice;
    private boolean onlySold;
    private String dateSold;
    private String creationDate;
    private int nbrPicture;

    public Filter(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer lessPrice) {
        this.minPrice = lessPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public boolean isOnlySold() {
        return onlySold;
    }

    public String getDateSold() {
        return dateSold;
    }

    public void setOnlySold(boolean onlySold, String dateSold) {
        this.onlySold = onlySold;
        this.dateSold = dateSold;
    }


    @Nullable
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(@Nullable String creationDate) {
        this.creationDate = creationDate;
    }

    public int getNbrPicture() {
        return nbrPicture;
    }

    public void setNbrPicture(int nbrPicture) {
        this.nbrPicture = nbrPicture;
    }

    public List<RealEstate> modifyList(List<RealEstate> original) {
        List<RealEstate> newList = new ArrayList<>();
        if (type.equals(TYPE_MIN_PRICE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getPriceUSD() > minPrice) {
                    newList.add(realEstate);
                }
            }
        }
        else if (type.equals(TYPE_MAX_PRICE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getPriceUSD() < maxPrice) {
                    newList.add(realEstate);
                }
            }
        }

        return newList;
    }

    @Override
    public String toString() {
        return type + "X";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return Objects.equals(type, filter.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
