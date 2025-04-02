package com.mss.eyespy;

import org.json.JSONException;
import org.json.JSONObject;

public class QrData {
    private int id;
    private String qrCode;
    private String latitude;
    private String longitude;
    private String datetime;

    public QrData(int id, String qrCode, String latitude, String longitude, String datetime) {
        this.id = id;
        this.qrCode = qrCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.datetime = datetime;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("qr_code", qrCode);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("datetime", datetime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public int getId() {
        return id;
    }
}
