package com.merlinsbeard.flashcardspro;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_card_view);

        setId = getIntent().getIntExtra("setId",-1);

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

    }

    public void handleRenameClick(final int i ) {
        final int idToRename = mDataset.get(i).getFlashcardId();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.edit_card_popup);
        dialog.setTitle("Edit card");
        dialog.show();

        final TextView frontTextView = (TextView) dialog.findViewById(R.id.newFrontText);
        frontTextView.setHint(mDataset.get(i).getFrontText());

        final TextView backTextView = (TextView) dialog.findViewById(R.id.newBackText);
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

                TextView frontText = (TextView) dialog.findViewById(R.id.frontText);
                TextView backText  = (TextView) dialog.findViewById(R.id.backText);
                final String frontString = String.valueOf(frontText.getText());
                final String backString = String.valueOf(backText.getText());


                if (frontString.isEmpty() || backString.isEmpty()) {
                    Toast.makeText(context, "Give your flashcard a front and back", Toast.LENGTH_LONG).show();
                } else {

                    RequestQueue queue = Volley.newRequestQueue(flashCardView);
                    String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/create_notecard.php?setId=";

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
}
