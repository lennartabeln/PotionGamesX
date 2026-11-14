# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

Nothing yet - new entries belong here and are moved into a version section on release.

## [1.0.0-dev.27] - 2026-11-14

### Added

- Configurable start freeze: `pg.defaults.startDelay` (default `10`, per lobby via `settings.startDelay`), shown in `/pg config`
- The round length is announced when the players land in the arena
- The lobby countdown is visible on the XP bar again
- Vote tally when the arena is revealed (`Votes (3)` plus one line per arena, including `Random`)
- `/pg status` shows per lobby whether a deathmatch is possible (`DM 4 spawns` / `no deathmatch`)

### Changed

- Round time announcements use the legacy thresholds (600, 300, 240, 180, 120, 60, 30, 20, 10, 5..1)
- The join sign header shows `PotionGamesX`; the lobby number moved into the count line
- Sidebar keeps map, team, kit and player count during the round and shows `Alive` instead of a frozen clock in the deathmatch

### Fixed

- A deathmatch requires at least two deathmatch spawns; without them the round ends normally
- Players are frozen for the start delay after the arena teleport (position locked, no damage) and the round starts with `SURVIVAL`
- An emptied round resets immediately instead of running the end countdown
- A timed-out round no longer declares a winner after the draw message
- A lobby without an arena aborts the round start instead of playing in the lobby
- The arena reveal also fires when the lobby countdown is shorter than ten seconds
- Voting and `/pg force` are closed once the countdown starts, so the announced arena is final
- Lobby players take no damage and no hunger, and cannot build or use buckets outside a running round
- The arena is cleaned of dropped items, arrows, TNT and mobs on reset
- Shop and kit defaults are seeded once instead of doubling on every `/pg reload`; a failing bootstrap step no longer skips the following ones
- Chest blocks open on a fresh or half-migrated install (legacy `END_PORTAL_FRAME`/`COMPOSTER` fallback) and loot chests work in the deathmatch
- Dropped the write-only `Lobby#joinable` flag

## [1.0.0-dev.26] - 2026-11-13

### Added

#### Gameplay
- Team-aware win condition: the round ends when a single team remains, and every surviving member earns the win and the reward
- Late-join spectating: with `pg.joinStarted`, players who join a running round become spectators instead of being dropped in as fighters
- `pg.broadcastStarting` announces a starting lobby to the whole server when its countdown begins
- `pg.compassOnSpawn` hands the player-finder compass to everyone at round start
- `pg.changeGamerules` freezes daylight and weather in the arena world during a round and restores the previous values afterwards
- `pg.allowOutsideChat` lets players outside a lobby follow lobby chat (default off, the previous behaviour)

#### Scoreboard and interface
- `pg.activateScoreboard` works again: a per-player sidebar shows map, team, kit and player count while a lobby waits, and remaining time plus kills during a round; leaving the lobby restores the server scoreboard
- `messages.yml` gains the `map`, `team`, `kit`, `time` and `scoreboard.none` labels (en_US/de_DE)
- Inventory items (arena, kit, team, stats, leave) share the `DARK_AQUA` style, and their GUI titles no longer carry a prefix line

#### Loot and shop
- Default loot is seeded on startup again: the `food1/2`, `armour1-5` and `weapons1/2` pools and the documented special chests (`pg.customchests` 1-4, including the Flame Bow, netherite and diamond-armour chests) work on a fresh install; existing admin entries are never overwritten
- The `shop` loot pool (`pg.chestloot.normal.loot.shop`, default `10.0`) rolls glass bottles and coins while a lobby has its shop enabled, so potions can actually be bought
- `Rich Kid` starts every round with 5 coins again (the legacy value) on top of the doubled kill coins

#### Configuration
- One structure for `config.yml`, `chests.yml`, `kits.yml`, `messages.yml` and `shop.yml`
- `plugin.yml` declares its `permissions` with sane defaults: `pg.join`/`pg.leave`/`pg.stats` for everyone, admin nodes op-only
- `api-version: 26.3`, matching the documented "Paper 26.3+" requirement
- Console support for `reload`, `status`, `top`, `version`, `database`, `gameserver`, `config`, `help` and `kick`
- New options (`pg.singleLobby`, `pg.activePotions`) are written into an existing `config.yml` on startup, so upgrades can see them without deleting the file

