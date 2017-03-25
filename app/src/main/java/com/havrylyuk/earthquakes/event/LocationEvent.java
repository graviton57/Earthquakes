package com.havrylyuk.earthquakes.event;

/**
 * Created by Igor Havrylyuk on 25.03.2017.
 */

public class LocationEvent {

    private String location;

    public LocationEvent(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}
