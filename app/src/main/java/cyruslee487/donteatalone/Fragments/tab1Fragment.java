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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.EventRoomDatabase.EventDatabase;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.RecyclerViewAdapter.MyEventRecyclerViewAdapter;

public class tab1Fragment extends Fragment {
    private static final String TAG = "DB";

    private RecyclerView mRecyclerView;
    private TextView mUsername, mRestName, mAddress, mDate, mTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        mRecyclerView = view.findViewById(R.id.recyclerview_tab1);
        mUsername = view.findViewById(R.id.username_tab1);
        mRestName = view.findViewById(R.id.restaurant_name_tab1);
        mAddress = view.findViewById(R.id.restaurant_address_tab1);
        mDate = view.findViewById(R.id.date_tab1);
        mTime = view.findViewById(R.id.time_tab1);

        getEventFromRoomDatabase();

        return view;
    }

    private void getEventFromRoomDatabase(){
        new getEventFromRoomDatabase(getActivity()).execute();
    }

    private class getEventFromRoomDatabase extends AsyncTask<Void, Void, List<Event>> {
        private EventDatabase eventDatabase;

        private getEventFromRoomDatabase(Context mContext){
            eventDatabase = EventDatabase.getDatabase(mContext);
        }

        @Override
        protected List<Event> doInBackground(Void... voids) {
            List<Event> list = eventDatabase.eventDao().getAll();
            return list;
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            setUpComingEvent(events);
        }
    }

    private void setUpComingEvent(List<Event> list){
        if(!list.isEmpty()){
            for(Event e: list){
                if(!checkExpiredEvent(e))
                    list.remove(e);
            }

            Event event = getClosestEvent(list);
            if (event != null) {
                mUsername.setText(event.getUsername());
                mRestName.setText(event.getRestaurant_name());
                mAddress.setText(event.getLocation());
                mDate.setText(event.getDate());
                mTime.setText(event.getTime());
                Log.d(TAG, "setUpComingEvent: Set");

                initRecyclerView(list);
            } else {
                Log.e(TAG, "setUpComingEvent: Event from room is null");
            }
        }else{
            mUsername.setText("You have not select any events");
            mRestName.setText("");
            mAddress.setText("");
            mDate.setText("");
            mTime.setText("");
        }
    }

    private void initRecyclerView(List<Event> list){
        MyEventRecyclerViewAdapter mAdapter =
                new MyEventRecyclerViewAdapter(getActivity(), list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private Event getClosestEvent(List<Event> events){
        Event closest_event = events.get(0);
        int closest_time = 0, closest_date = 0;

        for (Event event : events) {
            String[] sdate = event.getDate().split("/");
            String[] stime = event.getTime().split(":");
            int event_concat_date = Integer.parseInt(sdate[0] + sdate[1] + sdate[2]);
            int event_concat_time = Integer.parseInt(stime[0] + stime[1]);

            if (closest_time == 0 && closest_date == 0) {
                closest_time = event_concat_time;
                closest_date = event_concat_date;
                closest_event = event;
            } else {
                if(event_concat_date <= closest_date){
                    if(event_concat_time <= closest_time){
                        closest_event = event;
                    }
                }
            }
        }

        return closest_event;
    }

    private static boolean checkExpiredEvent(Event event){
        String[] sdate = event.getDate().split("/");
        String[] stime = event.getTime().split(":");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int event_concat_date = Integer.parseInt(sdate[0]+sdate[1]+sdate[2]);
        int event_concat_time = Integer.parseInt(stime[0]+stime[1]);
        int current_concat_date = Integer.parseInt(""+year+month+day);
        int current_concat_time = Integer.parseInt(""+hour+minute);

        if(event_concat_date > current_concat_date){
            //Log.d(TAG, "checkExpiredEvent: Event date > current date");
            return true;
        }else if(event_concat_date == current_concat_date){
            //Log.d(TAG, "checkExpiredEvent: Event date == current date");
            //Log.d(TAG, "checkExpiredEvent: " + event_concat_time + " " + current_concat_time);
            if(event_concat_time >= current_concat_time){
                //Log.d(TAG, "checkExpiredEvent: Event time >= current time");
                return true;
            }else{
                //Log.d(TAG, "checkExpiredEvent: Event time < current time");
                return false;
            }
        }else {
            //Log.d(TAG, "checkExpiredEvent: Event date < current date");
            return false;
        }
    }
}
