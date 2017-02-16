package com.aiosdev.isports.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.aiosdev.isports.R;
import com.aiosdev.isports.data.User;
import com.aiosdev.isports.tools.StepArcView;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentTab5Two extends LazyFragment {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	public static final String INTENT_STRING_TABNAME = "intent_String_tabName";
	public static final String INTENT_INT_POSITION = "intent_int_position";
	private String tabName;
	private int index;

	private ImageView ivCurrentGrade;
	private TextView tvCurrentGrade;
	private GridView gdView;

	private List<Map<String, Object>> data_list;
	private SimpleAdapter sim_adapter;

	private User user;

	// 图片封装为一个数组
	private int[] intTitleicon;
	private String[] iconName = { "通讯录", "日历", "照相机", "时钟", "游戏", "短信", "铃声",
			"设置", "语音", "天气", "浏览器", "视频" };

	//判断等级和头衔的变化
	private String[] strGrade = {"初级", "初级", "初级", "初级", "中级", "中级", "中级", "中级", "高级", "高级", "高级",
			"高级", "运动专家", "运动专家", "运动专家", "运动专家", "资深运动专家", "资深运动专家"};
	private String[] strTitle;
	private int[] intSteps = {0, 1000 , 2000, 4000, 8000, 24000, 60000, 120000, 240000, 360000, 500000,
			650000, 820000, 1000000, 1250000, 1600000, 2000000, 3000000};

	//显示当前等级图标
	private String[] strCurrentGrade = {"初级", "中级", "高级", "运动专家", "资深运动专家"};
	private int[] intCurrentGradeIcon = {R.mipmap.pic_titile_5_1, R.mipmap.pic_titile_5_2, R.mipmap.pic_titile_5_3, R.mipmap.pic_titile_5_4,
			R.mipmap.pic_titile_5_5};

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tab_5_two);
		Resources res = getResources();
		Bundle bundle = getArguments();
		tabName = bundle.getString(INTENT_STRING_TABNAME);
		index = bundle.getInt(INTENT_INT_POSITION);
		ivCurrentGrade = (ImageView) findViewById(R.id.iv_current_grade);
		tvCurrentGrade = (TextView) findViewById(R.id.tv_current_grade);
		gdView = (GridView) findViewById(R.id.gview);

	}

	@Override
	protected void onFragmentStartLazy() {
		super.onFragmentStartLazy();

		user = User.getInstence(getActivity());

		//显示当前等级
		tvCurrentGrade.setText(user.getGrade());

		//显示当前等级图标
		for(int i=0;i<strCurrentGrade.length;i++){
			if(strCurrentGrade[i].equals(user.getGrade())){
				ivCurrentGrade.setImageResource(intCurrentGradeIcon[i]);
			}
		}

		// 显示获得的头衔图标
		intTitleicon = new int[]{R.mipmap.pic_titile_1_1, R.mipmap.pic_titile_1_2, R.mipmap.pic_titile_1_3, R.mipmap.pic_titile_1_4,
				R.mipmap.pic_titile_2_1, R.mipmap.pic_titile_2_2, R.mipmap.pic_titile_2_3, R.mipmap.pic_titile_2_4,
				R.mipmap.pic_titile_3_1, R.mipmap.pic_titile_3_2, R.mipmap.pic_titile_3_3, R.mipmap.pic_titile_3_4,
				R.mipmap.pic_titile_4_1, R.mipmap.pic_titile_4_2, R.mipmap.pic_titile_4_3, R.mipmap.pic_titile_4_4,
				R.mipmap.pic_titile_3_5, R.mipmap.pic_titile_4_5};

		strTitle = new String[]{"列兵", "一等兵", "二等兵", "三等兵", "少尉", "中尉", "上尉", "大尉",
				"少校", "中校", "上校", "大校", "少将", "中将", "上将", "四星上将", "元帅", "司令"};

		for(int i=0;i<strTitle.length;i++){
			if(strTitle[i].equals(user.getTitle())){
				for(int j=i+1;j<strTitle.length;j++){
					strTitle[j] = "未获得";
					intTitleicon[j] = R.mipmap.lock1;
				}
			}
		}


		data_list = new ArrayList<>();
		//获取数据
		getData();
		//新建适配器
		String [] from ={"image","text"};
		int [] to = {R.id.image,R.id.text};
		sim_adapter = new SimpleAdapter(getActivity(), data_list, R.layout.item_fragment_tab5_one_gridview, from, to);
		//配置适配器
		gdView.setAdapter(sim_adapter);

		gdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> view, View view1, int i, long l) {
				showDes(intSteps[i]);
			}
		});
	}

	private void showDes(int steps) {
		Dialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle("晋级提示")
				.setIcon(android.R.drawable.ic_dialog_info)
				.setView(createView(steps))
				// 设置内容
				.setNegativeButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int whichButton) {
							}
						}).create();// 创建
		// 显示对话框
		dialog.show();
	}

	private View createView(int steps) {
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams paramLl = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layout.setLayoutParams(paramLl);

		StepArcView arcView = new StepArcView(getActivity());
		arcView.setCurrentCount(steps, user.getTotalStep());
		layout.addView(arcView);

		return layout;
	}

	public List<Map<String, Object>> getData(){
		//cion和iconName的长度是相同的，这里任选其一都可以
		for(int i = 0; i< intTitleicon.length; i++){
			Map<String, Object> map = new HashMap<>();
			map.put("image", intTitleicon[i]);
			map.put("text", strTitle[i]);
			data_list.add(map);
		}

		return data_list;
	}

	private void validDataDisplay(){
		User user = User.getInstence(getActivity());

		for(int i=0;i<strGrade.length;i++){
			if(strGrade[i].equals(user.getGrade())){

			}

		}
	}

}
