package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddHouseMemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house_member);

        Intent intent = getIntent();
    }

    // Adds name from
    public void addMember (View view) {
        Intent intent = new Intent(this, AddChoresActivity.class);
        EditText editMember = (EditText) findViewById(R.id.editMember);
        String newMember = editMember.getText().toString();
        // the newMember will take name from editMember and send it to Firebase
        startActivity(intent);
    }
}
