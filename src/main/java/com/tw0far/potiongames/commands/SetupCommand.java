package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * /pg setup - Start setup mode
 */
public class SetupCommand implements ICommand {
    private final PotionGames plugin;
    
    public SetupCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() {
        return "setup";
    }
    
    @Override
    public String getPermission() {
        return "pg.setup";
    }
    
    @Override
    public boolean requiresGameServer() {
        return true;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        try {
            plugin.setupPlayer.add(player);
            plugin.inv.put(player.getName(), player.getInventory().getContents());
            plugin.armor.put(player.getName(), player.getInventory().getArmorContents());
            plugin.lvl.put(player.getName(), player.getLevel());
            plugin.exp.put(player.getName(), player.getExp());
            plugin.loc.put(player.getName(), player.getLocation());
            plugin.gm.put(player.getName(), player.getGameMode());
            
            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.setHelmet(null);
            inventory.setChestplate(null);
            inventory.setLeggings(null);
            inventory.setBoots(null);
            
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setLevel(0);
            player.setExp(0);
            player.setGameMode(GameMode.ADVENTURE);
            plugin.clearEffects(player);
            player.setFireTicks(0);
            player.setAllowFlight(true);
            player.setFlying(true);
            
            plugin.setup(player);
        } catch (Exception ex) {
            player.sendMessage(Messages.ErrorGeneric());
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return "/pg setup - Start setup mode (requires pg.setup)";
    }
}
