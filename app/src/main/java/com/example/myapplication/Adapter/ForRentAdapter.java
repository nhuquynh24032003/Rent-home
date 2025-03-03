package com.example.myapplication.Adapter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Appointment;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.Report;
import com.example.myapplication.model.Room;
import com.example.myapplication.model.User;
import com.example.myapplication.screens.MessageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForRentAdapter extends RecyclerView.Adapter<ForRentAdapter.ViewHolder> {
    private Context mContext;

    private List<Room> mRooms;
    String myId;
    String userId;

    public ForRentAdapter(Context mContext, List<Room> mRooms, String myId) {
        this.mRooms = mRooms;
        this.mContext = mContext;
        this.myId = myId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_item_room, parent, false);
        return new ForRentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = mRooms.get(position);
        holder.titleTextView.setText(room.getTitle());
        holder.titleTextView.setText(room.getTitle());
        holder.descriptionTextView.setText(room.getDescription());
        holder.priceTextView.setText(String.valueOf(room.getPrice()));
        holder.locationTextView.setText(room.getLocation());
        Glide.with(mContext).load(room.getImageUrl()).into(holder.roomImageView);
        userId = room.getUserUid();

    }

    public int getItemCount() {
        return mRooms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView, descriptionTextView, priceTextView, locationTextView;
        ImageView roomImageView, btn_pay, btn_paid, btn_wait;
        ImageButton favoriteImageButton;


        public ViewHolder (View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            locationTextView = itemView.findViewById(R.id.locationTextView);
            roomImageView = itemView.findViewById(R.id.roomImageView);
            favoriteImageButton = itemView.findViewById(R.id.favoriteImageButton);

            btn_pay.setVisibility(View.GONE);
            favoriteImageButton.setVisibility(View.GONE);
            btn_paid.setVisibility(View.GONE);
        }
    }
}
