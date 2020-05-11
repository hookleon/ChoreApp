package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddHouseMemberActivity extends AppCompatActivity {

    // Will display names of house members
    ArrayList<String> members = new ArrayList<>();

    // Place to store names of house members
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mRef = database.getReference();

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
        RecyclerView recView = (RecyclerView) findViewById(R.id.recView);
        //RecyclerView.Adapter recAdapt = new

        EditText editMember = (EditText) findViewById(R.id.editMember);
        String newMember = editMember.getText().toString();
        members.add(newMember);

        //recView.
    }

    public void confirmMembers (View view) {
        //Adds all members under the name of new household
        EditText editHouse = (EditText) findViewById(R.id.editHouse);
        String house = editHouse.getText().toString();

        mRef.child("users").setValue(members);

        // Moves to the next page where you pick chores
        Intent intent = new Intent(this, AddChoresActivity.class);
        startActivity(intent);
    }
}
