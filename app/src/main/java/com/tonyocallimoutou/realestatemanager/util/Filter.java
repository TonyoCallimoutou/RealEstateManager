package com.tonyocallimoutou.realestatemanager.util;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.model.Place;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Filter {

    public static String TYPE_LOCATION = "TYPE_LOCATION";
    public static String TYPE_MIN_PRICE = "TYPE_MIN_PRICE";
    public static String TYPE_MAX_PRICE = "TYPE_MAX_PRICE";
    public static String TYPE_TYPE = "TYPE_TYPE";
    public static String TYPE_ROOM = "TYPE_ROOM";
    public static String TYPE_CREATION = "TYPE_CREATION";
    public static String TYPE_PICTURE = "TYPE_PICTURE";
    public static String TYPE_MINE = "TYPE_MINE";
    public static String TYPE_SOLD = "TYPE_SOLD";
    private String filterType;
    private String filterCountry;
    private Place filterCity;
    private float distance;
    private String type;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer minRoom;
    private String userId;
    private int dateSoldLimit;
    private int creationDateLimit;
    private int minNbrPicture;

    public Filter(String filterType) {
        this.filterType = filterType;
    }

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

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
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
        if (filterType.equals(TYPE_MIN_PRICE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getPriceUSD() > minPrice) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType.equals(TYPE_MAX_PRICE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getPriceUSD() < maxPrice) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType.equals(TYPE_TYPE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getType().equals(type)) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType.equals(TYPE_ROOM)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getNumberOfRooms() >= minRoom) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType.equals(TYPE_CREATION)) {
            for (RealEstate realEstate : original) {
                if (Utils.getAgeOfRealEstate(realEstate) <= creationDateLimit) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType.equals(TYPE_PICTURE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getPhotos().size() >= minNbrPicture) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType.equals(TYPE_SOLD)) {
            for (RealEstate realEstate : original) {
                if (realEstate.isSold()) {
                    if (Utils.getAgeOfSold(realEstate) <= dateSoldLimit || dateSoldLimit == 0) {
                        newList.add(realEstate);
                    }
                }
            }
        }
        else if (filterType.equals(TYPE_MINE)) {
            for (RealEstate realEstate : original) {
                if (realEstate.getUserId().equals(userId)) {
                    newList.add(realEstate);
                }
            }
        }
        else if (filterType.equals(TYPE_LOCATION)) {

            for (RealEstate realEstate : original) {
                if (realEstate.getPlace().getCity().equals(filterCity.getName()) || Utils.getDistanceFromCityInKm(realEstate,filterCity) < distance) {
                    newList.add(realEstate);
                }
            }
        }

        return newList;
    }

    @Override
    public String toString() {
        if(filterType.equals(TYPE_TYPE)) {
            return "Type : " +type;
        }
        else if (filterType.equals(TYPE_MIN_PRICE)) {
            return "min : "+ Utils.getStringOfPrice(minPrice);
        }
        else if (filterType.equals(TYPE_MAX_PRICE)) {
            return "max : "+ Utils.getStringOfPrice(maxPrice);
        }
        else if (filterType.equals(TYPE_ROOM)) {
            return "more than " + minRoom + " room";
        }
        else if (filterType.equals(TYPE_MINE)) {
            return "my real estate";
        }
        else if (filterType.equals(TYPE_SOLD)) {
            if (dateSoldLimit == 0) {
                return "only sold";
            }
            else return "only sold last " + dateSoldLimit + " months";
        }
        else if (filterType.equals(TYPE_CREATION)) {
            return "only published last " + creationDateLimit + " months";
        }
        else if (filterType.equals(TYPE_PICTURE)) {
            return "more than " + minNbrPicture + " pictures";
        }
        else if (filterType.equals(TYPE_LOCATION)) {
            if (distance > 0) {
                return filterCity.getName() + " + " + distance + " km";
            }
            else {
                return filterCity.getName();
            }
        }
        return "none";
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
