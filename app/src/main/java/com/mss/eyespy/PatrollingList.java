package com.mss.eyespy;

public class PatrollingList {

   String id, qrcode, qrname, latitude, longitude, uploaded, scandatetime;

    public PatrollingList(String id, String qrcode, String qrname, String latitude, String longitude, String uploaded, String scandatetime) {
        this.latitude = latitude;
        this.id = id;
        this.qrcode = qrcode;
        this.qrname = qrname;
        this.longitude = longitude;
        this.uploaded = uploaded;
        this.scandatetime = scandatetime;
    }

    public String getId() {
        return id;
    }

    public String getQrcode() {
        return qrcode;
    }

    public String getQrname() {
        return qrname;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getUploaded() {
        return uploaded;
    }

    public String getScandatetime() {
        return scandatetime;
    }
}
