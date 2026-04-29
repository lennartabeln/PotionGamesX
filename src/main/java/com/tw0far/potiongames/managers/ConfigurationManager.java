package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Centralized configuration manager.
 * Extracts all config getters/setters from main PotionGames class.
 */
public class ConfigurationManager implements IConfigurationManager {
    private final PotionGames plugin;
    private final FileConfiguration config;
    
    // Game settings
    private int countdown = 60;
    private int reset = 10;
    private int maxPlayers = 24;
    private int minPlayers = 12;
    private int playerAmount = 0;
    private int teamSize = 2;
    private int teamAmount;
    private int roundTime = 30;
    private int roundTimeSeconds;
    private int activePotions = 19;
    private int activeKits = 6;
    private int winningReward = 100;
    private int killReward = 10;
    
    // Feature flags
    private boolean activateTeams = true;
    private boolean activateKits = true;
    private boolean activateShop = true;
    private boolean activateAirdrops = true;
    private boolean activateMysql = false;
    private boolean activateScoreboard = true;
    private boolean activateDeathmatch = true;
    private boolean enableRewards = false;
    private boolean broadcastStarting = false;
    
    // Behavior flags
    private boolean startOnJoin = false;
    private boolean friendlyFire = false;
    private boolean joinStarted = false;
    private boolean tickStarted = false;
    private boolean compassOnSpawn = false;
    private boolean allowOutsideChat = false;
    private boolean changeGamerules = true;
    private boolean gameServer = true;
    private boolean lobbySystem = false;
    private boolean mysql = false;
    private boolean deathmatch = false;
    private boolean joinable = true;
    private boolean pause = false;
    private boolean build = false;
    private boolean move = true;
    private boolean voteallowed = false;
    private boolean teamallowed = false;
    private boolean kitallowed = false;
    private boolean forcearena = false;
    private boolean addlobby = false;
    private boolean addarena = false;
    private boolean dellobby = false;
    private boolean delarena = false;
    private boolean reload = false;
    private boolean checkArenas = false;
    private boolean singleArena = false;
    
    // Database
    private String language = "en_US";
    private String host = "localhost";
    private String port = "3306";
    private String database = "potiongames";
    private String user = "root";
    
    public ConfigurationManager(PotionGames plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        // Calculate derived values
        this.teamAmount = maxPlayers / teamSize;
        this.roundTimeSeconds = roundTime * 60;
    }
    
    @Override
    public void onEnable() {
        loadAllConfig();
    }
    
    @Override
    public void onDisable() {
        // No cleanup needed
    }
    
    private void loadAllConfig() {
        // Game settings
        countdown = config.getInt("countdown", 60);
        reset = config.getInt("reset", 10);
        maxPlayers = config.getInt("maxPlayers", 24);
        minPlayers = config.getInt("minPlayers", 12);
        playerAmount = config.getInt("playerAmount", 0);
        teamSize = config.getInt("teamSize", 2);
        teamAmount = maxPlayers / teamSize;
        roundTime = config.getInt("roundTime", 30);
        roundTimeSeconds = roundTime * 60;
        activePotions = config.getInt("activePotions", 19);
        activeKits = config.getInt("activeKits", 6);
        winningReward = config.getInt("winningReward", 100);
        killReward = config.getInt("killReward", 10);
        
        // Feature flags
        activateTeams = config.getBoolean("activateTeams", true);
        activateKits = config.getBoolean("activateKits", true);
        activateShop = config.getBoolean("activateShop", true);
        activateAirdrops = config.getBoolean("activateAirdrops", true);
        activateMysql = config.getBoolean("activateMySQL", false);
        activateScoreboard = config.getBoolean("activateScoreboard", true);
        activateDeathmatch = config.getBoolean("activateDeathmatch", true);
        enableRewards = config.getBoolean("enableRewards", false);
        broadcastStarting = config.getBoolean("broadcastStarting", false);
        
        // Behavior flags
        startOnJoin = config.getBoolean("startOnJoin", false);
        friendlyFire = config.getBoolean("friendlyFire", false);
        joinStarted = config.getBoolean("joinStarted", true);
        tickStarted = config.getBoolean("tickStarted", false);
        compassOnSpawn = config.getBoolean("compassOnSpawn", false);
        allowOutsideChat = config.getBoolean("allowOutsideChat", false);
        changeGamerules = config.getBoolean("changeGamerules", true);
        gameServer = config.getBoolean("gameServer", true);
        lobbySystem = config.getBoolean("lobbySystem", false);
        mysql = config.getBoolean("mysql", false);
        deathmatch = config.getBoolean("deathmatch", false);
        joinable = config.getBoolean("joinable", true);
        pause = config.getBoolean("pause", false);
        build = config.getBoolean("build", false);
        move = config.getBoolean("move", true);
        voteallowed = config.getBoolean("voteallowed", false);
        teamallowed = config.getBoolean("teamallowed", false);
        kitallowed = config.getBoolean("kitallowed", false);
        forcearena = config.getBoolean("forcearena", false);
        addlobby = config.getBoolean("addlobby", false);
        addarena = config.getBoolean("addarena", false);
        dellobby = config.getBoolean("dellobby", false);
        delarena = config.getBoolean("delarena", false);
        reload = config.getBoolean("reload", false);
        checkArenas = config.getBoolean("checkArenas", false);
        singleArena = config.getBoolean("singleArena", false);
        
        // Database
        language = config.getString("language", "en_US");
        host = config.getString("mysql.host", "localhost");
        port = config.getString("mysql.port", "3306");
        database = config.getString("mysql.database", "potiongames");
        user = config.getString("mysql.user", "root");
    }
    
