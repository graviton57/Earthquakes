package com.havrylyuk.earthquakes.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import com.havrylyuk.earthquakes.R;
import com.havrylyuk.earthquakes.util.PreferencesHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Igor Havrylyuk on 23.03.2017.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Date date;
    SimpleDateFormat format;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        String prefDate = PreferencesHelper.getInstance().getDate(getActivity());
        format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(prefDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = c.getTime();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        TextView tv = (TextView) getActivity().findViewById(R.id.tv_selected_date);
        if (tv != null) {
            tv.setText(format.format(date));
        }
    }

}
