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
            plugin.addSetupPlayer(player);
            plugin.savePlayerInventory(player, player.getInventory().getContents());
            plugin.savePlayerArmor(player, player.getInventory().getArmorContents());
            plugin.savePlayerLevel(player, player.getLevel());
            plugin.savePlayerExp(player, player.getExp());
            plugin.savePlayerLocation(player, player.getLocation());
            plugin.savePlayerGameMode(player, player.getGameMode());
            
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
