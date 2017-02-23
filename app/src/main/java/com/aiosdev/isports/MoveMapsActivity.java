package com.aiosdev.isports;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.aiosdev.isports.data.MapContract;
import com.aiosdev.isports.data.MapDbHelper;
import com.aiosdev.isports.data.Location;
import com.aiosdev.isports.data.Task;
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

public class MoveMapsActivity extends FragmentActivity implements  OnMapReadyCallback {

    private GoogleMap mMap;

    private ArrayList<Location> locationList;

    private PolylineOptions mPolylineOptions;

    private String date;
    private String taskNo;

    private Task mTask;

    private TextView tvDate;
    private TextView tvTaskNo;
    private TextView tvDuration;
    private TextView tvStartTime;
    //private TextView tvStopTime;
    private TextView tvSteps;
    private TextView tvDistances;
    private TextView tvCalories;
    private TextView tvVelocity;
    private TextView tvAvgStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_maps);

        Intent intent = getIntent();
        if("MoveActivity".equals(intent.getStringExtra("flag"))){
            mTask = (Task) intent.getSerializableExtra("task");
            date = mTask.getDate();
            taskNo = mTask.getTaskNo();
        }else if("FragmentTab5".equals(intent.getStringExtra("flag"))){
            date = intent.getStringExtra("date");
            taskNo = intent.getStringExtra("taskNo");
            queryTaskByDate(date, taskNo);
        }

        initViews();

        initDatas();

        initViewDatas();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    private void initViews() {
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTaskNo = (TextView) findViewById(R.id.tv_task_no);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        tvStartTime = (TextView) findViewById(R.id.tv_start_time);
        //tvStopTime = (TextView) findViewById(R.id.tv_stop_time);
        tvSteps = (TextView) findViewById(R.id.tv_steps);
        tvDistances = (TextView) findViewById(R.id.tv_distances);
        tvCalories = (TextView) findViewById(R.id.tv_calories);
        tvAvgStep = (TextView) findViewById(R.id.tv_avg_step);
        tvVelocity = (TextView) findViewById(R.id.tv_velocity);
    }

    private void initViewDatas() {
        if(mTask == null){
            Toast.makeText(this, "数据提取异常，请重试！", Toast.LENGTH_SHORT).show();
        }else {
            tvDate.setText(mTask.getDate());
            tvTaskNo.setText(mTask.getTaskNo());
            tvDuration.setText(mTask.getDuration() + "秒");
            if(!locationList.isEmpty()) {
                tvStartTime.setText(locationList.get(0).getDateTime().substring(11, 19));
            }
            //tvStopTime.setText(locationList.get(locationList.size() - 1).getDateTime());
            tvSteps.setText(mTask.getStep() + " 步");
            tvDistances.setText(mTask.getDistance() + " 米");
            tvCalories.setText(mTask.getCalories() + " 卡");
            tvAvgStep.setText(mTask.getAvg_step() + " 厘米");
            tvVelocity.setText(mTask.getAvg_speed() + " 米/秒");
        }
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


        if (locationList.isEmpty()) {
            Toast.makeText(this, "暂时没有位置数据！", Toast.LENGTH_SHORT).show();
        } else {

            mPolylineOptions = new PolylineOptions();

            for (Location location : locationList) {
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
                //double dis3 = computeDistanceBetween(begin, end);

                //DecimalFormat df = new DecimalFormat("#.##");
                //dis3 = Double.parseDouble(df.format(dis3));

                //Log.d("Distence: ---", dis3 + "");

            }

            mPolylineOptions.width(12);
            mPolylineOptions.zIndex(100);
            mMap.addPolyline(mPolylineOptions);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(locationList.get(0).getLat()), Double.parseDouble(locationList.get(0).getLon())), 17));

        }

    }

    private void initDatas() {
        //初始化数据列表
        locationList = new ArrayList<>();
        if (tableIsExist(MapContract.LoactionEntry.TABLE_NAME)) {
            queryLocByCurrentDate();
            String res = "";
            for (Location location : locationList) {
                res += location.toString();
            }
            Log.d("位置列表数量", locationList.size() + "");
            Log.d("位置列表", res);
        }
    }

    private void queryLocByCurrentDate() {
        String columns[] = new String[]{
                MapContract.LoactionEntry.COLUMN_DATE_TIME,
                MapContract.LoactionEntry.COLUMN_LAT,
                MapContract.LoactionEntry.COLUMN_LONG,};
        Uri myUri = MapContract.LoactionEntry.CONTENT_URI;
        //Cursor cur = FavoriteActivity.this.managedQuery(myUri, columns, null, null, null);
        Cursor cur = null;

        String orderbyTimeAsc = MapContract.LoactionEntry.COLUMN_DATE_TIME + " asc";

        //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        //date = df.format(new Date()).substring(0, 10);
        //String[] args = {date, mTask.getTaskNo()};

        cur = this.getContentResolver().query(myUri, columns, "substr(" + MapContract.LoactionEntry.COLUMN_DATE_TIME + ", 1, 10) = ? and " + MapContract.LoactionEntry.COLUMN_TASK_NO + " = ?", new String[]{date, taskNo}, orderbyTimeAsc);

        if (cur.moveToFirst()) {
            //String taskNo = null;
            String dateTime = null;
            String lat = null;
            String lon = null;

            do {
                //taskNo = cur.getString(cur.getColumnIndex(MapContract.LoactionEntry.COLUMN_TASK_NO));
                dateTime = cur.getString(cur.getColumnIndex(MapContract.LoactionEntry.COLUMN_DATE_TIME));
                lat = cur.getString(cur.getColumnIndex(MapContract.LoactionEntry.COLUMN_LAT));
                lon = cur.getString(cur.getColumnIndex(MapContract.LoactionEntry.COLUMN_LONG));

                //Toast.makeText(this, id + ” ” + userName, Toast.LENGTH_LONG).show();
                Location favo = new Location();
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

    private double getDistance(LatLng begin, LatLng end) {

        double PI = Math.PI; // 圆周率

        double R = 6371.229; // 地球的半径

        double x, y, distance;

        x = (end.longitude - begin.longitude) * PI * R * Math.cos(((begin.latitude + end.latitude) / 2) * PI / 180) / 180;

        y = (end.latitude - begin.latitude) * PI * R / 180;

        distance = Math.hypot(x, y);

        return distance;

    }

    private void queryTaskByDate(String date, String taskNo) {

        Uri myUri = MapContract.TaskEntry.CONTENT_URI;
        //Cursor cur = FavoriteActivity.this.managedQuery(myUri, columns, null, null, null);
        Cursor cur = null;
        String condition = "date = ? and task_no = ?";
        String[] argus = {date, taskNo};

        cur = this.getContentResolver().query(myUri, null, condition, argus, null);

        if (cur.moveToFirst()) {
            do {
                mTask = new Task();
                mTask.setDate(cur.getString(cur.getColumnIndex("date"))); ;
                mTask.setTaskNo(cur.getString(cur.getColumnIndex("task_no")));
                mTask.setStep(cur.getInt(cur.getColumnIndex("step")));
                mTask.setDistance(cur.getFloat(cur.getColumnIndex("distance")));
                mTask.setCalories(cur.getFloat(cur.getColumnIndex("calories")));
                mTask.setDuration(cur.getInt(cur.getColumnIndex("duration")));
                mTask.setAvg_step(cur.getInt(cur.getColumnIndex("avg_step")));
                mTask.setAvg_speed(cur.getFloat(cur.getColumnIndex("avg_speed")));
            } while (cur.moveToNext());
        }
    }


}
