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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Manages a local database for Location data.
 */
public class MapDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "map.db";

    public MapDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_LOC_TABLE = "CREATE TABLE " + MapContract.LoactionEntry.TABLE_NAME + " (" +
                MapContract.LoactionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MapContract.LoactionEntry.COLUMN_DATE_TIME + " TEXT, " +
                MapContract.LoactionEntry.COLUMN_TASK_NO + " TEXT, " +
                MapContract.LoactionEntry.COLUMN_LAT + " TEXT, " +
                MapContract.LoactionEntry.COLUMN_LONG + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_LOC_TABLE);

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + MapContract.UserInfoEntry.TABLE_NAME + " (" +
                MapContract.LoactionEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MapContract.UserInfoEntry.COLUMN_NAME + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_SEX + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_WEIGHT + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_GRADE + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_TITLE + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_STEP_COUNT + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_TOTAL_STEP + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_TOTAL_DISTANCE + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_TOTAL_CALORIES + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_TOTAL_DURATION + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_AVG_STEP + " TEXT, " +
                MapContract.UserInfoEntry.COLUMN_AVG_SPEED + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);

        final String SQL_CREATE_TASK_TABLE = "CREATE TABLE " + MapContract.TaskEntry.TABLE_NAME + " (" +
                MapContract.TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MapContract.TaskEntry.COLUMN_DATE + " TEXT, " +
                MapContract.TaskEntry.COLUMN_TASK_NO + " TEXT, " +
                MapContract.TaskEntry.COLUMN_STEP + " TEXT, " +
                MapContract.TaskEntry.COLUMN_DISTANCE + " TEXT, " +
                MapContract.TaskEntry.COLUMN_CALORIES + " TEXT, " +
                MapContract.TaskEntry.COLUMN_DURATION + " TEXT, " +
                MapContract.TaskEntry.COLUMN_AVG_STEP + " TEXT, " +
                MapContract.TaskEntry.COLUMN_AVG_SPEED + " TEXT, " +
                MapContract.TaskEntry.COLUMN_HIGH_SPEED + " TEXT, " +
                MapContract.TaskEntry.COLUMN_LOW_SPEED + " TEXT " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_TASK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MapContract.LoactionEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
