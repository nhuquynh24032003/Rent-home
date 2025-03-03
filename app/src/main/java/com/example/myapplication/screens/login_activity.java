package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class login_activity extends AppCompatActivity {
    Button btn_signup, btn_signin;
    ImageView img_homeicon;
    TextView tv_logoname, tv_slogan;
    TextInputLayout input_username, input_password;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    String newtoken;
    SharedPreferences sharedPreferences;

// Inside your onCreate method


    @Override
    public void onStart() {
        super.onStart();

        // Check if the user data is available in SharedPreferences
        if (sharedPreferences != null) {
            String userId = sharedPreferences.getString("userId", "");

            if (!userId.isEmpty()) {
                // User data is available, redirect to Dashboard
                String fullname = sharedPreferences.getString("fullname", "");
                String avatarUrl = sharedPreferences.getString("avatarUrl", "");
                String address = sharedPreferences.getString("address", "");
                String phonenumber = sharedPreferences.getString("phonenumber", "");
                String gender = sharedPreferences.getString("gender", "");
                String date = sharedPreferences.getString("date", "");
                String email = sharedPreferences.getString("email", "");
                String password = sharedPreferences.getString("password", "");
                String fcmToken = sharedPreferences.getString("fcmtoken", "");

                Intent intent = new Intent(login_activity.this, Dashboard.class);
                intent.putExtra("userId", userId);
                intent.putExtra("fullname", fullname);
                intent.putExtra("avatarUrl", avatarUrl);
                intent.putExtra("address", address);
                intent.putExtra("phonenumber", phonenumber);
                intent.putExtra("gender", gender);
                intent.putExtra("date", date);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("fcmtoken", fcmToken);
                startActivity(intent);
                finish(); // Finish the current activity to prevent the user from going back to the login screen
            } else {
                // Handle the case where user ID is empty
            }
        } else {
            // Handle the case where sharedPreferences is null
            Log.e("LoginActivity", "SharedPreferences is null in onStart");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);
        btn_signup = findViewById(R.id.btn_signup);
        btn_signin = findViewById(R.id.btn_signin);
        img_homeicon = findViewById(R.id.img_homeicon);
        tv_logoname = findViewById(R.id.tv_logoname);
        tv_slogan = findViewById(R.id.tv_slogan);
        input_username = findViewById(R.id.input_username);
        input_password = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login_activity.this, signup_activity.class);
                startActivity(intent);
            }
        });

        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newToken();
                signIn();
            }
        });
    }

    private boolean validateEmail() {
        String val = input_username.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            input_username.setError("Field is required");
            return false;
        } else {
            input_username.setError(null);
            input_username.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = input_password.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            input_password.setError("Field is required");
            return false;
        } else {
            input_password.setError(null);
            input_password.setErrorEnabled(false);
            return true;
        }
    }

    private void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        if(!validateEmail() | !validatePassword()){
            return;
        }

        String userEnteredUsername = input_username.getEditText().getText().toString();
        String userEnteredPassword = input_password.getEditText().getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("username").equalTo(userEnteredUsername);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               // progressBar.setVisibility(View.GONE);

                if (snapshot.exists()) {
                    String userId = snapshot.getChildren().iterator().next().getKey(); // Get the user ID
                    String passwordDB = snapshot.child(userId).child("password").getValue(String.class);
                    String fullname = snapshot.child(userId).child("fullname").getValue(String.class);
                    String avatarUrl = snapshot.child(userId).child("avatarUrl").getValue(String.class);
                    String address = snapshot.child(userId).child("address").getValue(String.class);
                    String phonenumber = snapshot.child(userId).child("phonenumber").getValue(String.class);
                    String gender = snapshot.child(userId).child("gender").getValue(String.class);
                    String date = snapshot.child(userId).child("date").getValue(String.class);
                    String email = snapshot.child(userId).child("email").getValue(String.class);
                    String password = snapshot.child(userId).child("password").getValue(String.class);
                    String fcmToken = snapshot.child(userId).child("fcmtoken").getValue(String.class);

                    if (passwordDB != null && passwordDB.equals(userEnteredPassword)) {
                        DatabaseReference userRef = snapshot.child(userId).getRef();
                        userRef.child("fcmtoken").setValue(newtoken);
                        Intent intent = new Intent(login_activity.this, Dashboard.class);
                        intent.putExtra("fullname", fullname);
                        intent.putExtra("avatarUrl", avatarUrl);
                        intent.putExtra("address", address);
                        intent.putExtra("phonenumber", phonenumber);
                        intent.putExtra("gender", gender);
                        intent.putExtra("date", date);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("userId", userId);
                        intent.putExtra("fcmtoken", fcmToken);


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("fullname", fullname);
                        editor.putString("avatarUrl", avatarUrl);
                        editor.putString("address", address);
                        editor.putString("phonenumber", phonenumber);
                        editor.putString("gender", gender);
                        editor.putString("date", date);
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.putString("userId", userId);
                        editor.putString("fcmtoken", fcmToken);
                        editor.apply();

                        startActivity(intent);
                        finish();
                    } else {
                        if (passwordDB == null) {
                            // Handle the case where passwordDB is null
                            Log.d("Debug", "Password not found in the database for the specified user");
                            Toast.makeText(login_activity.this, "Password not found in the database", Toast.LENGTH_SHORT).show();
                        } else {
                            // Incorrect password handling
                            input_password.setError("Incorrect password");
                            input_password.requestFocus();
                            Toast.makeText(login_activity.this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    // User does not exist
                    Toast.makeText(login_activity.this, "No such user exists", Toast.LENGTH_SHORT).show();
                    input_username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(login_activity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    void newToken () {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                          //  Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                         newtoken = task.getResult();

                    }
                });
    }
}
