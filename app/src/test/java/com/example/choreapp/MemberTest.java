package com.example.choreapp;

import org.junit.Before;
import org.junit.Test;

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
}
