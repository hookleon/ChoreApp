package com.example.choreapp;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import android.widget.Spinner;
import android.widget.TimePicker;

/**
 * Class prompts the user to input a date and time for the deadline of their chores.
 * Takes the user's input and passes it onto the database.
 */
public class AddDeadlineActivity extends AppCompatActivity {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mRef = database.getReference();
    public static final String HOUSE_ID = "com.example.choreapp.HOUSE_ID";
    public static final String PREF_HOUSE_ID = "PrefHouseID";
    private String houseID;
    private Spinner dayPicker;
    private Calendar calendar;
    private int year, month, day;
    private TimePicker alarmTimePicker;
    private String selectedTime;

    /**
     * Creates the instance of the activity where users can decide on a day and time their chores
     * are due by
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deadline);

        Intent intent = getIntent();
        houseID = intent.getStringExtra(AddHouseMemberActivity.HOUSE_ID);

        dayPicker = (Spinner) findViewById(R.id.days);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
    }

    /**
     * Gets the houseID stored on the device from SharedPreferences
     * @param context context in which the method is run
     * @param hid the houseID taken from SharedPrefs
     */
    public static void writeString(Context context, String hid) {
        SharedPreferences exitHouseID = context.getSharedPreferences(PREF_HOUSE_ID, 0);
        SharedPreferences.Editor editor = exitHouseID.edit();
        editor.putString("houseID", hid);
        editor.commit();
    }

    /**
     * Initiated when the submit button is pressed.
     * Assembles the date and time in the correct format and passes it onto the database.
     * Once all is done starts a new intent with the ChoreListActivity.
     *
     * @param view is used for the event handling of the submit button.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void submitDates(View view) {
        Intent intent = new Intent(this, ChoreListActivity.class);

        String selectedHour = String.valueOf(alarmTimePicker.getCurrentHour());
        String selectedMin = String.valueOf(alarmTimePicker.getCurrentMinute());
        selectedTime = selectedHour + ":" +selectedMin;

        String day = String.valueOf(dayPicker.getSelectedItem());
        String time = selectedTime + ":0";
        DayOfWeek dayOfWeek;
        if(day.equals("Monday")) {
            dayOfWeek = DayOfWeek.MONDAY;
        } else if(day.equals("Tuesday")) {
            dayOfWeek = DayOfWeek.TUESDAY;
        } else if(day.equals("Wednesday")) {
            dayOfWeek = DayOfWeek.WEDNESDAY;
        } else if(day.equals("Thursday")) {
            dayOfWeek = DayOfWeek.THURSDAY;
        } else if(day.equals("Friday")) {
            dayOfWeek = DayOfWeek.FRIDAY;
        } else if(day.equals("Saturday")) {
            dayOfWeek = DayOfWeek.SATURDAY;
        } else {
            dayOfWeek = DayOfWeek.SUNDAY;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        LocalDate ld = LocalDate.now();
        ld = ld.with(TemporalAdjusters.next(dayOfWeek));
        String finishedDate = ld.format(dtf) + " " + time;
        mRef.child("groups").child(houseID).child("deadline").setValue(finishedDate);
        mRef.child("groups").child(houseID).child("dlDay").setValue(day);
        mRef.child("groups").child(houseID).child("dlTime").setValue(time);
        mRef.child("groups").child(houseID).child("reset").setValue("y");

        writeString(this, houseID);
        startActivity(intent);
    }
}



