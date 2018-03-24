package cyruslee487.donteatalone;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cyrus on 2018-03-24.
 */

public class FindItemRecyclerViewApdater extends ArrayAdapter<Event>{

    private static final String TAG = "DB";

    public FindItemRecyclerViewApdater(@NonNull Context context, int resource, @NonNull List<Event> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = ((Activity) getContext())
                    .getLayoutInflater()
                    .inflate(R.layout.find_event_list_item, parent, false);
        }

        TextView username_text_view = convertView.findViewById(R.id.username_find_event_list_item);
        TextView rest_name_text_view = convertView.findViewById(R.id.restaurant_name_find_event_list_item);
        TextView adderss_text_view = convertView.findViewById(R.id.address_find_event_list_item);
        TextView date_text_view = convertView.findViewById(R.id.date_find_event_list_item);
        TextView time_text_view = convertView.findViewById(R.id.time_find_event_list_item);
        ImageView list_item_image_view = convertView.findViewById(R.id.image_find_event_list_item);
        RelativeLayout parentLayout = convertView.findViewById(R.id.relative__find_event_list_item);

        Event event = getItem(position);

        username_text_view.setText(event.getUsername());
        rest_name_text_view.setText(event.getRestaurant_name());
        adderss_text_view.setText(event.getLocation());
        date_text_view.setText(event.getDate());
        time_text_view.setText(event.getTime());

        String url = getImageUrl(event.getRestaurant_name());
        if(!url.equals("nothing")){
            Log.d(TAG, "getView: got url");
            Glide.with(list_item_image_view.getContext())
                    .load(url)
                    .asBitmap()
                    .into(list_item_image_view);
        }

        //parentLayout.setOnClickListener(new On);

        return convertView;
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
