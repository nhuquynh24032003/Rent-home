package com.example.myapplication.screens;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Room;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class ProfileBlogsAdapter extends FirebaseRecyclerAdapter<Room, ProfileBlogsAdapter.RoomViewHolder> {

    public ProfileBlogsAdapter(@NonNull FirebaseRecyclerOptions<Room> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RoomViewHolder holder, int position, @NonNull Room model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_room_edit_delete, parent, false);
        return new RoomViewHolder(view);
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView, descriptionTextView, priceTextView, locationTextView;
        private ImageView roomImageView;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView_edit_delete);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView_edit_delete);
            priceTextView = itemView.findViewById(R.id.priceTextView_edit_delete);
            locationTextView = itemView.findViewById(R.id.locationTextView_edit_delete);
            roomImageView = itemView.findViewById(R.id.roomImageView_edit_delete);
        }

        public void bind(Room room) {
            if (room != null) {
                titleTextView.setText(room.getTitle());
                descriptionTextView.setText(room.getDescription());
                priceTextView.setText(String.valueOf(room.getPrice()));
                locationTextView.setText(room.getLocation());

                if (roomImageView != null) {
                    if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                        Picasso.get().load(Uri.parse(room.getImageUrl())).into(roomImageView);
                    } else {
                        roomImageView.setImageResource(R.drawable.home_icon);
                    }
                }
            }
        }
    }
}
