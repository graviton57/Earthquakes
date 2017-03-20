
package com.havrylyuk.earthquakes.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 *
 * Created by Igor Havrylyuk on 08.03.2017.
 */

public class EarthquakesContract {

    public static final String CONTENT_AUTHORITY = "com.havrylyuk.earthquakes";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COUNTRIES = "countries";
    public static final String PATH_EARTHQUAKES = "earthquakes";

    public static final class CountriesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_COUNTRIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRIES;

        // Table name
        public static final String TABLE_NAME = "countries";

        public static final String COUNTRY_CONTINENT_NAME = "continent_name";
        public static final String COUNTRY_COUNTRY_CODE   = "country_code";
        public static final String COUNTRY_COUNTRY_NAME   = "country_name";
        public static final String COUNTRY_CAPITAL        = "capital";
        public static final String COUNTRY_LANGUAGES      = "languages";
        public static final String COUNTRY_GEONAME_ID     = "geonameId";
        public static final String COUNTRY_SOUTH          = "south";
        public static final String COUNTRY_NORTH          = "north";
        public static final String COUNTRY_EAST           = "east";
        public static final String COUNTRY_WEST           = "west";
        public static final String COUNTRY_POPULATION     = "population";
        public static final String COUNTRY_AREA           = "area";
        public static final String COUNTRY_CURRENCY_CODE  = "currency_code";
        public static final String COUNTRY_FAVORITE       = "favorite";


        public static Uri buildCountryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
    public static final class EarthquakesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EARTHQUAKES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EARTHQUAKES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EARTHQUAKES;

        // Table name
        public static final String TABLE_NAME = "earthquakes";

        public static final String EARTH_COUNTRY_ID = "country_id";
        public static final String EARTH_DATE_TIME  = "datetime";
        public static final String EARTH_DEPTH      = "depth";
        public static final String EARTH_LNG        = "lng";
        public static final String EARTH_SRC        = "src";
        public static final String EARTH_EQUID      = "eqid";
        public static final String EARTH_MAGNITUDE  = "magnitude";
        public static final String EARTH_LAT        = "lat";


        public static Uri buildEarthquakesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }
}
