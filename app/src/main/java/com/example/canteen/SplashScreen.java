package com.example.canteen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreferences sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        boolean isLoggedIn = sharedPref.getBoolean("IS_LOGGED_IN", false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (isLoggedIn) {
                    intent = new Intent(SplashScreen.this, MainActivity.class);
                } else {
                    intent = new Intent(SplashScreen.this, LoginActivity.class);
                }
                startActivity(intent);
                finish(); // Optional: Finish the splash activity

            }
        }, 5000);

    }
}