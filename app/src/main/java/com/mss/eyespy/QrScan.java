package com.mss.eyespy;

public class QrScan {
    private int id;
    private String qrdata;
    private double latitude;
    private double longitude;

    public QrScan(int id, String qrdata, double latitude, double longitude) {
        this.id = id;
        this.qrdata = qrdata;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getQrdata() {
        return qrdata;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}