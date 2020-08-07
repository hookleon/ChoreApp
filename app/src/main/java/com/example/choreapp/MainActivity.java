package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Click Create Household button
    public void createHousehold (View view) {
        Intent intent = new Intent(this, AddHouseMemberActivity.class);
        startActivity(intent);
    }

    public void login (View view) {
        Intent intent = new Intent(this,  LoginActivity.class);
        startActivity(intent);
    }




}
