package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Report;
import com.example.myapplication.model.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.threeten.bp.Duration;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailRoomFragment extends AppCompatActivity {
        TextView tv_fullname;
        CircleImageView profile_image;
        Button btn_profile;
        ImageView btn_report;
        RatingBar ratingBar;
        TextView tv_numReviews;
        Button btn_rent;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detail_room_fragment);
            tv_fullname = findViewById(R.id.tv_fullname);
            profile_image = findViewById(R.id.profile_image);
            btn_profile = findViewById(R.id.btn_profile);
            btn_report = findViewById(R.id.btn_report);
            ratingBar = findViewById(R.id.ratingBar);
            tv_numReviews = findViewById(R.id.tv_numReviews);
            btn_rent = findViewById(R.id.btn_rent);
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra("roomId")) {

                String userId = intent.getStringExtra("userId");
                String myId = intent.getStringExtra("myId");
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
                if (userId.equals(myId)) {
                    btn_report.setVisibility(View.GONE);
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(myId);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // User found, retrieve details
                                String fullname = dataSnapshot.child("fullname").getValue(String.class);
                                String avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);
                                String email = dataSnapshot.child("email").getValue(String.class);
                                String phonenumber = dataSnapshot.child("phonenumber").getValue(String.class);
                                String address = dataSnapshot.child("address").getValue(String.class);
                                String gender = dataSnapshot.child("gender").getValue(String.class);
                                String date = dataSnapshot.child("date").getValue(String.class);
                                String password = dataSnapshot.child("password").getValue(String.class);
                                String userId = dataSnapshot.child("userId").getValue(String.class);
                                btn_profile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(DetailRoomFragment.this, MyProfile.class);
                                        intent.putExtra("fullname", fullname);
                                        intent.putExtra("avatarUrl", avatarUrl);
                                        intent.putExtra("email", email);
                                        intent.putExtra("phonenumber", phonenumber);
                                        intent.putExtra("address", address);
                                        intent.putExtra("gender", gender);
                                        intent.putExtra("date", date);
                                        intent.putExtra("password", password);
                                        intent.putExtra("userId", userId);
                                        startActivity(intent);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle the error
                        }
                    });

                }
                else { btn_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(DetailRoomFragment.this, ProfileActivity.class);
                            intent.putExtra("userId", userId);
                            intent.putExtra("myId", myId);
                            startActivity(intent);
                        }
                    });}

                // Retrieve user details from Firebase using userId
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // User found, retrieve details
                            String fullname = dataSnapshot.child("fullname").getValue(String.class);
                            String userImage = dataSnapshot.child("avatarUrl").getValue(String.class);

                            // Set fullname to tv_fullname
                            if (tv_fullname != null) {
                                tv_fullname.setText(fullname != null ? fullname : "");
                            }

                            // Load user image into profile_image
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

                String roomId = intent.getStringExtra("roomId");
                String title = intent.getStringExtra("title");
                double price = intent.getDoubleExtra("price", 0.0);
                String description = intent.getStringExtra("description");
                String location = intent.getStringExtra("location");
                String imageUrl = intent.getStringExtra("imageUrl");
                double acreage = intent.getDoubleExtra("acreage", 0.0);
                String datetime = intent.getStringExtra("datetime");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss z yyyy");
                ZonedDateTime zonedDateTime = ZonedDateTime.parse(datetime, formatter);
                String formattedDatetime = formatTimeAgo(zonedDateTime);

                btn_rent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentRent = new Intent(DetailRoomFragment.this, ConfirmInfomationActivity.class);
                        intentRent.putExtra("userId", userId);
                        intentRent.putExtra("myId", myId);
                        intentRent.putExtra("roomId", roomId);
                        startActivity(intentRent);
                    }
                });

                ImageView roomImageView = findViewById(R.id.roomImageView);
                TextView titleTextView = findViewById(R.id.titleTextView);
                TextView priceTextView = findViewById(R.id.priceTextView);
                TextView descriptionTextView = findViewById(R.id.descriptionTextview);
                TextView locationTextView = findViewById(R.id.locationTextView);
                TextView tv_acreage = findViewById(R.id.tv_acreage);
                TextView tv_time = findViewById(R.id.tv_time);

                btn_report.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkIfAlreadyReported(roomId, myId, userId);
                    }
                });
                if (tv_acreage != null) {
                    tv_acreage.setText(String.valueOf(acreage));
                }
                if (tv_time != null) {
                    tv_time.setText(formattedDatetime);
                }


                if (roomImageView != null && imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(Uri.parse(imageUrl)).into(roomImageView);
                }

                if (titleTextView != null) {
                    titleTextView.setText(title);
                }

                if (priceTextView != null) {
                    priceTextView.setText(String.valueOf(price));
                }

                if (descriptionTextView != null) {
                    descriptionTextView.setText(description != null ? description : "");
                }

                if (locationTextView != null) {
                    locationTextView.setText(location);
                }
            }
        }
    // Your formatTimeAgo method
    private String formatTimeAgo(ZonedDateTime zonedDateTime) {
        ZonedDateTime now = ZonedDateTime.now();
        Duration duration = Duration.between(zonedDateTime, now);
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) {
            return "Vừa xong";
        } else if (hours < 1) {
            return minutes + " phút trước";
        } else if (hours < 24) {
            return hours + " giờ trước";
        } else if (days < 7) {
            return days + " ngày trước";
        } else {
            LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return localDateTime.format(outputFormatter);
        }
    }
    private void checkIfAlreadyReported(String roomId, String myId, String userId) {
        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");

        // Query to check if the current user (myId) has already reported the specified roomId
        Query query = reportsRef.orderByChild("roomId").equalTo(roomId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterate through the reports to check if myId has reported this room
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Report report = snapshot.getValue(Report.class);
                        if (report != null && report.getMyId().equals(myId)) {
                            // The user has already reported, show a Toast
                            Toast.makeText(getApplicationContext(), "You have already reported this room", Toast.LENGTH_SHORT).show();
                            return; // No need to check further
                        }
                    }

                    // If myId is not found in the reports for this room, navigate to the ReportActivity
                    navigateToReportActivity(roomId, myId, userId);
                } else {
                    // No reports for this room, navigate to the ReportActivity
                    navigateToReportActivity(roomId, myId, userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }


    private void navigateToReportActivity(String roomId, String myId, String userId) {
        Intent intent = new Intent(DetailRoomFragment.this, ReportActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("myId", myId);
        intent.putExtra("roomId", roomId);
        startActivity(intent);
    }

}