package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChoreListActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    private List<String> choresToAllocate = new ArrayList<>();
    private RecyclerView recView;
    private AddChoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_list);

        Intent intent = getIntent();
        String houseID = intent.getStringExtra(AddChoresActivity.HOUSE_ID);
    }

    public void assignChores() {
        //choresToAllocate
    }
    // Need code to add each member of household as a viewable text with their chore for the week next to them
}
