# Changelog - PotionGamesX

All notable changes to PotionGamesX are documented in this file.

## [1.0.0-dev.2] - 2026-06-13

### Changed

- release.yml: reads release body from `CHANGELOG.md` instead of auto-generated notes
- CHANGELOG.md: restructured to [Keep a Changelog](https://keepachangelog.com/) format with per pre-release entries

## [1.0.0-dev.1] - 2026-06-11

### Added

- Arena selector: fixed "Random" vote count display
- Kit selector: `handleKitSelection` listener method
- Messages.java: 86 typed `*Text()` methods replacing 107 `raw()` calls

### Changed

- Inventory GUI: arena selector, kit selector, team selector, stats, leave items unified to DARK_AQUA
- Inventory titles: ArenaSelectorTitle, KitSelector, SelectorTeamTitle no longer use prefix line

### Removed

- `BlockTracker.java`, `PotionChest.java`, `LootTable.java`
- `LobbySettings.java` (entire class unused)
- `ConfigKeys.java` (36/37 dead enum values inlined)
- 19 dead static fields from `Settings.java`
- `IManager.reload()` from interface and all 8 implementations
- ~30 dead methods from `Lobby.java`
- 13 dead methods from `Arena.java`, 8 from `Game.java`, 10 from `LobbyConfig.java`
- ~160 dead method declarations across all 8 managers
- BAD_OMEN entries 21-27 from `shop.yml` and `BootstrapInitializer.java` shop seeding

## [1.0.0] - 2026-05-29

### Added

#### Admin Commands
- `/pg config` - View current configuration settings
- `/pg status` - Show server status and active lobbies  
- `/pg debug` - Toggle debug logging mode
- `/pg broadcast` - Send server-wide announcements
- `/pg kick` - Remove players from lobbies
- `/pg top` - Leaderboard system (placeholder)
- `/pg gameserver` - Toggle gameserver mode without reload
- `/pg database` - Switch between MySQL/SQLite without reload

#### Configuration
- `logging` section - Control log levels (INFO/DEBUG/TRACE)
- `performance` section - Tune update rates and limits
- `security` section - Configure player limits and banned items
- Configuration validator on startup - Warns about invalid settings
- Consolidated config structure (config.yml, lobbies.yml, chests.yml, messages.yml)

#### Architecture
- Class-based OOP (no monolithic patterns)
- 8+ manager classes for state delegation
- Separated event handlers into 4+ listener classes
- Individual command classes (34+ command handlers)
- Delegation methods throughout codebase

#### Build & Documentation
- Maven configuration for clean builds
- BUILD.md and DOCUMENTATION.md

#### CI/CD & GitHub Config
- release.yml - Automated GitHub releases on version change
- ci.yml - Build and test on push/PR
- codeql.yml - Code security analysis
- stale.yml - Auto-close stale issues and PRs
- labeler.yml - Auto-label PRs by file paths
- dependabot.yml - Weekly dependency updates
- Issue templates (bug report, feature request)
- Pull request template with build checklist
- CODEOWNERS, SECURITY.md, CONTRIBUTING.md
- AGENTS.md to guide AI tooling workflows

### Fixed

- Setup mode chat input handling (now uses ConfigurationManager delegation)
- Messages file duplicate error on second startup
- Configuration loading from separate YAML files
- Missing `ensureFileExists()` for safe file initialization

### Changed

- Inventory GUI: arena selector, kit selector, team selector, stats, leave items unified to DARK_AQUA
- Inventory titles: ArenaSelectorTitle, KitSelector, SelectorTeamTitle no longer use prefix line
- Messages: all 107 `Messages.raw()` calls replaced with 86 typed `*Text()` methods

### Removed

- Hardcoded chest loot (now config-driven)
- Hardcoded shop data (now in config)
- Direct HashMap access (delegated to managers)
- Monolithic event/command classes
- `BlockTracker.java`, `PotionChest.java`, `LootTable.java`
- `LobbySettings.java` (entire class unused)
- `ConfigKeys.java` (36/37 dead enum values inlined)
- 19 dead static fields from `Settings.java`
- `IManager.reload()` from interface and all 8 implementations
- ~30 dead methods from `Lobby.java`
- 13 dead methods from `Arena.java`, 8 from `Game.java`, 10 from `LobbyConfig.java`
- ~160 dead method declarations across all 8 managers
- BAD_OMEN entries 21-27 from `shop.yml` and `BootstrapInitializer.java` shop seeding

## Known Issues

None currently reported.

## Contributors

- lennartabeln - Original author

---

**Format**: This changelog follows [Keep a Changelog](https://keepachangelog.com/)  
**Versioning**: This project follows [Semantic Versioning](https://semver.org/)
