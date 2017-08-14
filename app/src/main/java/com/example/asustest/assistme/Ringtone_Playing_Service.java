package com.example.asustest.assistme;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class Ringtone_Playing_Service extends Service {

    MediaPlayer media_song;
    int startId;
    boolean isRunning;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);

        String state = intent.getExtras().getString("extra");
        Log.e("Ringtone state is: ", state);

        Integer alarm_tone_id = intent.getExtras().getInt("alarm tone");
        Log.e("alarm tone is: ", alarm_tone_id.toString());


/*        //Notifications

            NotificationManager notify_manager =
                    (NotificationManager)getSystemService(NOTIFICATION_SERVICE);//Set up notification service

            Intent intent_main_activity =
                    new Intent(this.getApplicationContext(), MainActivity_clk.class);//Set up an intent that goes to the main activity

            PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0, intent_main_activity, 0);
            //stackBuilder=TaskStackBuilder.create()

            //notification parameters
            Notification notification_popup = new Notification.Builder(this)
                    .setContentTitle("Alarm is going Off!")
                    .setContentText("Click me!")
                    .setContentIntent(pending_intent_main_activity)
                    .setAutoCancel(true)
                    .build();*/




        assert state != null;
        if (state.equals("alarm on")){
            startId = 1;
        }
        else if (state.equals("alarm off")){
            startId = 0;
            Log.e("Start Id is: ", state);
        }
        else
            startId = 0;



        //Conditions to start or stop the ringtone

        //When no music is playing and the user presses the "alarm on" button
        //music should start playing
        if (!this.isRunning && startId == 1){

            Log.e("there is no music", "you want to start");

            this.isRunning = true;
            this.startId = 0;

            /*notify_manager.notify(0, notification_popup);//start the notification*/

            //alarm tone sound depending on the selected one from the spinner

            if (alarm_tone_id == 0){

                media_song = MediaPlayer.create(this, R.raw.apple);
                media_song.start();
            }
            else if (alarm_tone_id == 1){
                media_song = MediaPlayer.create(this, R.raw.coolest);
                media_song.start();
            }
            else if (alarm_tone_id == 2){
                media_song = MediaPlayer.create(this, R.raw.nokia);
                media_song.start();
            }
            else if (alarm_tone_id == 3){
                media_song = MediaPlayer.create(this, R.raw.nokiatwo);
                media_song.start();
            }
            else if (alarm_tone_id == 4){
                media_song = MediaPlayer.create(this, R.raw.sweetalarm);
                media_song.start();
            }
            else
                media_song = MediaPlayer.create(this, R.raw.nokia);
                media_song.start();


        }
        //When music is playing and the user presses the "alarm off" button
        //music should stop playing
        else if (this.isRunning && startId == 0){

            Log.e("there is music", "you want to end");

            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;

        }
        //When no music is playing and the user presses the "alarm off" button
        //do nothing
        else if (!this.isRunning && startId == 0){

            Log.e("there is no music", "you want to end");

            this.isRunning = false;
            this.startId = 0;

        }
        //When music is playing and the user presses the "alarm on" button
        //do nothing
        else if (this.isRunning && startId == 1){

            Log.e("there is music", "you want to start");

            this.isRunning = true;
            this.startId = 1;
        }
        //just to catch the odd event
        else
            Log.e("else", "somehow you've reached this");



        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

        // Tell the user we stopped.
        Log.e("On Destroy called", "Successful !");

        super.onDestroy();
        this.isRunning = false;
    }


}
