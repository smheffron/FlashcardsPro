package com.merlinsbeard.flashcardspro;

import android.databinding.Bindable;

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
