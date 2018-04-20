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
import java.util.List;

import cyruslee487.donteatalone.EventRoomDatabase.Event;
import cyruslee487.donteatalone.EventRoomDatabase.EventDatabase;
import cyruslee487.donteatalone.R;
import cyruslee487.donteatalone.RecyclerViewAdapter.MyEventRecyclerViewAdapter;

public class tab1Fragment extends Fragment {
    private static final String TAG = "DB";

    private List<Event> mEventFromRoomDatabase = new ArrayList<>();

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

    private class getEventFromRoomDatabase extends AsyncTask<Void, Void, Void> {
        private EventDatabase eventDatabase;

        private getEventFromRoomDatabase(Context mContext){
            eventDatabase = EventDatabase.getDatabase(mContext);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mEventFromRoomDatabase = eventDatabase.eventDao().getAll();
            setUpComingEvent();
            return null;
        }
    }

    private void setUpComingEvent(){
        if(!mEventFromRoomDatabase.isEmpty()){
            Event event = mEventFromRoomDatabase.get(0);
            if (event != null) {
                mUsername.setText(event.getUsername());
                mRestName.setText(event.getRestaurant_name());
                mAddress.setText(event.getLocation());
                mDate.setText(event.getDate());
                mTime.setText(event.getTime());
                Log.d(TAG, "setUpComingEvent: Set");

                initRecyclerView();
            } else {
                Log.e(TAG, "setUpComingEvent: Event from room is null");
            }
        }else{
            mUsername.setText("NO");
            mRestName.setText("EVENT");
            mAddress.setText("FOR");
            mDate.setText("YOU");
            mTime.setText("");
        }
    }

    private void initRecyclerView(){
        MyEventRecyclerViewAdapter mAdapter =
                new MyEventRecyclerViewAdapter(getActivity(), mEventFromRoomDatabase);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
