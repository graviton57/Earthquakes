package com.havrylyuk.earthquakes.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
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

    public static final String DATE_PICKER_TAG = "DATE_PICKER_TAG";

    public interface OnChangeDateListener{
        void onChangeDate(String newDate);
    }

    private Date date;
    private SimpleDateFormat format;
    private OnChangeDateListener listener;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (OnChangeDateListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment  must "
                    +"implement DatePickerFragment OnChangeDateListener");
        }
    }

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
        if (listener != null) {
            final Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            listener.onChangeDate(format.format(c.getTime()));
        }
    }

}
