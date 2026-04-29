package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Central command dispatcher.
 * Routes commands to individual command handlers instead of one monolithic class.
 */
public class CommandDispatcher implements CommandExecutor {
    private final PotionGames plugin;
    private final Map<String, ICommand> commands = new HashMap<>();
    
    public CommandDispatcher(PotionGames plugin) {
        this.plugin = plugin;
        registerCommands();
    }
    
    /**
     * Register all command handlers
     */
    private void registerCommands() {
        // Player commands
        registerCommand(new JoinCommand(plugin));
        registerCommand(new LeaveCommand(plugin));
        registerCommand(new ListCommand(plugin));
        registerCommand(new StatsCommand(plugin));
        registerCommand(new HelpCommand(plugin));
        registerCommand(new ReloadCommand(plugin));
        registerCommand(new VersionCommand(plugin));
        
        // Admin commands - Setup
        registerCommand(new SetupCommand(plugin));
        registerCommand(new SetLobbyCommand(plugin));
        registerCommand(new DelLobbyCommand(plugin));
        registerCommand(new AddArenaCommand(plugin));
        registerCommand(new DelArenaCommand(plugin));
        registerCommand(new AddSpawnCommand(plugin));
        registerCommand(new DelSpawnCommand(plugin));
        registerCommand(new AddDeathmatchCommand(plugin));
        registerCommand(new DelDeathmatchCommand(plugin));
        registerCommand(new JoinSignCommand(plugin));
        registerCommand(new HeadP1Command(plugin));
        registerCommand(new HeadP2Command(plugin));
        registerCommand(new HeadP3Command(plugin));
        registerCommand(new SignP1Command(plugin));
        registerCommand(new SignP2Command(plugin));
        registerCommand(new SignP3Command(plugin));
        
        // Admin commands - Game control
        registerCommand(new BuildCommand(plugin));
        registerCommand(new PauseCommand(plugin));
        registerCommand(new ForceCommand(plugin));
        registerCommand(new StartCommand(plugin));
    }
    
    /**
     * Register a command handler
     */
    public void registerCommand(ICommand command) {
        commands.put(command.getName().toLowerCase(), command);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtil.createError("Only players can use this command!"));
            return true;
        }
        
        Player player = (Player) sender;
        
        // Show help if no args
        if (args.length == 0) {
            return commands.get("help").execute(player, args);
        }
        
        String subCommand = args[0].toLowerCase();
        
        // Get and execute command
        ICommand command = commands.get(subCommand);
        if (command == null) {
            player.sendMessage(MessageUtil.createError("Unknown command! Use /pg help for help."));
            return true;
        }
        
        // Check permission
        if (command.getPermission() != null && !player.hasPermission(command.getPermission())) {
            player.sendMessage(MessageUtil.createError("You don't have permission to use this command!"));
            return true;
        }
        
        // Check if game server required
        if (command.requiresGameServer() && !plugin.isGameServer()) {
            player.sendMessage(MessageUtil.createError("This is not a game server!"));
            return true;
        }
        
        // Execute command
        try {
            return command.execute(player, args);
        } catch (Exception e) {
            player.sendMessage(MessageUtil.createError("An error occurred while executing this command!"));
            plugin.getLogger().severe("Error executing command " + subCommand + ": " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
    
    /**
     * Show available commands to player
     */
    public void showCommands(Player player) {
        player.sendMessage(Component.text("═════════ PotionGames Commands ═════════").color(NamedTextColor.AQUA));
        
        for (ICommand command : commands.values()) {
            if (command.getPermission() == null || player.hasPermission(command.getPermission())) {
                player.sendMessage(Component.text("  " + command.getUsage()).color(NamedTextColor.GRAY));
            }
        }
        
        player.sendMessage(Component.text("═════════════════════════════════════════").color(NamedTextColor.AQUA));
    }
}
