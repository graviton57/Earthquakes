package com.havrylyuk.earthquakes.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.android.gms.maps.model.LatLng;
import com.havrylyuk.earthquakes.fragment.SettingsFragment.DatePeriod;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    public static String convertDate(DatePeriod datePeriod) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        Date date;
        switch (datePeriod) {
            case LAST_MONTH:
                calendar.add(Calendar.MONTH, -1);
                date = calendar.getTime();
                return format.format(date);
            case LAST_YEAR:
                calendar.add(Calendar.YEAR, -1);
                date = calendar.getTime();
                return format.format(date);
            case ALL:
                calendar.add(Calendar.YEAR, -10);
                date = calendar.getTime();
                return format.format(date);
            default:
                calendar.add(Calendar.MONTH, -1);
                date = calendar.getTime();
                return format.format(date);
        }
    }
}
