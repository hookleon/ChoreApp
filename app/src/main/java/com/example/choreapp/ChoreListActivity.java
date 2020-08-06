package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
    public static final String MEMB_ID = "com.example.choreapp.MEMB_ID";


    private List<Member> members = new ArrayList<>();
    private List<String> membNames = new ArrayList<>();
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

        final TextView textHID = findViewById(R.id.textHID);
        textHID.setText("HID: " + houseID);

        for(int i = 0; i < members.size(); i++) {
            membNames.add(members.get(i).getName());
        }

        //Item click

        final Intent swapIntent = new Intent(this, SwapChoresActivity.class);
        final Intent profIntent = new Intent(this, MemberProfileActivity.class);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Swap chores");
        builder.setItems(R.array.list_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    //profile
                    builder.setTitle("Profile");
                    startActivity(profIntent);
                } else if(which == 1) {
                    //swap chores
                    builder.setTitle("Chores");
                    startActivity(swapIntent);
                }
            }
        });

        // RecView stuff
        recView = (RecyclerView) findViewById(R.id.recView3);
        LinearLayoutManager recLayout = new LinearLayoutManager(this);
        recView.setLayoutManager(recLayout);
        recView.setItemAnimator(new DefaultItemAnimator());
        adapter = new ListAdapter(members);
        recView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recView.setAdapter(adapter);
        recView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Member get = members.get(position); //Want to get this member into the builder so that it can send it through the intent to either profile or chore swap activity
                        String id = get.getID();
                        //builder.setMessage(position);
                        profIntent.putExtra(MEMB_ID, id);
                        swapIntent.putExtra(MEMB_ID, id);
                        builder.show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        //Nothing happens
                    }
                })
        );

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

        TextView textHouseName = (TextView) findViewById(R.id.textHouseName);
        String houseName = dataSnapshot.child("groups").child(houseID).child("name").getValue(String.class);
        textHouseName.setText(houseName);

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
        adapter.notifyDataSetChanged();
    }

    public void assignChores(View view) {
        // Randomly assigns chores to members
        Random rand = new Random();
        int r;
        int maxChores = choresToAllocate.size() / members.size() + 1;

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

        String cur;
        // if member has no chores, they get Nothing (only used when chores < memsize)
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getChores().isEmpty()) {
                members.get(i).addChore("Nothing");
            }
            cur = members.get(i).getID();
            mRef.child("users").child(cur).setValue(members.get(i));
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

