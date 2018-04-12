package com.merlinsbeard.flashcardspro;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SetViewActivity extends AppCompatActivity {
    private ArrayList<String> mDataset;
    private RecyclerView recyclerView;
    private RecyclerAdapter recyclerAdapter;
    private TextView emptyView;
    private Context context;

    private SetViewActivity setViewActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_view);

        setViewActivity = this;

        context = getApplicationContext();

        mDataset = new ArrayList<>();

        emptyView = findViewById(R.id.emptyView);
        emptyView.setVisibility(View.INVISIBLE);

        if(mDataset == null){
            emptyView.setVisibility(View.VISIBLE);
        }
        else if(recyclerAdapter == null){
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerAdapter = new RecyclerAdapter(mDataset, getApplicationContext(), this) {

            };
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

    }


    public void handleDeleteClick(Integer i){


        // add DB deletion logic here

        mDataset.remove(mDataset.get(i));

        recyclerView.removeViewAt(i);
        recyclerAdapter.notifyItemRemoved(i);
        recyclerAdapter.notifyItemRangeChanged(i, mDataset.size());

        Log.d("The values left are: ", mDataset.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_set_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.plusButton) {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.add_set_popup);
            dialog.setTitle("Create a new set");
            dialog.show();

            dialog.findViewById(R.id.dialogButtonCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.dialogButtonOK).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    TextView setName = (TextView) dialog.findViewById(R.id.setNameFromPopup);
                    String name = String.valueOf(setName.getText());

                    if (name.isEmpty()) {
                        Toast.makeText(context, "Give your set a name", Toast.LENGTH_LONG).show();
                    } else {

                        //add DB insertion logic here

                        mDataset.add(name);

                        if (recyclerAdapter == null) {

                            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                            recyclerAdapter = new RecyclerAdapter(mDataset, context, setViewActivity) {
                            };
                            recyclerView.setAdapter(recyclerAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));

                        }
                        recyclerAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
