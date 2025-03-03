package com.example.myapplication.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.Review;
import com.example.myapplication.model.Room;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView tv_fullname, tv_email, tv_address, tv_phonenumber, btn_message, tv_numReviews;
    private CircleImageView profile_image;
    private String userId, myId;
    private RecyclerView recyclerView;
    private ProfileBlogsAdapter adapter;
    TextView btn_rating;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_fullname = findViewById(R.id.tv_fullname);
        tv_email = findViewById(R.id.tv_email);
        tv_address = findViewById(R.id.tv_address);
        tv_phonenumber = findViewById(R.id.tv_phonenumber);
        btn_message = findViewById(R.id.btn_message);
        profile_image = findViewById(R.id.profile_image);
        recyclerView = findViewById(R.id.recyclerView);
        btn_rating = findViewById(R.id.btn_rating);
        ratingBar = findViewById(R.id.ratingBar);
        tv_numReviews = findViewById(R.id.tv_numReviews);
        userId = getIntent().getStringExtra("userId");
        myId = getIntent().getStringExtra("myId");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullname = dataSnapshot.child("fullname").getValue(String.class);
                    String userImage = dataSnapshot.child("avatarUrl").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phonenumber = dataSnapshot.child("phonenumber").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);

                    if (tv_fullname != null) {
                        tv_fullname.setText(fullname != null ? fullname : "");
                    }
                    if (tv_email != null) {
                        tv_email.setText(email != null ? email : "");
                    }
                    if (tv_phonenumber != null) {
                        tv_phonenumber.setText(phonenumber != null ? phonenumber : "");
                    }
                    if (tv_address != null) {
                        tv_address.setText(address != null ? address : "");
                    }
                    if (profile_image != null && userImage != null && !userImage.isEmpty()) {
                        Picasso.get().load(Uri.parse(userImage)).into(profile_image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Orders");
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);

                        // Check if the order matches the condition
                        if (order != null && order.getUserId().equals(userId) && order.getMyId().equals(myId)) {
                            // Show the btn_rating if the condition is true
                            btn_rating.setVisibility(View.VISIBLE);

                            btn_rating.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                  Intent intent = new Intent(ProfileActivity.this, RatingAvtivity.class);
                                  intent.putExtra("userId", userId);
                                  intent.putExtra("myId", myId);
                                  startActivity(intent);
                                }
                            });

                            // You can break the loop if you want to stop checking after the first matching order
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });

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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });

        setupRecyclerView();

        btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MessageActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("myId", myId);
                startActivity(intent);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<Room> options =
                new FirebaseRecyclerOptions.Builder<Room>()
                        .setQuery(FirebaseDatabase.getInstance().getReference("rooms").orderByChild("userUid").equalTo(userId), Room.class)
                        .build();

        adapter = new ProfileBlogsAdapter(options);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
