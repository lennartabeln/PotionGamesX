package com.tw0far.potiongames.util;

/**
 * Validates chat input submitted by the setup flow.
 */
public final class SetupInputValidator {
    private SetupInputValidator() {
    }

    /**
     * Parses a lobby ID submitted through setup chat.
     *
     * <p>Lobby IDs are 1-based; zero, negative numbers and non-numeric input are
     * rejected without touching the configuration or runtime state.</p>
     *
     * @param input the raw chat message
     * @return the parsed ID, or {@code null} when the input is not a positive integer
     */
    public static Integer parseLobbyId(String input) {
        if (input == null) {
            return null;
        }
        try {
            int lobbyId = Integer.parseInt(input.trim());
            return lobbyId > 0 ? lobbyId : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
