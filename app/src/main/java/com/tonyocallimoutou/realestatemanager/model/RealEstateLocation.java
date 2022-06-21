package com.tonyocallimoutou.realestatemanager.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import androidx.annotation.NonNull;

import com.google.android.libraries.places.api.model.Place;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;


public class RealEstateLocation implements Serializable {


    @NonNull
    private String placeId;
    private String name;
    private double lat;
    private double lng;
    private String address;
    private String country;
    private String city;


    public RealEstateLocation() {
    }

    public RealEstateLocation(Context context, Place place){
        this.placeId = place.getId();
        this.name = place.getName();
        this.lat = place.getLatLng().latitude;
        this.lng = place.getLatLng().longitude;
        this.address = place.getAddress();

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null ;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = addresses.get(0);

        this.country = address.getCountryName();
        this.city = address.getLocality();
    }

    public RealEstateLocation(String placeId, String name, double lat, double lng, String address) {
        this.placeId = placeId;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
