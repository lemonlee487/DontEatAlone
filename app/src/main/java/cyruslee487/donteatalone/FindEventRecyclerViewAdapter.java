package cyruslee487.donteatalone;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FindEventRecyclerViewAdapter extends RecyclerView.Adapter<FindEventRecyclerViewAdapter.mFEViewHolder>{

    private Context mContext;
    private List<Event> mEventList = new ArrayList<>();

    public FindEventRecyclerViewAdapter(Context mContext, List<Event> mEventList) {
        this.mContext = mContext;
        this.mEventList = mEventList;
    }

    @Override
    public mFEViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.find_event_list_item, parent, false);
        return new mFEViewHolder(view);
    }

    @Override
    public void onBindViewHolder(mFEViewHolder holder, int position) {
        final Event event = mEventList.get(position);

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
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
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
            relative_find_event = view.findViewById(R.id.relative_list_item);
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
