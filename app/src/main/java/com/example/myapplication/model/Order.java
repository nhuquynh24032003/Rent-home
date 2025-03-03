package com.example.myapplication.model;

public class Order {
    String userId;
    String myId;
    String roomId;
    Boolean paymentStatus;
    String orderId;
    String methodPayment;

    public Order(String orderId, String userId, String myId, String roomId, String methodPayment, Boolean paymentStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.myId = myId;
        this.roomId = roomId;
        this.paymentStatus = paymentStatus;
        this.methodPayment = methodPayment;
    }

    public String getMethodPayment() {
        return methodPayment;
    }

    public void setMethodPayment(String methodPayment) {
        this.methodPayment = methodPayment;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Order() {
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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
