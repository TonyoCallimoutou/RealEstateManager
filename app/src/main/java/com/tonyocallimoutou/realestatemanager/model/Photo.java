package com.tonyocallimoutou.realestatemanager.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Photo implements Serializable {

    @PrimaryKey
    @NonNull
    private String reference;
    @Nullable
    private String description;

    @Ignore
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
