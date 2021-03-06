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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import cyruslee487.donteatalone.Common;
import cyruslee487.donteatalone.DiscountRoomDatabase.Discount;
import cyruslee487.donteatalone.DiscountRoomDatabase.DiscountDatabase;
import cyruslee487.donteatalone.Model.MyResponse;
import cyruslee487.donteatalone.Model.Sender;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.Remote.APIService;
import cyruslee487.donteatalone.SharedPrefManager;
import cyruslee487.donteatalone.UtilFunction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                //Check if discount has expired
                if(UtilFunction.checkExpiredDiscount(discount)){
                    //Show description interface
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int decision) {
                            switch(decision){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Insert discount to room database
                                    new insertMyDiscountAsync(mContext, discount).execute(discount);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("Discount detail:\n\n" + discount.getDescription())
                            .setPositiveButton("Claim it", dialogClickListener)
                            .setNegativeButton("Dismiss", dialogClickListener)
                            .show();

                }else{
                    Toast.makeText(mContext, "This discount has expired", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Long click listener for Manager only
        holder.relative_fd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(isManager(mContext)){
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int decision) {
                            switch(decision){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Intent to Manager Activity with current result
                                    //Send action to Activity, "Modify"
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    String key = discount.getKey();
                                    databaseReference.child(key).removeValue();
                                    Toast.makeText(mContext, "Discount removed", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage("What do you want to do with this discount")
                            .setPositiveButton("Extend", dialogClickListener)
                            .setNegativeButton("Delete", dialogClickListener)
                            .show();
                }

                return true;
            }
        });
    }

    private boolean isManager(Context context){
        SharedPrefManager mSharedPrefManager = SharedPrefManager.getInstance(context);
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        String status = mSharedPrefManager.getOwnerStatus();
        String email = mSharedPrefManager.getOwnerEmail();
        String user_email = mFirebaseUser.getEmail();

        if(status.equals("Restaurant Manager")){
            if(email.equals(user_email)){
                Log.d(TAG, "isManager: Status: True, Email: Match");
                return true;
            }else{
                Log.d(TAG, "isManager: Status: True, Email: Not match");
                mSharedPrefManager.saveOwnerStatus("Guest", user_email);
                return false;
            }
        }else{
            Log.d(TAG, "isManager: Status: False");
            mSharedPrefManager.saveOwnerStatus("Guest", user_email);
            return false;
        }
    }

    private boolean checkAvailableRoom(Discount discount, DatabaseReference databaseReference){
        int people = discount.getNumOfPeople();
        if(people != 0)
            return true;
        else
            return false;
    }
    
    private class insertMyDiscountAsync extends AsyncTask<Discount, Void, Boolean>{
        private DiscountDatabase discountDatabase;
        private Discount discount;
        
        private insertMyDiscountAsync(Context mContext, Discount discount){
            discountDatabase = DiscountDatabase.getDatabase(mContext);
            this.discount = discount;
        }


        @Override
        protected Boolean doInBackground(Discount... discounts) {
            List<Discount> discountList = discountDatabase.discountDao().getAll();

            for(Discount d: discountList){
                if(d.getKey().equals(discounts[0].getKey()))
                    return false;
            }
            DatabaseReference databaseReference = FirebaseDatabase
                    .getInstance().getReference().child("discount");

            if(!checkAvailableRoom(discount, databaseReference)) {
                return false;
            }

            int people = discount.getNumOfPeople() - 1;

            Discount newDiscount = new Discount(
                    discount.getAddress(),
                    discount.getRest_name(),
                    discount.getStartDate(),
                    discount.getStartTime(),
                    discount.getEndDate(),
                    discount.getEndTime(),
                    people,
                    discount.getDescription(),
                    discount.getToken(),
                    discount.getKey(),
                    discount.getEmail()
            );
            databaseReference.child(discount.getKey()).setValue(newDiscount);

            discounts[0].setNumOfPeople(discounts[0].getNumOfPeople() - 1);
            discountDatabase.discountDao().insert(discounts);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
                Toast.makeText(mContext, "Check -My Discount- for new added discount", Toast.LENGTH_LONG).show();

                //Send FCM to Event host
                cyruslee487.donteatalone.Model.Notification notification =
                        new cyruslee487.donteatalone.Model.Notification(
                                "People need: " + discount.getNumOfPeople(),
                                "Someone has claimed this discount");
                Sender sender = new Sender(Common.currentToken, notification);
                mAPIService.sendNotification(sender)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.body().success == 1) {
                                    Log.d(TAG, "onResponse: FindDiscountRecyclerAdapter: Success");
                                } else {
                                    Log.d(TAG, "onResponse: FindDiscountRecyclerAdapter: Failed");
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {
                                Log.e(TAG, "onFailure: Error", t.getCause());
                            }
                        });
            }else{
                Toast.makeText(mContext, "You can't claim this discount", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDiscountList.size();
    }

    public class FDViewHolder extends RecyclerView.ViewHolder{
        TextView restname_fd, address_fd, startdate_fd, starttime_fd,
        enddate_fd, endtime_fd, people_fd;

        RelativeLayout relative_fd;

        public FDViewHolder(View itemView) {
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
