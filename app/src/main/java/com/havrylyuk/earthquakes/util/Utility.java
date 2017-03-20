package com.havrylyuk.earthquakes.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.android.gms.maps.model.LatLng;

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

    private static double distance(LatLng point1, LatLng point2) {
        return computeDistanceBetween(point1, point2) / 1000;//km
    }
}
