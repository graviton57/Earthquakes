package com.havrylyuk.earthquakes.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.havrylyuk.earthquakes.BuildConfig;
import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.data.EarthquakesContract.CountriesEntry;
import com.havrylyuk.earthquakes.model.BoundingBox;
import com.havrylyuk.earthquakes.service.EarthquakesService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();
    private static final int BASE_LOADER = 10;
    private static final int PERMISSIONS_REQUEST_LOCATION = 101;

    private GoogleApiClient googleApiClient;
    private Location currentLocation;
    private String countryCode, country, region ,currentCity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_REQUEST_LOCATION);
    }

    protected abstract int getLayout();

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(BaseActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(BaseActivity.this, permission)) {
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
                buildGoogleApiClient();
            }
            if (BuildConfig.DEBUG){
                Log.d(LOG_TAG, "" + permission + " is already granted.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSIONS_REQUEST_LOCATION:
                    buildGoogleApiClient();
                    break;
            }
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Permission granted");
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG,"onConnected");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (currentLocation != null) {
                updateCurrentLocation(currentLocation);
            }
        }
     }

    @Override
    public void onConnectionSuspended(int i) {
        if (BuildConfig.DEBUG) Log.d(LOG_TAG,"onConnectionSuspended "+i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "onConnectionFailed " + connectionResult.toString());
    }

    private void updateCurrentLocation(Location location) {
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                country = addresses.get(0).getCountryName();
                countryCode = addresses.get(0).getCountryCode();
                region = addresses.get(0).getAdminArea();
                currentCity = addresses.get(0).getLocality();
                String currentAddress = addresses.get(0).getAddressLine(0);
                if (BuildConfig.DEBUG){
                    Log.d(LOG_TAG, "updateCurrentLocation:You current country code=" +
                            countryCode+ " region="+region +" cityName=" + currentCity+" Address=" + currentAddress);
                    Log.d(LOG_TAG,"lat="+String.valueOf(location.getLatitude())+
                            "long="+String.valueOf(currentLocation.getLongitude()));
                }
                getSupportLoaderManager().initLoader(BASE_LOADER, null, BaseActivity.this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == BASE_LOADER) {
            return new CursorLoader(this, CountriesEntry.CONTENT_URI,
                    null,
                    CountriesEntry.COUNTRY_COUNTRY_CODE + " = ?",
                    new String[]{countryCode},
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == BASE_LOADER) {
            if (data != null && data.moveToFirst()) {
                Log.d(LOG_TAG, "onLoadFinished " + countryCode);
                Intent intent = new Intent(this, EarthquakesService.class);
                intent.setAction(EarthquakesService.ACTION_LOAD_EARTHQUAKES);
                long id = data.getLong(data.getColumnIndex(CountriesEntry._ID));
                intent.putExtra(EarthquakesService.EXTRA_COUNTRY_ID, id);
                BoundingBox box = new BoundingBox();
                box.setEast(data.getFloat(data.getColumnIndex(CountriesEntry.COUNTRY_EAST)));
                box.setWest(data.getFloat(data.getColumnIndex(CountriesEntry.COUNTRY_WEST)));
                box.setNorth(data.getFloat(data.getColumnIndex(CountriesEntry.COUNTRY_NORTH)));
                box.setSouth(data.getFloat(data.getColumnIndex(CountriesEntry.COUNTRY_SOUTH)));
                intent.putExtra(EarthquakesService.EXTRA_BOUNDING_BOX, box);
                startService(intent);
            }
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
