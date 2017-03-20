package com.havrylyuk.earthquakes.map;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.havrylyuk.earthquakes.R;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public abstract class BaseInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    protected final View window;
    protected Context context;

    public BaseInfoWindowAdapter(Activity context) {
        this.context = context;
        window = context.getLayoutInflater().inflate(R.layout.maps_custom_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        return null;
    }

    public abstract  void render (Marker marker, View view);


    }
