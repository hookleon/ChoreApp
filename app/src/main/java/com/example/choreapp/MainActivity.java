/*
  MainActivity.java
  -----------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 *
 */
public class MainActivity extends AppCompatActivity {

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Changes the activity to AddMemberActivity when CreateHousehold Button is Clicked
     * @param view user interface components
     */
    public void createHousehold (View view) {
        Intent intent = new Intent(this, AddHouseMemberActivity.class);
        startActivity(intent);
    }

    /**
     *
     * @param view
     */
    public void login (View view) {
        Intent intent = new Intent(this,  LoginActivity.class);
        startActivity(intent);
    }
}