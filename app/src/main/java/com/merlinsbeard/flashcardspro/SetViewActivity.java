package com.merlinsbeard.flashcardspro;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SetViewActivity extends AppCompatActivity {
    private ArrayList<FlashcardSet> mDataset;
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

        if(mDataset == null || mDataset.isEmpty()){
            emptyView.setVisibility(View.VISIBLE);
        }
        else if(recyclerAdapter == null){
            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerAdapter = new RecyclerAdapter(mDataset, getApplicationContext(), this) {

            };
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/verifyLogin.php?id=";

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        url += userId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LOGIN ERROR", error.toString());
            }
        });

        queue.add(request);
    }


    public void handleDeleteClick(Integer i){


        // add DB deletion logic here

        mDataset.remove(mDataset.get(i));

        recyclerView.removeViewAt(i);
        recyclerAdapter.notifyItemRemoved(i);
        recyclerAdapter.notifyItemRangeChanged(i, mDataset.size());

        Log.d("The values left are: ", mDataset.toString());

        if(mDataset.isEmpty()){
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_set_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

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

                        SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

                        int userId = preferences.getInt("userId", -1);

                        FlashcardSet flashcardSet = new FlashcardSet(userId,name);
                        
                        //add DB insertion logic here

                        mDataset.add(flashcardSet);

                        if (recyclerAdapter == null) {

                            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                            recyclerAdapter = new RecyclerAdapter(mDataset, context, setViewActivity) {
                            };
                            recyclerView.setAdapter(recyclerAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));

                        }
                        recyclerAdapter.notifyDataSetChanged();
                        emptyView.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
