package com.havrylyuk.earthquakes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;
import com.havrylyuk.earthquakes.fragment.CountriesFragment;
import com.havrylyuk.earthquakes.fragment.EarthquakesFragment;
import com.havrylyuk.earthquakes.fragment.EarthquakesMapFragment;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */
public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.getMenu().performIdentifierAction(R.id.navigation_home, 0);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fm = getSupportFragmentManager();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    /*SupportMapFragment supportMapFragment =  SupportMapFragment.newInstance();
                    fm.beginTransaction().replace(R.id.frame_content, supportMapFragment).commit();*/
                    EarthquakesMapFragment  mapFragment = new EarthquakesMapFragment();
                    fm.beginTransaction().replace(R.id.frame_content, mapFragment).commit();
                    return true;
                case R.id.navigation_countries:
                    CountriesFragment countriesFragment =  new CountriesFragment();
                    fm.beginTransaction().replace(R.id.frame_content, countriesFragment).commit();
                    return true;
                case R.id.navigation_notifications:
                    EarthquakesFragment earthquakesFragment =  new EarthquakesFragment();
                    fm.beginTransaction().replace(R.id.frame_content, earthquakesFragment).commit();
                    return true;
            }
            return false;
        }

    };

}
