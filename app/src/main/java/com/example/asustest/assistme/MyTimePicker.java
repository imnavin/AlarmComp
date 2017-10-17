package com.example.asustest.assistme;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by navin on 10/17/2017.
 */

public class MyTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    public static int hoursTP = 0;
    public static int minsTP = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        hoursTP = hourOfDay;
        minsTP = minute;

        Log.i("Hours SET",Integer.toString(hoursTP));
        Log.i("Minutes SET",Integer.toString(minsTP));
    }
}
