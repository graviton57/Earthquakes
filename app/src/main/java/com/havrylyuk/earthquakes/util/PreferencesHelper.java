package com.havrylyuk.earthquakes.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.havrylyuk.earthquakes.EarthquakeApp;
import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.fragment.SettingsFragment.DatePeriod;


/**
 * Preferences Helper
 * Created by Igor Havrylyuk on 21.03.2017.
 */

public class PreferencesHelper {

    public static final int DEFAULT_MAGNITUDE_VALUE = 3;
    public static final int DEFAULT_CONTINENT_ID = 6255148;//Europe

    private static PreferencesHelper sInstance = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static PreferencesHelper getInstance() {
        if(sInstance == null) {
            sInstance = new PreferencesHelper();
        }
        return sInstance;
    }

    public PreferencesHelper() {
        this.sharedPreferences = EarthquakeApp.getSharedPreferences();
        this.editor = this.sharedPreferences.edit();
    }

    //for save data in SharedPreferences
    public void setDate(Context context,  int date){
        editor.putInt(context.getString(R.string.pref_date_key), date);
        editor.apply();
    }

    //for the loading of data from SharedPreferences
    public int getDate(Context context){
        return sharedPreferences.getInt(context.getString(R.string.pref_date_key), DatePeriod.LAST_MONTH.ordinal());
    }

    public void setMagnitude(Context context, int magnitude){
        editor.putInt(context.getString(R.string.pref_magnitude_key), magnitude);
        editor.apply();
    }

    public int getMagnitude(Context context){
        return sharedPreferences.getInt(context.getString(R.string.pref_magnitude_key),
                DEFAULT_MAGNITUDE_VALUE);
    }

    public long getContinent(Context context){
        return sharedPreferences.getLong(context.getString(R.string.pref_continent_key),
                DEFAULT_CONTINENT_ID);
    }

    public void setContinent(Context context, long continentId){
        editor.putLong(context.getString(R.string.pref_continent_key), continentId);
        editor.apply();
    }
}
