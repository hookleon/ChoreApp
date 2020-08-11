/*
  SwapChoresActivity.java
  -----------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
 *
 */
public class SwapChoresActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    private String membID;
    private String houseID;
    private int membPos;

    private Spinner spinMembIn;

    private Spinner spinMembTo;
    private Spinner spinMembOut;

    private Member membIn;
    private Member membOut;
    private List<String> choreIn;
    private List<Member> members;

    private TextView test;

    /**
     *
     *  @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_chores);

        Intent intent = getIntent();
        membID = intent.getStringExtra(ChoreListActivity.MEMB_ID);
        houseID = intent.getStringExtra(ChoreListActivity.HOUSE_ID);
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
     *
     * @param ds
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
        test = (TextView) findViewById(R.id.test);

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

    public void confirmChanges(View view) {
        /*
            confirmChores runs when the confirmChores button is pressed. It will do the swapping of chores

            members is an array of all members in household
            membIn is the member initiating the swap of chores
            membOut is the member swapping chore with membIn
            membInPos is the position of membIn in the members array
            membOutPos is the position of membOut in the members array
        */

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
            int membOutPos = spinMembTo.getSelectedItemPosition();
            String swapIn = spinMembOut.getSelectedItem().toString();
            int swapInPos = spinMembOut.getSelectedItemPosition();

            choreIn.remove(swapOutPos);   //removes undesirable chore
            choreIn.add(swapIn);    //adds new chore
            choreOut.remove(swapInPos);
            choreOut.add(swapOut);

            membIn.setChores(choreIn);
            membOut.setChores(choreOut);

            mRef.child("users").child(membIn.getID()).setValue(membIn);
            mRef.child("users").child(membOut.getID()).setValue(membOut);
            mRef.child("groups").child(houseID).child("members").child(String.valueOf(membOutPos)).setValue(membOut);
            mRef.child("groups").child(houseID).child("members").child(String.valueOf(membPos)).setValue(membIn);

            Intent intent = new Intent(this, ChoreListActivity.class);
            intent.putExtra(HOUSE_ID, houseID);
            intent.setAction("swap");
            startActivity(intent);
        }
    }
}