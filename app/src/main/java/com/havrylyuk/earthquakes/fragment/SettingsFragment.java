package com.havrylyuk.earthquakes.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.data.EarthquakesContract.ContinentsEntry;
import com.havrylyuk.earthquakes.service.EarthquakesService;
import com.havrylyuk.earthquakes.util.PreferencesHelper;

/**
 * Created by Igor Havrylyuk on 22.03.2017.
 */

public class SettingsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {



    private static final String LOG_TAG = SettingsFragment.class.getSimpleName();
    private static final int LOADER_CONTINENT = 5758;
    private Spinner spinner;

    private PreferencesHelper prefHelper;

    private int magnitude;
    private String selectedDate;
    private long continentId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        prefHelper = PreferencesHelper.getInstance();
        selectedDate = prefHelper.getDate(getActivity());
        magnitude = prefHelper.getMagnitude(getActivity());
        continentId = prefHelper.getContinent(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.setings_fragment,container,false);
        setupDateView(rootView);
        setupSeekBak(rootView);
        setupSpinner(rootView);
        getActivity().getSupportLoaderManager().initLoader(LOADER_CONTINENT, null, this);
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_apply:
                prefHelper.setDate(getActivity(), selectedDate);
                prefHelper.setMagnitude(getActivity(), magnitude);
                prefHelper.setContinent(getActivity(), continentId);
                Toast.makeText(getActivity(), selectedDate +" "+ magnitude, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), EarthquakesService.class);
                getActivity().startService(intent);
                return false;
            default:
                break;
        }
        return false;
    }

    private void setupSpinner(View rootView) {
        spinner = (Spinner) rootView.findViewById(R.id.spinner_continets);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String continent = cursor.getString(cursor.getColumnIndex(ContinentsEntry.COLUMN_CONTINENT_NAME));
                continentId = cursor.getLong(cursor.getColumnIndex(ContinentsEntry.COLUMN_CONTINENT_GEONAMEID));
                Log.d(LOG_TAG,"Position ="+position+" value="+continent + " id="+continentId+ " adapter id="+ id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupDateView(View rootView) {
        TextView tvDate = (TextView) rootView.findViewById(R.id.tv_selected_date);
        if (tvDate != null) {
            tvDate.setText(selectedDate);
        }
        Button dateButton = (Button) rootView.findViewById(R.id.select_date_button);
        if (dateButton != null) {
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment = new DatePickerFragment();
                    newFragment.show(getFragmentManager(),"Date Picker");
                }
            });
        }
    }

    private void setupSeekBak(View rootView) {
        final TextView tvMagnitude  =(TextView) rootView.findViewById(R.id.tv_magnitude_value);
        final SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.magnitude_seek_bar);
        if (seekBar != null && tvMagnitude != null) {
            seekBar.setProgress(magnitude);
            tvMagnitude.setText(getString(R.string.format_magnitude_value, magnitude));
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    magnitude = progress;
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    tvMagnitude.setText(getString(R.string.format_magnitude_value, magnitude));
                }
            });
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_CONTINENT) {
            return new CursorLoader(getActivity(), ContinentsEntry.CONTENT_URI, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_CONTINENT) {
            if (data != null  ) {
                String[] adapterCols = new String[]{ContinentsEntry.COLUMN_CONTINENT_NAME};
                int[] adapterRowViews = new int[]{android.R.id.text1};
                SimpleCursorAdapter cursorAdapter =
                        new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item,
                                data, adapterCols, adapterRowViews, 0);
                cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(cursorAdapter);
                int index = getIndex(spinner, ContinentsEntry.COLUMN_CONTINENT_GEONAMEID, continentId);
                Log.d(LOG_TAG, "found index=" + index);
                spinner.setSelection(index);
            }
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private int getIndex(Spinner spinner, String columnName, long index) {
        if (index <= 0 || spinner.getCount() == 0) {
            return -1; // Not found
        } else {
            Cursor cursor = (Cursor) spinner.getItemAtPosition(0);
            for (int i = 0; i < spinner.getCount(); i++) {
                cursor.moveToPosition(i);
                long id = cursor.getLong(cursor.getColumnIndex(columnName));
                if (id ==index) {
                    return i;
                }
            }
            return -1; // Not found
        }
    }
}
