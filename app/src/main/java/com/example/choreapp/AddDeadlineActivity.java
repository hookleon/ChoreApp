package com.example.choreapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.Menu;
import android.app.Dialog;



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


    /**
     * new variables for date picker
     */
    private DatePicker datePicker;
    private Spinner dayPicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    private TimePicker alarmTimePicker;

    private String selectedDate;
    private String selectedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_deadline);

        Intent intent = getIntent();
        houseID = intent.getStringExtra(AddHouseMemberActivity.HOUSE_ID);

        dayPicker = (Spinner) findViewById(R.id.days);
        dateView = (TextView) findViewById(R.id.textView3);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);


        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
    }

    /**
     * @param context
     * @param hid
     */
    public static void writeString(Context context, String hid) {
        SharedPreferences exitHouseID = context.getSharedPreferences(PREF_HOUSE_ID, 0);
        SharedPreferences.Editor editor = exitHouseID.edit();
        editor.putString("houseID", hid);
        editor.commit();
    }

    /**
     * Copied method from https://www.tutorialspoint.com/android/android_datepicker_control.htm
     */
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }


      @Override protected Dialog onCreateDialog(int id) {
      // TODO Auto-generated method stub
      if (id == 999) {
      return new DatePickerDialog(this,
      myDateListener, year, month, day);
      }
      return null;
      }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2 + 1, arg3);
                }
            };


    private void showDate(int year, int month, int day) {
        //dateView.setText(new StringBuilder().append(day).append("/")
        //        .append(month).append("/").append(year));

        selectedDate = (new StringBuilder().append(day).append("-").append(month).append("-").append(year)).toString();
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

        /**
         EditText dateText = findViewById(R.id.dateInput);
         EditText timeText = findViewById(R.id.timeInput);
         String dateInput = dateText.getText().toString();
         String timeInput = timeText.getText().toString();

         String finishedDate = dateInput + " " + timeInput + ":0";
*/

        //need to add newwer version...
        String selectedHour = String.valueOf(alarmTimePicker.getCurrentHour());
        String selectedMin = String.valueOf(alarmTimePicker.getCurrentMinute());
        selectedTime = selectedHour + ":" +selectedMin;
        dateView.setText(selectedTime);

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
        mRef.child("groups").child(houseID).child("reset").setValue(1);
        //String finishedDate = selectedDate + " " + selectedTime + ":0";
        //mRef.child("groups").child(houseID).child("deadline").setValue(finishedDate);
        writeString(this, houseID);
        startActivity(intent);
    }
}



