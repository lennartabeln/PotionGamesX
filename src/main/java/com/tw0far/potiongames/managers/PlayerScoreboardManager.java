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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Builds and maintains the per-player sidebar scoreboard.
 *
 * <p>The layout follows the legacy release: while a lobby waits it shows the
 * arena, team, kit and player count; during a round it shows the remaining
 * time and the player's kills. Values live in team prefixes so other
 * listeners can keep updating them (for example the team selector).</p>
 */
public class PlayerScoreboardManager {

    /** Name of the sidebar objective; also used by the legacy-compatible listeners. */
    public static final String OBJECTIVE_NAME = "main-content";

    private static final String TEAM_TEAM = "team";
    private static final String TEAM_KIT = "kit";
    private static final String TEAM_PLAYERS = "players";
    private static final String TEAM_TIME = "timeScoreboard";
    private static final String TEAM_MAP = "map";
    private static final String TEAM_KILLS = "kills";

    /** Entries that belong to a team so its prefix is rendered as the value. */
    private static final String ENTRY_MAP = " ";
    private static final String ENTRY_TEAM = "   ";
    private static final String ENTRY_KIT = "     ";
    private static final String ENTRY_PLAYERS = "       ";
    private static final String ENTRY_TIME = "          ";
    private static final String ENTRY_KILLS = "        ";

    private static final String[] ALL_ENTRIES = {
        "pg-label-map", ENTRY_MAP,
        "pg-label-team", ENTRY_TEAM,
        "pg-label-kit", ENTRY_KIT,
        "pg-label-players", ENTRY_PLAYERS,
        "pg-label-time", ENTRY_TIME,
        "pg-label-kills", ENTRY_KILLS,
    };

    private final PotionGamesX plugin;
    /** Players that currently own a managed sidebar, mapped to the layout in use. */
    private final Map<UUID, Boolean> roundLayout = new HashMap<>();

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

        registerTeam(board, TEAM_MAP, ENTRY_MAP);
        registerTeam(board, TEAM_TEAM, ENTRY_TEAM);
        registerTeam(board, TEAM_KIT, ENTRY_KIT);
        registerTeam(board, TEAM_PLAYERS, ENTRY_PLAYERS);
        registerTeam(board, TEAM_TIME, ENTRY_TIME);
        registerTeam(board, TEAM_KILLS, ENTRY_KILLS);

        label(objective, "pg-label-map", Messages.MapLabel());
        label(objective, "pg-label-team", Messages.TeamLabel());
        label(objective, "pg-label-kit", Messages.KitLabel());
        label(objective, "pg-label-players", darkAqua(Messages.PlayersText()));
        label(objective, "pg-label-time", Messages.TimeLabel());
        label(objective, "pg-label-kills", darkAqua(Messages.KillsText()));

        player.setScoreboard(board);
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

    /** Refreshes the sidebar of every participant of a lobby (called once per tick). */
    public void update(Lobby lobby) {
        if (lobby == null || !enabled()) {
            return;
        }
        for (Participant participant : lobby.getParticipants()) {
            Player player = participant.getPlayer();
            if (player == null || !player.isOnline()) {
                continue;
            }
            if (player.getScoreboard().getObjective(OBJECTIVE_NAME) == null) {
                show(player);
            }
            render(player, lobby);
        }
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
            applyLayout(objective, round);
            roundLayout.put(player.getUniqueId(), round);
        }

        prefix(board, TEAM_MAP, arenaName(lobby));
        prefix(board, TEAM_TEAM, teamName(lobby, player));
        prefix(board, TEAM_KIT, kitName(lobby, player));
        prefix(board, TEAM_PLAYERS, lobby.getPlayerCount() + "/" + lobby.getMaxPlayers());

        if (round) {
            prefix(board, TEAM_TIME, minutes(lobby.getGameTimer()));
            prefix(board, TEAM_KILLS, Integer.toString(lobby.getKills(player)));
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

    private void registerTeam(Scoreboard board, String name, String entry) {
        Team team = board.getTeam(name);
        if (team == null) {
            team = board.registerNewTeam(name);
        }
        if (!team.hasEntry(entry)) {
            team.addEntry(entry);
        }
        team.prefix(darkAqua(Messages.ScoreboardNoneText()));
    }

    private void label(Objective objective, String entry, Component display) {
        Score score = objective.getScore(entry);
        score.customName(display);
    }

    private void prefix(Scoreboard board, String teamName, String value) {
        Team team = board.getTeam(teamName);
        if (team != null) {
            team.prefix(darkAqua(value));
        }
    }

    /**
     * Shows only the lines that belong to the requested layout. Scores of
     * unused lines are reset so old lines disappear immediately.
     */
    private void applyLayout(Objective objective, boolean round) {
        for (String entry : ALL_ENTRIES) {
            Score score = objective.getScore(entry);
            if (score.isScoreSet()) {
                score.resetScore();
            }
        }
        if (round) {
            objective.getScore("pg-label-time").setScore(3);
            objective.getScore(ENTRY_TIME).setScore(2);
            objective.getScore("pg-label-kills").setScore(1);
            objective.getScore(ENTRY_KILLS).setScore(0);
        } else {
            objective.getScore("pg-label-map").setScore(7);
            objective.getScore(ENTRY_MAP).setScore(6);
            objective.getScore("pg-label-team").setScore(5);
            objective.getScore(ENTRY_TEAM).setScore(4);
            objective.getScore("pg-label-kit").setScore(3);
            objective.getScore(ENTRY_KIT).setScore(2);
            objective.getScore("pg-label-players").setScore(1);
            objective.getScore(ENTRY_PLAYERS).setScore(0);
        }
    }

    /** Remaining round time, clamped so the sidebar never shows a negative value. */
    private static String minutes(int seconds) {
        return (Math.max(0, seconds) / 60) + " min";
    }

    private static Component darkAqua(String text) {
        return Component.text(text).color(NamedTextColor.DARK_AQUA);
    }
}