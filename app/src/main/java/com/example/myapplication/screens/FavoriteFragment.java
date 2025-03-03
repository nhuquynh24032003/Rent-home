package com.example.myapplication.screens;

import static com.example.myapplication.screens.HomeFragment.userId;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.FavoriteRoom;
import com.example.myapplication.model.Room;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FavoriteFragment extends AppCompatActivity {

    private static RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<FavoriteRoom, FavoriteRoomViewHolder> adapter;
    private static String currentUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_fragment);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference("favoriteRooms");

        if (getIntent() != null) {
            currentUserUid = getIntent().getStringExtra("userId");
            Log.d("FavoriteFragment", "Current user UID: " + currentUserUid);
            displayFavoriteRooms(currentUserUid);
        }
    }

    private void displayFavoriteRooms(String currentUserUid) {
        FirebaseRecyclerOptions<FavoriteRoom> options =
                new FirebaseRecyclerOptions.Builder<FavoriteRoom>()
                        .setQuery(databaseReference.orderByChild("favoriteUserId").equalTo(currentUserUid), FavoriteRoom.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<FavoriteRoom, FavoriteRoomViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FavoriteRoomViewHolder holder, int position, @NonNull FavoriteRoom model) {
                holder.bind(model);
            }

            @NonNull
            @Override
            public FavoriteRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_room_favorite, parent, false);
                return new FavoriteRoomViewHolder(view);
            }
        };

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

    public static class FavoriteRoomViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, descriptionTextView, priceTextView, locationTextView;
        private ImageView roomImageView;

        private ImageButton favoriteImageButton;
        private DatabaseReference favoriteRoomsRef;
        private FavoriteRoom currentFavoriteRoom;
        private boolean isFavorite = true;

        public FavoriteRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            roomImageView = itemView.findViewById(R.id.roomImageView);
            favoriteImageButton = itemView.findViewById(R.id.favoriteImageButton);
            favoriteRoomsRef = FirebaseDatabase.getInstance().getReference("favoriteRooms");

            favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleFavoriteStatus();
                }
            });
        }

        public void bind(FavoriteRoom favoriteRoom) {
            this.currentFavoriteRoom = favoriteRoom;
            if (isFavorite) {
                favoriteImageButton.setImageResource(R.drawable.ic_favorite_filled);
            } else {
                favoriteImageButton.setImageResource(R.drawable.ic_favorite_empty);
            }

            titleTextView.setText(favoriteRoom.getTitle());
            descriptionTextView.setText(favoriteRoom.getDescription());
            priceTextView.setText(favoriteRoom.getPrice() + "");
            locationTextView.setText(favoriteRoom.getLocation());

            if (favoriteRoom.getImageUrl() != null && !favoriteRoom.getImageUrl().isEmpty()) {
                Picasso.get().load(Uri.parse(favoriteRoom.getImageUrl())).into(roomImageView);
            }else {
                roomImageView.setImageResource(R.drawable.home_icon);
            }
        }

        private void toggleFavoriteStatus() {
            isFavorite = !isFavorite;

            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            favoriteImageButton.setImageResource(R.drawable.ic_favorite_empty);
            favoriteRoomsRef.child(currentFavoriteRoom.getRoomId()).removeValue();
            editor.putBoolean(currentFavoriteRoom.getRoomId(), false);
            editor.apply();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}
