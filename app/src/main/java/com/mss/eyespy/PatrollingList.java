package com.mss.eyespy;

public class PatrollingList {

    private int columnId;
    private String columnName;
    private String qrLocation;
    private String scanTimefr;
    private String scanTimeto;
    private int qrcodeId;

    public PatrollingList(int columnId, String columnName, String qrLocation, String scanTimefr, String scanTimeto, int qrcodeId) {
        this.columnId = columnId;
        this.columnName = columnName;
        this.qrLocation = qrLocation;
        this.scanTimefr = scanTimefr;
        this.scanTimeto = scanTimeto;
        this.qrcodeId = qrcodeId;
    }

    public int getColumnId() {
        return columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getQrLocation() {
        return qrLocation;
    }

    public String getScanTimefr() {
        return scanTimefr;
    }

    public String getScanTimeto() {
        return scanTimeto;
    }

    public int getQrcodeId() {
        return qrcodeId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setQrLocation(String qrLocation) {
        this.qrLocation = qrLocation;
    }

    public void setScanTimefr(String scanTimefr) {
        this.scanTimefr = scanTimefr;
    }

    public void setScanTimeto(String scanTimeto) {
        this.scanTimeto = scanTimeto;
    }

    public void setQrcodeId(int qrcodeId) {
        this.qrcodeId = qrcodeId;
    }
}
