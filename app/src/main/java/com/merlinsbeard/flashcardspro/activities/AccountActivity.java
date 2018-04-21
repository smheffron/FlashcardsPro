package com.merlinsbeard.flashcardspro.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.merlinsbeard.flashcardspro.R;
import com.merlinsbeard.flashcardspro.databinding.ActivityAccountBinding;
import com.merlinsbeard.flashcardspro.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {
    private ActivityAccountBinding binding;
    private User user;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Allow up navigation
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create user with current user's ID and username
        user =  new User();

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        user.setUsername(preferences.getString("username", ""));
        user.setUserId(preferences.getInt("userId", -1));

        binding = DataBindingUtil.setContentView(this, R.layout.activity_account);
        binding.setActivity(this);
        binding.setUser(user);
    }

    public void onClickChangeUsername(View view) {
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updateUsername.php?id=" + user.getUserId();

        // Display dialog for new username entry
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
                // Ensure the new username and the password are not empty
                if(newUsername.getText().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Enter new username", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(password.getText().length() < 1) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Set up POST parameters for HTTP request
                Map<String,String> params = new HashMap<>();
                params.put("password", String.valueOf(password.getText()));
                params.put("newUsername", String.valueOf(newUsername.getText()));

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("succeeded")) {
                                // Set user's username to the new name and write it to SharedPreferences
                                user.setUsername(String.valueOf(newUsername.getText()));
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("username", user.getUsername());
                                editor.apply();
                            }
                            else if(response.has("reason") || response.getString("reason").equals("name taken")) {
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

    public void onClickChangePassword(View view) {
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/updatePassword.php?id=" + user.getUserId();

        // Display dialog for old and new password entry
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
                // Ensure that old password, new password, and confirm password are not empty
                if(oldPassword.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Please enter old password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(newPassword.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Please enter new password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(confirmNewPassword.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Please confirm new password", Toast.LENGTH_SHORT).show();
                    return;
                }

                String newPasswordString = String.valueOf(newPassword.getText());
                String confirmNewPasswordString = String.valueOf(confirmNewPassword.getText());

                // Ensure that the new password matches the confirmed new password
                if(!newPasswordString.equals(confirmNewPasswordString)){
                    Toast.makeText(getApplicationContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Set up POST parameters for HTTP request
                Map<String,String> params = new HashMap<>();
                params.put("oldPassword", String.valueOf(oldPassword.getText()));
                params.put("newPassword", String.valueOf(newPassword.getText()));

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

    public void onClickLogout(View view) {
        logout();
    }

    public void onClickDeleteAccount(View view) {
        final RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "http://ec2-18-188-60-72.us-east-2.compute.amazonaws.com/FlashcardsPro/deleteUser.php?id=" + user.getUserId();

        // Display dialog for password to confirm account deletion
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
                // Ensure that the password is not empty
                if(password.getText().length() < 1){
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Set up POST parameters for HTTP request
                Map<String,String> params = new HashMap<>();
                params.put("password", String.valueOf(password.getText()));

                final SharedPreferences preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("succeeded")){
                                logout();
                            }
                            else if(response.has("reason") && response.getString("resaon").equals("authentication failure")){
                                Toast.makeText(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Could not connect to server", Toast.LENGTH_SHORT).show();

                                Log.d("Failed: ", response.getString("status"));

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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Remove user credentials from SharedPreferences, then navigate to the login page and clear the back stack
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("userId");
        editor.remove("username");
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
