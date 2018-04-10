package com.merlinsbeard.flashcardspro;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity);
        binding.setActivity(this);
        binding.setUser(user);




    }

    public void onClickCreateAccount(View view){
        Intent intent = new Intent(this,CreateAccountActivity.class);
        startActivity(intent);
    }

    public void onClickLoginButton(View view){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/verifyLogin.php";

        Map<String,String> params = new HashMap<>();
        params.put("username", user.getUsername());
        Log.d("PIZZZAAAAAAAAAAAA", user.getUsername());
        params.put("password", user.getPassword());
        Log.d("PIZZZAAAAAAAAAAAA", user.getPassword());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("PIZZZAAAAAAAAAAAA", "User ID: " + String.valueOf(response.getInt("userId")));
                } catch (JSONException e) {
                    Log.d("PIZZZZZZZAAAAAAAAA", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("PIZZZZZAAAAAAAAAABAD", error.toString());
            }
        });

        queue.add(request);
    }

    @Override
    protected void onDestroy(){
        binding = null;
        super.onDestroy();
    }
}
