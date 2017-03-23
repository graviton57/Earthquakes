package com.havrylyuk.earthquakes.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.android.gms.maps.model.LatLng;
import com.havrylyuk.earthquakes.data.EarthquakesContract;


import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

/**
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class Utility {

    public static boolean isNetworkAvailable(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        boolean isNetworkAvailable = cm.getBackgroundDataSetting() &&
                cm.getActiveNetworkInfo() != null;
        return isNetworkAvailable;
    }

    public static double distance(LatLng point1, LatLng point2) {
        return computeDistanceBetween(point1, point2) / 1000;//km
    }

    private String buildSelection(long continentId) {
        StringBuilder builder = new StringBuilder();
        builder.append(EarthquakesContract.EarthquakesEntry.EARTH_MAGNITUDE + " > ? AND ")
                .append(EarthquakesContract.EarthquakesEntry.EARTH_DATE_TIME + " < ?  ");
        if (continentId > 0) {
            builder.append(" AND ")
                    .append(EarthquakesContract.EarthquakesEntry.EARTH_CONTINENT_ID + " = ?");
        }
        return builder.toString();
    }

    private String[] buildSelectionArgs(String date, long continentId, int magnitude ){
        String[] selectionArgs ;
        if (continentId > 0) {
            selectionArgs = new String[]{ String.valueOf(magnitude), date, String.valueOf(continentId)};
        } else {
            selectionArgs = new String[]{ String.valueOf(magnitude), date};
        }
        return selectionArgs;
    }


}
