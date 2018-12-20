package com.workforce.app.ec.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.workforce.app.ec.R;
import com.workforce.app.ec.config.AppPreferences;

import java.util.Timer;
import java.util.TimerTask;



public class SplashActivity extends AppCompatActivity {
    private AppPreferences app;


    // Set the duration of the splash screen
    private static final long SPLASH_SCREEN_DELAY = 3000;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                app = new AppPreferences(getApplicationContext());
                user = FirebaseAuth.getInstance().getCurrentUser();

                if(app.getTour().equals("0"))
                {
                    Intent intent = new Intent(SplashActivity.this, TourActivity.class);
                    startActivity(intent);
                    finish();

                }else {
                    if (user != null) {
                        // User is signed in
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // No user is signed in
                        Intent mainIntent = new Intent().setClass(
                                SplashActivity.this, LoginActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }
                }

            }
        };

        // Simulate a long loading process on application startup.
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
}
