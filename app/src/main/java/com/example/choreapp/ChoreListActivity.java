/*
  ChoreListActivity.java
  ----------------------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * ChoreListActivity is the main screen logged in users will see
 * It displays the names of members in a household, the chores they have been allocated, a timer showing how long left to do chores and various buttons for settings
 */
public class ChoreListActivity extends AppCompatActivity {

    //links the app to the database stored on firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    public String houseID;
    public static final String MEMB_ID = "com.example.choreapp.MEMB_ID";
    public static final String MEMB_POS = "com.example.choreapp.MEMB_POS";
    public static final String PREF_HOUSE_ID = "PrefHouseID";
    private List<Member> members = new ArrayList<>();
    private List<String> membNames = new ArrayList<>();
    private List<String> choresToAllocate = new ArrayList<>();
    private ListAdapter adapter;
    private Handler handler = new Handler();
    private Runnable runnable;
    private String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private String deadline;
    private String dsDay;
    private String dsTime;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chorelist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra(HOUSE_ID, houseID);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Runs when ChoreListActivity first opens
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chore_list);

        // Sets the houseID so data can be accessed
        houseID = readString(this);
        for(int i = 0; i < members.size(); i++) {
            membNames.add(members.get(i).getName());
        }

        // Opens menu when click on a member
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

        // Sets up the list of members and their chores
        RecyclerView recView = (RecyclerView) findViewById(R.id.recView3);
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
                        profIntent.putExtra(MEMB_ID, id);
                        swapIntent.putExtra(MEMB_ID, id);
                        swapIntent.putExtra(HOUSE_ID, houseID);
                        swapIntent.putExtra(MEMB_POS, String.valueOf(position));

                        builder.show();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                    }
                })
        );

        mRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Gathers the house ID, the house members, the chores and the deadline from the database.
     * The house members and their chores are then displayed in a recyclerview.
     * @param dataSnapshot a snapshot of the database allowing use of information there.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showData(DataSnapshot dataSnapshot) {
        //clears both members and chores to allocate so they can be updated from the database
        members.clear();
        choresToAllocate.clear();
        TextView dueText = findViewById(R.id.deadLineText);

        /*String dsDeadline = (String)dataSnapshot.child("groups").child(houseID).child("deadline").getValue();
        deadline = dsDeadline;
        System.out.println(deadline);
        String addedTime = "";
        if(Integer.parseInt(deadline.substring(9,10)) >= 12){
            addedTime = "pm";
        }
        else{
            addedTime = "am";
        }
        dueText.setText("Chore Deadline: " + dsDeadline.substring(0, dsDeadline.length()-2) + addedTime);*/

        dsDay = (String)dataSnapshot.child("groups").child(houseID).child("dlDay").getValue();
        dsTime = (String)dataSnapshot.child("groups").child(houseID).child("dlTime").getValue();

        deadline = (String)dataSnapshot.child("groups").child(houseID).child("deadline").getValue();
        deadLineCountdown();

        String houseName = dataSnapshot.child("groups").child(houseID).child("name").getValue(String.class);
        getSupportActionBar().setTitle(houseName);

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
        assignChores();
        adapter.notifyDataSetChanged();
    }

    /**
     * The random allocation of chores to household members
     */
    public void assignChores() {
        // Randomly assigns chores to members
        Random rand = new Random();
        int r;
        int maxChores = choresToAllocate.size() / members.size() + 1;

        mRef.child("groups").child(houseID).child("firstTime").setValue(0);
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
                for (int i = 0; i < choresToAllocate.size(); i++) {
                    r = rand.nextInt(members.size());
                    if (members.get(r).getChores().size() < maxChores) {
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

    /**
     * Reads the houseID from the shared preferences
     * @param context
     * @return houseID is the houseID found in shared preferences
     */
    public static String readString(Context context) {
        SharedPreferences exitHouseID = context.getSharedPreferences(PREF_HOUSE_ID, 0);
        String houseID = exitHouseID.getString("houseID", "exit");
        return houseID;
    }


    /**
     *
     */
    private void deadLineCountdown() {
        final TextView days = findViewById(R.id.days_text);
        final TextView hours = findViewById(R.id.hours_text);
        final TextView minutes = findViewById(R.id.minutes_text);
        final TextView seconds = findViewById(R.id.seconds_text);

        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date choreDueDate = dateFormat.parse(deadline);
                    Date current_date = new Date();
                    if (!current_date.after(choreDueDate)) {
                        long diff = choreDueDate.getTime() - current_date.getTime();
                        long Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        //
                        days.setText(String.format("%02d", Days));
                        hours.setText(String.format("%02d", Hours));
                        minutes.setText(String.format("%02d", Minutes));
                        seconds.setText(String.format("%02d", Seconds));

                        System.out.println(days);
                        System.out.println(minutes);
                        System.out.println(seconds);

                    } else {
                        /*DayOfWeek dayOfWeek;
                        if(dsDay.equals("Monday")) {
                            dayOfWeek = DayOfWeek.MONDAY;
                        } else if(dsDay.equals("Tuesday")) {
                            dayOfWeek = DayOfWeek.TUESDAY;
                        } else if(dsDay.equals("Wednesday")) {
                            dayOfWeek = DayOfWeek.WEDNESDAY;
                        } else if(dsDay.equals("Thursday")) {
                            dayOfWeek = DayOfWeek.THURSDAY;
                        } else if(dsDay.equals("Friday")) {
                            dayOfWeek = DayOfWeek.FRIDAY;
                        } else if(dsDay.equals("Saturday")) {
                            dayOfWeek = DayOfWeek.SATURDAY;
                        } else {
                            dayOfWeek = DayOfWeek.SUNDAY;
                        }
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        LocalDate ld = LocalDate.now();
                        ld = ld.with(TemporalAdjusters.next(dayOfWeek));
                        String finishedDate = ld.format(dtf) + " " + dsTime;
                        mRef.child("groups").child(houseID).child("deadline").setValue(finishedDate);
                        mRef.child("groups").child(houseID).child("firstTime").setValue(1);
                        assignChores();*/
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }


}
