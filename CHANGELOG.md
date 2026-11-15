# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

Nothing yet - new entries belong here and are moved into a version section on release.

## [1.0.0] - 2026-11-15

First stable release of the rework. Everything below is in this build; the release page additionally
carries the complete `dev.N` development history in a collapsible block.

### Added

- Multi-lobby minigames: lobbies, arenas, arena voting each round, per-lobby settings inherited from `pg.defaults.*`
- Loot chests with weighted pools, custom chests with fixed content and the legacy feature items (compass player finder, airdrop torch, TNT, flint and steel)
- Potion shop with kit prices, coins and glass bottles as currency, airdrops, teams with configurable size and a deathmatch phase
- Spectators for eliminated players and late joiners, player-based lobby protection (no damage, no hunger, no PvP outside a running round)
- Statistics in SQLite or MySQL with async writes, a top-3 stats wall and `/pg top` leaderboards with a KD tie-break
- Join signs with live state, GUI or sign to join, BungeeCord game-server mode with `pg.kickOnRoundEnd` / `pg.kickOnLeave`
- Vault reward support (kill and win rewards), optional and inert without Vault
- Automatic migration from the 7.x layout (`arenadata.yml`, `pg.Lobby.*`, `pg.arenas`, `pg.lobbySystem`)
- Every `.yml` file documented inline in English; messages in `en_US` and `de_DE`
- Update check against the GitHub releases with separate channels: dev builds see newer dev builds and the stable release, stable builds only see stable releases

### Changed

- Flags that were easy to mix up are renamed - `pg.joinStarted` → `pg.spectateRunningRound`, `pg.startOnJoin` → `pg.autoJoinOnConnect`, `pg.broadcastStarting` → `pg.broadcastCountdown`, `pg.allowOutsideChat` → `pg.lobbyChatInGlobalChat` - and related ones grouped: `pg.compass.spawnItem` / `pg.compass.tracking` and `pg.rewards.enabled` / `.kill` / `.win`. The old names are still read as a fallback, an existing `config.yml` keeps its values
- Spectators of a running round are enabled with `pg.spectateRunningRound`, joining players are put into a lobby with `pg.autoJoinOnConnect`
- The server-list MOTD and slot count are also taken over when `pg.autoJoinOnConnect` is on without `pg.gameServer`; only a plain lobby server keeps the entry from `server.properties`
- Class-based architecture with manager delegation, one listener and one command class per topic
- Lobby protection is decided per player from the game state instead of by world flags
- Renamed the join sign command to `/pg sign`; `/pg joinsign` was removed
- `pg.activeKits` and `pg.activePotions` are plain counts (`0` = none); both default to the legacy values
- Build is warning-free (`-Xlint:all` + `-Werror`) and enforced in CI, with JUnit tests for the game rules

### Fixed

- The kill and win reward message printed the amount twice (`you get 10 $10`); it now shows the amount as formatted by the economy (`you get $10`, `you get 10 Coins`)
- Existing `Stats` tables are healed on start: duplicate rows per UUID are merged (counters summed, KD recomputed) before the unique key is added, and an already present key is detected instead of logging a false "UUID is not unique" line on every restart
- PvP, gamerules and respawn parity with 7.0.1 (arena world PvP, difficulty, noon, immediate respawn)
- Round start: random kits per player, team split that never leaves everyone in one team, arena abort on failed teleport
- Shop and chest seeding idempotent, loot chests never empty after a reload, 23 legacy chest effects
- Stats credited to the surviving team only, no blocking database calls on the tick thread
- Proxy leave: players are transferred to the hub when a proxy is reachable and disconnected instead of being stranded otherwise

## [1.0.0-dev.60] - 2026-11-15

### Changed

- Section headers are plain grey names (`Arena-Selector`, `Stats`, `Commands`, `Top …`) without the dashed line and without the duplicated footer line; the blank banners in `/pg config` and `/pg status` are gone
- The game server flags are called `pg.kickOnRoundEnd` and `pg.kickOnLeave`, both defaulting to `false`: players stay in the lobby after a round and after `/pg leave` unless the admin turns the kick on (with a reachable proxy the player is transferred to `pg.bungeeServer` instead of being disconnected)

