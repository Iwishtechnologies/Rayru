package com.iwish.rayru.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.iwish.rayru.R;
import com.iwish.rayru.other.Session;

import java.util.Map;

import static com.iwish.rayru.config.Constants.FIRST_TIME_COME;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_TIME_OUT = 2000;
    Session session;
    Map data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        session = new Session(this);
        data = session.getShare();

        getSupportActionBar().hide();
        splashTime();
    }


    public void splashTime() {

        new Handler().postDelayed(() -> {

            if (data.get(FIRST_TIME_COME) != null) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(SplashActivity.this, SignupActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }


}