package com.mss.eyespy;

public class VisitorList {
    private String id;
    private String visiting_to;
    private String code;
    private String flat_no;
    private String visitors_name;
    private String contact_no;
    private String photo;
    private String vehicleno;
    private String vehicle_photo;
    private String in_datetime;
    private String out_datetime;
    private String purpose;
    private String confirm_by;

    public VisitorList(String id, String code, String visiting_to, String flat_no, String visitors_name, String contact_no, String photo, String vehicleno, String vehicle_photo, String in_datetime, String out_datetime, String purpose, String confirm_by) {
        this.id = id;
        this.code = code;
        this.visiting_to = visiting_to;
        this.flat_no = flat_no;
        this.visitors_name = visitors_name;
        this.contact_no = contact_no;
        this.photo = photo;
        this.vehicleno = vehicleno;
        this.vehicle_photo = vehicle_photo;
        this.in_datetime = in_datetime;
        this.out_datetime = out_datetime;
        this.purpose = purpose;
        this.confirm_by = confirm_by;
    }

    public String getId() {
        return id;
    }
    public String getCode() {
        return code;
    }
    public String getVisiting_to() {
        return visiting_to;
    }
    public String getFlat_no() {
        return flat_no;
    }
    public String getVisitors_name() {
        return visitors_name;
    }
    public String getContact_no() {
        return contact_no;
    }
    public String getPhoto() {
        return photo;
    }
    public String getVehicleno() {
        return vehicleno;
    }
    public String getVehicle_photo() {
        return vehicle_photo;
    }
    public String getIn_datetime() {
        return in_datetime;
    }
    public String getOut_datetime() {
        return out_datetime;
    }
    public String getPurpose() {
        return purpose;
    }
    public String getConfirm_by() {
        return confirm_by;
    }
}
