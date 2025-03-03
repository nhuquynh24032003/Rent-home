package com.example.myapplication.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Room;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Dashboard extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton btn_addblogs;
    private Fragment selectedFragment;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);
        bottomNavigationView = findViewById(R.id.nav_view);
        btn_addblogs = findViewById(R.id.btn_addblogs);
        btn_addblogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, AddBlogs.class);
                String userId = getIntent().getStringExtra("userId");
                Toast.makeText(Dashboard.this, "" + userId, Toast.LENGTH_SHORT).show();
                intent.putExtra("userId", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.goup, R.anim.godown);
            }
        });
        // Set the default fragment
        boolean goToAccountFragment = getIntent().getBooleanExtra("goToAccountFragment", false);

        if (goToAccountFragment) {
            // Replace the current fragment with HomeFragment

            String avatarUrl = getIntent().getStringExtra("avatarUrl");
            String fullname = getIntent().getStringExtra("fullname");
            String address = getIntent().getStringExtra("address");
            String phonenumber = getIntent().getStringExtra("phonenumber");
            String gender = getIntent().getStringExtra("gender");
            String date = getIntent().getStringExtra("date");
            String email = getIntent().getStringExtra("email");
            String password = getIntent().getStringExtra("password");
            String userId = getIntent().getStringExtra("userId");
            selectedFragment = new AccountFragment();
            ((AccountFragment) selectedFragment).setFullName(fullname);
            ((AccountFragment) selectedFragment).setAvatarUrl(avatarUrl);
            ((AccountFragment) selectedFragment).setAddress(address);
            ((AccountFragment) selectedFragment).setPhoneNumber(phonenumber);
            ((AccountFragment) selectedFragment).setGender(gender);
            ((AccountFragment) selectedFragment).setDate(date);
            ((AccountFragment) selectedFragment).setEmail(email);
            ((AccountFragment) selectedFragment).setPassword(password);
            ((AccountFragment) selectedFragment).setuserId(userId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, selectedFragment)
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.btn_account);
        } else {
            // Set the default fragment
            HomeFragment homeFragment = new HomeFragment();
            String userId = getIntent().getStringExtra("userId");
            homeFragment.setuserId(userId);
// Replace the current fragment with the HomeFragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content, homeFragment)
                    .commit();
        }

        // ...

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {

                if (item.getItemId() == R.id.btn_home) {
                    String userId = getIntent().getStringExtra("userId");
                    selectedFragment = new HomeFragment();
                    ((HomeFragment) selectedFragment).setuserId(userId);

                } else if (item.getItemId() == R.id.btn_chat) {
                    String userId = getIntent().getStringExtra("userId");
                    selectedFragment = new ChatFragment();
                    ((ChatFragment) selectedFragment).setuserId(userId);
                } else if (item.getItemId() == R.id.btn_manablogs) {
                    String userId = getIntent().getStringExtra("userId");
                    selectedFragment = new BlogsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", userId);
                    selectedFragment.setArguments(bundle);
                } else if (item.getItemId() == R.id.btn_account) {
                    String avatarUrl = getIntent().getStringExtra("avatarUrl");
                    String fullname = getIntent().getStringExtra("fullname");
                    String address = getIntent().getStringExtra("address");
                    String phonenumber = getIntent().getStringExtra("phonenumber");
                    String gender = getIntent().getStringExtra("gender");
                    String date = getIntent().getStringExtra("date");
                    String email = getIntent().getStringExtra("email");
                    String password = getIntent().getStringExtra("password");
                    String userId = getIntent().getStringExtra("userId");
                    selectedFragment = new AccountFragment();
                    ((AccountFragment) selectedFragment).setFullName(fullname);
                    ((AccountFragment) selectedFragment).setAvatarUrl(avatarUrl);
                    ((AccountFragment) selectedFragment).setAddress(address);
                    ((AccountFragment) selectedFragment).setPhoneNumber(phonenumber);
                    ((AccountFragment) selectedFragment).setGender(gender);
                    ((AccountFragment) selectedFragment).setDate(date);
                    ((AccountFragment) selectedFragment).setEmail(email);
                    ((AccountFragment) selectedFragment).setPassword(password);
                    ((AccountFragment) selectedFragment).setuserId(userId);
                } else {

                    return false;
                }

                // Replace the current fragment with the selected one
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content, selectedFragment)
                        .commit();

                return true;
            }
        });

    }
}