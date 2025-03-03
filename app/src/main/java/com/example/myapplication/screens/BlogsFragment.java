package com.example.myapplication.screens;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

public class BlogsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Room, RoomViewHolder> adapter;
    private static String currentUserUid;

    public static BlogsFragment newInstance(String currentUserUid) {
        BlogsFragment fragment = new BlogsFragment();
        Bundle args = new Bundle();
        args.putString("userId", currentUserUid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUserUid = getArguments().getString("userId");
        } else {
            currentUserUid = "defaultUserId";
        }

        Log.d("Debug", "currentUserUid: " + currentUserUid);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blogs, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        databaseReference = FirebaseDatabase.getInstance().getReference("rooms");

        FirebaseRecyclerOptions<Room> options =
                new FirebaseRecyclerOptions.Builder<Room>()
                        .setQuery(databaseReference.orderByChild("userUid").equalTo(currentUserUid), Room.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Room, RoomViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RoomViewHolder holder, int position, @NonNull Room model) {
                try {
                    holder.bind(model);
                } catch (Exception e) {
                    Log.e("Debug", "Error binding data: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @NonNull
            @Override
            public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_room_edit_delete, parent, false);
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
            } else {
                Log.e("Debug", "Room is null");
            }

         //   ImageButton editButton = itemView.findViewById(R.id.editImageButton_edit_delete);
           /* editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), EditRoomActivity.class);
                    intent.putExtra("roomId", room.getRoomId());
                    intent.putExtra("title", room.getTitle());
                    intent.putExtra("description", room.getDescription());
                    intent.putExtra("price", room.getPrice());
                    intent.putExtra("location", room.getLocation());
                    intent.putExtra("userID", currentUserUid);
                    itemView.getContext().startActivity(intent);
                }
            });
*/
            ImageButton deleteButton = itemView.findViewById(R.id.deleteImageButton_edit_delete);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteConfirmationDialog(room.getRoomId());
                }
            });
        }

        private void showDeleteConfirmationDialog(String roomId) {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Delete Room");
            builder.setMessage("Are you sure you want to delete this room?");

            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteRoom(roomId);
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        private void deleteRoom(String roomId) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("rooms").child(roomId);
            databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(itemView.getContext(), "Room deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(itemView.getContext(), "Failed to delete room", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}