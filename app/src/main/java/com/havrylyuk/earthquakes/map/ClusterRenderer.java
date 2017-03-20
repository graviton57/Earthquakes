package com.havrylyuk.earthquakes.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.havrylyuk.earthquakes.R;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Igor Havrylyuk on 21.03.2017.
 */

public class ClusterRenderer extends DefaultClusterRenderer<PointItem> {

    private final IconGenerator iconGenerator;
    private final IconGenerator clusterIconGenerator;
    private final ImageView imageView;
    private final ImageView clusterImageView;
    private int dimension;
    private Context context;

    public ClusterRenderer(Activity context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        clusterIconGenerator = new IconGenerator(context);
        iconGenerator = new IconGenerator(context);
        View multiPoint = context.getLayoutInflater().inflate(R.layout.multi_point, null);
        clusterIconGenerator.setContentView(multiPoint);
        clusterImageView = (ImageView) multiPoint.findViewById(R.id.image);
        imageView = new ImageView(context);
        dimension = (int) context.getResources().getDimension(R.dimen.custom_point_image);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(dimension, dimension));
        int padding = (int) context.getResources().getDimension(R.dimen.custom_point_padding);
        imageView.setPadding(padding, padding, padding, padding);
        iconGenerator.setContentView(imageView);
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    @Override
    protected void onClusterItemRendered(PointItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

    @Override
    protected void onBeforeClusterItemRendered(PointItem item, MarkerOptions markerOptions) {
        imageView.setImageResource(item.getIcon());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
                .title(context.getString(R.string.app_name))
                .snippet(item.getLocation());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<PointItem> cluster, MarkerOptions markerOptions) {
        List<Drawable> pointIcon = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
        int width = dimension;
        int height = dimension;
        for (PointItem p : cluster.getItems()) {
            if (pointIcon.size() == 4) break;
            Drawable drawable = context.getResources().getDrawable(p.getIcon());
            drawable.setBounds(0, 0, width, height);
            pointIcon.add(drawable);
        }
        MultiDrawable multiDrawable = new MultiDrawable(pointIcon);
        multiDrawable.setBounds(0, 0, width, height);
        clusterImageView.setImageDrawable(multiDrawable);
        Bitmap icon = clusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

}
