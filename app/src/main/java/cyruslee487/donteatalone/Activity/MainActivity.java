package cyruslee487.donteatalone.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.persistence.room.Database;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cyruslee487.donteatalone.DiscountRoomDatabase.Discount;
import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.RecyclerViewAdapter.MainMenuRecyclerViewAdapter;
import cyruslee487.donteatalone.Restaurant;
import cyruslee487.donteatalone.SharedPrefManager;
import cyruslee487.donteatalone.UtilFunction;
import okhttp3.internal.Util;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DB";
    private static final String ANONYMOUS = "anonymous";
    private static final String SELECT_DATE = "select_date";
    private static final String SELECT_TIME = "select_time";
    private static final String IMAGE_NAME = "image_name";
    private static final String IMAGE_ADDRESS = "image_address";
    private static final String URL_STORE_TOKEN = "http://10.50.109.25/fcm/register.php";

    private static final int RC_SIGN_IN = 1001;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSION_REQUEST_CODE = 5001;

    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();
    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mFirebaseUser;
    private SharedPrefManager mSharedPrefManager;


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isManager()) {
                    Intent intent = new Intent(MainActivity.this, ManagerEventActivity.class);
                    startActivity(intent);

                } else {
                    Snackbar.make(view, "You do not have Manager Permission", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        UtilFunction.changeNavMenuItemName(navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView title = header.findViewById(R.id.nav_profile_title);
        TextView detail = header.findViewById(R.id.nav_profile_detail);

        if(isServiceOk()){
            getPermission();
        }

        new sendTokenAsync().execute();

        new checkTokenInFirebaseAsync().execute();

        initBitmaps();

        mUsername = ANONYMOUS;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSharedPrefManager = SharedPrefManager.getInstance(this);

        getIntentFromRestaurantInfoActivity();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if(mUser != null){
                    onSignedIn(mUser.getDisplayName());
                } else {
                    onSignOutCleanUp();
                    launchSignInIntent();
                }
            }
        };

        if(mSharedPrefManager != null && mFirebaseAuth.getCurrentUser() != null) {
            if (isManager()) {
                Log.d(TAG, "onCreate: I am owner");
                title.setText(mFirebaseAuth.getCurrentUser().getDisplayName());
                detail.setText(mSharedPrefManager.getOwnerStatus());
            } else {
                Log.d(TAG, "onCreate: I am not the owner");
                title.setText(mFirebaseAuth.getCurrentUser().getDisplayName());
                detail.setText(mSharedPrefManager.getOwnerStatus());
            }
        }

    }

    private void checkEventToken(){
        DatabaseReference databaseReference = mFirebaseDatabase.getReference().child("events");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Event event = postSnapshot.getValue(Event.class);
                    if(event!=null && event.getEmail().equals(mFirebaseUser.getEmail())){
                        Log.d(TAG, "onDataChange: Event: Same email");
                        if(!event.getToken().equals(mSharedPrefManager.getDeviceToken())){
                            Log.d(TAG, "onDataChange: Event Different token");
                            updateTokenInEvent(event);
                        }else
                            Log.d(TAG, "onDataChange: Event: Same Token");
                    }else
                        Log.d(TAG, "onDataChange: Event Different email");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private Event updateTokenInEvent(Event event){
        Event newEvent = new Event(
                event.getKey(),
                event.getUsername(),
                event.getRestaurant_name(),
                event.getLocation(),
                event.getDate(),
                event.getTime(),
                mSharedPrefManager.getDeviceToken(),
                event.getEmail()
        );
        mDatabaseReference.child(event.getKey()).setValue(newEvent);
        Log.d(TAG, "updateTokenInEvent: updated: " + newEvent.getKey());
        return newEvent;
    }

    private void checkDiscountToken(){
        DatabaseReference databaseReference = mFirebaseDatabase.getReference().child("discount");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    Discount discount = postSnapshot.getValue(Discount.class);
                    if(discount!=null && discount.getEmail().equals(mFirebaseUser.getEmail())) {
                        Log.d(TAG, "onDataChange: Discount: Same email");
                        if (!discount.getToken().equals(mSharedPrefManager.getDeviceToken())) {
                            Log.d(TAG, "onDataChange: Discount: Different token");
                            updateTokenInDiscount(discount);
                        }else
                            Log.d(TAG, "onDataChange: Discount: Same token");
                    }else
                        Log.d(TAG, "onDataChange: Discount: Different email");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private Discount updateTokenInDiscount(Discount discount){
        Discount newDiscount = new Discount(
                discount.getAddress(),
                discount.getRest_name(),
                discount.getStartDate(),
                discount.getStartTime(),
                discount.getEndDate(),
                discount.getEndTime(),
                discount.getNumOfPeople(),
                discount.getDescription(),
                mSharedPrefManager.getDeviceToken(),
                discount.getKey(),
                discount.getEmail()
        );
        mDatabaseReference.child(discount.getKey()).setValue(newDiscount);
        Log.d(TAG, "updateTokenInDiscount: updated: " + newDiscount.getKey());
        return newDiscount;
    }

    private class checkTokenInFirebaseAsync extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            checkEventToken();
            checkDiscountToken();
            return null;
        }
    }

    //Google Service Permission Check
    public boolean isServiceOk(){
        int available = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(MainActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //Log.d(TAG, "isServiceOk: Google play service is working");
            return true;
        } else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServiceOk: an error occured");
            Dialog dialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
        } else {
            //Log.d(TAG, "isServiceOk: No map request");
        }
        return false;
    }

    private void getPermission(){
        //Log.d(TAG, "init: Getting permission");
        if(checkLocationPermission()){
            //Log.d(TAG, "init: Permission granted");
        } else {
            //Log.d(TAG, "init: Requesting permission");
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
        switch(requestCode){
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            return;
                        }
                    }
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
                && getIntent().hasExtra(IMAGE_ADDRESS)
                && getIntent().hasExtra(SELECT_DATE)
                && getIntent().hasExtra(SELECT_TIME)){
            String name = getIntent().getStringExtra(IMAGE_NAME);
            String address = getIntent().getStringExtra(IMAGE_ADDRESS);
            String date = getIntent().getStringExtra(SELECT_DATE);
            String time = getIntent().getStringExtra(SELECT_TIME);
            String token = SharedPrefManager.getInstance(this).getDeviceToken();
            String email = "";
            if(mUsername.equals(ANONYMOUS)){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    mUsername = user.getDisplayName();
                    email = user.getEmail();
                }
            }
            String key = mDatabaseReference.push().getKey();
            Log.d(TAG, "getIntentFromRestaurantInfoActivity: key => " + key);
            mDatabaseReference.child("events").child(key).setValue(new Event(key, mUsername, name, address, date, time, token, email));
        }
    }

    //Firebase AuthUI
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
        if(mUsername.equals(ANONYMOUS)) {
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
        MainMenuRecyclerViewAdapter mAdapter =
                new MainMenuRecyclerViewAdapter(this, mRestaurants);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void insertRestaurantArray(String rest_name, String rest_address, String image_url,
                                       double latitude, double longitude){
        Restaurant restaurant = new Restaurant(rest_name, rest_address, image_url, latitude, longitude);
        mRestaurants.add(restaurant);
    }

    //Send Token to Server Using Volley
    private void sendTokenToServer(final String email) {

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        if (token == null) {
            Log.d(TAG, "sendTokenToServer: Token not generated");
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_STORE_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d(TAG, "onResponse: " + obj.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "onErrorResponse: " + error.toString());
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("email", email);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private class sendTokenAsync extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            while(user == null)
                user = FirebaseAuth.getInstance().getCurrentUser();
            Log.d(TAG, "onCreate: User email => " + user.getEmail());
            String email = user.getEmail();
            sendTokenToServer(email);
            return null;
        }
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
            Log.d(TAG, "onOptionsItemSelected: register restaurant account");
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.restaurant_permission_custom_dialog,
                    null);
            final EditText mRegEditText = mView.findViewById(R.id.editText_cus_dialog);
            mBuilder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mRegEditText.getText().toString().isEmpty()) {
                        String code = mRegEditText.getText().toString();
                        if(code.equals("restaurantowner")){
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            mSharedPrefManager.saveOwnerStatus("Restaurant Manager", mFirebaseUser.getEmail());
                            isManager();
                            NavigationView nv = findViewById(R.id.nav_view);
                            changeNavigationTitle(nv);
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Register as owner", Toast.LENGTH_SHORT).show();
                        } else{
                            Toast.makeText(MainActivity.this, "You are fake news", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isManager(){
        String status = mSharedPrefManager.getOwnerStatus();
        String email = mSharedPrefManager.getOwnerEmail();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        String user_email = mFirebaseUser.getEmail();

        if(status.equals("Restaurant Manager")){
            if(email.equals(user_email)){
                Log.d(TAG, "isManager: Status: True, Email: Match");
                return true;
            }else{
                Log.d(TAG, "isManager: Status: True, Email: Not match");
                mSharedPrefManager.saveOwnerStatus("Guest", user_email);
                return false;
            }
        }else{
            Log.d(TAG, "isManager: Status: False");
            mSharedPrefManager.saveOwnerStatus("Guest", user_email);
            return false;
        }
    }

    private void changeNavigationTitle(NavigationView navigationView){
        View header = navigationView.getHeaderView(0);
        TextView title = header.findViewById(R.id.nav_profile_title);
        TextView detail = header.findViewById(R.id.nav_profile_detail);
        if(mFirebaseAuth!=null)
            title.setText(mFirebaseAuth.getCurrentUser().getDisplayName());

        if(mSharedPrefManager!=null)
            detail.setText(mSharedPrefManager.getOwnerStatus());
    }
    
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, FindEventActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, FindDiscountActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, MyEventActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {
            Log.d(TAG, "onNavigationItemSelected: share");
        } else if (id == R.id.nav_send) {
            Log.d(TAG, "onNavigationItemSelected: send: User sign out");
            AuthUI.getInstance().signOut(this);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
