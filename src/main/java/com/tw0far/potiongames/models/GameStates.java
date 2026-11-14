package com.tw0far.potiongames.models;

public enum GameStates {
    WAITING, PREPARING, INGAME, DEATHMATCH, ENDING, RESET;

    /**
     * True while the players sit in the lobby instead of playing: waiting,
     * counting down or being reset. Nothing may hurt or change them there
     * (legacy kept the lobby world on PEACEFUL with no fall damage).
     */
    public boolean isLobbyPhase() {
        return this == WAITING || this == PREPARING || this == RESET;
    }
}
