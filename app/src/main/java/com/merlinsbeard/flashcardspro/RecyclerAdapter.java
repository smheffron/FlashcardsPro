package com.merlinsbeard.flashcardspro;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        // each data item is just a string in this case
        public CardView mCardView;

        public View view;

        public ViewHolder(View v) {
            super(v);

            mCardView = v.findViewById(R.id.cardView);
            mTextView = v.findViewById(R.id.setName);
            this.view = v;

        }
    }

    private ArrayList<String> mDataset;
    private SetViewActivity setViewActivity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(ArrayList<String> myDataset, SetViewActivity setViewActivity) {
        this.mDataset = myDataset;
        this.setViewActivity=setViewActivity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.card_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(mDataset.get(position));

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                setViewActivity.onLongClickofCard(position);

                return true;
            }

        });
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
