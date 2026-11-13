package com.tw0far.potiongames.managers;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tracks the pending setup chat input per player.
 *
 * <p>Chat events arrive on an asynchronous thread while the setup tools run on the
 * main/region thread, so the backing map must be thread-safe. State is keyed by
 * UUID so a name change cannot leak another player's pending input.</p>
 */
public final class SetupInputModes {

    private final Map<UUID, SetupInputMode> inputModes = new ConcurrentHashMap<>();

    /**
     * Sets the pending input mode for a player.
     *
     * @param playerUuid the player's UUID
     * @param mode the pending mode, or {@code null} to clear it
     */
    public void set(UUID playerUuid, SetupInputMode mode) {
        if (playerUuid == null) {
            return;
        }
        if (mode == null) {
            inputModes.remove(playerUuid);
        } else {
            inputModes.put(playerUuid, mode);
        }
    }

    /**
     * @param playerUuid the player's UUID
     * @return the pending mode, or {@code null} if the player has none
     */
    public SetupInputMode get(UUID playerUuid) {
        return playerUuid == null ? null : inputModes.get(playerUuid);
    }

    /**
     * @param playerUuid the player's UUID
     * @return whether the player is waiting for setup input
     */
    public boolean has(UUID playerUuid) {
        return get(playerUuid) != null;
    }

    /** Clears the pending mode for one player. */
    public void remove(UUID playerUuid) {
        if (playerUuid != null) {
            inputModes.remove(playerUuid);
        }
    }

    /** Clears every pending mode. */
    public void clear() {
        inputModes.clear();
    }

    /**
     * @return the number of players with a pending mode (used by tests)
     */
    public int size() {
        return inputModes.size();
    }
}
