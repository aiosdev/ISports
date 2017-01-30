/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.aiosdev.isports.tabmain.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class MapProvider extends ContentProvider {

    private static final String TAG = MapProvider.class.getSimpleName();

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private MapDbHelper dbHelper;

    private static final int LOCATIONS = 100;
    private static final int MOVIE_WITH_ID = 101;

    private static SQLiteQueryBuilder locationQueryBuilder;

    static {
        locationQueryBuilder = new SQLiteQueryBuilder();
        locationQueryBuilder.setTables(MapContract.MapEntry.TABLE_NAME);

    }

    public static String locationByDateSelection = MapContract.MapEntry.TABLE_NAME +
            "." + MapContract.MapEntry.COLUMN_DATE_TIME + "=?";


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MapContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MapContract.PATH_MAP, LOCATIONS);
        matcher.addURI(authority, MapContract.PATH_MAP + "/#", MOVIE_WITH_ID);


        return matcher;
    }

    private Cursor getLocations(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return locationQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getMovieByKey(Uri uri) {
        String _movieId = String.valueOf(ContentUris.parseId(uri));

        String[] selectionArgs = new String[]{_movieId};
        String selection = locationByDateSelection;

        return locationQueryBuilder.query(dbHelper.getReadableDatabase(),
                new String[]{"*"},
                selection,
                selectionArgs,
                null,
                null,
                null);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new MapDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = uriMatcher.match(uri);

        switch (match) {
            case LOCATIONS:
                return MapContract.MapEntry.CONTENT_DIR_TYPE;
            case MOVIE_WITH_ID:
                return MapContract.MapEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case LOCATIONS:
                cursor = getLocations(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case MOVIE_WITH_ID:
                cursor = getMovieByKey(uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case LOCATIONS: {
                long _id = db.insert(MapContract.MapEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MapContract.MapEntry.buildMapUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case LOCATIONS: {
                rowsDeleted = db.delete(
                        MapContract.MapEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if (context != null && rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case LOCATIONS: {
                rowsUpdated = db.update(MapContract.MapEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if (context != null && rowsUpdated != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}