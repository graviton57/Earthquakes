package com.havrylyuk.earthquakes.service;

import com.havrylyuk.earthquakes.model.Countries;
import com.havrylyuk.earthquakes.model.Earthquakes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */
public interface ApiService {

    @GET("countryInfoJSON")
    Call<Countries> getCountries(
            @Query("lang") String lang,
            @Query("username") String userName,
            @Query("style") String style);

    // Ukraine
    // http://api.geonames.org/earthquakesJSON?north=52.369362&south=44.390415&east=40.20739&west=22.128889&username=graviton57&maxRows=500
    @GET("earthquakesJSON")
    Call<Earthquakes> getEarthquakes(
            @Query("north") float north,
            @Query("south") float south,
            @Query("east") float east,
            @Query("west") float west,
            @Query("maxRows") int maxRows,
            @Query("username") String userName);
}
