package com.tw0far.potiongames.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MessageUtil
 */
public class MessageUtilTest {
    
    @Before
    public void setUp() {
        // MessageUtil uses static Adventure API - tests verify it exists
    }
    
    @Test
    public void testMessageUtilClassExists() {
        assertNotNull(MessageUtil.class);
    }
    
    @Test
    public void testCreateInfoComponentIsNotNull() {
        var component = MessageUtil.createInfo("Test info");
        assertNotNull(component);
    }
    
    @Test
    public void testCreateWarningComponentIsNotNull() {
        var component = MessageUtil.createWarning("Test warning");
        assertNotNull(component);
    }
    
    @Test
    public void testCreateErrorComponentIsNotNull() {
        var component = MessageUtil.createError("Test error");
        assertNotNull(component);
    }
    
    @Test
    public void testCreateSuccessComponentIsNotNull() {
        var component = MessageUtil.createSuccess("Test success");
        assertNotNull(component);
    }
}
