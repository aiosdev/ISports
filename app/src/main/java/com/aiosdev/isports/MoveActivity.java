package com.aiosdev.isports;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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

import com.aiosdev.isports.data.Location;
import com.aiosdev.isports.data.MapContract;
import com.aiosdev.isports.data.Task;
import com.aiosdev.isports.data.User;
import com.aiosdev.isports.tools.MoveDetector;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.R.attr.data;


public class MoveActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    private int btStatus;

    private ImageView ivMove;
    private TextView cityName;
    private ImageView weatherImage;
    private TextView weatherTemp;

    private Chronometer chronTimer;
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

    private int sportTimer = 0;// 运动时间
    private long startTimer = 0;// 开始时间

    private long tempTime = 0;

    private Float distance = Float.valueOf(0);// 路程：米
    private Float calories = Float.valueOf(0);// 热量：卡路里
    private Float velocity = Float.valueOf(0);// 速度：米每秒

    private int step_length = 0;  //步长
    private int weight = 0;       //体重
    private int total_step = 0;   //走的总步数

    final static int STATUS_STOP = 10;  //停止状态,就绪状态
    final static int STATUS_PAUSE = 20; //开始后的暂停状态
    final static int STATUS_RESUME = 30;//开始后的继续状态
    final static int STATUS_DISABLE = 40; //不可用状态

    private int taskNo;  //任务编号
    private String currentTaskDate;//当前任务日期

    //Service端的Messenger对象
    private Messenger mServiceMessenger;
    //Activity端的Messenger对象
    private Messenger mActivityMessenger;

    private User user;


    /**
     * Activity端的Handler处理Service中的消息
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 0x12:
                    Toast.makeText(MoveActivity.this, "Service发送过来的结果是......"
                            + msg.arg1, Toast.LENGTH_SHORT).show();
                    break;
                case 0x14:
                    //刷新步数统计
                    MoveActivity.this.paceCount.setText(msg.arg1 + "");
                    total_step = msg.arg1;

                    //保留小数点后两位
                    DecimalFormat df = new DecimalFormat("#.##");

                    //刷新距离统计
                    int avgStep = user.getAvgStep();
                    if(avgStep == 0){
                        avgStep = 60;
                    }
                    distance = Float.valueOf(msg.arg1 * 60 /100);
                    distance = Float.parseFloat(df.format(distance));
                    MoveActivity.this.paceDistance.setText(distance + "米");

                    //刷新热量统计
                    calories = Float.parseFloat(String.valueOf(50 * distance * 0.8214 / 1000));
                    calories = Float.parseFloat(df.format(calories));
                    MoveActivity.this.paceCalories.setText(calories + "卡");

                    //刷新平均速度

                    velocity = distance / sportTimer;
                    velocity = Float.parseFloat(df.format(velocity));
                    MoveActivity.this.paceSpeedAvg.setText(velocity + "米/秒");

                    break;
            }

        }
    };

    /**
     * Service绑定状态的监听
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取Service端的Messenger
            mServiceMessenger = new Messenger(service);

            //开始计步
            //Activity端的Messenger
            if (mActivityMessenger == null) {
                mActivityMessenger = new Messenger(handler);
            }

            //创建消息
            Message message = Message.obtain();
            message.what = 0x11;
            message.arg1 = taskNo;
            message.arg2 = 1;

            //设定消息要回应的Messenger
            message.replyTo = mActivityMessenger;

            try {
                //通过ServiceMessenger将消息发送到Service中的Handler
                mServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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

        //btStart.setEnabled(false);
        user = User.getInstence(MoveActivity.this);

        //设置地图按钮不可用
        btMap.setEnabled(false);
    }

    private void initTaskNo() {
        queryNewTaskNoByCurrentDate();
        taskNo++;
        paceSpeedLow.setText(taskNo + "");
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

        //chronTimer
        chronTimer = (Chronometer) findViewById(R.id.chronometer_timer);

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

        if (timeTemp.compareTo("05") >= 0 && timeTemp.compareTo("12") < 0) {
            Picasso.with(this).load(R.mipmap.move_morning).into(ivMove);
        } else if (timeTemp.compareTo("12") >= 0 && timeTemp.compareTo("17") < 0) {
            Picasso.with(this).load(R.mipmap.move_afternoon).into(ivMove);
        } else {
            Picasso.with(this).load(R.mipmap.move_evening).into(ivMove);
        }
    }

    @Override
    public void onClick(View view) {
        //初始化service
        Intent moveService = new Intent(MoveActivity.this, MoveService.class);
        switch (view.getId()) {
            case R.id.move_bt_start:
                //设置当前任务日期
                //取当前日期
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                currentTaskDate = df.format(new Date()).substring(0, 10);

                initTaskNo();//初始化当天任务编号

                //进入resume状态
                setActionStatus(STATUS_RESUME);
                //setBtStatus(false, true, false, true);
                //btStatus = STATUS_RESUME;
                Toast.makeText(getApplicationContext(), "resume", Toast.LENGTH_SHORT).show();


                //启动MoveService
                //startService(moveService);
                //Log.d("service服务", "启动");

                //绑定Service
                //Intent intent = new Intent(MoveActivity.this, MoveService.class);
                bindService(moveService, connection, Service.BIND_AUTO_CREATE);
                Log.d("service服务", "启动");

                //启动计时器
                sportTimer = 0;
                chronTimer.setBase(SystemClock.elapsedRealtime());//计时器清零
                int hour = (int) ((SystemClock.elapsedRealtime() - chronTimer.getBase()) / 1000 / 60);
                //chronTimer.setFormat("0" + String.valueOf(hour) + ":%s");
                chronTimer.start();
                chronTimer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                    @Override
                    public void onChronometerTick(Chronometer chronometer) {
                        sportTimer++;
                        chronTimer.setText(FormatMiss(sportTimer));
                    }

                    // 将秒转化成小时分钟秒
                    public String FormatMiss(int miss){
                        String hh=miss/3600>9?miss/3600+"":"0"+miss/3600;
                        String  mm=(miss % 3600)/60>9?(miss % 3600)/60+"":"0"+(miss % 3600)/60;
                        String ss=(miss % 3600) % 60>9?(miss % 3600) % 60+"":"0"+(miss % 3600) % 60;
                        return hh+":"+mm+":"+ss;
                    }
                });

                //设置地图按钮不可用
                btMap.setEnabled(false);

                break;
            case R.id.move_bt_pause:

                if (btStatus == STATUS_STOP || btStatus == STATUS_RESUME) {
                    //进入pause状态
                    setActionStatus(STATUS_PAUSE);
                    Toast.makeText(getApplicationContext(), "pause", Toast.LENGTH_SHORT).show();
                } else if (btStatus == STATUS_PAUSE) {
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
                //stopService(moveService);


                //保存数据库表Task
                ContentValues contentValues = new ContentValues();
                contentValues.put(MapContract.TaskEntry.COLUMN_DATE, currentTaskDate);
                contentValues.put(MapContract.TaskEntry.COLUMN_TASK_NO, taskNo);
                contentValues.put(MapContract.TaskEntry.COLUMN_STEP, total_step);
                contentValues.put(MapContract.TaskEntry.COLUMN_DISTANCE, distance);
                contentValues.put(MapContract.TaskEntry.COLUMN_CALORIES, calories);
                contentValues.put(MapContract.TaskEntry.COLUMN_DURATION, sportTimer);
                contentValues.put(MapContract.TaskEntry.COLUMN_AVG_SPEED, velocity);
                Uri url = MapContract.TaskEntry.CONTENT_URI;
                getContentResolver().insert(url, contentValues);

                //累加数据

                user.setTotalStep(user.getTotalStep() + total_step);
                user.setTotalDistance(user.getTotalDistance() + distance);
                user.setTotalCalories(user.getTotalCalories() + calories);
                user.setTotalDuration(user.getTotalDuration() + sportTimer);
                user.setAvgStep((int) (user.getTotalDistance() / user.getTotalStep() * 100));


                //解除service的绑定
                unbindService(connection);

                Log.d("service服务", "停止");

                //关闭timer
                chronTimer.stop();

                //地图按钮可用
                btMap.setEnabled(true);

                //判断等级和头衔的变化
                String[] strGrade = {"初级", "初级", "初级", "初级", "初级", "中级", "中级", "高级", "高级", "高级", "高级",
                        "运动专家", "运动专家", "运动专家", "资深运动专家", "资深运动专家", "资深运动专家", "资深运动专家"};
                String[] strTitle = {"小白", "列兵", "一等兵", "二等兵", "三等兵", "少尉", "中尉", "上尉",
                        "少校", "中校", "上校", "少将", "中将", "上将", "四星上将", "五星上将", "元帅", "奥运冠军"};
                int[] intSteps = {0, 1000 , 2000, 4000, 8000, 24000, 60000, 120000, 240000, 360000, 500000,
                        650000, 820000, 1000000, 1250000, 1600000, 2000000, 3000000};

                String gradeAfterStep = "";
                String titleAfterStep = "";

                //user.setTotalStep(820000);

                for(int i=0;i<intSteps.length;i++){
                    if(i == intSteps.length){
                        if(user.getTotalStep() > intSteps[i]){
                            gradeAfterStep = strGrade[i];
                            titleAfterStep = strTitle[i];
                            if(!titleAfterStep.equals(user.getTitle())){
                                Toast.makeText(this, "恭喜获得新头衔！", Toast.LENGTH_LONG).show();
                            }
                            if(!gradeAfterStep.equals(user.getGrade())){
                                Toast.makeText(this, "恭喜升级！", Toast.LENGTH_LONG).show();
                            }
                            user.setGrade(gradeAfterStep);
                            user.setTitle(titleAfterStep);
                        }
                    }
                    if(user.getTotalStep() >= intSteps[i] && user.getTotalStep() < intSteps[i + 1]){
                        gradeAfterStep = strGrade[i];
                        titleAfterStep = strTitle[i];
                        if(!titleAfterStep.equals(user.getTitle())){
                            Toast.makeText(this, "恭喜获得新头衔！", Toast.LENGTH_LONG).show();
                        }
                        if(!gradeAfterStep.equals(user.getGrade())){
                            Toast.makeText(this, "恭喜升级！", Toast.LENGTH_LONG).show();
                        }
                        user.setGrade(gradeAfterStep);
                        user.setTitle(titleAfterStep);
                    }
                }

                //保存数据到SharedPreferences “userInfo”
                user.saveData(MoveActivity.this);

                break;
            case R.id.move_bt_map:
                Task task = new Task();
                task.setDate(currentTaskDate);
                task.setTaskNo(taskNo + "");
                task.setStep(total_step);
                task.setDistance(distance);
                task.setCalories(calories);
                task.setDuration(sportTimer);
                task.setAvg_speed(velocity);

                Intent intent = new Intent();
                intent.putExtra("flag", "MoveActivity");
                Bundle bundle = new Bundle();
                bundle.putSerializable("task", task);
                intent.putExtras(bundle);
                intent.setClass(MoveActivity.this, MoveMapsActivity.class);
                startActivity(intent);
        }
    }

    private void setActionStatus(int status) {

        //设置运行状态
        btStatus = status;
        Log.d("btStatus Action:", btStatus + "");
        //设置按钮状态
        switch (status) {
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

    private void setBtStatus(boolean start, boolean pause, boolean resume, boolean stop) {

        btStart.setEnabled(start);
        btStop.setEnabled(stop);
        if (pause && !resume) {
            btPause.setEnabled(pause);
            btPause.setText("暂停");
        } else if (!pause && resume) {
            btPause.setEnabled(resume);
            btPause.setText("继续");
        } else if (!pause && !resume) {
            btPause.setEnabled(pause);
            btPause.setText("暂停");
        }
    }

    /**
     * 实际的步数
     */
    private void countStep() {
        if (MoveDetector.CURRENT_SETP % 2 == 0) {
            total_step = MoveDetector.CURRENT_SETP;
        } else {
            total_step = MoveDetector.CURRENT_SETP + 1;
        }

        total_step = MoveDetector.CURRENT_SETP;
    }

    private void queryNewTaskNoByCurrentDate() {

        //提取数据
        String columns[] = new String[]{"max(cast(" + MapContract.TaskEntry.COLUMN_TASK_NO + " as integer)) as max_task_no",};
        String columns1[] = new String[]{"count(*)",};
        Uri myUri = MapContract.TaskEntry.CONTENT_URI;
        //Cursor cur = FavoriteActivity.this.managedQuery(myUri, columns, null, null, null);
        Cursor cur = null;

        //String orderbyTimeAsc = MapContract.LoactionEntry.COLUMN_DATE_TIME + " asc";
        cur = this.getContentResolver().query(myUri, columns, MapContract.TaskEntry.COLUMN_DATE + " = ? ", new String[]{currentTaskDate}, null);

        if (cur.moveToFirst()) {
            String taskMaxNo = null;

            do {
                taskMaxNo = cur.getString(cur.getColumnIndex("max_task_no"));
                if (taskMaxNo == null) {
                    taskNo = 0;
                } else {
                    taskNo = Integer.parseInt(taskMaxNo);
                }
            } while (cur.moveToNext());
        }


    }

}
