package cyruslee487.donteatalone.Fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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

    private List<Discount> mDiscountFromRoomDatabase = new ArrayList<>();

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

    private class getDiscountFromRoomDatabaseAsync extends AsyncTask<Void, Void, Void>{
        private DiscountDatabase discountDatabase;

        private getDiscountFromRoomDatabaseAsync(Context context){
            discountDatabase = DiscountDatabase.getDatabase(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDiscountFromRoomDatabase = discountDatabase.discountDao().getAll();
            initRecyclerView();
            return null;
        }
    }

    private void initRecyclerView(){
        MyDiscountRecyclerViewAdapter mAdapter = new MyDiscountRecyclerViewAdapter(mDiscountFromRoomDatabase, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
