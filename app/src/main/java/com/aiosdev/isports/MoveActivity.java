package com.aiosdev.isports;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MoveActivity extends AppCompatActivity implements View.OnClickListener {

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
    private Button btStop;
    private Button btMap;

    private Button btGetMap;

    private ArrayList<String> datelist;

    final static int STATUS_STOP = 10;  //停止状态,就绪状态
    final static int STATUS_PAUSE = 20; //开始后的暂停状态
    final static int STATUS_RESUME = 30;//开始后的继续状态
    final static int STATUS_DISABLE = 40; //不可用状态



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);

        initView(); //初始化View

        initListener(); //初始化按钮监听器

        //设置图片
        setMoveImage();

        //初始化运行状态及按钮状态
        setActionStatus(STATUS_STOP);



    }

    private void initListener() {
        btStart.setOnClickListener(this);
        btPause.setOnClickListener(this);
        btStop.setOnClickListener(this);
        btMap.setOnClickListener(this);
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
        btStop = (Button) findViewById(R.id.move_bt_stop);
        btMap = (Button) findViewById(R.id.move_bt_map);
        //设置运行状态为不可用状态，且按钮为不可用状态
        setActionStatus(STATUS_DISABLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("dataset", btStatus);
        Log.d("btStatus状态保存：", btStatus + "");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("btStatus初始化:", btStatus + "");

        //设置图片
        setMoveImage();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        //恢复btStatus状态，并设置按钮状态
        int statusTemp = (Integer) savedInstanceState.getSerializable("dataset");
        Log.d("btStatus状态恢复：", statusTemp + "");
        setActionStatus(statusTemp);

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
    public void onClick(View view) {
        //初始化service
        Intent moveService =  new Intent(this, MoveService.class);
        switch (view.getId()){
            case R.id.move_bt_start:
                //进入resume状态
                setActionStatus(STATUS_RESUME);
                //setBtStatus(false, true, false, true);
                //btStatus = STATUS_RESUME;
                Toast.makeText(getApplicationContext(), "resume", Toast.LENGTH_SHORT).show();

                //启动MoveService
                startService(moveService);
                Log.d("service服务", "启动");

                //启动计时器
                timer.setBase(SystemClock.elapsedRealtime());//计时器清零
                int hour = (int) ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000 / 60);
                timer.setFormat("0"+String.valueOf(hour)+":%s");
                timer.start();
                break;
            case R.id.move_bt_pause:

                if(btStatus == STATUS_STOP || btStatus == STATUS_RESUME){
                    //进入pause状态
                    setActionStatus(STATUS_PAUSE);
                    Toast.makeText(getApplicationContext(), "pause", Toast.LENGTH_SHORT).show();
                }else if(btStatus == STATUS_PAUSE){
                    //进入resume状态
                    setActionStatus(STATUS_RESUME);
                    Toast.makeText(getApplicationContext(), "resume", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.move_bt_stop:
                //进入stop状态
                setActionStatus(STATUS_STOP);
                //setBtStatus(true, false, false, false);
                //btStatus = STATUS_STOP;
                Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_SHORT).show();

                //停止MoveService
                stopService(moveService);
                Log.d("service服务", "停止");

                //关闭timer
                timer.stop();
                break;
            case R.id.move_bt_map:
                startActivity(new Intent(MoveActivity.this, MoveMapsActivity.class));
        }
    }

    private void setActionStatus(int status){

        //设置运行状态
        btStatus = status;
        Log.d("btStatus Action:", btStatus + "");
        //设置按钮状态
        switch (status){
            case STATUS_RESUME:
                setBtStatus(false, true, false, true);
                break;
            case STATUS_PAUSE:
                setBtStatus(false, false, true, true);
                break;
            case STATUS_STOP:
                setBtStatus(true, false, false, false);
                break;
            case STATUS_DISABLE:
                setBtStatus(false, false, false, false);
                break;
            default:
                setBtStatus(true, false, false, false);
                break;
        }
    }

    private void setBtStatus(boolean start, boolean pause, boolean resume, boolean stop){

        btStart.setEnabled(start);
        btStop.setEnabled(stop);
        if(pause && !resume){
            btPause.setEnabled(pause);
            btPause.setText("暂停");
        }else if(!pause && resume){
            btPause.setEnabled(resume);
            btPause.setText("继续");
        }else if(!pause && !resume){
            btPause.setEnabled(pause);
            btPause.setText("暂停");
        }


    }
}
