package com.example.choreapp;

public class Member {
    private String name, id, houseID;

    public Member() {
    }

    public Member(String name, String id, String houseID) {
        this.name = name;
        this.id = id;
        this.houseID = houseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public String getHouseID() {
        return houseID;
    }

    public void setHouseID(String houseID) {
        this.houseID = houseID;
    }
}