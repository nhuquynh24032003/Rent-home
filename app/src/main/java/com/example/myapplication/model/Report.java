package com.example.myapplication.model;

public class Report {
    String userId;
    String myId;
    String roomId;
    String reportId;
    String reason;
    public Report(String reportId, String userId, String myId, String roomId, String reason) {
        this.userId = userId;
        this.myId = myId;
        this.roomId = roomId;
        this.reportId = reportId;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Report() {
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
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
}
