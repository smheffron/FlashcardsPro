package com.merlinsbeard.flashcardspro.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.merlinsbeard.flashcardspro.R;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        // Activity does not need an action bar, hide it
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        // Retrieve user ID from SharedPreferences if it exists
        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        final int id = preferences.getInt("userId", -1);
        final LaunchActivity thisActivity = this;

        // Show splash screen with app logo until timer runs out
        CountDownTimer timer = new CountDownTimer(800, 800) {

            @Override
            public void onTick(long l) {
                // Do nothing
            }

            @Override
            public void onFinish() {
                // If user was previously logged in, navigate to FlashcardSetActivity, otherwise, navigate to LoginActivity
                // Either way, clear the back stack
                if(id != -1) {
                    Intent intent = new Intent(thisActivity, FlashcardSetActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(thisActivity, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        }.start();
    }
}
