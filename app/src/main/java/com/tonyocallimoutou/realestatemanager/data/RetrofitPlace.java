package com.tonyocallimoutou.realestatemanager.data;

import com.tonyocallimoutou.realestatemanager.BuildConfig;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitPlace {

    @GET("nearbysearch/json?sensor=true&radius=1000&type=secondary_school|primary_school|university&key="+ BuildConfig.PLACES_API_KEY)
    Call<NearbyPlace> getNearbySchool(@Query("location") String location);

    @GET("nearbysearch/json?sensor=true&radius=1000&type=park&key="+ BuildConfig.PLACES_API_KEY)
    Call<NearbyPlace> getNearbyPark(@Query("location") String location);

    @GET("nearbysearch/json?sensor=true&radius=1000&type=supermarket&key="+ BuildConfig.PLACES_API_KEY)
    Call<NearbyPlace> getNearbyStore(@Query("location") String location);


    String baseUrl = "https://maps.googleapis.com/maps/api/place/";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
