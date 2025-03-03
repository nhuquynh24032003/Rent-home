package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.MessageAdapter;
import com.example.myapplication.FCMNotificationService;
import com.example.myapplication.R;
import com.example.myapplication.model.Appointment;
import com.example.myapplication.model.Chat;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthMultiFactorException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    Toolbar toolbar;
    TextView fullname;
    String date;
    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;
    ImageButton btn_send;
    EditText text_send;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    List<Chat> mChat;
    String userId;
    String myId;
    String fcmToken;
    String send;
    ImageButton btn_appointment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recycler_view);
        btn_appointment = findViewById(R.id.btn_appointment);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profile_image = findViewById(R.id.profile_image);
        fullname = findViewById(R.id.fullname);
        intent = getIntent();
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        userId = intent.getStringExtra("userId");
        myId = intent.getStringExtra("myId");


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {

                    sendMessage(myId, userId, msg);
                    sendFCMNotification(send, msg, fcmToken);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");

            }
        });
        reference = FirebaseDatabase.getInstance().getReference("users").child(myId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userSend = dataSnapshot.getValue(User.class);
                send = userSend.getFullname();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullname.setText(user.getFullname());
                fcmToken = user.getFCMToken();
                Glide.with(MessageActivity.this).load(user.getAvatarUrl()).into(profile_image);
                readMessage(myId, userId, user.getAvatarUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       btn_appointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePickerDialog();
            }
        });

    }
    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();

        // Create a DatePickerDialog with the current date
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date = String.format("%02d/%02d/%04d", day, month + 1, year);
                        showTimePickerDialog(year, month, day);

                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Show the DatePickerDialog
        datePickerDialog.show();
    }
    private void showTimePickerDialog(int year, int month, int day) {
        // Get the current time
        Calendar calendar = Calendar.getInstance();

        // Create a TimePickerDialog with the current time
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        // Combine the selected date and time
                        String formattedDateTime = String.format("%02d/%02d/%04d %02d:%02d", day, month + 1, year, hourOfDay, minute);
                        // Display or use the formattedDateTime as needed
                        // input_date.getEditText().setText(formattedDateTime);
                        Toast.makeText(MessageActivity.this, "Selected Date and Time: " + formattedDateTime, Toast.LENGTH_SHORT).show();
                        showConfirmationDialog(formattedDateTime);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // 24-hour time format
        );

        timePickerDialog.show();
    }
    private void showConfirmationDialog(String formattedDateTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Appointment Confirmation");
        builder.setMessage("Selected Date and Time: " + formattedDateTime);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle the confirmation action if needed
                // For example, you can save the selected date and time to your database
                // or perform any other necessary tasks.
                attemptToAddAppointment(myId, userId, formattedDateTime);
                sendFCMNotification("Hệ thống", "Bạn vừa nhận được một lịch hẹn, hãy nhanh chóng xác nhận", fcmToken);
              //  Toast.makeText(MessageActivity.this, "Appointment Confirmed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Handle the cancellation action if needed
                Toast.makeText(MessageActivity.this, "Appointment Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void addAppointment(String sender, String receiver, String datetime) {
        DatabaseReference appointmentReference = FirebaseDatabase.getInstance().getReference("Appointment");

        // Create a new Appointment object with a generated ID
        String appointmentId = appointmentReference.push().getKey();
        Appointment appointment = new Appointment(appointmentId,"", sender, receiver, datetime, false);

        // Push the new appointment to the database
        appointmentReference.child(appointmentId).setValue(appointment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Show a confirmation message to the user
                            Toast.makeText(MessageActivity.this, "Đã đặt lịch hẹn vui lòng chờ xác nhận", Toast.LENGTH_SHORT).show();
                        } else {
                            // Handle the error
                            Log.e("Firebase", "Error adding appointment", task.getException());
                            Toast.makeText(MessageActivity.this, "Failed to add appointment", Toast.LENGTH_SHORT).show();
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
    private void attemptToAddAppointment(String sender, String receiver, String datetime) {
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
                            } else {
                                // Show a message to the user about the conflict for the receiver
                                Toast.makeText(MessageActivity.this, "Đối phương đã có lịch hẹn! Vui lòng chọn giờ khác", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    // Show a message to the user about the conflict for the sender
                    Toast.makeText(MessageActivity.this, "Bạn đã có lịch hẹn! Vui lòng chọn giờ khác", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }
    private void readMessage (String myId, String userId, String imageurl) {
        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mChat.clear();
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId) ||
                    chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                        mChat.add(chat);
                    }
                }

                messageAdapter = new MessageAdapter(MessageActivity.this, mChat, myId, imageurl);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
}