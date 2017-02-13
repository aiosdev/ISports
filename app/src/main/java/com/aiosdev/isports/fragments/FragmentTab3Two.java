package com.aiosdev.isports.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aiosdev.isports.R;
import com.aiosdev.isports.adapter.PlaceAutocompleteAdapter;
import com.aiosdev.isports.tabmain.SecondLayerFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;

public class FragmentTab3Two extends LazyFragment implements GoogleApiClient.OnConnectionFailedListener {
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    public static final String INTENT_STRING_TABNAME = "intent_String_tabName";
    public static final String INTENT_INT_POSITION = "intent_int_position";
    private String tabName;
    private int index;
    private EditText et_start;
    private EditText et_dest;
    private boolean bt_navigate_Value;
    private Button bt_navigate;

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteViewStart;
    private AutoCompleteTextView mAutocompleteViewStop;

    private TextView mPlaceDetailsAttribution;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        setContentView(R.layout.fragment_tab_3_two);
        Resources res = getResources();

        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        mAutocompleteViewStart = (AutoCompleteTextView)
                findViewById(R.id.et_tab3_two_search2);

        mAutocompleteViewStop = (AutoCompleteTextView)
                findViewById(R.id.et_tab3_two_search3);

        // Register a listener that receives callbacks when a suggestion has been selected
        //mAutocompleteViewStart.setOnItemClickListener(mAutocompleteClickListener);
        //mAutocompleteViewStop.setOnItemClickListener(mAutocompleteClickListener);

        // Retrieve the TextViews that will display details and attributions of the selected place.
        //mPlaceDetailsText = (TextView) findViewById(R.id.place_details);
        //mPlaceDetailsAttribution = (TextView) findViewById(R.id.place_attribution);

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteViewStart.setAdapter(mAdapter);
        mAutocompleteViewStop.setAdapter(mAdapter);



        Bundle bundle = getArguments();
        tabName = bundle.getString(INTENT_STRING_TABNAME);
        index = bundle.getInt(INTENT_INT_POSITION);

        bt_navigate = (Button) findViewById(R.id.bt_tab3_two_go2);

        bt_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Navigation", "导航开始");
                String str1 = mAutocompleteViewStart.getText().toString();
                String str2 = mAutocompleteViewStop.getText().toString();
                Intent localIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://maps.google.com/maps?saddr=" + str2 + "&daddr=" + str1));
                localIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(localIntent);

            }
        });

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(),
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
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
