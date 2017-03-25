package com.havrylyuk.earthquakes.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.havrylyuk.earthquakes.event.LocationEvent;
import com.havrylyuk.earthquakes.map.DetailInfoWindowAdapter;
import com.havrylyuk.earthquakes.service.LocationService;
import com.havrylyuk.earthquakes.util.Utility;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class DetailActivity extends BaseActivity  implements
        OnMapReadyCallback, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAIL_LOADER = 5151;
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
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
    private String location;
    private float lng;
    private float lat;
    private double depth;
    private String datetime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        uri =  getIntent().getData();
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
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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

    @SuppressWarnings("deprecation")
    private void initFabFavorite() {
        FloatingActionButton share = (FloatingActionButton) findViewById(R.id.fab_share);
        if (share != null) {
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    } else {
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    }
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, location + " Magnitude:" + magnitude);
                    startActivity(shareIntent);
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
                return new CursorLoader(this,
                        uri, null, null, null, null);
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 5);
        map.animateCamera(cameraUpdate);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == DETAIL_LOADER) {
            if (cursor != null && cursor.moveToFirst()) {
                magnitude = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_MAGNITUDE));
                lng = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_LNG));
                lat = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_LAT));
                depth  = cursor.getDouble(cursor.getColumnIndex(EarthquakesEntry.EARTH_DEPTH));
                datetime  = cursor.getString(cursor.getColumnIndex(EarthquakesEntry.EARTH_DATE_TIME));
                String src  = cursor.getString(cursor.getColumnIndex(EarthquakesEntry.EARTH_SRC));
                addMarker(lat,lng);
                startLocationService(this, new LatLng(lat, lng));
            } else {
                Toast.makeText(this,getString(R.string.nothing_found),Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUI(float magnitude, float lat, float lng, double depth, String datetime, String location) {
        if (appBarLayout != null) {
            appBarLayout.setTitle(location);
            appBarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimaryDark));
            appBarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white));
        } else if (getSupportActionBar() != null) {
            getSupportActionBar(). setTitle(location);
        }
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
        if (distanceView != null && getCurrentLocation() != null) {
            double distance = Utility.distance(new LatLng(lat, lng), new LatLng(
                      getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()));
            distanceView.setText(getString(R.string.format_distance, distance));
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) { }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(LocationEvent event) {
        Log.d(LOG_TAG, "LocationEvent location=" + event.getLocation());
        location = event.getLocation();
        updateUI(magnitude, lat, lng, depth, datetime, location);
    }

    public void startLocationService(Context context, LatLng latLng){
        // Determine whether a Geocoder is available.
        if (!Geocoder.isPresent()) {
            Toast.makeText(context, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(context, LocationService.class);
        intent.putExtra(LocationService.LATLNG_DATA_EXTRA, latLng);
        context.startService(intent);
    };
}
