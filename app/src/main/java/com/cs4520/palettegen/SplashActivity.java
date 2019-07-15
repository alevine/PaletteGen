package com.cs4520.palettegen;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This activity simply displays a drawable theme with a picture on it as a splash screen.
 * At the same time it creates an intent for the main activity and loads it.
 * Serves as the splash / intro screen for the app.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
