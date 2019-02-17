package com.StevenGibson.outdoorcompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "Blah";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, SOS.class);

        String message = "blah Blah";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void showSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void showFlare(View view) {
        Intent intent = new Intent(this, Flare.class);
        startActivity(intent);
    }

    public void showTimer(View view) {
        Intent intent = new Intent(this, Timmer.class);
        startActivity(intent);
    }
}
