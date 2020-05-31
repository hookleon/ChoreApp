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

        Intent intent = getIntent();
        houseID = intent.getStringExtra(AddChoresActivity.HOUSE_ID);

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
        DataSnapshot ds = dataSnapshot.child("groups").child(houseID).child("members");
        long n = ds.getChildrenCount();
        String name = "";
        String id = "";
        String hid = "";
        for (int i = 0; i < n; i++){
            name = ds.child(String.valueOf(i)).child("name").getValue().toString();
            id = ds.child(String.valueOf(i)).child("id").getValue().toString();
            hid = ds.child(String.valueOf(i)).child("houseID").getValue().toString();
            members.add(new Member(name,id,hid));
        }
    }

    public void assignChores(View view) {
        // test code
        choresToAllocate.add("Dishes");
        choresToAllocate.add("Rubbish");
        choresToAllocate.add("Vacuum");
        choresToAllocate.add("Toilet");

        //members.add(new Member("Anna", "", ""));
        //members.add(new Member("Bobby", "", ""));
        //members.add(new Member("Cameron", "", ""));

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
