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
    //holds the flashcard sets, used to populate recyclerView
    private ArrayList<FlashcardSet> mDataset;
    private RecyclerView recyclerView;
    //adapter used to handle creation/interaction with recyclerView
    private FlashcardSetRecyclerAdapter flashcardSetRecyclerAdapter;
    private TextView emptyView;
    private Context context;
    //"plus" button in bottom right of flashcard set activity
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

        //set up swipe down to refresh
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


    public void handleDeleteClick(final Integer position){
        //calls web service and deletes the set in the database, refreshes recyclerView
        //gets position clicked from FlashcardSetRecyclerAdapter


        //remove this set
        int idToRemove = mDataset.get(position).getSetId();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteCardSet.php?id=" + idToRemove;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){
                        //update local data-set and notify recyclerView
                        mDataset.remove(mDataset.get(position));
                        recyclerView.removeViewAt(position);
                        flashcardSetRecyclerAdapter.notifyItemRemoved(position);
                        flashcardSetRecyclerAdapter.notifyItemRangeChanged(position, mDataset.size());

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

    public void handleRenameClick(final Integer position){
        //calls web service and renames the set in the database, refreshes recyclerView
        //gets position from FlashcardSetRecyclerAdapter

        //rename this set
        final int idToRename = mDataset.get(position).getSetId();

        //display popup presenting user with rename options
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_set_popup);
        dialog.setTitle("Rename set");
        dialog.show();

        final TextView setName = dialog.findViewById(R.id.newSetNameFromPopup);
        setName.setHint(mDataset.get(position).getName());


        //popup canceled
        dialog.findViewById(R.id.dialogButtonCancelFromRename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //popup ok
        dialog.findViewById(R.id.dialogButtonOKFromRename).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //new set name from popup
                final String name = String.valueOf(setName.getText());

                //ensure valid name for set
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
                                    //update local data-set with new set name

                                    mDataset.get(position).setName(name);

                                    //ensure recyclerView loaded properly if it was empty when starting activity
                                    if (flashcardSetRecyclerAdapter == null) {
                                        flashcardSetRecyclerAdapter = new FlashcardSetRecyclerAdapter(mDataset, context, flashcardSetActivity) {
                                        };
                                        recyclerView.setAdapter(flashcardSetRecyclerAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                                    }

                                    //notify recyclerView of renamed set
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
        //inflates menu when 3 vertical dots menu clicked
        getMenuInflater().inflate(R.menu.account_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handles clicks on user account graphic in action bar

        int id = item.getItemId();

        if(id == R.id.accountButton){
            //go to AccountActivity
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickPlusButton(View view){
        //add a set to the database

        //display popup for new set name
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_set_popup);
        dialog.setTitle("Create a new set");
        dialog.show();

        //popup canceled
        dialog.findViewById(R.id.dialogButtonCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //popup ok
        dialog.findViewById(R.id.dialogButtonOK).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //new set name
                TextView setName = dialog.findViewById(R.id.setNameFromPopup);
                final String name = String.valueOf(setName.getText());

                //check new set validity
                if (name.isEmpty()) {
                    Toast.makeText(context, "Give your set a name", Toast.LENGTH_LONG).show();
                } else {
                    //update database with new set
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
                                    //update data-set with new set
                                    int newSetId = response.getInt("newSetId");
                                    FlashcardSet flashcardSet = new FlashcardSet(newSetId, name);

                                    mDataset.add(flashcardSet);

                                    //ensure recyclerView is not null if entering activity with no sets
                                    if (flashcardSetRecyclerAdapter == null) {
                                        flashcardSetRecyclerAdapter = new FlashcardSetRecyclerAdapter(mDataset, context, flashcardSetActivity) {
                                        };
                                        recyclerView.setAdapter(flashcardSetRecyclerAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                                    }
                                    //notify recyclerView of new set
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
        //populates the recyclerView with set data from dataset

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getSets.php?id=";

        //access user id from shared preferences and get set data from the database for that user
        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        url += userId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){
                        JSONArray sets = response.getJSONArray("sets");

                        //response is JSON array, populate flashcard sets and recycler view with this data

                        for(int i = 0; i < sets.length(); i++){
                            JSONObject set = sets.getJSONObject(i);
                            FlashcardSet cardSet = new FlashcardSet(set.getInt("setId"), set.getString("setName"));
                            mDataset.add(cardSet);
                        }

                        if(mDataset == null || mDataset.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                        }

                        //ensure recyclerAdapter is not null in case of no data being returned
                        if(flashcardSetRecyclerAdapter == null){
                            flashcardSetRecyclerAdapter = new FlashcardSetRecyclerAdapter(mDataset, context, flashcardSetActivity) {

                            };
                            recyclerView.setAdapter(flashcardSetRecyclerAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(flashcardSetActivity));
                        }

                        //notify recyclerView of new set data
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

    public void handleItemClick(Integer position) {
        //handle click on a set in the recyclerView

        //position of set clicked on is passed from FlashcardSetRecyclerAdapter


        if(position>=mDataset.size() || position <0){
            //not valid
        }
        else {
            //go to study view for this flashcard set
            Intent intent = new Intent(this, FlashcardActivity.class);
            intent.putExtra("setId", mDataset.get(position).getSetId());
            intent.putExtra("setName", mDataset.get(position).getName());
            startActivity(intent);
        }


    }
}
