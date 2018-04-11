package com.merlinsbeard.flashcardspro;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class SetViewActivity extends AppCompatActivity {
    private User user = new User();
    private ArrayList<String> mDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_view);

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        user.setUsername(preferences.getString("username", ""));
        user.setUserId(preferences.getInt("userId", -1));

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
