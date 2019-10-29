package com.example.firebasepushnotifications;

public class UserId {
    public String mUserId;
    public <T extends UserId> T withId(String id){
        mUserId = id;
        return (T) this;
    }
}
