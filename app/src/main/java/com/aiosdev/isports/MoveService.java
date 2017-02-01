package com.aiosdev.isports;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.aiosdev.isports.data.MapContract;
import com.aiosdev.isports.data.MapDbHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MoveService extends Service implements com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static Boolean FLAG = false;// service运行标志

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    private ArrayList<String> datelist;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FLAG = true;// 标记为服务正在运行

        //首先生成一个GoogleApiClient对象并且设置属性
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //然后连接,连接成功后会在onConnected回调
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FLAG = false;// 服务停止
        //停止位置变化监听
        stopLocation();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location myLocation =
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (myLocation != null) {
            Log.d("myLocation:", myLocation + "");
            updateToNewLocation(myLocation);
        }

        //开始定位前构造LocationRequest类设置定位时的属性.比如隔多久定位一次，设置定位精度等等
        createLocationRequest();

        //创建完成后开始定位
        startLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.d("service链接状态：", "失败!");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("service位置变化：", "lat= " + location.getLatitude() + "; lon= " + location.getLongitude());
        if (location != null) {
            //保存数据库
            ContentValues contentValues = new ContentValues();
            contentValues.put(MapContract.MapEntry.COLUMN_LAT, location.getLatitude());
            contentValues.put(MapContract.MapEntry.COLUMN_LONG, location.getLongitude());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
            contentValues.put(MapContract.MapEntry.COLUMN_DATE_TIME, df.format(new Date()));

            Uri url = MapContract.MapEntry.CONTENT_URI;
            getContentResolver().insert(url, contentValues);
            Log.d("物理位置存档：", "成功记录一条信息：lat= " + location.getLatitude() + "; lon= " + location.getLongitude());

        } else {
            System.out.println("---测试mylocation---location为null");
            Log.d("物理位置存档：", "---测试mylocation---location为null");
        }

    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(0.1F);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void updateToNewLocation(Location location) {
        Log.d("service位置初始化：", "lat= " + location.getLatitude() + "; lon= " + location.getLongitude());
    }

    public void startLocation() {
        Log.d("mGoogleApiClient是否链接:", mGoogleApiClient.isConnected() + "");
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, locationRequest, this);
            Log.d("位置变化监听器状态：", "开启！");
        }
    }

    public void stopLocation() {
        Log.d("mGoogleApiClient是否链接:", mGoogleApiClient.isConnected() + "");
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("位置变化监听器状态：", "关闭！");
        }
    }

    /**
     * 判断某张表是否存在
     */
    public boolean tableIsExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        Cursor cursor = null;
        MapDbHelper dbHelper = new MapDbHelper(this);
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //这里表名可以是Sqlite_master
            String sql = "select count(*) as c from sqlite_master" + " where type ='table' and name ='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void queryPointByDate() {
        datelist.clear();
        String columns[] = new String[]{"distinct substr(" + MapContract.MapEntry.COLUMN_DATE_TIME + ", 1, 10) as date"};
        Uri myUri = MapContract.MapEntry.CONTENT_URI;
        //Cursor cur = FavoriteActivity.this.managedQuery(myUri, columns, null, null, null);
        Cursor cur = null;

        String orderbyTimeAsc = "date" + " asc";
        cur = this.getContentResolver().query(myUri, columns, null, null, orderbyTimeAsc);

        if (cur.moveToFirst()) {

            do {
                datelist.add(cur.getString(cur.getColumnIndex("date")));

            } while (cur.moveToNext());


        }
    }
}
