package com.tonyocallimoutou.realestatemanager.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Photo implements Serializable {

    @NonNull
    private String reference;
    @Nullable
    private String description;
    private boolean isSync;


    public Photo() {}

    public Photo(String reference, @Nullable String description) {
        this.reference = reference;
        this.description = description;
        this.isSync = false;
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

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return reference.equals(photo.reference) && Objects.equals(description, photo.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, description);
    }
}
