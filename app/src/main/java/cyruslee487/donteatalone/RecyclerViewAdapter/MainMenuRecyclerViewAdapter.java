package cyruslee487.donteatalone.RecyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
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

import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.Restaurant;
import cyruslee487.donteatalone.Activity.RestaurantInfoActivity;


public class MainMenuRecyclerViewAdapter extends RecyclerView.Adapter<MainMenuRecyclerViewAdapter.mViewHolder> {

    //Constants
    private static final String TAG = "DB";
    private static final String IMAGE_URL = "image_url";
    private static final String IMAGE_NAME = "image_name";
    private static final String IMAGE_ADDRESS = "image_address";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";

    //vars
    private ArrayList<Restaurant> mRestaurants;
    private Context mContext;

    public MainMenuRecyclerViewAdapter(Context mContext, ArrayList<Restaurant> mRestaurants) {
        this.mContext = mContext;
        this.mRestaurants = mRestaurants;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_list_item, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final mViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        final Restaurant restaurant = mRestaurants.get(position);

        Glide.with(mContext)
                .load(restaurant.getImageUrl())
                .asBitmap()
                .into(holder.list_item_image_view);

        holder.list_item_text_view.setText(restaurant.getName());
        holder.address_text_view.setText(restaurant.getAddress());


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + restaurant.getName());
                Intent intent = new Intent(mContext ,RestaurantInfoActivity.class);

                intent.putExtra(IMAGE_URL, restaurant.getImageUrl());
                intent.putExtra(IMAGE_NAME, restaurant.getName());
                intent.putExtra(IMAGE_ADDRESS, restaurant.getAddress());
                intent.putExtra(LATITUDE, restaurant.getLatitude());
                intent.putExtra(LONGITUDE, restaurant.getLongitude());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public class mViewHolder extends RecyclerView.ViewHolder{
        TextView list_item_text_view;
        TextView address_text_view;
        ImageView list_item_image_view;
        RelativeLayout parentLayout;


        public mViewHolder(View itemView) {
            super(itemView);
            list_item_text_view = itemView.findViewById(R.id.textview_list_item);
            address_text_view = itemView.findViewById(R.id.textview2_list_item);
            list_item_image_view = itemView.findViewById(R.id.image_list_item);
            parentLayout = itemView.findViewById(R.id.relative_list_item);

        }
    }

}
