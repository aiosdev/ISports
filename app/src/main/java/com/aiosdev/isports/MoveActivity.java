package com.aiosdev.isports;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aiosdev.isports.tabmain.data.MapContract;
import com.aiosdev.isports.tabmain.data.MapDbHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MoveActivity extends AppCompatActivity implements View.OnClickListener,com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    private int btStatus;

    private ImageView ivMove;
    private TextView cityName;
    private ImageView weatherImage;
    private TextView weatherTemp;

    private Chronometer timer;
    private TextView paceCount;
    private TextView paceDistance;
    private TextView paceCalories;
    private TextView paceSpeedAvg;
    private TextView paceSpeedHeigh;
    private TextView paceSpeedLow;

    private Button btStart;
    private Button btPause;
    private Button btResume;
    private Button btStop;

    private Button btGetMap;

    private ArrayList<String> datelist;

    final static int STATUS_STOP = 10;  //停止状态
    final static int STATUS_PAUSE = 20; //开始后的暂停状态
    final static int STATUS_RESUME = 30;//开始后的继续状态



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);

        initView(); //初始化View

        initListener(); //初始化按钮监听器

        //设置图片
        setMoveImage();



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

    private void initListener() {
        btStart.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btResume.setOnClickListener(this);
        btStop.setOnClickListener(this);
    }

    private void initView() {

        //image
        ivMove = (ImageView) findViewById(R.id.move_image);
        weatherImage = (ImageView) findViewById(R.id.move_weather_image);
        weatherTemp = (TextView) findViewById(R.id.move_weather_temp);
        cityName = (TextView) findViewById(R.id.move_city_name);

        //timer
        timer = (Chronometer) findViewById(R.id.chronometer_timer);

        paceCount = (TextView) findViewById(R.id.move_pace_count);
        paceDistance = (TextView) findViewById(R.id.move_pace_distance);
        paceCalories = (TextView) findViewById(R.id.move_pace_calories);
        paceSpeedAvg = (TextView) findViewById(R.id.move_pace_speed_average);
        paceSpeedHeigh = (TextView) findViewById(R.id.move_pace_speed_heigh);
        paceSpeedLow = (TextView) findViewById(R.id.move_pace_speed_low);



        //Button
        btStart = (Button) findViewById(R.id.move_bt_start);
        btPause = (Button) findViewById(R.id.move_bt_pause);
        btPause.setEnabled(false);
        btResume = (Button) findViewById(R.id.move_bt_resume);
        btResume.setEnabled(false);
        btStop = (Button) findViewById(R.id.move_bt_stop);
        btStop.setEnabled(false);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("dataset", btStatus);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("btStatus:", btStatus + "");

        //设置图片
        setMoveImage();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //恢复btStatus状态
        btStatus = (Integer) savedInstanceState.getSerializable("dataset");

        //设置按钮状态
        switch (btStatus){
            case STATUS_RESUME:
                setBtEnable(false, true, false, true);
                break;
            case STATUS_PAUSE:
                setBtEnable(false, false, true, true);
                break;
            case STATUS_STOP:
                setBtEnable(true, false, false, false);
                break;
            default:
                setBtEnable(true, false, false, false);
                break;
        }

        //设置图片
        setMoveImage();


    }

    private void setMoveImage() {
        //设置图片
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String timeTemp = df.format(new Date());
        //Log.d("timeTemp: ", timeTemp);
        timeTemp = timeTemp.substring(11, 13);

        if(timeTemp.compareTo("05") >= 0 &&  timeTemp.compareTo("12") < 0){
            Picasso.with(this).load(R.mipmap.move_morning).into(ivMove);
        }else if(timeTemp.compareTo("12") >= 0 &&  timeTemp.compareTo("17") < 0){
            Picasso.with(this).load(R.mipmap.move_afternoon).into(ivMove);
        }else {
            Picasso.with(this).load(R.mipmap.move_evening).into(ivMove);
        }
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
        /*
        //创建完成后开始定位
        startLocation();
        */

        //设置监听器按钮可用状态
        //btStatus = true;
        //btStart.setEnabled(true);

        Toast.makeText(this, "GPS链接成功！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Toast.makeText(this, "GPS链接失败！", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        /*
        if (location != null) {
            System.out.println("---测试mylocation---" + "lat = " + location.getLatitude() + "; lon = " + location.getLongitude());

            //保存数据库
            ContentValues contentValues = new ContentValues();
            contentValues.put(MapContract.MapEntry.COLUMN_LAT, location.getLatitude());
            contentValues.put(MapContract.MapEntry.COLUMN_LONG, location.getLongitude());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
            contentValues.put(MapContract.MapEntry.COLUMN_DATE_TIME, df.format(new Date()));

            Uri url = MapContract.MapEntry.CONTENT_URI;
            getContentResolver().insert(url, contentValues);
            Toast.makeText(getApplicationContext(), "成功记录一条信息：lat= " + location.getLatitude() + "; lon= " + location.getLongitude(), Toast.LENGTH_LONG).show();

        } else {
            System.out.println("---测试mylocation---location为null");
            Toast.makeText(getApplicationContext(), "location为null", Toast.LENGTH_SHORT).show();
        }
        */
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setSmallestDisplacement(0.5F);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void updateToNewLocation(Location location) {
        Toast.makeText(this, "位置变化：lat= " + location.getLatitude() + "; lon= " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    public void startLocation() {
        Log.d("mGoogleApiClient是否链接:", mGoogleApiClient.isConnected() + "");
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        }
    }

    public void stopLocation() {
        Log.d("mGoogleApiClient是否链接:", mGoogleApiClient.isConnected() + "");
        if (mGoogleApiClient.isConnected()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    private void queryPointByDate() {
        datelist.clear();
        String columns[] = new String[] {"distinct substr(" + MapContract.MapEntry.COLUMN_DATE_TIME + ", 1, 10) as date"};
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.move_bt_start:
                setBtEnable(false, true, false, true);
                btStatus = STATUS_RESUME; //进入resume状态
                Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
                break;
            case R.id.move_bt_pause:
                setBtEnable(false, false, true, true);
                btStatus = STATUS_PAUSE; //进入pause状态
                Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
                break;
            case R.id.move_bt_resume:
                setBtEnable(false, true, false, true);
                btStatus = STATUS_RESUME; //进入resume状态
                Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
                break;
            case R.id.move_bt_stop:
                setBtEnable(true, false, false, false);
                btStatus = STATUS_STOP; //进入stop状态
                Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setBtEnable(boolean start, boolean pause, boolean resume, boolean stop){
        btStart.setEnabled(start);
        btPause.setEnabled(pause);
        btResume.setEnabled(resume);
        btStop.setEnabled(stop);
    }
}
