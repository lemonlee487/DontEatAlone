package cyruslee487.donteatalone;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Calendar;

/**
 * Created by cyrus on 2018-03-22.
 */

public class RestaurantInfoActivity extends AppCompatActivity{

    //Constants
    private static final String TAG = "DB";
    private static final String USER_NAME = "username";
    private static final String IMAGE_URL = "image_url";
    private static final String IMAGE_NAME = "image_name";

    //vars
    private String mImageUrl;
    private String mImageName;
    private ImageView mImageView;
    private TextView mTextView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private Context mContext;
    private String mDate="";
    private String mTime="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurant_info_activity);

        mContext = getApplicationContext();

        Intent intent = getIntent();
        if(intent.hasExtra(IMAGE_URL) && intent.hasExtra(IMAGE_NAME)) {
            mImageUrl = intent.getStringExtra(IMAGE_URL);
            mImageName = intent.getStringExtra(IMAGE_NAME);
        }

        setImage(mImageUrl, mImageName);
    }

    private void setImage(String imageUrl, String imageName){
        Log.d(TAG, "setImage: ");
        mImageView = findViewById(R.id.image_info_activity);
        mTextView = findViewById(R.id.textview_info_activity);

        mTextView.setText(imageName);

        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .into(mImageView);
    }

    public void createEvent(View view){
        Log.d(TAG, "createEvent: button clicked");
        showDatePickerDialog();
    }

    private void showDatePickerDialog(){
        Log.d(TAG, "showDatePickerDialog: ");
        Calendar cal = Calendar.getInstance();
        final int year = cal.get(Calendar.YEAR);
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        final int concat_date = Integer.parseInt("" + year + month + day);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                int mConcat_date = Integer.parseInt("" + mYear + mMonth + mDay);
                if(mConcat_date < concat_date){
                    Toast.makeText(mContext, "Invalid Date", Toast.LENGTH_SHORT).show();
                    showDatePickerDialog();
                } else {
                    mMonth += 1;
                    mDate = mYear + "/" + mMonth + "/" + mDay;
                    Log.d(TAG, "onDateSet: Date selected: " + mDate);
                    showTimePickerDialog();
                }
            }
        };

        final DatePickerDialog dialog = new DatePickerDialog(
                RestaurantInfoActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                mDateSetListener,
                year,month,day);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showTimePickerDialog(){
        Log.d(TAG, "showTimePickerDialog: ");
        Calendar cal = Calendar.getInstance();
        final int hour = cal.get(Calendar.HOUR);
        final int minute = cal.get(Calendar.MINUTE);
        Log.d(TAG, "showTimePickerDialog: Current time: " + hour + ":" + minute);

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int mHour, int mMinute) {
                mTime = mHour + ":" + mMinute;
                Log.d(TAG, "onTimeSet: " + mTime);

                if(!mDate.isEmpty() && !mTime.isEmpty()){
                    backToMainActivity(mDate, mTime);
                }
            }
        };

        final TimePickerDialog dialog = new TimePickerDialog(
                RestaurantInfoActivity.this,
                mTimeSetListener,
                hour, minute, true);

        dialog.show();
    }

    private void backToMainActivity(String date, String time){
        Intent intent = new Intent(RestaurantInfoActivity.this, MainActivity.class);
        intent.putExtra("select_date", date);
        intent.putExtra("select_time", time);
        intent.putExtra(IMAGE_NAME, mImageName);
        startActivity(intent);
        finish();
    }

}
