package cyruslee487.donteatalone;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DB";
    private static final String ANONYMOUS = "anonymous";
    private static final String SELECT_DATE = "select_date";
    private static final String SELECT_TIME = "select_time";
    private static final String IMAGE_NAME = "image_name";
    private static final int RC_SIGN_IN = 1001;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSION_REQUEST_CODE = 5001;

    //vars
    //private ArrayList<String> mImageNames = new ArrayList<>();
    //private ArrayList<String> mImagesUrls = new ArrayList<>();
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();
    private String mUsername;
    private boolean mLocationPermissionGranted;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        changeNavMenuItemName(navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        if(isServiceOk()){
            getPermission();
        }

        initBitmaps();

        mUsername = ANONYMOUS;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("events");
        mFirebaseAuth = FirebaseAuth.getInstance();

        getIntentFromRestaurantInfoActivity();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if(mUser != null){
                    onSignedIn(mUser.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged: User signed in");
                } else {
                    onSignOutCleanUp();
                    Log.d(TAG, "onAuthStateChanged: User signed out");
                    launchSignInIntent();
                }
            }
        };
    }

    public boolean isServiceOk(){
        Log.d(TAG, "isServiceOk: Checking google service version");

        int available = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServiceOk: Google play service is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServiceOk: an error occured");
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
        } else {
            Log.d(TAG, "isServiceOk: No map request");
        }
        return false;
    }

    private void getPermission(){
        Log.d(TAG, "init: Getting permission");
        if(checkLocationPermission()){
            Log.d(TAG, "init: Permission granted");
        } else {
            Log.d(TAG, "init: Requesting permission");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Please allow the permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    private boolean checkLocationPermission() {
        int permissioinState = ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissioinState == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;

        switch(requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //init map here
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, "onActivityResult: " + user.getDisplayName());
            } else {
                Log.d(TAG, "onActivityResult: result not ok");
            }
        }
    }

    private void getIntentFromRestaurantInfoActivity(){
        if(getIntent().hasExtra(IMAGE_NAME)
                && getIntent().hasExtra(SELECT_DATE)
                && getIntent().hasExtra(SELECT_TIME)){
            String name = getIntent().getStringExtra(IMAGE_NAME);
            String date = getIntent().getStringExtra(SELECT_DATE);
            String time = getIntent().getStringExtra(SELECT_TIME);

            if(mUsername == ANONYMOUS){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                mUsername = user.getDisplayName();
            }

            mDatabaseReference.push().setValue(new Event(mUsername, name, null, date, time));

            Log.d(TAG, "onCreate: Set value: " + mUsername + "___"
                    + name + "___" + date + "___" + time);
        }
    }

    private void launchSignInIntent(){
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        );
    }

    private void onSignedIn(String username) {
        mUsername = username;
    }

    private void onSignOutCleanUp(){
        mUsername = ANONYMOUS;
    }

    private void detchDatabaseListener(){
        if (mDatabaseReference != null && mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFirebaseAuth != null) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }
        //Log.d(TAG, "onResume: username ===> " + mUsername);
        if(mUsername == ANONYMOUS) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null)
                mUsername = user.getDisplayName();
            //Log.d(TAG, "onResume: username ===> " + mUsername);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        //mMessageAdapter.clear();
        detchDatabaseListener();
    }

    //Recycler View
    private void initBitmaps(){
        //Log.d(TAG, "init: perparing images");

        insertRestaurantArray(getResources().getString(R.string.McDonald)
                ,getResources().getString(R.string.McDonald_01_address)
                ,getResources().getString(R.string.McDonald_Url)
                ,50.663558, -120.350930);

        insertRestaurantArray(getResources().getString(R.string.McDonald)
                ,getResources().getString(R.string.McDonald_02_address)
                ,getResources().getString(R.string.McDonald_Url)
                ,50.665622, -120.369793);

        insertRestaurantArray(getResources().getString(R.string.McDonald)
                ,getResources().getString(R.string.McDonald_03_address)
                ,getResources().getString(R.string.McDonald_Url)
                ,50.675703, -120.334223);

        insertRestaurantArray(getResources().getString(R.string.WhiteSpot)
                ,getResources().getString(R.string.White_Spot_01_address)
                ,getResources().getString(R.string.WhiteSpot_Url)
                ,50.663599, -120.352802);

        insertRestaurantArray(getResources().getString(R.string.WhiteSpot)
                ,getResources().getString(R.string.White_Spot_02_address)
                ,getResources().getString(R.string.WhiteSpot_Url)
                ,50.696459, -120.361058);

        insertRestaurantArray(getResources().getString(R.string.RR)
                ,getResources().getString(R.string.Red_Robin_01_address)
                ,getResources().getString(R.string.RR_Url)
                ,50.666638, -120.355216);

        insertRestaurantArray(getResources().getString(R.string.Domino)
                ,getResources().getString(R.string.Domino_01_address)
                ,getResources().getString(R.string.Domino_Url)
                ,50.671228, -120.354309);

        insertRestaurantArray(getResources().getString(R.string.PizzaHut)
                ,getResources().getString(R.string.PizzaHut_01_address)
                ,getResources().getString(R.string.PizzaHut_Url)
                ,50.667309, -120.355510);

        insertRestaurantArray(getResources().getString(R.string.PizzaHut)
                ,getResources().getString(R.string.PizzaHut_02_address)
                ,getResources().getString(R.string.PizzaHut_Url)
                ,50.700848, -120.360063);

        insertRestaurantArray(getResources().getString(R.string.Wendy)
                ,getResources().getString(R.string.Wendy_01_address)
                ,getResources().getString(R.string.Wendy_Url)
                ,50.669688, -120.354922);

        insertRestaurantArray(getResources().getString(R.string.Wendy)
                ,getResources().getString(R.string.Wendy_02_address)
                ,getResources().getString(R.string.Wendy_Url)
                ,50.677869, -120.289915);

        insertRestaurantArray(getResources().getString(R.string.Wendy)
                ,getResources().getString(R.string.Wendy_03_address)
                ,getResources().getString(R.string.Wendy_Url)
                ,50.699066, -120.363341);

        insertRestaurantArray(getResources().getString(R.string.Boston)
                ,getResources().getString(R.string.Boston_01_address)
                ,getResources().getString(R.string.Boston_Url)
                ,50.663752, -120.351726);

        insertRestaurantArray(getResources().getString(R.string.KFC)
                ,getResources().getString(R.string.KFC_01_address)
                ,getResources().getString(R.string.KFC_Url)
                ,50.663173, -120.351940);

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerview_content_main);
        RecyclerViewAdapter mAdapter =
                new RecyclerViewAdapter(this, mRestaurants);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void insertRestaurantArray(String rest_name, String rest_address, String image_url,
                                       double latitude, double longitude){
        Restaurant restaurant = new Restaurant(rest_name, rest_address, image_url, latitude, longitude);
        mRestaurants.add(restaurant);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.d(TAG, "onOptionsItemSelected: sign out");
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            Log.d(TAG, "onNavigationItemSelected: camera");
        } else if (id == R.id.nav_gallery) {
            Log.d(TAG, "onNavigationItemSelected: gallery");
            Intent intent = new Intent(this, FindEventActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            Log.d(TAG, "onNavigationItemSelected: slideshow");
        } else if (id == R.id.nav_manage) {
            Log.d(TAG, "onNavigationItemSelected: manage");
        } else if (id == R.id.nav_share) {
            Log.d(TAG, "onNavigationItemSelected: share");
        } else if (id == R.id.nav_send) {
            Log.d(TAG, "onNavigationItemSelected: send: User sign out");
            AuthUI.getInstance().signOut(this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeNavMenuItemName(NavigationView navigationView){
        //Change Navigation Menu item name
        Menu menu = navigationView.getMenu();
        MenuItem nav_camara = menu.findItem(R.id.nav_camera);
        MenuItem nav_gallery = menu.findItem(R.id.nav_gallery);
        MenuItem nav_slideshow = menu.findItem(R.id.nav_slideshow);
        MenuItem nav_manage = menu.findItem(R.id.nav_manage);
        MenuItem nav_share = menu.findItem(R.id.nav_share);
        MenuItem nav_send = menu.findItem(R.id.nav_send);

        nav_camara.setTitle("Restaurant");
        nav_gallery.setTitle("Find Event");
        nav_slideshow.setTitle("My Event");
        nav_manage.setTitle("Setting");
        nav_share.setTitle("Profile");
        nav_send.setTitle("Sign out");
    }
}
