package com.tw0far.potiongames.commands;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.util.LegacyArenaImporter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * /pg importarenas [lobbynumber] [file] - Import arenas from a legacy
 * {@code arenadata.yml} (global {@code pg.arenas.<n>}) into a lobby of the
 * current {@code lobbies.yml} layout.
 */
public class ImportArenasCommand implements ICommand {
    /** File the legacy release wrote into the plugin folder. */
    private static final String DEFAULT_FILE = "arenadata.yml";

    private final PotionGamesX plugin;

    public ImportArenasCommand(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "importarenas";
    }

    @Override
    public String getPermission() {
        return "pg.setup";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Integer lobbyId = parseLobby(args);
        if (lobbyId == null) {
            sender.sendMessage(Messages.CommandImportarenasUsageText());
            return false;
        }
        Lobby lobby = plugin.getGame().getLobby(lobbyId);
        if (lobby == null) {
            sender.sendMessage(Messages.LobbyDoesNotExist());
            return false;
        }

        String fileName = args.length >= 3 ? args[2] : DEFAULT_FILE;
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.isFile()) {
            sender.sendMessage(Messages.ArenasImportFileMissing(fileName));
            return false;
        }

        YamlConfiguration legacy = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sourceArenas = legacy.getConfigurationSection("pg.arenas");
        ConfigurationSection lobbySection = Settings.lobbies.getConfigurationSection("pg.lobbies." + lobbyId);

        LegacyArenaImporter.Result result = LegacyArenaImporter.importArenas(sourceArenas, lobbySection);
        if (result.isEmpty()) {
            sender.sendMessage(Messages.ArenasImportNoArenas(fileName));
            return true;
        }

        try {
            Settings.lobbies.save(Settings.lobbiesFile);
        } catch (IOException ex) {
            plugin.getLogger().warning("Could not save lobbies.yml after arena import: " + ex.getMessage());
            sender.sendMessage(Messages.FileSaveFailed());
            return false;
        }

        // Re-read the arenas so the imported ones are usable without a restart.
        lobby.load();

        sender.sendMessage(Messages.ArenasImported(result.imported().size(), lobbyId));
        if (!result.skipped().isEmpty()) {
            sender.sendMessage(Messages.ArenasImportSkipped(result.skipped().size()));
        }
        if (result.unnamed() > 0) {
            sender.sendMessage(Messages.ArenasImportUnnamed(result.unnamed()));
        }
        return true;
    }

    private Integer parseLobby(String[] args) {
        if (args.length >= 2) {
            try {
                return Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return plugin.getConfigManager().isSingleLobby() ? Integer.valueOf(1) : null;
    }

    @Override
    public String getUsage() {
        return Messages.CommandImportarenasUsageText();
    }
}
