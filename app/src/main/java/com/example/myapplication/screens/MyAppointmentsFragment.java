package com.example.myapplication.screens;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.myapplication.Adapter.AppointmentAdapter;
import com.example.myapplication.R;
import com.example.myapplication.model.Appointment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyAppointmentsFragment extends Fragment {
    private String myId;
    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private List<Appointment> mAppointments;
    private Set<String> appointmentList;
    private AppointmentAdapter appointmentAdapter;

    public static MyAppointmentsFragment newInstance(String myId) {
        MyAppointmentsFragment fragment = new MyAppointmentsFragment();
        Bundle args = new Bundle();
        args.putString("myId", myId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myId = getArguments().getString("myId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_appointments, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAppointments = new ArrayList<>();
        appointmentList = new HashSet<>();

        readMyAppointment(myId);

        return view;
    }

    private void readMyAppointment(String myId) {
        reference = FirebaseDatabase.getInstance().getReference("Appointment");
        reference.orderByChild("receiver").equalTo(myId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAppointments.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null) {
                        mAppointments.add(appointment);
                    }
                }
                appointmentAdapter = new AppointmentAdapter(getContext(), mAppointments, myId);
                recyclerView.setAdapter(appointmentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });
    }

}
