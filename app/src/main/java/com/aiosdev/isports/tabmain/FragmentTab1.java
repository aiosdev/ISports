package com.aiosdev.isports.tabmain;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiosdev.isports.R;
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

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tab_1);
		Resources res = getResources();

		Bundle bundle = getArguments();
		tabName = bundle.getString(INTENT_STRING_TABNAME);
		index = bundle.getInt(INTENT_INT_INDEX);

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



	private class MyAdapter extends IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 4;
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
