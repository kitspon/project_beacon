package com.example.beaconapplication;

public class Information_Route {
    String first;
    String end;
    String direction;
    String map;

    public Information_Route() {
    }

    public Information_Route(String first, String end, String direction, String map) {
        this.first = first;
        this.end = end;
        this.direction = direction;
        this.map = map;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }
}


