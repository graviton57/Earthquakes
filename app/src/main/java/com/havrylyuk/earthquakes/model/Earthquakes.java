package com.havrylyuk.earthquakes.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class Earthquakes {

    @SerializedName("earthquakes")
    private List<Earthquake> earthquakes;

    public List<Earthquake> getEarthquakes() {
        return earthquakes;
    }
}
