package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.myapplication.R;
import com.example.myapplication.model.Order;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ConfirmInfomationActivity extends AppCompatActivity {
    String userId;
    String myId;
    String roomId;
    TextInputLayout input_fullname, input_address, input_phonenumber, input_addressRoom, input_price, input_deposite, input_furniture;
    RadioGroup radioGroup;
    Button btn_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_infomation);
        userId = getIntent().getStringExtra("userId");
        myId = getIntent().getStringExtra("myId");
        roomId = getIntent().getStringExtra("roomId");

        input_fullname = findViewById(R.id.input_fullname);
        input_address = findViewById(R.id.input_address);
        input_phonenumber = findViewById(R.id.input_phonenumber);
        input_addressRoom = findViewById(R.id.input_addressRoom);
        input_price = findViewById(R.id.input_price);
        input_deposite = findViewById(R.id.input_deposit);
        input_furniture = findViewById(R.id.input_furniture);
        radioGroup = findViewById(R.id.radioGroup);
        btn_next = findViewById(R.id.btn_next);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(myId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User found, retrieve details
                    String fullname = dataSnapshot.child("fullname").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);
                    String phonenumber = dataSnapshot.child("phonenumber").getValue(String.class);

                    input_fullname.getEditText().setText(fullname);
                    input_address.getEditText().setText(address);
                    input_phonenumber.getEditText().setText(phonenumber);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomId);
        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User found, retrieve details
                    String addressRoom = dataSnapshot.child("location").getValue(String.class);
                    Double price = dataSnapshot.child("price").getValue(Double.class);
                    String priceStr = String.valueOf(price);
                    Double deposite = dataSnapshot.child("deposite").getValue(Double.class);
                    String depositeStr = String.valueOf(deposite);
                    String furniture = dataSnapshot.child("furniture").getValue(String.class);
                    input_addressRoom.getEditText().setText(addressRoom);
                    input_price.getEditText().setText(priceStr);
                    input_deposite.getEditText().setText(depositeStr);
                    input_furniture.getEditText().setText(furniture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                // Step 2: Extract methodPayment from the selected RadioButton
                String methodPayment = getMethodPayment(selectedRadioButtonId);
                Order order = new Order(
                        FirebaseDatabase.getInstance().getReference("orders").push().getKey(),
                        userId,
                        myId,
                        roomId,
                        methodPayment,
                        false
                );
                DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
                ordersRef.child(order.getOrderId()).setValue(order);
                Intent intent = new Intent(ConfirmInfomationActivity.this, rentedRoomActivity.class);
                intent.putExtra("myId", myId);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
            }
        });
    }
    private String getMethodPayment(int selectedRadioButtonId) {
        if (selectedRadioButtonId == R.id.tienmat) {
            return "Tiền mặt";
        } else if (selectedRadioButtonId == R.id.chuyenkhoan) {
            return "Chuyển khoản";
        } else {
            return ""; // Handle the case when no RadioButton is selected
        }
    }
}