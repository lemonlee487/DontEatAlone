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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DB";

    //vars
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImagesUrls = new ArrayList<>();

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

        mImagesUrls.add("http://computercleaning.files.wordpress.com/2008/03/mcdonalds.jpg");
        mImageNames.add("McDonald");

        mImagesUrls.add("https://www.startwire.com/job-applications/logos/kfc.png");
        mImageNames.add("KFC");

        mImagesUrls.add("https://fthmb.tqn.com/4GbIePRLV9MWmqtQMDWRMTaF3wc=/2000x1500/filters:fill(auto,1)/red-robin-logo-581753a33df78cc2e89d269c.PNG");
        mImageNames.add("Red Robin");

        mImagesUrls.add("https://upload.wikimedia.org/wikipedia/en/thumb/6/66/Wendy%27s_logo_2012.svg/1200px-Wendy%27s_logo_2012.svg.png");
        mImageNames.add("Wendy's");

        mImagesUrls.add("https://vignette.wikia.nocookie.net/logopedia/images/b/b3/Pizza_Hut_Logo_2.png/revision/latest?cb=20161129133747");
        mImageNames.add("Pizza Hut");

        mImagesUrls.add("https://s3-media4.fl.yelpcdn.com/bphoto/uM02kVl22c0R5_btCDmwDQ/ls.jpg");
        mImageNames.add("Domino's");

        mImagesUrls.add("https://www.whitespot.ca/sites/all/themes/whitespot/logo.png");
        mImageNames.add("White Spot");

        mImagesUrls.add("https://pbs.twimg.com/profile_images/899615036699574272/qC1FzTOv_400x400.jpg");
        mImageNames.add("Boston Pizza");

        initRecyclerView();
    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: ");
        RecyclerView recyclerView = findViewById(R.id.recyclerview_content_main);
        RecyclerViewAdapter mAdapter = new RecyclerViewAdapter(this, mImageNames, mImagesUrls);
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
