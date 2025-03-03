package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class signup_activity extends AppCompatActivity {
    ImageView img_homeicon;
    TextView tv_logoname;
    TextView tv_slogan;
    Button btn_next, btn_login;
    TextInputLayout input_fullname, input_username, input_password, input_address, input_email;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    FirebaseDatabase rootNode;
    DatabaseReference reference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        img_homeicon = findViewById(R.id.img_homeicon);
        tv_logoname = findViewById(R.id.tv_logoname);
        tv_slogan = findViewById(R.id.tv_slogan);
        btn_next = findViewById(R.id.btn_next);
        btn_login = findViewById(R.id.btn_login);
        input_fullname = findViewById(R.id.input_fullname);
        input_username = findViewById(R.id.input_username);
        input_email = findViewById(R.id.input_email);
        input_address = findViewById(R.id.input_address);
        input_password = findViewById(R.id.input_password);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = input_username.getEditText().getText().toString();
                String email = input_email.getEditText().getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                checkExistingnUser(username, email, v);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup_activity.this, login_activity.class);

                Pair[] pairs = new Pair[3];
                pairs[0] =  new Pair<View, String>(img_homeicon, "logo_image");
                pairs[1] =  new Pair<View, String>(tv_logoname, "logo_text");
                pairs[2] =  new Pair<View, String>(tv_slogan, "logo_desc");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signup_activity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void callNextSignupScreen(View view) {


       if (!validateName() | !validateUsername() | !validateEmail() | !validatePassword() | !validateAddress()) {
            progressBar.setVisibility(View.GONE);
            return;
        }

        Intent intent = new Intent(signup_activity.this, signup2_activity.class);

        Pair[] pairs = new Pair[5];
        pairs[0] =  new Pair<View, String>(img_homeicon, "logo_image");
        pairs[1] =  new Pair<View, String>(tv_logoname, "logo_desc");
        pairs[2] =  new Pair<View, String>(tv_slogan, "logo_text");
        pairs[3] =  new Pair<View, String>(btn_next, "button_tran");
        pairs[4] =  new Pair<View, String>(btn_login, "login_signup_tran");


        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signup_activity.this, pairs);
        String fullname = input_fullname.getEditText().getText().toString();
        String email = input_email.getEditText().getText().toString();
        String username = input_username.getEditText().getText().toString();
        String password = input_password.getEditText().getText().toString();
        String address = input_address.getEditText().getText().toString();
        intent.putExtra("fullname", fullname);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("address", address);
        startActivity(intent, options.toBundle());
    }

    private Boolean validateName() {
        String val = input_fullname.getEditText().getText().toString();
        if (val.isEmpty()){
            input_fullname.setError("Field is not empty");
            return false;
        } else {
            input_fullname.setError(null);
            input_fullname.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateUsername() {
        String noSpaceWhite = "\\A\\w{4,20}\\z";
        String val = input_username.getEditText().getText().toString();
        if (val.isEmpty()){
            input_username.setError("Field is not empty!");
            return false;
        } else if (val.length() >= 15) {
            input_username.setError("Username is too long");
            return false;
        } else if (!val.matches(noSpaceWhite)) {
            input_username.setError("White Spaces are not allowed");
            return false;
        } else {
            input_username.setError(null);
            input_username.setErrorEnabled(false);
            return true;
        }
    }
    private  Boolean validateEmail() {
        String val = input_email.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()){
            input_email.setError("Field is not empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            input_email.setError("Invalid email address");
            return false;
        } else {
            input_fullname.setError(null);
            input_username.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = input_password.getEditText().getText().toString();String passwordVal = "^(?=.*[0-9])" +           // Require at least one digit
                "(?=.*[a-z])" +              // Require at least one lowercase letter
                "(?=.*[A-Z])" +              // Require at least one uppercase letter
                "(?=.*[!@$%^&(){}\\[\\]:;<>,.?/~_+-=|])" + // Require at least one special character
                "(?=\\S+$)" +                // No white space
                ".{8,32}$";                  // Password length between 8 and 32 characters


        if (val.isEmpty()){
            input_password.setError("Field is not empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            input_password.setError("Password is too weaK");
            return false;
        } else {
            input_password.setError(null);
            input_password.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validateAddress() {
        String val = input_address.getEditText().getText().toString();
        if (val.isEmpty()){
            input_address.setError("Field is not empty");
            return false;
        } else {
            input_address.setError(null);
            input_address.setErrorEnabled(false);
            return true;
        }
    }
    private void checkExistingnUser(String username, String email, View view) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query emailRef = reference.orderByChild("email").equalTo(email);
        Query usernameRef = reference.orderByChild("username").equalTo(username);
        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot emailSnapshot) {
                if (emailSnapshot.exists()) {
                    // Email already exists
                    Toast.makeText(signup_activity.this, "Email already exists", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot usernameSnapshot) {
                            if (usernameSnapshot.exists()) {
                                // Username already exists
                                Toast.makeText(signup_activity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            } else {

                                            callNextSignupScreen(view);
                                            progressBar.setVisibility(View.GONE);
                                        }
                                }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }



}