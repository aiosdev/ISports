package com.aiosdev.isports;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.aiosdev.isports.data.MapContract;
import com.aiosdev.isports.data.MapDbHelper;
import com.aiosdev.isports.tools.MoveDetector;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MoveService extends Service implements SensorEventListener, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static Boolean FLAG = false;// service运行标志

    //服务service（与Acitivity）交互相关参数
    private SensorManager mSensorManager;// 传感器服务
    private MoveDetector detector;// 传感器监听对象

    private PowerManager mPowerManager;// 电源管理服务
    private PowerManager.WakeLock mWakeLock;// 屏幕灯

    //计步传感器相关参数
    public static int CURRENT_SETP = 0;
    public static float SENSITIVITY = 0;   //SENSITIVITY灵敏度
    private float mLastValues[] = new float[3 * 2];
    private float mScale[] = new float[2];
    private float mYOffset;
    private static long end = 0;
    private static long start = 0;

    //最后加速度方向
    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = {new float[3 * 2], new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;


    //GPS定位相关参数
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    //Service 与 Activity交互的相关参数
    private Messenger mActivityMessenger;

    private Messenger mServiceMessenger;


    private ArrayList<String> datelist;

    private Handler handler;

    private int taskNo;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mServiceMessenger.getBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();

        FLAG = true;// 标记为服务正在运行

        // 创建监听器类，实例化监听对象
        int h = 480;
        mYOffset = h * 0.5f;
        mScale[0] = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));
        mScale[1] = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
        //detector = new MoveDetector(this);

        HandlerThread handlerThread = new HandlerThread("serviceCalculate");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                switch (msg.what) {
                    case 0x11:
                        if (mActivityMessenger == null) {
                            mActivityMessenger = msg.replyTo;
                        }

                        //初始化任务编号
                        taskNo = msg.arg1;

                        // 获取传感器的服务，初始化传感器
                        mSensorManager = (SensorManager) MoveService.this.getSystemService(SENSOR_SERVICE);

                        // 注册传感器，注册监听器
                        mSensorManager.registerListener(MoveService.this,
                                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                                SensorManager.SENSOR_DELAY_FASTEST);

                        // 电源管理服务
                        mPowerManager = (PowerManager) MoveService.this
                                .getSystemService(Context.POWER_SERVICE);
                        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "S");
                        mWakeLock.acquire();

                        /*
                        //发送结果回Activity
                        Message message = this.obtainMessage();
                        message.what = 0x12;
                        message.arg1 = msg.arg1 + msg.arg2;
                        try {
                            mActivityMessenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        */

                        break;
                        /*
                    case 0x13:
                        Log.d("sjlfjs=========", msg.arg1 + "");

                        //发送结果回Activity
                        Message stepMessage = this.obtainMessage();
                        stepMessage.what = 0x14;
                        stepMessage.arg1 = msg.arg1;
                        try {
                            mActivityMessenger.send(stepMessage);

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        break;
                        */

                }

            }
        };
        mServiceMessenger = new Messenger(handler);

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

        FLAG = false;// 服务停止

        //注销计步器监听器
        if (this != null) {
            mSensorManager.unregisterListener(this);
        }

        if (mWakeLock != null) {
            mWakeLock.release();
        }

        //停止位置变化监听
        stopLocation();

        //结束Service，然而handler还在工作
        handler.removeCallbacksAndMessages(null);

        super.onDestroy();

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

            //计算距离
            //double dis3 = computeDistanceBetween(begin, end);


            //保存数据库
            ContentValues contentValues = new ContentValues();
            contentValues.put(MapContract.LoactionEntry.COLUMN_TASK_NO, taskNo);
            contentValues.put(MapContract.LoactionEntry.COLUMN_LAT, location.getLatitude());
            contentValues.put(MapContract.LoactionEntry.COLUMN_LONG, location.getLongitude());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
            contentValues.put(MapContract.LoactionEntry.COLUMN_DATE_TIME, df.format(new Date()));

            Uri url = MapContract.LoactionEntry.CONTENT_URI;
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
        String columns[] = new String[]{"distinct substr(" + MapContract.LoactionEntry.COLUMN_DATE_TIME + ", 1, 10) as date"};
        Uri myUri = MapContract.LoactionEntry.CONTENT_URI;
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
    public void onSensorChanged(SensorEvent event) {
        // Log.i(Constant.STEP_SERVER, "StepDetector");
        Sensor sensor = event.sensor;
        // Log.i(Constant.STEP_DETECTOR, "onSensorChanged");
        synchronized (this) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {//方位感应检测
            } else {
                int j = (sensor.getType() == Sensor.TYPE_ACCELEROMETER) ? 1 : 0;//加速度感应检测
                if (j == 1) {//加速度感应发生改变，则执行如下操作
                    float vSum = 0;
                    for (int i = 0; i < 3; i++) {
                        /*
                        * Accelerometer Sensor测量的是所有施加在设备上的力所产生的加速度的负值（包括重力加速度）。
						* 加速度所使用的单位是m/sec^2，数值是加速度的负值。
						* SensorEvent.values[0]：加速度在X轴的负值
						* SensorEvent.values[1]：加速度在Y轴的负值
						* SensorEvent.values[2]：加速度在Z轴的负值
						* 例如：当手机Z轴朝上平放在桌面上，并且从左到右推动手机，此时X轴上的加速度是正数。
						* 当手机Z轴朝上静止放在桌面上，此时Z轴的加速度是+9.81m/sec^2。
						*
						* */

                        final float v = mYOffset + event.values[i] * mScale[j];
                        vSum += v;
                    }
                    int k = 0;
                    float v = vSum / 3;

                    float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
                    if (direction == -mLastDirections[k]) {
                        // Direction changed
                        int extType = (direction > 0 ? 0 : 1); // minumum or
                        // maximum?
                        mLastExtremes[extType][k] = mLastValues[k];
                        float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                        if (diff > 3.0) {
                            boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                            boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                            boolean isNotContra = (mLastMatch != 1 - extType);

                            if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                                end = System.currentTimeMillis();
                                if (end - start > 500) {// 此时判断为走了一步
                                    //将数据发送到Handler
                                    //Message message = handler.obtainMessage();
                                    //message.what = 0x13;
                                    //message.arg1 = CURRENT_SETP;
                                    //message.sendToTarget();

                                    Message stepMessage = handler.obtainMessage();
                                    stepMessage.what = 0x14;
                                    stepMessage.arg1 = CURRENT_SETP;
                                    try {
                                        mActivityMessenger.send(stepMessage);

                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }


                                    Log.i("StepDetector", "CURRENT_SETP:"
                                            + CURRENT_SETP);
                                    CURRENT_SETP++;
                                    mLastMatch = extType;
                                    start = end;
                                }
                            } else {
                                mLastMatch = -1;
                            }
                        }
                        mLastDiff[k] = diff;
                    }
                    mLastDirections[k] = direction;
                    mLastValues[k] = v;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

}
