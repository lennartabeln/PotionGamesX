package com.tw0far.potiongames.managers;

/**
 * Interface for all manager classes.
 * Managers coordinate specific aspects of the plugin.
 */
public interface IManager {
    /**
     * Called when the plugin enables
     */
    void onEnable();
    
    /**
     * Called when the plugin disables
     */
    void onDisable();
}
