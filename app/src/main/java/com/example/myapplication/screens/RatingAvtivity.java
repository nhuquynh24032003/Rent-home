package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.example.myapplication.R;
import com.example.myapplication.model.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RatingAvtivity extends AppCompatActivity {
    TextInputLayout input_comment;
    RatingBar ratingBar;
    Button btn_review;
    String userId;
    String myId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_avtivity);
        input_comment = findViewById(R.id.input_comment);
        ratingBar = findViewById(R.id.ratingBar);
        btn_review = findViewById(R.id.btn_review);
        userId = getIntent().getStringExtra("userId");
        myId = getIntent().getStringExtra("myId");

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the review comment
                String comment = input_comment.getEditText().getText().toString();

                // Get the rating value
                float rating = ratingBar.getRating();

                // Create a unique reviewId (you can use the push key from Firebase)
                String reviewId = FirebaseDatabase.getInstance().getReference("reviews").push().getKey();

                // Create a Review object
                Review review = new Review(reviewId, userId, myId, comment, rating);

                // Save the review to the "reviews" node in the database
                DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("reviews").child(reviewId);
                reviewRef.setValue(review).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RatingAvtivity.this, ProfileActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("myId", myId);
                            startActivity(intent);
                            finish();
                        } else {
                            // Failed to add review
                        }
                    }
                });
            }
        });
    }
}