#### Localization
- `/pg status` and `/pg database` are translated (`status.*`, `database.mode_*` in en_US and de_DE), including the per-lobby line `Lobby 1 - Players: 3/24, Spectators: 1 (INGAME)`
- `/pg kick` feedback is translated as well (`kick.*`)
- New keys `database.not_connected`, `bucket.fill_not_allowed` and `setup.not_a_sign` replace a zeroed stat sheet, a silently ignored bucket and a stack trace

#### Legacy compatibility
- `pg.singleLobby`: single-lobby mode accepts the legacy short setup forms (`/pg addarena <arena>`, `/pg addspawn <arena>`, `/pg joinsign`, ...) and targets lobby 1, while the long form keeps working
- Automatic migration on startup: an existing `arenadata.yml` is imported without a command. Lobbies come from `pg.lobbies.<id>` (spawn from `coords`, settings into `settings.*`), a legacy single-lobby server gets `pg.Lobby.coords`/`pg.Lobby.sign` as lobby 1, the global `pg.arenas.<n>` are copied into every lobby (existing names skipped, spawns and deathmatch spawns verbatim), and a legacy `pg.lobbySystem` becomes `pg.singleLobby`. Nothing is overwritten and the console tells you when `arenadata.yml` can be deleted
- `pg.activePotions` caps how many `shop.yml` entries are shown and buyable (`0` = all)
- The arena selector paper is only handed out when a lobby has more than one arena, like the legacy `singleArena` behaviour

#### Build and tooling
- Java 21 to 25 across all build configs and CI workflows
- The compile fails on javac warnings (`failOnWarning`, `-Werror` with `-Xlint:all`), locally and in CI
- Unit tests (JUnit 5) for the arena spawn ID math, the per-lobby config resolution and the team contract (`LobbyTeamTest`, `LobbyConfigTest`)
- Release bodies are read from `CHANGELOG.md` instead of auto-generated notes

### Changed

#### Commands and permissions
- Incomplete commands print their own `/pg help` line with the plugin prefix instead of ad-hoc usage strings; the help texts follow one shape and cover every command
- Permission nodes match their command names: `/pg version` requires `pg.version` (replaces `pg.update`), `/pg reload` requires `pg.reload` (previously shared `pg.setup`), and `/pg leave` is actually gated by `pg.leave`
- `/pg list` has its own node (`pg.list`, default true), while every other command keeps its previous node
- `pg.admin` grants every admin subcommand through `children:` in `plugin.yml`; before it only changed the `/pg help` list
- `/pg config` is fully localized and prints booleans as `true`/`false`
- `/pg stats <player>` also resolves offline players whose profile Paper has cached
- Removed the redundant spectator fallbacks in `/pg join|force|start|pause|build` (`getPlayerLobby` already covers spectators)

#### Gameplay and state
- Team count is derived from the lobby capacity (`maxPlayers / teamSize`, at least two), players are spread over the emptiest teams, manual picks survive the round start, and the selector is re-seeded after every round
- `roundTime` is stored in seconds internally; `LobbyConfig` is the only place that converts minutes to seconds
- Arena gamerules freeze `ADVANCE_TIME`, `ADVANCE_WEATHER`, `SPAWN_MOBS`, `MOB_GRIEFING` and a fire-spread radius of `0`, and restore the captured values (including a custom radius) afterwards
- Votes have one source of truth (`ArenaStateManager` via `Lobby#recordVote`); the duplicate record on the participant is gone
- The inventory listener was split into `GameInventoryListener`, `GameItemListener` and `SetupInteractionListener`; the six stats-wall commands became one parameterized `StatsWallCommand`
- Setup-mode chat input only swallows chat while an input mode is active

#### Tooling
- `maven-resources-plugin` pinned to 3.5.0 for Eclipse M2E compatibility
- Vault uses jitpack's canonical coordinates (`com.github.MilkBowl`), which removes 18 Maven cache warnings on case-insensitive filesystems
- Deprecated `org.bukkit.GameRule` replaced by `org.bukkit.GameRules` (`ADVANCE_TIME`, `ADVANCE_WEATHER`, `MOB_GRIEFING`, `SPAWN_MOBS`, `FIRE_SPREAD_RADIUS_AROUND_PLAYER`), removing 15 build warnings

