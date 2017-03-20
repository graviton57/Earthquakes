/*
 * Copyright (c)  2017. Igor Gavriluyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.havrylyuk.earthquakes.data;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.havrylyuk.earthquakes.service.EarthquakesService;

import static com.havrylyuk.earthquakes.data.EarthquakesContract.CountriesEntry;
import static com.havrylyuk.earthquakes.data.EarthquakesContract.EarthquakesEntry;
import static com.havrylyuk.earthquakes.service.EarthquakesService.ACTION_LOAD_COUNTRIES;

/**
 *
 * Created by Igor Havrylyuk on 08.03.2017.
 */

public class GazetteerDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "gazetteer.db";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    private Context mContext;

    private final String SQL_CREATE_COUNTRIES_TABLE = "CREATE TABLE " +
            CountriesEntry.TABLE_NAME + " (" +
            CountriesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CountriesEntry.COUNTRY_GEONAME_ID + " INTEGER NOT NULL , " +
            CountriesEntry.COUNTRY_CONTINENT_NAME + " TEXT NOT NULL DEFAULT '', " +
            CountriesEntry.COUNTRY_COUNTRY_CODE + " TEXT NOT NULL DEFAULT '', " +
            CountriesEntry.COUNTRY_COUNTRY_NAME + " TEXT NOT NULL DEFAULT '', " +
            CountriesEntry.COUNTRY_CAPITAL + " TEXT NOT NULL DEFAULT '', " +
            CountriesEntry.COUNTRY_AREA + " REAL NOT NULL DEFAULT 0, " +
            CountriesEntry.COUNTRY_POPULATION + " INTEGER NOT NULL DEFAULT 0, " +
            CountriesEntry.COUNTRY_CURRENCY_CODE + " TEXT NOT NULL DEFAULT '', " +
            CountriesEntry.COUNTRY_LANGUAGES + " TEXT NOT NULL DEFAULT '', " +
            CountriesEntry.COUNTRY_SOUTH + " REAL NOT NULL DEFAULT 0, " +
            CountriesEntry.COUNTRY_WEST + " REAL NOT NULL DEFAULT 0, " +
            CountriesEntry.COUNTRY_EAST + " REAL NOT NULL DEFAULT 0, " +
            CountriesEntry.COUNTRY_NORTH + " REAL NOT NULL DEFAULT 0, " +
            CountriesEntry.COUNTRY_FAVORITE + " INTEGER NOT NULL DEFAULT 0, " +
            " UNIQUE (" + CountriesEntry.COUNTRY_GEONAME_ID + ") ON CONFLICT REPLACE);";


    private final String SQL_CREATE_EARTHQUAKES_TABLE = "CREATE TABLE " +
            EarthquakesEntry.TABLE_NAME + " (" +
            EarthquakesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            EarthquakesEntry.EARTH_COUNTRY_ID + " INTEGER NOT NULL , " +
            EarthquakesEntry.EARTH_DATE_TIME + " TEXT NOT NULL DEFAULT '', " +
            EarthquakesEntry.EARTH_DEPTH + " REAL NOT NULL DEFAULT 0 , " +
            EarthquakesEntry.EARTH_LNG + " REAL NOT NULL DEFAULT 0, " +
            EarthquakesEntry.EARTH_SRC + " TEXT NOT NULL DEFAULT '', " +
            EarthquakesEntry.EARTH_EQUID + " TEXT NOT NULL DEFAULT '', " +
            EarthquakesEntry.EARTH_MAGNITUDE + " REAL NOT NULL DEFAULT 0, " +
            EarthquakesEntry.EARTH_LAT + " REAL NOT NULL DEFAULT 0, " +
            " UNIQUE (" + EarthquakesEntry.EARTH_DATE_TIME +  ", " + EarthquakesEntry.EARTH_LNG +
                       ", " + EarthquakesEntry.EARTH_LAT+ ") ON CONFLICT REPLACE, "+
            "FOREIGN KEY (" + EarthquakesEntry.EARTH_COUNTRY_ID + ") REFERENCES " +
            CountriesEntry.TABLE_NAME + "(" + CountriesEntry._ID + ") );";


    public GazetteerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_COUNTRIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EARTHQUAKES_TABLE);
        Intent intent = new Intent(mContext, EarthquakesService.class);
        intent.setAction(ACTION_LOAD_COUNTRIES);
        mContext.startService(intent);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
    }
}
