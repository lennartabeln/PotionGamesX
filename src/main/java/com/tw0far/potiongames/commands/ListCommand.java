package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

/**
 * /pg list - Open GUI with all lobbies
 */
public class ListCommand implements ICommand {
    private final PotionGamesX plugin;
    
    public ListCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "list";
    }
    
    @Override
    public String getPermission() {
        return "pg.join";
    }

    
    @Override
    public boolean execute(Player player, String[] args) {
        // Build an inventory GUI listing all lobbies with basic info
        java.util.List<Lobby> lobbies = plugin.getGame().getLobbies();
        if (lobbies == null || lobbies.isEmpty()) {
            player.sendMessage(Messages.raw("list.no_lobbies", "No lobbies available."));
            return true;
        }

        int size = ((lobbies.size() + 8) / 9) * 9; // round up to multiple of 9
        Inventory inv = Bukkit.createInventory(null, Math.max(9, size), Messages.LobbyListTitle());

        for (Lobby lobby : lobbies) {
            int id = lobby.getId();
            int active = lobby.getActivePlayers().size();
            int max = plugin.getConfigManager().getMaxPlayers();

            ItemStack item = new ItemStack(Material.CHEST);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                // Display name MUST be the numeric lobby id so existing click handler can parse it
                meta.displayName(Component.text(Integer.toString(id)).color(NamedTextColor.GOLD));
                java.util.List<Component> lore = new java.util.ArrayList<>();
                lore.add(Component.text(Messages.raw("list.lobby", "Lobby") + " #"+id).color(NamedTextColor.GRAY));
                lore.add(Component.text(Messages.raw("list.state", "State") + ": ").color(NamedTextColor.GREEN).append(Component.text(lobby.getState().name()).color(NamedTextColor.AQUA)));
                lore.add(Component.text(Messages.raw("list.players", "Players") + ": ").color(NamedTextColor.GREEN).append(Component.text(active + "/" + max).color(NamedTextColor.AQUA)));
                meta.lore(lore);
                item.setItemMeta(meta);
            }
            inv.addItem(item);
        }

        if (inv != null) {
            player.openInventory(inv);
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.list_usage", "/pg list");
    }
}
