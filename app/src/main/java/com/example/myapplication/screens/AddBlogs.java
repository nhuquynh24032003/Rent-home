package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBlogs extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private TextInputLayout titleEditText, descriptionEditText, priceEditText, locationEditText, input_deposite, input_acreage;
    private ImageView roomImageView;
    private Button saveButton;
    private DatabaseReference databaseReference;
    private Uri selectedImageUri;
    Spinner furnitureSpinner;
    StorageReference storageReference;
    String imageUrl;
    StringBuilder selectedFurniture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blogs);

        databaseReference = FirebaseDatabase.getInstance().getReference("rooms");
        storageReference = FirebaseStorage.getInstance().getReference("room");
        titleEditText = findViewById(R.id.input_title);
        descriptionEditText = findViewById(R.id.input_description);
        priceEditText = findViewById(R.id.input_price);
        locationEditText = findViewById(R.id.input_address);
        roomImageView = findViewById(R.id.btn_upload);
        saveButton = findViewById(R.id.btn_save);
        furnitureSpinner = findViewById(R.id.furnitureSpinner);
        input_deposite = findViewById(R.id.input_deposit);
        input_acreage = findViewById(R.id.input_acreage);
        roomImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveRoom();
            }
        });
        furnitureSpinner = findViewById(R.id.furnitureSpinner);

        // Debug log to check if furnitureSpinner is not null
        Log.d("SpinnerDebug", "furnitureSpinner: " + (furnitureSpinner != null ? "not null" : "null"));

        if (furnitureSpinner != null) {
            String[] furnitureArray = getResources().getStringArray(R.array.furniture_array);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, furnitureArray);
            furnitureSpinner.setAdapter(adapter);

            // Debug log to check if onItemSelectedListener is set
            Log.d("SpinnerDebug", "onItemSelectedListener: " + (furnitureSpinner.getOnItemSelectedListener() != null ? "set" : "not set"));
            selectedFurniture = new StringBuilder();
            furnitureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Log.d("SelectedFurniture", "Item selected at position: " + position);
                    String selectedFurnitureItem = (String) parentView.getItemAtPosition(position);

                    if (selectedFurnitureItem != null) {
                        selectedFurniture.setLength(0);
                        selectedFurniture.append(selectedFurnitureItem);
                    }

                    Log.d("SelectedFurniture", "Selected: " + selectedFurniture.toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    Log.d("SelectedFurniture", "Nothing selected");
                }
            });
        }
    }
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            loadImage(selectedImageUri);
            uploadImageToFirebase(selectedImageUri);
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    updateImage(uri.toString());
                });
            }).addOnFailureListener(e -> {
                // Handle the failure
            });
        }
    }
    private void updateImage(String imageurl) {
        // Update the user's profile in Firebase Realtime Database or Firestore with the new image URL
        imageUrl = imageurl;
    }
    private void loadImage(Uri imageUri) {
        Picasso.get().load(imageUri).into(roomImageView);
    }


    private void saveRoom() {

        String title = titleEditText.getEditText().getText().toString().trim();
        String description = descriptionEditText.getEditText().getText().toString().trim();
        String priceStr = priceEditText.getEditText().getText().toString().trim();
        String location = locationEditText.getEditText().getText().toString().trim();
        String depositStr = input_deposite.getEditText().getText().toString().trim();
        String acreageStr = input_acreage.getEditText().getText().toString().trim();
        if (title.isEmpty() || description.isEmpty() || priceStr.isEmpty() || location.isEmpty()) {
            Toast.makeText(AddBlogs.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        double deposite = Double.parseDouble(depositStr);
        double acreage = Double.parseDouble(acreageStr);
        String userId = getIntent().getStringExtra("userId");


        if (userId != null) {
            String roomId = databaseReference.push().getKey();
            Date currentDateTime = new Date();

            String formattedDateTime = currentDateTime.toString();

            Room room = new Room(roomId, title, description, price, deposite,acreage, location, selectedFurniture.toString(), imageUrl, userId, formattedDateTime, true);
            databaseReference.child(roomId).setValue(room).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(AddBlogs.this, "Room saved successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddBlogs.this, "Failed to save room", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}