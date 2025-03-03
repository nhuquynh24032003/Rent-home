package com.example.myapplication.screens;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.Adapter.RoomAdapter;
import com.example.myapplication.R;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class forRentActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String myId;
    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_rent);
        recyclerView = findViewById(R.id.recyclerView);
        myId = getIntent().getStringExtra("myId");
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        Query userOrdersQuery = ordersRef.orderByChild("userId").equalTo(myId);
        userOrdersQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> roomIds = new ArrayList<>();
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null) {
                        roomIds.add(order.getRoomId());
                    }
                }

                if (!roomIds.isEmpty()) {
                    DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
                    roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Room> userRooms = new ArrayList<>();
                            for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                                Room room = roomSnapshot.getValue(Room.class);
                                if (room != null && roomIds.contains(room.getRoomId())) {
                                    userRooms.add(room);
                                }
                            }

                            // Set up RecyclerView with the user's rooms
                            roomAdapter = new RoomAdapter(forRentActivity.this, userRooms, myId);
                            recyclerView.setAdapter(roomAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(forRentActivity.this));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle errors
                        }
                    });
                } else {
                    // Handle the case where the user has no rented rooms
                    // For example, show a message or handle it according to your app's logic
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
