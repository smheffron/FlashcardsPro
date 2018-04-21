package com.merlinsbeard.flashcardspro.animators;

import android.support.v7.widget.RecyclerView;


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

    public abstract void hide();

    public abstract void show();
}

