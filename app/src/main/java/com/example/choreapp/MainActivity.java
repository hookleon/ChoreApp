/*
  MainActivity.java
  -----------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt  
 */
/** \mainpage My Personal Index Page
 *
 * \section intro_sec Introduction
 *
 * This is the introduction.
 *
 * \section install_sec Installation
 *
 * \subsection step1 Step 1: Opening the box
 *
 * etc...
 */
package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * This is the MainActivity that runs when the app starts
 */
public class MainActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";  //Passes houseid to next activity so chores can be added to activity
    public static final String PREF_HOUSE_ID = "PrefHouseID";

    /**
     * The app will attempt to login if a houseID is stored on the phone
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String houseID = readString(this);
        TextView test = findViewById(R.id.test);
        test.setText(houseID);

        attemptLogin(houseID);
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
     * Changes the activity to LoginActivity when login button is clicked
     * @param view
     */
    public void login (View view) {
        Intent intent = new Intent(this,  LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Reads the houseID stored in a shared preference file. Used for quick startup once an account is created
     * @param context
     * @return houseID inside shared preference
     */
    public static String readString(Context context) {
        SharedPreferences exitHouseID = context.getSharedPreferences(PREF_HOUSE_ID, 0);
        String houseID = exitHouseID.getString("houseID", "exit");
        return houseID;
    }

    /**
     * Attempts to login to a previously created account if a houseID is stored in a shared preference file
     * @param hid the houseID
     */
    public void attemptLogin(final String hid) {
        mRef.child("groups").child(hid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    proceedLogin(hid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Empty
            }
        });
    }

    /**
     * If the houseID points to an account, it changes to ChoreListActivity
     * @param hid the houseID
     */
    public void proceedLogin(String hid){
        Intent intent = new Intent(this, ChoreListActivity.class);
        intent.putExtra(HOUSE_ID, hid);
        intent.setAction("savedLogin");
        startActivity(intent);
    }
}
