package cyruslee487.donteatalone.RecyclerViewAdapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import cyruslee487.donteatalone.Common;
import cyruslee487.donteatalone.DiscountRoomDatabase.Discount;
import cyruslee487.donteatalone.DiscountRoomDatabase.DiscountDatabase;
import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.Model.MyResponse;
import cyruslee487.donteatalone.Model.Sender;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.Remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyDiscountRecyclerViewAdapter extends RecyclerView.Adapter<MyDiscountRecyclerViewAdapter.MDViewHolder>{

    private static final String TAG ="DB";

    private List<Discount> mDiscountList;
    private Context mContext;
    private APIService mAPIService;

    public MyDiscountRecyclerViewAdapter(List<Discount> mDiscountList, Context mContext) {
        this.mDiscountList = mDiscountList;
        this.mContext = mContext;
    }

    @Override
    public MDViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.discount_list_item, parent, false);
        return new MDViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MDViewHolder holder, int position) {
        final Discount discount = mDiscountList.get(position);
        Common.currentToken = discount.getToken();
        mAPIService = Common.getFCMClient();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("discount");

        holder.restname_fd.setText(discount.getRest_name());
        holder.address_fd.setText(discount.getAddress());
        holder.startdate_fd.setText(discount.getStartDate());
        holder.starttime_fd.setText(discount.getStartTime());
        holder.enddate_fd.setText(discount.getEndDate());
        holder.endtime_fd.setText(discount.getEndTime());
        holder.people_fd.setText(String.valueOf(discount.getNumOfPeople()));

        holder.relative_fd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int decision) {
                        switch(decision){
                            case DialogInterface.BUTTON_POSITIVE:
                                Log.d(TAG, "removeItem: discount room database size: " + mDiscountList.size());
                                updateNumOfPeople(discount, databaseReference);
                                Toast.makeText(mContext, "You have remove this discount", Toast.LENGTH_SHORT).show();
                                //Send FCM to Event host
                                cyruslee487.donteatalone.Model.Notification notification =
                                        new cyruslee487.donteatalone.Model.Notification(
                                                "People need: " + discount.getNumOfPeople(),
                                                "Someone has give up the discount");
                                Sender sender = new Sender(Common.currentToken, notification);
                                mAPIService.sendNotification(sender)
                                        .enqueue(new Callback<MyResponse>() {
                                            @Override
                                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                if (response.body().success == 1) {
                                                    Log.d(TAG, "onResponse: FindDiscountRecyclerAdapter: Success"); } else {
                                                    Log.d(TAG, "onResponse: FindDiscountRecyclerAdapter: Failed"); } }
                                                    @Override
                                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                                Log.e(TAG, "onFailure: Error", t.getCause()); }
                                            });
                                removeItem(discount);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Discount detail:\n\n" + discount.getDescription() + "\n\nYou wanna remove this discount?")
                        .setPositiveButton("Remove", dialogClickListener)
                        .setNegativeButton("Dismiss", dialogClickListener)
                        .show();
            }
        });
    }

    private void removeItem(Discount discount){
        new removeDiscountFromRoom(discount, mContext).execute();
        int position = mDiscountList.indexOf(discount);
        mDiscountList.remove(position);
        notifyItemRemoved(position);
    }

    private class removeDiscountFromRoom extends AsyncTask<Void, Void, Void>{
        private Discount discount;
        private DiscountDatabase discountDatabase;

        public removeDiscountFromRoom(Discount discount, Context context) {
            this.discount = discount;
            discountDatabase = DiscountDatabase.getDatabase(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            discountDatabase.discountDao().delete(discount);
            return null;
        }
    }

    private void updateNumOfPeople(Discount discount, DatabaseReference databaseReference){
        Discount newDiscount = new Discount(
                discount.getAddress(),
                discount.getRest_name(),
                discount.getStartDate(),
                discount.getStartTime(),
                discount.getEndDate(),
                discount.getEndTime(),
                discount.getNumOfPeople()+1,
                discount.getDescription(),
                discount.getToken(),
                discount.getKey(),
                discount.getEmail()
        );
        databaseReference.child(discount.getKey()).setValue(newDiscount);
        Log.d(TAG, "updateTokenInDiscount: updated num of people: " + newDiscount.getKey());
    }
    @Override
    public int getItemCount() {
        return mDiscountList.size();
    }

    public class MDViewHolder extends RecyclerView.ViewHolder{
        TextView restname_fd, address_fd, startdate_fd, starttime_fd,
                enddate_fd, endtime_fd, people_fd;

        RelativeLayout relative_fd;

        public MDViewHolder(View itemView) {
            super(itemView);
            restname_fd = itemView.findViewById(R.id.rest_name_discount_list_item);
            address_fd = itemView.findViewById(R.id.rest_address_discount_list_item);
            startdate_fd = itemView.findViewById(R.id.start_date_discount_list_item);
            starttime_fd = itemView.findViewById(R.id.start_time_discount_list_item);
            enddate_fd = itemView.findViewById(R.id.end_date_discount_list_item);
            endtime_fd = itemView.findViewById(R.id.end_time_discount_list_item);
            people_fd = itemView.findViewById(R.id.people_num_discount_list_item);
            relative_fd = itemView.findViewById(R.id.relative_discount_list_item);
        }
    }
}
