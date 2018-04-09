package com.merlinsbeard.flashcardspro;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.databinding.DataBindingUtil;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.merlinsbeard.flashcardspro.databinding.LoginActivityBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        binding.setActivity(this);





    }

    public void onClickCreateAccount(View view){
        Intent intent = new Intent(this,CreateAccountActivity.class);
        startActivity(intent);
    }

    public void onClickLoginButton(View view){
        Intent intent = new Intent(this,SetViewActivity.class);
        startActivity(intent);
    }

}
