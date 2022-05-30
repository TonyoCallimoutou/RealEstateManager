package com.tonyocallimoutou.realestatemanager.model;

public class Photo {

    private String reference;
    private String description;

    public Photo(String reference, String description) {
        this.reference = reference;
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
