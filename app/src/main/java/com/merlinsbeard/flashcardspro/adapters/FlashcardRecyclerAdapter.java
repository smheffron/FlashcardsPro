package com.merlinsbeard.flashcardspro.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.merlinsbeard.flashcardspro.activities.FlashcardActivity;
import com.merlinsbeard.flashcardspro.model.Flashcard;
import com.merlinsbeard.flashcardspro.R;

import java.util.ArrayList;

public class FlashcardRecyclerAdapter extends RecyclerView.Adapter<FlashcardRecyclerAdapter.ViewHolder>{

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        // each data item is just a string in this case
        CardView mCardView;

        public View view;

        public ImageView threeDots;

        ViewHolder(View v) {
            super(v);

            mCardView = v.findViewById(R.id.cardViewForFlashcards);
            mTextView = v.findViewById(R.id.flashcardName);
            Log.d("saldknflsadkjfljknasdf", String.valueOf(v.findViewById(R.id.flashcardName)));
            threeDots = v.findViewById(R.id.threeDotsForFlashcards);
            view = v;
        }


    }

    private ArrayList<Flashcard> mDataset;
    private Context context;
    private FlashcardActivity flashcardActivity;

    // Provide a suitable constructor (depends on the kind of dataset)
    protected FlashcardRecyclerAdapter(ArrayList<Flashcard> myDataset, Context context, FlashcardActivity s) {
        this.mDataset = myDataset;
        this.context = context;
        this.flashcardActivity =s;

    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public FlashcardRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                  int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.flashcard_card_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d("HOLDER", holder.toString());
        Log.d("TEXTVIEW",holder.mTextView.toString());

        holder.mTextView.setText(mDataset.get(position).getFrontText());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flashcardActivity.handleItemClick(position);
            }
        });


        holder.threeDots.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(context, holder.threeDots);
                //inflating menu from xml resource
                popup.inflate(R.menu.popup_menu_options_edit_flashcard);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.popupDelete:

                                flashcardActivity.handleDeleteClick(position);
                                break;

                            case R.id.popupEdit:

                                flashcardActivity.handleRenameClick(position);
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
