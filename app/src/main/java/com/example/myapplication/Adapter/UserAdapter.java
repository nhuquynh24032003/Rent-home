package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.example.myapplication.screens.MessageActivity;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context mContext;

    private List<User> mUsers;
    String myId;

    public UserAdapter(Context mContext, List<User> mUsers, String myId) {
        this.mUsers = mUsers;
        this.mContext = mContext;
        this.myId = myId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = mUsers.get(position);
        Glide.with(mContext).load(user.getAvatarUrl()).into(holder.profile_image);
        holder.fullname.setText(user.getFullname());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
               intent.putExtra("userId", user.getUserId());
               intent.putExtra("myId", myId);
               mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView fullname;
        public ImageView profile_image;
        public ViewHolder (View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.fullname);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }
}
