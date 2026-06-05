package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.entity.Player;

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
        return false;
    }
    
    @Override
    public boolean execute(Player player, String[] args) {
        try {
            if (plugin.getSetupStateManager().isSetupPlayer(player)) {
                plugin.getSetupHandler().exitSetup(player);
                player.sendMessage(Messages.raw("setup.disabled", "Setup mode disabled."));
            } else {
                plugin.getSetupHandler().setup(player);
                player.sendMessage(Messages.raw("setup.enabled", "Setup mode enabled."));
            }
        } catch (Exception ex) {
            player.sendMessage(Messages.ErrorGeneric());
        }
        return true;
    }
    
    @Override
    public String getUsage() {
        return Messages.raw("help.setup_usage", "/pg setup - Start setup mode (requires pg.setup)");
    }
}
