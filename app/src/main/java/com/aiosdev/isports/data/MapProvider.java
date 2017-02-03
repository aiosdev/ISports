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
package com.aiosdev.isports.data;

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
    private static final int USER = 200;
    private static final int TASK = 300;

    private static SQLiteQueryBuilder locationQueryBuilder;
    private static SQLiteQueryBuilder userQueryBuilder;
    private static SQLiteQueryBuilder taskQueryBuilder;

    static {
        locationQueryBuilder = new SQLiteQueryBuilder();
        locationQueryBuilder.setTables(MapContract.LoactionEntry.TABLE_NAME);

        userQueryBuilder = new SQLiteQueryBuilder();
        userQueryBuilder.setTables(MapContract.UserInfoEntry.TABLE_NAME);

        taskQueryBuilder = new SQLiteQueryBuilder();
        taskQueryBuilder.setTables(MapContract.TaskEntry.TABLE_NAME);

    }

    public static String locationByDateSelection = MapContract.LoactionEntry.TABLE_NAME +
            "." + MapContract.LoactionEntry.COLUMN_DATE_TIME + "=?";


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MapContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MapContract.PATH_LOC, LOCATIONS);
        matcher.addURI(authority, MapContract.PATH_LOC + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MapContract.PATH_USER, USER);
        matcher.addURI(authority, MapContract.PATH_USER + "/#", USER);

        matcher.addURI(authority, MapContract.PATH_TASK, TASK);
        matcher.addURI(authority, MapContract.PATH_TASK + "/#", TASK);

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

    private Cursor getUser(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return userQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getTask(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return taskQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getLocByKey(Uri uri) {
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
                return MapContract.LoactionEntry.CONTENT_DIR_TYPE;
            case MOVIE_WITH_ID:
                return MapContract.LoactionEntry.CONTENT_ITEM_TYPE;
            case USER:
                return MapContract.UserInfoEntry.CONTENT_DIR_TYPE;
            case TASK:
                return MapContract.TaskEntry.CONTENT_DIR_TYPE;
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
                cursor = getLocByKey(uri);
                break;
            case USER:
                cursor = getUser(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case TASK:
                cursor = getTask(uri, projection, selection, selectionArgs, sortOrder);
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
                long _id = db.insert(MapContract.LoactionEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MapContract.LoactionEntry.buildMapUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case USER: {
                long _id = db.insert(MapContract.UserInfoEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MapContract.UserInfoEntry.buildMapUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case TASK: {
                long _id = db.insert(MapContract.TaskEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = MapContract.TaskEntry.buildMapUri(_id);
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
                        MapContract.LoactionEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case USER: {
                rowsDeleted = db.delete(
                        MapContract.UserInfoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TASK: {
                rowsDeleted = db.delete(
                        MapContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
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
                rowsUpdated = db.update(MapContract.LoactionEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }

            case USER: {
                rowsUpdated = db.update(MapContract.UserInfoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }

            case TASK: {
                rowsUpdated = db.update(MapContract.TaskEntry.TABLE_NAME, values, selection, selectionArgs);
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