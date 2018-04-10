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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SetViewActivity extends AppCompatActivity {
    public ArrayList<String> mDataset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_view);

        mDataset = new ArrayList<>();
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");
        mDataset.add("pie");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerAdapter radapter = new RecyclerAdapter(mDataset, this){

        };
        recyclerView.setAdapter(radapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    public void onLongClickofCard(Integer position){
        Log.d("sdjlkfnlkasjdf", position.toString());
    }
}
