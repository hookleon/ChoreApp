package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddChoresActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    private String houseID;
    private ArrayList<String> choresToAllocate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chores);

        Intent intent = getIntent();
        houseID = intent.getStringExtra(AddHouseMemberActivity.HOUSE_ID);
    }

    public void addChore (View view){
        //Need code that will add selected chore to the list of chores house will use
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        String chore = spinner.getSelectedItem().toString();
        choresToAllocate.add(chore);
    }

    public void confirmChores (View view) {
        mRef.child("groups").child(houseID).child("chores").setValue(choresToAllocate);
        Intent intent = new Intent(this, ChoreListActivity.class);
        startActivity(intent);
    }
}
