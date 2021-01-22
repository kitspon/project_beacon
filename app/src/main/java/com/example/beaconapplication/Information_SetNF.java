package com.example.beaconapplication;

public class Information_SetNF {
    private String name;
    private String floor;
    private String Image;
    String building_id;

    public Information_SetNF() {
    }

    public Information_SetNF(String name, String floor, String image) {
        this.name = name;
        this.floor = floor;
        this.Image = image;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(String building_id) {
        this.building_id = building_id;
    }
}
