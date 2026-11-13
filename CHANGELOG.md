# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

#### Gameplay
- Team-aware win condition: the round ends when a single team remains; every surviving member earns a win and reward
- Late-join spectating: with `pg.joinStarted`, players who join a running round become spectators instead of being dropped in as fighters
- `pg.broadcastStarting`: broadcasts a "lobby is starting" message to the whole server when a countdown begins
- `pg.compassOnSpawn`: hands the player-finder compass to everyone at round start
- `pg.changeGamerules`: freezes daylight/weather in the arena world during rounds and restores the previous values afterwards
- `pg.allowOutsideChat`: lets players outside a lobby watch lobby chat (default off, previous behavior)
- Console support for `/pg reload|status|top|broadcast|version|database|gameserver|config|help|kick`
- `permissions` section in plugin.yml with sane defaults (`pg.join`/`pg.leave`/`pg.stats` for everyone, admin nodes op-only)
- Unit tests (JUnit 5): arena spawn ID math and per-lobby config resolution/normalization

#### Scoreboard
- `pg.activateScoreboard` works again: players get a per-player sidebar with map, team, kit and player count while a lobby waits, and remaining time plus kills during a round; leaving the lobby restores the server scoreboard
- `messages.yml` gains `map`, `team`, `kit`, `time` and `scoreboard.none` labels (en_US/de_DE)

#### Configuration
- Consolidated config structure (config.yml, chests.yml, kits.yml, messages.yml, shop.yml)

#### Loot & shop
- Default loot is seeded again on startup: the `food1/2`, `armour1-5` and `weapons1/2` pools and the documented special chests (`pg.customchests` 1-4, including the Flame Bow, netherite and diamond-armour chests) work on a fresh install; existing admin entries are never overwritten
- Shop currency is part of the chest loot again: the new `shop` loot pool (`pg.chestloot.normal.loot.shop`, default `10.0`) rolls glass bottles and coins while a lobby has its shop enabled, so potions can actually be bought
- `Rich Kid` starts every round with 5 coins again (legacy value) next to the doubled kill coins

#### Localization
- `/pg status` and `/pg database` are translated (`status.*`, `database.mode_*` in `en_US` and `de_DE`), including the per-lobby line `Lobby 1 - Players: 3/24, Spectators: 1 (INGAME)`
- `/pg kick` feedback is translated as well (`kick.*`, `command.kick_usage`)
- New keys `database.not_connected`, `bucket.fill_not_allowed` and `setup.not_a_sign`, so admins get a clear message instead of a zeroed stat sheet, a silently ignored bucket or a stack trace

#### Legacy compatibility
- `pg.singleLobby`: single-lobby mode accepts the legacy short setup forms (`/pg addarena <arena>`, `/pg addspawn <arena>`, `/pg joinsign`, …) and targets lobby 1, while the long form keeps working
- `/pg importarenas <lobby> [file]`: imports arenas from a legacy `arenadata.yml` (`pg.arenas.<n>` with `name`, `spawns`, `deathmatch`) into a lobby of the current `lobbies.yml` layout, skips existing arenas and keeps the spawn locations verbatim; usable from the console
- `pg.activePotions`: caps how many entries of `shop.yml` are shown and buyable (`0` = all), the legacy `activePotions` knob
- The arena selector paper is only handed out when the lobby has more than one arena, like the legacy `singleArena` behaviour

#### Build & tooling
- The compile fails on javac warnings (`failOnWarning`, i.e. `-Werror`, with `-Xlint:all`), locally and in CI
- Unit tests for the team amount/assignment contract (`LobbyTeamTest`) and for enabling/disabling kits (`LobbyConfigTest`)

### Fixed

