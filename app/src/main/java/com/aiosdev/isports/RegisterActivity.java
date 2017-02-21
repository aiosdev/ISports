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

    private EditText etPaceLength;
    private TextView tvSb1;

    private SeekBar sbPara;
    private TextView tvSb2;

    private EditText etWeight;
    private TextView tvSb3;

    private EditText etPlan;
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
        etPaceLength = (EditText) findViewById(R.id.et_pace_length);
        //etPaceLength.setProgress(60);
        tvSb1 = (TextView) findViewById(R.id.tv_sb1_value);
        tvSb1.setText(R.string.register_activity_unit_pace_length);

        //灵敏度设定
        sbPara = (SeekBar) findViewById(R.id.sb_para);
        sbPara.setProgress(2 * 10);
        tvSb2 = (TextView) findViewById(R.id.tv_sb2_value);
        tvSb2.setText(2 + R.string.register_activity_recommand);

        //体重设定
        etWeight = (EditText) findViewById(R.id.et_weight);
        //etWeight.setProgress(50);
        tvSb3 = (TextView) findViewById(R.id.tv_sb3_value);
        tvSb3.setText(R.string.register_activity_unit_weight);

        //计划设定
        etPlan = (EditText) findViewById(R.id.et_plan);
        //etPlan.setProgress(5000);
        tvSb4 = (TextView) findViewById(R.id.tv_sb4_value);
        tvSb4.setText(R.string.register_activity_unit_steps);

        sbPara.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int i, boolean b) {
                if(20 == i) {
                    tvSb2.setText((float)(i / 10) + "" + R.string.register_activity_recommand);
                }else {
                    float iTemp = (float) i;
                    tvSb2.setText(iTemp / 10 + "");
                }
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

                //合法性验证
                if (validateData()) {

                    //设定初次登录标志
                    SharedPreferences sharePreference = getSharedPreferences("init", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorInit = sharePreference.edit();
                    editorInit.putBoolean("first", true);
                    editorInit.commit();


                    User user = User.getInstence(RegisterActivity.this);
                    user.setName(tvName.getText().toString().trim());
                    user.setSex(spinnerSex.getSelectedItem().toString().trim());
                    user.setWeight(Integer.parseInt(etWeight.getText().toString()));
                    user.setAvgStep(Integer.parseInt(etPaceLength.getText().toString()));

                    float senTemp = (float)sbPara.getProgress();
                    user.setSensitivity(senTemp / 10);

                    user.setGrade("初级");
                    user.setTitle("列兵");
                    user.setStepCount(Integer.parseInt(etPlan.getText().toString()));
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
                editor.putInt("weight", etWeight.getProgress());
                editor.putInt("pace_length", etPaceLength.getProgress());
                editor.putInt("sensitivity", sbPara.getProgress());
                editor.putString("grade", "初级");
                editor.putString("title", "列兵");
                editor.putInt("step_count_plan", etPlan.getProgress()); //步
                editor.putInt("total_step", 0);          //步
                editor.putFloat("total_distance", 0);  //公里
                editor.putFloat("total_calories", 0);  //卡路里
                editor.putInt("total_duration", 0);  //分钟
                editor.putFloat("avg_speed", 0);       //米/秒
                editor.commit();
                */
                    Toast.makeText(getApplicationContext(), R.string.register_activity_toast_success, Toast.LENGTH_SHORT).show();
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
            }
        });

        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etWeight.setText(null);
                etPaceLength.setText("60");
                sbPara.setProgress(3);
                etPlan.setText("6000");

                /*
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
                */

            }
        });

    }

    private boolean validateData() {

        boolean res = true;

        //验证名字
        if(tvName.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.register_activity_toast_valid_nickname, Toast.LENGTH_LONG).show();
            tvName.setFocusable(true);
            tvName.setFocusableInTouchMode(true);
            tvName.requestFocus();
            res = false;
        }

        //验证体重
        if(etWeight.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.register_activity_toast_valid_weight_null, Toast.LENGTH_LONG).show();
            etWeight.setFocusable(true);
            etWeight.setFocusableInTouchMode(true);
            etWeight.requestFocus();
            res = false;
        }else if (Integer.parseInt(etWeight.getText().toString()) > 300 || Integer.parseInt(etWeight.getText().toString()) < 30) {
            Toast.makeText(getApplicationContext(), R.string.register_activity_toast_valid_weight_fail, Toast.LENGTH_LONG).show();
            etWeight.setText(null);
            etWeight.setFocusable(true);
            etWeight.setFocusableInTouchMode(true);
            etWeight.requestFocus();
            res = false;
        }

        //验证初始步幅
        if(etPaceLength.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.register_activity_toast_valid_pace_lenght_null, Toast.LENGTH_LONG).show();
            etPaceLength.setFocusable(true);
            etPaceLength.setFocusableInTouchMode(true);
            etPaceLength.requestFocus();
            res = false;
        }else if (Integer.parseInt(etPaceLength.getText().toString()) > 150 || Integer.parseInt(etPaceLength.getText().toString()) < 20) {
            Toast.makeText(getApplicationContext(), R.string.register_activity_toast_valid_pace_lenght_fail, Toast.LENGTH_LONG).show();
            etPaceLength.setText(null);
            etPaceLength.setFocusable(true);
            etPaceLength.setFocusableInTouchMode(true);
            etPaceLength.requestFocus();
            res = false;
        }

        //验证计划步数
        if(etPlan.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.register_activity_toast_valid_plan_null, Toast.LENGTH_LONG).show();
            etPlan.setFocusable(true);
            etPlan.setFocusableInTouchMode(true);
            etPlan.requestFocus();
            res = false;
        }else if (Integer.parseInt(etPlan.getText().toString()) > 10000 || Integer.parseInt(etPlan.getText().toString()) < 1000) {
            Toast.makeText(getApplicationContext(), R.string.register_activity_toast_valid_plan_fail, Toast.LENGTH_LONG).show();
            etPlan.setText(null);
            etPlan.setFocusable(true);
            etPlan.setFocusableInTouchMode(true);
            etPlan.requestFocus();
            res = false;
        }


        return res;
    }
}
