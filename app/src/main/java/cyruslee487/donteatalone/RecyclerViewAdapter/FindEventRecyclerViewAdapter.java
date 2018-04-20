package cyruslee487.donteatalone.RecyclerViewAdapter;

import android.app.Notification;
import android.arch.persistence.room.Database;
import android.content.Context;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import cyruslee487.donteatalone.Common;
import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.EventRoomDatabase.EventDatabase;
import cyruslee487.donteatalone.Model.MyResponse;
import cyruslee487.donteatalone.Model.Sender;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.Remote.APIService;
import cyruslee487.donteatalone.SharedPrefManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindEventRecyclerViewAdapter extends RecyclerView.Adapter<FindEventRecyclerViewAdapter.mFEViewHolder>{

    private static final String TAG = "DB";

    private Context mContext;
    private List<Event> mEventsFromFirebase;
    private APIService mAPIService;


    public FindEventRecyclerViewAdapter(Context mContext, List<Event> mEventsFromFirebase) {
        this.mContext = mContext;
        this.mEventsFromFirebase = mEventsFromFirebase;
    }

    @Override
    public mFEViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.find_event_list_item, parent, false);
        return new mFEViewHolder(view);
    }

    @Override
    public void onBindViewHolder(mFEViewHolder holder, int position) {
        final Event event = mEventsFromFirebase.get(position);
        //Common.currentToken = SharedPrefManager.getInstance(mContext).getDeviceToken();
        Common.currentToken = event.getToken();
        mAPIService = Common.getFCMClient();

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
                //Add event to Room Databse
                new insertMyEventAsync(mContext).execute(event);

                Toast.makeText(mContext, "Check -My Event- for new added event", Toast.LENGTH_LONG).show();

                //Send FCM to Event host
                cyruslee487.donteatalone.Model.Notification notification =
                        new cyruslee487.donteatalone.Model.Notification(
                                event.getRestaurant_name() + " On " + event.getDate() + " At " + event.getTime(),
                                "You have some to eat with");
                Sender sender = new Sender(Common.currentToken,notification);
                mAPIService.sendNotification(sender)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if(response.body().success == 1){
                                    Log.d(TAG, "onResponse: FindEventRecyclerAdapter: Success");
                                }else{
                                    Log.d(TAG, "onResponse: FindEventRecyclerAdapter: Failed");
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.e(TAG, "onFailure: Error", t.getCause());
                            }
                        });
            }
        });
    }

    private void deleteEvent(String key){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("events").child(key);
        reference.removeValue();
        Toast.makeText(mContext, "Event removed", Toast.LENGTH_SHORT).show();
    }

    private class insertMyEventAsync extends AsyncTask<Event, Void, Void>{
        private EventDatabase eventDatabase;

        private insertMyEventAsync(Context mContext){
            eventDatabase = EventDatabase.getDatabase(mContext);
        }

        @Override
        protected Void doInBackground(Event... events) {
            eventDatabase.eventDao().insert(events);
            Log.d(TAG, "doInBackground: FindEventRecyclerViewAdapter: add event to my event");
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return mEventsFromFirebase.size();
    }

    public class mFEViewHolder extends RecyclerView.ViewHolder{

        TextView username_find_event, restaurant_name_find_event,
                restaurant_address_find_event, date_find_event, time_find_event;
        ImageView image_find_event;
        RelativeLayout relative_find_event;


        public mFEViewHolder(View view) {
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
