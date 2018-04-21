package cyruslee487.donteatalone.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.SharedPrefManager;

public class RestaurantInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    //Constants
    private static final String TAG = "DB";
    private static final String IMAGE_URL = "image_url";
    private static final String IMAGE_NAME = "image_name";
    private static final String IMAGE_ADDRESS = "image_address";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";
    private static final int DEFAUT_ZOOM = 15;

    //vars
    private String mImageUrl;
    private String mImageName;
    private String mImageAddress;
    private double mLat, mLng;
    private ImageView mImageView;
    private TextView mTextViewRestName, mTextViewRestAddress;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private Context mContext;
    private String mDate = "";
    private String mTime = "";
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(cyruslee487.donteatalone.R.layout.restaurant_info_activity);

        mContext = getApplicationContext();

        Intent intent = getIntent();
        if (intent.hasExtra(IMAGE_URL) && intent.hasExtra(IMAGE_NAME)
                && intent.hasExtra(IMAGE_ADDRESS) && intent.hasExtra(LATITUDE)
                && intent.hasExtra(LONGITUDE)) {
            mImageUrl = intent.getStringExtra(IMAGE_URL);
            mImageName = intent.getStringExtra(IMAGE_NAME);
            mImageAddress = intent.getStringExtra(IMAGE_ADDRESS);
            mLat = intent.getDoubleExtra(LATITUDE, 0.0);
            mLng = intent.getDoubleExtra(LONGITUDE, 0.0);
        }

        setImage(mImageUrl, mImageName, mImageAddress);

        initMap();
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_rest_info);

        mapFragment.getMapAsync(RestaurantInfoActivity.this);
        //Log.d(TAG, "initMap: called");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Log.d(TAG, "onMapReady: Map should be ready");
        mMap = googleMap;

        LatLng location = new LatLng(mLat, mLng);
        moveCamera(location, DEFAUT_ZOOM);
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void moveCamera(LatLng latLng, float zoom){
        //Log.d(TAG, "moveCamera: lat: " + latLng.latitude + " lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        mMap.addMarker(new MarkerOptions().position(latLng).title(mImageAddress));
    }

    private void setImage(String imageUrl, String imageName, String address){
        //Log.d(TAG, "setImage: ");
        mImageView = findViewById(R.id.image_view_rest_info);
        mTextViewRestName = findViewById(R.id.rest_name_rest_info);
        mTextViewRestAddress = findViewById(R.id.rest_address_rest_info);

        mTextViewRestName.setText(imageName);
        mTextViewRestAddress.setText(address);

        Glide.with(this)
                .load(imageUrl)
                .asBitmap()
                .into(mImageView);
    }

    public void createEvent(View view){
        //Log.d(TAG, "createEvent: button clicked");
        showDatePickerDialog();
    }

    private void showDatePickerDialog(){
        //Log.d(TAG, "showDatePickerDialog: ");
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
                    //Log.d(TAG, "onDateSet: Date selected: " + mDate);
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
        //Log.d(TAG, "showTimePickerDialog: ");
        Calendar cal = Calendar.getInstance();
        final int hour = cal.get(Calendar.HOUR_OF_DAY);
        final int minute = cal.get(Calendar.MINUTE);
        //Log.d(TAG, "showTimePickerDialog: Current time: " + hour + ":" + minute);

        mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int mHour, int mMinute) {
                mTime = mHour + ":" + mMinute;
                //Log.d(TAG, "onTimeSet: " + mTime);

                if(!mDate.isEmpty() && !mTime.isEmpty()){
                    setEvent(mDate, mTime);
                }
            }
        };

        final TimePickerDialog dialog = new TimePickerDialog(
                RestaurantInfoActivity.this,
                mTimeSetListener,
                hour, minute, true);

        dialog.show();
    }

    private void setEvent(String date, String time){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String username = SharedPrefManager.getInstance(this).getUsername();
        String email = SharedPrefManager.getInstance(this).getOwnerEmail();
        String token = SharedPrefManager.getInstance(this).getDeviceToken();
        String key = reference.push().getKey();
        Event event = new Event(
                key, username, mImageName, mImageAddress, date, time, token, email);

        reference.child("events").child(key).setValue(event);
        Log.d(TAG, "setEvent: Restaurant Info: done");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void toMapActivity(View view) {
        Toast.makeText(RestaurantInfoActivity.this,
                "YOu got pranked bro",
                Toast.LENGTH_SHORT).show();
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(RestaurantInfoActivity.this);

        try{
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        //Log.d(TAG, "onComplete: Found current location");
                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude()
                                , currentLocation.getLongitude())
                                , DEFAUT_ZOOM);
                    }else{
                        //Log.d(TAG, "onComplete: current location is null");

                    }
                }
            });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

}
