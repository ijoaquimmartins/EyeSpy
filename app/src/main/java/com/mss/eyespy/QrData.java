package com.mss.eyespy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

public class QrData {
    private String id;
    private String qrCode;
    private String latitude;
    private String longitude;
    private String datetime;
    private String userid;
    private String formtype;

    public QrData(String id, String qrCode, String latitude, String longitude, String datetime, String userid, String formtype) {
        this.id = id;
        this.qrCode = qrCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.datetime = datetime;
        this.userid = userid;
        this.formtype = formtype;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("qr_code", Base64.getEncoder().encodeToString((qrCode.trim()).getBytes()));
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("datetime", datetime);
            jsonObject.put("user_id", userid);
            jsonObject.put("formtype", formtype);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getString() {
        return id;
    }
}
