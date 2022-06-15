package com.tonyocallimoutou.realestatemanager.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.model.Place;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Filter {

    public static int TYPE_LOCATION = 1;
    public static int TYPE_MIN_PRICE = 2;
    public static int TYPE_MAX_PRICE = 3;
    public static int TYPE_TYPE = 4;
    public static int TYPE_ROOM = 5;
    public static int TYPE_CREATION = 6;
    public static int TYPE_PICTURE = 7;
    public static int TYPE_MINE = 8;
    public static int TYPE_SOLD = 9;
    public static int TYPE_DRAFT = 10;
    public static int TYPE_NOT_SYNC = 11;
    private int filterType;
    private String filterCountry;
    private Place filterCity;
    private float distance;
    private int typeId;
    private Integer minPrice;
    private Integer maxPrice;
    private String moneyKey;
    private final Context context;
    private Integer minRoom;
    private String userId;
    private int dateSoldLimit;
    private int creationDateLimit;
    private int minNbrPicture;

    // Constructor
    public Filter(Context context, int filterType) {
        this.context = context;
        this.filterType = filterType;
    }

    // GETTER & SETTER
    public String getFilterCountry() {
        return filterCountry;
    }

    public void setFilterCountry(String filterCountry) {
        this.filterCountry = filterCountry;
    }

    public Place getFilterCity() {
        return filterCity;
    }

    public void setFilterCity(Place filterCity) {
        this.filterCity = filterCity;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getFilterType() {
        return filterType;
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
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

    public String getMoneyKey() {
        return moneyKey;
    }

    public void setMoneyKey(String moneyKey) {
        this.moneyKey = moneyKey;
    }

    public Integer getMinRoom() {
        return minRoom;
    }

    public void setMinRoom(Integer minRoom) {
        this.minRoom = minRoom;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getDateSoldLimit() {
        return dateSoldLimit;
    }

    public void setDateSoldLimit(int dateSoldLimit) {
        this.dateSoldLimit = dateSoldLimit;
    }

    public int getCreationDateLimit() {
        return creationDateLimit;
    }

    public void setCreationDateLimit(int creationDateLimit) {
        this.creationDateLimit = creationDateLimit;
    }

    public int getMinNbrPicture() {
        return minNbrPicture;
    }

    public void setMinNbrPicture(int minNbrPicture) {
        this.minNbrPicture = minNbrPicture;
    }

    public List<RealEstate> modifyList(List<RealEstate> original) {

        List<RealEstate> newList = new ArrayList<>();
        if (filterType == (TYPE_MIN_PRICE)) {
            int minPriceUSD = Utils.getPriceInUSD(context,minPrice,moneyKey);
            for (RealEstate realEstate : original) {
                if (realEstate.getPriceUSD() > minPriceUSD) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType == (TYPE_MAX_PRICE)) {
            int maxPriceUSD = Utils.getPriceInUSD(context,maxPrice,moneyKey);
            for (RealEstate realEstate : original) {
                if (realEstate.getPriceUSD() < maxPriceUSD) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType == (TYPE_TYPE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getTypeId() == typeId) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType == (TYPE_ROOM)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getNumberOfRooms() >= minRoom) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType == (TYPE_CREATION)) {
            for (RealEstate realEstate : original) {
                if (Utils.getAgeOfRealEstate(realEstate) <= creationDateLimit) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType == (TYPE_PICTURE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getPhotos().size() >= minNbrPicture) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType == (TYPE_SOLD)) {
            for (RealEstate realEstate : original) {
                if (realEstate.isSold()) {
                    if (Utils.getAgeOfSold(realEstate) <= dateSoldLimit || dateSoldLimit == 0) {
                        newList.add(realEstate);
                    }
                }
            }
        }
        else if (filterType == (TYPE_MINE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getUser().getUid().equals(userId)) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType == (TYPE_LOCATION)) {

            for (RealEstate realEstate : original) {
                if (realEstate.getPlace().getCity().equals(filterCity.getName()) || Utils.getDistanceFromCityInKm(realEstate,filterCity) < distance) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType == (TYPE_DRAFT)) {
            for (RealEstate realEstate : original) {
                if (! realEstate.isDraft()) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType == (TYPE_NOT_SYNC)) {
            for (RealEstate realEstate : original) {
                if (realEstate.isSync() || realEstate.isDraft()) {
                    newList.add(realEstate);
                }
            }
        }

        return newList;
    }

    @Override
    public String toString() {
        if(filterType == (TYPE_TYPE)) {
            String[] str = context.getResources().getStringArray(R.array.SpinnerTypeOfResidence);
            return context.getString(R.string.filter_to_string_type) + " " +str[typeId];
        }
        else if (filterType == (TYPE_MIN_PRICE)) {
            return context.getString(R.string.filter_to_string_min) + " "+ Utils.getStringOfPrice(minPrice) + " " + moneyKey;
        }
        else if (filterType == (TYPE_MAX_PRICE)) {
            return context.getString(R.string.filter_to_string_max) + " "+ Utils.getStringOfPrice(maxPrice)+ " " + moneyKey;
        }
        else if (filterType == (TYPE_ROOM)) {
            return context.getString(R.string.filter_to_string_more_than) + " " + minRoom + context.getString(R.string.filter_to_string_room) ;
        }
        else if (filterType == (TYPE_MINE)) {
            return context.getString(R.string.filter_to_string_my_real_estates) ;
        }
        else if (filterType == (TYPE_SOLD)) {
            if (dateSoldLimit == 0) {
                return context.getString(R.string.filter_to_string_only_sold);
            }
            else return context.getString(R.string.filter_to_string_only_sold_last)+" " + dateSoldLimit + " " +context.getString(R.string.filter_to_string_months);
        }
        else if (filterType == (TYPE_CREATION)) {
            return context.getString(R.string.filter_to_string_only_published_last)+" " + creationDateLimit + " " +context.getString(R.string.filter_to_string_months);
        }
        else if (filterType == (TYPE_PICTURE)) {
            return context.getString(R.string.filter_to_string_more_than) +" " + minNbrPicture + context.getString(R.string.filter_to_string_pictures) ;
        }
        else if (filterType == (TYPE_LOCATION)) {
            if (distance > 0) {
                return filterCity.getName() + " + " + distance + " km";
            }
            else {
                return filterCity.getName();
            }
        }
        else if (filterType == (TYPE_DRAFT)) {
            return context.getString(R.string.filter_to_string_without_draft) ;
        }
        else if (filterType == (TYPE_NOT_SYNC)) {
            return context.getString(R.string.filter_to_string_without_not_sync) ;
        }
        return "error";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return Objects.equals(filterType, filter.filterType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filterType);
    }
}
