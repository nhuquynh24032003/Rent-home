package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.Appointment;
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
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private Context mContext;

    private List<Appointment> mAppointments;
    String myId;

    public AppointmentAdapter(Context mContext, List<Appointment> mAppointments, String myId) {
        this.mAppointments = mAppointments;
        this.mContext = mContext;
        this.myId = myId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_appointment, parent, false);
        return new AppointmentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = mAppointments.get(position);

        // Assuming you have a "Users" node in your Firebase database
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        usersReference.child(appointment.getSender()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        DatabaseReference appointmentsReference = FirebaseDatabase.getInstance().getReference("Appointment");

        if (appointment.getStatus()) {
            // Status is true, set btn_accept to invisible
            holder.btn_accept.setVisibility(View.VISIBLE);
            holder.tv_text.setText("Xác nhận thanh toán");
            holder.tv_text.setVisibility(View.VISIBLE);
            holder.btn_cancle.setVisibility(View.VISIBLE);
            holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(appointment.getOrderId());
                    DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("Appointment").child(appointment.getId());
                    // Assuming you want to set statusPayment to true, change the value accordingly
                    Map<String, Object> updateMap = new HashMap<>();
                    updateMap.put("paymentStatus", true);

                    ordersRef.updateChildren(updateMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("UpdateStatus", "StatusPayment updated successfully");
                                        // Handle success, if needed
                                        // Now, delete the corresponding appointment
                                        appointmentsRef.removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("DeleteAppointment", "Appointment deleted successfully");
                                                            // Handle success, if needed
                                                        } else {
                                                            Log.e("DeleteAppointment", "Error deleting appointment: " + task.getException());
                                                            // Handle error
                                                        }
                                                    }
                                                });
                                    } else {
                                        Log.e("UpdateStatus", "Error updating statusPayment: " + task.getException());
                                        // Handle error
                                    }
                                }
                            });
                }
            });
        } else {
            // Status is false, set all buttons to visible
            holder.btn_accept.setVisibility(View.VISIBLE);
            holder.btn_cancle.setVisibility(View.VISIBLE);
            holder.tv_text.setVisibility(View.VISIBLE);
            holder.btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptToAddAppointment(appointment.getSender(), appointment.getReceiver(), appointment.getDatetime(), appointment);
                    sendFCMNotification("Hệ thống", "Yêu cầu thuê phòng của bạn đã bị huỷ", appointment.getSender());
                }
            });
            holder.btn_cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Remove the appointment from the database
                    appointmentsReference.child(appointment.getId()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        // Show a confirmation message to the user
                                        Toast.makeText(mContext, "Appointment canceled", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Handle the error
                                        Log.e("Firebase", "Error canceling appointment", task.getException());
                                        Toast.makeText(mContext, "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    appointmentsReference.child(appointment.getSender()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        // Show a confirmation message to the user
                                        Toast.makeText(mContext, "Appointment canceled", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Handle the error
                                        Log.e("Firebase", "Error canceling appointment", task.getException());
                                        Toast.makeText(mContext, "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            });
        }
    }

    private void updateStatus(Appointment appointment, boolean newStatus) {
        // Assuming you have a "Appointment" node in your Firebase database
        DatabaseReference appointmentsReference = FirebaseDatabase.getInstance().getReference("Appointment");

        appointmentsReference.child(appointment.getId()).child("status").setValue(newStatus);
    }
    private void checkExistingAppointments(String sender, String datetime, ExistingAppointmentsCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Appointment");

        // Parse the appointment time
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        try {
            Date appointmentTime = sdf.parse(datetime);

            // Get the time 1 hour before the appointment
            Calendar startTime = Calendar.getInstance();
            startTime.setTime(appointmentTime);
            startTime.add(Calendar.HOUR_OF_DAY, -1);

            // Get the time 1 hour after the appointment
            Calendar endTime = Calendar.getInstance();
            endTime.setTime(appointmentTime);
            endTime.add(Calendar.HOUR_OF_DAY, 1);

            Query existingAppointmentsQuery = reference.orderByChild("datetime");

            existingAppointmentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isConflict = false;

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Appointment existingAppointment = snapshot.getValue(Appointment.class);

                        if (existingAppointment != null) {
                            // Parse the existing appointment time
                            try {
                                Date existingTime = sdf.parse(existingAppointment.getDatetime());

                                // Check for conflicts
                                if (existingTime.after(startTime.getTime()) && existingTime.before(endTime.getTime()) &&
                                        existingAppointment.getSender().equals(sender) && existingAppointment.getStatus()) {
                                    isConflict = true;
                                    break;
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    // Callback to inform the result
                    callback.onCheckComplete(isConflict);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error checking existing appointments", error.toException());
                }
            });

        } catch (ParseException e) {
            // Handle the parsing exception, e.g., show an error to the user
            Log.e("Date Parsing Error", "Error parsing the appointment datetime: " + datetime);
            e.printStackTrace();
        }
    }

    private interface ExistingAppointmentsCallback {
        void onCheckComplete(boolean isConflict);
    }
    private void attemptToAddAppointment(String sender, String receiver, String datetime, Appointment appointment) {
        // Check for conflicts for the sender
        checkExistingAppointments(sender, datetime, new ExistingAppointmentsCallback() {
            @Override
            public void onCheckComplete(boolean isSenderConflict) {
                if (!isSenderConflict) {
                    // No conflict for the sender, check for conflicts for the receiver
                    checkExistingAppointments(receiver, datetime, new ExistingAppointmentsCallback() {
                        @Override
                        public void onCheckComplete(boolean isReceiverConflict) {
                            if (!isReceiverConflict) {
                                // No conflict for the receiver as well, add the appointment
                             updateStatus(appointment, true);
                            } else {
                                // Show a message to the user about the conflict for the receiver
                                Toast.makeText(mContext, "Đối phương đã có lịch hẹn khác! Vui lòng huỷ hẹn", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Show a message to the user about the conflict for the sender
                    Toast.makeText(mContext, "Bạn đã có lịch hẹn! Vui lòng huỷ lịch hẹn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void sendFCMNotification(String sent, String message, String receiverToken) {
        // Use Firebase Cloud Messaging to send a notification
        // You need to implement your FCM logic here
        // This is just a basic example, you may need to customize it based on your server setup and requirements

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        // Replace "YOUR_SERVER_KEY" with your actual FCM server key
        String serverKey = "AAAA8xJdtKI:APA91bGE5O5ubV2YtrmUzHl8LHuyVkdTsnKeAf85Y0mP7ham8whA4xS2clEEIthW1GXJx8tBXqUzpe9pVGkymX2Owg8GedT1VwCk99fsIxwNYjRuHyavlEdcJUy7kuTQ_M9c6VY9h1UT";

        JsonObject json = new JsonObject();
        json.addProperty("to", receiverToken);

        JsonObject notification = new JsonObject();
        notification.addProperty("body", message);
        notification.addProperty("title", sent);

        json.add("notification", notification);

        RequestBody requestBody = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .addHeader("Authorization", "key=" + serverKey)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FCM", "Failed to send FCM notification");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("FCM", "FCM notification sent successfully");
            }
        });
    }
    @Override
    public int getItemCount() {
        return mAppointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_fullname;
        public TextView tv_datetime;
        public ImageView btn_accept;
        public ImageView btn_cancle;
        public TextView tv_text;
        public ViewHolder (View itemView) {
            super(itemView);
            tv_fullname = itemView.findViewById(R.id.tv_fullname);
            tv_datetime = itemView.findViewById(R.id.tv_datetime);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_cancle = itemView.findViewById(R.id.btn_cancle);
            tv_text = itemView.findViewById(R.id.tv_text);
        }
    }
}
