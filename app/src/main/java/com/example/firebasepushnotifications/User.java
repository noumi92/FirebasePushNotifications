package com.example.firebasepushnotifications;

public class User extends UserId{
    private String mName, mImage;

    public User() {
    }

    public User(String name, String image) {
        mName = name;
        mImage = image;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }
}
