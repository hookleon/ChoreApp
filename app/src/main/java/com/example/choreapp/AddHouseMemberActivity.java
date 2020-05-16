package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

public class AddHouseMemberActivity extends AppCompatActivity{

    // Will display names of house members
    private ArrayList<String> members = new ArrayList<>();;

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    //house id, this allows every member to store this id that is unique to their household
    private String houseID = UUID.randomUUID().toString();
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";  //Passes houseid to next activity so chores can be added to activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_house_member);

        Intent intent = getIntent();
    }

    // Adds name from the textbox
    public void addMember (View view) {
        // When clicked, the text will be taken and added as a name of a person in household
        // the newMember will take name from editMember and send it to Firebase
        EditText editMember = (EditText) findViewById(R.id.editMember);
        String newMember = editMember.getText().toString();

        //each member gets a unique id
        String memberID = UUID.randomUUID().toString();
        members.add(memberID);

        RecyclerView recView = (RecyclerView) findViewById(R.id.recView);

        LinearLayoutManager recLayout = new LinearLayoutManager(this);
        recView.setLayoutManager(recLayout);
        //String[] shit = {"stuff","more stuff","even more stuff"};
        //MyAdapter adapter = new MyAdapter(shit);
        //recView.setAdapter(adapter);

        mRef.child("users").child(memberID).child("name").setValue(newMember);
        mRef.child("users").child(memberID).child("group").setValue(houseID);
    }

    public void confirmMembers (View view) {
        //Adds all members under the name of new household
        EditText editHouse = (EditText) findViewById(R.id.editHouse);
        String house = editHouse.getText().toString();

        mRef.child("groups").child(houseID).child("members").setValue(members);
        mRef.child("groups").child(houseID).child("name").setValue(house);
        // Moves to the next page where you pick chores
        Intent intent = new Intent(this, AddChoresActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        startActivity(intent);
    }
}
