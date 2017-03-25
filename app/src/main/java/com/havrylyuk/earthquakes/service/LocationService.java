package com.havrylyuk.earthquakes.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.event.LocationEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by Igor Havrylyuk on 25.03.2017.
 */

public class LocationService extends IntentService {

    public static final String LATLNG_DATA_EXTRA = "com.havrylyuk.earthquakes.LATLNG_DATA_EXTRA";
    private static final String LOG_TAG = LocationService.class.getSimpleName();

    public LocationService() {
        super("LocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            LatLng latLng = intent.getParcelableExtra(LATLNG_DATA_EXTRA);
            String location = parseLocation(latLng);
            Log.d(LOG_TAG, "parseLocation: " + location);
            EventBus.getDefault().postSticky(new LocationEvent(location));
        }
    }

    public String parseLocation(LatLng latLng) {
        if (latLng == null) {
            return getString(R.string.unknown_location);
        }
        StringBuilder result = new StringBuilder("");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses  = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                if (addresses.get(0).getCountryName() != null) {
                    result.append(addresses.get(0).getCountryName());
                }
                if (addresses.get(0).getAdminArea() != null) {
                    result.append(",").append(addresses.get(0).getAdminArea());
                }
                if (addresses.get(0).getLocality() != null) {
                    result.append(",").append(addresses.get(0).getLocality());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.append(getString(R.string.error_parse_location));
        }
        if (TextUtils.isEmpty(result.toString())) {
            result.append(getString(R.string.unknown_location));
        }
        return result.toString();
    }
}
