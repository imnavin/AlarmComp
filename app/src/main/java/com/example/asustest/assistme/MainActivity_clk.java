package com.example.asustest.assistme;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;
import java.util.Calendar;


public class MainActivity_clk extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    AlarmManager alarm_manager;
    TimePicker alarm_time_picker;
    TextView update_text;
    Context context;
    PendingIntent pending_intent;
    int alarm_tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_clk);
        this.context = this;

        alarm_manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm_time_picker = (TimePicker)findViewById(R.id.timePicker);
        update_text = (TextView)findViewById(R.id.update_alarm);
        final Calendar calendar = Calendar.getInstance(); //Create an instance of the calendar
        final Intent my_intent = new Intent(this.context, Alarm_Receiver.class);//Create an intent to the alarm receiver class


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.alarms_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);


        Button alarm_on = (Button)findViewById(R.id.alarm_on);

        //Create an onclick listener to start the alarm
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar.set(Calendar.HOUR_OF_DAY, alarm_time_picker.getHour());//set calendar instance with hours and minutes on the time picker
                calendar.set(Calendar.MINUTE, alarm_time_picker.getMinute());

                int hour = alarm_time_picker.getHour();
                int minute = alarm_time_picker.getMinute();


                String hour_string = String.valueOf(hour);
                String minute_string = String.valueOf(minute);

                if (hour > 12){

                    hour_string = String.valueOf(hour - 12);
                }

                if (minute < 10){

                    minute_string = "0" +String.valueOf(minute);

                }

                set_alarm_text("Alarm Set to " +"0"+hour_string+":" +minute_string);//changes the text in the update text box

                my_intent.putExtra("extra", "alarm on");//tells the clock that the alarm on button is pressed, putting extra string to my_intent

                my_intent.putExtra("alarm tone", alarm_tracks);//tell the app that you wanta certain value from the spinner

                Log.e("The alarm id is", String.valueOf(alarm_tracks));

                pending_intent = PendingIntent.getBroadcast(MainActivity_clk.this, 0,
                                my_intent, PendingIntent.FLAG_UPDATE_CURRENT);//Create a pending intent

                alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent );

            }
        });


        Button alarm_off = (Button)findViewById(R.id.alarm_off);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                set_alarm_text("Alarm Off");//changes the text in the update text box

                alarm_manager.cancel(pending_intent);

                my_intent.putExtra("extra", "alarm off");//tells the clock that the alarm off button is pressed, putting extra string to my_intent

                my_intent.putExtra("alarm tone",alarm_tracks);//prevent crashes in a null point exception

                sendBroadcast(my_intent);//Stops the ringtone

            }
        });


    }

    private void set_alarm_text(String output) {

        update_text.setText(output);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

       /* long newId = id;
        ++newId;*/

        Toast.makeText(parent.getContext(), "spinner item is "+id, Toast.LENGTH_SHORT).show();
        alarm_tracks = (int)id;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
