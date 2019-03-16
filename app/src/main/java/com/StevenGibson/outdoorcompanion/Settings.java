package com.StevenGibson.outdoorcompanion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

public class Settings extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button btContact1;
    Button btContact2;
    String buttonClicked;
    String ICE1 ="Select Contact",ICE1Number ="000",ICE2 ,ICE2Number;

    private final int REQUEST_CODE=99;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @Override
    /*protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }*/
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    public void loadData(){
        sharedPreferences = this.getSharedPreferences("com.StevenGibson.outdoorcompanion", Context.MODE_PRIVATE);
        ICE1 = sharedPreferences.getString("ICE1","");
        ICE1Number = sharedPreferences.getString("ICE1Number","");
        ICE2 = sharedPreferences.getString("ICE2","");
        ICE2Number = sharedPreferences.getString("ICE2Number","");

        btContact1.setText(ICE1);
        btContact2.setText(ICE2);
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
}
