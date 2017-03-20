package com.havrylyuk.earthquakes.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.adapter.EarthquakesAdapter;
import com.havrylyuk.earthquakes.data.EarthquakesContract.EarthquakesEntry;

/**
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class EarthquakesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EARTHQUAKES_LOADER = 12;
    private EarthquakesAdapter earthquakesAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public EarthquakesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_list,container,false);
        getLoaderManager().initLoader(EARTHQUAKES_LOADER, null, this);
        initRecycler(rootView);
        initSwipe(rootView);
        return rootView;
    }

    private void initSwipe(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(EARTHQUAKES_LOADER, null, EarthquakesFragment.this);
                swipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 2000);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void initRecycler(View rootView) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        earthquakesAdapter = new EarthquakesAdapter(getActivity(), new EarthquakesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(long id) {

            }
        });
        recyclerView.setAdapter(earthquakesAdapter);

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == EARTHQUAKES_LOADER) {
            return new CursorLoader(getActivity(),
                    EarthquakesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    EarthquakesEntry.EARTH_DATE_TIME + " DESC ");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == EARTHQUAKES_LOADER) {
            if (earthquakesAdapter !=null) earthquakesAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == EARTHQUAKES_LOADER) {
            if (earthquakesAdapter !=null) earthquakesAdapter.swapCursor(null);
        }
    }
}
