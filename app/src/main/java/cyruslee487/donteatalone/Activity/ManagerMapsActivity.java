package cyruslee487.donteatalone.Activity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cyruslee487.donteatalone.R;

public class ManagerMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "DB";
    private static final float DEFAULT_ZOOM = 15;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private EditText mSearchET;
    private ImageView mConfirmLocationBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mSearchET = findViewById(R.id.search_address_manager_map);
        mConfirmLocationBtn = findViewById(R.id.confirm_address_btn_manager_map);

        init();
    }

    private void init(){
        Log.d(TAG, "init: gogogo");
        mSearchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    geoLocate();
                }
                return false;
            }
        });

        hideSoftKeyboard();
    }

    private void geoLocate(){
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = mSearchET.getText().toString();
        Geocoder geocoder = new Geocoder(ManagerMapsActivity.this);

        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Log.e(TAG, "geoLocate: IOException" + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: " + address.getAddressLine(0));
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude())
                    , DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String datitle){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if(!datitle.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(datitle);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(ManagerMapsActivity.this);

        try{
            Task location = mFusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: Found current location");
                        Location currentLocation = (Location) task.getResult();
                        moveCamera(new LatLng(currentLocation.getLatitude()
                                        , currentLocation.getLongitude())
                                , DEFAULT_ZOOM, "My location");
                    }else{
                        Log.d(TAG, "onComplete: current location is null");
                    }
                }
            });
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }

    public void sendSearchResult(View view){
        String searchString = mSearchET.getText().toString();
        Geocoder geocoder = new Geocoder(ManagerMapsActivity.this);

        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch(IOException e){
            Log.e(TAG, "geoLocate: IOException" + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "geoLocate: " + address.getAddressLine(0));
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude())
                    , DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        getDeviceLocation();
    }
}
