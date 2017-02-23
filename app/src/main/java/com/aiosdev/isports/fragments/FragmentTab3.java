package com.aiosdev.isports.fragments;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiosdev.isports.R;
import com.shizhefei.fragment.LazyFragment;

import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.slidebar.LayoutBar;
import com.shizhefei.view.indicator.slidebar.ScrollBar.Gravity;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

public class FragmentTab3 extends LazyFragment {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
	public static final String INTENT_INT_INDEX = "intent_int_index";
	private String tabName;
	private int index;

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tab_3);
		Resources res = getResources();

		Bundle bundle = getArguments();
		tabName = bundle.getString(INTENT_STRING_TABNAME);
		index = bundle.getInt(INTENT_INT_INDEX);

		ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);

		indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.BLUE, 5));

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

	}

	@Override
	protected void onResumeLazy() {
		super.onResumeLazy();
		Log.d("cccc", "Fragment所在的Activity onResume, onResumeLazy " + this);
	}

	@Override
	protected void onFragmentStartLazy() {
		super.onFragmentStartLazy();
		Log.d("cccc", "Fragment 显示 " + this);
	}

	@Override
	protected void onFragmentStopLazy() {
		super.onFragmentStopLazy();
		Log.d("cccc", "Fragment 掩藏 " + this);
	}

	@Override
	protected void onPauseLazy() {
		super.onPauseLazy();
		Log.d("cccc", "Fragment所在的Activity onPause, onPauseLazy " + this);
	}

	@Override
	protected void onDestroyViewLazy() {
		super.onDestroyViewLazy();
		Log.d("cccc", "Fragment View将被销毁 " + this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("cccc", "Fragment 所在的Activity onDestroy " + this);
	}

	private class MyAdapter extends IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				switch (position){
					case 0:
						convertView = inflate.inflate(R.layout.tab_top, container, false);
						TextView textView = (TextView) convertView;
						textView.setText(getString(R.string.tab3_fragment_tab1_name));
						break;
					case 1:
						convertView = inflate.inflate(R.layout.tab_top, container, false);
						TextView textView1 = (TextView) convertView;
						textView1.setText(getString(R.string.tab3_fragment_tab2_name));
						break;
				}

			}

			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			switch (position){
				case 0:
					FragmentTab3One fragmentTab3One = new FragmentTab3One();
					Bundle bundle = new Bundle();
					bundle.putString(FragmentTab3One.INTENT_STRING_TABNAME, tabName);
					bundle.putInt(FragmentTab5One.INTENT_INT_POSITION, position);
					fragmentTab3One.setArguments(bundle);
					return fragmentTab3One;
				case 1:
					FragmentTab3Two fragmentTab3Two = new FragmentTab3Two();
					Bundle bundle1 = new Bundle();
					bundle1.putString(FragmentTab3Two.INTENT_STRING_TABNAME, tabName);
					bundle1.putInt(FragmentTab3Two.INTENT_INT_POSITION, position);
					fragmentTab3Two.setArguments(bundle1);
					return fragmentTab3Two;
			}

			return null;

		}
	}

}
