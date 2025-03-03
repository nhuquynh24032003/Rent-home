package com.example.myapplication.screens;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.example.myapplication.Adapter.ViewPagerAdapter;
import com.example.myapplication.R;
import com.google.android.material.tabs.TabLayout;

// AppointmentActivity.java
public class AppointmentActivity extends AppCompatActivity {
    String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment);

        // Get the user ID from the intent
        myId = getIntent().getStringExtra("myId");

        // Get the TabLayout and ViewPager from the layout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        // Create an instance of the ViewPagerAdapter and pass myId
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), myId);

        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
    }

    // Your other AppointmentActivity code goes here
}
