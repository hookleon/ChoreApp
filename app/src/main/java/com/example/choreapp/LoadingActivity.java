/*
  LoadingActivity.java
  --------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
 * LoadingActivity may end up being the first screen you say. Will allow a nicer opening before logging in using shared preference login
 */
public class LoadingActivity extends AppCompatActivity {

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
    }
}
