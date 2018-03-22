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

/**
 * Created by cyrus on 2018-03-22.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.mViewHolder> {

    private String TAG = "DB";
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context mContext,
                               ArrayList<String> mImageNames,
                               ArrayList<String> mImages) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_list_item, parent, false);
        mViewHolder holder = new mViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called");

        Glide.with(mContext)
                .load(mImages.get(position))
                .into(holder.list_item_image_view);

        holder.list_item_text_view.setText(mImageNames.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + mImageNames.get(position));
            }
        });

        //hi

    }

    @Override
    public int getItemCount() {
        return mImages.size();
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
