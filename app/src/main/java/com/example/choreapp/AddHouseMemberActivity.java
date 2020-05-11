package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class AddHouseMemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house_member);

        Intent intent = getIntent();
    }

    ArrayList<TextView> memberDisplay = new ArrayList<>();
    //Can't figure out linear layout, keeps crashing
    //LinearLayout linear = (LinearLayout) findViewById(R.id.linear);

    // Adds name from
    public void addMember (View view) {
        // When clicked, the text will be taken and added as a name of a person in household
        // the newMember will take name from editMember and send it to Firebase
        EditText editMember = (EditText) findViewById(R.id.editMember);
        String newMember = editMember.getText().toString();
        TextView n = new TextView(this);
        n.setText(newMember);
        //memberDisplay.add(n);
        //linear.addView(memberDisplay[n]);
    }

    public void confirmMembers (View view) {
        // Moves to the next page where you pick chores
        Intent intent = new Intent(this, AddChoresActivity.class);
        startActivity(intent);
    }
}
