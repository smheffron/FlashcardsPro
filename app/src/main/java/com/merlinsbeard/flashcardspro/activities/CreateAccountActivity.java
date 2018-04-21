package com.merlinsbeard.flashcardspro.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.merlinsbeard.flashcardspro.model.NewUser;
import com.merlinsbeard.flashcardspro.R;
import com.merlinsbeard.flashcardspro.databinding.ActivityCreateAccountBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private ActivityCreateAccountBinding binding;
    private NewUser newUser = new NewUser();
    private ProgressBar loadingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account);
        binding.setActivity(this);
        binding.setNewUser(newUser);

        loadingAnimation = binding.loadingAnimation;
        loadingAnimation.setVisibility(View.INVISIBLE);
    }

    public void onClickCreateAccountButton(View view) {
        // Ensure that username, password, and confirm password are not empty and that password matches confirm password
        if(newUser.getUsername() == null || newUser.getUsername().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(newUser.getPassword() == null || newUser.getPassword().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(newUser.getConfirmPassword() == null || newUser.getConfirmPassword().isEmpty()){
            Toast.makeText(getApplicationContext(), "Please confirm your password", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(!newUser.getPassword().equals(newUser.getConfirmPassword())){
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            newUser.setPassword("");
            newUser.setConfirmPassword("");
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/newUser.php";

        // Set up POST parameters for HTTP request
        Map<String,String> params = new HashMap<>();
        params.put("username", newUser.getUsername());
        params.put("password", newUser.getPassword());

        final Activity thisActivity = this;
        final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        // Disable create account button while waiting for request to be processed
        binding.newUserCreateAccountButton.setEnabled(false);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("status").equals("succeeded")){
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username",newUser.getUsername());
                        editor.putInt("userId",response.getInt("userId"));
                        editor.apply();
                        Intent intent = new Intent(thisActivity, FlashcardSetActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        loadingAnimation.setVisibility(ProgressBar.INVISIBLE);
                        startActivity(intent);
                    }
                    else {
                        if(response.has("reason") && response.getString("reason").equals("name taken")) {
                            Toast.makeText(getApplicationContext(), "This username is already taken", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                            Log.d("WEB SERVICE ERROR", "problem with web serivce");
                        }

                        binding.newUserCreateAccountButton.setEnabled(true);
                        loadingAnimation.setVisibility(ProgressBar.INVISIBLE);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_SHORT).show();
                    binding.newUserCreateAccountButton.setEnabled(true);
                    loadingAnimation.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Incorrect username/password", Toast.LENGTH_SHORT).show();
                binding.newUserCreateAccountButton.setEnabled(true);
                loadingAnimation.setVisibility(ProgressBar.INVISIBLE);
            }
        });

        loadingAnimation.setVisibility(ProgressBar.VISIBLE);
        queue.add(request);
    }
}
