package com.merlinsbeard.flashcardspro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {



    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        // each data item is just a string in this case
        public CardView mCardView;

        public ImageView threeDots;

        public ViewHolder(View v) {
            super(v);

            mCardView = v.findViewById(R.id.cardView);
            mTextView = v.findViewById(R.id.setName);
            threeDots = v.findViewById(R.id.threeDots);
        }

    }

    private ArrayList<String> mDataset;
    private Context context;
    private SetViewActivity setViewActivity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(ArrayList<String> myDataset, Context context, SetViewActivity s) {
        this.mDataset = myDataset;
        this.context = context;
        this.setViewActivity=s;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.mTextView.setText(mDataset.get(position));

        holder.threeDots.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(context, holder.threeDots);
                //inflating menu from xml resource
                popup.inflate(R.menu.popup_menu_options);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popupMenuOptions1:

                                setViewActivity.handleDeleteClick(position);

                                break;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

