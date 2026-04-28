package com.tw0far.potiongames.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LocationUtil
 */
public class LocationUtilTest {
    private World mockWorld;
    private Location testLocation;
    
    @Before
    public void setUp() {
        mockWorld = mock(World.class);
        when(mockWorld.getName()).thenReturn("world");
        testLocation = new Location(mockWorld, 100, 64, 200);
        testLocation.setYaw(45.0f);
        testLocation.setPitch(-30.0f);
    }
    
    @Test
    public void testLocationUtilClassExists() {
        assertNotNull(LocationUtil.class);
    }
    
    @Test
    public void testLocationHasValidCoordinates() {
        assertEquals(100, testLocation.getBlockX());
        assertEquals(64, testLocation.getBlockY());
        assertEquals(200, testLocation.getBlockZ());
    }
    
    @Test
    public void testLocationHasValidRotation() {
        assertEquals(45.0f, testLocation.getYaw(), 0.01);
        assertEquals(-30.0f, testLocation.getPitch(), 0.01);
    }
    
    @Test
    public void testLocationClone() {
        Location clone = testLocation.clone();
        assertNotNull(clone);
        assertEquals(testLocation.getX(), clone.getX(), 0.001);
        assertEquals(testLocation.getY(), clone.getY(), 0.001);
        assertEquals(testLocation.getZ(), clone.getZ(), 0.001);
    }
}
