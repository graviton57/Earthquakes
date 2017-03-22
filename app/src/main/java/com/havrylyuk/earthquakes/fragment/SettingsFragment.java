package com.havrylyuk.earthquakes.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
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

public class SettingsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_CONTINENT = 5758;
    private DatePeriod selectedPeriod;
    private int magnitude;
    PreferencesHelper pf;
    private Spinner spinner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pf = PreferencesHelper.getInstance();
        selectedPeriod = DatePeriod.values()[pf.getDate(getActivity())];
        magnitude = pf.getMagnitude(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.setings_fragment,container,false);
        setupRadioGroup(rootView);
        setupSeekBak(rootView);
        spinner = (Spinner) rootView.findViewById(R.id.spinner_continets);
        Button button = (Button) rootView.findViewById(R.id.button_apply);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pf.setDate(getActivity(), selectedPeriod.ordinal());
                    pf.setMagnitude(getActivity(), magnitude);
                    Toast.makeText(getActivity(), selectedPeriod +" "+ magnitude, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), EarthquakesService.class);
                    getActivity().startService(intent);
                }
            });
        }
        getActivity().getSupportLoaderManager().initLoader(LOADER_CONTINENT, null, this);
        return rootView;
    }
    
    private void setupRadioGroup(View rootView) {
        RadioGroup radioGroup = (RadioGroup) rootView.findViewById(R.id.date_radio_group);
        switch (selectedPeriod) {
            case ALL:
                radioGroup.check(R.id.radio_all_years);
                break;
            case LAST_MONTH:
                radioGroup.check(R.id.radio_last_month);
                break;
            case LAST_YEAR:
                radioGroup.check(R.id.radio_last_year);
                break;
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_all_years:
                        selectedPeriod = DatePeriod.ALL;
                        break;
                    case R.id.radio_last_month:
                        selectedPeriod = DatePeriod.LAST_MONTH;
                        break;
                    case R.id.radio_last_year:
                        selectedPeriod = DatePeriod.LAST_YEAR;
                        break;
                }
            }
        });
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
            if (data != null && data.moveToFirst() ) {
                String[] adapterCols = new String[]{ContinentsEntry.COLUMN_CONTINENT_NAME};
                int[] adapterRowViews = new int[]{android.R.id.text1};
                SimpleCursorAdapter cursorAdapter =
                        new SimpleCursorAdapter(getActivity(), android.R.layout.simple_spinner_item,
                                data, adapterCols, adapterRowViews, 0);
                cursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(cursorAdapter);
            }
        }
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public enum DatePeriod {

        ALL,
        LAST_MONTH,
        LAST_YEAR,
    }
}
