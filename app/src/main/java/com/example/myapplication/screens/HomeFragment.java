package com.example.myapplication.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.FavoriteRoom;
import com.example.myapplication.model.Room;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


public class HomeFragment extends Fragment {

    private static RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Room, RoomViewHolder> adapter;
    LinearLayout home;
    FrameLayout formPrice;
    TextView btn_priceFilter;
    ImageView btn_closeForm;
    EditText edt_minPrice;
    EditText edt_maxPrice;
    Button btn_find;
    static String userId;
    public void setuserId(String userId) {
        this.userId = userId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        databaseReference = FirebaseDatabase.getInstance().getReference("rooms");
        btn_priceFilter = view.findViewById(R.id.btn_priceFilter);
        formPrice = view.findViewById(R.id.formPrice);
        home = view.findViewById(R.id.home);
        btn_closeForm = view.findViewById(R.id.btn_closeForm);
        edt_minPrice = view.findViewById(R.id.edt_minPrice);
        edt_maxPrice = view.findViewById(R.id.edt_maxPrice);
        btn_find = view.findViewById(R.id.btn_find);
        TextWatcher priceTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // No action needed during text changes
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Check if either minimum or maximum price is empty
                boolean isMinPriceEmpty = edt_minPrice.getText().toString().trim().isEmpty();
                boolean isMaxPriceEmpty = edt_maxPrice.getText().toString().trim().isEmpty();

                // Enable or disable the "Áp dụng" button based on the conditions
                btn_find.setEnabled(!isMinPriceEmpty && !isMaxPriceEmpty);
            }
        };
//        Log.d("userId", userId);
// Attach the TextWatcher to both minimum and maximum price EditTexts
        edt_minPrice.addTextChangedListener(priceTextWatcher);
        edt_maxPrice.addTextChangedListener(priceTextWatcher);
        btn_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the minimum and maximum prices from the EditText fields
                String minPriceStr = edt_minPrice.getText().toString().trim();
                String maxPriceStr = edt_maxPrice.getText().toString().trim();

                if (!minPriceStr.isEmpty() && !maxPriceStr.isEmpty()) {
                    double minPrice = Double.parseDouble(minPriceStr);
                    double maxPrice = Double.parseDouble(maxPriceStr);

                    // Construct a new query based on the price range
                    FirebaseRecyclerOptions<Room> options =
                            new FirebaseRecyclerOptions.Builder<Room>()
                                    .setQuery(databaseReference.orderByChild("price").startAt(minPrice).endAt(maxPrice), Room.class)
                                    .build();

                    // Create a new adapter with the updated query
                    FirebaseRecyclerAdapter<Room, HomeFragment.RoomViewHolder> newAdapter =
                            new FirebaseRecyclerAdapter<Room, HomeFragment.RoomViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull RoomViewHolder holder, int position, @NonNull Room model) {
                                    holder.bind(model, userId);
                                }

                                @NonNull
                                @Override
                                public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_room, parent, false);
                                    RoomViewHolder viewHolder = new RoomViewHolder(view);
                                    return viewHolder;
                                }
                            };

                    // Set the new adapter to the RecyclerView
                    recyclerView.setAdapter(newAdapter);

                    // Start listening for changes
                    newAdapter.startListening();
                }

                // Hide the price filter form and show the home layout
                formPrice.setVisibility(View.INVISIBLE);
                home.setVisibility(View.VISIBLE);
            }
        });


        btn_closeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formPrice.setVisibility(View.INVISIBLE);
                home.setVisibility(View.VISIBLE);
            }
        });
        btn_priceFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                formPrice.setVisibility(View.VISIBLE);
                home.setVisibility(View.INVISIBLE);
            }
        });
        FirebaseRecyclerOptions<Room> options =
                new FirebaseRecyclerOptions.Builder<Room>()
                        .setQuery(databaseReference.orderByKey(), Room.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Room, HomeFragment.RoomViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RoomViewHolder holder, int position, @NonNull Room model) {
                holder.bind(model, userId);
            }

            @NonNull
            @Override
            public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_room, parent, false);
                RoomViewHolder viewHolder = new RoomViewHolder(view);
                return viewHolder;
            }
        };

        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, descriptionTextView, priceTextView, locationTextView;
        private ImageView roomImageView;

        private ImageButton favoriteImageButton;
        FrameLayout sold;

        private Room room;
        private DatabaseReference favoriteRoomsRef;


        private boolean isFavorite = false;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            roomImageView = itemView.findViewById(R.id.roomImageView);
            favoriteImageButton = itemView.findViewById(R.id.favoriteImageButton);
            sold = itemView.findViewById(R.id.sold);
            favoriteRoomsRef = FirebaseDatabase.getInstance().getReference("favoriteRooms");

            favoriteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleFavoriteStatus();
                }
            });
        }

        public void bind(Room room, String userId) {
            this.room = room;
           // Log.d("RoomViewHolder", "Binding room: " + room.getRoomId());
            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
            isFavorite = sharedPreferences.getBoolean(room.getRoomId(), false);

            if (isFavorite) {
                favoriteImageButton.setImageResource(R.drawable.ic_favorite_filled);
            } else {
                favoriteImageButton.setImageResource(R.drawable.ic_favorite_empty);
            }

            titleTextView.setText(room.getTitle());
            descriptionTextView.setText(room.getDescription());
            priceTextView.setText(room.getPrice() + "");
            locationTextView.setText(room.getLocation());
            if(!room.getStatus()){

                sold.setVisibility(View.VISIBLE);
            }
            else {
                itemView.findViewById(R.id.cardView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(itemView.getContext(), "Item Clicked", Toast.LENGTH_SHORT).show();
                        openRoomDetail(itemView.getContext(), userId);
                    }
                });
            }
            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                Picasso.get().load(Uri.parse(room.getImageUrl())).into(roomImageView);
            }else {
                roomImageView.setImageResource(R.drawable.home_icon);
            }

        }

        private void openRoomDetail(Context context, String userId) {
            Intent intent = new Intent(itemView.getContext(), DetailRoomFragment.class);
           // Log.d("RoomViewHolder", "room" + room.getRoomId());
            intent.putExtra("roomId", room.getRoomId());
            intent.putExtra("title", room.getTitle());
            intent.putExtra("description", room.getDescription());
            intent.putExtra("price", room.getPrice());
            intent.putExtra("location", room.getLocation());
            intent.putExtra("imageUrl", room.getImageUrl());
            intent.putExtra("userId", room.getUserUid());
            intent.putExtra("myId", userId);
            intent.putExtra("acreage", room.getAcreage());
            intent.putExtra("datetime", room.getDatetime());
            itemView.getContext().startActivity(intent);
        }
        private void toggleFavoriteStatus() {
            isFavorite = !isFavorite;

            SharedPreferences sharedPreferences = itemView.getContext().getSharedPreferences("favorites", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isFavorite) {
                favoriteImageButton.setImageResource(R.drawable.ic_favorite_filled);
                FavoriteRoom favoriteRoom = new FavoriteRoom(room, userId);
                favoriteRoomsRef.child(room.getRoomId()).setValue(favoriteRoom);
                editor.putBoolean(room.getRoomId(), true);
            } else {
                favoriteImageButton.setImageResource(R.drawable.ic_favorite_empty);
                favoriteRoomsRef.child(room.getRoomId()).removeValue();
                editor.putBoolean(room.getRoomId(), false);

            }
            editor.apply();
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
}