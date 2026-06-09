package com.tw0far.potiongames.managers;

/**
 * Interface for configuration management.
 * Defines contract for accessing and modifying plugin configuration.
 */
public interface IConfigurationManager extends IManager {
    
    // Reload configuration
    void reload();
    int getCountdown();
    int getTeamSize();
    int getMaxPlayers();
    int getMinPlayers();
    int getRoundTime();
    int getRoundTimeSeconds();
    int getActivePotions();
    int getActiveKits();
    int getWinningReward();
    int getKillReward();
    
    // Game Rule Setters
    void setCountdown(int countdown);
    void setTeamSize(int teamSize);
    void setMaxPlayers(int maxPlayers);
    void setMinPlayers(int minPlayers);
    void setRoundTime(int roundTime);
    void setRoundTimeSeconds(int roundTimeSeconds);
    void setActivePotions(int activePotions);
    void setActiveKits(int activeKits);
    void setWinningReward(int winningReward);
    void setKillReward(int killReward);
    
    // Feature Flags Getters
    boolean isActivateTeams();
    boolean isActivateKits();
    boolean isActivateShop();
    boolean isActivateAirdrops();
    boolean isActivateMysql();
    boolean isActivateScoreboard();
    boolean isActivateDeathmatch();
    
    // Feature Flags Setters
    void setActivateTeams(boolean activate);
    void setActivateKits(boolean activate);
    void setActivateShop(boolean activate);
    void setActivateAirdrops(boolean activate);
    void setActivateMysql(boolean activate);
    void setActivateScoreboard(boolean activate);
    void setActivateDeathmatch(boolean activate);
    
    // Game State Getters
    boolean isDeathmatch();
    boolean isJoinable();
    boolean isPause();
    boolean isBuild();
    boolean isMove();
    boolean isVoteallowed();
    boolean isTeamallowed();
    boolean isKitallowed();
    boolean isForcearena();
    boolean isStartOnJoin();
    boolean isTickStarted();
    boolean isMySQL();
    boolean isGameServer();
    boolean isCompassOnSpawn();
    boolean isAllowOutsideChat();
    boolean isChangeGamerules();
    boolean isFriendlyFire();
    boolean isJoinStarted();
    boolean isEnableRewards();
    boolean isBroadcastStarting();
    
    // Game State Setters
    void setDeathmatch(boolean deathmatch);
    void setJoinable(boolean joinable);
    void setPause(boolean pause);
    void setBuild(boolean build);
    void setMove(boolean move);
    void setVoteallowed(boolean voteallowed);
    void setTeamallowed(boolean teamallowed);
    void setKitallowed(boolean kitallowed);
    void setForcearena(boolean forcearena);
    void setStartOnJoin(boolean startOnJoin);
    void setTickStarted(boolean tickStarted);
    void setMySQL(boolean mysql);
    void setGameServer(boolean gameServer);
    void setCompassOnSpawn(boolean compassOnSpawn);
    void setAllowOutsideChat(boolean allowOutsideChat);
    void setChangeGamerules(boolean changeGamerules);
    void setFriendlyFire(boolean friendlyFire);
    void setJoinStarted(boolean joinStarted);
    void setEnableRewards(boolean enableRewards);
    void setBroadcastStarting(boolean broadcastStarting);
    
    // String Getters
    String getLanguage();
    String getHost();
    String getPort();
    String getDatabase();
    String getUser();
    
    // String Setters
    void setLanguage(String language);
    void setHost(String host);
    void setPort(String port);
    void setDatabase(String database);
    void setUser(String user);
}
