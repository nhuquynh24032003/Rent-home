package com.example.myapplication.model;

public class Room {
    private String roomId;
    private String title;
    private String description;
    private double price;
    private String location;
    private String imageUrl;
    private String userUid;
    private double deposite;
    private String furniture;
    private double acreage;
    Boolean status;
    String datetime;
    public Room() {
    }

    public Room(String roomId, String title, String description, double price, double deposite, double acreage, String location, String furniture, String imageUrl, String userUid, String datetime, Boolean status) {
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.deposite = deposite;
        this.price = price;
        this.location = location;
        this.imageUrl = imageUrl;
        this.userUid = userUid;
        this.furniture = furniture;
        this.acreage = acreage;
        this.datetime = datetime;
        this.status = status;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public double getAcreage() {
        return acreage;
    }

    public void setAcreage(double acreage) {
        this.acreage = acreage;
    }

    public String getFurniture() {
        return furniture;
    }

    public void setFurniture(String furniture) {
        this.furniture = furniture;
    }

    public double getDeposite() {
        return deposite;
    }

    public void setDeposite(double deposite) {
        this.deposite = deposite;
    }

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
}
