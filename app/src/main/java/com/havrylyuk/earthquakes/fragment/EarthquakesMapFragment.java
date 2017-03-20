package com.havrylyuk.earthquakes.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.havrylyuk.earthquakes.MainActivity;
import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.activity.DetailActivity;
import com.havrylyuk.earthquakes.data.EarthquakesContract.EarthquakesEntry;
import com.havrylyuk.earthquakes.map.ClusterRenderer;
import com.havrylyuk.earthquakes.map.MarkerInfoWindowAdapter;
import com.havrylyuk.earthquakes.map.PointItem;

/**
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class EarthquakesMapFragment extends SupportMapFragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        OnMapReadyCallback,
       // LocationListener,
        ClusterManager.OnClusterClickListener<PointItem>,
        ClusterManager.OnClusterInfoWindowClickListener<PointItem>,
        ClusterManager.OnClusterItemInfoWindowClickListener<PointItem>{

    private static final int EARTHQUAKE_LOADER = 57;
    private GoogleMap map;
    private ClusterManager<PointItem> clusterManager;
    private PointItem destinationPoint; //destination position
    Marker currLocationMarker;//original user position
    private Location lastLocation;

    public EarthquakesMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMapAsync(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == EARTHQUAKE_LOADER) {
            return new CursorLoader(getActivity(),
                    EarthquakesEntry.CONTENT_URI,
                    null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == EARTHQUAKE_LOADER) {
            //if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (cursor != null && cursor.moveToFirst()) {
                clusterManager.clearItems();
                map.clear();
                lastLocation = ((MainActivity)getActivity()).getCurrentLocation();
                if (lastLocation != null) {
                    addLocationMarker(lastLocation);
                }
                LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
                do {
                    float lat = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_LAT));
                    float lng = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_LNG));
                    LatLng point = new LatLng(lat, lng);
                    PointItem pointItem = new PointItem(point);
                    float m = cursor.getFloat(cursor.getColumnIndex(EarthquakesEntry.EARTH_MAGNITUDE));
                    pointItem.setIcon(m >= 5 ? R.drawable.earthquake:R.drawable.earthquake_low);
                    pointItem.setId(cursor.getInt(cursor.getColumnIndex(EarthquakesEntry._ID)));
                    latLngBuilder.include(point);
                    clusterManager.addItem(pointItem);
                } while (cursor.moveToNext());
                Toast.makeText(getActivity(),
                        getString(R.string.format_found_points, String.valueOf(cursor.getCount())),
                        Toast.LENGTH_SHORT).show();
                clusterManager.cluster();
                LatLngBounds latLngBounds = latLngBuilder.build();
                CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, 25);
                map.moveCamera(track);
            }
            //if (progressBar != null) progressBar.setVisibility(View.GONE);
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void addLocationMarker(Location location) {
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(getString(R.string.self_position));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        currLocationMarker = map.addMarker(markerOptions);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setMapToolbarEnabled(true);
        //map.setOnMapClickListener(this);
        clusterManager = new ClusterManager<>(getActivity(), map);
        clusterManager.setRenderer(new ClusterRenderer(getActivity(), map, clusterManager));
        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());
        map.setOnInfoWindowClickListener(clusterManager);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<PointItem>() {
            @Override
            public boolean onClusterItemClick(PointItem item) {
                destinationPoint = item;
                return false;
            }
        });
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterInfoWindowClickListener(this);
        clusterManager.getMarkerCollection()
                .setOnInfoWindowAdapter( new MarkerInfoWindowAdapter(getActivity()));
       /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                map.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            map.setMyLocationEnabled(true);
        }*/
        getActivity().getSupportLoaderManager().initLoader(EARTHQUAKE_LOADER, null, this);//load data
    }
    @Override
    public boolean onClusterClick(Cluster<PointItem> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        final LatLngBounds bounds = builder.build();
        try {
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void onClusterInfoWindowClick(Cluster<PointItem> cluster) {
    }

    @Override
    public void onClusterItemInfoWindowClick(PointItem pointItem) {
        Bundle args = new Bundle();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Uri uri = EarthquakesEntry.buildEarthquakesUri(destinationPoint.getId());
        args.putParcelable(DetailActivity.DETAIL_POINT_URI, uri);
        intent.putExtras(args);
        startActivity(intent);
    }

    /*@Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        addLocationMarker(location);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(11));
        parseLocation(location);
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }*/
}
