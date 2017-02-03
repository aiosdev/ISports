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

    public static final String PATH_LOC = "location";
    public static final String PATH_USER = "user";
    public static final String PATH_TASK = "task";

    //Location Table database entry
    public static final class LoactionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOC).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_LOC;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_LOC;

        // Table name
        public static final String TABLE_NAME = "location";

        //column name
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_DATE_TIME = "datetime";    //日期时间
        public static final String COLUMN_TASK_NO = "task_no";       //每天任务编号
        public static final String COLUMN_LAT = "lat";              //经度
        public static final String COLUMN_LONG = "lon";             //纬度

        public static Uri buildMapUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    //UserInfo Table database entry
    public static final class UserInfoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USER).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_USER;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_USER;

        // Table name
        public static final String TABLE_NAME = "user";

        //column name
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_NAME = "name";                          //名字
        public static final String COLUMN_SEX = "sex";                            //性别
        public static final String COLUMN_GRADE = "grade";                       //等级
        public static final String COLUMN_TITLE = "title";                       //头衔
        public static final String COLUMN_STEP_COUNT = "step_count";            //计划每天步数
        public static final String COLUMN_WEIGHT = "weight";                       //体重
        public static final String COLUMN_TOTAL_STEP = "total_step";              //累计步数
        public static final String COLUMN_TOTAL_DISTANCE = "total_distance";      //累计距离
        public static final String COLUMN_TOTAL_CALORIES = "total_calories";      //累计热量
        public static final String COLUMN_TOTAL_DURATION = "total_duration";      //累计时长
        public static final String COLUMN_AVG_STEP = "avg_step";            //平均步幅
        public static final String COLUMN_AVG_SPEED = "avg_speed";          //平均速度



        public static Uri buildMapUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    //Task Table database entry
    public static final class TaskEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASK).build();

        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_TASK;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_URI + "/" + PATH_TASK;

        // Table name
        public static final String TABLE_NAME = "task";

        //column name
        public static final String COLUMN_ID = "_ID";
        public static final String COLUMN_DATE = "date";                          //日期
        public static final String COLUMN_TASK_NO = "task_no";                    //任务编号
        public static final String COLUMN_STEP = "step";              //步数
        public static final String COLUMN_DISTANCE = "distance";      //距离
        public static final String COLUMN_CALORIES = "calories";      //热量
        public static final String COLUMN_DURATION = "duration";      //时长
        public static final String COLUMN_AVG_STEP = "avg_step";      //平均步幅
        public static final String COLUMN_AVG_SPEED = "avg_speed";    //平均速度
        public static final String COLUMN_HIGH_SPEED = "high_speed";    //最高速度
        public static final String COLUMN_LOW_SPEED = "low_speed";    //最低速度



        public static Uri buildMapUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
