package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

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

    //Leon Hook
}
