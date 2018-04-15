package com.merlinsbeard.flashcardspro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.merlinsbeard.flashcardspro.databinding.LoginActivityBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityBinding binding;
    private User user = new User();
    private ProgressBar loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        binding.setActivity(this);
        binding.setUser(user);

        loadingAnimation = binding.loadingAnimation;
        loadingAnimation.setVisibility(View.INVISIBLE);
    }

    public void onClickCreateAccount(View view){
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    public void onClickLoginButton(View view){
        if(user.getUsername() == null || user.getUsername().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(user.getPassword() == null || user.getPassword().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/verifyLogin.php";

        Map<String,String> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("password", user.getPassword());

        final Activity thisActivity = this;
        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        binding.loginButton.setEnabled(false);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){
                        if(response.getString("login").equals("succeeded")){
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username",user.getUsername());
                            editor.putInt("userId",response.getInt("userId"));
                            editor.apply();
                            Intent intent = new Intent(thisActivity, SetViewActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            loadingAnimation.setVisibility(ProgressBar.INVISIBLE);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_SHORT).show();
                            binding.loginButton.setEnabled(true);
                            loadingAnimation.setVisibility(ProgressBar.INVISIBLE);
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                        binding.loginButton.setEnabled(true);
                        loadingAnimation.setVisibility(ProgressBar.INVISIBLE);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_SHORT).show();
                    binding.loginButton.setEnabled(true);
                    loadingAnimation.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_SHORT).show();
                binding.loginButton.setEnabled(true);
                loadingAnimation.setVisibility(ProgressBar.INVISIBLE);
            }
        });

        loadingAnimation.setVisibility(ProgressBar.VISIBLE);
        queue.add(request);
    }

    @Override
    protected void onDestroy(){
        binding = null;
        super.onDestroy();
    }
}
