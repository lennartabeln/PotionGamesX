package com.tw0far.potiongames.util;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Moves a player to another server through the BungeeCord plugin messaging channel.
 *
 * <p>Plugin messages are only forwarded when the server runs in proxy mode
 * ({@code spigot.yml -> settings.bungeecord: true}) and a proxy is actually connected.
 * On a standalone server the message is accepted and silently dropped, which looks like
 * "/pg leave does nothing" - so this helper checks the requirement first and, when it is
 * not met, disconnects the player with the hub message instead of leaving them on the
 * game server.</p>
 */
public final class BungeeConnector {

    private static final String CHANNEL = "BungeeCord";
    private static boolean warned;

    private BungeeConnector() {
    }

    /**
     * True when a proxy can receive "Connect" messages from this server.
     *
     * <p>{@code Server#spigot()} is deprecated for removal but is still the only public way to
     * read {@code settings.bungeecord}, so the deprecation is suppressed here instead of
     * adding a reflection hack.</p>
     */
    @SuppressWarnings({"deprecation", "removal"})
    public static boolean available(PotionGamesX plugin) {
        if (plugin == null) {
            return false;
        }
        try {
            boolean proxyMode = Bukkit.getServer().spigot().getConfig()
                .getBoolean("settings.bungeecord", false);
            boolean registered = Bukkit.getServer().getMessenger()
                .isOutgoingChannelRegistered(plugin, CHANNEL);
            return proxyMode && registered;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Sends the player to {@code server} through the proxy. Without proxy support the player
     * is disconnected with the "connecting" message, because staying on a game server after
     * leaving is never what the caller wants.
     */
    public static void send(PotionGamesX plugin, Player player, String server) {
        if (plugin == null || player == null || !player.isOnline()) {
            return;
        }
        if (available(plugin)) {
            try {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);
                out.writeUTF("Connect");
                out.writeUTF(server);
                player.sendPluginMessage(plugin, CHANNEL, b.toByteArray());
                return;
            } catch (IOException | RuntimeException e) {
                plugin.getLogger().log(Level.WARNING,
                    "[PotionGamesX] Could not send " + player.getName() + " to " + server, e);
            }
        } else if (!warned) {
            warned = true;
            plugin.getLogger().warning("[PotionGamesX] pg.gameServer is true but BungeeCord mode is off "
                + "(spigot.yml -> settings.bungeecord). Players cannot be moved to '" + server
                + "', they are disconnected with the hub message instead.");
        }
        player.kick(Messages.LobbyConnecting(server));
    }
}
