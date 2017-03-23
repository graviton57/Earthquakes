package com.havrylyuk.earthquakes.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.havrylyuk.earthquakes.BuildConfig;
import com.havrylyuk.earthquakes.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String LOG_TAG = BaseActivity.class.getSimpleName();
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String parseLocation(float latitude, float longitude) {
        StringBuilder result = new StringBuilder("");
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses  = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                if (addresses.get(0).getCountryName() != null) {
                    result.append(addresses.get(0).getCountryName());
                }
                if (addresses.get(0).getAdminArea() != null) {
                    result.append(",").append(addresses.get(0).getAdminArea());
                }
                if (addresses.get(0).getLocality() != null) {
                    result.append(",").append(addresses.get(0).getLocality());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.append("Error parse location");
        }
        if (TextUtils.isEmpty(result.toString())) {
            result.append("Unknown location");
        }
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "parseLocation: " + result.toString());
        return result.toString();
    }

}
