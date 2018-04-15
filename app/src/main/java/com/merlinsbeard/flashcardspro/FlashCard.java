package com.merlinsbeard.flashcardspro;

public class FlashCard {
    private String frontText;
    private String backText;
    private int flashcardId;

    FlashCard(String frontText, String backText, int flashcardId){
        this.backText=backText;
        this.flashcardId=flashcardId;
        this.frontText=frontText;
    }

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
}