- Game state is now propagated from the `Lobby` model to listeners; previously deaths/kills/rewards were never recorded and in-game block rules never applied
- Shop GUI was unreachable (title mismatch + wrong state gate); purchases now count real coin items and can no longer loop forever
- Random team assignment froze the server when all teams were full; switching teams validates capacity first so nobody ends up teamless
- Arena spawn add/remove kept config keys and memory in sync (1-based contiguous IDs) with rollback on save failure
- Joining via signs/GUIs mid-game or into full lobbies is now rejected (or turned into spectator late join)
- Rank wall updates no longer die on bad blocks/DB errors; DB queries run async and only block/sign writes happen on the region thread
- PreparedStatements are closed with their ResultSets (cursor leak on MySQL)
- Update-check failures are reported instead of silently doing nothing (or throwing NPE)
- TNT bonus damage applies once inside active games (was an extra flat health drain anywhere on the server)
- Lobby maps are protected outside running games; placed/broken blocks and liquids are actually restored at reset
- `/pg reload` fully restores setup players (level/exp/gamemode/location/health) and restarts tick loops + rank wall
- `/pg database` reconnects immediately instead of requiring a reload
- `/pg stats <player>` looks up the target correctly (was parsing "stats")
- `/pg pause` actually pauses the tick loop again
- Status screen shows real game states and live database connectivity
- `/pg top` and the stats signs/emerald readout no longer block the main thread while querying the database
- Player records are created asynchronously on join instead of during the join event
- Setup chat input is tracked per player, so one staff member's pending lobby/arena input no longer swallows another staff member's chat
- Setup chat actions run on the player scheduler instead of the asynchronous chat thread
- Add-lobby chat input rejects zero and negative IDs with a clear message and keeps the input mode active
- `/pg reload` now re-reads `lobbies.yml`, `messages.yml`, `kits.yml`, `shop.yml` and `chests.yml` in addition to `config.yml`
- `/pg reload` reports failed reload steps instead of claiming success when configuration, database or game data reloading failed
- `/pg stats <player>` also resolves offline players whose profile is cached by Paper
- `/pg join <id>` now goes through the shared lobby join validation (capacity, state, `pg.joinStarted`) instead of adding the player to `activePlayers` unconditionally; a full lobby can no longer leave a player half-joined
- Per-round kills are tracked through `Lobby#addKill` again (previously the scoreboard kill counter parsed its own team prefix, and `Lobby#getKills` was always 0)
- Server list now reflects the plugin state again: the MOTD shows `WAITING`/`VOTING`/the voted arena/`INGAME` for the busiest lobby and the max player count is the summed capacity of all lobbies
- Restored the legacy in-game audio cues: countdown bells in the last five seconds, a launch firework when the countdown starts, blast/dragon sounds at round start, a dragon growl when a round ends, a death sound for the eliminated player, and click/error sounds in the arena, kit, team and shop GUIs
- Loot and shop chests work on a fresh install again: `chests.yml` ships usable `chestblocks` defaults (`END_PORTAL_FRAME`/`COMPOSTER`), empty values are seeded on startup, and the matcher accepts both the legacy string and the new list format
- Winning a round now actually pays `pg.winningReward` through Vault (previously the reward was only announced), logs failed transactions, and spawns the celebratory firework from the legacy release
- Free-for-all lobbies can fight again: the friendly-fire check only compares teams when the lobby actually uses teams, and teammates may attack each other in `DEATHMATCH` (previously `friendlyFire: false` cancelled every player hit without teams because all teams were `null`)
- Player-finder compass points at enemies again: it ignored same-team players only when teams are active, targets any other player in free-for-all, also works in `DEATHMATCH`, and only considers players of the same running lobby
- Explosions in arena worlds no longer permanently remove blocks (only entity damage applies); build mode still allows admins to reshape the arena, matching the legacy TNT whitelist behaviour
- `roundTime` was converted from minutes to seconds twice, so a configured 30-minute round lasted 30 hours; the conversion happens exactly once in `LobbyConfig` now
- Chest contents no longer regenerate on every open: each chest is stored for the round, shop chests only open and only sell during `INGAME`/`DEATHMATCH`, GUI drags are cancelled and clicks outside the top inventory are ignored, so loot and potions cannot be duplicated
- Rounds start in `SURVIVAL` with the lobby items and leftover effects cleared, so breaking/placing blocks, buckets and flint and steel work again; the kit item and the kit GUI are only available with `activateKits`
- The team count is derived from the lobby capacity (`maxPlayers / teamSize`, at least two teams) instead of the number of online players, players are spread over the emptiest teams, manual team picks survive the round start, and the selector state is re-seeded after every round (it was dead after round 1)
- `messages.yml` duplicate keys are gone, missing translations are seeded, and every accessor resolves through the same `lang -> en_US -> literal` fallback chain
- Stats backend: no more `StackOverflowError` when the database is unreachable, `Stats.UUID` is unique, counters are incremented inside SQL, a dropped connection is re-established once, the JDBC drivers ship through `plugin.yml` `libraries`, and the MySQL URL enables `allowPublicKeyRetrieval`, `serverTimezone` and `characterEncoding`
- Command UX: `/pg force` explains its usage and rejects unknown arenas, `/pg commands` aliases `/pg help`, the console may run `help` and `kick`, tab completion only suggests permitted commands, `/pg list` caps at 54 entries, setup errors name the missing argument, join/leave print feedback, `/pg start` forces a 10-second countdown, and `plugin.yml` no longer advertises absent permissions
- Arena worlds are protected on every level: liquid flow, block fade, leaf decay, fire spread, mob griefing and bucket filling are limited to the arena world, and mob spawning, weather and lightning only change in arena worlds of a running round (previously one waiting player froze mobs and weather on the whole server)
- Spectators: a killed player stays at the death location instead of being teleported back to the hub, keeps a live sidebar, and every exit path (`/pg leave`, kick, quit, reset, reload, disable) restores gamemode, inventory, effects, collision and the server scoreboard
- Lobby lifecycle: a reset only affects its own lobby and returns its players to the hub (other lobbies keep players, inventories and state), the last player leaving ends a running round through `ENDING` (draw message, stats, block restore), the pause flag no longer locks an emptied lobby, and exactly one tick task runs per lobby
- Entering the lobby clears armour, offhand, potion effects, fire and health/food, so arena gear and shop potions no longer follow the player into the lobby or the next round
- Votes and team slots are released when a player leaves (`/pg leave`, quit, kick, death, reset), so a disconnected player no longer decides the arena and team capacity stays correct
- Dropping items in a lobby is cancelled again (the guard tested `isActivePlayer` and therefore never triggered)
- `/pg joinsign` reports "That is not a sign!" instead of throwing a `ClassCastException` when aiming at a non-sign block, and `/pg kick` keeps the player's restored pre-join location instead of teleporting them to the spawn of world 0
- Deprecated `org.bukkit.GameRule` usages replaced by the new `org.bukkit.GameRules` (`ADVANCE_TIME`, `ADVANCE_WEATHER`, `MOB_GRIEFING`, `SPAWN_MOBS`, `FIRE_SPREAD_RADIUS_AROUND_PLAYER`), which removes 15 build warnings
- The Vault dependency uses jitpack's canonical coordinates (`com.github.MilkBowl`), which removes 18 Maven cache warnings on case-insensitive filesystems

