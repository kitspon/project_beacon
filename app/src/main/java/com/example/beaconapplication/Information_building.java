package com.example.beaconapplication;

public class Information_building {

    String building_id;
    String building_name;
    String building_img;
    String latitude;
    String longtitude;
    String floors;
    String information;

    public Information_building() {
    }

    public Information_building(String building_id, String building_name, String building_img, String latitude, String longitude, String floors, String information) {
        this.building_id = building_id;
        this.building_name = building_name;
        this.building_img = building_img;
        this.latitude = latitude;
        this.longtitude = longitude;
        this.floors = floors;
        this.information = information;
    }

    public String getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(String building_id) {
        this.building_id = building_id;
    }

    public String getBuilding_name() {
        return building_name;
    }

    public void setBuilding_name(String building_name) {
        this.building_name = building_name;
    }

    public String getBuilding_img() {
        return building_img;
    }

    public void setBuilding_img(String building_img) {
        this.building_img = building_img;
    }

    public String getLatitude() { return latitude; }

    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongtitude() { return longtitude; }

    public void setLongtitude(String longtitude) { this.longtitude = longtitude; }

    public String getFloors() {
        return floors;
    }

    public void setFloors(String floors) {
        this.floors = floors;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }
}
