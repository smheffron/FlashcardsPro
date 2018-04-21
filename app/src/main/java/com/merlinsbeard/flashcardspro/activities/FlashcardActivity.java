package com.merlinsbeard.flashcardspro.activities;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.merlinsbeard.flashcardspro.R;
import com.merlinsbeard.flashcardspro.adapters.FlashcardRecyclerAdapter;
import com.merlinsbeard.flashcardspro.animators.RecyclerScrollAnimator;
import com.merlinsbeard.flashcardspro.model.Flashcard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlashcardActivity extends AppCompatActivity {

    //data-set of flashcards belonging to set whose id is passed from parent activity
    private ArrayList<Flashcard> mDataset;
    private RecyclerView recyclerView;
    private FlashcardRecyclerAdapter recyclerAdapter;
    private TextView emptyView;
    private Context context;
    //"plus" button in bottom right to add flashcards to the set
    private FloatingActionButton addFlashcardButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FlashcardActivity flashcardActivity;
    //set id and name for the flashcards in set passed in from parent activity
    private int setId;
    private String setName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //transition into activity animation
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            Fade fade = new Fade();
            fade.setDuration(300);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }

        setContentView(R.layout.activity_flash_card);
        if(getSupportActionBar() != null){
            //add system back button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //id and name of set to which flashcards belong passed in from FlashcardSetActivity through intents
        setId = getIntent().getIntExtra("setId",-1);
        setName = getIntent().getStringExtra("setName");
        setTitle(setName);

        addFlashcardButton = findViewById(R.id.addSetButtonForflashcards);

        //refresh animation on swipe down
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutForFlashcards);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDataset.clear();
                //update recyclerView
                initializeFlashcardList();
            }
        });

        recyclerView = findViewById(R.id.recyclerViewForFlashcards);

        //hide the floating action button (plus button) hide when scrolling down
        recyclerView.addOnScrollListener(new RecyclerScrollAnimator() {
            @Override
            public void hide() {
                //hide plus button when not at top of list
                addFlashcardButton.animate().translationY(addFlashcardButton.getHeight() + 50).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void show() {
                //show plus button when at top of list
                addFlashcardButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        });

        flashcardActivity = this;

        context = getApplicationContext();

        mDataset = new ArrayList<>();

        emptyView = findViewById(R.id.emptyViewForFlashcards);
        emptyView.setVisibility(View.INVISIBLE);

        //init recyclerView from database
        initializeFlashcardList();
    }

    public void handleDeleteClick(final int position) {
        //deletes card from database and recyclerView
        //position of flashcard clicked on is passed in from FlashcardRecyclerAdapter

        //remove this flashcard from database and dataset
        int idToRemove = mDataset.get(position).getFlashcardId();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteFlashcard.php?cardId=" + idToRemove;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){
                        //update dataset with removed card

                        mDataset.remove(mDataset.get(position));

                        //notify recyclerView
                        recyclerView.removeViewAt(position);
                        recyclerAdapter.notifyItemRemoved(position);
                        recyclerAdapter.notifyItemRangeChanged(position, mDataset.size());

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

    public void handleRenameClick(final int position ) {
        //update database with renamed card (front or back)
        //position of card to rename is passed in from FlashcardRecyclerAdapter

        //rename this card (front of back)
        final int idToRename = mDataset.get(position).getFlashcardId();

        //popup for getting updated front or back of flashcard
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_card_popup);
        dialog.setTitle("Edit card");
        dialog.show();

        //get front text from popup
        final TextView frontTextView = dialog.findViewById(R.id.newFrontText);
        frontTextView.setHint(mDataset.get(position).getFrontText());

        //get back text from popup
        final TextView backTextView = dialog.findViewById(R.id.newBackText);
        backTextView.setHint(mDataset.get(position).getBackText());

        //popup canceled
        dialog.findViewById(R.id.dialogButtonCancelForFlashcardEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.dialogButtonOKForFlashcardEdit).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //update database with new front and back for specified flashcard

                final String newFrontText = String.valueOf(frontTextView.getText());
                final String newBackText = String.valueOf(backTextView.getText());

                if (newBackText.isEmpty() || newFrontText.isEmpty()) {
                    Toast.makeText(context, "Give your flashcard a front and back", Toast.LENGTH_LONG).show();
                } else {

                    RequestQueue queue = Volley.newRequestQueue(flashcardActivity);
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

                                    //update recyclerView with new card
                                    mDataset.get(position).setBackText(newBackText);
                                    mDataset.get(position).setFrontText(newFrontText);

                                    //make sure adapter is not null if starting from empty flashcard set
                                    if (recyclerAdapter == null) {
                                        recyclerAdapter = new FlashcardRecyclerAdapter(mDataset, context, flashcardActivity) {
                                        };
                                        recyclerView.setAdapter(recyclerAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                                    }
                                    //notify recyclerView of changed card
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
        //get flashcard from the database and populate the recyclerView and data-model

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/getFlashcards.php?setId=";

        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        url += setId;

        Log.d("URL:", url);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //get the flashcards for the specified ID from the database
                    if(response.getString("status").equals("succeeded")){
                        JSONArray sets = response.getJSONArray("cards");

                        //load model from JSON array passed back from database

                        for(int i = 0; i < sets.length(); i++){
                            JSONObject set = sets.getJSONObject(i);
                            Flashcard flashcard = new Flashcard(set.getString("frontText"),set.getString("backText"), set.getInt("cardId"));
                            mDataset.add(flashcard);
                        }

                        if(mDataset == null || mDataset.isEmpty()){
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        if(recyclerAdapter == null){
                            recyclerAdapter = new FlashcardRecyclerAdapter(mDataset, context, flashcardActivity) {

                            };
                            recyclerView.setAdapter(recyclerAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(flashcardActivity));
                        }

                        //notify recyclerView of refreshed data model
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
        //start add card logic to database and data model


        //popup to get new card front and back
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_card_popup);
        dialog.setTitle("Create a new card");
        dialog.show();

        //popup canceled
        dialog.findViewById(R.id.dialogButtonCancelForFlashcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        //popup ok
        dialog.findViewById(R.id.dialogButtonOKForFlashcard).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //get new card front and back
                TextView frontText = dialog.findViewById(R.id.frontText);
                TextView backText  = dialog.findViewById(R.id.backText);
                final String frontString = String.valueOf(frontText.getText());
                final String backString = String.valueOf(backText.getText());


                if (frontString.isEmpty() || backString.isEmpty()) {
                    Toast.makeText(context, "Give your flashcard a front and back", Toast.LENGTH_LONG).show();
                } else {

                    //add new card to the database
                    RequestQueue queue = Volley.newRequestQueue(flashcardActivity);
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
                                    Flashcard flashcard = new Flashcard(frontString,backString, newCardId );

                                    //update data-model
                                    mDataset.add(flashcard);

                                    if (recyclerAdapter == null) {
                                        recyclerAdapter = new FlashcardRecyclerAdapter(mDataset, context, flashcardActivity) {
                                        };
                                        recyclerView.setAdapter(recyclerAdapter);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(context));

                                    }
                                    //notify recyclerView of changed data-model
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
        //handle click on flashcard

        if(position >= mDataset.size() || position < 0){
            //invalid index
        }
        else {
            //go to the study view for the specified card
            Intent intent = new Intent(this, StudyActivity.class);
            intent.putParcelableArrayListExtra("data", mDataset);
            intent.putExtra("position", position);
            intent.putExtra("setName", setName);
            intent.putExtra("setId", setId);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } else {
                startActivity(intent);
            }

            // Wait for animations to finish then destroy activity
            new CountDownTimer(500, 500) {

                @Override
                public void onTick(long l) {
                    // do nothing
                }

                @Override
                public void onFinish() {
                    finish();
                }
            }.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.account_button, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //go to account activity if user graphic is clicked on the activity bar

        int id = item.getItemId();

        if(id == R.id.accountButton){
            Intent intent = new Intent(this, AccountActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.homeAsUp){
            //built in system back button
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
