package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Central command dispatcher.
 * Routes commands to individual command handlers instead of one monolithic class.
 */
public class CommandDispatcher implements CommandExecutor, TabCompleter {
    private final PotionGamesX plugin;
    private final Map<String, ICommand> commands = new HashMap<>();

    public CommandDispatcher(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    public void register() {
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
                sender.sendMessage(Component.text("PotionGamesX Commands:").color(NamedTextColor.AQUA));
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
            String msg = Messages.CommandUnknownText();
            sender.sendMessage(Component.text(msg).color(NamedTextColor.RED));
            return true;
        }

        // Check if command requires a player
        if (!isPlayer) {
            sender.sendMessage(Component.text("This command can only be used by players!").color(NamedTextColor.RED));
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (command.getPermission() != null && !player.hasPermission(command.getPermission())) {
            player.sendMessage(createError("You don't have permission to use this command!"));
            return true;
        }


        // Execute command
        try {
            return command.execute(player, args);
        } catch (Exception e) {
            player.sendMessage(createError(Messages.CommandExecutionErrorText()));
            plugin.getLogger().severe("Error executing command " + subCommand + ": " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }

    public static Component createInfo(String message) {
        return Component.text(message).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
    }

    public static Component createSuccess(String message) {
        return Component.text(message).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false);
    }

    public static Component createError(String message) {
        return Component.text(message).color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> result = new ArrayList<>();
            String prefix = args[0].toLowerCase();
            for (ICommand command : commands.values()) {
                String name = command.getName().toLowerCase();
                if (name.startsWith(prefix)) {
                    if (command.getPermission() == null || sender.hasPermission(command.getPermission())) {
                        result.add(name);
                    }
                }
            }
            Collections.sort(result);
            return result;
        }
        return Collections.emptyList();
    }
}

