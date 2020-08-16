/*
  SwapChoresActivity.java
  -----------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * SwapChoresActivity runs when you click swap chores in the popup menu in ChoreListActivity
 */
public class SwapChoresActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    public static final String PREF_HOUSE_ID = "PrefHouseID";

    private String membID;
    private String houseID;
    private int membPos;

    private TextView txtMembInChores;
    private TextView txtMembOut;
    private TextView txtMembOutChores;
    private Spinner spinMembIn;
    private Spinner spinMembTo;
    private Spinner spinMembOut;

    private Member membIn;
    private Member membOut;
    private List<String> choreIn;
    private List<Member> members;

    /**
     * Runs when SwapChoresActivity first opens
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_chores);

        Intent intent = getIntent();
        txtMembInChores = findViewById(R.id.membInChore);
        txtMembOut = findViewById(R.id.membOut);
        txtMembOutChores = findViewById(R.id.membOutChore);
        txtMembInChores.setText("Your chores");
        txtMembOut.setText("Person you give to");
        txtMembOutChores.setText("Other person's chores");

        membID = intent.getStringExtra(ChoreListActivity.MEMB_ID);
        houseID = readString(this);
        String strPos = intent.getStringExtra(ChoreListActivity.MEMB_POS);
        membPos = Integer.parseInt(strPos);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                swapChores(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Empty
            }
        });
    }

    /**
     * swapChores runs whenever the database updates (at least once when the activity first begins)
     * @param ds a snapshot of the database allowing use of information there
     */
    public void swapChores(DataSnapshot ds) {
        //Organises data to grab from database as the member initiating the chore swap
        DataSnapshot dsMemIn = ds.child("users").child(membID);
        String name = dsMemIn.child("name").getValue(String.class);
        String id = membID;
        String hid = houseID;
        membIn = new Member(name, id, hid);
        choreIn = new ArrayList<>();
        String chore;
        long nMemChores = dsMemIn.child("chores").getChildrenCount();
        for(int i = 0; i < nMemChores; i++){
            chore = dsMemIn.child("chores").child(String.valueOf(i)).getValue(String.class);
            membIn.addChore(chore);
        }
        choreIn = membIn.getChores();

        ArrayAdapter<CharSequence> spinAdapIn = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, choreIn);
        spinAdapIn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMembIn = (Spinner) findViewById(R.id.spinner2);
        spinMembIn.setAdapter(spinAdapIn);

        DataSnapshot dsMembers = ds.child("groups").child(houseID).child("members");
        members = new ArrayList<>();
        List<String> membNames = new ArrayList<>();
        Member member;

        long nMembers = dsMembers.getChildrenCount();
        for(int i = 0; i < nMembers; i++) {
            name = dsMembers.child(String.valueOf(i)).child("name").getValue(String.class);
            id = dsMembers.child(String.valueOf(i)).child("id").getValue(String.class);
            hid = houseID;
            member = new Member(name, id, hid);
            membNames.add(name);
            nMemChores = dsMembers.child(String.valueOf(i)).child("chores").getChildrenCount();
            for(int j = 0; j < nMemChores; j++) {
                chore = dsMembers.child(String.valueOf(i)).child("chores").child(String.valueOf(j)).getValue(String.class);
                member.addChore(chore);
            }
            members.add(member);
        }

        final ArrayAdapter<CharSequence> spinAdapTo = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, membNames);
        spinAdapTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinMembTo = (Spinner) findViewById(R.id.spinner3);
        spinMembOut = (Spinner) findViewById(R.id.spinner4);
        spinMembTo.setAdapter(spinAdapTo);
        spinMembTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                membOut = members.get(position);
                List<String> ch = membOut.getChores();
                ArrayAdapter<CharSequence> spinAdapOut = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, ch);
                spinAdapOut.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinMembOut.setAdapter(spinAdapOut);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //Empty
            }
        });
    }

    /**
     * Stores the new changes in the database so the swap is finalised
     * @param view
     */
    public void confirmChanges(View view) {
        /*
            members is an array of all members in household
            membIn is the member initiating the swap of chores
            membOut is the member swapping chore with membIn
            membInPos is the position of membIn in the members array
            membOutPos is the position of membOut in the members array
        */

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Swap chores")
                .setPositiveButton("Confirm Changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertConfirm();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Empty
                    }
                });

        if(membOut.getID().equals(membIn.getID())) {
            Context context = getApplicationContext();
            CharSequence text = "Don't swap chores with yourself...";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            List<String> choreOut = membOut.getChores();
            String swapOut = spinMembIn.getSelectedItem().toString();
            int swapOutPos = spinMembIn.getSelectedItemPosition();

            String swapIn = spinMembOut.getSelectedItem().toString();
            int swapInPos = spinMembOut.getSelectedItemPosition();

            choreIn.remove(swapOutPos);   //removes undesirable chore
            choreIn.add(swapIn);    //adds new chore
            choreOut.remove(swapInPos);
            choreOut.add(swapOut);

            membIn.setChores(choreIn);
            membOut.setChores(choreOut);
            builder.setMessage(membIn.getName() + " will swap " + swapOut + " with " + membOut.getName() + " for " + swapIn);
            builder.show();
        }
    }

    /**
     * When finally confirmed, the new chorelists will be pushed to each user's section in the database and return you to ChoreListActivity
     */
    void alertConfirm() {
        int membOutPos = spinMembTo.getSelectedItemPosition();
        mRef.child("users").child(membIn.getID()).setValue(membIn);
        mRef.child("users").child(membOut.getID()).setValue(membOut);
        mRef.child("groups").child(houseID).child("members").child(String.valueOf(membOutPos)).setValue(membOut);
        mRef.child("groups").child(houseID).child("members").child(String.valueOf(membPos)).setValue(membIn);

        Intent intent = new Intent(this, ChoreListActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        intent.setAction("swap");
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
}