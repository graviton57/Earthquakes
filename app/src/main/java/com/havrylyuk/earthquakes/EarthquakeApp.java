package com.havrylyuk.earthquakes;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class EarthquakeApp extends Application {

    private static SharedPreferences sSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        sSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    public static SharedPreferences getSharedPreferences() {
        return sSharedPreferences;
    }
}
