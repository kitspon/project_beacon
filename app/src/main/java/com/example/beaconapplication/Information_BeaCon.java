package com.example.beaconapplication;

public class Information_BeaCon {
    private String Name;
    private String imageurl;
    private String floors;
    private String Address;
    private String rm_L;
    private String rm_R;
    private String rm_F;
    private String rm_B;
    private String st_T;
    private String st_B;
    private String el_B;
    private String el_T;
    private String type;
    private String build_id;

    public String getBuild_id() {
        return build_id;
    }

    public void setBuild_id(String build_id) {
        this.build_id = build_id;
    }

    public Information_BeaCon() { }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFloors() { return floors; }

    public void setFloors(String floors) { this.floors = floors; }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getRm_L() {
        return rm_L;
    }

    public void setRm_L(String rm_L) {
        this.rm_L = rm_L;
    }

    public String getRm_R() {
        return rm_R;
    }

    public void setRm_R(String rm_R) {
        this.rm_R = rm_R;
    }

    public String getRm_F() {
        return rm_F;
    }

    public void setRm_F(String rm_F) {
        this.rm_F = rm_F;
    }

    public String getRm_B() {
        return rm_B;
    }

    public void setRm_B(String rm_B) {
        this.rm_B = rm_B;
    }

    public String getSt_T() { return st_T; }

    public void setSt_T(String st_T) { this.st_T = st_T; }

    public String getSt_B() { return st_B; }

    public void setSt_B(String st_B) { this.st_B = st_B; }

    public String getEl_B() { return el_B; }

    public void setEl_B(String el_B) { this.el_B = el_B; }

    public String getEl_T() { return el_T; }

    public void setEl_T(String el_T) { this.el_T = el_T; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
