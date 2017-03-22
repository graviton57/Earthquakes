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

import static com.havrylyuk.earthquakes.data.EarthquakesContract.ContinentsEntry;
import static com.havrylyuk.earthquakes.data.EarthquakesContract.EarthquakesEntry;

/**
 *
 * Created by Igor Havrylyuk on 19.03.2017.
 */

public class GazetteerDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "earthquakes.db";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    private Context mContext;

    private final String SQL_CREATE_CONTINENT_TABLE = "CREATE TABLE " + ContinentsEntry.TABLE_NAME + " (" +
            ContinentsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ContinentsEntry.COLUMN_CONTINENT_CODE + " TEXT NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_NAME + " TEXT NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_GEONAMEID + " INTEGER NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_POPULATION + " INTEGER NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_GEOCODE + " TEXT NOT NULL, " +
            ContinentsEntry.COLUMN_COUNTRY_COUNT + " INTEGER NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_AREA + " REAL NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_WIKIPEDIA + " TEXT NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_EAST + " REAL NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_WEST + " REAL NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_SOUTH + " REAL NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_NORTH + " REAL NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_LAT + " TEXT NOT NULL, " +
            ContinentsEntry.COLUMN_CONTINENT_LNG + " TEXTL NOT NULL, " +
            " UNIQUE (" + ContinentsEntry.COLUMN_CONTINENT_CODE + ") ON CONFLICT REPLACE);";

    private final String[] SQL_INSERTS_CONTINENT_DATA = {
            "INSERT INTO continentcodes ( code, name, geonameId, population, wikipediaURL, east, west, south, north, lat, lng, countries, area, geocode)" +
                    " VALUES ( 'AF', 'Africa', 6255146, 1031833000, 'en.wikipedia.org/wiki/Africa', 51.412635803222656, -18.157487869262695, -46.9697265625, 37.53944396972656, '7.1881', '21.09375', 54, 30221532, '002');",
            "INSERT INTO continentcodes ( code, name, geonameId, population, wikipediaURL, east, west, south, north, lat, lng, countries, area, geocode)" +
                    " VALUES ( 'AS', 'Asia', 6255147, 3812366000, 'en.wikipedia.org/wiki/Asia', -168.98974609375, 25.66851043701172, -10.930000305175781, 81.8519287109375, '29.84064', '89.29688', 48, 44579000, '142');",
            "INSERT INTO continentcodes ( code, name, geonameId, population, wikipediaURL, east, west, south, north, lat, lng, countries, area, geocode) " +
                    "VALUES ( 'EU', 'Europe', 6255148, 742452000, 'en.wikipedia.org/wiki/Europe', 41.73303985595703, -24.542224884033203, 27.637788772583008, 80.76416015625, '48.69096', '9.14062', 50, 10180000, '150');",
            "INSERT INTO continentcodes ( code, name, geonameId, population, wikipediaURL, east, west, south, north, lat, lng, countries, area, geocode) " +
                    "VALUES ( 'NA', 'North America', 6255149, 565265000, 'en.wikipedia.org/wiki/North_America', -25.012222290039062, 172.34783935546875, 7.206110954284668, 83.14808654785156, '46.07323', '-100.54688', 23, 24709000, '019');",
            "INSERT INTO continentcodes ( code, name, geonameId, population, wikipediaURL, east, west, south, north, lat, lng, countries, area, geocode)" +
                    " VALUES ( 'OC', 'Oceania', 6255151, 36659000, 'en.wikipedia.org/wiki/Oceania', -180, 112.75579833984375, -47.28999328613281, -5.517269134521484, '-18.31281', '138.51562', 15, 8525989, '009');",
            "INSERT INTO continentcodes ( code, name, geonameId, population, wikipediaURL, east, west, south, north, lat, lng, countries, area, geocode) " +
                    "VALUES ( 'SA', 'South America', 6255150, 385742554, 'en.wikipedia.org/wiki/South_America', -28.83609390258789, -91.66389465332031, -55.9893684387207, 13.394789695739746, '-14.60485', '-57.65625', 12, 17840000, '019');",
            "INSERT INTO continentcodes ( code, name, geonameId, population, wikipediaURL, east, west, south, north, lat, lng, countries, area, geocode)" +
                    " VALUES ( 'AN', 'Antarctica', 6255152, 0, 'en.wikipedia.org/wiki/Antarctica', 150, -150, -89, -70, '-78.15856', '16.40626', 0, 14000000, '000');"};

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
                       ", " + EarthquakesEntry.EARTH_LAT+ ") ON CONFLICT REPLACE ) ";


    public GazetteerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_CONTINENT_TABLE);
        for (String sql : SQL_INSERTS_CONTINENT_DATA) {
            sqLiteDatabase.execSQL(sql);
        }
        sqLiteDatabase.execSQL(SQL_CREATE_EARTHQUAKES_TABLE);
        Intent intent = new Intent(mContext, EarthquakesService.class);
        mContext.startService(intent);
    }


    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
