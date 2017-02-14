package com.aiosdev.isports.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aiosdev.isports.R;
import com.aiosdev.isports.alerm.AlarmManagerUtil;
import com.aiosdev.isports.data.MapContract;
import com.aiosdev.isports.data.User;
import com.aiosdev.isports.tabmain.SecondLayerFragment;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentTab4 extends LazyFragment implements View.OnClickListener {
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
    public static final String INTENT_INT_INDEX = "intent_int_index";
    private String tabName;
    private int index;

    private EditText etName;

    private EditText etPaceLength;
    private TextView tvSb1;

    private SeekBar sbPara;
    private TextView tvSb2;

    private EditText etWeight;
    private TextView tvSb3;

    private EditText etPlan;
    private TextView tvSb4;

    private Button btCommit;
    private Button btReset;
    private Button btClear;

    private Switch swAlerm;
    private TextView tvAlermTime;
    private Spinner spinAlarmType;

    private User mUser;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_tab_4);
        Resources res = getResources();

        Bundle bundle = getArguments();
        tabName = bundle.getString(INTENT_STRING_TABNAME);
        index = bundle.getInt(INTENT_INT_INDEX);


        btCommit = (Button) findViewById(R.id.bt_setting_commit);
        btReset = (Button) findViewById(R.id.bt_setting_reset);
        btClear = (Button) findViewById(R.id.bt_setting_clear);

        //用户名
        etName = (EditText) findViewById(R.id.et_name);

        //初始步幅设定
        etPaceLength = (EditText) findViewById(R.id.et_pace_length);
        //etPaceLength.setProgress(60);
        tvSb1 = (TextView) findViewById(R.id.tv_sb1_value);
        tvSb1.setText("厘米");

        //灵敏度设定
        sbPara = (SeekBar) findViewById(R.id.sb_para);
        sbPara.setProgress(3);
        tvSb2 = (TextView) findViewById(R.id.tv_sb2_value);
        tvSb2.setText(sbPara.getProgress() + "(推荐)");

        //体重设定
        etWeight = (EditText) findViewById(R.id.et_weight);
        //etWeight.setProgress(50);
        tvSb3 = (TextView) findViewById(R.id.tv_sb3_value);
        tvSb3.setText("公斤");

        //计划设定
        etPlan = (EditText) findViewById(R.id.et_plan);
        //etPlan.setProgress(5000);
        tvSb4 = (TextView) findViewById(R.id.tv_sb4_value);
        tvSb4.setText("步");

        //闹钟设定
        swAlerm = (Switch) findViewById(R.id.sw_alerm);
        tvAlermTime = (TextView) findViewById(R.id.tv_alerm_clock);
        spinAlarmType = (Spinner) findViewById(R.id.spinner_alarm_type);

		/*
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);

		switch (index) {
		case 0:
			indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.RED, 5));
			break;
		case 1:
			indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.RED, 0, Gravity.CENTENT_BACKGROUND));
			break;
		case 2:
			indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.RED, 5, Gravity.TOP));
			break;
		case 3:
			indicator.setScrollBar(new LayoutBar(getApplicationContext(), R.layout.layout_slidebar, Gravity.CENTENT_BACKGROUND));
			break;
		}

		float unSelectSize = 16;
		float selectSize = unSelectSize * 1.2f;

		int selectColor = res.getColor(R.color.tab_top_text_2);
		int unSelectColor = res.getColor(R.color.tab_top_text_1);
		indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

		viewPager.setOffscreenPageLimit(4);

		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		inflate = LayoutInflater.from(getApplicationContext());

		// 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
		// 而在activity里面用FragmentManager 是 getSupportFragmentManager()
		indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

		Log.d("cccc", "Fragment 将要创建View " + this);
		*/

        mUser = User.getInstence(getActivity());
        Log.d("setting:", mUser.toString());

        etName.setText(mUser.getName());
        etWeight.setText(mUser.getWeight() + "");
        etPaceLength.setText(mUser.getAvgStep() + "");
        etPlan.setText(mUser.getStepCount() + "");
        sbPara.setProgress(mUser.getSensitivity());
        int initSwAlerm = mUser.getAlerm();
        if(initSwAlerm == 1) {
            swAlerm.setChecked(true);
        }else {
            swAlerm.setChecked(false);
        }

        tvAlermTime.setText(mUser.getAlermTime());
        spinAlarmType.setSelection(mUser.getAlermType());

        swAlerm.setOnClickListener(this);
        tvAlermTime.setOnClickListener(this);
        btCommit.setOnClickListener(this);
        btReset.setOnClickListener(this);
        btClear.setOnClickListener(this);

        swAlerm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean b) {
                if (b) {
                    mUser.setAlerm(1);
                    Toast.makeText(getActivity(), "打开", Toast.LENGTH_SHORT).show();

                } else {
                    mUser.setAlerm(0);
                    Toast.makeText(getActivity(), "关闭", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_alerm_clock:
                setAlermClock();
                break;
            case R.id.bt_setting_commit:
                saveSetting();
                break;
            case R.id.bt_setting_clear:
                ClearDialog();
                break;
        }
    }

    private void ClearDialog() {
        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("警告")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("是否放弃当前等级与头衔，并删除全部历史数据？")
                // 设置内容
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        clearAllData();
                        //Main.this.finish();
                    }
                })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        }).create();// 创建
        // 显示对话框
        dialog.show();
    }

    private void clearAllData() {
        mUser.setGrade("初级");
        mUser.setTitle("小白");
        mUser.setTotalStep(0);
        mUser.setTotalDuration(0);
        mUser.setTotalDistance(Float.parseFloat("0"));
        mUser.setTotalCalories(Float.parseFloat("0"));
        mUser.setAvgSpeed(Float.parseFloat("0"));

        //保存数据到SharedPreferences “userInfo”
        mUser.saveData(getActivity());

        //删除数据库数据
        ContentValues contentValues = new ContentValues();
        Uri urlTask = MapContract.TaskEntry.CONTENT_URI;
        getActivity().getContentResolver().delete(urlTask, null, null);

        Uri urlLocation = MapContract.LoactionEntry.CONTENT_URI;
        getActivity().getContentResolver().delete(urlLocation, null, null);

        Toast.makeText(getActivity(), "数据清除干净！", Toast.LENGTH_SHORT).show();

    }

    private void saveSetting() {

        //保存闹钟设置，并启动闹钟服务
        if (1 == mUser.getAlerm()) {
            //取设定的时间
            String time = tvAlermTime.getText().toString().trim();
            //取闹钟方式
            if ("震动".equals(spinAlarmType.getSelectedItem())) {
                mUser.setAlermType(0);
            } else if ("响铃".equals(spinAlarmType.getSelectedItem())) {
                mUser.setAlermType(1);
            }

            //保存闹钟时间设置信息
            mUser.setAlermTime(tvAlermTime.getText().toString().trim());

            if (time != null && time.length() > 0) {
                String[] times = time.split(":");

                AlarmManagerUtil.setAlarm(getActivity(), 1, Integer.parseInt(times[0]), Integer.parseInt
                        (times[1]), 0, 0, "今天该运动了！", mUser.getAlermType());

                Toast.makeText(getActivity(), "闹钟设置成功", Toast.LENGTH_LONG).show();
            }


        }

        //保存其他参数设置
        mUser.setName(etName.getText().toString().trim());
        mUser.setWeight(Integer.parseInt(etWeight.getText().toString().trim()));
        mUser.setAvgStep(Integer.parseInt(etPaceLength.getText().toString().trim()));
        mUser.setSensitivity((sbPara.getProgress()));
        mUser.setStepCount(Integer.parseInt(etPlan.getText().toString().trim()));

        mUser.saveData(getActivity());

        Toast.makeText(getActivity(), "参数设置成功", Toast.LENGTH_LONG).show();
    }

    private void setAlermClock() {
        final Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
//        String time = tv_remind_time.getText().toString().trim();
        final DateFormat df = new SimpleDateFormat("HH:mm");
//        Date date = null;
//        try {
//            date = df.parse(time);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        if (null != date) {
//            calendar.setTime(date);
//        }
        new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String remaintime = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE);
                Date date = null;
                try {
                    date = df.parse(remaintime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (null != date) {
                    calendar.setTime(date);
                }
                tvAlermTime.setText(df.format(date));
            }
        }, hour, minute, true).show();
    }


    private class MyAdapter extends IndicatorFragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabName + " " + position);
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            SecondLayerFragment mainFragment = new SecondLayerFragment();
            Bundle bundle = new Bundle();
            bundle.putString(SecondLayerFragment.INTENT_STRING_TABNAME, tabName);
            bundle.putInt(SecondLayerFragment.INTENT_INT_POSITION, position);
            mainFragment.setArguments(bundle);
            return mainFragment;
        }
    }

}
