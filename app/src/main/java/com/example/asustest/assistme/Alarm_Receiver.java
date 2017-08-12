package com.example.asustest.assistme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Asus on 8/9/2017.
 */

public class Alarm_Receiver extends BroadcastReceiver  {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("We are in the Receiver", "Yay!!");

        //tells the app which button the user has pressed
        String get_your_string = intent.getExtras().getString("extra");//fetch extra strings from the intent

        Log.e("What is the key?", get_your_string);

        Integer get_your_alarm_tone = intent.getExtras().getInt("alarm tone");

        Log.e("The alarm is", get_your_alarm_tone.toString());

        Intent service_intent = new Intent(context, Ringtone_Playing_Service.class);

        service_intent.putExtra("extra", get_your_string);//parse the extra string from the main activity to the ringtone playing service

        service_intent.putExtra("alarm tone", get_your_alarm_tone);//parse the extra integer from the reciever to ringtone playing service
        context.startService(service_intent);
    }
}
