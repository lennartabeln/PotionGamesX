# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
- Consolidated config structure (config.yml, chests.yml, kits.yml, messages.yml, shop.yml)

#### Architecture
- Class-based OOP (no monolithic patterns)
- 8+ manager classes for state delegation
- Separated event handlers into 4+ listener classes
- Individual command classes (34+ command handlers)
- Delegation methods throughout codebase

#### Build & Documentation
- Maven configuration for clean builds
- DOCUMENTATION.md

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

#### Inventory & Messages
- Arena selector: "Random" vote count now shows actual votes
- Kit selector: `handleKitSelection` applies kit+items on selection
- Messages.java: 86 typed `*Text()` methods replacing 107 `raw()` calls

### Fixed

- Setup mode chat input handling (now uses ConfigurationManager delegation)
- Messages file duplicate error on second startup
- Configuration loading from separate YAML files
- Missing `ensureFileExists()` for safe file initialization

### Changed

- Java 21 → 25 across all build configs and CI workflows
- `maven-resources-plugin` pinned to 3.5.0 for Eclipse M2E compat
- release.yml: reads release body from `CHANGELOG.md` instead of auto-generated notes
- release.yml: pre-release changelog uses category-aware diff against previous tag to show only new entries with headers
- CHANGELOG.md: restructured to [Keep a Changelog](https://keepachangelog.com/) format
- Inventory GUI: arena selector, kit selector, team selector, stats, leave items unified to DARK_AQUA
- Inventory titles: ArenaSelectorTitle, KitSelector, SelectorTeamTitle no longer use prefix line

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
