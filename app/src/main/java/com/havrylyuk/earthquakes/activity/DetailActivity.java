package com.havrylyuk.earthquakes.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.havrylyuk.earthquakes.R;

public class DetailActivity extends AppCompatActivity {

    public static final String DETAIL_POINT_URI = "com.havrylyuk.earthquakes.DETAIL_POINT_URI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }
}
