package com.tw0far.potiongames.managers;

/**
 * A pending chat input the setup flow is waiting for.
 * Stored per player so concurrent staff members cannot consume each other's messages.
 */
public enum SetupInputMode {
    /** The next chat message is the ID of a lobby to add. */
    ADD_LOBBY,

    /** The next chat message is the name of an arena to add. */
    ADD_ARENA,

    /** The next chat message is the ID of a lobby to delete. */
    DEL_LOBBY,

    /** The next chat message is the name of an arena to delete. */
    DEL_ARENA
}