#### Prefix and messages
- The prefix reads `[PotionGamesX]` with a dark aqua `X`, and the MOTD fallback uses the same name
- Console lines no longer repeat the prefix; console-bound messages have prefix-free variants
- Every chat message carries the prefix, including `/pg list` without lobbies, setup toggles, bucket guards, setup chat input, `/pg gameserver`, `/pg headpN`/`signpN`, `/pg top` errors, the `/pg status` title and all dispatcher info/error lines
- Grammar fixes in several player-facing messages ("does not exists", "You not have ...")

### Removed

- `/pg broadcast` and its `pg.broadcast` permission (`pg.broadcastStarting` is untouched)
- `/pg importarenas` and its message keys - the startup migration imports the legacy arenas into every lobby
- `/pg debug` and the `pg.debug` permission (the command toggled a field nothing read)
- Hardcoded chest loot and shop data (now configuration), and the monolithic event/command classes they lived in
- `BlockTracker`, `PotionChest`, `LootTable`, `LobbySettings`, `ConfigKeys`, `BlockStateManager` and `PlayerStateManager` (state now lives in `Lobby`/`Game`)
- Dead config knobs with no reader: `logging.*`, `performance.*`, `security.*`
- ~45 dead message methods with their orphaned translation keys, plus 35 keys that no code path could resolve
- 5 no-op listener classes (consume, food, respawn, teleport, explosion), the never-functional move-freeze flag, write-only voting/team maps and `BAD_OMEN` entries 21-27
- The unused participant type enum and the kit id field

### Fixed

#### Rounds and lobbies
- Lobby capacity is capped by the fewest usable arena spawns (join check, join sign, sidebar, lobby list, `/pg status`, server ping); a round without a usable spawn aborts back to `WAITING` instead of half-starting
- The deathmatch starts when the remaining players fit onto the arena's usable deathmatch spawns (`max(2, spawns)`), so two DM spawns keep the legacy behaviour
- Rounds start in `SURVIVAL` with lobby items and leftover effects cleared, so breaking, placing, buckets and flint and steel work again
- Entering the lobby clears armour, offhand, potion effects, fire and health/food, so arena gear and shop potions no longer follow the player into the lobby or the next round
- Votes and team slots are released when a player leaves (`/pg leave`, quit, kick, death, reset), so a disconnected player no longer decides the arena
- Free-for-all lobbies can fight again: the friendly-fire check only compares teams when teams are active, and teammates may attack each other in `DEATHMATCH`
- The player-finder compass points at enemies again in free-for-all and `DEATHMATCH`, and only considers players of the same running lobby
- Explosions in arena worlds no longer remove blocks permanently (build mode still allows reshaping), TNT bonus damage applies once inside active games, and lobby maps are protected outside running games
- A lobby reset only affects its own lobby; the last player leaving ends the round through `ENDING`; the pause flag no longer locks an emptied lobby, and exactly one tick task runs per lobby
- Spectators keep their death location, a live sidebar and get gamemode, inventory, effects, collision and scoreboard restored on every exit path
- A killed player is no longer teleported back to the hub

#### Chests, shop and rewards
- Loot and shop chests work on a fresh install: `chests.yml` ships usable `chestblocks` defaults, empty values are seeded on startup, and the matcher accepts both the legacy string and the list format
- Chest contents no longer regenerate on every open: each chest is stored per round, shop chests open and sell only during `INGAME`/`DEATHMATCH`, GUI drags are cancelled and clicks outside the top inventory are ignored
- Shop prices come from one place: the kit price applies to the matching kit, and a zero or missing `sale` falls back to `cost` instead of making the potion free; `shop.yml` documents `sale` as the kit price
- Shop GUI was unreachable (title mismatch and wrong state gate); purchases count real coin items and can no longer loop forever
- Winning a round pays `pg.winningReward` through Vault, logs failed transactions and spawns the celebratory firework
- `pg.enableRewards: true` without a Vault economy no longer disables the whole plugin; it logs a warning and rewards stay inactive
- Airdrop torch checks the column up to the world's real build height (was hardcoded 320), accepts cave air, and clamps the drop inside the world

#### Scoreboard, GUI and chat
- Sidebar rendered internal entry keys (`pg-label-map`, ...) instead of the configured labels; each field is one line and the round timer shows `m:ss`
- Random team assignment spread players over every free team instead of landing in the team that already had a player; a full selection validates capacity first instead of freezing the server
- A single-map lobby shows its arena name in the sidebar and on the join sign instead of `Voting`
- Dropping items in a lobby is cancelled again (the guard tested `isActivePlayer` and never triggered)
- The legacy audio cues are back: countdown bells, launch firework, blast/dragon sounds at round start, dragon growl at round end, death sound, and click/error sounds in the GUIs
- `/pg joinsign` reports "That is not a sign!" instead of a `ClassCastException`, and `/pg kick` keeps the restored pre-join location