### Changed

- Java 21 → 25 across all build configs and CI workflows
- `maven-resources-plugin` pinned to 3.5.0 for Eclipse M2E compat
- release.yml: reads release body from `CHANGELOG.md` instead of auto-generated notes
- Inventory GUI: arena selector, kit selector, team selector, stats, leave items unified to DARK_AQUA
- Inventory titles: ArenaSelectorTitle, KitSelector, SelectorTeamTitle no longer use prefix line
- `InventoryEventListener` split into `GameInventoryListener`, `GameItemListener` and `SetupInteractionListener`
- Six clone commands (headp1-3/signp1-3) consolidated into one parameterized `StatsWallCommand`
- Setup-mode chat input no longer swallows normal chat while no input mode is active
- Votes have a single source of truth (`ArenaStateManager` via `Lobby#recordVote`); the duplicate vote record on participants was removed
- Removed redundant spectator fallbacks in `/pg join|force|start|pause|build` (`getPlayerLobby` already covers spectators)
- Permission nodes now match their command names: `/pg version` requires `pg.version` (replaces `pg.update`, which also gated update notifications) and `/pg reload` requires `pg.reload` (previously shared `pg.setup`)
- `/pg leave` is now actually gated by `pg.leave` (the node existed in plugin.yml but was never checked)
- Corrected grammar in several player-facing messages ("does not exists", "You not have …")
- `roundTime` is stored in seconds internally; `LobbyConfig` is the only place that converts minutes to seconds
- The team amount is derived from `maxPlayers / teamSize` (legacy behaviour) instead of `ceil(players / teamSize)`
- Setup commands accept an optional lobby number in single-lobby mode; the long form is unchanged
- Arena gamerules now freeze `ADVANCE_TIME`, `ADVANCE_WEATHER`, `SPAWN_MOBS`, `MOB_GRIEFING` and a fire-spread radius of `0`, and restore the captured values (including a custom radius) afterwards

### Removed

- Hardcoded chest loot (now config-driven)
- Hardcoded shop data (now in config)
- Monolithic event/command classes
- `BlockTracker.java`, `PotionChest.java`, `LootTable.java`
- `LobbySettings.java` (entire class unused)
- `ConfigKeys.java` (36/37 dead enum values inlined)
- Dead config knobs that had no code reader: `logging.*`, `performance.*`, `security.*`, `activePotions`
- `BlockStateManager` / `PlayerStateManager` (duplicated state owned by `Lobby`/`Game`; tracking now lives where it is used)
- Unused ParticipantType enum and Kit id field
- ~45 dead message methods and their orphaned translation keys
- 5 no-op listener classes (consume/food/respawn/teleport/explosion)
- Never-functional move-freeze flag (cost a lobby lookup on every move event)
- Write-only voting/team maps in `Lobby` and mirror flags in `LobbyStateManager`
- BAD_OMEN entries 21-27 from `shop.yml` and `BootstrapInitializer.java` shop seeding
- `/pg debug` and the `pg.debug` permission (the command toggled a field that nothing read)
