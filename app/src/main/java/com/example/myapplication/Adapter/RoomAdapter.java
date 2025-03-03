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

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private Context mContext;

    private List<Room> mRooms;
    String myId;
    String userId;
    String date;
    String orderId;

    public RoomAdapter(Context mContext, List<Room> mRooms, String myId) {
        this.mRooms = mRooms;
        this.mContext = mContext;
        this.myId = myId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_item_room, parent, false);
        return new RoomAdapter.ViewHolder(view);
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
        findOrder(room.getRoomId(), holder.btn_pay, holder.btn_paid, holder.btn_wait);
        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("Appointment");
        Query query = appointmentRef.orderByChild("receiver").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot apppointmentSnapshot : dataSnapshot.getChildren()) {
                        Appointment appointment = apppointmentSnapshot.getValue(Appointment.class);
                        if (appointment != null) {
                            // Do something with the order
                            //Log.d("Order", "Order found: " + order.getPaymentStatus());
                            if(appointment.getSender().equals(myId)) {
                               holder.btn_wait.setVisibility(View.VISIBLE);
                               holder.btn_pay.setVisibility(View.GONE);
                            }
                            else {
                                holder.btn_wait.setVisibility(View.GONE);
                                holder.btn_pay.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                  //  Log.d("Order", "No order found for roomId: " + roomId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("Order", "Error finding order: " + databaseError.getMessage());
            }
        });

    }
    // Helper method to check payment status in orders

    private void findOrder(String roomId, ImageView btn_pay, ImageView btn_paid, ImageView btn_wait) {
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        Query query = ordersRef.orderByChild("roomId").equalTo(roomId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);
                        if (order != null) {
                            // Do something with the order
                            orderId = order.getOrderId();
                            Log.d("Order", "Order found: " + order.getPaymentStatus());
                            if(!order.getPaymentStatus()) {
                                btn_pay.setVisibility(View.VISIBLE);
                                if (order.getMethodPayment().equals("Tiền mặt")) {
                                   btn_pay.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           showDatePickerDialog(btn_wait, btn_pay);
                                       }
                                   });
                                }
                            }
                            else {
                                btn_pay.setVisibility(View.GONE);
                                btn_wait.setVisibility(View.GONE);
                                btn_paid.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                } else {
                    Log.d("Order", "No order found for roomId: " + roomId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                Log.e("Order", "Error finding order: " + databaseError.getMessage());
            }
        });
    }
    private void showDatePickerDialog(ImageView btn_wait, ImageView btn_paid) {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog with the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                mContext,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date = String.format("%02d/%02d/%04d", day, month + 1, year);
                        showTimePickerDialog(year, month, day, btn_wait, btn_paid);

                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
    private void showTimePickerDialog(int year, int month, int day, ImageView btn_wait, ImageView btn_paid) {
        // Get the current time
        Calendar calendar = Calendar.getInstance();

        // Create a TimePickerDialog with the current time
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                mContext,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        // Combine the selected date and time
                        String formattedDateTime = String.format("%02d/%02d/%04d %02d:%02d", day, month + 1, year, hourOfDay, minute);
                        // Display or use the formattedDateTime as needed
                        // input_date.getEditText().setText(formattedDateTime);
                        Toast.makeText(mContext, "Selected Date and Time: " + formattedDateTime, Toast.LENGTH_SHORT).show();
                        showConfirmationDialog(formattedDateTime, btn_wait, btn_paid);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // 24-hour time format
        );

        timePickerDialog.show();
    }
    private void showConfirmationDialog(String formattedDateTime, ImageView btn_wait, ImageView btn_paid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Appointment Confirmation");
        builder.setMessage("Selected Date and Time: " + formattedDateTime);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle the confirmation action if needed
                // For example, you can save the selected date and time to your database
                // or perform any other necessary tasks.
                attemptToAddAppointment(myId, userId, formattedDateTime, btn_wait, btn_paid);
                //sendFCMNotification("Hệ thống", "Bạn vừa nhận được một lịch hẹn, hãy nhanh chóng xác nhận", fcmToken);
                //  Toast.makeText(MessageActivity.this, "Appointment Confirmed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle the cancellation action if needed
                Toast.makeText(mContext, "Appointment Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void addAppointment(String sender, String receiver, String datetime) {
        DatabaseReference appointmentReference = FirebaseDatabase.getInstance().getReference("Appointment");

        // Create a new Appointment object with a generated ID
        String appointmentId = appointmentReference.push().getKey();
        Appointment appointment = new Appointment(appointmentId, orderId, sender, receiver, datetime, false);

        // Push the new appointment to the database
        appointmentReference.child(appointmentId).setValue(appointment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Show a confirmation message to the user
                            Toast.makeText(mContext, "Đã đặt lịch hẹn vui lòng chờ xác nhận", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle the error
                            Log.e("Firebase", "Error adding appointment", task.getException());
                            Toast.makeText(mContext, "Failed to add appointment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
    private void attemptToAddAppointment(String sender, String receiver, String datetime, ImageView btn_wait,ImageView btn_paid) {
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
                                addAppointment(sender, receiver, datetime);
                                btn_wait.setVisibility(View.VISIBLE);
                                btn_paid.setVisibility(View.GONE);
                            } else {
                                // Show a message to the user about the conflict for the receiver
                                Toast.makeText(mContext, "Đối phương đã có lịch hẹn! Vui lòng chọn giờ khác", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Show a message to the user about the conflict for the sender
                    Toast.makeText(mContext, "Bạn đã có lịch hẹn! Vui lòng chọn giờ khác", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            btn_pay = itemView.findViewById(R.id.btn_pay);
            btn_paid = itemView.findViewById(R.id.btn_paid);
            btn_wait = itemView.findViewById(R.id.btn_wait);
            btn_pay.setVisibility(View.VISIBLE);
            favoriteImageButton.setVisibility(View.GONE);
            btn_paid.setVisibility(View.GONE);
        }
    }
}
