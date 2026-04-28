package com.tw0far.potiongames.error;

import org.junit.Before;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Unit tests for ErrorContext
 */
public class ErrorContextTest {
    private Logger testLogger;
    
    @Before
    public void setUp() {
        testLogger = Logger.getLogger("PotionGames-Test");
    }
    
    @Test
    public void testErrorContextBuilder() {
        ErrorContext context = new ErrorContext.Builder("TEST_001")
            .userMessage("User message")
            .adminMessage("Admin message")
            .consoleMessage("Console message")
            .level(ErrorContext.ErrorLevel.ERROR)
            .build();
        
        assertEquals("TEST_001", context.getErrorCode());
        assertEquals("User message", context.getUserMessage());
        assertEquals("Admin message", context.getAdminMessage());
        assertEquals("Console message", context.getConsoleMessage());
        assertEquals(ErrorContext.ErrorLevel.ERROR, context.getLevel());
    }
    
    @Test
    public void testErrorContextWithException() {
        Exception ex = new Exception("Test exception");
        ErrorContext context = new ErrorContext.Builder("TEST_002")
            .exception(ex)
            .build();
        
        assertNotNull(context.getException());
        assertEquals("Test exception", context.getException().getMessage());
    }
    
    @Test
    public void testErrorContextDefaultMessages() {
        ErrorContext context = new ErrorContext.Builder("TEST_003")
            .build();
        
        assertNotNull(context.getUserMessage());
        assertNotNull(context.getAdminMessage());
        assertNotNull(context.getConsoleMessage());
    }
    
    @Test
    public void testErrorLevelValues() {
        assertNotNull(ErrorContext.ErrorLevel.INFO);
        assertNotNull(ErrorContext.ErrorLevel.WARNING);
        assertNotNull(ErrorContext.ErrorLevel.ERROR);
        assertNotNull(ErrorContext.ErrorLevel.CRITICAL);
    }
    
    @Test
    public void testErrorContextLoggingToConsole() {
        ErrorContext context = new ErrorContext.Builder("TEST_004")
            .userMessage("Test")
            .level(ErrorContext.ErrorLevel.WARNING)
            .build();
        
        // Should not throw exception
        assertDoesNotThrow(() -> context.logToConsole(testLogger));
    }
    
    private void assertDoesNotThrow(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}
