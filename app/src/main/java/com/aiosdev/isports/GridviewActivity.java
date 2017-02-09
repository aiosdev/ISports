package com.aiosdev.isports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class GridviewActivity extends AppCompatActivity {

    private GridView gridView;
    private List<Map<String,Object>> dataList;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tab_2);

        gridView = (GridView) findViewById(R.id.gridview);

    }
}
