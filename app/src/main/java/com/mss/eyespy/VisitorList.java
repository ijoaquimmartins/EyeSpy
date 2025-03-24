package com.mss.eyespy;

public class VisitorList {
    private String visitorname, visitingto, image, intime, outtime, qrcode;

    public VisitorList(String visitorname, String visitingto, String image, String intime, String outtime, String qrcode) {
        this.visitorname = visitorname;
        this.visitingto = visitingto;
        this.image = image;
        this.intime = intime;
        this.outtime = outtime;
        this.qrcode = qrcode;
    }

    public String getVisitorname() {
        return visitorname;
    }

    public void setVisitorname(String visitorname) {
        this.visitorname = visitorname;
    }

    public String getVisitingto() {
        return visitingto;
    }

    public void setVisitingto(String visitingto) {
        this.visitingto = visitingto;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIntime() {
        return intime;
    }

    public void setIntime(String intime) {
        this.intime = intime;
    }

    public String getOuttime() {
        return outtime;
    }

    public void setOuttime(String outtime) {
        this.outtime = outtime;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }
}
