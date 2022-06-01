package com.tonyocallimoutou.realestatemanager.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

public class RealEstateLocation {

    private String placeId;
    private String name;
    private double lat;
    private double lng;
    private String address;

    public RealEstateLocation() {
    }

    public RealEstateLocation(Place place) {
        this.placeId = place.getId();
        this.name = place.getName();
        this.lat = place.getLatLng().latitude;
        this.lng = place.getLatLng().longitude;
        this.address = place.getAddress();
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
}