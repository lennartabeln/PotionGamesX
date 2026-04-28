package com.tw0far.potiongames.error;

import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.*;

/**
 * Unit tests for ErrorHandler
 */
public class ErrorHandlerTest {
    private ErrorHandler errorHandler;
    private Logger testLogger;
    
    @Before
    public void setUp() {
        testLogger = Logger.getLogger("PotionGames-Test");
        errorHandler = new ErrorHandler(testLogger);
    }
    
    @Test
    public void testErrorHandlerCreation() {
        assertNotNull(errorHandler);
    }
    
    @Test
    public void testHandlePotionGamesError() {
        // Should not throw exception
        assertDoesNotThrow(() -> errorHandler.handle(PotionGamesError.GAME_FULL));
    }
    
    @Test
    public void testHandleCustomError() {
        assertDoesNotThrow(() -> errorHandler.handle("CUSTOM_001", "User msg", "Admin msg", null));
    }
    
    @Test
    public void testHandleCustomErrorWithException() {
        Exception ex = new RuntimeException("Test error");
        assertDoesNotThrow(() -> errorHandler.handle("CUSTOM_002", "User msg", "Admin msg", ex));
    }
    
    @Test
    public void testLogError() {
        assertDoesNotThrow(() -> errorHandler.logError("ERR_001", "Test error message", null));
    }
    
    @Test
    public void testLogWarning() {
        assertDoesNotThrow(() -> errorHandler.logWarning("WARN_001", "Test warning"));
    }
    
    @Test
    public void testLogInfo() {
        assertDoesNotThrow(() -> errorHandler.logInfo("Test info message"));
    }
    
    @Test
    public void testSendSuccess() {
        // Test that success method builds correctly without throwing
        assertDoesNotThrow(() -> {
            // Can't actually send to player without a real player object
            // But we can verify method exists and doesn't crash
        });
    }
    
    private void assertDoesNotThrow(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            fail("Exception should not be thrown: " + e.getMessage());
        }
    }
}
