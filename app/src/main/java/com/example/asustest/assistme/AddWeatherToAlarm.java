package com.example.asustest.assistme;

import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by navin on 8/15/2017.
 */

public class AddWeatherToAlarm {

    TimePicker timePicker;
    DateFormat df;
    Date time1, time2;

    public TextView setTimeText(TextView currentTime)
    {
        int hour = timePicker.getCurrentHour();
        int min = timePicker.getCurrentMinute();



        return currentTime;
    }

    int hour = timePicker.getCurrentHour();
    int min = timePicker.getCurrentMinute();
    String timeNow = ""+hour+":"+String.format("%02d",min);
                //currentTime.setText(timeNow);


}
