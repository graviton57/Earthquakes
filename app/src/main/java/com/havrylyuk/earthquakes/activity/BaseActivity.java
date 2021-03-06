package com.havrylyuk.earthquakes.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.havrylyuk.earthquakes.BuildConfig;
import com.havrylyuk.earthquakes.R;

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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

}
