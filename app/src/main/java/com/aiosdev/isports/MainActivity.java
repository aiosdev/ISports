package com.aiosdev.isports;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aiosdev.isports.fragments.FragmentTab5;
import com.aiosdev.isports.tabmain.FirstLayerFragment;
import com.aiosdev.isports.fragments.FragmentTab1;
import com.aiosdev.isports.fragments.FragmentTab2;
import com.aiosdev.isports.fragments.FragmentTab3;
import com.aiosdev.isports.fragments.FragmentTab4;
import com.shizhefei.view.indicator.FixedIndicatorView;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;
import com.shizhefei.view.viewpager.SViewPager;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private IndicatorViewPager indicatorViewPager;
    private FixedIndicatorView indicator;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_main);
        SViewPager viewPager = (SViewPager) findViewById(R.id.tabmain_viewPager);
        indicator = (FixedIndicatorView) findViewById(R.id.tabmain_indicator);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(Color.RED, Color.GRAY));

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        // 禁止viewpager的滑动事件
        viewPager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        viewPager.setOffscreenPageLimit(4);
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"计步", "统计", "地图", "设置", "查询"};
        private int[] tabIcons = {R.drawable.maintab_1_selector, R.drawable.maintab_2_selector, R.drawable.maintab_3_selector,
                R.drawable.maintab_4_selector, R.drawable.maintab_5_selector};
        private LayoutInflater inflater;

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.tab_main, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabNames[position]);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[position], 0, 0);
            return textView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            ArrayList<Fragment> fragments = getFragments();

            Bundle bundle = new Bundle();
            switch (position){
                case 0:
                    bundle.putString(FragmentTab1.INTENT_STRING_TABNAME, tabNames[position]);
                    bundle.putInt(FragmentTab1.INTENT_INT_INDEX, position);
                    break;
                case 1:
                    bundle.putString(FragmentTab2.INTENT_STRING_TABNAME, tabNames[position]);
                    bundle.putInt(FragmentTab2.INTENT_INT_INDEX, position);
                    break;
                case 2:
                    bundle.putString(FragmentTab3.INTENT_STRING_TABNAME, tabNames[position]);
                    bundle.putInt(FragmentTab3.INTENT_INT_INDEX, position);
                    break;
                case 3:
                    bundle.putString(FragmentTab4.INTENT_STRING_TABNAME, tabNames[position]);
                    bundle.putInt(FragmentTab4.INTENT_INT_INDEX, position);
                    break;
                case 4:
                    bundle.putString(FragmentTab5.INTENT_STRING_TABNAME, tabNames[position]);
                    bundle.putInt(FragmentTab5.INTENT_INT_INDEX, position);
                    break;
                default:
                    bundle.putString(FirstLayerFragment.INTENT_STRING_TABNAME, tabNames[position]);
                    bundle.putInt(FirstLayerFragment.INTENT_INT_INDEX, position);
                    break;
            }


            fragments.get(position).setArguments(bundle);

            return fragments.get(position);
        }
    }

    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new FragmentTab1());
        fragments.add(new FragmentTab2());
        fragments.add(new FragmentTab3());
        fragments.add(new FragmentTab4());
        fragments.add(new FragmentTab5());
        return fragments;
    }
}
