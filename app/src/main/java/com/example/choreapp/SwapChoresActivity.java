package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SwapChoresActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    public String membID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_chores);

        Intent intent = getIntent();
        membID = intent.getStringExtra(ChoreListActivity.MEMB_ID);
        //Member membIn = mRef.child("users").child(membID).get

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                swapChores(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void swapChores(DataSnapshot ds) {
        //Organises data tom grab from database as the member initiating the chore swap
        DataSnapshot dsMemIn = ds.child("users").child(membID);
        String name = dsMemIn.child("name").getValue(String.class);
        String id = membID;
        String hid = dsMemIn.child("houseID").getValue(String.class);
        Member membIn = new Member(name, id, hid);
        List<String> s = new ArrayList<>();
        String chore;
        long nMemChores = dsMemIn.child("chores").getChildrenCount();
        for(int i = 0; i < nMemChores; i++){
            chore = dsMemIn.child("chores").child(String.valueOf(i)).getValue(String.class);
            membIn.addChore(chore);
        }
        s = membIn.getChores();
        TextView test = (TextView) findViewById(R.id.test);
        test.setText(s.get(0));
    }
}
