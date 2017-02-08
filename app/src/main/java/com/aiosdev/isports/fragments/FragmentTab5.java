package com.aiosdev.isports.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aiosdev.isports.MoveMapsActivity;
import com.aiosdev.isports.R;
import com.aiosdev.isports.adapter.RecyclerAdapterList;
import com.aiosdev.isports.data.MapContract;
import com.aiosdev.isports.data.Task;
import com.aiosdev.isports.tabmain.SecondLayerFragment;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentTab5 extends LazyFragment {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
	public static final String INTENT_INT_INDEX = "intent_int_index";
	private String tabName;
	private int index;

	private Button btSearch;
	private ListView lvSearch;

	private List<Task> datelist;
	private RecyclerAdapterList adapter;

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tab_5);
		Resources res = getResources();

		Bundle bundle = getArguments();
		tabName = bundle.getString(INTENT_STRING_TABNAME);
		index = bundle.getInt(INTENT_INT_INDEX);

		btSearch = (Button) findViewById(R.id.bt_search);

		datelist = new ArrayList<>();

		//加载RecyclerView数据

		queryPointByDate();
		RecyclerView recycleView = (RecyclerView) findViewById(R.id.rv_search);
		recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, true));
		datelist = new ArrayList<>();
		queryPointByDate();
		adapter = new RecyclerAdapterList(getActivity(), datelist);
		recycleView.setAdapter(adapter);

		adapter.setOnItemClickListener(new RecyclerAdapterList.OnItemClickListener() {
			@Override
			public void onItemClick(int position, Object object, View view) {

				Intent intent = new Intent();
				intent.putExtra("flag", "FragmentTab5");
				intent.putExtra("taskNo", datelist.get(position).getTaskNo());
				intent.putExtra("date", datelist.get(position).getDate());
				intent.setClass(getActivity(), MoveMapsActivity.class);
				startActivity(intent);
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

	private void queryPointByDate() {
		datelist.clear();
		String columns[] = new String[] {MapContract.TaskEntry.COLUMN_DATE, MapContract.TaskEntry.COLUMN_TASK_NO};
		Uri myUri = MapContract.TaskEntry.CONTENT_URI;
		//Cursor cur = FavoriteActivity.this.managedQuery(myUri, columns, null, null, null);
		Cursor cur = null;

		String orderbyTimeAsc = "_id" + " desc";
		cur = getActivity().getContentResolver().query(myUri, columns, null, null, orderbyTimeAsc);

		if (cur.moveToFirst()) {

			do {
				Task task = new Task();
				task.setDate(cur.getString(cur.getColumnIndex("date"))); ;
				task.setTaskNo(cur.getString(cur.getColumnIndex("task_no")));

				datelist.add(0,task);

			} while (cur.moveToNext());


		}
	}
}
