package com.merlinsbeard.flashcardspro;

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

import java.util.ArrayList;

public class RecyclerAdapterForFlashcards extends RecyclerView.Adapter<RecyclerAdapterForFlashcards.ViewHolder>{

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

            mCardView = v.findViewById(R.id.cardViewForFlashcards);
            mTextView = v.findViewById(R.id.flashcardName);
            Log.d("saldknflsadkjfljknasdf", String.valueOf(v.findViewById(R.id.flashcardName)));
            threeDots = v.findViewById(R.id.threeDotsForFlashcards);
        }

    }

    private ArrayList<FlashCard> mDataset;
    private Context context;
    private FlashCardView flashCardView;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapterForFlashcards(ArrayList<FlashCard> myDataset, Context context, FlashCardView s) {
        this.mDataset = myDataset;
        this.context = context;
        this.flashCardView=s;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapterForFlashcards.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.flashcards_layout, parent, false);

        RecyclerAdapterForFlashcards.ViewHolder viewHolder = new RecyclerAdapterForFlashcards.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d("HOLDER", holder.toString());
        Log.d("TEXTVIEW",holder.mTextView.toString());

        holder.mTextView.setText(mDataset.get(position).getFrontText());



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

                                flashCardView.handleDeleteClick(position);
                                break;

                            case R.id.popupMenuOptions2:

                                flashCardView.handleRenameClick(position);
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
