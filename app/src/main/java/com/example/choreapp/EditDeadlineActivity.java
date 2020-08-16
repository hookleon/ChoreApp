package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Accessed through the settings page, this class is used for editing the deadline date for
 * the households chores.
 */
public class EditDeadlineActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    public static final String PREF_HOUSE_ID = "PrefHouseID";
    private String houseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_deadline);

            Intent intent = getIntent();
            houseID = intent.getStringExtra(AddHouseMemberActivity.HOUSE_ID);
        }

    /**
     * Takes the user inputted dates and converts them to the correct date format.
     * Passes the final date string onto the database.
     * @param view used for the event handling of the submit button.
     */
    public void submitDates(View view){
            Intent intent = new Intent(this, ChoreListActivity.class);
            EditText dateText = (EditText) findViewById(R.id.dateInput);
            EditText timeText = (EditText) findViewById(R.id.timeInput);
            String dateInput = dateText.getText().toString();
            String timeInput = timeText.getText().toString();
            String finishedDate = dateInput + " " + timeInput+ ":0";
            mRef.child("groups").child(houseID).child("deadline").setValue(finishedDate);
            writeString(this, houseID);
            intent.putExtra(HOUSE_ID, houseID);
            intent.setAction("create");
            startActivity(intent);
        }

    /**
     *
     * @param context
     * @param hid
     */
    public static void writeString(Context context, String hid) {
            SharedPreferences exitHouseID = context.getSharedPreferences(PREF_HOUSE_ID, 0);
            SharedPreferences.Editor editor = exitHouseID.edit();
            editor.putString("houseID", hid);
            editor.commit();
        }
    }


