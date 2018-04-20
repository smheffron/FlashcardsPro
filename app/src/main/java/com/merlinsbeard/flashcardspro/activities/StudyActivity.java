package com.merlinsbeard.flashcardspro.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.merlinsbeard.flashcardspro.model.Flashcard;
import com.merlinsbeard.flashcardspro.R;

import java.util.ArrayList;

public class StudyActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private ArrayList<Flashcard> mDataSet;
    private Integer positionClicked;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private int numberElements;
    TextView cardText;
    ConstraintLayout constraintLayout;
    String setName;
    int setId;

    boolean isFront = true;
    private GestureDetectorCompat detector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);

            Slide slide = new Slide(Gravity.BOTTOM);
            slide.setDuration(200);

            getWindow().setEnterTransition(slide);
            getWindow().setExitTransition(slide);
        }

        setContentView(R.layout.activity_study);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDataSet = getIntent().getParcelableArrayListExtra("data");
        positionClicked = getIntent().getIntExtra("position", -1);

        cardText = findViewById(R.id.flashcardText);
        constraintLayout = findViewById(R.id.individualCardView);

        setName = String.valueOf(getIntent().getCharSequenceExtra("setName"));
        setTitle(setName);

        setId = getIntent().getIntExtra("setId", -1);

        Log.d("SET ID:", String.valueOf(setId));

        cardText.setText(mDataSet.get(positionClicked).getFrontText());

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

        detector = new GestureDetectorCompat(this,this);

    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

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

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {


        if(motionEvent.getX() - motionEvent1.getX()<0 && Math.abs(motionEvent.getX() - motionEvent1.getX())>150){
            if(positionClicked - 1 >= 0){
                cardText.setText(mDataSet.get(--positionClicked).getFrontText());
                isFront=true;
            }
        }


        if(motionEvent.getX() - motionEvent1.getX()>0 && Math.abs(motionEvent.getX() - motionEvent1.getX())>150){
            //right swipe
            if(positionClicked + 1 < mDataSet.size()){
                cardText.setText(mDataSet.get(++positionClicked).getFrontText());
                isFront=true;
            }
        }

        if(motionEvent.getY() - motionEvent1.getY() < 0 && Math.abs(motionEvent.getY() - motionEvent1.getY())>150){
            Intent intent = NavUtils.getParentActivityIntent(this);
            if(intent != null) {
                intent.putExtra("setId", setId);
                intent.putExtra("setName", setName);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        }


        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            Log.d("PIZZA", "correct id");
            Intent intent = NavUtils.getParentActivityIntent(this);
            if(intent != null) {
                intent.putExtra("setId", setId);
                intent.putExtra("setName", setName);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }
}

