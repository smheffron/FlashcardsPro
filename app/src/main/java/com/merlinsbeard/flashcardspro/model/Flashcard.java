package com.merlinsbeard.flashcardspro.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Flashcard implements Parcelable {
    private String frontText;
    private String backText;
    private int flashcardId;

    public Flashcard(String frontText, String backText, int flashcardId){
        this.backText=backText;
        this.flashcardId=flashcardId;
        this.frontText=frontText;
    }

    private Flashcard(Parcel in) {
        frontText = in.readString();
        backText = in.readString();
        flashcardId = in.readInt();
    }

    public static final Creator<Flashcard> CREATOR = new Creator<Flashcard>() {
        @Override
        public Flashcard createFromParcel(Parcel in) {
            return new Flashcard(in);
        }

        @Override
        public Flashcard[] newArray(int size) {
            return new Flashcard[size];
        }
    };

    public int getFlashcardId() {
        return flashcardId;
    }

    public String getBackText() {
        return backText;
    }

    public String getFrontText() {
        return frontText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public void setFlashcardId(int flashcardId) {
        this.flashcardId = flashcardId;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(frontText);
        parcel.writeString(backText);
        parcel.writeInt(flashcardId);
    }


}

