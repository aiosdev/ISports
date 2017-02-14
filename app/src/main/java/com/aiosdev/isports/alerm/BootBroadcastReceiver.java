package com.aiosdev.isports.alerm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.aiosdev.isports.MainActivity;
import com.aiosdev.isports.R;
import com.aiosdev.isports.data.User;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by loongggdroid on 2016/3/21.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    // 系统启动完成
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // 当收听到的事件是“BOOT_COMPLETED”时，就创建并启动相应的Activity和Service
        if (intent.getAction().equals(ACTION)) {
            // 开机启动的alerm clock
            User user = User.getInstence(context);
            if(1 == user.getAlerm()) {
                String[] times = user.getAlermTime().split(":");

                AlarmManagerUtil.setAlarm(context, 1, Integer.parseInt(times[0]), Integer.parseInt
                        (times[1]), 0, 0, "今天该运动了！", user.getAlermType());

                Log.d("iSport", "闹钟已经设置");

                simpleNotify(context, user);
            }
        }
    }

    private void simpleNotify(Context context, User user){

        NotificationManager manger = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //Ticker是状态栏显示的提示
        builder.setTicker("iSport clock reminder");
        //第一行内容  通常作为通知栏标题
        builder.setContentTitle("iSport");
        //第二行内容 通常是通知正文
        builder.setContentText("iSport clock reminder is setting ( " + user.getAlermTime() + " )");
        //第三行内容 通常是内容摘要什么的 在低版本机器上不一定显示
        //builder.setSubText("这里显示的是通知第三行内容！");
        //ContentInfo 在通知的右侧 时间的下面 用来展示一些其他信息
        //builder.setContentInfo("2");
        builder.setAutoCancel(true);
        //builder.setNumber(2);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.bug));
        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context,1,intent,0);
        builder.setContentIntent(pIntent);
        //设置震动
        //long[] vibrate = {100,200,100,200};
        //builder.setVibrate(vibrate);

        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        Notification notification = builder.build();
        int TYPE_Normal = 1;
        manger.notify(TYPE_Normal,notification);
    }

}
