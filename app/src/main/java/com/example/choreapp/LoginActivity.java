/*
  LoginActivity.java
  ------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * LoginActivity allows users to enter a houseID that allows access to their previously created household
 */
public class LoginActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    public static final String PREF_HOUSE_ID = "PrefHouseID";

    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";  //Passes houseid to next activity so chores can be added to activity
    public String hid;

    /**
     * Creates the UI of the login screen
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Runs when the login button is clicked. Checks what is in editHID and logs in if houseID exists in database
     * @param view
     */
    public void loginClick(final View view){
        EditText editHID = (EditText) findViewById(R.id.editHID);
        hid = editHID.getText().toString();
        mRef.child("groups").child(hid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    proceedLogin();
                }
                else{
                    Context context = getApplicationContext();
                    CharSequence text = hid + " doesn't exist";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Empty
            }
        });
    }

    /**
     * Runs when houseID entered exists in the database
     */
    public void proceedLogin(){
        writeString(this, hid);
        Intent intent = new Intent(this, ChoreListActivity.class);
        intent.putExtra(HOUSE_ID, hid);
        intent.setAction("login");
        startActivity(intent);
    }

    /**
     * Stores houseID of login into the shared preferences file for easy relogin
     * @param context
     * @param hid houseID to store in shared preferences
     */
    public static void writeString(Context context, String hid) {
        SharedPreferences exitHouseID = context.getSharedPreferences(PREF_HOUSE_ID, 0);
        SharedPreferences.Editor editor = exitHouseID.edit();
        editor.putString("houseID", hid);
        editor.commit();
    }
}