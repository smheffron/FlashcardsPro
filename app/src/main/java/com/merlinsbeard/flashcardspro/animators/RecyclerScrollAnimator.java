package com.merlinsbeard.flashcardspro.animators;

import android.support.v7.widget.RecyclerView;

// This class is used to show or hide UI elements in a RecyclerView when the user scrolls up or down
public abstract class RecyclerScrollAnimator extends RecyclerView.OnScrollListener {
    private int scrollDistance = 0;
    private boolean visible = true;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
        super.onScrolled(recyclerView, dx, dy);

        if(visible && scrollDistance > 25){
            hide();
            scrollDistance = 0;
            visible = false;
        }
        else if(!visible && scrollDistance < -25){
            show();
            scrollDistance = 0;
            visible = true;
        }

        if(visible && dy > 0 || !visible && dy < 0){
            scrollDistance += dy;
        }
    }

    protected abstract void hide();

    protected abstract void show();
}
