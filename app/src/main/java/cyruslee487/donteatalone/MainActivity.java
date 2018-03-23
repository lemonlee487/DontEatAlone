package cyruslee487.donteatalone;

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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DB";

    //vars
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImagesUrls = new ArrayList<>();
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        navigationView.setNavigationItemSelectedListener(this);

        initBitmaps();
    }

    //Recycler View
    private void initBitmaps(){
        Log.d(TAG, "init: perparing images");

        insertImageArray(getResources().getString(R.string.McDonald)
                ,getResources().getString(R.string.McDonald_Url));

        insertImageArray(getResources().getString(R.string.KFC)
                ,getResources().getString(R.string.KFC_Url));

        insertImageArray(getResources().getString(R.string.Wendy)
                ,getResources().getString(R.string.Wendy_Url));

        insertImageArray(getResources().getString(R.string.RR)
                ,getResources().getString(R.string.RR_Url));

        insertImageArray(getResources().getString(R.string.WhiteSpot)
                ,getResources().getString(R.string.WhiteSpot_Url));

        insertImageArray(getResources().getString(R.string.PizzaHut)
                ,getResources().getString(R.string.PizzaHut_Url));

        insertImageArray(getResources().getString(R.string.Domino)
                ,getResources().getString(R.string.Domino_Url));

        insertImageArray(getResources().getString(R.string.Boston)
                ,getResources().getString(R.string.Boston_Url));

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: " + mRestaurants.size());
        RecyclerView recyclerView = findViewById(R.id.recyclerview_content_main);
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(this, mRestaurants);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void insertImageArray(String image_name, String image_url){
        Restaurant restaurant = new Restaurant(image_name, image_url);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            Log.d(TAG, "onOptionsItemSelected: checking");
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
            // Handle the camera action
            Log.d(TAG, "onNavigationItemSelected: camera");
        } else if (id == R.id.nav_gallery) {
            Log.d(TAG, "onNavigationItemSelected: gallery");
        } else if (id == R.id.nav_slideshow) {
            Log.d(TAG, "onNavigationItemSelected: slideshow");
        } else if (id == R.id.nav_manage) {
            Log.d(TAG, "onNavigationItemSelected: manage");
        } else if (id == R.id.nav_share) {
            Log.d(TAG, "onNavigationItemSelected: share");
        } else if (id == R.id.nav_send) {
            Log.d(TAG, "onNavigationItemSelected: send");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
