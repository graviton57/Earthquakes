package com.havrylyuk.earthquakes.service;

import com.havrylyuk.earthquakes.model.Earthquakes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */
public interface ApiService {

    // Ukraine date : date of earthquakes 'yyyy-MM-dd'
    // http://api.geonames.org/earthquakesJSON?north=52.369362&south=44.390415&east=40.20739&west=22.128889&username=graviton57&maxRows=500
    // http://api.geonames.org/earthquakesJSON?north=52.369362&south=44.390415&east=40.20739&west=22.128889&date=2017-03-23&date=2017-02-23&username=graviton57&maxRows=500
    @GET("earthquakesJSON")
    Call<Earthquakes> getEarthquakes(
            @Query("north")        float north,
            @Query("south")        float south,
            @Query("east")         float east,
            @Query("west")         float west,
            @Query("minMagnitude") int minMagnitude,
            @Query("date")         String date,
            @Query("maxRows")      int maxRows,
            @Query("username")     String userName);
}
