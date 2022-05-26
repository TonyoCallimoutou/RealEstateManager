package com.tonyocallimoutou.realestatemanager;

import org.junit.Test;

import static org.junit.Assert.*;

import com.tonyocallimoutou.realestatemanager.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UtilsUnitTest {

    @Test
    public void cashConverterDollarsToEuros() {
        // random cash in dollars
        int dollars = (int) (Math.random()*100 + 1);
        int euros = Utils.convertDollarToEuro(dollars);

        int eurosExpected = (int) (Math.round(dollars * 0.812));

        assertEquals(eurosExpected, euros);
    }

    @Test
    public void cashConverterEurosToDollars() {
        // random cash in dollars
        int dollars = (int) (Math.random()*100 + 1);
        int euros = Utils.convertDollarToEuro(dollars);

        int dollarsConverted = Utils.convertEuroToDollar(euros);

        assertEquals(dollars, dollarsConverted);
    }

    @Test
    public void getStringOfTodayDate() {
        Date today = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        int years = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String monthString = "" + month;
        String dayString = "" + day;

        if (month < 10) {
            monthString = "0"+month;
        }
        if (day < 10) {
            dayString = "0" + day;
        }


        String dateStringExpected = dayString + "/" + monthString + "/" + years;

        String dateString = Utils.getTodayDate();

        assertEquals(dateStringExpected, dateString);
    }
}