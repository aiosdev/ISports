package com.aiosdev.isports;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.aiosdev.isports.data.MapContract;
import com.aiosdev.isports.data.MapDbHelper;
import com.aiosdev.isports.data.MapLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class MoveMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ArrayList<MapLocation> locationList;

    private PolylineOptions mPolylineOptions;

    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        date = df.format(new Date()).substring(0, 10);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setIndoorEnabled(true);

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
        mMap.setMyLocationEnabled(true);

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        /*
        Double[] lats = {45.470813, 45.4708272, 45.4706755, 45.4705364, 45.4703989, 45.4704712, 45.4704882, 45.4705577, 45.4707251, 45.4708783, 45.4708783, 45.471101};
        Double[] lons = {-73.5670947, -73.567191, -73.5676989, -73.5677809, -73.568385, -73.5693882, -73.5705287, -73.5704877, -73.5705981, -73.5706123, -73.5706123, -73.5705856};

        for(int i=0;i<lats.length;i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(lats[i], lons[i])));
        }
        */

        initDatas();
        if (locationList.isEmpty()) {
            Toast.makeText(this, "暂时没有位置数据！", Toast.LENGTH_SHORT).show();
        } else {

            mPolylineOptions = new PolylineOptions();

            for (MapLocation location : locationList) {
                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLon()))));

                //PolylineOptions options = new PolylineOptions();
                mPolylineOptions.add(new LatLng(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLon())));

                LatLng begin = new LatLng(Double.parseDouble(location.getLat()), Double.parseDouble(location.getLon()));
                LatLng end = new LatLng(Double.parseDouble(locationList.get(locationList.size() - 1).getLat()), Double.parseDouble(locationList.get(locationList.size() - 1).getLon()));

                //求两点间距离方法一：使用本地算法getDistance（）
                //double dis = Math.abs(getDistance(begin, end));


                //求两点间距离方法三：使用 android-maps-utils
            /*
            computeDistanceBetween
            public static double computeDistanceBetween(LatLng from, LatLng to)
            Returns the distance between two LatLngs, in meters.
           */
                double dis3 = computeDistanceBetween(begin, end);

                DecimalFormat df = new DecimalFormat("#.##");
                dis3 = Double.parseDouble(df.format(dis3));

                Log.d("Distence: ---", dis3 + "");

            }

            mPolylineOptions.width(12);
            mPolylineOptions.zIndex(100);
            mMap.addPolyline(mPolylineOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(locationList.get(0).getLat()), Double.parseDouble(locationList.get(0).getLon())), 17));
        }
    }

    private void initDatas(){
        //初始化数据列表
        locationList = new ArrayList<>();
        if(tableIsExist(MapContract.MapEntry.TABLE_NAME)) {
            queryFavoriteProduct();
            String res = "";
            for(MapLocation location: locationList){
                res += location.toString();
            }
            Log.d("位置列表数量", locationList.size() + "");
            Log.d("位置列表", res);
        }
    }

    private void queryFavoriteProduct() {
        String columns[] = new String[] {MapContract.MapEntry.COLUMN_DATE_TIME,
                MapContract.MapEntry.COLUMN_LAT,
                MapContract.MapEntry.COLUMN_LONG, };
        Uri myUri = MapContract.MapEntry.CONTENT_URI;
        //Cursor cur = FavoriteActivity.this.managedQuery(myUri, columns, null, null, null);
        Cursor cur = null;

        String orderbyTimeAsc = MapContract.MapEntry.COLUMN_DATE_TIME + " asc";
        cur = this.getContentResolver().query(myUri, columns, "substr(" + MapContract.MapEntry.COLUMN_DATE_TIME + ", 1, 10) = ? ", new String[]{date}, orderbyTimeAsc);

        if (cur.moveToFirst()) {
            String dateTime = null;
            String lat = null;
            String lon = null;

            do {
                dateTime = cur.getString(cur.getColumnIndex(MapContract.MapEntry.COLUMN_DATE_TIME));
                lat = cur.getString(cur.getColumnIndex(MapContract.MapEntry.COLUMN_LAT));
                lon = cur.getString(cur.getColumnIndex(MapContract.MapEntry.COLUMN_LONG));

                //Toast.makeText(this, id + ” ” + userName, Toast.LENGTH_LONG).show();
                MapLocation favo = new MapLocation();
                favo.setDateTime(dateTime);
                favo.setLat(lat);
                favo.setLon(lon);

                locationList.add(favo);
            } while (cur.moveToNext());


        }
    }

    /**
     * 判断某张表是否存在
     */
    public boolean tableIsExist(String tableName){
        boolean result = false;
        if(tableName == null){
            return false;
        }
        Cursor cursor = null;
        MapDbHelper dbHelper = new MapDbHelper(this);
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //这里表名可以是Sqlite_master
            String sql = "select count(*) as c from sqlite_master" + " where type ='table' and name ='"+tableName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private double getDistance(LatLng begin, LatLng end) {

        double PI = Math.PI; // 圆周率

        double R = 6371.229; // 地球的半径

        double x, y, distance;

        x = (end.longitude - begin.longitude)*PI*R* Math.cos(((begin.latitude+end.latitude)/2)*PI/180)/180;

        y = (end.latitude - begin.latitude)*PI*R/180;

        distance = Math.hypot(x, y);

        return distance;

    }

}
