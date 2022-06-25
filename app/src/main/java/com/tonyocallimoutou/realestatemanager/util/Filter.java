package com.tonyocallimoutou.realestatemanager.util;

import android.content.Context;

import com.google.android.libraries.places.api.model.Place;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.data.localDatabase.DatabaseRealEstateHandler;

import java.util.Date;
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
    public static int TYPE_NEXT_SCHOOL = 12;
    public static int TYPE_NEXT_PARK = 13;
    public static int TYPE_NEXT_STORE = 14;
    private final int filterType;
    private Place filterCity;
    private float distance;
    private int typeId;
    private Integer minPrice;
    private Integer maxPrice;
    private String moneyKey;
    private final Context context;
    private Integer minRoom;
    private String userEmail;
    private int dateSoldLimit;
    private int creationDateLimit;
    private int minNbrPicture;

    // Constructor
    public Filter(Context context, int filterType) {
        this.context = context;
        this.filterType = filterType;
    }

    // GETTER

    public int getFilterType() {
        return filterType;
    }

    // SETTER

    public void setFilterCity(Place filterCity) {
        this.filterCity = filterCity;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public void setMinPrice(Integer lessPrice) {
        this.minPrice = lessPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setMoneyKey(String moneyKey) {
        this.moneyKey = moneyKey;
    }

    public void setMinRoom(Integer minRoom) {
        this.minRoom = minRoom;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setDateSoldLimit(int dateSoldLimit) {
        this.dateSoldLimit = dateSoldLimit;
    }

    public void setCreationDateLimit(int creationDateLimit) {
        this.creationDateLimit = creationDateLimit;
    }

    public void setMinNbrPicture(int minNbrPicture) {
        this.minNbrPicture = minNbrPicture;
    }


    public String getQueryStr() {

        String str = "";

        if (filterType == (TYPE_MIN_PRICE)) {
            str = DatabaseRealEstateHandler.PRICE_USD_COL + " > " + Utils.getPriceInUSD(context,minPrice,moneyKey);
        }
        else if (filterType == (TYPE_MAX_PRICE)) {
            str = DatabaseRealEstateHandler.PRICE_USD_COL + " < " + Utils.getPriceInUSD(context,maxPrice,moneyKey);
        }
        else if (filterType == (TYPE_TYPE)) {
            str = DatabaseRealEstateHandler.TYPE_ID_COL + " = " + typeId;
        }
        else if (filterType == (TYPE_ROOM)) {
            str = DatabaseRealEstateHandler.NUMBER_OF_ROOM_COL + " >= " + minRoom;
        }
        else if (filterType == (TYPE_MINE)) {
            str = DatabaseRealEstateHandler.USER_EMAIL_COL + " = \"" + userEmail + "\"";
        }
        else if (filterType == (TYPE_DRAFT)) {
            str = DatabaseRealEstateHandler.IS_DRAFT_COL + " = 0";
        }
        else if (filterType == (TYPE_NOT_SYNC)) {
            str = DatabaseRealEstateHandler.IS_SYNC_COL + " = 1";
        }
        else if (filterType == (TYPE_PICTURE)) {
            str = DatabaseRealEstateHandler.NUMBER_PHOTOS_COL + " >= " + minNbrPicture;
        }
        else if (filterType == (TYPE_CREATION)) {
            Date today = new Date();
            long limitDate = today.getTime() - ((long) creationDateLimit *31*24*3600*1000);

            str = DatabaseRealEstateHandler.CREATION_COL + " < " + limitDate;
        }

        else if (filterType == (TYPE_SOLD)) {
            Date today = new Date();
            long limitDate = today.getTime() - ((long) dateSoldLimit *31*24*3600*1000);

            str = DatabaseRealEstateHandler.IS_SOLD_COL + " = 1 AND " + DatabaseRealEstateHandler.SOLD_DATE_COL + " < " + limitDate;

        }
        else if (filterType == (TYPE_LOCATION)) {

            str = DatabaseRealEstateHandler.conditionDistanceQuery(distance,filterCity);
        }
        else if (filterType == (TYPE_NEXT_SCHOOL)) {
            str = DatabaseRealEstateHandler.PLACE_IS_NEXT_TO_SCHOOL_COL + " = 1";
        }
        else if (filterType == (TYPE_NEXT_PARK)) {
            str = DatabaseRealEstateHandler.PLACE_IS_NEXT_TO_PARK_COL + " = 1";
        }
        else if (filterType == (TYPE_NEXT_STORE)) {
            str = DatabaseRealEstateHandler.PLACE_IS_NEXT_TO_STORE_COL + " = 1";
        }


        return str;
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
            return context.getString(R.string.filter_to_string_more_than) + " " + minRoom +" " + context.getString(R.string.filter_to_string_room) ;
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
            return context.getString(R.string.filter_to_string_more_than) +" " + minNbrPicture + " " + context.getString(R.string.filter_to_string_pictures) ;
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
        else if (filterType == (TYPE_NEXT_SCHOOL)) {
            return context.getString(R.string.filter_is_next_school);
        }
        else if (filterType == (TYPE_NEXT_PARK)) {
            return context.getString(R.string.filter_is_next_park);
        }
        else if (filterType == (TYPE_NEXT_STORE)) {
            return context.getString(R.string.filter_is_next_store);
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
