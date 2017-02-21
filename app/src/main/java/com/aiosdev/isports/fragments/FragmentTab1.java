package com.aiosdev.isports.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiosdev.isports.MoveActivity;
import com.aiosdev.isports.MoveWithMapActivity;
import com.aiosdev.isports.R;
import com.aiosdev.isports.data.User;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;

import java.math.BigDecimal;

public class FragmentTab1 extends LazyFragment {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
	public static final String INTENT_INT_INDEX = "intent_int_index";
	private String tabName;
	private int index;

	private TextView tvName;
	private TextView tvGrade;
	private TextView tvTitle;
	private TextView tvTotalSteps;
	private TextView tvTotalDistance;
	private TextView tvTotalDuration;
	private TextView tvTotalCalories;
	private TextView tvAvgStep;
	private ImageView ivSex;


	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tab_1);

		Bundle bundle = getArguments();
		tabName = bundle.getString(INTENT_STRING_TABNAME);
		index = bundle.getInt(INTENT_INT_INDEX);

		initView();

		/*
		SharedPreferences sharePreference = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		tvName.setText(sharePreference.getString("name", "无名氏"));
		String strSex = sharePreference.getString("sex", "男");
		if("男".equals(strSex)){
			ivSex.setImageResource(R.mipmap.pic_man);
		}else {
			ivSex.setImageResource(R.mipmap.pic_woman);
		}
		tvGrade.setText(sharePreference.getString("grade", "初级"));
		tvTitle.setText(sharePreference.getString("grade", "初级"));
		tvGrade.setText(sharePreference.getString("title", "列兵"));
		tvTotalSteps.setText(String.valueOf(sharePreference.getInt("total_step", 0)));
		tvTotalDistance.setText(String.valueOf(sharePreference.getFloat("total_distance", 0)));
		tvTotalCalories.setText(String.valueOf(sharePreference.getFloat("total_calories", 0)));
		tvTotalDuration.setText(String.valueOf(sharePreference.getInt("total_duration", 0)));
		*/
		Button btMove = (Button) findViewById(R.id.bt_move_normal);
		btMove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), MoveActivity.class));
			}
		});

		Button btMoveWithMap = (Button) findViewById(R.id.bt_move_special);
		btMoveWithMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), MoveWithMapActivity.class));
			}
		});



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
	}


	//保证user的数据及时刷新
	@Override
	protected void onFragmentStartLazy() {
		super.onFragmentStartLazy();
		setViewData();
	}

	//保证user的数据及时刷新
	@Override
	protected void onResumeLazy() {
		super.onResumeLazy();
		setViewData();

	}

	private void setViewData() {
		//获取用户基本信息和历史运动信息
		User user = User.getInstence(getActivity());
		tvName.setText(user.getName());

		if("男".equals(user.getSex())){
			ivSex.setImageResource(R.mipmap.pic_man);
		}else {
			ivSex.setImageResource(R.mipmap.pic_woman);
		}
		tvGrade.setText(user.getGrade());
		tvTitle.setText(user.getTitle());
		tvTotalSteps.setText(user.getTotalStep() + " 步");

		//里程显示，将米转换为公里
		Float distTemp = user.getTotalDistance() / 1000;
		BigDecimal bDistTemp = new BigDecimal(distTemp);
		distTemp = bDistTemp.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();

		tvTotalDistance.setText(distTemp + " 公里");

		tvTotalCalories.setText(user.getTotalCalories() + " 卡");

		//时间显示，将秒转换为小时，分钟，秒
		//user.setTotalDuration(1234567);
		int hourTemp = user.getTotalDuration() / 3600;
		int minutTemp = user.getTotalDuration() % 3600 / 60;
		int secTemp = user.getTotalDuration() % 3600 % 60;
		tvTotalDuration.setText(hourTemp + " 小时 " + minutTemp + " 分钟 " + secTemp  + " 秒");


		tvAvgStep.setText(user.getAvgStep() + " 厘米");
	}


	private void initView() {
		tvName = (TextView) findViewById(R.id.tv_name);
		tvGrade = (TextView) findViewById(R.id.tv_grade);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTotalSteps = (TextView) findViewById(R.id.tv_total_step);
		tvTotalDistance = (TextView) findViewById(R.id.tv_total_distance);
		tvTotalCalories = (TextView) findViewById(R.id.tv_total_calories);
		tvTotalDuration = (TextView) findViewById(R.id.tv_total_duration);
		tvAvgStep = (TextView) findViewById(R.id.tv_avg_step);
		ivSex = (ImageView) findViewById(R.id.image_person);
	}

}
