package com.example.choreapp;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MemberTest {
    private Member m;

    @Before
    public void init() {
        m = new Member("Daniel", "", "");
    }

    @Test
    public void testGetName() {
        assertEquals("Checking name", "Daniel", m.getName());
    }

    @Test
    public void testSetName() {
        m.setName("Harry");
        assertEquals("Check set name", "Harry", m.getName());
    }

    @Test
    public void testSetChores() {
        List<String> chores = new ArrayList<>();
        chores.add("Dishes");
        m.setChores(chores);
        assertEquals(chores, m.getChores());
    }

    @Test
    public void testAddChore() {
        m.addChore("Rubbish");
        assertEquals("Rubbish", m.getChores().get(1));
    }
}
