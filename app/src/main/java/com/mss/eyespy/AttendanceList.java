package com.mss.eyespy;

public class AttendanceList {
    private String id, name, type, phone, inTime, profileUrl, status;

    public AttendanceList(String id, String name, String type, String phone, String inTime, String profileUrl, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.phone = phone;
        this.inTime = inTime;
        this.profileUrl = profileUrl;
        this.status = status;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getPhone() {
        return phone;
    }

    public String getInTime() {
        return inTime;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getStatus() {
        return status;
    }
}
