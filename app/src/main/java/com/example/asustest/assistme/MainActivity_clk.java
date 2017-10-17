package com.example.asustest.assistme;

import android.app.AlarmManager;
import android.os.AsyncTask;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import weathercomp.data.JSONWeatherParser;
import weathercomp.data.WeatherHttpClient;
import weathercomp.model.BadWeather;
import weathercomp.model.Weather;


public class MainActivity_clk extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    //Alarm >>>>>>>>>>>>>>>>>>>
    AlarmManager alarm_manager;
    TextView update_text;
    Context context;
    PendingIntent pending_intent;
    int alarm_tracks;
    DateFormat df;
    Date time1, time2;
    long delay = 900000;
    String timeNow;
    String destAddress;
    String parsedata="";
    String am_pm = "AM";

    int hour = 0;
    int minute = 0;

    //Weather >>>>>>>>>>>>>>>>>>>>>>>
    private boolean weatherCondition;
    private String myAppId = "dcb6553bfccc040683d9917eedd6cfbe";
    Weather weather = new Weather();
    BadWeather badWeather = new BadWeather();

    //Maps >>>>>>>>>>>>>>>>>>>>>>>>>>
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private LatLng dest, current;
    private int distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_clk);
        this.context = this;

        alarm_manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        update_text = (TextView)findViewById(R.id.update_alarm);
        final Calendar calendar = Calendar.getInstance(); //Create an instance of the calendar
        final Intent my_intent = new Intent(this.context, Alarm_Receiver.class);//Create an intent to the alarm receiver class

        //country=(EditText)findViewById(R.id.txt_Country);
        //city=(EditText)findViewById(R.id.txt_City);

        //WEATHER >>>>>>>>>>>>>>>>>>>>>
        renderWeatherData("Colombo,LK");
        //ERROR
        //Colombo,LK
        //Spokane,US

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.alarms_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);


        //turn_on
        Button alarm_on = (Button)findViewById(R.id.alarm_on);

        //Create an onclick listener to start the alarm
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(hour == 0 && minute == 0 && dest == null)) {

                    parsedata = extractCity(destAddress) + ",LK";
                    renderWeatherData(parsedata);

//                int hour = alarm_time_picker.getHour();
//                int minute = alarm_time_picker.getMinute();

                    //ADD TIME
                    hour = MyTimePicker.hoursTP;
                    minute = MyTimePicker.minsTP;

                    Log.i("Hours PASS : ", Integer.toString(hour));
                    Log.i("Mins PASS : ", Integer.toString(minute));

                    //weatherCondition=false;
                    if (weatherCondition) {
                        Log.i("Weather data PASS : ",Boolean.toString(weatherCondition));
                        minute = minute - 15;
                        if (minute < 0) {
                            minute = 60 + minute;
                            hour = hour - 1;
                        }
                    }


                    calendar.set(Calendar.HOUR_OF_DAY, hour);//set calendar instance with hours and minutes on the time picker
                    calendar.set(Calendar.MINUTE, minute);


                    String hour_string = String.valueOf(hour);
                    String minute_string = String.valueOf(minute);

                    if (hour > 12) {

                        hour_string = String.valueOf(hour - 12);
                        am_pm = "PM";
                    }

                    if (minute < 10) {

                        minute_string = "0" + String.valueOf(minute);

                    }

                    set_alarm_text(hour_string + ":" + minute_string + am_pm);//changes the text in the update text box

                    my_intent.putExtra("extra", "alarm on");//tells the clock that the alarm on button is pressed, putting extra string to my_intent
                    my_intent.putExtra("alarm tone", alarm_tracks);//tell the app that you want a certain value from the spinner

                    Log.e("The alarm id is", String.valueOf(alarm_tracks));

                    pending_intent = PendingIntent.getBroadcast(MainActivity_clk.this, 0,
                            my_intent, PendingIntent.FLAG_UPDATE_CURRENT);//Create a pending intent

                    alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending_intent);

                }

                else{
                    //Alert the user to enter the time or the Destination
                    Log.e("FAIL","Alarm set FAIL");
                    Log.i("FAILED",Integer.toString(hour));
                    Log.i("FAILED",Integer.toString(minute));
                    Log.i("FAILED",dest.toString());
                }
            }
        });


        //turn_off
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


        //renderWeatherData("Spokane,US"); //ERROR
        //Colombo,LK
        //Spokane,US


    }

    public String extractCity(String fullAddress){
        if(!(fullAddress == null)){
            String[] parts = fullAddress.split(", ");
            String extracted = parts[1];
            Log.i("Extracted City:", extracted);
            return extracted;
        }
        else{
            Log.i("ERROR: ", "Can't extract city cos the String provided is NULL");
            return null;
        }
    }

    /*
    FRAGMENT
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                dest = place.getLatLng();
                destAddress = place.getAddress().toString();
                Log.i("Destination", "Place: " + place.getName());
                Log.i("City", extractCity(place.getAddress().toString()));
                Log.i("Address", place.getAddress().toString());
                Log.i("LatLng",dest.toString());

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Destination", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /*
        *
        * WEATHER *********************************
        *
        * */
    public void renderWeatherData(String city){

        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute(new String[]{city+"&appid="+myAppId}); //FIX if needed
        //weatherTask.execute(new String[]{city+"&appid=dcb6553bfccc040683d9917eedd6cfbe"});

    }

    public void onClick(View view) {

        switch (view.getId()){

            case R.id.btn_location:
            {
                // AUTOCOMPLETE FRAGMENT
                try {
                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                            .setCountry("LK")
                            .build();

                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setFilter(typeFilter)
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
            break;

            case R.id.btn_time:
            {
                DialogFragment newFragment = new MyTimePicker();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
            break;
        }

    }

    private class WeatherTask extends AsyncTask<String, Void, Weather>{

        @Override
        protected Weather doInBackground(String... params) {
            //data hold the whole StringBuffer that we returned from WeatherHttpClient class
            String data = ((new WeatherHttpClient()).getWeatherData(params[0]));
            weather = JSONWeatherParser.getWeather(data);

            //Log.v("Data : ",weather.place.getCity());
            //Log.v("Data : ",weather.currentCondition.getDescription());
            String weatherSample = weather.currentCondition.getDescription();

            if ((badWeather.isCloudy(weatherSample)) || (badWeather.isRaining(weatherSample))){
                weatherCondition = true;
                Log.v("Good or Bad : true ", String.valueOf(weatherCondition));
            }
            else{
                weatherCondition = false;
                Log.v("Good or Bad : false ", String.valueOf(weatherCondition));
            }

            Log.v("Good or Bad : ", weather.currentCondition.getDescription());

            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);
        }
    }
    /*
    *
    * WEATHER **********************************
    *
    * */

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
