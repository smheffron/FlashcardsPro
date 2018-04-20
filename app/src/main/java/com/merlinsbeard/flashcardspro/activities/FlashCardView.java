package com.merlinsbeard.flashcardspro.activities;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.merlinsbeard.flashcardspro.model.FlashCard;
import com.merlinsbeard.flashcardspro.R;
import com.merlinsbeard.flashcardspro.adapters.RecyclerAdapterForFlashcards;
import com.merlinsbeard.flashcardspro.animators.RecyclerScrollAnimator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlashCardView extends AppCompatActivity {

    private ArrayList<FlashCard> mDataset;
    private RecyclerView recyclerView;
    private RecyclerAdapterForFlashcards recyclerAdapter;
    private TextView emptyView;
    private Context context;
    private FloatingActionButton addSetButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FlashCardView flashCardView;
    private int setId;
    private String setName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            Fade fade = new Fade();
            fade.setDuration(300);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }

        setContentView(R.layout.activity_flash_card_view);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setId = getIntent().getIntExtra("setId",-1);
        setName = getIntent().getStringExtra("setName");
        setTitle(setName);

        Log.d("SET ID: ", String.valueOf(setId));

        addSetButton = findViewById(R.id.addSetButtonForflashcards);
        Animation growAnimation = AnimationUtils.loadAnimation(this, R.anim.grow);
        addSetButton.startAnimation(growAnimation);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutForFlashcards);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataset.clear();
                initializeFlashcardList();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewForFlashcards);
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

        flashCardView = this;

        context = getApplicationContext();

        mDataset = new ArrayList<>();

        emptyView = findViewById(R.id.emptyViewForFlashcards);
        emptyView.setVisibility(View.INVISIBLE);

        initializeFlashcardList();
    }

    public void handleDeleteClick(final int i) {

        int idToRemove = mDataset.get(i).getFlashcardId();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteFlashcard.php?cardId=" + idToRemove;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){
                        mDataset.remove(mDataset.get(i));

                        recyclerView.removeViewAt(i);
                        recyclerAdapter.notifyItemRemoved(i);
                        recyclerAdapter.notifyItemRangeChanged(i, mDataset.size());

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

    public void handleRenameClick(final int i ) {
        final int idToRename = mDataset.get(i).getFlashcardId();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_card_popup);
        dialog.setTitle("Edit card");
        dialog.show();

        final TextView frontTextView = dialog.findViewById(R.id.newFrontText);
        frontTextView.setHint(mDataset.get(i).getFrontText());

        final TextView backTextView = dialog.findViewById(R.id.newBackText);
        backTextView.setHint(mDataset.get(i).getBackText());


        dialog.findViewById(R.id.dialogButtonCancelForFlashcardEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.dialogButtonOKForFlashcardEdit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final String newFrontText = String.valueOf(frontTextView.getText());
                final String newBackText = String.valueOf(backTextView.getText());

                if (newBackText.isEmpty() || newFrontText.isEmpty()) {
                    Toast.makeText(context, "Give your flashcard a front and back", Toast.LENGTH_LONG).show();
                } else {

                    RequestQueue queue = Volley.newRequestQueue(flashCardView);
                    String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updateFlashcard.php?cardId=";

                    url+=idToRename;

                    Map<String,String> params = new HashMap<>();
                    params.put("newFront", newFrontText);
                    params.put("newBack", newBackText);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("status").equals("succeeded")){

                                    mDataset.get(i).setBackText(newBackText);
                                    mDataset.get(i).setFrontText(newFrontText);

                                    if (recyclerAdapter == null) {
                                        recyclerAdapter = new RecyclerAdapterForFlashcards(mDataset, context, flashCardView) {
                                        };
                                        recyclerView.setAdapter(recyclerAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                                    }
                                    recyclerAdapter.notifyDataSetChanged();
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

    private void initializeFlashcardList(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getFlashcards.php?setId=";

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        url += setId;

        Log.d("URL:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){
                        JSONArray sets = response.getJSONArray("cards");

                        for(int i = 0; i < sets.length(); i++){
                            JSONObject set = sets.getJSONObject(i);
                            FlashCard flashCard = new FlashCard(set.getString("frontText"),set.getString("backText"), set.getInt("cardId"));
                            mDataset.add(flashCard);
                        }

                        if(mDataset == null || mDataset.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        if(recyclerAdapter == null){
                            recyclerAdapter = new RecyclerAdapterForFlashcards(mDataset, context, flashCardView) {

                            };
                            recyclerView.setAdapter(recyclerAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(flashCardView));
                        }

                        recyclerAdapter.notifyDataSetChanged();
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

    public void onClickPlusButton(View view){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.flashcard_dio);
        dialog.setTitle("Create a new card");
        dialog.show();

        dialog.findViewById(R.id.dialogButtonCancelForFlashcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.dialogButtonOKForFlashcard).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                TextView frontText = dialog.findViewById(R.id.frontText);
                TextView backText  = dialog.findViewById(R.id.backText);
                final String frontString = String.valueOf(frontText.getText());
                final String backString = String.valueOf(backText.getText());


                if (frontString.isEmpty() || backString.isEmpty()) {
                    Toast.makeText(context, "Give your flashcard a front and back", Toast.LENGTH_LONG).show();
                } else {

                    RequestQueue queue = Volley.newRequestQueue(flashCardView);
                    String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/newFlashcard.php?setId=";

                    url += setId;

                    Map<String,String> params = new HashMap<>();
                    params.put("newCardFront", frontString);
                    params.put("newCardBack",backString);

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if(response.getString("status").equals("succeeded")){
                                    int newCardId = response.getInt("newCardId");
                                    FlashCard flashcard = new FlashCard(frontString,backString, newCardId );

                                    mDataset.add(flashcard);

                                    if (recyclerAdapter == null) {
                                        recyclerAdapter = new RecyclerAdapterForFlashcards(mDataset, context, flashCardView) {
                                        };
                                        recyclerView.setAdapter(recyclerAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                                    }
                                    recyclerAdapter.notifyDataSetChanged();
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

    public void handleItemClick(Integer position) {
        Intent intent = new Intent(this, ScrollView.class);
        intent.putParcelableArrayListExtra("data",mDataset);
        intent.putExtra("position",position);
        intent.putExtra("setName", setName);
        intent.putExtra("setId", setId);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
        else {
            startActivity(intent);
        }
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
            Intent intent = new Intent(this, AccountViewActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.homeAsUp){
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
