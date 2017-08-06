package com.example.gauravgupta.rapidodemo.rest;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by gauravgupta on 06/08/17.
 */
public interface RetrofitInterface {
    @GET("json")
    Call<JsonObject> getPlaceInfo(@Query("location") String location, @Query("key") String apiKey);

    @GET("json")
    Call<JsonObject> getDirectionInfo(@Query("location") String location, @Query("key") String apiKey);
}
