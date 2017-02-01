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
package com.aiosdev.isports.data.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the Location database.
 */
public class MapContract {

    public static final String CONTENT_AUTHORITY = "com.aiosdev.isports";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MAP = "map";


    public static final class MapEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MAP).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_MAP;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_MAP;

        // Table name
        public static final String TABLE_NAME = "location";

        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_DATE_TIME = "datetime";
        public static final String COLUMN_LAT = "lat";
        public static final String COLUMN_LONG = "lon";


        public static Uri buildMapUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


    }

}
