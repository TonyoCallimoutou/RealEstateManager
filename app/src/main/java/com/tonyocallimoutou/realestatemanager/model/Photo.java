package com.tonyocallimoutou.realestatemanager.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class Photo implements Serializable {

    private String reference;
    @Nullable
    private String description;

    public Photo() {}

    public Photo(String reference, @Nullable String description) {
        this.reference = reference;
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
