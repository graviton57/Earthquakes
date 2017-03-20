package com.havrylyuk.earthquakes.map;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.havrylyuk.earthquakes.model.Earthquake;

/**
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class PointItem extends Earthquake implements ClusterItem {

    private int id;
    private int icon;
    private final LatLng position;

    public PointItem(LatLng position) {
        this.position = position;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }
}
