package com.havrylyuk.earthquakes.activity;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.data.EarthquakesContract.EarthquakesEntry;
import com.havrylyuk.earthquakes.map.DetailInfoWindowAdapter;
import com.havrylyuk.earthquakes.map.MarkerInfoWindowAdapter;
import com.havrylyuk.earthquakes.util.Utility;


public class DetailActivity extends BaseActivity  implements
        OnMapReadyCallback {

    public static final String DETAIL_POINT_URI = "com.havrylyuk.earthquakes.DETAIL_POINT_URI";
    private static final int DETAIL_LOADER = 5151;
    private Uri uri;
    private CollapsingToolbarLayout appBarLayout;
    private TextView magnitudeView;
    private TextView depthView;
    private TextView datetimeView;
    private TextView lngLatView;
    private TextView locationView;
    private TextView distanceView;
    private GoogleMap map;
    private float magnitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri =  getIntent().getParcelableExtra(DETAIL_POINT_URI);
        appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        magnitudeView = (TextView) findViewById(R.id.point_magnitude);
        depthView = (TextView) findViewById(R.id.point_depth);
        lngLatView = (TextView) findViewById(R.id.point_lng_lat);
        datetimeView = (TextView) findViewById(R.id.point_date_time);
        locationView = (TextView) findViewById(R.id.point_location);
        distanceView = (TextView) findViewById(R.id.point_distance);
        initFabFavorite();
        initToolBar();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(true);
        map.setInfoWindowAdapter(new DetailInfoWindowAdapter(this));
        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        setupMap();
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_detail;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFabFavorite() {
        FloatingActionButton share = (FloatingActionButton) findViewById(R.id.fab_share);
        if (share != null) {
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailActivity.this,"Share",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == DETAIL_LOADER) {
            if ( null != uri ) {
                return new CursorLoader(
                        this,
                        uri,
                        null,
                        null,
                        null,
                        null
                );
            }
        }
        return null;
    }

    private void addMarker(float lat, float lng) {
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getString(R.string.format_magnitude, magnitude));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.earthquake));
        map.addMarker(markerOptions);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(latLng, 5);
        map.animateCamera(location);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == DETAIL_LOADER) {
            if (cursor != null && cursor.moveToFirst()) {
                int pointId = cursor.getInt(cursor.getColumnIndex(EarthquakesEntry._ID));
                magnitude = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_MAGNITUDE));
                float lng = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_LNG));
                float lat = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_LAT));
                String location = getCountry() + "," + getRegion() + ", " + getCurrentCity();
                double depth  = cursor.getDouble(cursor.getColumnIndex(EarthquakesEntry.EARTH_DEPTH));
                String datetime  = cursor.getString(cursor.getColumnIndex(EarthquakesEntry.EARTH_DATE_TIME));
                String src  = cursor.getString(cursor.getColumnIndex(EarthquakesEntry.EARTH_SRC));
                addMarker(lat,lng);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(getCountry()+", "+ getRegion());
                    appBarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimaryDark));
                    appBarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
                } else if (getSupportActionBar() != null) {
                    getSupportActionBar(). setTitle(getCountry()+", "+ getRegion());
                }
                updateUI(magnitude, lat, lng, depth, datetime, location);
                if (distanceView != null) {
                    double distance = Utility.distance(new LatLng(lat, lng), new LatLng(
                            getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()));
                    distanceView.setText(String.format("Distance:%s km", distance));
                }
            } else {
                Toast.makeText(this,getString(R.string.nothing_found),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(float magnitude, float lat, float lng, double depth, String datetime, String location) {
        if (magnitudeView != null) {
            magnitudeView.setText(getString(R.string.format_magnitude, magnitude ));
        }
        if (depthView != null) {
            depthView.setText(getString(R.string.format_detail_depth, depth ));
        }
        if (datetimeView != null) {
            datetimeView.setText(getString(R.string.format_time, datetime));
        }
        if (lngLatView != null) {
            lngLatView.setText(getString(R.string.format_cord, lat, lng ));
        }
        if (locationView != null) {
            locationView.setText(getString(R.string.format_location, location));
        }

    }

    public void onLoaderReset(Loader<Cursor> loader) { }

}
