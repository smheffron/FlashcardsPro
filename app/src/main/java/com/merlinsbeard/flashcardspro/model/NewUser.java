package com.merlinsbeard.flashcardspro.model;

import android.databinding.Bindable;

import com.merlinsbeard.flashcardspro.BR;

public class NewUser extends User {
    private String confirmPassword;

    @Bindable
    public String getConfirmPassword(){
        return confirmPassword;
    }

    public void setConfirmPassword(String password){
        this.confirmPassword = password;
        notifyPropertyChanged(BR.confirmPassword);
    }
}
