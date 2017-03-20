package com.havrylyuk.earthquakes.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */
public class Countries extends ApiResponse {

    @SerializedName("geonames")
    private List<Country> countries;

    public List<Country> getCountries() {
        return countries;
    }
}