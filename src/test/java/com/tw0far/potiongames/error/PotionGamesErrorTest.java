package com.tw0far.potiongames.error;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for PotionGamesError enum
 */
public class PotionGamesErrorTest {
    
    @Test
    public void testErrorCodesAreUnique() {
        PotionGamesError[] errors = PotionGamesError.values();
        for (int i = 0; i < errors.length; i++) {
            for (int j = i + 1; j < errors.length; j++) {
                assertNotEquals("Duplicate error codes found: " + errors[i].getCode(),
                    errors[i].getCode(), errors[j].getCode());
            }
        }
    }
    
    @Test
    public void testErrorMessagesNotEmpty() {
        for (PotionGamesError error : PotionGamesError.values()) {
            assertNotNull("User message null for " + error, error.getUserMessage());
            assertNotNull("Technical message null for " + error, error.getTechnicalMessage());
            assertFalse("User message empty for " + error, error.getUserMessage().trim().isEmpty());
            assertFalse("Technical message empty for " + error, error.getTechnicalMessage().trim().isEmpty());
        }
    }
    
    @Test
    public void testConfigErrorExists() {
        assertEquals("CFG_001", PotionGamesError.CONFIG_LOAD_FAILED.getCode());
        assertTrue(PotionGamesError.CONFIG_LOAD_FAILED.getUserMessage().length() > 0);
    }
    
    @Test
    public void testDatabaseErrorExists() {
        assertEquals("DB_001", PotionGamesError.DB_CONNECTION_FAILED.getCode());
        assertTrue(PotionGamesError.DB_CONNECTION_FAILED.getUserMessage().length() > 0);
    }
    
    @Test
    public void testGameErrorExists() {
        assertEquals("GAME_001", PotionGamesError.GAME_NOT_RUNNING.getCode());
        assertEquals("GAME_002", PotionGamesError.GAME_FULL.getCode());
    }
    
    @Test
    public void testPlayerErrorExists() {
        assertEquals("PLAYER_001", PotionGamesError.PLAYER_NOT_FOUND.getCode());
        assertEquals("PLAYER_004", PotionGamesError.INSUFFICIENT_PLAYERS.getCode());
    }
    
    @Test
    public void testCommandErrorExists() {
        assertEquals("CMD_001", PotionGamesError.INVALID_COMMAND.getCode());
        assertEquals("CMD_003", PotionGamesError.PERMISSION_DENIED.getCode());
    }
    
    @Test
    public void testUnknownErrorExists() {
        assertNotNull(PotionGamesError.UNKNOWN_ERROR);
        assertEquals("SYS_999", PotionGamesError.UNKNOWN_ERROR.getCode());
    }
}
