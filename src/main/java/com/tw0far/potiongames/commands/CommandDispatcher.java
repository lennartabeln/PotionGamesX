package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
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
        registerCommand(new GameServerCommand(plugin));
        registerCommand(new DatabaseCommand(plugin));
        registerCommand(new ConfigCommand(plugin));
        registerCommand(new StatusCommand(plugin));
        registerCommand(new DebugCommand(plugin));
        registerCommand(new BroadcastCommand(plugin));
        registerCommand(new KickCommand(plugin));
        registerCommand(new TopCommand(plugin));
        
        // Admin commands - Setup
        registerCommand(new SetupCommand(plugin));
        registerCommand(new AddLobbyCommand(plugin));
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
        boolean isPlayer = (sender instanceof Player);
        
        // Show help if no args
        if (args.length == 0) {
            ICommand helpCmd = commands.get("help");
            if (helpCmd != null) {
                if (isPlayer) {
                    return helpCmd.execute((Player) sender, args);
                }
                sender.sendMessage(Component.text("PotionGames Commands:").color(NamedTextColor.AQUA));
                for (ICommand command : commands.values()) {
                    sender.sendMessage(Component.text("  " + command.getUsage()).color(NamedTextColor.GRAY));
                }
                return true;
            }
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        // Get and execute command
        ICommand command = commands.get(subCommand);
        if (command == null) {
            String msg = Messages.raw("command.unknown", "Unknown command! Use /pg help for help.");
            sender.sendMessage(Component.text(msg).color(NamedTextColor.RED));
            return true;
        }
        
        // Check if command requires a player
        if (!isPlayer) {
            // Console-only commands that need player context
            if (subCommand.equals("join") || subCommand.equals("leave")) {
                sender.sendMessage(Component.text("This command can only be used by players!").color(NamedTextColor.RED));
                return true;
            }
            // Allow other commands (reload, etc.) from console
            try {
                return command.execute((Player) sender, args);
            } catch (ClassCastException e) {
                sender.sendMessage(Component.text("This command can only be used by players!").color(NamedTextColor.RED));
                return true;
            }
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if (command.getPermission() != null && !player.hasPermission(command.getPermission())) {
            player.sendMessage(MessageUtil.createError("You don't have permission to use this command!"));
            return true;
        }
        
        // Check if game server required
        if (command.requiresGameServer() && !plugin.getConfigManager().isGameServer()) {
            player.sendMessage(MessageUtil.createError(Messages.raw("command.not_game_server", "This is not a game server!")));
            return true;
        }
        
        // Execute command
        try {
            return command.execute(player, args);
        } catch (Exception e) {
            player.sendMessage(MessageUtil.createError(Messages.raw("command.execution_error", "An error occurred while executing this command!")));
            plugin.getLogger().severe("Error executing command " + subCommand + ": " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
}
