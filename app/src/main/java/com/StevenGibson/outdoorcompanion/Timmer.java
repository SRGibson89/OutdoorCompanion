package com.StevenGibson.outdoorcompanion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class Timmer extends AppCompatActivity {

    TextView lbltimer;
    TextView currentLocation;
    Button btnStart;
    public boolean counterrunning;
    public long counterTimer = 0;
    public int hours = 0;
    public int mins = 0;
    public long secs = 0;
    private int secondsLeft;
    private int mEndTime;
    SharedPreferences sharedPreferences;
    CountDownTimer CDT;

    Location location;
    double lat, lng;


    LocationManager locationManager;
    LocationListener locationListener;

    String ICE1 ="Select Contact",ICE1Number ="000",ICE2,ICE2Number;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timmer);
        lbltimer = findViewById(R.id.lblremaining);
        currentLocation = findViewById(R.id.lblCLocat2);
        btnStart = findViewById(R.id.BtnStart);
        sharedPreferences = this.getSharedPreferences("com.StevenGibson.outdoorcompanion", Context.MODE_PRIVATE);
        getLocation();
        ActivityCompat.requestPermissions(Timmer.this,
                new String[]{Manifest.permission.SEND_SMS},
                1);
        //createview();
        if (counterrunning){
            btnStart.setText("Stop the counter!");

        }
        else{
             btnStart.setText("Start the counter!");
        }
        loadData();

    }








    public void startCountdown(View view) {
        //createview();





        if (counterrunning == true){
            sharedPreferences.edit().putBoolean("CounterRunning",false).apply();
            if (CDT != null) {
                CDT.cancel();
                CDT = null;
                Log.i("Info", "The counter is cancelled");
            }

            btnStart.setText("Start!");
            counterrunning = false;
            lbltimer.setText("Timer not started");

        }

        else {

            sharedPreferences.edit().putBoolean("CounterRunning", true).apply();
            counterrunning = true;
            btnStart.setText("Stop!");


            //converts hours to mins
            EditText et = findViewById(R.id.txthours);
            String temp = et.getText().toString();

            if (!temp.equals("")) {
                hours = Integer.parseInt(temp);
                hours = (hours * 60);
                Log.i("Info", "Timer is: " + hours + " in mins");
            } else {
                Log.i("Info", "No Hours were entered");
            }
            //converts mins to secs
            et = findViewById(R.id.txtMins);
            temp = et.getText().toString();

            if (!temp.equals("")) {
                mins = Integer.parseInt(temp);
                secs = (mins + hours) * 60;
                Log.i("Info", "Timer is: " + secs + " in Seconds");
            } else {
                Log.i("Info", "No Mins were entered");
                secs = (mins + hours) * 60;
            }

            counterTimer = secs * 1000;
            secondsLeft = (int) counterTimer;
            startTimer();
            //countdownStart(counterTimer);

            /*//start the count down
            CDT = new CountDownTimer(counterTimer,1000){

                @Override
                public void onTick(long l) {
                    secondsLeft =(int) l / 1000;
                    updateTimer();
                    //counterrunning = true;
                    Log.i("Info","X");
                }

                @Override
                public void onFinish() {
                    MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
                    mplayer.start();
                    sendSMS();
                    sharedPreferences.edit().putBoolean("CounterRunning",false).apply();
                    counterrunning = sharedPreferences.getBoolean("CounterRunning",false);
                    Log.i("Info", "Timer done");
                    Log.i("Info", "Is the counter Running: " + String.valueOf(counterrunning));
                    btnStart.setText("Start!");
                }
            }.start();
        */
        }

    }

    public void createview(){

        //counterrunning = sharedPreferences.getBoolean("CounterRunning",false);

    }


    private void startTimer() {
        mEndTime = (int) (System.currentTimeMillis() + secondsLeft);

        CDT = new CountDownTimer(secondsLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondsLeft = (int) millisUntilFinished;
                counterrunning = true;
                Log.i("info","X");
                Log.i("Info", "Is the counter Running: " + String.valueOf(counterrunning));
                getLocation();
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                counterrunning = false;
                //getLocation();
                MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
                mplayer.start();
                sendMessage();

                Log.i("Info", "Timer done");
                Log.i("Info", "Is the counter Running: " + String.valueOf(counterrunning));
                lbltimer.setText("Countdown Finsihed");
                btnStart.setText("Start!");

            }
        }.start();

        counterrunning = true;
        //updateButtons();
    }


    private void updateCountDownText() {
        int minutes = (int) (secondsLeft / 1000) / 60;
        int seconds = (int) (secondsLeft / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        lbltimer.setText(timeLeftFormatted);

    }


    /*public void countdownStart(int counter){
        mEndTime = (int) (System.currentTimeMillis() + counter);

        CDT = new CountDownTimer(counter,1000){

           @Override
           public void onTick(long l) {
               secondsLeft =(int) l / 1000;
               updateTimer();
           }

           @Override
           public void onFinish() {
               MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
               mplayer.start();
               sendSMS();
               counterrunning = false;
               Log.i("Info", "Timer done");
               Log.i("Info", "Is the counter Running: " + String.valueOf(counterrunning));
               btnStart.setText("Start!");
           }
       }.start();
    }*/

    public void updateTimer() {
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - (minutes * 60);

        String secondString = Integer.toString(seconds);

        if (seconds <= 9) {
            secondString = "0" + secondString;
        }

        lbltimer.setText(Integer.toString(minutes) + ":" + secondString);
    }

    public void loadData(){
        sharedPreferences = this.getSharedPreferences("com.StevenGibson.outdoorcompanion", Context.MODE_PRIVATE);
        ICE1 = sharedPreferences.getString("ICE1","Select Contact");
        ICE1Number = sharedPreferences.getString("ICE1Number","000");
        ICE2 = sharedPreferences.getString("ICE2","Select Contact");
        ICE2Number = sharedPreferences.getString("ICE2Number","");

        if (ICE1.equals("Select Contact")){
            ICE1 = "";
            ICE1Number="";
        }
        if (ICE2.equals("Select Contact")){
            ICE2 = "";
            ICE2Number="";
        }

        Log.i("Info","ICE1 is :" + ICE1 + " "+ ICE1Number);
        Log.i("Info","ICE2 is :" + ICE2 + " "+ ICE2Number);
        Log.i("info",ICE2);

    }

    public void sendMessage() {
        if (ICE1.equals("")) {
            Context context = getApplicationContext();
            Toast.makeText(context, "No ICE Contact found.\n Message not sent", Toast.LENGTH_SHORT).show();
        } else {
            getLocation();
            Log.i("info", "Lat = " + lat + " Long = " + lng);
            String messageToSend = "Hi " + ICE1 + " I may be in trouble my location is " + lat + " , " + lng;
            String locationLink = " https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng + "";
            String number = ICE1Number;

            SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null, null);
            SmsManager.getDefault().sendTextMessage(number, null, locationLink, null, null);
            Context context = getApplicationContext();
            Toast.makeText(context, "SoS Message Sent to " + ICE1, Toast.LENGTH_SHORT).show();
        }
        //ICE2Number = "000";
        if (ICE2.equals("")) {
            Context context = getApplicationContext();
            Toast.makeText(context, "No ICE Contact found.\n Message not sent", Toast.LENGTH_SHORT).show();
            Log.i("info", "No ICE2 Number");
        } else {
            getLocation();
            Log.i("info", "Lat = " + lat + " Long = " + lng);

            String messageToSend = "Hi " + ICE2 + " I may be in trouble my location is " + lat + " , " + lng;
            String locationLink = " https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng + "";
            String number = ICE2Number;

            SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null, null);
            SmsManager.getDefault().sendTextMessage(number, null, locationLink, null, null);
            Context context = getApplicationContext();
            Toast.makeText(context, "SoS Message Sent to " + ICE2, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(Timmer.this, "Permission denied to Send SMS", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putBoolean("MyBoolean", counterrunning);
        savedInstanceState.putInt("endTime", mEndTime);
        savedInstanceState.putInt("millisLeft", secondsLeft);

    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        counterrunning = savedInstanceState.getBoolean("MyBoolean");
        secondsLeft = savedInstanceState.getInt("millisLeft");

        updateTimer();

        if (counterrunning) {
            mEndTime = savedInstanceState.getInt("endTime");
            secondsLeft = (int) (mEndTime - System.currentTimeMillis());
            startTimer();
        }

    }

    public void getLocation() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);




        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        lat= location.getLatitude() ; lng = location.getLongitude();


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //LatLng userLocation = new LatLng(-31 ,151);
                //Log.i("Location",location.toString());
                lat= location.getLatitude() ; lng = location.getLongitude();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }


    }


}
