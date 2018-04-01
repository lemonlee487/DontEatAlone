package cyruslee487.donteatalone.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.EventRoomDatabase.EventDatabase;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.RecyclerViewAdapter.FindEventRecyclerViewAdapter;

public class MyEventActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DB";


    private List<Event> mEventFromRoomDatabase = new ArrayList<>();
    private TextView usernameTV, restNameTV, addressTV, dateTV, timeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        changeNavMenuItemName(navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        usernameTV = findViewById(R.id.username_my_event);
        restNameTV = findViewById(R.id.restaurant_name_my_event);
        addressTV = findViewById(R.id.restaurant_address_my_event);
        dateTV = findViewById(R.id.date_my_event);
        timeTV = findViewById(R.id.time_my_event);

        getEventFromRoomDatabase();
    }

    private void getEventFromRoomDatabase(){
        new getEventFromRoomDatabase(this).execute();
    }

    private class getEventFromRoomDatabase extends AsyncTask<Void, Void, Void> {
        private EventDatabase eventDatabase;

        private getEventFromRoomDatabase(Context mContext){
            eventDatabase = EventDatabase.getDatabase(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mEventFromRoomDatabase = eventDatabase.eventDao().getAll();
            setUpComingEvent();
            return null;
        }
    }

    private void setUpComingEvent(){
        Event event = mEventFromRoomDatabase.get(0);
        if(event != null){
            usernameTV.setText(event.getUsername());
            restNameTV.setText(event.getRestaurant_name());
            addressTV.setText(event.getLocation());
            dateTV.setText(event.getDate());
            timeTV.setText(event.getTime());
            Log.d(TAG, "setUpComingEvent: Set");

            initRecyclerView();
        }else{
            Log.e(TAG, "setUpComingEvent: Event from room is null");
        }
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: ok");
        RecyclerView recyclerView = findViewById(R.id.recyclerview_my_event);
        FindEventRecyclerViewAdapter mAdapter =
                new FindEventRecyclerViewAdapter(this, mEventFromRoomDatabase);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(this, FindEventActivity.class);
            intent.setFlags(intent.getFlags()|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
