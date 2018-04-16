package com.merlinsbeard.flashcardspro;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ScrollView extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private ArrayList<FlashCard> mDataSet;
    private Integer positionClicked;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private int numberElements;
    TextView cardText;
    ConstraintLayout constraintLayout;

    boolean isFront = true;
    private GestureDetectorCompat detector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view);

        mDataSet = getIntent().getParcelableArrayListExtra("data");
        positionClicked = getIntent().getIntExtra("position", -1);

        cardText = findViewById(R.id.flashcardText);
        constraintLayout = findViewById(R.id.individualCardView);





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
}