    @Override
    public void reload() {
        plugin.reloadConfig();
        loadAllConfig();
    }
    
    // Game Rule Getters
    @Override
    public int getCountdown() { return countdown; }
    
    @Override
    public int getReset() { return reset; }
    
    @Override
    public int getTeamSize() { return teamSize; }
    
    @Override
    public int getMaxPlayers() { return maxPlayers; }
    
    @Override
    public int getMinPlayers() { return minPlayers; }
    
    @Override
    public int getPlayerAmount() { return playerAmount; }
    
    @Override
    public int getTeamAmount() { return teamAmount; }
    
    @Override
    public int getRoundTime() { return roundTime; }
    
    @Override
    public int getRoundTimeSeconds() { return roundTimeSeconds; }
    
    @Override
    public int getActivePotions() { return activePotions; }
    
    @Override
    public int getActiveKits() { return activeKits; }
    
    @Override
    public int getWinningReward() { return winningReward; }
    
    @Override
    public int getKillReward() { return killReward; }
    
    // Game Rule Setters
    @Override
    public void setCountdown(int countdown) { this.countdown = countdown; }
    
    @Override
    public void setReset(int reset) { this.reset = reset; }
    
    @Override
    public void setTeamSize(int teamSize) { 
        this.teamSize = teamSize;
        this.teamAmount = maxPlayers / teamSize;
    }
    
    @Override
    public void setMaxPlayers(int maxPlayers) { 
        this.maxPlayers = maxPlayers;
        this.minPlayers = maxPlayers / 2;
        this.teamAmount = maxPlayers / teamSize;
    }
    
    @Override
    public void setMinPlayers(int minPlayers) { this.minPlayers = minPlayers; }
    
    @Override
    public void setPlayerAmount(int playerAmount) { this.playerAmount = playerAmount; }
    
    @Override
    public void setTeamAmount(int teamAmount) { this.teamAmount = teamAmount; }
    
    @Override
    public void setRoundTime(int roundTime) { 
        this.roundTime = roundTime;
        this.roundTimeSeconds = roundTime * 60;
    }
    
    @Override
    public void setRoundTimeSeconds(int roundTimeSeconds) { this.roundTimeSeconds = roundTimeSeconds; }
    
    @Override
    public void setActivePotions(int activePotions) { this.activePotions = activePotions; }
    
    @Override
    public void setActiveKits(int activeKits) { this.activeKits = activeKits; }
    
    @Override
    public void setWinningReward(int winningReward) { this.winningReward = winningReward; }
    
    @Override
    public void setKillReward(int killReward) { this.killReward = killReward; }
    
    // Feature Flags Getters
    @Override
    public boolean isActivateTeams() { return activateTeams; }
    
    @Override
    public boolean isActivateKits() { return activateKits; }
    
    @Override
    public boolean isActivateShop() { return activateShop; }
    
    @Override
    public boolean isActivateAirdrops() { return activateAirdrops; }
    
    @Override
    public boolean isActivateMysql() { return activateMysql; }
    
    @Override
    public boolean isActivateScoreboard() { return activateScoreboard; }
    
    @Override
    public boolean isActivateDeathmatch() { return activateDeathmatch; }
    
    // Feature Flags Setters
    @Override
    public void setActivateTeams(boolean activate) { this.activateTeams = activate; }
    
    @Override
    public void setActivateKits(boolean activate) { this.activateKits = activate; }
    
    @Override
    public void setActivateShop(boolean activate) { this.activateShop = activate; }
    
    @Override
    public void setActivateAirdrops(boolean activate) { this.activateAirdrops = activate; }
    
    @Override
    public void setActivateMysql(boolean activate) { this.activateMysql = activate; }
    
    @Override
    public void setActivateScoreboard(boolean activate) { this.activateScoreboard = activate; }
    
    @Override
    public void setActivateDeathmatch(boolean activate) { this.activateDeathmatch = activate; }
    
    // Game State Getters
    @Override
    public boolean isDeathmatch() { return deathmatch; }
    
    @Override
    public boolean isJoinable() { return joinable; }
    
    @Override
    public boolean isPause() { return pause; }
    
    @Override
    public boolean isBuild() { return build; }
    
    @Override
    public boolean isMove() { return move; }
    
    @Override
    public boolean isVoteallowed() { return voteallowed; }
    
    @Override
    public boolean isTeamallowed() { return teamallowed; }
    
    @Override
    public boolean isKitallowed() { return kitallowed; }
    
