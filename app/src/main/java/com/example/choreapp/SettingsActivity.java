package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    private String houseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        houseID = intent.getStringExtra(AddHouseMemberActivity.HOUSE_ID);
    }

    /*public void editChores(View view) {
        Intent intent = new Intent(this, AddChoresActivity.class);
        startActivity(intent);
    }*/

    public void editMembers(View view) {
        Intent intent = new Intent(this, EditHouseMemberActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("edit");
        startActivity(intent);
    }

    public void back(View view){
        Intent intent = new Intent(this, ChoreListActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("settings");
        startActivity(intent);
    }
}
