package com.example.firebasepushnotifications;


public class Notification {
    private String senderId, receiverId, message, notificationId;

    public Notification() {
    }

    public Notification(String senderId, String receiverId, String message, String notificationId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.notificationId = notificationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}
