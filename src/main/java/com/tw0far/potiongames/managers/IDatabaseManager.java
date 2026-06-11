package com.tw0far.potiongames.managers;

import java.sql.ResultSet;

/**
 * Interface for database operations (MySQL/SQLite).
 * Abstracts database connectivity and player statistics management.
 *
 * Supports both MySQL and SQLite backends.
 */
public interface IDatabaseManager extends IManager {

    // ===== Connection Management =====
    /**
     * Establish database connection (MySQL or SQLite).
     * Creates Stats table if not exists.
     */
    void connect();

    /**
     * Check if database connection is active.
     *
     * @return true if connected, false otherwise
     */
    boolean isConnected();

    /**
     * Close database connection.
     */
    void closeConnection();

    // ===== Raw Database Operations =====
    /**
     * Execute an update query (INSERT, UPDATE, DELETE, CREATE TABLE, etc.)
     *
     * @param qry SQL query string
     */
    void update(String qry);

    /**
     * Execute a select query and return ResultSet.
     *
     * @param qry SQL query string
     * @return ResultSet from query or null if error
     */
    ResultSet query(String qry);

    // ===== Player Management =====
    /**
     * Check if player exists in database.
     *
     * @param uuid Player's UUID string
     * @return true if player exists, false otherwise
     */
    boolean playerExists(String uuid);

    /**
     * Create new player record in database with default stats (0 for all values).
     *
     * @param uuid Player's UUID string
     */
    void createPlayer(String uuid);

    // ===== Player Statistics Getters =====
    /**
     * Get player's kill count.
     *
     * @param uuid Player's UUID string
     * @return Number of kills (0 if player doesn't exist, creates player)
     */
    int getKills(String uuid);

    /**
     * Get player's death count.
     *
     * @param uuid Player's UUID string
     * @return Number of deaths (0 if player doesn't exist, creates player)
     */
    int getDeaths(String uuid);

    /**
     * Get player's kill/death ratio.
     *
     * @param uuid Player's UUID string
     * @return K/D ratio as double (0 if player doesn't exist, creates player)
     */
    double getKD(String uuid);

    /**
     * Get player's win count.
     *
     * @param uuid Player's UUID string
     * @return Number of wins (0 if player doesn't exist, creates player)
     */
    int getWins(String uuid);

    /**
     * Get player's loss count.
     *
     * @param uuid Player's UUID string
     * @return Number of losses (0 if player doesn't exist, creates player)
     */
    int getLosses(String uuid);

    /**
     * Get player's round count (calculated as wins + losses).
     *
     * @param uuid Player's UUID string
     * @return Number of rounds
     */
    int getRounds(String uuid);

    // ===== Player Statistics Setters =====
    /**
     * Set player's kill count to specific value.
     *
     * @param uuid Player's UUID string
     * @param kills New kill count
     */
    void setKills(String uuid, int kills);

    /**
     * Set player's death count to specific value.
     *
     * @param uuid Player's UUID string
     * @param deaths New death count
     */
    void setDeaths(String uuid, int deaths);

    /**
     * Set player's K/D ratio to specific value.
     *
     * @param uuid Player's UUID string
     * @param kd New K/D ratio
     */
    void setKD(String uuid, double kd);

    /**
     * Set player's win count to specific value.
     *
     * @param uuid Player's UUID string
     * @param wins New win count
     */
    void setWins(String uuid, int wins);

    /**
     * Set player's loss count to specific value.
     *
     * @param uuid Player's UUID string
     * @param losses New loss count
     */
    void setLosses(String uuid, int losses);

    /**
     * Set player's round count to specific value.
     *
     * @param uuid Player's UUID string
     * @param rounds New round count
     */
    void setRounds(String uuid, int rounds);

    // ===== Player Statistics Incrementers =====
    /**
     * Add kills to player's total (calls setKD internally).
     *
     * @param uuid Player's UUID string
     * @param kills Number of kills to add
     */
    void addKills(String uuid, int kills);

    /**
     * Add deaths to player's total (calls setKD internally).
     *
     * @param uuid Player's UUID string
     * @param deaths Number of deaths to add
     */
    void addDeaths(String uuid, int deaths);

    /**
     * Add wins to player's total (calls setRounds internally).
     *
     * @param uuid Player's UUID string
     * @param wins Number of wins to add
     */
    void addWins(String uuid, int wins);

    /**
     * Add losses to player's total (calls setRounds internally).
     *
     * @param uuid Player's UUID string
     * @param losses Number of losses to add
     */
    void addLosses(String uuid, int losses);
}