## [1.0.0-dev.59] - 2026-11-15

### Fixed

- Kit, team and vote feedback use the legacy header/footer block (`--------------Kit-Selector--------------`) with green labels and light purple values, including the `Players: <n>/<max>` line and the "already full" block; the vote confirmation no longer prints the "Votes" label twice
- Inventory titles (kit, team, arena, shop, lobby list) carry the chat prefix like the legacy release

## [1.0.0-dev.58] - 2026-11-15

### Fixed

- `lobbies.yml` keeps its documentation: the file is copied on first start instead of created empty, every save writes the comment header back (`LobbyFileWriter`), and an existing file without the header gets it on the next start - including the worked example of a per-lobby `settings:` block

## [1.0.0-dev.57] - 2026-11-15

### Fixed

- The leave item works for a spectator of a running round (`pg.joinStarted`): `/pg leave` and the item now share one leave flow that also clears the lobby bookkeeping

## [1.0.0-dev.56] - 2026-11-15

### Fixed

- `/pg leave` and the round end no longer strand players on a game server without proxy mode: the transfer is checked (`spigot.yml -> settings.bungeecord` plus the registered channel), `/pg leave` disconnects with the hub message when no proxy is reachable, the round end keeps the players in the lobby instead, and the plugin warns once on startup and once when the fallback fires

## [1.0.0-dev.55] - 2026-11-15

### Changed

- README and store description are aligned section by section: identical feature list (18 entries), the practical quick start in both, matching section names and the same chest type, sign, stats wall and configuration facts; `StoreDescriptionTest` fails when one document loses a section

## [1.0.0-dev.54] - 2026-11-15

### Fixed

- The store description no longer uses `[HR][/HR]` (vBulletin syntax, Spigot shows it as literal text) - the dividers are a centered line of dashes now, and the store test rejects any tag outside the XenForo 1.5 whitelist
- The store description listed a `/pg setlobbyspawn` command that does not exist, and its feature list lacked the GUI join; `/pg addlobby <id>` creates the lobby at the player's position and stores that as the lobby spawn (now stated in the description, `lobbies.yml` and the README)
- `StoreDescriptionTest` also checks that the README names every command, so both documents stay in step

## [1.0.0-dev.53] - 2026-11-15

### Changed

- The SpigotMC store description uses the chat prefix colours for the title (dark purple `Potion`, gold `Games`, dark aqua `X`)

### Added

- `StoreDescriptionTest` keeps the store description in sync on every build: every command with a help line must appear, BBCode tags must be balanced and no Markdown may leak in

## [1.0.0-dev.52] - 2026-11-15

### Added

- `docs/store/spigot-description.txt`: the README as a paste-ready BBCode description for SpigotMC, plus `docs/store/README.md` explaining which platform takes Markdown and which needs BBCode

## [1.0.0-dev.51] - 2026-11-15

### Changed

- Documentation sweep before the 1.0 tag: `DOCUMENTATION.md` no longer lists the removed `/pg broadcast` command, has a complete admin command list and the current jar size; `README.md` documents `/pg help`

### Removed

- 17 message keys per locale that nothing ever sent (`command.*.usage` of the nine setup commands, `help.headp1-3`, `help.signp1-3`, `help.help`, `lobby.removed_success`) and the `command.kick_usage` seed - both locales now define the same 244 keys

## [1.0.0-dev.50] - 2026-11-15

### Changed

- `lobbies.yml` documents how a lobby overrides the `config.yml` defaults: a one-line example, a complete `settings` block and the precedence (`settings` -> legacy root key -> `pg.defaults` -> built in default)

## [1.0.0-dev.49] - 2026-11-15

### Changed

- Every `.yml` file is documented in English: what each toggle does, which values are allowed and how to add entries (`config.yml`, `chests.yml`, `shop.yml`, `kits.yml`, `lobbies.yml`, `messages.yml`, `plugin.yml`)

### Fixed

- Chest loot weights are read from `chests.yml` (the file they are documented in) instead of `config.yml`

## [1.0.0-dev.48] - 2026-11-15

### Fixed

