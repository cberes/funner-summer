package net.seabears.funner.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CrowdTest {
    @Test
    public void testFromStringLowercase() {
        assertEquals(Crowd.COUPLE, Crowd.fromString("c"));
        assertEquals(Crowd.GROUP, Crowd.fromString("g"));
        assertEquals(Crowd.SINGLE, Crowd.fromString("s"));
    }

    @Test
    public void testFromStringUppercase() {
        assertEquals(Crowd.COUPLE, Crowd.fromString("C"));
        assertEquals(Crowd.GROUP, Crowd.fromString("G"));
        assertEquals(Crowd.SINGLE, Crowd.fromString("S"));
    }
}
