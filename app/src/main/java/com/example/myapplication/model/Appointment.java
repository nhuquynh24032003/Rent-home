package com.example.myapplication.model;

public class Appointment {
    private String sender;
    private String receiver;
    private String datetime;
    Boolean status;
    String id;
    String orderId;

    // Empty constructor (required by Firebase)
    public Appointment() {
    }

    public Appointment(String id, String orderId, String sender, String receiver, String datetime, Boolean status) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.datetime = datetime;
        this.status = status;
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
// Getters and setters

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
