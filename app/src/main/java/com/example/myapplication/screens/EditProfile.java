package com.example.myapplication.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EditProfile extends AppCompatActivity {
    Toolbar toolbar;
    RelativeLayout ly_date;
    String fullname;
    String avatarUrl;
    String address;
    String phonenumber;
    String gender;
    String date;
    String email;
    String password;
    String userId;
    TextInputLayout input_date;
    ImageView dropdownIcon;
    TextInputLayout input_address;
    TextInputLayout input_phonenumber;
    TextInputLayout input_fullname;
    TextInputLayout input_password;
    RadioGroup radioGroup;
    RadioButton maleRadioButton;
    RadioButton femaleRadioButton;
    RadioButton otherRadioButton;
    DatabaseReference reference;
    Button btn_save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        reference = FirebaseDatabase.getInstance().getReference("users");

        toolbar = findViewById(R.id.toolbar);
        input_date = findViewById(R.id.input_date);
        dropdownIcon = findViewById(R.id.dropdownIcon);
        input_address = findViewById(R.id.input_address);
        input_phonenumber  = findViewById(R.id.input_phonenumber);
        input_date = findViewById(R.id.input_date);
        input_fullname = findViewById(R.id.input_fullname);
        input_password = findViewById(R.id.input_password);
        maleRadioButton = findViewById(R.id.male);
        femaleRadioButton = findViewById(R.id.female);
        otherRadioButton = findViewById(R.id.other);
        btn_save = findViewById(R.id.btn_save);
        setToolBar(toolbar);
        fullname = getIntent().getStringExtra("fullname");
        avatarUrl = getIntent().getStringExtra("avatarUrl");
        address = getIntent().getStringExtra("address");
        phonenumber = getIntent().getStringExtra("phonenumber");
        gender = getIntent().getStringExtra("gender");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");
        date = getIntent().getStringExtra("date");
        userId = getIntent().getStringExtra("userId");

        input_fullname.getEditText().setText(fullname);
        input_date.getEditText().setText(date);
        input_address.getEditText().setText(address);
        input_phonenumber.getEditText().setText(phonenumber);
        input_password.getEditText().setText(password);
        if ("Male".equals(gender)) {
            maleRadioButton.setChecked(true);
        } else if ("Female".equals(gender)) {
            femaleRadioButton.setChecked(true);
        } else if ("Other".equals(gender)) {
            otherRadioButton.setChecked(true);
        }
        dropdownIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("EditProfile", " clicked");
                Toast.makeText(EditProfile.this, "ok", Toast.LENGTH_SHORT).show();
                showDatePickerDialog();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(v);
            }
        });

    }
    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog with the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String formattedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
                        input_date.getEditText().setText(formattedDate);

                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
    private void setToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.toolbar_title);

        if (toolbar != null && toolbarTitle != null) {
            // Customize the toolbar title for this activity
            toolbarTitle.setText("Chỉnh sửa trang cá nhân");
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        // Set a click listener for the navigation icon
        toolbar.setNavigationOnClickListener(v -> {
            // Handle the navigation icon click (e.g., go back)
            Intent intent = new Intent(EditProfile.this, MyProfile.class);
            intent.putExtra("fullname", fullname);
            intent.putExtra("avatarUrl", avatarUrl);
            intent.putExtra("address", address);
            intent.putExtra("phonenumber", phonenumber);
            intent.putExtra("gender", gender);
            intent.putExtra("date", date);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            intent.putExtra("userId", userId);

            startActivity(intent);
        });
    }

    public void update(View view) {
        if (isNameChange() || isAddressChange() || isGenderChange ()|| isDateChange()) {
            Toast.makeText(this, "Data has been update", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Data is same can not update", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isDateChange() {
        if (!date.equals(input_date.getEditText().getText().toString())) {
            reference.child(userId).child("date").setValue(input_date.getEditText().getText().toString());
            date = input_date.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isGenderChange() {
        String selectedGender;

        // Check which radio button is selected
        if (maleRadioButton.isChecked()) {
            selectedGender = "Male";
        } else if (femaleRadioButton.isChecked()) {
            selectedGender = "Female";
        } else if (otherRadioButton.isChecked()) {
            selectedGender = "Other";
        } else {
            return false;
        }

        if (!gender.equals(selectedGender)) {
            // Update gender in Firebase
            reference.child(userId).child("gender").setValue(selectedGender);
            gender = selectedGender;
            return true;
        } else {
            return false;
        }
    }


    private boolean isAddressChange() {
        if (!address.equals(input_address.getEditText().getText().toString())) {
            reference.child(userId).child("address").setValue(input_address.getEditText().getText().toString());
            address = input_address.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }

    private boolean isNameChange() {
        if (!fullname.equals(input_fullname.getEditText().getText().toString())) {
            reference.child(userId).child("fullname").setValue(input_fullname.getEditText().getText().toString());
            fullname = input_fullname.getEditText().getText().toString();
            return true;
        } else {
            return false;
        }
    }


}