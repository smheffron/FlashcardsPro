package com.merlinsbeard.flashcardspro.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.merlinsbeard.flashcardspro.R;
import com.merlinsbeard.flashcardspro.model.Flashcard;

import java.util.ArrayList;

public class StudyActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private ArrayList<Flashcard> mDataSet;
    private Integer positionClicked;
    private TextView cardText;
    private String setName;
    private int setId;

    private boolean isFront = true;
    private GestureDetectorCompat detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If device supports enter and exit transitions, enable them and set them up
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            Slide slide = new Slide(Gravity.BOTTOM);
            slide.setDuration(150);

            getWindow().setEnterTransition(slide);
            getWindow().setExitTransition(slide);
        }

        setContentView(R.layout.activity_study);

        // Allow up navigation
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get flashcard data from previous activity
        mDataSet = getIntent().getParcelableArrayListExtra("data");
        positionClicked = getIntent().getIntExtra("position", -1);

        // Set card text to display front text of card that was selected
        cardText = findViewById(R.id.flashcardText);
        cardText.setText(mDataSet.get(positionClicked).getFrontText());

        // Make card text change from front to back or back to front when the view is long pressed
        ConstraintLayout constraintLayout = findViewById(R.id.individualCardView);
        constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(isFront){
                    cardText.setText(mDataSet.get(positionClicked).getBackText());
                    isFront = false;
                }
                else{
                    cardText.setText(mDataSet.get(positionClicked).getFrontText());
                    isFront = true;
                }
                return true;
            }
        });

        // Set title in action bar to the name of the set
        setName = String.valueOf(getIntent().getCharSequenceExtra("setName"));
        setTitle(setName);

        setId = getIntent().getIntExtra("setId", -1);

        detector = new GestureDetectorCompat(this,this);
    }

    // Using custom gesture detection in onFling(), prevent the default implementations of these from interfering
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
        // Do nothing
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        // Do nothing
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        detector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {

        // Left to right swipe, move to previous card in data set if it exists, display front text
        if(motionEvent.getX() - motionEvent1.getX()<0 && Math.abs(motionEvent.getX() - motionEvent1.getX()) > 150){
            if(positionClicked - 1 >= 0){
                cardText.setText(mDataSet.get(--positionClicked).getFrontText());
                isFront=true;
            }
        }

        // Right to left swipe, move to next card in data set if it exists, display front text
        if(motionEvent.getX() - motionEvent1.getX()>0 && Math.abs(motionEvent.getX() - motionEvent1.getX()) > 150){
            //right swipe
            if(positionClicked + 1 < mDataSet.size()){
                cardText.setText(mDataSet.get(++positionClicked).getFrontText());
                isFront=true;
            }
        }

        // Down swipe, navigate back to FlashcardActivity
        if(motionEvent.getY() - motionEvent1.getY() < 0 && Math.abs(motionEvent.getY() - motionEvent1.getY()) > 150 && Math.abs(motionEvent.getX() - motionEvent1.getX()) < 150){
            navigateUpToFlashcardActivity();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            navigateUpToFlashcardActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigateUpToFlashcardActivity(){
        Intent intent = NavUtils.getParentActivityIntent(this);
        if(intent != null) {
            intent.putExtra("setId", setId);
            intent.putExtra("setName", setName);

            // Navigate back to FlashcardActivity, use transitions if supported
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            }
            else {
                startActivity(intent);
            }
        }
    }
}

