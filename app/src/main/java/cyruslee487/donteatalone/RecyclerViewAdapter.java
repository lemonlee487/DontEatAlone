package cyruslee487.donteatalone;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by cyrus on 2018-03-22.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.mViewHolder> {

    //Constants
    private String TAG = "DB";
    private String IMAGE_URL = "image_url";
    private String IMAGE_NAME = "image_name";

    //vars
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context mContext,
                               ArrayList<String> mImageNames,
                               ArrayList<String> mImages) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
    }

    public RecyclerViewAdapter(Context mContext, ArrayList<Restaurant> mRestaurants) {
        this.mContext = mContext;
        this.mRestaurants = mRestaurants;
        //Log.d(TAG, "RecyclerViewAdapter: Constructor: "+this.mUsername + "____" + mUsername);
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_list_item, parent, false);
        mViewHolder holder = new mViewHolder(view);
        return holder;
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

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + restaurant.getName());
                Intent intent = new Intent(mContext ,RestaurantInfoActivity.class);

                intent.putExtra(IMAGE_URL, restaurant.getImageUrl());
                intent.putExtra(IMAGE_NAME, restaurant.getName());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        //return mImages.size();
        return mRestaurants.size();
    }

    public class mViewHolder extends RecyclerView.ViewHolder{
        TextView list_item_text_view;
        ImageView list_item_image_view;
        RelativeLayout parentLayout;


        public mViewHolder(View itemView) {
            super(itemView);
            list_item_text_view = itemView.findViewById(R.id.textview_list_item);
            list_item_image_view = itemView.findViewById(R.id.image_list_item);
            parentLayout = itemView.findViewById(R.id.relative_list_item);

        }
    }

}