    @Override
    public boolean isForcearena() { return forcearena; }
    
    @Override
    public boolean isStartOnJoin() { return startOnJoin; }
    
    @Override
    public boolean isTickStarted() { return tickStarted; }
    
    @Override
    public boolean isMySQL() { return mysql; }
    
    @Override
    public boolean isLobbySystem() { return lobbySystem; }
    
    @Override
    public boolean isGameServer() { return gameServer; }
    
    @Override
    public boolean isAddlobby() { return addlobby; }
    
    @Override
    public boolean isAddarena() { return addarena; }
    
    @Override
    public boolean isDellobby() { return dellobby; }
    
    @Override
    public boolean isDelarena() { return delarena; }
    
    @Override
    public boolean isReload() { return reload; }
    
    @Override
    public boolean isCompassOnSpawn() { return compassOnSpawn; }
    
    @Override
    public boolean isAllowOutsideChat() { return allowOutsideChat; }
    
    @Override
    public boolean isChangeGamerules() { return changeGamerules; }
    
    @Override
    public boolean isCheckArenas() { return checkArenas; }
    
    @Override
    public boolean isSingleArena() { return singleArena; }
    
    @Override
    public boolean isFriendlyFire() { return friendlyFire; }
    
    @Override
    public boolean isJoinStarted() { return joinStarted; }
    
    @Override
    public boolean isEnableRewards() { return enableRewards; }
    
    @Override
    public boolean isBroadcastStarting() { return broadcastStarting; }
    
    // Game State Setters
    @Override
    public void setDeathmatch(boolean deathmatch) { this.deathmatch = deathmatch; }
    
    @Override
    public void setJoinable(boolean joinable) { this.joinable = joinable; }
    
    @Override
    public void setPause(boolean pause) { this.pause = pause; }
    
    @Override
    public void setBuild(boolean build) { this.build = build; }
    
    @Override
    public void setMove(boolean move) { this.move = move; }
    
    @Override
    public void setVoteallowed(boolean voteallowed) { this.voteallowed = voteallowed; }
    
    @Override
    public void setTeamallowed(boolean teamallowed) { this.teamallowed = teamallowed; }
    
    @Override
    public void setKitallowed(boolean kitallowed) { this.kitallowed = kitallowed; }
    
    @Override
    public void setForcearena(boolean forcearena) { this.forcearena = forcearena; }
    
    @Override
    public void setStartOnJoin(boolean startOnJoin) { this.startOnJoin = startOnJoin; }
    
    @Override
    public void setTickStarted(boolean tickStarted) { this.tickStarted = tickStarted; }
    
    @Override
    public void setMySQL(boolean mysql) { this.mysql = mysql; }
    
    @Override
    public void setLobbySystem(boolean lobbySystem) { this.lobbySystem = lobbySystem; }
    
    @Override
    public void setGameServer(boolean gameServer) { this.gameServer = gameServer; }
    
    @Override
    public void setAddlobby(boolean addlobby) { this.addlobby = addlobby; }
    
    @Override
    public void setAddarena(boolean addarena) { this.addarena = addarena; }
    
    @Override
    public void setDellobby(boolean dellobby) { this.dellobby = dellobby; }
    
    @Override
    public void setDelarena(boolean delarena) { this.delarena = delarena; }
    
    @Override
    public void setReload(boolean reload) { this.reload = reload; }
    
    @Override
    public void setCompassOnSpawn(boolean compassOnSpawn) { this.compassOnSpawn = compassOnSpawn; }
    
    @Override
    public void setAllowOutsideChat(boolean allowOutsideChat) { this.allowOutsideChat = allowOutsideChat; }
    
    @Override
    public void setChangeGamerules(boolean changeGamerules) { this.changeGamerules = changeGamerules; }
    
    @Override
    public void setCheckArenas(boolean checkArenas) { this.checkArenas = checkArenas; }
    
    @Override
    public void setSingleArena(boolean singleArena) { this.singleArena = singleArena; }
    
    @Override
    public void setFriendlyFire(boolean friendlyFire) { this.friendlyFire = friendlyFire; }
    
    @Override
    public void setJoinStarted(boolean joinStarted) { this.joinStarted = joinStarted; }
    
    @Override
    public void setEnableRewards(boolean enableRewards) { this.enableRewards = enableRewards; }
    
    @Override
    public void setBroadcastStarting(boolean broadcastStarting) { this.broadcastStarting = broadcastStarting; }
    
    // String Getters
    @Override
    public String getLanguage() { return language; }
    
    @Override
    public String getHost() { return host; }
    
    @Override
    public String getPort() { return port; }
    
    @Override
    public String getDatabase() { return database; }
    
    @Override
    public String getUser() { return user; }
    
    // String Setters
    @Override
    public void setLanguage(String language) { this.language = language; }
    
    @Override
    public void setHost(String host) { this.host = host; }
    
    @Override
    public void setPort(String port) { this.port = port; }
    
    @Override
    public void setDatabase(String database) { this.database = database; }
    
    @Override
    public void setUser(String user) { this.user = user; }
}
