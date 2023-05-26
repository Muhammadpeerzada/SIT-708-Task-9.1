package com.example.lostandfound.model;

public class Post {
    private int post_id;
    private String postName;
    private String phoneNumber;
    private String description;
    private String state;
    private String date;
    private String location;
    private String latitude;
    private String longitude;

    public Post() {
    }

    public Post(String postName, String phoneNumber, String description, String state, String date, String location, String latitude, String longitude) {

        this.postName = postName;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.state = state;
        this.date = date;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Post(int post_id, String postName, String phoneNumber, String description, String state, String date, String location, String latitude, String longitude) {
        this.post_id = post_id;
        this.postName = postName;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.state = state;
        this.date = date;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getPost_id() {
        return post_id;
    }

    public String getPostName() {
        return postName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getState() {
        return state;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
