package com.aiosdev.isports;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

import java.util.ArrayList;

public class MoveWithMapActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , OnMapReadyCallback{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;

    private PolylineOptions mPolylineOptions;
    private ArrayList<Location> mLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_with_map);
        Log.d("--------------------", "带地图Activity启动！");

        mPolylineOptions = new PolylineOptions();
        mLocations = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        Button btStart = (Button) findViewById(R.id.move_bt_start);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoveWithMapActivity.this, RegisterActivity.class));
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {

        mMap = map;

        mMap.setIndoorEnabled(true);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //然后连接,连接成功后会在onConnected回调
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        /*
        Double[] lats = {45.470813, 45.4708272, 45.4706755, 45.4705364, 45.4703989, 45.4704712, 45.4704882, 45.4705577, 45.4707251, 45.4708783, 45.4708783, 45.471101};
        Double[] lons = {-73.5670947, -73.567191, -73.5676989, -73.5677809, -73.568385, -73.5693882, -73.5705287, -73.5704877, -73.5705981, -73.5706123, -73.5706123, -73.5705856};

        for(int i=0;i<lats.length;i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(lats[i], lons[i])));
        }
        */

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        //开始定位前构造LocationRequest类设置定位时的属性.比如隔多久定位一次，设置定位精度等等
        createLocationRequest();

        //创建完成后开始定位
        //startLocation();


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));

        if(!mLocations.isEmpty()){
            Location locationFront = mLocations.get(mLocations.size()-1);
            mPolylineOptions.add(new LatLng(locationFront.getLatitude(), locationFront.getLongitude()));
            mPolylineOptions.add(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        mLocations.clear();
        mLocations.add(location);

        mPolylineOptions.width(12);
        mPolylineOptions.zIndex(100);
        mMap.addPolyline(mPolylineOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
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
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
}
