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
import com.havrylyuk.earthquakes.adapter.CountriesAdapter;
import com.havrylyuk.earthquakes.data.EarthquakesContract;

/**
 *
 * Created by Igor Havrylyuk on 20.03.2017.
 */

public class CountriesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int COUNTRIES_LOADER = 11;
    private CountriesAdapter countriesAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    public CountriesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_list,container,false);
        getLoaderManager().initLoader(COUNTRIES_LOADER, null, this);
        initRecycler(rootView);
        initSwipe(rootView);
        return rootView;
    }

    private void initSwipe(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getSupportLoaderManager().restartLoader(COUNTRIES_LOADER, null, CountriesFragment.this);
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
        countriesAdapter = new CountriesAdapter(new CountriesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(long id) {

            }
        });
        recyclerView.setAdapter(countriesAdapter);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == COUNTRIES_LOADER) {
            return new CursorLoader(getActivity(),
                    EarthquakesContract.CountriesEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == COUNTRIES_LOADER) {
            if (countriesAdapter !=null) countriesAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == COUNTRIES_LOADER) {
            if (countriesAdapter !=null) countriesAdapter.swapCursor(null);
        }
    }
}
