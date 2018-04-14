package com.merlinsbeard.flashcardspro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.merlinsbeard.flashcardspro.databinding.ActivityAccountViewBinding;

public class AccountViewActivity extends Activity {
    private ActivityAccountViewBinding binding;
    private User user;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        user =  new User();

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        user.setUsername(preferences.getString("username", ""));
        user.setUserId(preferences.getInt("userId", -1));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_view);
        binding.setActivity(this);
        binding.setUser(user);
    }

    public void onClickLogout(View view){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("userId");
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
