package com.aiosdev.isports.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aiosdev.isports.R;
import com.aiosdev.isports.data.User;
import com.aiosdev.isports.tabmain.SecondLayerFragment;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;

public class FragmentTab4 extends LazyFragment {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
	public static final String INTENT_INT_INDEX = "intent_int_index";
	private String tabName;
	private int index;

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

	private Switch swAlert;

	private User mUser;

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tab_4);
		Resources res = getResources();

		Bundle bundle = getArguments();
		tabName = bundle.getString(INTENT_STRING_TABNAME);
		index = bundle.getInt(INTENT_INT_INDEX);


		btCommit = (Button) findViewById(R.id.bt_register_commit);
		btReset = (Button) findViewById(R.id.bt_register_reset);

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

		swAlert = (Switch) findViewById(R.id.sw_alert);
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

		etWeight.setText(mUser.getWeight() + "");
		etPaceLength.setText(mUser.getAvgStep() + "");
		etPlan.setText(mUser.getStepCount() + "");
		sbPara.setProgress(mUser.getSensitivity());

		swAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean b) {
				if(b){
					Toast.makeText(getActivity(), "打开", Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(getActivity(), "关闭", Toast.LENGTH_SHORT).show();
				}
			}
		});

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
