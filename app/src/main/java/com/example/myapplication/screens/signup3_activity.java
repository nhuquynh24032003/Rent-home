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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

public class signup3_activity extends AppCompatActivity {

    Button btn_next, btn_login;
    ImageView img_homeicon;
    TextView tv_logoname;
    TextView tv_slogan;
    TextInputLayout input_phonenumber;
    CountryCodePicker countryCodePicker;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup3);
        btn_login = findViewById(R.id.btn_login);
        btn_next = findViewById(R.id.btn_next);
        input_phonenumber = findViewById(R.id.input_phonenumber);
        countryCodePicker = findViewById(R.id.country_code_picker);
        progressBar = findViewById(R.id.progressBar);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup3_activity.this, login_activity.class);

                Pair[] pairs = new Pair[5];
                pairs[0] =  new Pair<View, String>(img_homeicon, "logo_image");
                pairs[1] =  new Pair<View, String>(tv_logoname, "logo_text");
                pairs[2] =  new Pair<View, String>(tv_slogan, "logo_desc");
                pairs[3] =  new Pair<View, String>(btn_next, "button_tran");
                pairs[4] =  new Pair<View, String>(btn_login, "login_signup_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signup3_activity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String phonenumber = input_phonenumber.getEditText().getText().toString();
                checkExistingnUser(phonenumber, v);
            }
        });
    }
    private void nextAcitivity(View v) {

        if (!validatePhone()) {
            return;
        }
        String fullname = getIntent().getStringExtra("fullname");
        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String address = getIntent().getStringExtra("address");
        String gender = getIntent().getStringExtra("gender");
        String date = getIntent().getStringExtra("date");

        String phonenumberEntered = input_phonenumber.getEditText().getText().toString();
        String phonenumber =countryCodePicker.getSelectedCountryCodeWithPlus()+phonenumberEntered;

        Intent intent = new Intent(signup3_activity.this, VerifyOTP.class);
        intent.putExtra("fullname", fullname);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("address", address);
        intent.putExtra("gender", gender);
        intent.putExtra("date", date);
        intent.putExtra("phonenumber", phonenumber);
        startActivity(intent);
    }
    private Boolean validatePhone() {
        String val = input_phonenumber.getEditText().getText().toString();
        if (val.isEmpty()){
            input_phonenumber.setError("Field is not empty");
            return false;
        } else {
            input_phonenumber.setError(null);
            input_phonenumber.setErrorEnabled(false);
            return true;
        }
    }
    private void checkExistingnUser(String phonenumber, View view) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query phonenumberRef = reference.orderByChild("phonenumber").equalTo(phonenumber);
        phonenumberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot phoneSnapshot) {
                if (phoneSnapshot.exists()) {
                    // Email already exists
                    Toast.makeText(signup3_activity.this, "Phone already exists", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {

                    nextAcitivity(view);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}