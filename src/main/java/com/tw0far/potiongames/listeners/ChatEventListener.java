package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.managers.SetupInputMode;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;

import com.tw0far.potiongames.util.PlayerTaskDispatcher;
import com.tw0far.potiongames.util.SetupInputValidator;

/**
 * Handles player chat events.
 * Manages team/spectator chat separation, message filtering, and setup mode input.
 */
public class ChatEventListener implements Listener {
    private final PotionGamesX plugin;

    public ChatEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent e) {
        Player p = e.getPlayer();

        // Handle setup mode chat input first
        if (plugin.getSetupStateManager().isSetupPlayer(p)) {
            String messageText = PlainTextComponentSerializer.plainText().serialize(e.message());
            handleSetupChatInput(e, p, messageText);
            return;
        }

        boolean isActive = plugin.getGame().isActivePlayer(p);
        boolean isSpectator = plugin.getGame().isSpectatorPlayer(p);

        if (!isActive && !isSpectator) {
            return;
        }

        // Multi-lobby mode: get lobby ID and filter chat per lobby
        String lobbyId = null;
        if (isActive) {
            lobbyId = plugin.getGame().getPlayerLobby(p);
        } else if (isSpectator) {
            lobbyId = plugin.getGame().getSpectatorLobby(p);
        }

        if (lobbyId != null && !plugin.getConfigManager().isAllowOutsideChat()) {
            // Filter recipients: only players in the same lobby (plus console)
            e.viewers().clear();
            e.viewers().add(Bukkit.getConsoleSender());

            // Add active players in same lobby
            for (Player active : plugin.getGame().getPlayersInLobby(lobbyId)) {
                e.viewers().add(active);
            }

            // Add spectators in same lobby
            for (Player spec : plugin.getGame().getSpectatorsInLobby(lobbyId)) {
                e.viewers().add(spec);
            }

            // Add colored name prefix based on team
            boolean activateTeams;
            try {
                int id = Integer.parseInt(lobbyId);
                Lobby lobby = plugin.getGame().getLobby(id);
                activateTeams = lobby != null ? lobby.isActivateTeams() : plugin.getConfigManager().isActivateTeams();
            } catch (NumberFormatException e2) {
                activateTeams = plugin.getConfigManager().isActivateTeams();
            }
            if (activateTeams) {
                Lobby chatLobby = plugin.getGame().getLobbyByPlayer(p);
                String teamName = chatLobby != null ? chatLobby.getPlayerTeam(p) : null;
                if (teamName != null) {
                    Component currentMessage = e.message();
                    if (currentMessage != null) {
                        Component prefixed = Component.text("[" + teamName + "] ").color(NamedTextColor.YELLOW).append(currentMessage);
                        if (prefixed != null) {
                            e.message(prefixed);
                        }
                    }
                }
            }
        }
    }

    /**
     * Handle chat input during setup mode
     */
    private void handleSetupChatInput(AsyncChatEvent e, Player p, String input) {
        SetupInputMode mode = plugin.getSetupStateManager().getInputMode(p);
        // No pending input mode: let normal chat through instead of swallowing it
        if (mode == null) {
            return;
        }

        // AsyncChatEvent runs asynchronously. Cancel here, then perform all player,
        // world, and configuration work on the player's owning region thread.
        e.setCancelled(true);
        PlayerTaskDispatcher.dispatch(
                action -> p.getScheduler().run(plugin, task -> action.run(), null),
                () -> processSetupChatInput(p, input, mode));
    }

    private void processSetupChatInput(Player p, String input, SetupInputMode mode) {
        var state = plugin.getSetupStateManager();
        // The player may have cancelled or changed the mode before the entity task ran.
        if (state.getInputMode(p) != mode) {
            return;
        }
        Integer selectedLobby = state.getSelectedLobby(p);

        switch (mode) {
            case ADD_LOBBY -> {
                // Input is the lobby ID to add; invalid IDs keep the input mode active
                Integer lobbyId = SetupInputValidator.parseLobbyId(input);
                if (lobbyId == null) {
                    p.sendMessage(Messages.InvalidLobbyId());
                    return;
                }
                plugin.getSetupHandler().addLobby(p, lobbyId);
                state.clearInputMode(p);
            }
            case DEL_LOBBY -> {
                // Input is the lobby ID to delete
                try {
                    int lobbyId = Integer.parseInt(input);
                    plugin.getSetupHandler().removeLobby(p, lobbyId);
                    state.clearInputMode(p);
                } catch (NumberFormatException ex) {
                    p.sendMessage(Component.text("Invalid lobby ID. Must be a number.").color(NamedTextColor.RED));
                }
            }
            case ADD_ARENA -> {
                // Input is the arena name to add
                if (selectedLobby != null) {
                    plugin.getSetupHandler().addArena(p, input, selectedLobby);
                    state.clearInputMode(p);
                } else {
                    p.sendMessage(Component.text("Please select a lobby first!").color(NamedTextColor.RED));
                }
            }
            case DEL_ARENA -> {
                // Input is the arena name to delete
                if (selectedLobby != null) {
                    plugin.getSetupHandler().removeArena(p, input, selectedLobby);
                    state.clearInputMode(p);
                } else {
                    p.sendMessage(Component.text("Please select a lobby first!").color(NamedTextColor.RED));
                }
            }
        }
    }
}
