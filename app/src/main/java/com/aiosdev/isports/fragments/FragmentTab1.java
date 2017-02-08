package com.aiosdev.isports.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiosdev.isports.MoveActivity;
import com.aiosdev.isports.MoveWithMapActivity;
import com.aiosdev.isports.R;
import com.aiosdev.isports.data.User;
import com.aiosdev.isports.tabmain.SecondLayerFragment;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;

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

	@Override
	protected void onResumeLazy() {
		super.onResumeLazy();
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
		tvTotalDistance.setText(user.getTotalDistance() + " 米");
		tvTotalCalories.setText(user.getTotalCalories() + " 卡");
		tvTotalDuration.setText(user.getTotalDuration() + " 秒");
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
