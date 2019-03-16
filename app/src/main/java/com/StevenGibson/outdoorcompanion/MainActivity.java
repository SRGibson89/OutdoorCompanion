package com.StevenGibson.outdoorcompanion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity {

    private String UserLocation = "";
    SharedPreferences sharedPreferences;
    Location location;
    double lat, lng;
    String ICE1 ="Select Contact",ICE1Number ="000",ICE2,ICE2Number;

    LocationManager locationManager;
    LocationListener locationListener;

    WifiManager wifiManager;

    Boolean FlareON=false;
    Button btnFlare;



    @RequiresApi(api = Build.VERSION_CODES.O)



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnFlare = findViewById(R.id.Flare);
        loadData();
        getLocation();

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

        String FlareStatus = sharedPreferences.getString("FlareStatus","false");
        if (FlareStatus.equals("true")){
            FlareON = true;
            btnFlare.setBackgroundResource(R.drawable.flareon);
        }
        else{
            FlareON = false;
            btnFlare.setBackgroundResource(R.drawable.flareoff);
        }


        Log.i("Info","ICE1 is :" + ICE1 + " "+ ICE1Number);
        Log.i("Info","ICE2 is :" + ICE2 + " "+ ICE2Number);
        Log.i("info",ICE2);


    }

    public void sendMessage(View view) {

        loadData();
        Context context = getApplicationContext();
        if (ICE1.equals("")) {
            //Context context = getApplicationContext();
            Toast.makeText(context, "No ICE Contact found.\n Message not sent", Toast.LENGTH_SHORT).show();
        }
        else {
            getLocation();
            Log.i("info", "Lat = " + lat + " Long = " + lng);
            String messageToSend = "Hi "+ ICE1 +" I may be in trouble my location is " + lat + " , " + lng;
            String locationLink = " https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng + "" ;
            String number = ICE1Number;

            SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null, null);
            SmsManager.getDefault().sendTextMessage(number, null, locationLink, null, null);
            // context = getApplicationContext();
            //Toast.makeText(context, "SoS Message Sent to "+ICE1, Toast.LENGTH_SHORT).show();
        }
        //ICE2Number = "000";
        if (ICE2.equals("")){
            //Context context = getApplicationContext();
            Toast.makeText(context, "No ICE Contact found.\n Message not sent", Toast.LENGTH_SHORT).show();
            Log.i("info", "No ICE2 Number");
        }
        else {
            getLocation();
            Log.i("info", "Lat = " + lat + " Long = " + lng);

            String messageToSend = "Hi "+ ICE2 +" I may be in trouble my location is " + lat + " , " + lng;
            String locationLink = " https://www.google.com/maps/search/?api=1&query=" + lat + "," + lng + "" ;
            String number = ICE2Number;

            SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null, null);
            SmsManager.getDefault().sendTextMessage(number, null, locationLink, null, null);
            //Context context = getApplicationContext();
            //Toast.makeText(context, "SoS Message Sent to "+ ICE2, Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(context, "SoS Message Sent to " +ICE1+" and "+ ICE2, Toast.LENGTH_SHORT).show();

    }

    public void showSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    //Digital Flare
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showFlare(View view) {

        Flare();


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Flare(){
        Context context = getApplicationContext();

        // Check whether has the write settings permission or not.
        boolean settingsCanWrite = android.provider.Settings.System.canWrite(context);

        if(!settingsCanWrite) {
            // If do not have write settings permission then open the Can modify system settings panel.
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
            startActivity(intent);
        }else {
            // If has permission then show an alert dialog with message.
            //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            //alertDialog.setMessage("You have system write settings permission now.");
            //alertDialog.show();
        }




        if (FlareON == false) {
            FlareON = true;
            sharedPreferences.edit().putString("FlareStatus","true");
            btnFlare.setBackgroundResource(R.drawable.flareon);
            //setWifiTetheringEnabled(FlareON);
           createHotspot();

        }
        else {
            FlareON = false;
            sharedPreferences.edit().putString("FlareStatus","false");
            btnFlare.setBackgroundResource(R.drawable.flareoff);
            //setWifiTetheringEnabled(FlareON);
            //createHotspot();
            destroyHotspot();

        }



    }


    private void createHotspot(){
        HotspotDetails hotspotDetails = new HotspotDetails();
        hotspotDetails.setSsid("Help Me!!");
        hotspotDetails.setPassword("");

        new MyHotspotManager((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).setHotspot(hotspotDetails);
    }
    private void destroyHotspot(){
        Boolean Enabled = checkWifiEnabled();
        if (!Enabled){
            //AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            //alertDialog.setMessage("Wifi is not enabled");
            //alertDialog.show();
            wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
        }
    }
    public boolean checkWifiEnabled() {
        // checks if WiFi is enabled
        return (wifiManager != null && wifiManager.isWifiEnabled());
    }






    public void showTimer(View view) {
        Intent intent = new Intent(this, Timmer.class);
        startActivity(intent);
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

    //Location Permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }



}