package com.merlinsbeard.flashcardspro.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.merlinsbeard.flashcardspro.model.FlashcardSet;
import com.merlinsbeard.flashcardspro.R;
import com.merlinsbeard.flashcardspro.adapters.FlashcardSetRecyclerAdapter;
import com.merlinsbeard.flashcardspro.animators.RecyclerScrollAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlashcardSetActivity extends AppCompatActivity {
    private ArrayList<FlashcardSet> mDataset;
    private RecyclerView recyclerView;
    private FlashcardSetRecyclerAdapter flashcardSetRecyclerAdapter;
    private TextView emptyView;
    private Context context;
    private FloatingActionButton addSetButton;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FlashcardSetActivity flashcardSetActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_set);

        addSetButton = findViewById(R.id.addSetButton);
        Animation growAnimation = AnimationUtils.loadAnimation(this, R.anim.grow);
        addSetButton.startAnimation(growAnimation);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataset.clear();
                initializeSetList();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addOnScrollListener(new RecyclerScrollAnimator() {
            @Override
            public void hide() {
                addSetButton.animate().translationY(addSetButton.getHeight() + 50).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void show() {
                addSetButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });

        flashcardSetActivity = this;

        context = getApplicationContext();

        mDataset = new ArrayList<>();

        emptyView = findViewById(R.id.emptyView);
        emptyView.setVisibility(View.INVISIBLE);


        initializeSetList();
    }


    public void handleDeleteClick(final Integer i){
        int idToRemove = mDataset.get(i).getSetId();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteCardSet.php?id=" + idToRemove;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){
                        mDataset.remove(mDataset.get(i));

                        recyclerView.removeViewAt(i);
                        flashcardSetRecyclerAdapter.notifyItemRemoved(i);
                        flashcardSetRecyclerAdapter.notifyItemRangeChanged(i, mDataset.size());

                        Log.d("The values left are: ", mDataset.toString());

                        if(mDataset.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                        }
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

    public void handleRenameClick(final Integer i){
        final int idToRename = mDataset.get(i).getSetId();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_set_popup);
        dialog.setTitle("Rename set");
        dialog.show();

        final TextView setName = dialog.findViewById(R.id.newSetNameFromPopup);
        setName.setHint(mDataset.get(i).getName());


        dialog.findViewById(R.id.dialogButtonCancelFromRename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.dialogButtonOKFromRename).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String name = String.valueOf(setName.getText());

                if (name.isEmpty()) {
                    Toast.makeText(context, "Give your set a name", Toast.LENGTH_LONG).show();
                } else {

                    RequestQueue queue = Volley.newRequestQueue(flashcardSetActivity);
                    String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updateSetName.php?id=";

                    url+=idToRename;

                    Map<String,String> params = new HashMap<>();
                    params.put("title", name);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("status").equals("succeeded")){

                                    mDataset.get(i).setName(name);

                                    if (flashcardSetRecyclerAdapter == null) {
                                        flashcardSetRecyclerAdapter = new FlashcardSetRecyclerAdapter(mDataset, context, flashcardSetActivity) {
                                        };
                                        recyclerView.setAdapter(flashcardSetRecyclerAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                                    }
                                    flashcardSetRecyclerAdapter.notifyDataSetChanged();
                                    emptyView.setVisibility(View.INVISIBLE);
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

                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.accountButton){
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickPlusButton(View view){
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

                TextView setName = dialog.findViewById(R.id.setNameFromPopup);
                final String name = String.valueOf(setName.getText());

                if (name.isEmpty()) {
                    Toast.makeText(context, "Give your set a name", Toast.LENGTH_LONG).show();
                } else {

                    RequestQueue queue = Volley.newRequestQueue(flashcardSetActivity);
                    String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/newCardSet.php?id=";

                    final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                    int userId = preferences.getInt("userId", -1);
                    url += userId;

                    Map<String,String> params = new HashMap<>();
                    params.put("title", name);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("status").equals("succeeded")){
                                    int newSetId = response.getInt("newSetId");
                                    FlashcardSet flashcardSet = new FlashcardSet(newSetId, name);

                                    mDataset.add(flashcardSet);

                                    if (flashcardSetRecyclerAdapter == null) {
                                        flashcardSetRecyclerAdapter = new FlashcardSetRecyclerAdapter(mDataset, context, flashcardSetActivity) {
                                        };
                                        recyclerView.setAdapter(flashcardSetRecyclerAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                                    }
                                    flashcardSetRecyclerAdapter.notifyDataSetChanged();
                                    emptyView.setVisibility(View.INVISIBLE);
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

                    dialog.dismiss();
                }
            }
        });
    }


    private void initializeSetList(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getSets.php?id=";

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        url += userId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){
                        JSONArray sets = response.getJSONArray("sets");

                        for(int i = 0; i < sets.length(); i++){
                            JSONObject set = sets.getJSONObject(i);
                            FlashcardSet cardSet = new FlashcardSet(set.getInt("setId"), set.getString("setName"));
                            mDataset.add(cardSet);
                        }

                        if(mDataset == null || mDataset.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        if(flashcardSetRecyclerAdapter == null){
                            flashcardSetRecyclerAdapter = new FlashcardSetRecyclerAdapter(mDataset, context, flashcardSetActivity) {

                            };
                            recyclerView.setAdapter(flashcardSetRecyclerAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(flashcardSetActivity));
                        }

                        flashcardSetRecyclerAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
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

    public void handleItemClick(Integer i) {

        if(i>=mDataset.size() || i <0){

        }
        else {

            Intent intent = new Intent(this, FlashcardActivity.class);
            intent.putExtra("setId", mDataset.get(i).getSetId());
            intent.putExtra("setName", mDataset.get(i).getName());
            startActivity(intent);
        }


    }
}
