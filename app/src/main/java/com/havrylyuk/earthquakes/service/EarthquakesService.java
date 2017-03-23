package com.havrylyuk.earthquakes.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.util.Log;

import com.havrylyuk.earthquakes.BuildConfig;
import com.havrylyuk.earthquakes.data.EarthquakesContract.EarthquakesEntry;
import com.havrylyuk.earthquakes.data.EarthquakesContract.ContinentsEntry;
import com.havrylyuk.earthquakes.model.Earthquake;
import com.havrylyuk.earthquakes.model.Earthquakes;
import com.havrylyuk.earthquakes.util.PreferencesHelper;

import java.io.IOException;

import retrofit2.Call;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class EarthquakesService extends IntentService {


    private static final String LOG_TAG = EarthquakesService.class.getSimpleName();
    private static final int E_MAX_ROW_SIZE = 500;//max
    private PreferencesHelper pf;

    public EarthquakesService() {
        super("EarthquakesService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        pf = PreferencesHelper.getInstance();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            loadData();
        }
    }

    private void loadData() {
        long continentId = pf.getContinent(this);
        int minMagnitude = pf.getMagnitude(this);
        String date = pf.getDate(this);
        String selection = continentId == -1 ? null : ContinentsEntry.COLUMN_CONTINENT_GEONAMEID + " = ?";
        String[] selectionArgs = continentId == -1 ? null : new String[]{String.valueOf(continentId)};
        final Cursor cursor = getContentResolver().query(ContinentsEntry.CONTENT_URI,
                null, selection, selectionArgs, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    float east = cursor.getFloat(cursor.getColumnIndex(ContinentsEntry.COLUMN_CONTINENT_EAST));
                    float west = cursor.getFloat(cursor.getColumnIndex(ContinentsEntry.COLUMN_CONTINENT_WEST));
                    float north = cursor.getFloat(cursor.getColumnIndex(ContinentsEntry.COLUMN_CONTINENT_NORTH));
                    float south = cursor.getFloat(cursor.getColumnIndex(ContinentsEntry.COLUMN_CONTINENT_SOUTH));
                    loadEarthquakes(continentId, north, south, east, west, minMagnitude, date);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void loadEarthquakes(long continentId, float north, float south, float east,
                                 float west, int minMagnitude, String date) {
        try {
            long inserted = 0;
            Log.v( LOG_TAG, "Begin load Earthquakes:continentId="
                    +continentId + " minMag="+minMagnitude+" date=" + date);
            ApiService service = ApiClient.getClient().create(ApiService.class);
            Call<Earthquakes> responseCall = service.getEarthquakes(north, south, east, west,
                    minMagnitude, date, E_MAX_ROW_SIZE, BuildConfig.GEONAME_API_KEY);
            Earthquakes earthquakes = responseCall.execute().body();
            if (earthquakes.getEarthquakes() != null) {
                ContentValues[] contentValues = new ContentValues[earthquakes.getEarthquakes().size()];
                for (int i = 0; i < earthquakes.getEarthquakes().size(); i++) {
                    contentValues[i] = earthquakesToContentValues(continentId, earthquakes.getEarthquakes().get(i));
                }
                inserted = getContentResolver().bulkInsert(EarthquakesEntry.CONTENT_URI, contentValues);
            }
            Log.v(LOG_TAG, "End load Earthquakes insert=" + inserted);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private ContentValues earthquakesToContentValues(long continentId, Earthquake earthquake) {
        ContentValues cv = new ContentValues();
        cv.put(EarthquakesEntry.EARTH_DATE_TIME, earthquake.getDatetime());
        cv.put(EarthquakesEntry.EARTH_DEPTH, earthquake.getDepth());
        cv.put(EarthquakesEntry.EARTH_EQUID, earthquake.getEqid());
        cv.put(EarthquakesEntry.EARTH_MAGNITUDE, earthquake.getMagnitude());
        cv.put(EarthquakesEntry.EARTH_SRC, earthquake.getSrc());
        cv.put(EarthquakesEntry.EARTH_LAT, earthquake.getLat());
        cv.put(EarthquakesEntry.EARTH_LNG, earthquake.getLng());
        cv.put(EarthquakesEntry.EARTH_CONTINENT_ID, continentId);
        return cv;
    }
}
