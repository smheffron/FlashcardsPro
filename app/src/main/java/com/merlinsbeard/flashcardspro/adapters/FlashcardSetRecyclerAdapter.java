package com.merlinsbeard.flashcardspro.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.merlinsbeard.flashcardspro.activities.FlashcardSetActivity;
import com.merlinsbeard.flashcardspro.model.FlashcardSet;
import com.merlinsbeard.flashcardspro.R;

import java.util.ArrayList;

public class FlashcardSetRecyclerAdapter extends RecyclerView.Adapter<FlashcardSetRecyclerAdapter.ViewHolder> {

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        CardView mCardView;

        View view;

        public ImageView threeDots;

        ViewHolder(View v) {
            super(v);

            view = v;
            mCardView = v.findViewById(R.id.cardView);
            mTextView = v.findViewById(R.id.setName);
            threeDots = v.findViewById(R.id.threeDots);
        }

    }

    private ArrayList<FlashcardSet> mDataset;
    private Context context;
    private FlashcardSetActivity flashcardSetActivity;

    protected FlashcardSetRecyclerAdapter(ArrayList<FlashcardSet> myDataset, Context context, FlashcardSetActivity s) {
        this.mDataset = myDataset;
        this.context = context;
        this.flashcardSetActivity =s;

    }

    @NonNull
    @Override
    public FlashcardSetRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.set_card_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final int p = position;

        // Set the text on the card to the name of the flashcard set
        holder.mTextView.setText(mDataset.get(p).getName());

        holder.threeDots.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Display popup with options to rename or delete flashcard set
                PopupMenu popup = new PopupMenu(context, holder.threeDots);
                popup.inflate(R.menu.popup_menu_options);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popupMenuOptions1:

                                flashcardSetActivity.handleDeleteClick(p);
                                break;

                            case R.id.popupMenuOptions2:

                                flashcardSetActivity.handleRenameClick(p);
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashcardSetActivity.handleItemClick(p);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

