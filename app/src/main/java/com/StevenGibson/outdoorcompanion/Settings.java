package com.StevenGibson.outdoorcompanion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Settings extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button btContact1;
    Button btContact2;
    String buttonClicked;
    String ICE1 ="Select Contact",ICE1Number ="000",ICE2 ,ICE2Number;

    private final int REQUEST_CODE=99;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    Button btnEval;

    @Override
    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestAppPermissions();
        RequestContacts();
        setContentView(R.layout.activity_settings);

        btContact1 = (findViewById(R.id.btnContact1));
        btContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
                buttonClicked = "btContact1";
            }
        });

        btContact2 = findViewById(R.id.btnContact2);
        btContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE);
                buttonClicked = "btContact2";
            }
        });


        Button btSave = findViewById(R.id.btnSave);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveData();
            }
        });

        loadData();

        //testing
        btnEval = (findViewById(R.id.btnEval));
    }

    public void loadData(){
        sharedPreferences = this.getSharedPreferences("com.StevenGibson.outdoorcompanion", Context.MODE_PRIVATE);
        ICE1 = sharedPreferences.getString("ICE1","");
        ICE1Number = sharedPreferences.getString("ICE1Number","");
        ICE2 = sharedPreferences.getString("ICE2","");
        ICE2Number = sharedPreferences.getString("ICE2Number","");

        if (ICE1.equals("")){
            btContact1.setText("Select Contact");
        }
        else{
            btContact1.setText(ICE1);
        }
        if (ICE1.equals("")){
            btContact1.setText("Select Contact");
        }
        else {
            btContact2.setText(ICE2);
        }

        Log.i("Info","ICE1 is :" + ICE1 + " "+ ICE1Number);
        Log.i("Info","ICE2 is :" + ICE2 + " "+ ICE2Number);

    }

        @Override
        public void onActivityResult(int reqCode, int resultCode, Intent data) {
            super.onActivityResult(reqCode, resultCode, data);
            switch (reqCode) {
                case (REQUEST_CODE):
                    if (resultCode == Activity.RESULT_OK) {
                        Uri contactData = data.getData();
                        Cursor c = getContentResolver().query(contactData, null, null, null, null);
                        if (c.moveToFirst()) {
                            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                            String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                            String num = "";
                            String name = "";
                            if (Integer.valueOf(hasNumber) == 1) {
                                Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                                while (numbers.moveToNext()) {
                                    name = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                    num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    Toast.makeText(Settings.this, "Number="+num, Toast.LENGTH_LONG).show();
                                    if (buttonClicked =="btContact1") {
                                        btContact1.setText(name);
                                        ICE1 = name;
                                        ICE1Number = num;

                                    }
                                    else if (buttonClicked =="btContact2"){
                                        btContact2.setText(name);
                                        ICE2 = name;
                                        ICE2Number = num;
                                    }
                                    else{

                                    }

                                }
                            }
                        }
                        break;
                    }
            }
        }

        public void saveData(){

            sharedPreferences.edit().putString("ICE1",ICE1).apply();
            sharedPreferences.edit().putString("ICE1Number",ICE1Number).apply();
            sharedPreferences.edit().putString("ICE2",ICE2).apply();
            sharedPreferences.edit().putString("ICE2Number",ICE2Number).apply();

            AlertDialog alertDialog = new AlertDialog.Builder(Settings.this).create();
            alertDialog.setMessage("You have saved ICE Contacts");
            alertDialog.show();

        }

    private void RequestContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted

            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // the following is used methods are only here to gather test data

    private static final int REQUEST_WRITE_STORAGE = 112;
    Context context = this;


    public void batteryLeft(View view){
        String Data = "This a is a test \n";
        String File = "/Battery.txt";
        int Battery = getBatteryPercentage(this);
        Data = "\n --Testing started "+"Battery percentage is " + String.valueOf(Battery) + "--\n";
        writeToFile(Data,File);
        startTimer();
    }

    private void startTimer() {

        //mEndTime = (int) (System.currentTimeMillis() + secondsLeft);
        btnEval.setAlpha(.5f);
        btnEval.setClickable(false);
        CountDownTimer CDT = new CountDownTimer(3600000,600000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("info","X");
                int Battery = getBatteryPercentage(context);

                String Data = "Battery percentage is " + String.valueOf(Battery) + "\n";
                Log.i("info",Data);
                writeToFile(Data,"/Battery.txt");
            }

            @Override
            public void onFinish() {
                MediaPlayer mplayer = MediaPlayer.create(getApplicationContext(), R.raw.airhorn);
                mplayer.start();
                Log.i("Info", "Timer done");
                btnEval.setAlpha(1);
                btnEval.setClickable(true);
                int Battery = getBatteryPercentage(context);
                String Data = "\n --Testing ended "+"Battery percentage is " + String.valueOf(Battery) + "--\n";
                writeToFile(Data,"/Battery.txt");

            }
        }.start();

    }


    public static int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }

    public void writeToFile(String Data,String Filename) {

        try {
            //makes a directory for Android 8.0 and below
            File root = new File(Environment.getExternalStorageDirectory(), "Outdoor_Companion");
            File root2 = new File(context.getFilesDir(), "Outdoor_Companion");
            if (!root.exists()) {
                root.mkdirs();
            }

            //makes a directory for Android 9.0 and above
            if (!root2.exists()) {
                root2.mkdirs();
            }

            File gpxfile = new File(root, Filename);
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(Data);
            writer.flush();
            writer.close();

            File gpxfile2 = new File(root2, Filename);
            FileWriter writer2 = new FileWriter(gpxfile2, true);
            writer2.append(Data);
            writer2.flush();
            writer2.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

}
