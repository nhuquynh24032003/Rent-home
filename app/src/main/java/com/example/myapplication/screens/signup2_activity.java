package com.example.myapplication.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.Calendar;

public class signup2_activity extends AppCompatActivity {
    Button btn_next, btn_login;
    ImageView img_homeicon;
    TextView tv_logoname;
    TextView tv_slogan;
    RadioGroup radioGroup;
    DatePicker datePicker;
    RadioButton selectedGender;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        btn_next = findViewById(R.id.btn_next);
        img_homeicon = findViewById(R.id.img_homeicon);
        tv_logoname = findViewById(R.id.tv_logoname);
        tv_slogan = findViewById(R.id.tv_slogan);
        btn_next = findViewById(R.id.btn_next);
        btn_login = findViewById(R.id.btn_login);
        radioGroup = findViewById(R.id.radioGroup);
        datePicker = findViewById(R.id.age_picker);
        progressBar = findViewById(R.id.progressBar);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup2_activity.this, login_activity.class);

                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View, String>(img_homeicon, "logo_image");
                pairs[1] = new Pair<View, String>(tv_logoname, "logo_text");
                pairs[2] = new Pair<View, String>(tv_slogan, "logo_desc");
                pairs[3] = new Pair<View, String>(btn_next, "button_tran");
                pairs[4] = new Pair<View, String>(btn_login, "login_signup_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signup2_activity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                callNextSignupScreen(v);
            }
        });
    }

    private void callNextSignupScreen(View view) {
        if (!validateAge() | !validateGender()) {
            return;
        }

        selectedGender = findViewById(radioGroup.getCheckedRadioButtonId());
        String gender = selectedGender.getText().toString();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        String date = day + "/" + month + "/" + year;

        String fullname = getIntent().getStringExtra("fullname");
        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String address = getIntent().getStringExtra("address");
        Intent intent = new Intent(signup2_activity.this, signup3_activity.class);

        Pair[] pairs = new Pair[5];
        pairs[0] = new Pair<View, String>(img_homeicon, "logo_image");
        pairs[1] = new Pair<View, String>(tv_logoname, "logo_desc");
        pairs[2] = new Pair<View, String>(tv_slogan, "logo_text");
        pairs[3] = new Pair<View, String>(btn_next, "button_tran");
        pairs[4] = new Pair<View, String>(btn_login, "login_signup_tran");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(signup2_activity.this, pairs);
        intent.putExtra("fullname", fullname);
        intent.putExtra("email", email);
        intent.putExtra("username", username);
        intent.putExtra("password", password);
        intent.putExtra("address", address);
        intent.putExtra("gender", gender);
        intent.putExtra("date", date);
        Toast.makeText(this, address, Toast.LENGTH_SHORT).show();
        startActivity(intent, options.toBundle());
    }

    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 14) {
            Toast.makeText(this, "You are not eligible to apply", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}