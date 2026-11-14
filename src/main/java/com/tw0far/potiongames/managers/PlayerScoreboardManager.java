package com.tw0far.potiongames.managers;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Participant;
import com.tw0far.potiongames.models.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Builds and maintains the per-player sidebar scoreboard.
 *
 * <p>Every field is a single line: the translated label is the score entry
 * itself and the value sits in the suffix of that entry's team, so it can be
 * refreshed without touching the entry list. The lobby layout shows arena,
 * team, kit and player count; the round layout shows the remaining time and
 * the player's kills.</p>
 *
 * <p>Labels are rendered as legacy entry strings rather than through
 * {@code Score#customName}: a custom name set before the score exists is
 * dropped, which left the raw internal keys visible on the sidebar.</p>
 */
public class PlayerScoreboardManager {

    /** Name of the sidebar objective; also used by the legacy-compatible listeners. */
    public static final String OBJECTIVE_NAME = "main-content";

    private static final String TEAM_MAP = "map";
    private static final String TEAM_TEAM = "team";
    private static final String TEAM_KIT = "kit";
    private static final String TEAM_PLAYERS = "players";
    private static final String TEAM_TIME = "timeScoreboard";
    private static final String TEAM_KILLS = "kills";

    /** Lobby rows, top to bottom. */
    private static final String[] LOBBY_ROWS = {TEAM_MAP, TEAM_TEAM, TEAM_KIT, TEAM_PLAYERS};
    /** Round rows, top to bottom. */
    private static final String[] ROUND_ROWS = {TEAM_TIME, TEAM_KILLS};
    private static final String[] ALL_ROWS = {
        TEAM_MAP, TEAM_TEAM, TEAM_KIT, TEAM_PLAYERS, TEAM_TIME, TEAM_KILLS
    };

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private final PotionGamesX plugin;
    /** Players that currently own a managed sidebar, mapped to the layout in use. */
    private final Map<UUID, Boolean> roundLayout = new HashMap<>();
    /** Row team name to the score entry that carries its label. */
    private final Map<String, String> entries = new LinkedHashMap<>();

    public PlayerScoreboardManager(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    private boolean enabled() {
        return plugin.getConfigManager() != null && plugin.getConfigManager().isActivateScoreboard();
    }

    /** Creates the sidebar for a player that joins a lobby. */
    public void show(Player player) {
        if (player == null || !enabled()) {
            return;
        }
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, Settings.prefix);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // The labels are the same for every player, so the entry table is
        // rebuilt once here and reused by every sidebar of this manager.
        entries.clear();
        registerRow(board, TEAM_MAP, Messages.MapLabel(), 0);
        registerRow(board, TEAM_TEAM, Messages.TeamLabel(), 1);
        registerRow(board, TEAM_KIT, Messages.KitLabel(), 2);
        registerRow(board, TEAM_PLAYERS, darkAqua(Messages.PlayersText()), 3);
        registerRow(board, TEAM_TIME, Messages.TimeLabel(), 4);
        registerRow(board, TEAM_KILLS, darkAqua(Messages.KillsText()), 5);

        player.setScoreboard(board);
        entryLayout(objective, false);
        roundLayout.put(player.getUniqueId(), false);
    }

    /** Restores the server scoreboard when a player leaves the lobby. */
    public void hide(Player player) {
        if (player == null) {
            return;
        }
        roundLayout.remove(player.getUniqueId());
        if (player.getScoreboard().getObjective(OBJECTIVE_NAME) != null) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    /**
     * Refreshes the sidebar of every participant and spectator of a lobby
     * (called once per tick). Dead players keep watching the round, so they
     * must keep a live sidebar until spectating ends.
     */
    public void update(Lobby lobby) {
        if (lobby == null || !enabled()) {
            return;
        }
        for (Participant participant : lobby.getParticipants()) {
            renderFor(participant.getPlayer(), lobby);
        }
        for (Player spectator : lobby.getSpectatorPlayers()) {
            renderFor(spectator, lobby);
        }
    }

    private void renderFor(Player player, Lobby lobby) {
        if (player == null || !player.isOnline()) {
            return;
        }
        if (player.getScoreboard().getObjective(OBJECTIVE_NAME) == null) {
            show(player);
        }
        render(player, lobby);
    }

    private void render(Player player, Lobby lobby) {
        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective(OBJECTIVE_NAME);
        if (objective == null) {
            return;
        }

        GameStates state = lobby.getState();
        boolean round = state == GameStates.INGAME || state == GameStates.DEATHMATCH;
        Boolean previous = roundLayout.get(player.getUniqueId());
        if (previous == null || previous != round) {
            entryLayout(objective, round);
            roundLayout.put(player.getUniqueId(), round);
        }

        row(board, TEAM_MAP, arenaName(lobby));
        row(board, TEAM_TEAM, teamName(lobby, player));
        row(board, TEAM_KIT, kitName(lobby, player));
        row(board, TEAM_PLAYERS, lobby.getPlayerCount() + "/" + lobby.getCapacity());

        if (round) {
            // The deathmatch has no timer (legacy behaviour), so the frozen clock
            // would be misleading: show how many players are left instead.
            row(board, TEAM_TIME, state == GameStates.DEATHMATCH
                ? lobby.getAlivePlayerCount() + " " + Messages.AliveLabel()
                : clock(lobby.getGameTimer()));
            row(board, TEAM_KILLS, Integer.toString(lobby.getKills(player)));
        }
    }

    private String arenaName(Lobby lobby) {
        Arena arena = lobby.getDisplayArena();
        return arena != null && arena.getName() != null ? arena.getName() : Messages.MapVotingText();
    }

    private String teamName(Lobby lobby, Player player) {
        String team = lobby.getPlayerTeam(player);
        return team != null ? team : Messages.ScoreboardNoneText();
    }

    private String kitName(Lobby lobby, Player player) {
        Participant participant = lobby.getParticipant(player);
        if (participant != null && participant.getKit() != null && participant.getKit().getName() != null) {
            return participant.getKit().getName();
        }
        return Messages.ScoreboardNoneText();
    }

    /** Registers a row team together with the entry that displays its label. */
    private void registerRow(Scoreboard board, String teamName, Component label, int index) {
        String entry = labelEntry(label, index);
        entries.put(teamName, entry);
        Team team = board.getTeam(teamName);
        if (team == null) {
            team = board.registerNewTeam(teamName);
        }
        if (!team.hasEntry(entry)) {
            team.addEntry(entry);
        }
        team.suffix(value(Messages.ScoreboardNoneText()));
    }

    /**
     * The score entry that renders the given label in the sidebar.
     *
     * <p>Entries must be unique within an objective; one invisible reset code
     * per row keeps two identically configured labels apart.</p>
     */
    private static String labelEntry(Component label, int index) {
        return LEGACY.serialize(label) + "\u00A7r".repeat(Math.max(0, index));
    }

    /** Writes a row value into the team suffix, i.e. right after the label. */
    private void row(Scoreboard board, String teamName, String text) {
        Team team = board.getTeam(teamName);
        if (team != null) {
            team.suffix(value(text));
        }
    }

    /**
     * Updates the team row immediately instead of waiting for the next tick.
     * Used by the team selector, whose click already knows the new team.
     */
    public void updateTeamValue(Player player, String text) {
        if (player == null || text == null) {
            return;
        }
        row(player.getScoreboard(), TEAM_TEAM, text);
    }

    /**
     * Shows only the rows that belong to the requested layout. Scores of
     * unused rows are reset so the previous layout disappears immediately.
     */
    private void entryLayout(Objective objective, boolean round) {
        for (String teamName : ALL_ROWS) {
            String entry = entries.get(teamName);
            if (entry == null) {
                continue;
            }
            Score score = objective.getScore(entry);
            if (score.isScoreSet()) {
                score.resetScore();
            }
        }
        String[] rows = round ? ROUND_ROWS : LOBBY_ROWS;
        for (int i = 0; i < rows.length; i++) {
            String entry = entries.get(rows[i]);
            if (entry != null) {
                objective.getScore(entry).setScore(rows.length - 1 - i);
            }
        }
    }

    /**
     * Remaining round time as {@code m:ss}. Clamped so a stalled or negative
     * timer can never push a broken value onto the sidebar.
     */
    static String clock(int seconds) {
        int remaining = Math.max(0, seconds);
        return (remaining / 60) + ":" + String.format("%02d", remaining % 60);
    }

    private static Component value(String text) {
        return Component.text(": ").color(NamedTextColor.GRAY)
                .append(Component.text(text).color(NamedTextColor.AQUA));
    }

    private static Component darkAqua(String text) {
        return Component.text(text).color(NamedTextColor.DARK_AQUA);
    }
}
