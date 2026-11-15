package com.tw0far.potiongames.managers;

public interface IConfigurationManager extends IManager {

    /**
     * Reload all values from the plugin's current configuration.
     */
    void reload();

    String getLanguage();
    int getCountdown();

    int getStartDelay();
    int getRoundTime();
    int getMinPlayers();
    int getTeamSize();
    int getMaxPlayers();
    /** How many entries of kits.yml are selectable: 1 = one, 6 = six, 0 = none. */
    int getActiveKits();
    /** How many entries of shop.yml are shown and buyable: 1 = one, 20 = all twenty, 0 = none. */
    int getActivePotions();
    int getWinningReward();
    int getKillReward();

    boolean isActivateTeams();
    boolean isActivateKits();
    boolean isActivateShop();
    boolean isActivateAirdrops();
    boolean isActivateMysql();
    boolean isActivateScoreboard();
    boolean isActivateDeathmatch();

    void setActivateTeams(boolean activate);
    void setActivateKits(boolean activate);
    void setActivateShop(boolean activate);
    void setActivateAirdrops(boolean activate);
    void setActivateMysql(boolean activate);
    void setActivateScoreboard(boolean activate);
    void setActivateDeathmatch(boolean activate);

    boolean isStartOnJoin();
    boolean isFriendlyFire();
    boolean isGameServer();

    /** Kick players off the server when a round ends (proxy transfer, otherwise disconnect). */
    boolean isKickOnRoundEnd();

    /** Kick the player off the server when they use /pg leave. */
    boolean isKickOnLeave();
    boolean isEnableRewards();

    // Gameplay toggles
    boolean isJoinStarted();
    boolean isAllowOutsideChat();
    boolean isBroadcastStarting();
    boolean isCompassOnSpawn();

    /** Compass tracks the nearest opponent (Player-Finder). */
    boolean isPlayerFinder();
    boolean isChangeGamerules();
    /** Single-lobby compatibility mode: setup commands may omit the lobby number. */
    boolean isSingleLobby();

    void setGameServer(boolean gameServer);

    String getHost();
    String getPort();
    String getDatabase();
    String getUser();
}
