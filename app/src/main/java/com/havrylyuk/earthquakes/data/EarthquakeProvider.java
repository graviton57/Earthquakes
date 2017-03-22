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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.havrylyuk.earthquakes.data.EarthquakesContract.ContinentsEntry;
import com.havrylyuk.earthquakes.data.EarthquakesContract.EarthquakesEntry;

/**
 *
 * Created by Igor Havrylyuk on 19.03.2017.
 */

public class EarthquakeProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private GazetteerDBHelper openHelper;

    static final int CONTINENTS = 1000;
    static final int CONTINENT_WITH_ID = 1001;
    static final int EARTHQUAKES = 1002;
    static final int EARTHQUAKES_WITH_ID = 1003;

    private static final SQLiteQueryBuilder sContinentByIdQueryBuilder;
    private static final SQLiteQueryBuilder sEarthquakeByIdQueryBuilder;

    static {
        sContinentByIdQueryBuilder = new SQLiteQueryBuilder();
        sContinentByIdQueryBuilder.setTables(ContinentsEntry.TABLE_NAME);

        sEarthquakeByIdQueryBuilder = new SQLiteQueryBuilder();
        sEarthquakeByIdQueryBuilder.setTables(EarthquakesEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EarthquakesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, EarthquakesContract.PATH_CONTINENT, CONTINENTS);
        matcher.addURI(authority, EarthquakesContract.PATH_CONTINENT +"/#", CONTINENT_WITH_ID);
        matcher.addURI(authority, EarthquakesContract.PATH_EARTHQUAKES, EARTHQUAKES);
        matcher.addURI(authority, EarthquakesContract.PATH_EARTHQUAKES +"/#", EARTHQUAKES_WITH_ID);
        return matcher;
    }

    private static final String continentByIdSelection =
            ContinentsEntry.TABLE_NAME + "." + ContinentsEntry._ID + " = ? ";

    private static final String earthquakesByIdSelection =
            EarthquakesEntry.TABLE_NAME + "." + EarthquakesEntry._ID + " = ? ";

    private Cursor getContinentById(Uri uri, String[] projection, String sortOrder) {
        String selectionId = String.valueOf(ContinentsEntry.getIdFromUri(uri));
        String selection = continentByIdSelection;
        String[] selectionArgs = new String[]{selectionId};
        return sContinentByIdQueryBuilder.query(openHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getEarthquakeById(Uri uri, String[] projection, String sortOrder) {
        String selectionId = String.valueOf(EarthquakesEntry.getIdFromUri(uri));
        String selection = earthquakesByIdSelection;
        String[] selectionArgs = new String[]{selectionId};
        return sEarthquakeByIdQueryBuilder.query(openHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    public EarthquakeProvider() {
    }

    @Override
    public boolean onCreate() {
        openHelper = new GazetteerDBHelper(getContext());
        return true;
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case CONTINENTS:
                rowsDeleted = db.delete(
                        ContinentsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case EARTHQUAKES:
                rowsDeleted = db.delete(
                        EarthquakesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CONTINENTS:
                return ContinentsEntry.CONTENT_TYPE;
            case CONTINENT_WITH_ID:
                return ContinentsEntry.CONTENT_ITEM_TYPE;
            case EARTHQUAKES:
                return EarthquakesEntry.CONTENT_TYPE;
            case EARTHQUAKES_WITH_ID:
                return EarthquakesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case CONTINENTS: {
                long _id = db.insert(ContinentsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ContinentsEntry.buildContinentsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case EARTHQUAKES: {
                long _id = db.insert(EarthquakesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EarthquakesEntry.buildEarthquakesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CONTINENTS: {
                retCursor = openHelper.getReadableDatabase().query(
                        ContinentsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CONTINENT_WITH_ID: {
                retCursor = getContinentById(uri, projection, sortOrder);
                break;
            }
            case EARTHQUAKES: {
                retCursor = openHelper.getReadableDatabase().query(
                        EarthquakesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case EARTHQUAKES_WITH_ID: {
                retCursor = getEarthquakeById(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case CONTINENTS:
                rowsUpdated = db.update(ContinentsEntry.TABLE_NAME, values, selection,
                       selectionArgs);
                break;
            case EARTHQUAKES:
                rowsUpdated = db.update(EarthquakesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case CONTINENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ContinentsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case EARTHQUAKES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(EarthquakesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
