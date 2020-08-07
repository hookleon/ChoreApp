package com.example.choreapp;

import java.util.ArrayList;
import java.util.List;
//test
public class Member {
    private String name;
    private String id;
    private String houseID;
    private List<String> chores;

    public Member(String name, String id, String houseID) {
        this.name = name;
        this.id = id;
        this.houseID = houseID;
        this.chores = new ArrayList<>();
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

    public List<String> getChores() {
        return chores;
    }

    public void addChore(String chore) {
        this.chores.add(chore);
    }

    public void resetChores() { this.chores.clear(); }

}