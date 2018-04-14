package cyruslee487.donteatalone.RecyclerViewAdapter;

import android.app.AlertDialog;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import cyruslee487.donteatalone.Common;
import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.EventRoomDatabase.EventDao;
import cyruslee487.donteatalone.EventRoomDatabase.EventDatabase;
import cyruslee487.donteatalone.Model.MyResponse;
import cyruslee487.donteatalone.Model.Sender;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.Remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.mMEViewHolder>{
    private static final String TAG = "DB";

    private Context mContext;
    private List<Event> mEventsList;


    public MyEventRecyclerViewAdapter(Context mContext, List<Event> mEventsFromFirebase) {
        this.mContext = mContext;
        this.mEventsList = mEventsFromFirebase;
    }

    @Override
    public MyEventRecyclerViewAdapter.mMEViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.find_event_list_item, parent, false);
        return new MyEventRecyclerViewAdapter.mMEViewHolder(view);
    }

    @Override
    public void onBindViewHolder(mMEViewHolder holder, int position) {
        final Event event = mEventsList.get(position);
        //Common.currentToken = SharedPrefManager.getInstance(mContext).getDeviceToken();

        String url = getImageUrl(event.getRestaurant_name());
        if(!url.equals("nothing")){
            Glide.with(mContext)
                    .load(url)
                    .asBitmap()
                    .into(holder.image_find_event);
        }

        holder.username_find_event.setText(event.getUsername());
        holder.restaurant_name_find_event.setText(event.getRestaurant_name());
        holder.restaurant_address_find_event.setText(event.getLocation());
        holder.date_find_event.setText(event.getDate());
        holder.time_find_event.setText(event.getTime());

        holder.relative_find_event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Show map
                Log.d(TAG, "onClick: MyEventAdapter => Show map");
            }
        });

        holder.relative_find_event.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLongClick: MyEventAdapter => Delete my event");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int decision) {
                        switch(decision){
                            case DialogInterface.BUTTON_POSITIVE:
                                removeItem(event);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Delete this event?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener)
                        .show();
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mEventsList.size();
    }

    public class mMEViewHolder extends RecyclerView.ViewHolder{

        TextView username_find_event, restaurant_name_find_event,
                restaurant_address_find_event, date_find_event, time_find_event;
        ImageView image_find_event;
        RelativeLayout relative_find_event;


        public mMEViewHolder(View view) {
            super(view);
            username_find_event = view.findViewById(R.id.username_find_event);
            restaurant_name_find_event = view.findViewById(R.id.restaurant_name_find_event);
            restaurant_address_find_event = view.findViewById(R.id.restaurant_address_find_event);
            date_find_event = view.findViewById(R.id.date_find_event);
            time_find_event = view.findViewById(R.id.time_find_event);
            image_find_event = view.findViewById(R.id.image_view_find_event);
            relative_find_event = view.findViewById(R.id.relative_find_event);
        }
    }

    private void removeItem(Event event){
        new removeItemAsync(mContext, event).execute();
        int position = mEventsList.indexOf(event);
        mEventsList.remove(position);
        notifyItemRemoved(position);
    }

    private class removeItemAsync extends AsyncTask<Void, Void, Void>{
        private EventDatabase eventDatabase;
        private Event event;

        private removeItemAsync(Context mContext, Event event){
            eventDatabase = EventDatabase.getDatabase(mContext);
            this.event = event;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            eventDatabase.eventDao().delete(event);
            return null;
        }
    }

    private String getImageUrl(String name){
        switch(name){
            case "McDonald":
                return "http://computercleaning.files.wordpress.com/2008/03/mcdonalds.jpg";
            case "KFC":
                return "https://www.startwire.com/job-applications/logos/kfc.png";
            case "Red Robin":
                return "https://fthmb.tqn.com/4GbIePRLV9MWmqtQMDWRMTaF3wc=/2000x1500/filters:fill(auto,1)/red-robin-logo-581753a33df78cc2e89d269c.PNG";
            case "Wendy's":
                return "https://upload.wikimedia.org/wikipedia/en/thumb/6/66/Wendy%27s_logo_2012.svg/1200px-Wendy%27s_logo_2012.svg.png";
            case "Pizza Hut":
                return "https://vignette.wikia.nocookie.net/logopedia/images/b/b3/Pizza_Hut_Logo_2.png/revision/latest?cb=20161129133747";
            case "Domino's":
                return "https://s3-media4.fl.yelpcdn.com/bphoto/uM02kVl22c0R5_btCDmwDQ/ls.jpg";
            case "White Spot":
                return "https://www.whitespot.ca/sites/all/themes/whitespot/logo.png";
            case "Boston Pizza":
                return  "https://pbs.twimg.com/profile_images/899615036699574272/qC1FzTOv_400x400.jpg";
            default:
                return "nothing";
        }
    }
}
