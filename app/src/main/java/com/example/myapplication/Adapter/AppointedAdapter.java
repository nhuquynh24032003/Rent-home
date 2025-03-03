package com.example.myapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Appointment;
import com.example.myapplication.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AppointedAdapter extends RecyclerView.Adapter<AppointedAdapter.ViewHolder> {
    private Context mContext;

    private List<Appointment> mAppointments;
    String myId;

    public AppointedAdapter(Context mContext, List<Appointment> mAppointments, String myId) {
        this.mAppointments = mAppointments;
        this.mContext = mContext;
        this.myId = myId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_appointment, parent, false);
        return new AppointedAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = mAppointments.get(position);

        // Assuming you have a "Users" node in your Firebase database
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        usersReference.child(appointment.getReceiver()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User found, retrieve the name
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        holder.tv_fullname.setText(user.getFullname());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
        holder.tv_datetime.setText(appointment.getDatetime());
    }

    @Override
    public int getItemCount() {
        return mAppointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_fullname;
        public TextView tv_datetime;
        public ViewHolder (View itemView) {
            super(itemView);
            tv_fullname = itemView.findViewById(R.id.tv_fullname);
            tv_datetime = itemView.findViewById(R.id.tv_datetime);
        }
    }
}
