package com.example.chemirmir_justus_s2038641;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //remove actionbar
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start your main activity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                // Finish the splash activity
                finish();
            }
        }, SPLASH_DELAY);

    }
}