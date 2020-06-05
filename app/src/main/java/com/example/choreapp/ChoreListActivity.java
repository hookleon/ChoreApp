package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

        //House ID can come from multiple different activities, new group or preexisting group
        Intent intent = getIntent();
        if(intent.getAction() == "create") {
            houseID = intent.getStringExtra(AddChoresActivity.HOUSE_ID);
        } else if(intent.getAction() == "login") {
            houseID = intent.getStringExtra(LoginActivity.HOUSE_ID);
        } else if(intent.getAction() == "settings") {
            houseID = intent.getStringExtra(SettingsActivity.HOUSE_ID);
        }

        TextView textHID = findViewById(R.id.textHID);
        textHID.setText("HID: " + houseID);

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
        //clears both members and chores to allocate so they can be updated from the database
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
            for(int k = 0; k < nMemChores; k++){
                dsMemChores = dsMems.child(String.valueOf(i)).child("chores");
                members.get(i).addChore(dsMemChores.child(String.valueOf(k)).getValue(String.class));
            }
        }

        String chores = "";
        //Gets chores to allocate from database
        for(int i = 0; i < nChores; i++){
            chores = dsChores.child(String.valueOf(i)).getValue(String.class);
            choresToAllocate.add(chores);
        }
        adapter.notifyDataSetChanged();
    }

    public void assignChores(View view) {
        // Randomly assigns chores to members
        Random rand = new Random();
        int r;

        // remove all previous chores before allocating chores
        //mRef.child("groups").child(houseID).child("members").removeValue();
        for (int i = 0; i < members.size(); i++) {
            members.get(i).resetChores();
        }

        // allocate chores
        for (int i = 0; i < choresToAllocate.size(); i++) {
            r = rand.nextInt(members.size());
            members.get(r).addChore(choresToAllocate.get(i));
        }
        // if member has no chores, they get Nothing
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getChores().isEmpty()) {
                members.get(i).addChore("Nothing");
            }
        }
        mRef.child("groups").child(houseID).child("members").setValue(members);

    }
    // Need code to add each member of household as a viewable text with their chore for the week next to them

    public void settings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(HOUSE_ID, houseID);
        startActivity(intent);
    }

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
}

