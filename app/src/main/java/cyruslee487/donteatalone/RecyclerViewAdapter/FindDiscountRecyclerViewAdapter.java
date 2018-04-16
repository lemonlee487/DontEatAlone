package cyruslee487.donteatalone.RecyclerViewAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cyruslee487.donteatalone.Common;
import cyruslee487.donteatalone.Discount;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.Remote.APIService;

public class FindDiscountRecyclerViewAdapter extends RecyclerView.Adapter<FindDiscountRecyclerViewAdapter.FDViewHolder>{

    private static final String TAG = "DB";

    private List<Discount> mDiscountList;
    private Context mContext;
    private APIService mAPIService;

    public FindDiscountRecyclerViewAdapter(List<Discount> mDiscountList, Context mContext) {
        this.mDiscountList = mDiscountList;
        this.mContext = mContext;
    }

    @Override
    public FDViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discount_list_item, parent, false);
        return new FDViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FDViewHolder holder, int position) {
        final Discount discount = mDiscountList.get(position);
        Common.currentToken = discount.getToken();
        mAPIService = Common.getFCMClient();

        Log.d(TAG, "onBindViewHolder: " + discount.getEndDate() + " " + discount.getEndTime());

        holder.restname_fd.setText(discount.getRest_name());
        holder.address_fd.setText(discount.getAddress());
        holder.startdate_fd.setText(discount.getStartDate());
        holder.starttime_fd.setText(discount.getStartTime());
        holder.enddate_fd.setText(discount.getEndDate());
        holder.endtime_fd.setText(discount.getEndTime());
        holder.people_fd.setText(String.valueOf(discount.getNumOfPeople()));
    }

    @Override
    public int getItemCount() {
        return mDiscountList.size();
    }

    public class FDViewHolder extends RecyclerView.ViewHolder{
        TextView restname_fd, address_fd, startdate_fd, starttime_fd,
        enddate_fd, endtime_fd, people_fd;

        public FDViewHolder(View itemView) {
            super(itemView);
            restname_fd = itemView.findViewById(R.id.rest_name_discount_list_item);
            address_fd = itemView.findViewById(R.id.rest_address_discount_list_item);
            startdate_fd = itemView.findViewById(R.id.start_date_discount_list_item);
            starttime_fd = itemView.findViewById(R.id.start_time_discount_list_item);
            enddate_fd = itemView.findViewById(R.id.end_date_discount_list_item);
            endtime_fd = itemView.findViewById(R.id.end_time_discount_list_item);
            people_fd = itemView.findViewById(R.id.people_num_discount_list_item);
        }
    }
}
