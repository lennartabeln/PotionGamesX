package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Manages game state, rounds, and main game loop.
 * Extracted from monolithic PotionGames class.
 */
public class GameManager implements IManager {
    private final PotionGames plugin;
    private final ConfigurationManager config;
    private final DatabaseManager database;
    
    private BukkitTask gameTask;
    private int tick = 0;
    private int countdown;
    private int reset;
    
    private Set<Player> activePlayers = new HashSet<>();
    private Set<Player> spectators = new HashSet<>();
    private Map<String, GameState> lobbyStates = new HashMap<>();
    
    public enum GameState {
        LOBBY, WAITING, GAME_RUNNING, DEATHMATCH, ENDED
    }
    
    public GameManager(PotionGames plugin, ConfigurationManager config, DatabaseManager database) {
        this.plugin = plugin;
        this.config = config;
        this.database = database;
        this.countdown = config.getCountdown();
        this.reset = 10;
    }
    
    @Override
    public void onEnable() {
        startGameLoop();
    }
    
    @Override
    public void onDisable() {
        stopGameLoop();
        clearAllEffects();
    }
    
    @Override
    public void reload() {
        stopGameLoop();
        this.countdown = config.getCountdown();
        startGameLoop();
    }
    
    /**
     * Start the main game tick loop
     */
    private void startGameLoop() {
        gameTask = plugin.getServer().getScheduler().runTaskTimer(plugin, this::gameTick, 0L, 1L);
    }
    
    /**
     * Stop the game tick loop
     */
    private void stopGameLoop() {
        if (gameTask != null) {
            gameTask.cancel();
        }
    }
    
    /**
     * Main game tick - runs every 1 tick (20 ticks = 1 second)
     */
    private void gameTick() {
        tick++;
        
        if (tick % 20 == 0) { // Every second
            updateCountdowns();
        }
        
        // Update per-lobby game states
        // - Check arena voting
        // - Teleport players when ready
        // - Handle deathmatch transitions
        // - Apply effects
        updateGameStates();
    }
    
    /**
     * Update game states for all active lobbies
     */
    private void updateGameStates() {
        // This is called every second
        // Each lobby handles its own state transitions through its game loop
        // Managed by PotionGames.tickLobby() method
    }
    
    /**
     * Update all active countdowns
     */
    private void updateCountdowns() {
        countdown--;
        if (countdown <= 0) {
            onCountdownComplete();
        }
    }
    
    /**
     * Called when lobby countdown reaches zero
     */
    private void onCountdownComplete() {
        // Game start logic is handled by Lobby class
        // when startGame() is called by commands or countdown completion
        // This method resets the countdown for the next round
        countdown = config.getCountdown();
    }
    
    /**
     * Add player to active game
     */
    public void addActivePlayer(Player player) {
        activePlayers.add(player);
        database.createPlayer(player);
        clearEffects(player);
    }
    
    /**
     * Remove player from active game
     */
    public void removeActivePlayer(Player player) {
        activePlayers.remove(player);
        clearEffects(player);
    }
    
    /**
     * Add player to spectator mode
     */
    public void addSpectator(Player player) {
        spectators.add(player);
        player.setGameMode(GameMode.SPECTATOR);
    }
    
    /**
     * Remove player from spectator mode
     */
    public void removeSpectator(Player player) {
        spectators.remove(player);
        player.setGameMode(GameMode.SURVIVAL);
    }
    
    /**
     * Clear all potion effects from a player
     */
    public void clearEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
    
    /**
     * Clear effects from all active players
     */
    public void clearAllEffects() {
        for (Player player : activePlayers) {
            clearEffects(player);
        }
        for (Player player : spectators) {
            clearEffects(player);
        }
    }
    
    /**
     * Apply potion effect to player
     */
    public void applyEffect(Player player, PotionEffectType type, int duration, int amplifier) {
        player.addPotionEffect(new PotionEffect(type, duration, amplifier, true, false, true));
    }
    
    // Getters - return unmodifiable views instead of defensive copies
    public Set<Player> getActivePlayers() { 
        return Collections.unmodifiableSet(activePlayers); 
    }
    public Set<Player> getSpectators() { 
        return Collections.unmodifiableSet(spectators); 
    }
    public int getCountdown() { return countdown; }
    public int getTick() { return tick; }
    
    // Setters
    public void setCountdown(int value) { countdown = value; }
}
