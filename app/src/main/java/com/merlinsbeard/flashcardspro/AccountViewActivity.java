package com.merlinsbeard.flashcardspro;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.merlinsbeard.flashcardspro.databinding.ActivityAccountViewBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountViewActivity extends AppCompatActivity {
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

    public void onClickChangeUsername(View view){
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updateUsername.php?id=" + user.getUserId();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.change_username_popup);
        dialog.setTitle("Change username");
        dialog.show();

        final TextView newUsername = dialog.findViewById(R.id.newUsernameFromChangeUsername);
        final TextView password = dialog.findViewById(R.id.passwordFromChangeUsername);

        dialog.findViewById(R.id.dialogButtonCancelFromChangeUsername).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.dialogButtonRenameFromChangeUsername).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newUsername.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Enter new username", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(password.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String,String> params = new HashMap<>();
                params.put("password", String.valueOf(password.getText()));
                params.put("newUsername", String.valueOf(newUsername.getText()));

                final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("succeeded")){
                                user.setUsername(String.valueOf(newUsername.getText()));
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("username", user.getUsername());
                                editor.apply();
                            }
                            else if(response.has("reason") || response.getString("reason").equals("name taken")){
                                Toast.makeText(getApplicationContext(), "Username is already taken", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                    }
                });

                queue.add(request);
                dialog.dismiss();
            }
        });
    }

    public void onClickChangePassword(View view){
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updatePassword.php?id=" + user.getUserId();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.change_password_popup);
        dialog.setTitle("Change password");
        dialog.show();

        final TextView oldPassword = dialog.findViewById(R.id.oldPasswordFromChangePassword);
        final TextView newPassword = dialog.findViewById(R.id.passwordFromChangePassword);
        final TextView confirmNewPassword = dialog.findViewById(R.id.confirmPasswordFromChangePassword);

        dialog.findViewById(R.id.dialogButtonCancelFromChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.dialogButtonChangePasswordFromChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(oldPassword.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Enter old password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(newPassword.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(confirmNewPassword.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Confirm new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                String newPasswordString = String.valueOf(newPassword.getText());
                String confirmNewPasswordString = String.valueOf(confirmNewPassword.getText());

                if(!newPasswordString.equals(confirmNewPasswordString)){
                    Toast.makeText(getApplicationContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String,String> params = new HashMap<>();
                params.put("oldPassword", String.valueOf(oldPassword.getText()));
                params.put("newPassword", String.valueOf(newPassword.getText()));

                final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("succeeded")){
                                Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_SHORT).show();
                            }
                            else if(response.has("reason") && response.getString("resaon").equals("authentication failure")){
                                Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                    }
                });

                queue.add(request);
                dialog.dismiss();
            }
        });
    }

    public void onClickLogout(View view){
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("userId");
        editor.remove("username");
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onClickDeleteAccount(View view){
        final Activity thisActivity = this;
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteUser.php?id=" + user.getUserId();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.delete_account_popup);
        dialog.setTitle("Delete account");
        dialog.show();

        final TextView password = dialog.findViewById(R.id.passwordFromDeleteAccount);

        dialog.findViewById(R.id.dialogButtonCancelFromDeleteAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.dialogButtonDeleteFromDeleteAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String,String> params = new HashMap<>();
                params.put("password", String.valueOf(password.getText()));

                final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("succeeded")){
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.remove("username");
                                editor.remove("userId");
                                editor.apply();
                                Intent intent = new Intent(thisActivity, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else if(response.has("reason") && response.getString("resaon").equals("authentication failure")){
                                Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                    }
                });

                queue.add(request);
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy(){
        binding = null;
        super.onDestroy();
    }
}
