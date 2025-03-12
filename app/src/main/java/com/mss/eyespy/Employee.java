package com.mss.eyespy;

public class Employee {
    private String name, phone, inTime, profileUrl;

    public Employee(String name, String phone, String inTime, String profileUrl) {
        this.name = name;
        this.phone = phone;
        this.inTime = inTime;
        this.profileUrl = profileUrl;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getInTime() { return inTime; }
    public String getProfileUrl() { return profileUrl; }
}
