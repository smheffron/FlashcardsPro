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

import com.merlinsbeard.flashcardspro.R;
import com.merlinsbeard.flashcardspro.activities.FlashcardActivity;
import com.merlinsbeard.flashcardspro.model.Flashcard;

import java.util.ArrayList;

public class FlashcardRecyclerAdapter extends RecyclerView.Adapter<FlashcardRecyclerAdapter.ViewHolder>{

    // Provide a reference to the views for each data item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        CardView mCardView;

        public View view;

        public ImageView threeDots;

        ViewHolder(View v) {
            super(v);

            mCardView = v.findViewById(R.id.cardViewForFlashcards);
            mTextView = v.findViewById(R.id.flashcardName);
            threeDots = v.findViewById(R.id.threeDotsForFlashcards);
            view = v;
        }
    }

    private ArrayList<Flashcard> mDataset;
    private Context context;
    private FlashcardActivity flashcardActivity;

    protected FlashcardRecyclerAdapter(ArrayList<Flashcard> myDataset, Context context, FlashcardActivity s) {
        this.mDataset = myDataset;
        this.context = context;
        this.flashcardActivity =s;

    }

    @NonNull
    @Override
    public FlashcardRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.flashcard_card_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final int p = position;

        // Set the text on the card to the front text of the flashcard
        holder.mTextView.setText(mDataset.get(p).getFrontText());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashcardActivity.handleItemClick(p);
            }
        });


        holder.threeDots.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                // Display popup with options to rename or delete flashcard
                PopupMenu popup = new PopupMenu(context, holder.threeDots);
                popup.inflate(R.menu.popup_menu_options_edit_flashcard);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popupDelete:

                                flashcardActivity.handleDeleteClick(p);
                                break;

                            case R.id.popupEdit:

                                flashcardActivity.handleRenameClick(p);
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
