package com.StevenGibson.outdoorcompanion;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Timmer extends AppCompatActivity {

    TextView lbltimer;
    public long counterTimer= 0;
    public int hours = 0;
    public int mins = 0;
    public long secs = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timmer);
        lbltimer = findViewById(R.id.lblremaining);
    }

    public void startCountdown(View view) {

        //converts hours to mins
        EditText et = findViewById(R.id.txthours);
        String temp = et.getText().toString();

        if (!temp.equals("")){
            hours = Integer.parseInt(temp);
            hours = (hours * 60);
            Log.i("Info", "Timer is: " + hours + " in mins");
        }

        else{
            Log.i("Info", "No Hours were entered");
        }
        //converts mins to secs
        et = findViewById(R.id.txtMins);
        temp = et.getText().toString();

        if (!temp.equals("")) {
            mins = Integer.parseInt(temp);
            secs = (mins + hours) * 60;
            Log.i("Info", "Timer is: " + secs + " in Seconds");
        }
        else{
            Log.i("Info", "No Mins were entered");
            secs = (mins + hours) * 60;
        }

        counterTimer = secs*1000;
        countdownStart(counterTimer);




    }

    public void countdownStart(long counter){
       CountDownTimer CDT = new CountDownTimer(counter,1000){

           @Override
           public void onTick(long l) {
               updateTimer((int) l / 1000);
           }

           @Override
           public void onFinish() {
               MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
               mplayer.start();
               Log.i("Info", "Timer done");
           }
       }.start();
    }

    public void updateTimer(int secondsLeft) {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - (minutes * 60);

        String secondString = Integer.toString(seconds);

        if (seconds <= 9) {
            secondString = "0" + secondString;
        }

        lbltimer.setText(Integer.toString(minutes) + ":" + secondString);
    }


}
