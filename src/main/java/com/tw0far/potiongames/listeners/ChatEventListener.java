package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.entity.Player;

/**
 * Handles player chat events.
 * Manages team/spectator chat separation and message filtering.
 */
public class ChatEventListener implements Listener {
    private final PotionGames plugin;
    
    public ChatEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        
        boolean isActive = plugin.game.isActivePlayer(p);
        boolean isSpectator = plugin.game.isSpectatorPlayer(p);
        
        if (!isActive && !isSpectator) {
            return;
        }
        
        // Multi-lobby mode: get lobby ID and filter chat per lobby
        String lobbyId = null;
        if (isActive) {
            lobbyId = plugin.game.getPlayerLobby(p);
        } else if (isSpectator) {
            lobbyId = plugin.game.getSpectatorLobby(p);
        }
        
        if (lobbyId != null) {
            // Filter recipients: only players in the same lobby
            e.getRecipients().clear();
            
            // Add active players in same lobby
            for (Player active : plugin.game.getPlayersInLobby(lobbyId)) {
                e.getRecipients().add(active);
            }
            
            // Add spectators in same lobby
            for (Player spec : plugin.game.getSpectatorsInLobby(lobbyId)) {
                e.getRecipients().add(spec);
            }
            
            // Add colored name prefix based on team
            if (plugin.isLobbyActivateTeams(lobbyId)) {
                String teamName = plugin.game.getPlayerTeam(p);
                if (teamName != null) {
                    String prefix = "§e[" + teamName + "] §r";
                    e.setMessage(prefix + e.getMessage());
                }
            }
        }
    }
}
