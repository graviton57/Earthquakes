package com.havrylyuk.earthquakes.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.havrylyuk.earthquakes.BuildConfig;
import com.havrylyuk.earthquakes.data.EarthquakesContract.EarthquakesEntry;
import com.havrylyuk.earthquakes.data.EarthquakesContract.CountriesEntry;
import com.havrylyuk.earthquakes.model.BoundingBox;
import com.havrylyuk.earthquakes.model.Countries;
import com.havrylyuk.earthquakes.model.Country;
import com.havrylyuk.earthquakes.model.Earthquake;
import com.havrylyuk.earthquakes.model.Earthquakes;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class EarthquakesService extends IntentService {

    public static final String EXTRA_COUNTRY_ID = "com.havrylyuk.earthquakes.EXTRA_COUNTRY_ID";
    public static final String EXTRA_BOUNDING_BOX = "com.havrylyuk.earthquakes.EXTRA_BOUNDING_BOX";

    public static final String ACTION_LOAD_COUNTRIES = "com.havrylyuk.earthquakes.ACTION_LOAD_COUNTRIES";
    public static final String ACTION_LOAD_EARTHQUAKES = "com.havrylyuk.earthquakes.ACTION_LOAD_EARTHQUAKES";

    private static final String LOG_TAG = EarthquakesService.class.getSimpleName();
    private static final int E_MAX_ROW_SIZE = 500;//max

    public EarthquakesService() {
        super("EarthquakesService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(ACTION_LOAD_COUNTRIES)) {
                loadCountries();
            }
            if (intent.getAction().equals(ACTION_LOAD_EARTHQUAKES)) {
                long countryId = intent.getLongExtra(EXTRA_COUNTRY_ID, -1);
                BoundingBox  box = intent.getParcelableExtra(EXTRA_BOUNDING_BOX);
                loadEarthquakes(countryId, box.getNorth(), box.getSouth(), box.getEast(), box.getWest());
            }
        }
    }

    private void loadCountries() {
        try {
            long inserted = 0;
            if (BuildConfig.DEBUG) Log.v( LOG_TAG, "Begin load countries" );
            ApiService service = ApiClient.getClient().create(ApiService.class);
            Call<Countries> responseCall =
                    service.getCountries(Locale.getDefault().getLanguage(), BuildConfig.GEONAME_API_KEY, "FULL");
            Countries countries = responseCall.execute().body();
            if (countries.getCountries() != null) {
                ContentValues[] contentValues = new ContentValues[countries.getCountries().size()];
                for (int i = 0; i < countries.getCountries().size(); i++) {
                    contentValues[i] = countryToContentValues(countries.getCountries().get(i));
                    /*loadEarthquakes(countries.getCountries().get(i).getGeonameId(),
                                    countries.getCountries().get(i).getNorth(),
                                    countries.getCountries().get(i).getSouth(),
                                    countries.getCountries().get(i).getEast(),
                                    countries.getCountries().get(i).getWest());*/
                }
                inserted = getContentResolver().bulkInsert(CountriesEntry.CONTENT_URI, contentValues);
            }
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "End load countries insert=" + inserted);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private ContentValues countryToContentValues(Country country) {
        ContentValues cv = new ContentValues();
        cv.put(CountriesEntry.COUNTRY_AREA,  country.getAreaInSqKm());
        cv.put(CountriesEntry.COUNTRY_CAPITAL, country.getCapital());
        cv.put(CountriesEntry.COUNTRY_CONTINENT_NAME, country.getContinentName());
        cv.put(CountriesEntry.COUNTRY_COUNTRY_CODE, country.getCountryCode());
        cv.put(CountriesEntry.COUNTRY_COUNTRY_NAME, country.getCountryName());
        cv.put(CountriesEntry.COUNTRY_WEST, country.getWest());
        cv.put(CountriesEntry.COUNTRY_EAST, country.getEast());
        cv.put(CountriesEntry.COUNTRY_SOUTH, country.getSouth());
        cv.put(CountriesEntry.COUNTRY_NORTH, country.getNorth());
        cv.put(CountriesEntry.COUNTRY_CURRENCY_CODE, country.getCurrencyCode());
        cv.put(CountriesEntry.COUNTRY_GEONAME_ID, country.getGeonameId());
        return cv;
    }

    private void loadEarthquakes(long countryId, float north, float south, float east, float west) {
        try {
            long inserted = 0;
            if (BuildConfig.DEBUG) Log.v( LOG_TAG, "Begin load Earthquakes" );
            ApiService service = ApiClient.getClient().create(ApiService.class);
            Call<Earthquakes> responseCall = service.getEarthquakes(north, south, east, west,
                    E_MAX_ROW_SIZE, BuildConfig.GEONAME_API_KEY);
            Earthquakes earthquakes = responseCall.execute().body();
            if (earthquakes.getEarthquakes() != null) {
                ContentValues[] contentValues = new ContentValues[earthquakes.getEarthquakes().size()];
                for (int i = 0; i < earthquakes.getEarthquakes().size(); i++) {
                    contentValues[i] = earthquakesToContentValues(countryId, earthquakes.getEarthquakes().get(i));
                }
                inserted = getContentResolver().bulkInsert(EarthquakesEntry.CONTENT_URI, contentValues);
            }
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "End load Earthquakes insert=" + inserted);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    private ContentValues earthquakesToContentValues(long countryId, Earthquake earthquake) {
        ContentValues cv = new ContentValues();
        cv.put(EarthquakesEntry.EARTH_DATE_TIME, earthquake.getDatetime());
        cv.put(EarthquakesEntry.EARTH_DEPTH, earthquake.getDepth());
        cv.put(EarthquakesEntry.EARTH_EQUID, earthquake.getEqid());
        cv.put(EarthquakesEntry.EARTH_MAGNITUDE, earthquake.getMagnitude());
        cv.put(EarthquakesEntry.EARTH_SRC, earthquake.getSrc());
        cv.put(EarthquakesEntry.EARTH_LAT, earthquake.getLat());
        cv.put(EarthquakesEntry.EARTH_LNG, earthquake.getLng());
        cv.put(EarthquakesEntry.EARTH_COUNTRY_ID, countryId);
        return cv;
    }
}
