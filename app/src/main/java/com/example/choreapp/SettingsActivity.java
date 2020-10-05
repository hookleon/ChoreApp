/*
  SettingsActivity.java
  ---------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SettingsActivity shows a menu of settings users can adjust
 */
public class SettingsActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    public static final String PREF_HOUSE_ID = "PrefHouseID";

    private String houseID;
    private List<Member> members = new ArrayList<>();
    private List<String> choresToAllocate = new ArrayList<>();

    /**
     * Runs when SettingsActivity first opens
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        houseID = readString(this);
        final TextView textHID = findViewById(R.id.textHID);
        textHID.setText("HID: " + houseID);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Sets up the data so that chores can be reassigned when Reshuffle button is pressed
     * @param dataSnapshot the snapshot of database storing all info
     */
    private void showData(DataSnapshot dataSnapshot) {
        members.clear();
        choresToAllocate.clear();

        DataSnapshot dsMems = dataSnapshot.child("groups").child(houseID).child("members");
        DataSnapshot dsChores = dataSnapshot.child("groups").child(houseID).child("chores");
        DataSnapshot dsMemChores;
        long nMems = dsMems.getChildrenCount();
        long nChores = dsChores.getChildrenCount();
        long nMemChores;

        String name = "";
        String id = "";
        String hid = "";

        //Gets member names and info from database
        for (int i = 0; i < nMems; i++){
            name = dsMems.child(String.valueOf(i)).child("name").getValue(String.class);
            id = dsMems.child(String.valueOf(i)).child("id").getValue(String.class);
            hid = dsMems.child(String.valueOf(i)).child("houseID").getValue(String.class);
            members.add(new Member(name,id,hid));

            nMemChores = dsMems.child(String.valueOf(i)).child("chores").getChildrenCount();
            dsMemChores = dsMems.child(String.valueOf(i)).child("chores");
            for(int k = 0; k < nMemChores; k++){
                members.get(i).addChore(dsMemChores.child(String.valueOf(k)).getValue(String.class));
            }
        }

        String chores = "";
        //Gets chores to allocate from database
        for(int i = 0; i < nChores; i++){
            chores = dsChores.child(String.valueOf(i)).getValue(String.class);
            choresToAllocate.add(chores);
        }
    }

    /**
     * Reshuffles the chores using the method assignChores()
     * @param view
     */
    public void reshuffle(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reshuffle chores")
                .setMessage("Do you really want to reshuffle the chores?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        assignChores();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Empty
                    }
                });
        builder.show();
    }

    /**
     * Assigns chores using random numbers so that each member gets a relatively even number of
     * chores each. If there are leftovers, remaining chores are distributed to random members
     */
    public void assignChores() {
        // Randomly assigns chores to members
        Random rand = new Random();
        int r;
        int maxChores = choresToAllocate.size() / members.size() + 1;
        String cur;

        // remove all previous chores before allocating chores
        for (int i = 0; i < members.size(); i++) {
            members.get(i).resetChores();
        }

        //Fair chore alloc
        while(choresToAllocate.size() > 0) {
            if(choresToAllocate.size() >= members.size()) {
                for(int i = 0; i < members.size(); i++) {
                    r = rand.nextInt(choresToAllocate.size());
                    members.get(i).addChore(choresToAllocate.get(r));
                    choresToAllocate.remove(r);
                }
            } else {
                for(int i = 0; i < choresToAllocate.size(); i++) {
                    r = rand.nextInt(members.size());
                    if(members.get(r).getChores().size() < maxChores) {
                        members.get(r).addChore(choresToAllocate.get(i));
                        choresToAllocate.remove(i);
                    }
                }
            }
        }

        // if member has no chores, they get Nothing (should only used when no. chores < memsize)
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getChores().isEmpty()) {
                members.get(i).addChore("Nothing");
            }
            cur = members.get(i).getID();
            mRef.child("users").child(cur).setValue(members.get(i));
        }
        mRef.child("groups").child(houseID).child("members").setValue(members);

        Intent intent = new Intent(this, ChoreListActivity.class);
        startActivity(intent);
    }

    /**
     * Changes to EditChoreListActivity
     * @param view
     */
    public void editChores(View view) {
        Intent intent = new Intent(this, EditChoreListActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("edit");
        startActivity(intent);
    }

    /**
     * Changes to EditHouseMemberActivity
     * @param view
     */
    public void editMembers(View view) {
        Intent intent = new Intent(this, EditHouseMemberActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("edit");
        startActivity(intent);
    }

    /**
     * Changes to EditDeadlineActivity
     * @param view
     */
    public void editDeadline(View view){
        Intent intent = new Intent(this, EditDeadlineActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("edit");
        startActivity(intent);
    }


    /**
     * Sends you back to ChoreListActivity
     */
    public void backNav() {
        Intent intent = new Intent(this, ChoreListActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("settings");
        startActivity(intent);
    }

    /**
     * Sends you back to MainActivity while also removing current houseID from shared preferences
     * @param view
     */
    public void logout(View view) {
        writeString(this);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Removes the current houseID from shared preferences to stop auto login when app starts
     * @param context
     */
    public static void writeString(Context context) {
        SharedPreferences exitHouseID = context.getSharedPreferences(PREF_HOUSE_ID, 0);
        SharedPreferences.Editor editor = exitHouseID.edit();
        editor.putString("houseID", "logged out");
        editor.commit();
    }

    /**
     * Copies the current houseID and stores inside the clipboard for easy pasting. Good for putting into messenger
     * @param view
     */
    public void copyHID(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("HID", houseID);
        clipboard.setPrimaryClip(clip);

        Context context = getApplicationContext();
        CharSequence text = houseID + " copied";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Reads the houseID stored in a shared preference file.
     * @param context
     * @return houseID inside shared preference
     */
    public static String readString(Context context) {
        SharedPreferences exitHouseID = context.getSharedPreferences(PREF_HOUSE_ID, 0);
        String houseID = exitHouseID.getString("houseID", "exit");
        return houseID;
    }
}