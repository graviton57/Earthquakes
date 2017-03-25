package com.havrylyuk.earthquakes.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.fragment.EarthquakesFragment;
import com.havrylyuk.earthquakes.fragment.EarthquakesMapFragment;
import com.havrylyuk.earthquakes.fragment.SettingsFragment;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */
public class MainActivity extends BaseActivity {

    private static final String SELECTED_ITEM = "SELECTED_ITEM";
    private int selectedItem = R.id.navigation_home;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (savedInstanceState != null) {
            selectedItem = savedInstanceState.getInt(SELECTED_ITEM, R.id.navigation_home);
        }
        navigation.getMenu().performIdentifierAction(selectedItem, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_ITEM, selectedItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    swithToFragment(new EarthquakesMapFragment());
                    return true;
                case R.id.navigation_settings:
                    swithToFragment(new SettingsFragment());
                    return true;
                case R.id.navigation_list:
                    swithToFragment(new EarthquakesFragment());
                    return true;
            }
            return false;
        }

    };

    private void swithToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment).commit();
        if (fragment instanceof EarthquakesMapFragment) {
            selectedItem = R.id.navigation_home;
        } else if (fragment instanceof SettingsFragment){
            selectedItem = R.id.navigation_settings;
        } else if (fragment instanceof EarthquakesFragment){
            selectedItem = R.id.navigation_list;
        }
    }
}
