# PotionGamesX

> A Minecraft minigames plugin for Paper servers!

[![CI Build](https://github.com/lennartabeln/PotionGamesX/actions/workflows/ci.yml/badge.svg)](https://github.com/lennartabeln/PotionGamesX/actions/workflows/ci.yml)
[![CodeQL](https://github.com/lennartabeln/PotionGamesX/actions/workflows/codeql.yml/badge.svg)](https://github.com/lennartabeln/PotionGamesX/actions/workflows/codeql.yml)
[![Release](https://img.shields.io/github/v/release/lennartabeln/PotionGamesX)](https://github.com/lennartabeln/PotionGamesX/releases/latest)

PotionGamesX is a minigames plugin that works like SurvivalGames but with potions and effects!

## Features

* Custom loot tables with probability-weighted chests
* Custom potion effects from looting chests
* Custom chest types (Normal, Target, Netherite, Composter)
* Airdrops (activate with redstone torch)
* Loot coins and glass bottles to use in the shop
* Potion shop with kit-specific sale prices
* Multiple lobbies with arena voting each round
* Teams with custom team sizes — last team standing wins
* Spectators: eliminated players (and late joiners via `spectateRunningRound`) watch the round
* Deathmatch mode for final battles
* Player statistics (SQLite or MySQL)
* Top 3 stats wall display
* Join signs with live player count updates
* BungeeCord / proxy support
* GUI or sign to join lobbies
* Rewards system (Vault)
* Every config file documented in English
* Class-based architecture with manager delegation

## Requirements

- **Java 25+**
- **Paper 26.3+**
- **Multiverse-Core** (soft dependency)
- **Vault** (optional, for economy rewards)

## Installation

1. Download the latest release from the [releases page](https://github.com/lennartabeln/PotionGamesX/releases)
2. Place the `.jar` in your server's `plugins/` folder
3. Install Multiverse-Core if using multi-world setups
4. Restart your server
5. Configure via `plugins/PotionGamesX/config.yml`
6. Run `/pg setup` to set up the plugin

## Setup

### Quick Start
1. Create a lobby with `/pg setup` — it is created where you stand, that position is also the lobby spawn
2. Add arenas and their spawns through the wizard (at least two spawns per arena)
3. Place the chest blocks: end portal frame for loot, composter for the shop
4. Optional: add deathmatch spawns with `/pg adddeathmatch`
5. Set a join sign with `/pg sign <lobby>`, then test the round with two players
6. Fine-tune the settings in `config.yml`

### Lobby Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/pg help` | List every command and permission (alias: `/pg commands`) | – |
| `/pg join <id>` | Join a lobby | `pg.join` |
| `/pg leave` | Leave current lobby | `pg.leave` |
| `/pg list` | Open lobby GUI | `pg.list` |
| `/pg start` | Start countdown | `pg.start` |
| `/pg build` | Toggle build mode | `pg.build` |
| `/pg pause` | Pause/resume the game timer | `pg.pause` |
| `/pg force <arena>` | Force an arena | `pg.force` |
| `/pg stats [player]` | Show player stats | `pg.stats` |

### Admin Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/pg setup` | Interactive setup wizard | `pg.setup` |
| `/pg config` | View current configuration | `pg.config` |
| `/pg status` | Show server status and lobbies | `pg.status` |
| `/pg kick <player>` | Remove player from lobby | `pg.kick` |
| `/pg top` | Show leaderboards | `pg.top` |
| `/pg gameserver` | Toggle game/hub mode | `pg.gameserver` |
| `/pg database` | Switch MySQL/SQLite | `pg.database` |
| `/pg reload` | Reload config files | `pg.reload` |
| `/pg version` | Show plugin version | `pg.version` |

Most admin commands can also be run from the server console (`reload`, `status`, `top`, `version`, `database`, `gameserver`, `config`, `help`, `kick`).

### Advanced Setup Commands

Alternative to the `/pg setup` wizard — individual commands for manual setup:

| Command | Description | Permission |
|---------|-------------|------------|
| `/pg addlobby <id>` | Create a lobby at your position (also stores the lobby spawn) | `pg.setup` |
| `/pg dellobby <id>` | Remove lobby | `pg.setup` |
| `/pg addarena <lobby> <name>` | Add arena | `pg.setup` |
| `/pg delarena <lobby> <name>` | Remove arena | `pg.setup` |
| `/pg addspawn <lobby> <arena>` | Add spawn | `pg.setup` |
| `/pg delspawn <lobby> <arena>` | Remove last spawn | `pg.setup` |
| `/pg adddeathmatch <lobby> <arena>` | Add deathmatch spawn | `pg.setup` |
| `/pg deldeathmatch <lobby> <arena>` | Remove deathmatch spawn | `pg.setup` |
| `/pg sign <lobby>` | Set the join sign (look at a sign) | `pg.setup` |
| `/pg setcountdown <seconds>` | Change the lobby countdown (console: global default) | `pg.setup` |
| `/pg headp1\|p2\|p3` | Set stats wall head | `pg.setup` |
| `/pg signp1\|p2\|p3` | Set stats wall sign | `pg.setup` |

With `pg.singleLobby: true` the setup commands also accept their legacy short form without a lobby number (`/pg addarena <arena>`, `/pg addspawn <arena>`, `/pg sign`) and always target lobby 1.

**Upgrading from the legacy release:** on startup the plugin looks for an `arenadata.yml` in `plugins/PotionGamesX/` and migrates it automatically — lobbies from `pg.lobbies.<id>` are created (spawn from `coords`, settings moved into `settings.*`), a legacy single-lobby server gets its `pg.Lobby.coords`/`pg.Lobby.sign` from `config.yml` turned into lobby 1, the global `pg.arenas.<n>` are copied into every lobby that has no arena yet, and a legacy `pg.lobbySystem: false` enables `pg.singleLobby`. Nothing is ever overwritten: arenas a lobby already has (same name) are skipped. Once the migration is done, delete `arenadata.yml` — otherwise its arenas are checked again on every start (an arena you deleted on purpose would come back).

### Chest Types

* **End Portal Frame**: Normal loot chest with probability-weighted loot (food, armour, weapons, coins and glass bottles)
* **Target**: Special chest with Flame Bow and Arrows
* **Netherite Block**: Special chest with Netherite Ingots (Smithing Table upgrades diamond → netherite)
* **Composter**: Potion shop (right-click during a round; prices are in coins and one glass bottle; `pg.activePotions` limits how many entries are active)

Additional chest types and their loot can be defined in `chests.yml` (`pg.customchests.<id>` with `activate`, `chesttype`, `chestsize` and numbered slots).

### Lobby protection

Protection is decided per player and then by the game state, so lobby and arena can share one world:

- A player who is in a lobby is safe while no round is running: waiting, counting down, the cleanup after a round and the reset all mean no damage, no hunger drain and no PvP.
- Only a running round (`INGAME`, `DEATHMATCH`) takes the protection away - damage, fall damage, hunger and PvP follow the round rules (friendly fire, teams, deathmatch).
- Players who are in no lobby at all keep the normal server rules.
- Spectators stay protected in every state.

### Feature items

A few items carry their own game logic. They are recognised **by material**, so it does not matter where a player gets them from - loot chests, a custom chest, the shop, a spawn item or another plugin:

| Item | Behaviour | Gated by |
| --- | --- | --- |
| Compass | Points to the nearest opposing player ("Player-Finder") | `pg.compass.tracking` enables the tracking, `pg.compass.spawnItem` hands one out at round start |
| Redstone torch | Throws an airdrop above the player (coordinates are broadcast to the lobby) | `pg.lobbies.<id>.settings.activateAirdrops` |
| TNT | Explodes and damages players; blocks are protected by the arena guard | `pg.activateDeathmatch` is unrelated - TNT is always live |
| Flint and steel, bow, arrows, fishing rod, shield | Vanilla behaviour, using the normal durability | - |
| Milk bucket, stews | Clear effects / restore hunger (`handleStew`, `handleMilk`) | - |

Because the behaviour is bound to the material, you can put any of these items into any loot pool in `chests.yml` (`MATERIAL:AMOUNT`), add them to a custom chest, or sell them in `shop.yml` - no plugin change needed. The legacy display names ("Player-Finder", "Extremely explosive TNT") and the almost used-up flint and steel are applied automatically when the item is seeded from `chests.yml`.

### Signs

* **Join Sign**: Place a sign, look at it, use `/pg sign <lobby>`
* **Stats Sign**: Place a sign, write `PotionGamesX` on line 2 and `Stats` on line 3

### Stats Wall

1. Place 3 player heads on blocks next to each other
2. Place 3 signs at the front
3. Look at head #1 and run `/pg headp1` (repeat for p2, p3)
4. Look at sign #1 and run `/pg signp1` (repeat for p2, p3)

## Configuration

Config files in `plugins/PotionGamesX/`:

| File | Purpose |
|------|---------|
| **config.yml** | Global settings |
| **lobbies.yml** | Lobbies, arenas, spawn locations |
| **chests.yml** | Chest loot item definitions |
| **shop.yml** | Shop item definitions |
| **kits.yml** | Kit definitions |
| **messages.yml** | Localized text messages |

Every key in these files is documented inline, in English: what the toggle does, which values are allowed and how to add an entry. The shipped files are the reference; an existing installation keeps its own values and comments.

Settings use a lobby-first model:
- `pg.defaults.*` stores global defaults for every lobby
- `pg.lobbies.<id>.settings.*` stores per-lobby overrides
- Falls back to defaults if per-lobby setting is missing

Feature defaults (`pg.defaults.*`, overridable per lobby):
| Setting | Effect |
|---------|--------|
| `countdown` | Seconds between reaching `minPlayers` and the round start |
| `roundTime` | Round length in minutes (values above 300 are read as seconds) |
| `maxPlayers` / `minPlayers` / `teamSize` | Capacity, start threshold and team size; the team count is `maxPlayers / teamSize` |
| `activateTeams` / `activateKits` / `activateShop` / `activateAirdrops` | Feature toggles per lobby |

Notable global toggles (`config.yml`):
| Setting | Effect |
|---------|--------|
| `pg.gameServer` | Dedicated game server: auto-join on connect and the `pg.kickOn*` options decide whether players leave the server (transfer to `pg.bungeeServer`, or disconnect without a reachable proxy) |
| `pg.kickOnRoundEnd` | Kick the players off the server when a round ends (`true`) or return them to the lobby (`false`, default) |
| `pg.kickOnLeave` | Kick the player off the server on `/pg leave` (`true`) or keep them in the lobby (`false`, default) |
| `pg.singleLobby` | Single-lobby compatibility mode: setup commands may omit the lobby number (targets lobby 1) |
| `pg.language` | Locale used by `messages.yml` (`en_US`, `de_DE`) |
| `pg.spectateRunningRound` | Late joiners spectate a running round instead of being rejected |
| `pg.autoJoinOnConnect` | Auto-join the first open lobby on connect; also makes the plugin own the server-list MOTD and slot count (like `pg.gameServer` does) |
| `pg.activateDeathmatch` | Switch a close round to the deathmatch arena |
| `pg.activateScoreboard` | Per-player sidebar with map, team, kit, time and kills |
| `pg.friendlyFire` | Allow teammates to damage each other outside `DEATHMATCH` |
| `pg.broadcastCountdown` | Broadcast a server-wide message when a lobby countdown starts |
| `pg.compass.spawnItem` | Give everyone the player-finder compass at round start |
| `pg.changeGamerules` | Freeze time, weather, fire spread, mob griefing and mob spawning in the arena world during rounds |
| `pg.lobbyChatInGlobalChat` | Players outside lobbies can see lobby chat |
| `pg.rewards.enabled` / `.kill` / `.win` | Pay kill and win rewards through Vault (money per kill / per won round) |
| `pg.activeKits` | Number of selectable kits (`kits.yml`) |
| `pg.activePotions` | Shop entries shown and buyable, counted from `shop.yml` (`1` = first entry only, `20` = all twenty, `0` = none) |

## Development

```bash
mvn clean install    # build + run the JUnit 5 tests
mvn test             # tests only
```

The build is warning-free and stays that way: `maven-compiler-plugin` runs `-Xlint:all` and fails on any javac warning (`failOnWarning`, i.e. `-Werror`), locally and in CI. The resulting `target/PotionGamesX-<version>.jar` is a Paper plugin — copy it into your server's `plugins/` folder; it cannot be executed with `java -jar`.

Architecture notes live in [DOCUMENTATION.md](DOCUMENTATION.md), the release notes in [CHANGELOG.md](CHANGELOG.md).

Publishing the plugin: [docs/store](docs/store/README.md) holds the ready-to-paste store description for SpigotMC (BBCode), since Spigot does not accept Markdown.

## Release History

Development builds are published as pre-releases (`1.0.0-dev.N`) with the changelog section of the commit; full releases use the `[version]` section of `CHANGELOG.md`. The built-in update check follows the channel of the installed build: a `dev` build also sees the next stable release, a stable build only sees newer stable releases.

### v1.0 — PotionGamesX (Rework)
Complete rework of the original PotionGames, released as **1.0.0**.
* Full class-based OOP refactor with manager delegation
* Manager classes and separated event listeners / command classes
* Configuration-driven design
* Consolidated config structure (config.yml, chests.yml, kits.yml, messages.yml, shop.yml)
* GitHub CI/CD pipeline (build, test, CodeQL, automated releases)

### Original PotionGames
Previous versions of the original PotionGames plugin before the rework:

* **v7.0** — Support for Minecraft 1.13+
* **v6.0** — Airdrops
* **v5.2** — Sounds
* **v5.1** — Rewards for kills and winning (Vault)
* **v5.0** — Deathmatch
* **v4.9.5** — Team-Mode
* **v4.9** — Scoreboard
* **v4.8** — One arena lobbies
* **v4.7** — Primed TNT to loot table
* **v4.6** — `/pg reload` command
* **v4.5** — `/pg version` command
* **v4.4** — Join lobby via GUI
* **v4.3** — Support for 1.17
* **v4.2** — Bungee Hub-Server setting
* **v4.1** — Option to change all chest blocks
* **v4.0** — Option to add own blocks with their own loot table
* **v3.1** — Own settings per lobby
* **v3.0** — Inventory setup
* **v2.3** — Round-Time
* **v2.2** — Own settings per lobby
* **v2.1** — Join signs with live updates
* **v2.0** — Multi-Arena-System
* **v1.0** — Option to change chest items and effects
* **v0.9** — Option to change database type (MySQL/SQLite)
* **v0.8.5** — Option to deactivate MySQL/stats
* **v0.8** — Option to change kits
* **v0.7** — Option to change shop items
* **v0.6** — Option to turn Teams, Kits and Shop on/off
* **v0.5** — Teams
* **v0.4** — Kits
* **v0.3** — Shop
* **v0.2** — ArenaVote-System
* **v0.1** — SurvivalGames-System with chests giving potion effects

## License

Licensed under the [MIT License](LICENSE).

## Issues / Ideas

[Report bugs / request features here!](https://github.com/lennartabeln/PotionGamesX/issues)