- `pg.activeKits` and `pg.activePotions` count entries the same way everywhere: `1` uses the first entry, `2` two, `20` all twenty, `0` none. `activeKits` previously treated `0` as "all" when dealing kits but as "none" in the selector.
- Startup warning when `activeKits` or `activePotions` is `0` while the file has entries (an empty shop or an empty kit selector is easy to miss after an update)

## [1.0.0-dev.47] - 2026-11-15

### Changed

- Lobby protection is decided per player from the game state: a lobby member is safe in `WAITING`, `PREPARING`, `ENDING` and `RESET` (no damage, no hunger, no PvP) and vulnerable in `INGAME` and `DEATHMATCH`. Players outside a lobby keep the normal server rules, spectators stay protected. The position based safe zone is gone.

## [1.0.0-dev.46] - 2026-11-15

### Changed

- Lobby protection is player based instead of state/world based: whoever is nearer to the lobby spawn than to the nearest arena spawn of that lobby gets no damage, no hunger and no PvP, while the arena side keeps damage, fall damage, hunger and PvP. Players in a running round are never treated as lobby players. No new setting.

## [1.0.0-dev.45] - 2026-11-15

### Fixed

- A round can no longer start with every player in one team: picks that would leave a single team are dropped and the players spread over at least two teams (two players never end up together)
- `pg.activePotions` counts entries as written: `1` activates the first entry, `20` all twenty, `0` none

## [1.0.0-dev.44] - 2026-11-15

### Fixed

- `pg.activePotions` defaults to the legacy value 19 instead of "all entries", so the shop shows 19 of the 20 configured potions like 7.0.1 did (`0` still means all)

## [1.0.0-dev.43] - 2026-11-15

### Changed

- `/pg top` without a leaderboard kind no longer falls back to kills - it answers with `/pg top <kills|deaths|wins|kd>` (`top.usage`, EN/DE reworded)

## [1.0.0-dev.42] - 2026-11-15

### Changed

- `/pg joinsign` is gone, `/pg sign` is the only name
- README documents the feature items (player-finder compass, airdrop torch, TNT, flint and steel, bow, milk bucket, stews) as material-bound features that can be placed in any loot pool, custom chest or shop

### Added

- `pg.playerFinder` to switch the compass tracking off (default `true`); the airdrop torch is still gated per lobby by `settings.activateAirdrops`

## [1.0.0-dev.41] - 2026-11-15

### Added

- The legacy loot item details: TNT named "Extremely explosive TNT", the player-finder compass keeps its name in chests and the flint and steel comes with 60/64 durability (new message key `tnt.extremely_explosive`)

### Removed

- The legacy `removearena`/`removelobby`/`removespawn`/`removedeathmatch` aliases - `del*` is shorter and is the documented name
- The `/pg` command aliases `potiongames`, `pgames` and `potiong`

## [1.0.0-dev.40] - 2026-11-15

### Added

- Legacy command aliases `removearena`, `removelobby`, `removespawn` and `removedeathmatch`
- Legacy permission node `pg.update` still receives the update notice on join and is declared in `plugin.yml`
- A late joiner is told that they are spectating (`you.in_spectator_mode`)

## [1.0.0-dev.39] - 2026-11-15

### Fixed

- Chests roll their random potion effects again: the effect pool is seeded with the legacy list (23 effects) on enable and after a reload, so the 0-3 roll finds something to pick
- The winner firework no longer damages the winner - firework and lightning damage is cancelled for every lobby player, not only during a running round
- The shop tooltip shows `Price: <n> Coins` instead of a raw component dump

## [1.0.0-dev.38] - 2026-11-15

### Fixed

- Loot chests stay filled after `/pg reload`: the reload re-seeds the chest loot pools, and seeding no longer doubles the pools
- Opening a loot chest (portal frame) rolls 0-3 random potion effects from the legacy pool while the player has no active effect
- Voting and `/pg force` stay open during the countdown until the arena is revealed (legacy decided the map at 10 seconds), instead of closing when the countdown starts
- The shop rebuilds itself from `shop.yml` when the in-memory list is empty, and a startup line reports how many shop entries and loot items were loaded

## [1.0.0-dev.37] - 2026-11-14

