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
 * Member is a type that stores important information about a user of our app
 * String name stores the name of user
 * String id stores a unique id of user
 * String houseID stores the houseID of user
 * List<String> chores stores a list of chores assigned to user
 */
public class Member {
    private String name;
    private String id;
    private String houseID;
    private List<String> chores;

    /**
     * Member constructor requires a name, id and houseID
     * @param name name of user
     * @param id id of user
     * @param houseID houseID of user
     */
    public Member(String name, String id, String houseID) {
        this.name = name;
        this.id = id;
        this.houseID = houseID;
        this.chores = new ArrayList<>();
    }

    /**
     * Gets the name of the user
     * @return name name of the user
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user
     * @param name name of the user
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ID of the user
     * @return id id of the user
     */
    public String getID() {
        return id;
    }

    /**
     * Gets the houseID of the user
     * @return houseID houseID of the user
     */
    public String getHouseID() {
        return houseID;
    }

    /**
     * Sets the houseID of the user
     * @param houseID houseID of the user
     */
    public void setHouseID(String houseID) {
        this.houseID = houseID;
    }

    /**
     * Gets a list of chores assigned to the user
     * @return chores list
     */
    public List<String> getChores() {
        return chores;
    }

    /**
     * Adds a chore to a list of chores
     * @param chore added chore
     */
    public void addChore(String chore) {
        this.chores.add(chore);
    }

    /**
     * Sets a list of chores to the user
     * @param chores list of chores set
     */
    public void setChores(List<String> chores) {
        this.chores = chores;
    }

    /**
     * Empties the list of chores for the user
     */
    public void resetChores() {
        this.chores.clear();
    }

}
