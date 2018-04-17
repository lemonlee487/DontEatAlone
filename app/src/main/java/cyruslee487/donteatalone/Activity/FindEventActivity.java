package cyruslee487.donteatalone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.RecyclerViewAdapter.FindEventRecyclerViewAdapter;
import cyruslee487.donteatalone.SharedPrefManager;
import cyruslee487.donteatalone.UtilFunction;

public class FindEventActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DB";

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private ChildEventListener mChildEventListener;
    private SharedPrefManager mSharedPrefManager;
    private RecyclerView recyclerView;
    private FindEventRecyclerViewAdapter mAdapter;
    
    private ArrayList<Event> mEventsFromFirebase = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_event);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        UtilFunction.changeNavMenuItemName(navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("events");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSharedPrefManager = SharedPrefManager.getInstance(this);

        if(isManager())
            changeNavigationTitle(navigationView);
        else
            changeNavigationTitle(navigationView);

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: FindEventActivity");
        recyclerView = findViewById(R.id.recyclerview_find_event);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEventsFromFirebase.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Event event = postSnapshot.getValue(Event.class);
                    //Update event's token with same email
                    if(event!=null && event.getEmail().equals(mFirebaseUser.getEmail())){
                        Log.d(TAG, "onDataChange: Same email");
                        if(!event.getToken().equals(mSharedPrefManager.getDeviceToken())){
                            Log.d(TAG, "onDataChange: Different token");
                            Event newEvent = updateTokenInEvent(event);
                            mEventsFromFirebase.add(newEvent);
                        }else{
                            Log.d(TAG, "onDataChange: Same Token");
                            mEventsFromFirebase.add(event);
                        }
                    }else{
                        Log.d(TAG, "onDataChange: Different email");
                        mEventsFromFirebase.add(event);
                    }

                    mAdapter = new FindEventRecyclerViewAdapter(FindEventActivity.this, mEventsFromFirebase);
                    recyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: Read failed: " + databaseError.getMessage());
            }
        });


    }

    private Event updateTokenInEvent(Event event){
        String key, username, rest_name, address, date, time, token, email;
        key = event.getKey();
        username = event.getUsername();
        rest_name = event.getRestaurant_name();
        address = event.getLocation();
        date = event.getDate();
        time = event.getTime();
        email = event.getEmail();
        token = mSharedPrefManager.getDeviceToken();
        Event newEvent = new Event(key, username, rest_name, address, date, time, token ,email);
        mDatabaseReference.child(key).setValue(newEvent);
        Log.d(TAG, "updateTokenInEvent: updated: " + newEvent.getKey());
        return newEvent;
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
        TextView title = header.findViewById(R.id.nav_profile_title_fe);
        TextView detail = header.findViewById(R.id.nav_profile_detail_fe);

        if(mFirebaseAuth.getCurrentUser()!=null)
            title.setText(mFirebaseAuth.getCurrentUser().getDisplayName());

        if(mSharedPrefManager!=null)
            detail.setText(mSharedPrefManager.getOwnerStatus());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.find_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(FindEventActivity.this, MainActivity.class);
            intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, FindDiscountActivity.class);
            intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(this, MyEventActivity.class);
            intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
