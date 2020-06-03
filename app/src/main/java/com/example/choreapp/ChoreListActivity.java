package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChoreListActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    public String houseID;

    private List<Member> members = new ArrayList<>();
    private List<String> choresToAllocate = new ArrayList<>();

    private RecyclerView recView;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_list);

        //House ID can come from two different activities, new group or preexisting group
        Intent intent = getIntent();
        if(intent.getAction() == "create") {
            houseID = intent.getStringExtra(AddChoresActivity.HOUSE_ID);
        } else if(intent.getAction() == "login") {
            houseID = intent.getStringExtra(LoginActivity.HOUSE_ID);
        }

        // RecView stuff
        recView = (RecyclerView) findViewById(R.id.recView3);
        LinearLayoutManager recLayout = new LinearLayoutManager(this);
        recView.setLayoutManager(recLayout);
        recView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ListAdapter(members);
        recView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recView.setAdapter(adapter);

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

    private void showData(DataSnapshot dataSnapshot) {
        DataSnapshot dsMems = dataSnapshot.child("groups").child(houseID).child("members");
        DataSnapshot dsChores = dataSnapshot.child("groups").child(houseID).child("chores");

        long nMems = dsMems.getChildrenCount();
        long nChores = dsChores.getChildrenCount();

        String name = "";
        String id = "";
        String hid = "";
        //Gets member names and info from database
        for (int i = 0; i < nMems; i++){
            name = dsMems.child(String.valueOf(i)).child("name").getValue(String.class);
            id = dsMems.child(String.valueOf(i)).child("id").getValue(String.class);
            hid = dsMems.child(String.valueOf(i)).child("houseID").getValue(String.class);
            members.add(new Member(name,id,hid));
        }

        String chores = "";
        //Gets chores to allocate from database
        for(int i = 0; i < nChores; i++){
            chores = dsChores.child(String.valueOf(i)).getValue(String.class);
            choresToAllocate.add(chores);
        }
    }

    public void assignChores(View view) {
        // Randomly assigns chores to members
        Random rand = new Random();
        int r;

        // allocate chores
        for (int i = 0; i < choresToAllocate.size(); i++) {
            r = rand.nextInt(members.size());
            members.get(r).addChore(choresToAllocate.get(i));
        }
        adapter.notifyDataSetChanged();
    }
    // Need code to add each member of household as a viewable text with their chore for the week next to them
}
