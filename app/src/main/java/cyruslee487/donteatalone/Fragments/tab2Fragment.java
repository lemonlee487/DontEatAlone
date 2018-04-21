package cyruslee487.donteatalone.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cyruslee487.donteatalone.DiscountRoomDatabase.Discount;
import cyruslee487.donteatalone.DiscountRoomDatabase.DiscountDatabase;
import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.RecyclerViewAdapter.FindDiscountRecyclerViewAdapter;
import cyruslee487.donteatalone.RecyclerViewAdapter.FindEventRecyclerViewAdapter;
import cyruslee487.donteatalone.RecyclerViewAdapter.MyDiscountRecyclerViewAdapter;
import cyruslee487.donteatalone.RecyclerViewAdapter.MyEventRecyclerViewAdapter;

public class tab2Fragment extends Fragment{
    private static final String TAG = "DB";

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        mRecyclerView = view.findViewById(R.id.recyclerview_tab2);

        getDiscountFromRoomDatabase();

        return view;
    }

    private void getDiscountFromRoomDatabase(){
        new getDiscountFromRoomDatabaseAsync(getActivity()).execute();
    }

    private class getDiscountFromRoomDatabaseAsync extends AsyncTask<Void, Void, List<Discount>>{
        private DiscountDatabase discountDatabase;

        private getDiscountFromRoomDatabaseAsync(Context context){
            discountDatabase = DiscountDatabase.getDatabase(context);
        }

        @Override
        protected List<Discount> doInBackground(Void... voids) {
            List<Discount> list = discountDatabase.discountDao().getAll();
            return list;
        }

        @Override
        protected void onPostExecute(List<Discount> discounts) {
            initRecyclerView(discounts);
        }
    }

    private void initRecyclerView(List<Discount> list){
        for(Discount d: list){
            if(!checkExpiredDiscount(d))
                list.remove(d);
        }

        MyDiscountRecyclerViewAdapter mAdapter = new MyDiscountRecyclerViewAdapter(list, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private static boolean checkExpiredDiscount(Discount discount){
        String[] sdate = discount.getEndDate().split("/");
        String[] stime = discount.getEndTime().split(":");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int discount_concat_date = Integer.parseInt(sdate[0]+sdate[1]+sdate[2]);
        int discount_concat_time = Integer.parseInt(stime[0]+stime[1]);
        int current_concat_date = Integer.parseInt(""+year+month+day);
        int current_concat_time = Integer.parseInt(""+hour+minute);


        if(discount_concat_date > current_concat_date){
            //Log.d(TAG, "checkExpiredEvent: Discount date > current date");
            return true;
        }else if(discount_concat_date == current_concat_date){
            //Log.d(TAG, "checkExpiredEvent: Discount date == current date");
            //Log.d(TAG, "checkExpiredEvent: " + event_concat_time + " " + current_concat_time);
            if(discount_concat_time >= current_concat_time){
                //Log.d(TAG, "checkExpiredEvent: Discount time >= current time");
                return true;
            }else{
                //Log.d(TAG, "checkExpiredEvent: Discount time < current time");
                return false;
            }
        }else {
            //Log.d(TAG, "checkExpiredEvent: Discount date < current date");
            return false;
        }
    }
}
