package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Text;

public class MyProfile extends AppCompatActivity {
    private StorageReference storageReference;
    Toolbar toolbar;
    TextView tv_fullname;
    TextView tv_email;
    TextView tv_phonenumber;
    TextView tv_address;
    String fullname;
    String email;
    String avatarUrl;
    String address;
    String phonenumber;
    String gender;
    String date;
    String password;
    String userId;
    TextView btn_editProfile;
    ImageView profile_image;
    RatingBar ratingBar;
    private Uri selectedImageUri;
    TextView btn_shareProfile;
    TextView tv_numReviews;
    private static final int PICK_IMAGE_REQUEST = 1;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        reference = FirebaseDatabase.getInstance().getReference("users");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        ratingBar = findViewById(R.id.ratingBar);
        tv_numReviews =  findViewById(R.id.tv_numReviews);
        toolbar = findViewById(R.id.toolbar);
        tv_fullname = findViewById(R.id.tv_fullname);
        tv_email = findViewById(R.id.tv_email);
        tv_phonenumber = findViewById(R.id.tv_phonenumber);
        tv_address = findViewById(R.id.tv_address);
        btn_editProfile = findViewById(R.id.btn_editProfile);
        profile_image = findViewById(R.id.profile_image);
        fullname = getIntent().getStringExtra("fullname");
        email = getIntent().getStringExtra("email");
        phonenumber = getIntent().getStringExtra("phonenumber");
        avatarUrl = getIntent().getStringExtra("avatarUrl");
        address = getIntent().getStringExtra("address");
        gender = getIntent().getStringExtra("gender");
        date = getIntent().getStringExtra("date");
        password = getIntent().getStringExtra("password");
        userId = getIntent().getStringExtra("userId");
        storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        setToolBar(toolbar, fullname);

        tv_fullname.setText(fullname);
        tv_email.setText(email);
        tv_phonenumber.setText(phonenumber);
        tv_address.setText(address);
        Picasso.get().load(avatarUrl).into(profile_image);
        DatabaseReference reviewsRef = FirebaseDatabase.getInstance().getReference("reviews");
        Query userReviewsQuery = reviewsRef.orderByChild("userId").equalTo(userId);
        userReviewsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalRating = 0;
                int numReviews = 0;

                for (DataSnapshot reviewSnapshot : dataSnapshot.getChildren()) {
                    Review review = reviewSnapshot.getValue(Review.class);
                    if (review != null) {
                        totalRating += review.getRating();
                        numReviews++;
                    }
                }

                if (numReviews > 0) {
                    float averageRating = (float) totalRating / numReviews;
                    ratingBar.setRating(averageRating);
                    String numReviewsText = String.format("(%d đánh giá)", numReviews);
                    tv_numReviews.setText(numReviewsText);
                    Log.d("rating", "rate " + averageRating);
                    Log.d("num", "num " + numReviews);
                }
                else {
                    ratingBar.setRating(0);
                    tv_numReviews.setText("(0 đánh giá)");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });

       btn_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfie(fullname, email, phonenumber, avatarUrl, address, gender, date, password, userId);

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
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
            uploadImageToFirebase(selectedImageUri);
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = storageReference.child(System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Update the user's profile with the new image URL
                    updateProfileImage(uri.toString());
                });
            }).addOnFailureListener(e -> {
                // Handle the failure
            });
        }
    }
    private void updateProfileImage(String imageUrl) {
        // Update the user's profile in Firebase Realtime Database or Firestore with the new image URL
        reference.child(userId).child("avatarUrl").setValue(imageUrl);
        avatarUrl = imageUrl;
    }
    private void loadImage(Uri imageUri) {
        Picasso.get().load(imageUri).into(profile_image);
    }
    private void editProfie(String fullname, String email, String phonenumber, String avatarUrl, String address, String gender, String date, String password, String userId) {
        Intent intent = new Intent(MyProfile.this, EditProfile.class);
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
    }
    private void setToolBar(Toolbar toolbar, String fullname) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setTitle("");
        TextView toolbarTitle = findViewById(R.id.toolbar_title);

        if (toolbar != null && toolbarTitle != null) {
            // Customize the toolbar title for this activity
            toolbarTitle.setText(fullname);
            setSupportActionBar(toolbar);
        }
        // Set a custom navigation icon (replace with your own icon)
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        // Set a click listener for the navigation icon
        toolbar.setNavigationOnClickListener(v -> {
            // Handle the navigation icon click (e.g., go back)
            Intent intent = new Intent(MyProfile.this, Dashboard.class);
            intent.putExtra("fullname", fullname);
            intent.putExtra("avatarUrl", avatarUrl);
            intent.putExtra("address", address);
            intent.putExtra("phonenumber", phonenumber);
            intent.putExtra("gender", gender);
            intent.putExtra("date", date);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            intent.putExtra("userId", userId);
            intent.putExtra("goToAccountFragment", true);

            startActivity(intent);
        });
    }
}