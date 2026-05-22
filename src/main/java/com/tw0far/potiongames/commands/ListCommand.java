package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Lobby;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import com.tw0far.potiongames.models.Settings;

/**
 * /pg list - Open GUI with all lobbies
 */
public class ListCommand implements ICommand {
    private final PotionGames plugin;
    
    public ListCommand(PotionGames plugin) {
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
    public boolean requiresGameServer() {
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // Build an inventory GUI listing all lobbies with basic info
        java.util.List<Lobby> lobbies = plugin.getGame().getLobbies();
        if (lobbies == null || lobbies.isEmpty()) {
            player.sendMessage(Component.text("No lobbies available.").color(NamedTextColor.RED));
            return true;
        }

        int size = ((lobbies.size() + 8) / 9) * 9; // round up to multiple of 9
        Inventory inv = Bukkit.createInventory(null, Math.max(9, size), Settings.prefix.append(Component.text("Lobby List").color(NamedTextColor.DARK_AQUA)));

        for (Lobby lobby : lobbies) {
            int id = lobby.getId();
            int active = lobby.getActivePlayers().size();
            int max = plugin.getMaxPlayers();

            ItemStack item = new ItemStack(Material.CHEST);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                // Display name MUST be the numeric lobby id so existing click handler can parse it
                meta.displayName(Component.text(Integer.toString(id)).color(NamedTextColor.GOLD));
                java.util.List<Component> lore = new java.util.ArrayList<>();
                lore.add(Component.text("Lobby ").color(NamedTextColor.GRAY).append(Component.text("#" + id).color(NamedTextColor.AQUA)));
                lore.add(Component.text("State: ").color(NamedTextColor.GREEN).append(Component.text(lobby.getState().name()).color(NamedTextColor.AQUA)));
                lore.add(Component.text("Players: ").color(NamedTextColor.GREEN).append(Component.text(active + "/" + max).color(NamedTextColor.AQUA)));
                meta.lore(lore);
                item.setItemMeta(meta);
            }
            inv.addItem(item);
        }

        player.openInventory(inv);
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg list";
    }
}
