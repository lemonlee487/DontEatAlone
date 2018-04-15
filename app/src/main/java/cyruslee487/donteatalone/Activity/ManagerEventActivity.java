package cyruslee487.donteatalone.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import cyruslee487.donteatalone.R;

public class ManagerEventActivity extends AppCompatActivity {

    private static final String TAG = "DB";

    private String mStartDate = "";
    private String mStartTime = "";
    private String mEndDate = "";
    private String mEndTime = "";
    private String mAddress = "";
    int mConcat_start_date = 0;
    int mConcat_end_date = 0;
    int mConcat_start_time = 0;
    int mConcat_end_time = 0;

    private TextView mStartDateTV, mEndDateTV;
    private EditText mNumPeopleET, mDescriptionET;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_event);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        mContext = getApplicationContext();
        mStartDateTV = findViewById(R.id.start_date_time_tv_manager_event);
        mEndDateTV = findViewById(R.id.end_date_time_tv_manager_event);
        mNumPeopleET = findViewById(R.id.people_et_manager_event);
        mDescriptionET = findViewById(R.id.description_et_manager_event);

        mAddress = getIntent().getStringExtra("Address");
        Log.d(TAG, "onCreate: ManagerEventActivity: " + mAddress);
    }

    public void pickStart(View view){
        showStartDatePickerDialog();
    }

    public void pickEnd(View view){
        if(mConcat_start_date == 0)
            Toast.makeText(mContext, "Pick the start date first", Toast.LENGTH_SHORT).show();
        else
            showEndDatePickerDialog();
    }

    private void showStartDatePickerDialog(){
        Log.d(TAG, "showDatePickerDialog: ");
        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int concat_date = Integer.parseInt("" + year + month + day);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                mConcat_start_date = Integer.parseInt("" + mYear + mMonth + mDay);
                if(mConcat_start_date < concat_date){
                    Toast.makeText(mContext, "Invalid Date", Toast.LENGTH_SHORT).show();
                    showStartDatePickerDialog();
                } else {
                    mMonth += 1;
                    mStartDate = mYear + "/" + mMonth + "/" + mDay;
                    Log.d(TAG, "onDateSet: Date selected: " + mStartDate);
                    showStartTimePickerDialog();
                }
            }
        };

        final DatePickerDialog dialog = new DatePickerDialog(
                ManagerEventActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showStartTimePickerDialog(){
        Log.d(TAG, "showTimePickerDialog: ");
        Calendar cal = Calendar.getInstance();
        final int hour = cal.get(Calendar.HOUR);
        final int minute = cal.get(Calendar.MINUTE);
        Log.d(TAG, "showTimePickerDialog: Current time: " + hour + ":" + minute);

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int mHour, int mMinute) {
                mConcat_start_time = Integer.parseInt("" + mHour + mMinute);
                mStartTime = mHour + ":" + mMinute;
                Log.d(TAG, "onTimeSet: " + mStartTime);

                if(!mStartDate.isEmpty() && !mStartTime.isEmpty()){
                    Log.d(TAG, "onTimeSet: Date: " + mStartDate + " Time: " + mStartTime);
                    mStartDateTV.setText(mStartDate + " " + mStartTime);
                }
            }
        };

        final TimePickerDialog dialog = new TimePickerDialog(
                ManagerEventActivity.this,
                mTimeSetListener,
                hour, minute, true);

        dialog.show();
    }

    private void showEndDatePickerDialog(){
        Log.d(TAG, "showEndDatePickerDialog: ");
        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int concat_date = Integer.parseInt("" + year + month + day);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                Log.d(TAG, "onDateSet: HI");
                mConcat_end_date = Integer.parseInt("" + mYear + mMonth + mDay);
                Log.d(TAG, "onDateSet: " + mConcat_end_date + " " + concat_date);
                if(mConcat_end_date < concat_date){
                    Log.d(TAG, "onDateSet: error 1");
                    Toast.makeText(mContext, "Cannot pick date that is before today", Toast.LENGTH_SHORT).show();
                    showEndDatePickerDialog();
                } else{
                    if(mConcat_end_date < mConcat_start_date){
                        Log.d(TAG, "onDateSet: error 2");
                        Toast.makeText(mContext, "End Date must be equal or after the Start Date", Toast.LENGTH_SHORT).show();
                        showEndDatePickerDialog();
                    }else {
                        mMonth += 1;
                        mEndDate = mYear + "/" + mMonth + "/" + mDay;
                        Log.d(TAG, "onDateSet: Date selected: " + mEndDate);
                        showEndTimePickerDialog();
                    }
                }
            }
        };

        final DatePickerDialog dialog = new DatePickerDialog(
                ManagerEventActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showEndTimePickerDialog(){
        Log.d(TAG, "showEndTimePickerDialog: ");
        Calendar cal = Calendar.getInstance();
        final int hour = cal.get(Calendar.HOUR);
        final int minute = cal.get(Calendar.MINUTE);
        Log.d(TAG, "showTimePickerDialog: Current time: " + hour + ":" + minute);

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int mHour, int mMinute) {
                mConcat_end_time = Integer.parseInt("" + mHour + mMinute);
                mEndTime = mHour + ":" + mMinute;
                Log.d(TAG, "onTimeSet: " + mEndTime + " " +  mConcat_start_time + " " + mConcat_end_time);

                if(mConcat_start_date == mConcat_end_date && mConcat_end_time < mConcat_start_time){
                    Toast.makeText(mContext, "End time cannot be smaller than the Start time on the same day", Toast.LENGTH_SHORT).show();
                    showEndTimePickerDialog();
                }else{
                    if(!mEndDate.isEmpty() && !mEndTime.isEmpty()){
                        Log.d(TAG, "onTimeSet: Date: " + mEndDate + " Time: " + mEndTime);
                        mEndDateTV.setText(mEndDate + " " + mEndTime);
                    }
                }
            }
        };

        final TimePickerDialog dialog = new TimePickerDialog(
                ManagerEventActivity.this,
                mTimeSetListener,
                hour, minute, true);

        dialog.show();
    }

    public void postEvent(View view){
        Intent intent = new Intent(ManagerEventActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void toManagerMapActivity(View view){
        Intent intent = new Intent(ManagerEventActivity.this, ManagerMapsActivity.class);
        startActivity(intent);
    }
}
