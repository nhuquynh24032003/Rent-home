package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.myapplication.R;
import com.example.myapplication.model.Room;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

public class EditRoomActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText titleEditText, descriptionEditText, priceEditText, locationEditText;
    private ImageView roomImageView;
    private Button chooseImageButton, saveButton;
    private String roomId;
    private String currentUserUid;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_room);

        // Ánh xạ các view
        titleEditText = findViewById(R.id.titleEditText_edit);
        descriptionEditText = findViewById(R.id.descriptionEditText_edit);
        priceEditText = findViewById(R.id.priceEditText_edit);
        locationEditText = findViewById(R.id.locationEditText_edit);
        roomImageView = findViewById(R.id.roomImageView_edit);
        chooseImageButton = findViewById(R.id.chooseImageButton_edit);
        saveButton = findViewById(R.id.saveButton_edit);

        roomId = getIntent().getStringExtra("roomId");
        currentUserUid = getIntent().getStringExtra("userID");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        double price = getIntent().getDoubleExtra("price", 0);
        String location = getIntent().getStringExtra("location");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        titleEditText.setText(title);
        descriptionEditText.setText(description);
        priceEditText.setText(String.valueOf(price));
        locationEditText.setText(location);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(Uri.parse(imageUrl)).into(roomImageView);
        } else {
            roomImageView.setImageResource(R.drawable.home_icon);
        }

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRoom();
            }
        });
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
        }
    }

    private void loadImage(Uri imageUri) {
        Picasso.get().load(imageUri).into(roomImageView);
    }

    private void saveRoom() {
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        String priceString = priceEditText.getText().toString();
        String location = locationEditText.getText().toString();

        if (title.isEmpty() || description.isEmpty() || priceString.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceString);

        if (price < 0) {
            Toast.makeText(this, "Price cannot be negative", Toast.LENGTH_SHORT).show();
            return;
        }

//        Room room = new Room(roomId, title, description, price, location, selectedImageUri.toString(), currentUserUid);

     // DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("rooms").child(roomId);
     //   databaseReference.setValue(room);

        Toast.makeText(this, "Room updated successfully", Toast.LENGTH_SHORT).show();

        finish();
    }
}
