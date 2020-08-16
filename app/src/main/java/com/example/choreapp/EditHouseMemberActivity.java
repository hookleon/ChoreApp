/*
  EditHouseMemberActivity.java
  ----------------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * EditHouseMemberActivity runs from SettingsActivity as a way to edit house members
 */
public class EditHouseMemberActivity extends AppCompatActivity{

    // Will display names of house members
    private List<Member> members = new ArrayList<>();

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    //house id, this allows every member to store this id that is unique to their household
    private String houseID;
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";  //Passes houseid to next activity so chores can be added to activity

    private MyAdapter adapter;
    //private TextView textView;
    private EditText editHouse;

    /**
     * Runs when activity first opens
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house_member);

        Intent intent = getIntent();
        houseID = intent.getStringExtra(SettingsActivity.HOUSE_ID);
        editHouse = findViewById(R.id.editHouse);
        // RecView stuff
        RecyclerView recView = (RecyclerView) findViewById(R.id.recView);
        LinearLayoutManager recLayout = new LinearLayoutManager(this);
        recView.setLayoutManager(recLayout);
        recView.setItemAnimator(new DefaultItemAnimator());
        adapter = new MyAdapter(members);
        recView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recView.setAdapter(adapter);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Empty
            }
        });
    }

    /**
     * Used to clear both members and chores to allocate so they can be updated from the database
     * @param dataSnapshot
     */
    private void showData(DataSnapshot dataSnapshot) {
        //clears both members and chores to allocate so they can be updated from the database
        members.clear();
        String houseName = dataSnapshot.child("groups").child(houseID).child("name").getValue(String.class);
        editHouse.setText(houseName);
        DataSnapshot dsMems = dataSnapshot.child("groups").child(houseID).child("members");
        DataSnapshot dsMemChores;
        long nMems = dsMems.getChildrenCount();
        long nMemChores;

        String name = "";
        String id = "";
        String hid = "";

        //Gets member names and info from database
        for (int i = 0; i < nMems; i++) {
            name = dsMems.child(String.valueOf(i)).child("name").getValue(String.class);
            id = dsMems.child(String.valueOf(i)).child("id").getValue(String.class);
            hid = dsMems.child(String.valueOf(i)).child("houseID").getValue(String.class);
            members.add(new Member(name, id, hid));

            nMemChores = dsMems.child(String.valueOf(i)).child("chores").getChildrenCount();
            for (int k = 0; k < nMemChores; k++) {
                dsMemChores = dsMems.child(String.valueOf(i)).child("chores");
                members.get(i).addChore(dsMemChores.child(String.valueOf(k)).getValue(String.class));
            }
        }
        adapter.notifyDataSetChanged();
        //textView.setText(houseID);
    }

    /**
     * Adds member of household with name taken from the textbox
     * @param view
     */
    public void addMember (View view) {
        // When clicked, the text will be taken and added as a name of a person in household
        // the newMember will take name from editMember and send it to Firebase
        EditText editMember = (EditText) findViewById(R.id.editMember);
        String name = editMember.getText().toString();
        String id = UUID.randomUUID().toString();

        members.add(new Member(name,id,houseID));
        members.get(members.size()-1).addChore("Nothing");
        adapter.notifyDataSetChanged();   //This updates the recyclerView
    }

    /**
     * Commits changes of house members into the database
     * @param view
     */
    public void confirmMembers (View view) {
        //Adds all members under the name of new household
        String house = editHouse.getText().toString();
        if (members.size() != 0) {
            mRef.child("groups").child(houseID).child("members").setValue(members);
            mRef.child("groups").child(houseID).child("name").setValue(house);

            for (int i = 0; i < members.size(); i++) {
                mRef.child("users").child(members.get(i).getID()).setValue(members.get(i));
            }

            // Moves to the next page where you pick chores
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra(HOUSE_ID, houseID);
            startActivity(intent);
        }
        else{
            Context context = getApplicationContext();
            CharSequence text = "No members please try again";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }
}