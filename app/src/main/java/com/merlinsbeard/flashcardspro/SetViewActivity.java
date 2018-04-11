package com.merlinsbeard.flashcardspro;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;

public class SetViewActivity extends AppCompatActivity {
    private ArrayList<String> mDataset;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private TextView emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_view);

        emptyView = findViewById(R.id.emptyView);
        emptyView.setVisibility(View.INVISIBLE);

        if(mDataset == null){
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerAdapter = new RecyclerAdapter(mDataset, getApplicationContext(), this) {

            };
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }


    public void handleDeleteClick(Integer i){

        mDataset.remove(mDataset.get(i));

        recyclerView.removeViewAt(i);
        recyclerAdapter.notifyItemRemoved(i);
        recyclerAdapter.notifyItemRangeChanged(i, mDataset.size());

        Log.d("The values left are: ", mDataset.toString());

    }

}
