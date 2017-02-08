package com.aiosdev.isports;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aiosdev.isports.data.User;

public class RegisterActivity extends AppCompatActivity {

    private SeekBar sbPaceLength;
    private TextView tvSb1;

    private SeekBar sbPara;
    private TextView tvSb2;

    private SeekBar sbWeight;
    private TextView tvSb3;

    private SeekBar sbPlan;
    private TextView tvSb4;

    private TextView tvName;
    private Spinner spinnerSex;
    private TextView tvWeight;

    private Button btCommit;
    private Button btReset;


    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvName = (EditText) findViewById(R.id.tv_name);
        spinnerSex = (Spinner) findViewById(R.id.spinner_sex);
        btCommit = (Button) findViewById(R.id.bt_register_commit);
        btReset = (Button) findViewById(R.id.bt_register_reset);

        //初始步幅设定
        sbPaceLength = (SeekBar) findViewById(R.id.sb_pace_length);
        sbPaceLength.setProgress(60);
        tvSb1 = (TextView) findViewById(R.id.tv_sb1_value);
        tvSb1.setText(sbPaceLength.getProgress() + "厘米");

        //灵敏度设定
        sbPara = (SeekBar) findViewById(R.id.sb_para);
        sbPara.setProgress(3);
        tvSb2 = (TextView) findViewById(R.id.tv_sb2_value);
        tvSb2.setText(sbPara.getProgress() + "");

        //体重设定
        sbWeight = (SeekBar) findViewById(R.id.sb_weight);
        sbWeight.setProgress(50);
        tvSb3 = (TextView) findViewById(R.id.tv_sb3_value);
        tvSb3.setText(sbWeight.getProgress() + "公斤");

        //计划设定
        sbPlan = (SeekBar) findViewById(R.id.sb_plan);
        sbPlan.setProgress(5000);
        tvSb4 = (TextView) findViewById(R.id.tv_sb4_value);
        tvSb4.setText(sbPlan.getProgress() + "步");


        sbPaceLength.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int i, boolean b) {
                tvSb1.setText(i + "厘米");
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {

            }
        });

        sbPara.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int i, boolean b) {
                tvSb2.setText(i + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {

            }
        });

        sbWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int i, boolean b) {
                tvSb3.setText(i + "公斤");
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {

            }
        });

        sbPlan.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int i, boolean b) {
                tvSb4.setText(i + "步");
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {

            }
        });

        btCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //设定初次登录标志
                SharedPreferences sharePreference = getSharedPreferences("init", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorInit = sharePreference.edit();
                editorInit.putBoolean("first", true);
                editorInit.commit();


                User user = User.getInstence(RegisterActivity.this);
                user.setName(tvName.getText().toString().trim());
                user.setSex(spinnerSex.getSelectedItem().toString().trim());
                user.setWeight(sbWeight.getProgress());
                user.setAvgStep(sbPaceLength.getProgress());
                user.setSensitivity(sbPara.getProgress());
                user.setGrade("初级");
                user.setTitle("列兵");
                user.setStepCount(sbPlan.getProgress());
                user.setTotalStep(0);
                user.setTotalDistance(Float.parseFloat("0"));
                user.setTotalCalories(Float.parseFloat("0"));
                user.setTotalDuration(0);

                user.saveData(RegisterActivity.this);
                /*
                //设定sharerpeference
                mPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("name", tvName.getText().toString().trim());
                editor.putString("sex", spinnerSex.getSelectedItem().toString().trim());
                editor.putInt("weight", sbWeight.getProgress());
                editor.putInt("pace_length", sbPaceLength.getProgress());
                editor.putInt("sensitivity", sbPara.getProgress());
                editor.putString("grade", "初级");
                editor.putString("title", "列兵");
                editor.putInt("step_count_plan", sbPlan.getProgress()); //步
                editor.putInt("total_step", 0);          //步
                editor.putFloat("total_distance", 0);  //公里
                editor.putFloat("total_calories", 0);  //卡路里
                editor.putInt("total_duration", 0);  //分钟
                editor.putFloat("avg_speed", 0);       //米/秒
                editor.commit();
                */
                Toast.makeText(getApplicationContext(), "信息设定完成，准备开始运动之旅!", Toast.LENGTH_SHORT).show();
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {

                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                 finish();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            }
                        });
                    }
                }.start();
            }
        });

        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设定sharerpeference
                mPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                Log.d("name:", mPreferences.getString("name", ""));
                Log.d("sex:", mPreferences.getString("sex", ""));
                Log.d("weight:", String.valueOf(mPreferences.getInt("weight", 0)));
                Log.d("pace_length:", String.valueOf(mPreferences.getInt("pace_length", 0)));
                Log.d("sensitivity:", String.valueOf(mPreferences.getInt("sensitivity", 0)));
                Log.d("grade:", mPreferences.getString("grade", ""));
                Log.d("title:", mPreferences.getString("title", ""));
                Log.d("step_count_plan:", String.valueOf(mPreferences.getInt("step_count_plan", 0)));
                Log.d("total_step:", String.valueOf(mPreferences.getInt("total_step", 0)));
                Log.d("total_distance:", String.valueOf(mPreferences.getFloat("total_distance", 0)));
                Log.d("total_calories:", String.valueOf(mPreferences.getFloat("total_calories", 0)));
                Log.d("total_duration:", String.valueOf(mPreferences.getInt("total_duration", 0)));
                Log.d("avg_speed:", String.valueOf(mPreferences.getFloat("avg_speed", 0)));


            }
        });

    }
}
