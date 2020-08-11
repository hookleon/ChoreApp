/*
  Member.java
  -----------
  Chore Roulette App
  Leon Hook, Magnus McGee and Tiaan Stevenson-Brunt
 */
package com.example.choreapp;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Member {
    private String name;
    private String id;
    private String houseID;
    private List<String> chores;

    /**
     * @param name
     * @param id
     * @param houseID
     */
    public Member(String name, String id, String houseID) {
        this.name = name;
        this.id = id;
        this.houseID = houseID;
        this.chores = new ArrayList<>();
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return id
     */
    public String getID() {
        return id;
    }

    /**
     * @return houseID
     */
    public String getHouseID() {
        return houseID;
    }

    /**
     * @param houseID
     */
    public void setHouseID(String houseID) {
        this.houseID = houseID;
    }

    /**
     * @return
     */
    public List<String> getChores() {
        return chores;
    }

    /**
     * @param chore
     */
    public void addChore(String chore) {
        this.chores.add(chore);
    }


    public void setChores(List<String> chores) {
        this.chores = chores;
    }

    /**
     * To clear chores List
     */
    public void resetChores() {
        this.chores.clear();
    }

}
