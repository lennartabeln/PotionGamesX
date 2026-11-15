package com.tw0far.potiongames.models;

public enum GameStates {
    WAITING, PREPARING, INGAME, DEATHMATCH, ENDING, RESET;

    /**
     * True while a round is actually played. Every other state - waiting,
     * counting down, the cleanup after a round and the reset - counts as lobby,
     * where players take no damage, do not get hungry and cannot fight.
     */
    public boolean isRunning() {
        return this == INGAME || this == DEATHMATCH;
    }
}
