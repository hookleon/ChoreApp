/*
  SettingsActivity.java
  ---------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 *
 */
public class SettingsActivity extends AppCompatActivity {
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    public static final String PREF_HOUSE_ID = "PrefHouseID";

    private String houseID;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();
        houseID = intent.getStringExtra(AddHouseMemberActivity.HOUSE_ID);

        /*OnBackPressedCallback cb = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backNav();
            }
        };*/

    }

    public void editChores(View view) {
        Intent intent = new Intent(this, EditChoreListActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("edit");
        startActivity(intent);
    }

    /**
     *
     * @param view
     */
    public void editMembers(View view) {
        Intent intent = new Intent(this, EditHouseMemberActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("edit");
        startActivity(intent);
    }

    /**
     *
     * @param view
     */
    public void back(View view){
        backNav();
    }

    public void backNav() {
        Intent intent = new Intent(this, ChoreListActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("settings");
        startActivity(intent);
    }
    public void logout(View view) {
        writeString(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public static void writeString(Context context) {
        SharedPreferences exitHouseID = context.getSharedPreferences(PREF_HOUSE_ID, 0);
        SharedPreferences.Editor editor = exitHouseID.edit();
        editor.putString("houseID", "logged out");
        editor.commit();
    }
}