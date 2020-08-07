package com.example.choreapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;

import android.widget.EditText;

import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddChoresActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();

    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    private String houseID;
    private List<String> choresToAllocate = new ArrayList<>();
    private AddChoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chores);

        Intent intent = getIntent();
        houseID = intent.getStringExtra(AddHouseMemberActivity.HOUSE_ID);

        // RecView stuff
        RecyclerView recView = (RecyclerView) findViewById(R.id.recView2);
        LinearLayoutManager recLayout = new LinearLayoutManager(this);
        recView.setLayoutManager(recLayout);
        recView.setItemAnimator(new DefaultItemAnimator());
        adapter = new AddChoreAdapter(choresToAllocate);
        recView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recView.setAdapter(adapter);
    }

    public void addChore (View view){
        //Need code that will add selected chore to the list of chores house will use
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        final String chore = spinner.getSelectedItem().toString();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText choreIn = new EditText(this);
        choreIn.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(choreIn);
        builder.setTitle("Add new chore");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(choreIn.getText() != null) {
                    final String newChore = choreIn.getText().toString();
                    choresToAllocate.add(newChore);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Empty
            }
        });

        // Checks that chore hasn't already been added
        Boolean add = Boolean.TRUE;
        for(int i = 0; i < choresToAllocate.size(); i++) {
            if (choresToAllocate.get(i) == chore) {
                add = Boolean.FALSE;
            }
        }
        if (add == Boolean.TRUE) {
            if(spinner.getSelectedItemPosition() == 4) {
                builder.show();
            } else {
                choresToAllocate.add(chore);
                adapter.notifyDataSetChanged();   //This updates the recyclerView
            }
        }
    }

    public void confirmChores (View view) {
        if (choresToAllocate.size() < 1) {
            Context context = getApplicationContext();
            CharSequence text = "No chores please try again";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            mRef.child("groups").child(houseID).child("chores").setValue(choresToAllocate);
            Intent intent = new Intent(this, ChoreListActivity.class);
            intent.putExtra(HOUSE_ID, houseID);
            intent.setAction("create");
            startActivity(intent);
        }

    }
}
