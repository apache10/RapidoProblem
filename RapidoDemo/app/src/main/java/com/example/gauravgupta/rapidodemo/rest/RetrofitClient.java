package com.example.gauravgupta.rapidodemo.rest;

import com.example.gauravgupta.rapidodemo.constants.ProjectConstant;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gauravgupta on 06/08/17.
 */
public class RetrofitClient {
    public static final String Google_Place_Url = ProjectConstant.GetPlaceApiUrl;
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(Google_Place_Url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}