### Changed

- `minPlayers` defaults to half of `maxPlayers` (legacy behaviour) unless `pg.defaults.minPlayers` is set; the key is optional in `config.yml`
- A new lobby only stores its spawn and inherits countdown, round time, start delay, player limits, team size and the feature flags from `pg.defaults`, so defaults configured once apply to every lobby created later

## [1.0.0-dev.36] - 2026-11-14

### Fixed

- Kits are unlimited again: every player without a choice draws independently, so two players may share a kit (legacy behaviour)
- A team win only credits the alive members of the surviving team; a player without a team assignment no longer receives a win
- Kill, death, win and loss counters are written off the tick thread, so a JDBC round trip can no longer stall a kill or a round end

## [1.0.0-dev.35] - 2026-11-14

### Changed

- The rank wall ranks players with the same wins by KD (`ORDER BY WINS DESC, KD DESC`); `/pg top wins` uses the same tie-break
- Players without a kit choice get different random kits while the active kits last (dealt like a shuffled deck) instead of possibly drawing the same kit

## [1.0.0-dev.34] - 2026-11-14

### Changed

- The join sign shows the lobby number again (state, arena and `[players/capacity]` below it) instead of the plugin name
- `/pg joinsign` is renamed to `/pg sign`; `joinsign` stays available as an alias

## [1.0.0-dev.33] - 2026-11-14

### Fixed

- The arena world gets `pvp` enabled and a `PEACEFUL` world is switched to `EASY` for the round (legacy `setGameRules`), so players can damage each other on a lobby world that ships `pvp: false` (e.g. a Multiverse lobby); both are restored afterwards
- A join sign is recognised by its location instead of the first line, which now shows the plugin name - right clicking it joins the lobby again

## [1.0.0-dev.32] - 2026-11-14

### Fixed

- The arena runs with `doImmediateRespawn` and noon time during a round (legacy `setGameRules`); both are captured and restored afterwards
- A dead player respawns at the death location instead of the world spawn, so dying turns into spectating without leaving the arena

## [1.0.0-dev.31] - 2026-11-14

### Fixed

- Players can damage each other again as soon as the round is running: the start freeze only pins the position (legacy `move = false` never suppressed damage), and the PvP gate checks the lobby state instead of the internal player list
- Arrows and other projectiles are resolved to their shooter, so hits from outside the round stay blocked
- A participant that stayed frozen after the countdown is unlocked by the tick, so nobody is stuck without movement or combat

## [1.0.0-dev.30] - 2026-11-14

### Added

- `/pg setcountdown <seconds>` changes the countdown of the lobby you stand in, or the global default from the console (`pg.setup`)
- Legacy multi-lobby arenas (`pg.lobbies.<id>.<n>`) are migrated into `lobbies.yml`, not only the global `pg.arenas` section

### Fixed

- A `Random` vote now really plays a random map when it wins or ties the vote (it was recorded but ignored)
- A failing `messages.yml` save or a broken bundled language file logs a warning instead of staying silent

## [1.0.0-dev.29] - 2026-11-14

### Fixed

- Every player who does not pick a kit gets a random kit out of the active kits when the round starts, so the shop discount and the Rich Kid start coins apply (legacy `rnd.nextInt(activeKits)`)
- Advancement pop-ups are disabled for the arena world during a round and restored afterwards (`SHOW_ADVANCEMENT_MESSAGES`)
- The team selector is only handed out when teams are active and the leave item only without `startOnJoin`; the legacy join sound plays on arrival

## [1.0.0-dev.28] - 2026-11-14

### Fixed

- Arena worlds stay protected between rounds: leaves do not decay, ice and snow do not melt, water and lava do not flow, no mobs spawn and no rain or lightning (legacy guarded its arena worlds whenever the plugin ran, not only during a round)
- The join message is no longer broadcast for players who land in a lobby on a game server (`startOnJoin`), matching the legacy `joinMessage(null)`

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

## Earlier development builds (1.0.0-dev.1 - 1.0.0-dev.25)

These builds predate this changelog and shipped without separate notes; their tags and jars
remain on the releases page. Everything from `1.0.0-dev.26` on is documented below.

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
