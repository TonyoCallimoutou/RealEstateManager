package com.tonyocallimoutou.realestatemanager.data.localDatabase;

import java.util.Date;

public class DateConverter {

    public static Date fromString(Long value) {
        return value == null ? null: new Date(value);
    }

    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }
}
