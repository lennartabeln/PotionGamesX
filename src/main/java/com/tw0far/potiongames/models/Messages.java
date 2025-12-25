package com.tw0far.potiongames.models;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Messages {
    private static String get(String key, String def) {
        String lang = Settings.language != null ? Settings.language : "en_US";
        String primaryPath = lang + ".messages." + key;
        String value = Settings.messages.getString(primaryPath, null);
        if (value == null) {
            value = Settings.messages.getString("en_US.messages." + key, def);
        }
        return value != null ? value : def;
    }

    private static Component prefix(Component message) {
        return Settings.prefix.append(message);
    }

    public static final Component LobbyAdded(int lobbyId) {
        String pattern = get("lobby.added", "Lobby %d added successfully.");
        return prefix(Component.text(String.format(pattern, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component LobbyAddedFailed(int lobbyId) {
        String pattern = get("lobby.added_failed", "Failed to add lobby %d.");
        return prefix(Component.text(String.format(pattern, lobbyId)).color(NamedTextColor.RED));
    }

    public static final Component LobbyRemoved(int lobbyId) {
        String pattern = get("lobby.removed", "Lobby %d removed successfully.");
        return prefix(Component.text(String.format(pattern, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component LobbyRemovedFailed(int lobbyId) {
        String pattern = get("lobby.removed_failed", "Failed to remove lobby %d.");
        return prefix(Component.text(String.format(pattern, lobbyId)).color(NamedTextColor.RED));
    }

    public static final Component ArenaAdded(String arenaName, int lobbyId) {
        String pattern = get("arena.added", "Arena %s added successfully. (Lobby: %d)");
        return prefix(Component.text(String.format(pattern, arenaName, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component ArenaRemoved(String arenaName, int lobbyId) {
        String pattern = get("arena.removed", "Arena %s removed successfully. (Lobby: %d)");
        return prefix(Component.text(String.format(pattern, arenaName, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component SpawnAdded(int spawnId, String arenaName, int lobbyId) {
        String pattern = get("spawn.added", "Spawn %d added successfully. (Arena: %s, Lobby: %d)");
        return prefix(Component.text(String.format(pattern, spawnId, arenaName, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static final Component SpawnRemoved(int spawnId, String arenaName, int lobbyId) {
        String pattern = get("spawn.removed", "Spawn %d removed successfully. (Arena: %s, Lobby: %d)");
        return prefix(Component.text(String.format(pattern, spawnId, arenaName, lobbyId)).color(NamedTextColor.GREEN));
    }

    public static Component AdminPleaseInform() {
        return prefix(Component.text(get("admin.please_inform", "Please inform an admin!")).color(NamedTextColor.RED));
    }

    public static Component AirdropFallingAt(String location) {
        return prefix(Component.text(get("airdrop.falling_at", "Airdrop is falling at") + " " + location).color(NamedTextColor.GOLD));
    }

    public static Component AirdropFallingHere() {
        return prefix(Component.text(get("airdrop.falling_here", "Airdrop is falling at your location!")).color(NamedTextColor.GOLD));
    }

    public static Component ArenaCouldNotLoad() {
        return prefix(Component.text(get("arena.could_not_load", "Could not load an arena!")).color(NamedTextColor.RED));
    }

    public static Component ArenaForced(String arenaName) {
        return prefix(Component.text(arenaName + " " + get("arena.forced", "has been forced as arena!")).color(NamedTextColor.GREEN));
    }

    public static Component ArenaNotArena(String arenaName) {
        return prefix(Component.text(arenaName + " " + get("arena.not_arena", "is not an arena!")).color(NamedTextColor.RED));
    }

    public static Component ArenaNotExists() {
        return prefix(Component.text(get("arena.not_exists", "This arena does not exists!")).color(NamedTextColor.RED));
    }

    public static Component ArenaSelector() {
        return prefix(Component.text("--------------" + get("arena.selector", "Arena-Selector") + "--------------").color(NamedTextColor.GRAY));
    }

    public static Component ArenaSelectorTitle() {
        return prefix(Component.text(get("arena.selector", "Arena-Selector")).color(NamedTextColor.DARK_AQUA));
    }

    public static Component BuildToggle(boolean enabled) {
        NamedTextColor color = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
        return prefix(Component.text(get("build.toggle", "Build") + ": " + enabled).color(color));
    }

    public static Component CoinSingle() {
        return prefix(Component.text(get("coin.single", "Coin")).color(NamedTextColor.GOLD));
    }

    public static Component CoinsLabel() {
        return prefix(Component.text(get("coins.label", "Coins")).color(NamedTextColor.GOLD));
    }

    public static Component CommandsLabel() {
        return prefix(Component.text("--------------" + get("commands.label",  "Commands") + "--------------").color(NamedTextColor.GRAY));
    }

    public static Component DatabaseClosed() {
        return prefix(Component.text(get("database.closed", "Connection to database closed!")).color(NamedTextColor.GREEN));
    }

    public static Component DatabaseCloseFailed() {
        return prefix(Component.text(get("database.close_failed", "Failed to close connection to database! For more information see console.")).color(NamedTextColor.RED));
    }

    public static Component DatabaseConnectFailed() {
        return prefix(Component.text(get("database.connect_failed", "Connection to database failed! For more information see console.")).color(NamedTextColor.RED));
    }

    public static Component DatabaseConnected() {
        return prefix(Component.text(get("database.connected", "Connection to database established!")).color(NamedTextColor.GREEN));
    }

    public static Component DeadLabel(Component playerName, Component message) {
        return prefix(Component.text(get("dead.label", "[Dead]")).color(NamedTextColor.DARK_RED).append(Component.text(" " + playerName + ": " + message).color(NamedTextColor.DARK_AQUA)));
    }

    public static Component DeathmatchStarted() {
        return prefix(Component.text(get("deathmatch.started", "Deathmatch started!")).color(NamedTextColor.RED));
    }

    public static Component DeathmatchStartingIn(int time) {
        return prefix(Component.text(get("deathmatch.starting_in", "Deathmatch is starting in") + " " + time).color(NamedTextColor.GOLD));
    }

    public static Component DeathmatchTeleportIn(int time) {
        return prefix(Component.text(get("deathmatch.teleport_in", "Teleporting to deathmatch arena in") + " " + time).color(NamedTextColor.YELLOW));
    }

    public static Component DeathmatchTeleportNow() {
        return prefix(Component.text(get("deathmatch.teleport_now", "Teleporting to deathmatch arena now!")).color(NamedTextColor.YELLOW));
    }

    public static Component DeathsLabel(int deaths) {
        return prefix(Component.text(get("deaths.label", "Deaths") + ": ").color(NamedTextColor.GREEN).append(Component.text(deaths).color(NamedTextColor.AQUA)));
    }

    public static Component DurationLabel() {
        return prefix(Component.text(get("duration.label", "Duration")).color(NamedTextColor.AQUA));
    }

    public static Component ErrorGeneric() {
        return prefix(Component.text(get("error.generic", "An error occurred")).color(NamedTextColor.RED));
    }

    public static Component FileSaveFailed() {
        return prefix(Component.text(get("file.save_failed", "File loading / saving fail! For more information see console.")).color(NamedTextColor.RED));
    }

    public static Component GameAlreadyStarted() {
        return prefix(Component.text(get("game.already_started", "The game has already started!")).color(NamedTextColor.RED));
    }

    public static Component GameStarted() {
        return prefix(Component.text(get("game.started", "The game has been started!")).color(NamedTextColor.GREEN));
    }

    public static Component GameNotEnoughPlayers() {
        return prefix(Component.text(get("game.not_enough_players", "Not enough players to start the game!")).color(NamedTextColor.RED));
    }

    public static Component GameStartsIn(int time) {
        return prefix(Component.text(get("game.starts_in", "The game starts in") + ": ").color(NamedTextColor.GREEN).append(Component.text(time).color(NamedTextColor.AQUA)));
    }

    public static Component GameStartsNow() {
        return prefix(Component.text(get("game.starts_now", "The game starts now!")).color(NamedTextColor.GREEN));
    }

    public static Component GameWaiting(int players, int minPlayers) {
        return Component.text(String.format(get("game.waiting", "Waiting for players! [%d/%d]"), players, minPlayers)).color(NamedTextColor.GRAY);
    }

    public static Component HeadSet() {
        return prefix(Component.text(get("head.set", "Head successfully set!")).color(NamedTextColor.GREEN));
    }

    public static Component HelpUsePgHelp() {
        return prefix(Component.text(get("help.use_pg_help", "Use /pg help for help!")).color(NamedTextColor.AQUA));
    }

    public static Component JoinCouldNotJoinLobby() {
        return prefix(Component.text(get("join.could_not_join_lobby", "Could not join lobby!")).color(NamedTextColor.RED));
    }

    public static Component JoinSuccess() {
        return prefix(Component.text(get("join.success", "Successfully joined lobby")).color(NamedTextColor.GREEN));
    }

    public static Component KDLabel(double kd) {
        return prefix(Component.text(get("kd.label", "K/D") + ": ").color(NamedTextColor.GREEN).append(Component.text(kd).color(NamedTextColor.AQUA)));
    }

    public static Component KillReward(int amount) {
        return prefix(Component.text(get("kill.reward", "For killing a player you get") + " " + amount).color(NamedTextColor.GOLD));
    }

    public static Component KilledBy() {
        return prefix(Component.text(get("killed.by", "was killed by")).color(NamedTextColor.DARK_RED));
    }

    public static Component KilledBy(String playerName) {
        return prefix(Component.text(get("killed.by", "was killed by") + " " + playerName).color(NamedTextColor.DARK_RED));
    }

    public static Component KillsLabel(int kills) {
        return prefix(Component.text(get("kills.label", "Kills") + ": ").color(NamedTextColor.GREEN).append(Component.text(kills).color(NamedTextColor.AQUA)));
    }

    public static Component KitNowHave() {
        return prefix(Component.text(get("kit.now_have", "You now have the kit")).color(NamedTextColor.GREEN));
    }

    public static Component KitNowHave(String kitName) {
        return prefix(Component.text(get("kit.now_have", "You now have the kit") + " " + kitName).color(NamedTextColor.GREEN));
    }

    public static Component KitSelector() {
        return prefix(Component.text(get("kit.selector", "Kit-Selector")).color(NamedTextColor.AQUA));
    }

    public static Component LeaveLabel() {
        return prefix(Component.text(get("leave.label", "Leave")).color(NamedTextColor.RED));
    }

    public static Component LobbyCouldNotSpawn() {
        return prefix(Component.text(get("lobby.could_not_spawn", "could not be teleported to a spawn!")).color(NamedTextColor.RED));
    }

    public static Component LobbyCouldNotTeleport() {
        return prefix(Component.text(get("lobby.could_not_teleport", "could not be teleported to the lobby!")).color(NamedTextColor.RED));
    }

    public static Component LobbyDisabled() {
        return prefix(Component.text(get("lobby.disabled", "Lobby disabled!")).color(NamedTextColor.GRAY));
    }

    public static Component LobbyDoesNotExist() {
        return prefix(Component.text(get("lobby.does_not_exist", "This lobby does not exists!")).color(NamedTextColor.RED));
    }

    public static Component ArenaDoesNotExist() {
        return prefix(Component.text(get("arena.does_not_exist", "This arena does not exists!")).color(NamedTextColor.RED));
    }

    public static Component LobbyEnabled() {
        return prefix(Component.text(get("lobby.enabled", "Lobby enabled!")).color(NamedTextColor.GREEN));
    }

    public static Component LobbyRemovedSuccess() {
        return prefix(Component.text(get("lobby.removed_success", "Lobby successfully removed!")).color(NamedTextColor.GREEN));
    }

    public static Component LobbyStartingBroadcast(String lobbyId) {
        String pattern = get("lobby.starting_broadcast", "Lobby %s is starting! Join with /pg join %s");
        return prefix(Component.text(String.format(pattern, lobbyId, lobbyId)).color(NamedTextColor.GRAY));
    }

    public static Component LobbySuccessSet() {
        return prefix(Component.text(get("lobby.success_set", "Lobby successfully set!")).color(NamedTextColor.GREEN));
    }

    public static Component LossesLabel(int losses) {
        return prefix(Component.text(get("lost.label", "Losses") + ": ").color(NamedTextColor.GREEN).append(Component.text(losses).color(NamedTextColor.AQUA)));
    }

    public static Component NoPlayerFound() {
        return prefix(Component.text(get("no_player_found", "No player found!")).color(NamedTextColor.YELLOW));
    }

    public static Component PauseToggle(boolean paused) {
        NamedTextColor color = paused ? NamedTextColor.GREEN : NamedTextColor.RED;
        return prefix(Component.text(get("pause.toggle", "Pause") + ": " + paused).color(color));
    }

    public static Component SignPlace(int place) {
        return Component.text(get("sign.place", "Place") + ": #" + place);
    }

    public static Component SignWins(int wins) {
        return Component.text(get("sign.wins", "Wins") + ": " + wins);
    }

    public static Component SignKD(double kd) {
        return Component.text(get("sign.kd", "K/D") + ": " + kd);
    }

    public static Component PlayerFinder() {
        return prefix(Component.text(get("player.finder", "Player-Finder")).color(NamedTextColor.DARK_AQUA));
    }

    public static Component PlayersLabel() {
        return prefix(Component.text(get("players.label", "Players")).color(NamedTextColor.GRAY));
    }

    public static Component PluginReloaded() {
        return prefix(Component.text(get("plugin.reloaded", "Plugin successfully reloaded!")).color(NamedTextColor.GREEN));
    }

    public static Component PluginStarted() {
        return prefix(Component.text(get("plugin.started", "Plugin started successfully!")).color(NamedTextColor.GREEN));
    }

    public static Component PluginStopped() {
        return prefix(Component.text(get("plugin.stopped", "Plugin stopped successfully!")).color(NamedTextColor.RED));
    }

    public static Component PriceLabel() {
        return prefix(Component.text(get("price.label", "Price")).color(NamedTextColor.GOLD));
    }

    public static Component RandomLabel() {
        return prefix(Component.text(get("random.label", "Random")).color(NamedTextColor.AQUA));
    }

    public static Component RankwallCouldNotUpdate() {
        return prefix(Component.text(get("rankwall.could_not_update", "Could not update Rank-Wall!")).color(NamedTextColor.RED));
    }

    public static Component RewardLabel() {
        return prefix(Component.text(get("reward.label", "Reward")).color(NamedTextColor.GOLD));
    }

    public static Component RoundNobodyWon() {
        return prefix(Component.text(get("round.nobody_won", "Nobody won this round!")).color(NamedTextColor.YELLOW));
    }

    public static Component RoundSecondsRemaining(int seconds) {
        return prefix(Component.text(seconds + " " + get("round.seconds_remaining", "seconds remaining to end this round!")).color(NamedTextColor.GOLD));
    }

    public static Component RoundMinutesRemaining(int minutes) {
        return prefix(Component.text(minutes + " " + get("round.minutes_remaining", "minutes remaining to end this round!")).color(NamedTextColor.GOLD));
    }

    public static Component RoundsLabel(int rounds) {
        return prefix(Component.text(get("rounds.label", "Rounds") + ": ").color(NamedTextColor.GREEN).append(Component.text(rounds).color(NamedTextColor.AQUA)));
    }

    public static Component SelectorTeam() {
        return prefix(Component.text(get("selector.team", "Team-Selector")).color(NamedTextColor.AQUA));
    }

    public static Component ServerStopped() {
        return prefix(Component.text(get("server.stopped", "Server stopped!")).color(NamedTextColor.RED));
    }

    public static Component ShopLabel() {
        return prefix(Component.text(get("shop.label", "Shop")).color(NamedTextColor.AQUA));
    }

    public static Component SignSet() {
        return prefix(Component.text(get("sign.set", "Sign successfully set!")).color(NamedTextColor.GREEN));
    }

    public static Component SpawnInvalid() {
        return prefix(Component.text(get("spawn.invalid", "is not a valid spawn!")).color(NamedTextColor.RED));
    }

    public static Component StatsLabel() {
        return prefix(Component.text("--------------" + get("stats.label", "Stats") + "--------------").color(NamedTextColor.GRAY));
    }

    public static Component TeamAlreadyFull() {
        return prefix(Component.text(get("team.already_full", "This team is already full!")).color(NamedTextColor.RED));
    }

    public static Component TeamNowIn(String teamName) {
        return prefix(Component.text(get("team.now_in", "You are now in team") + " " + teamName).color(NamedTextColor.GREEN));
    }

    public static Component TeleportToArenaFailed() {
        return prefix(Component.text(get("teleport.to_arena_failed", "Could not teleport to arena!")).color(NamedTextColor.RED));
    }

    public static Component TeleportToDmFailed() {
        return prefix(Component.text(get("teleport.to_dm_failed", "Could not teleport to deathmatch arena!")).color(NamedTextColor.RED));
    }

    public static Component TeleportToLobbyIn(int time) {
        return prefix(Component.text(get("teleport.to_lobby_in", "Teleporting to lobby in") + " " + time).color(NamedTextColor.YELLOW));
    }

    public static Component TeleportToLobbyNow() {
        return prefix(Component.text(get("teleport.to_lobby_now", "Teleporting to lobby now!")).color(NamedTextColor.YELLOW));
    }

    public static Component TntExtremelyExplosive() {
        return prefix(Component.text(get("tnt.extremely_explosive", "Extremely explosive TNT")).color(NamedTextColor.DARK_AQUA));
    }

    public static Component TypeArenaNameAdd() {
        return prefix(Component.text(get("type.arena_name_add", "Type arena name in chat to add it!")).color(NamedTextColor.YELLOW));
    }

    public static Component TypeArenaNameRemove() {
        return prefix(Component.text(get("type.arena_name_remove", "Type arena name in chat to remove it!")).color(NamedTextColor.YELLOW));
    }

    public static Component TypeLobbyNumberAdd() {
        return prefix(Component.text(get("type.lobby_number_add", "Type lobby number in chat to add it!")).color(NamedTextColor.YELLOW));
    }

    public static Component TypeLobbyNumberRemove() {
        return prefix(Component.text(get("type.lobby_number_remove", "Type lobby number in chat to remove it!")).color(NamedTextColor.YELLOW));
    }

    public static Component UpdateAvailable(String currentVersion, String newVersion) {
        return prefix(Component.text(String.format(get("update.available", "There is a new update available. %s -> %s"), currentVersion, newVersion)).color(NamedTextColor.GREEN));
    }

    public static Component UpdateCheckerError() {
        return prefix(Component.text(get("update.checker_error", "Update-Checker-Error")).color(NamedTextColor.RED));
    }

    public static Component UpdateNotAvailable() {
        return prefix(Component.text(get("update.not_available", "There is not a new update available.")).color(NamedTextColor.GRAY));
    }

    public static Component VoteLabel() {
        return prefix(Component.text(get("vote.label", "Votes")).color(NamedTextColor.AQUA));
    }

    public static Component VoteYouHaveVotedFor(String arena) {
        return prefix(Component.text(get("vote.you_have_voted_for", "You have voted for") + " " + arena).color(NamedTextColor.GREEN));
    }

    public static Component WillBePlayed(String arena) {
        return prefix(Component.text(arena + " " + get("will_be_played", "will be played!")).color(NamedTextColor.GOLD));
    }

    public static Component WinnerHasWonTheGame(String winner) {
        return prefix(Component.text(winner + " " + get("winner.has_won_the_game", "has won the game!")).color(NamedTextColor.GOLD));
    }

    public static Component WinsLabel(int wins) {
        return prefix(Component.text(get("won.label", "Wins") + ": ").color(NamedTextColor.GREEN).append(Component.text(wins).color(NamedTextColor.AQUA)));
    }

    public static Component YouBlockAbove() {
        return prefix(Component.text(get("you.block_above", "You have a block above you!")).color(NamedTextColor.YELLOW));
    }

    public static Component YouInSpectatorMode() {
        return prefix(Component.text(get("you.in_spectator_mode", "in spectator mode")).color(NamedTextColor.GRAY));
    }

    public static Component YouNotEmptyBottle() {
        return prefix(Component.text(get("you.not_empty_bottle", "You not have an empty bottle!")).color(NamedTextColor.RED));
    }

    public static Component YouNotEnoughCoins() {
        return prefix(Component.text(get("you.not_enough_coins", "You not have enough Coins!")).color(NamedTextColor.RED));
    }
}