#### Data, reload and server
- Game state is propagated from the `Lobby` model to listeners, so deaths, kills, rewards and in-game block rules work again
- Per-round kills are tracked through `Lobby#addKill` again (the scoreboard parsed its own team prefix and `getKills` was always 0)
- `/pg stats` runs its six database getters on the async scheduler, sharing a `PlayerStats` record with the compass stat sheet; `/pg top` and the stats signs no longer block the main thread
- `PreparedStatement`s are closed with their `ResultSet`s (MySQL cursor leak), and the stats backend no longer overflows the stack when the database is unreachable
- SQL counters are incremented inside the statement, `Stats.UUID` is unique, a dropped connection is re-established once, and the MySQL URL enables `allowPublicKeyRetrieval`, `serverTimezone` and `characterEncoding`
- Player records are created asynchronously on join instead of during the join event
- `/pg reload` re-reads `lobbies.yml`, `messages.yml`, `kits.yml`, `shop.yml` and `chests.yml`, reports failed steps instead of claiming success, and restores setup players completely
- `/pg reload` and a server shutdown mid-round restore the arena blocks and the frozen gamerules instead of leaving the arena damaged or frozen
- `/pg database` reconnects immediately instead of requiring a reload; the status screen shows real game states and live connectivity
- MOTD and slot count stay untouched when game server mode is off (`pg.gameServer: false`), matching legacy 7.0.1
- Server list shows `WAITING`/`VOTING`/the voted arena/`INGAME` for the busiest lobby, and the max player count is the summed capacity
- Upgrading merges message keys that only the new jar ships, in every locale, without overwriting edited texts
- `messages.yml` has no duplicate keys, missing translations are seeded, and every accessor resolves through `lang -> en_US -> literal`
- Arena worlds are protected on every level: liquid flow, block fade, leaf decay, fire spread, mob griefing, bucket filling, mob spawning, weather and lightning only apply inside arena worlds of a running round
- Rank wall updates survive bad blocks and database errors, and requests run async
- `LobbyConfig` `roundTime` was converted from minutes to seconds twice, so a configured 30-minute round lasted 30 hours

#### Commands
- `/pg start`, `/pg pause`, `/pg build`, `/pg force` and `/pg leave` report `You are not in a lobby!` instead of doing nothing
- `/pg force` explains its usage and rejects unknown arenas; `/pg start` forces a ten-second countdown; `/pg list` caps at 54 entries
- Setup commands name the missing argument instead of silently doing nothing, and arena spawn add/remove keeps config keys and memory in sync (1-based contiguous IDs) with rollback on save failure
- `/pg stats <player>` looks up the target correctly (it parsed "stats"), and add-lobby chat input rejects zero and negative IDs while keeping the input mode active
- Setup chat input is tracked per player and runs on the player scheduler, so one staff member's pending input no longer swallows another's chat
- Join, leave, kick and sign/GUI joins validate capacity, state and `pg.joinStarted`; `/pg join <id>` can no longer leave a player half-joined
- `/pg pause` pauses the tick loop again, and `/pg commands` aliases `/pg help`
- Tab completion only suggests permitted commands, and the console may run `help` and `kick`
- `/pg top` no longer resolves `messages.yml` text on the async scheduler, and its no-data, empty and failed rows are localized
- Update-check failures are reported instead of doing nothing or throwing an NPE
- `/pg reload`, the permission denial and the setup chat answer are localized instead of hardcoded English
- `ReloadCommand` and `CommandDispatcher` log exceptions through the plugin logger instead of `printStackTrace()`
- Lobby join aborts with `lobby.no_spawn` instead of clearing the inventory and throwing when the spawn or its world is missing
- A spawn shortage is reported to the round instead of silently starting

### Security

- The update checker parses the GitHub release list with a linear scan instead of a backtracking regex (CodeQL `java/polynomial-redos`)
- Update checks read the GitHub releases instead of the legacy SpigotMC resource, so 1.x installs are no longer told to "update" to 7.0.1; versions are compared numerically
- `pg.mysql.port` is read as a number or a string, and the shipped config writes it as a number
