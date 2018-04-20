package com.merlinsbeard.flashcardspro.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.merlinsbeard.flashcardspro.BR;

public class User extends BaseObservable{
    private String username;
    private String password;
    private int userId;

    public User(){
        userId = -1;
    }

    @Bindable
    public String getUsername(){
        return username;
    }

    @Bindable
    public String getPassword(){
        return password;
    }

    public int getUserId(){
        return userId;
    }

    public void setUsername(final String username){
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    public void setPassword(final String password){
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void setUserId(final int id){
        this.userId = id;
    }
}
