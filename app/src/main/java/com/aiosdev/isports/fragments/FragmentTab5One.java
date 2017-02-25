package com.aiosdev.isports.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.aiosdev.isports.MoveMapsActivity;
import com.aiosdev.isports.R;
import com.aiosdev.isports.adapter.RecyclerAdapterList;
import com.aiosdev.isports.data.MapContract;
import com.aiosdev.isports.data.MapDbHelper;
import com.aiosdev.isports.data.Task;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;

import java.util.ArrayList;
import java.util.List;

public class FragmentTab5One extends LazyFragment {
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    public static final String INTENT_STRING_TABNAME = "intent_String_tabName";
    public static final String INTENT_INT_POSITION = "intent_int_position";
    private String tabName;
    private int index;

    private Button btSearch;
    private ListView lvSearch;

    private List<Task> datelist;
    private RecyclerAdapterList adapter;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_tab_5_one);
        Resources res = getResources();

        Bundle bundle = getArguments();
        tabName = bundle.getString(INTENT_STRING_TABNAME);
        index = bundle.getInt(INTENT_INT_POSITION);

        btSearch = (Button) findViewById(R.id.bt_search);

        datelist = new ArrayList<>();
    }

    @Override
    protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        //判断数据库是否存在
        if(tableIsExist(MapContract.TaskEntry.TABLE_NAME)){
            //加载RecyclerView数据
            queryPointByDate();
            RecyclerView recycleView = (RecyclerView) findViewById(R.id.rv_search);
            recycleView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayout.VERTICAL, true));
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
        }else {
            Toast.makeText(getActivity(), "暂时没有数据！", Toast.LENGTH_SHORT).show();
        }


    }



    private void queryPointByDate() {
        datelist.clear();
        String columns[] = new String[]{MapContract.TaskEntry.COLUMN_DATE, MapContract.TaskEntry.COLUMN_TASK_NO, MapContract.TaskEntry.COLUMN_STEP};
        Uri myUri = MapContract.TaskEntry.CONTENT_URI;
        //Cursor cur = FavoriteActivity.this.managedQuery(myUri, columns, null, null, null);
        Cursor cur = null;

        String orderbyTimeAsc = "_id" + " desc";
        cur = getActivity().getContentResolver().query(myUri, columns, null, null, orderbyTimeAsc);

        if (cur.moveToFirst()) {

            do {
                Task task = new Task();
                task.setDate(cur.getString(cur.getColumnIndex(MapContract.TaskEntry.COLUMN_DATE)));
                task.setTaskNo(cur.getString(cur.getColumnIndex(MapContract.TaskEntry.COLUMN_TASK_NO)));
                task.setStep(cur.getInt(cur.getColumnIndex(MapContract.TaskEntry.COLUMN_STEP)));

                datelist.add(0, task);

            } while (cur.moveToNext());


        }
    }

    /**
     * 判断某张表是否存在
     */
    public boolean tableIsExist(String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        Cursor cursor = null;
        MapDbHelper dbHelper = new MapDbHelper(getActivity());
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //这里表名可以是Sqlite_master
            String sql = "select count(*) as c from sqlite_master" + " where type ='table' and name ='" + tableName.trim() + "' ";
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int count = cursor.getInt(0);
                if (count > 0) {
                    result = true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
