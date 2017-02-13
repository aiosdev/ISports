package com.aiosdev.isports.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aiosdev.isports.R;
import com.aiosdev.isports.tabmain.SecondLayerFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;

import java.util.ArrayList;

public class FragmentTab3One extends LazyFragment implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , OnMapReadyCallback {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	public static final String INTENT_STRING_TABNAME = "intent_String_tabName";
	public static final String INTENT_INT_POSITION = "intent_int_position";
	private String tabName;
	private int index;

	private GoogleMap mMap;
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest locationRequest;

	private PolylineOptions mPolylineOptions;
	private ArrayList<Location> mLocations;

	private int i = 0;

	private Button btStart;
	private Button btStop;

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tab_3_one);
		Resources res = getResources();

		Bundle bundle = getArguments();
		tabName = bundle.getString(INTENT_STRING_TABNAME);
		index = bundle.getInt(INTENT_INT_POSITION);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map3);
		mapFragment.getMapAsync(FragmentTab3One.this);

		btStart = (Button) findViewById(R.id.bt_tab3_one_start);
		btStop = (Button) findViewById(R.id.bt_tab3_one_stop);
		btStart.setEnabled(false);
		btStop.setEnabled(false);

	}

	@Override
	public void onMapReady(GoogleMap map) {
		mMap = map;

		mMap.setIndoorEnabled(true);

		if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		mMap.setMyLocationEnabled(true);

		//首先生成一个GoogleApiClient对象并且设置属性
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API)
					.build();
		}

		//然后连接,连接成功后会在onConnected回调
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onDestroyViewLazy() {
		super.onDestroyViewLazy();

		//停止定位
		stopLocation();

		//设置start按钮可用
		btStart.setEnabled(true);
		btStop.setEnabled(false);
	}

	@Override
	public void onConnected(@Nullable Bundle bundle) {
		if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		Location myLocation =
				LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (myLocation != null) {
			LatLng myPoint = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
			mMap.addMarker(new MarkerOptions().position(myPoint).title("Current Location"));
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPoint, 17));
		}

		btStart.setEnabled(true);
		btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//开始定位前构造LocationRequest类设置定位时的属性.比如隔多久定位一次，设置定位精度等等
				createLocationRequest();

				//创建完成后开始定位
				startLocation();

				//设置stop按钮可用
				btStart.setEnabled(false);
				btStop.setEnabled(true);
			}
		});

		btStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//停止定位
				stopLocation();

				//设置start按钮可用
				btStart.setEnabled(true);
				btStop.setEnabled(false);
			}
		});


	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult result) {

	}

	@Override
	public void onLocationChanged(Location location) {

		mMap.clear();
		mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
		Toast.makeText(getActivity(), "新位置被检测" + i++ , Toast.LENGTH_SHORT).show();
		Log.d("新位置：", i+"");

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

	private void createLocationRequest() {
		locationRequest = new LocationRequest();
		locationRequest.setInterval(10000);
		locationRequest.setFastestInterval(1000);
		locationRequest.setSmallestDisplacement(0.1F);
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	}

	public void startLocation() {
		Log.d("mGoogleApiClient是否链接:", mGoogleApiClient.isConnected() + "");
		if (mGoogleApiClient.isConnected()) {
			if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			LocationServices.FusedLocationApi.requestLocationUpdates(
					mGoogleApiClient, locationRequest, this);
			Log.d("位置变化监听器状态：", "开启！");
		}
	}
	public void stopLocation() {
		Log.d("mGoogleApiClient是否链接:", mGoogleApiClient.isConnected() + "");
		if (mGoogleApiClient.isConnected()) {
			if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
		}
	}


}
