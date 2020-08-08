/*
  MemberProfile.java
  ------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

/**
 * Creates instance object to lin the app with the Firebase database.
 */
public class MemberProfileActivity extends AppCompatActivity {
    //private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //private DatabaseReference mRef = database.getReference();

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_profile);
        //Intent intent = getIntent();
        //String membID = intent.getStringExtra(ChoreListActivity.MEMB_ID);
    }
}