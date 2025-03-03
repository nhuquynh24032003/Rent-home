package com.example.myapplication.model;

public class Review {
    String userId;
    String myId;
    String comment;
    float rating;
    String reviewId;
    public Review(String reviewId, String userId, String myId, String comment, float rating) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.myId = myId;
        this.comment = comment;
        this.rating = rating;
    }

    public Review() {
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
