# PotionGamesX - Official Documentation

> A modern Minecraft minigames plugin for Paper servers with potions, loot, and team-based gameplay.

## 📋 Documentation Index

- **[README.md](README.md)** - Main plugin features and installation
- **[CHANGELOG.md](CHANGELOG.md)** - Version history and release notes

## 🚀 Quick Start

### Build
```bash
mvn clean install        # build + unit tests (add -DskipTests to skip them)
```

Javac warnings fail the build (`failOnWarning` with `-Xlint:all`), so a green build is also a warning-free build.

### Deploy
1. Copy `target/PotionGamesX-*.jar` to server `plugins/`
2. Restart server
3. Configure in `plugins/PotionGamesX/config.yml`
4. Use `/pg help` for commands

## 📦 What's Included

### Core Features
- ✅ Multi-lobby minigames system (unlimited lobbies)
- ✅ Customizable loot chests with probability-weighted pools
- ✅ Team-based gameplay with custom team sizes and team-aware win conditions
- ✅ Potion shop system with kit sale prices
- ✅ Player kits
- ✅ Deathmatch mode for final battles
- ✅ Spectator mode for eliminated players and late joiners (`joinStarted`)
- ✅ Player statistics (SQLite/MySQL)
- ✅ Top 3 player display wall

### Modern Architecture
- ✅ Class-based OOP (no monolithic patterns)
- ✅ Manager-based state delegation
- ✅ Individual command classes (not monolithic)
- ✅ Separated event listeners
- ✅ Configuration-driven design
- ✅ Warning-free build (`-Xlint:all` + `-Werror`), enforced in CI

### Admin Commands
- `/pg setup` - Interactive setup wizard (lobbies, arenas, spawns, chests, signs)
- `/pg config` - View configuration
- `/pg status` - Show server status and lobbies
- `/pg reload` - Reload every config file
- `/pg gameserver` - Toggle offline/online mode
- `/pg database` - Switch MySQL/SQLite
- `/pg broadcast` - Send announcements
- `/pg kick` - Remove players from lobbies
- `/pg top` - Show leaderboards
- `/pg version` - Show the plugin and the latest release version

## ⬆️ Upgrading from the legacy release (7.0.1)

Copy your old `plugins/PotionGames/arenadata.yml` and `config.yml` into `plugins/PotionGamesX/` and start the server. The plugin migrates automatically:

| Legacy source | Becomes |
|---|---|
| `arenadata.yml` → `pg.lobbies.<id>` | lobby with `spawn` (from `coords`) and `settings.*` |
| `config.yml` → `pg.Lobby.coords`/`pg.Lobby.sign` | lobby **1** (single-lobby servers) |
| `arenadata.yml` → `pg.arenas.<n>` | arenas of every lobby without arenas (spawns/deathmatch verbatim) |
| `config.yml` → `pg.lobbySystem` | `pg.singleLobby` (`false` → `true`) |
| `lobbies.yml` root settings | `pg.lobbies.<id>.settings.*` |

Nothing is overwritten and the migration is idempotent (it only logs when it did something). A single file can still be imported on demand with `/pg importarenas <lobby> [file]`.

## 🛠️ Configuration

All config files in `plugins/PotionGamesX/`:

| File | Purpose |
|------|---------|
| **config.yml** | Global settings |
| **lobbies.yml** | Lobbies, arenas, spawn locations |
| **chests.yml** | Chest loot item definitions |
| **shop.yml** | Shop item definitions |
| **kits.yml** | Kit definitions |
| **messages.yml** | Localized text messages |

## 📊 Build Status

| Metric | Status |
|--------|--------|
| Java | 25+ required |
| Paper | 26.3+ required |
| Compiler | Warning-free (`-Xlint:all`, `-Werror`) |
| Tests | ✅ JUnit 5 unit tests (`mvn test`) |
| JAR Size | ~0.25 MB |

## 🔗 Requirements

- **Java 25+**
- **Maven 3.8+**
- **Paper 26.3+**
- **VaultAPI 1.7.1** (optional, for economy)
- **Multiverse-Core** (soft dependency)

## 🏗️ Project Structure

```
src/main/java/com/tw0far/potiongames/
├── models/              # Domain models (Game, Lobby, etc)
├── managers/            # State managers (Config, Database, etc)
├── commands/            # Individual command classes
├── listeners/           # Separated event handlers
├── handlers/            # Complex workflows
├── bootstrap/           # Startup initialization
├── util/                # Utilities and helpers
└── PotionGamesX.java    # Main plugin class
```

## 📝 Key Files

| File | Purpose |
|------|---------|
| pom.xml | Maven build configuration |
| plugin.yml | Minecraft plugin metadata |

## 🚨 Support

For issues, features, or questions:
1. Check console logs for error messages
2. Verify config files are valid YAML
3. Open an issue on the [GitHub repository](https://github.com/lennartabeln/PotionGamesX/issues)

## 📜 License

Licensed under the [MIT License](LICENSE).
