package com.tonyocallimoutou.realestatemanager.data.localDatabase;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tonyocallimoutou.realestatemanager.model.Photo;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PhotoListConverter {
    public static List<Photo> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Photo>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }
    public static String fromList(List<Photo> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
