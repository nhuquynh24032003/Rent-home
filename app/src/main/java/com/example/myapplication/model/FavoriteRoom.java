package com.example.myapplication.model;

import com.example.myapplication.model.Room;

public class FavoriteRoom {
    private String roomId;
    private String title;
    private String description;
    private double price;
    private String location;
    private String imageUrl;
    private String userUid; // ID của người đăng bài
    private String favoriteUserId; // ID của người đăng nhập yêu thích bài đăng

    public FavoriteRoom() {

    }

    public FavoriteRoom(Room room, String favoriteUserId) {
        this.roomId = room.getRoomId();
        this.title = room.getTitle();
        this.description = room.getDescription();
        this.price = room.getPrice();
        this.location = room.getLocation();
        this.imageUrl = room.getImageUrl();
        this.userUid = room.getUserUid();
        this.favoriteUserId = favoriteUserId;
    }

    // Getter và setter

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getFavoriteUserId() {
        return favoriteUserId;
    }

    public void setFavoriteUserId(String favoriteUserId) {
        this.favoriteUserId = favoriteUserId;
    }
}
