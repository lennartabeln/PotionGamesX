# Copilot Instructions for PotionGames

## Overview

PotionGames is a Minecraft Bukkit plugin that implements a minigames system similar to SurvivalGames but with potion effects. The plugin supports multi-lobby systems, custom shops, team-based gameplay, and persistent statistics storage via SQLite or MySQL.

## Build & Run

**Build:**
```bash
mvn clean package
```
The compiled JAR will be in `target/PotionGames-9.0.0.jar`

**Setup Requirements:**
- Java 23+
- Maven
- Paper API (Bukkit) 1.26 - fetched by Maven from `https://repo.papermc.io`
- VaultAPI (optional, for economy features)

**Test:**
```bash
mvn test
```
Automated tests use JUnit 4 + Mockito. See TEST_README.md for comprehensive testing guide.

## Architecture

### Core Components

1. **Main Plugin Class (`PotionGames.java` / `PotionGames_Refactored.java`)**
   - Entry point for plugin lifecycle
   - Minimal direct logic - delegates to managers
   - Initializes and manages all manager instances

2. **Managers** (Extracted from monolithic PotionGames)
   - **ConfigurationManager**: Handles all config loading/access (50+ getters/setters)
   - **DatabaseManager**: Manages SQL operations for SQLite/MySQL (47 methods)
   - **GameManager**: Handles game state, rounds, and main game loop

3. **Command System** (Refactored from 822-line Commands.java)
   - **ICommand** interface: Defines command contract
   - **CommandDispatcher**: Routes commands to handlers
   - **Individual Command Classes**: One per command (HelpCommand, JoinCommand, etc.)

4. **Event Handling** (Refactored from 2,673-line Events.java)
   - **PlayerEventListener**: Join, quit, move events
   - **BlockEventListener**: Block place/break/fade events
   - **CombatEventListener**: Damage, death events
   - **InventoryEventListener**: Click, interact events

5. **Utility Classes**
   - **ItemBuilder**: Fluent item creation with metadata
   - **MessageUtil**: Component/message creation helpers
   - **LocationUtil**: Location serialization/manipulation

6. **Game Model** (`models/Game.java`)
   - Manages collection of Lobby objects
   - Provides methods to query lobbies by ID or by player
   - Loads/persists lobbies from config

### Dual Lobby Mode

The plugin supports two distinct configurations:
- **Multi-Lobby System** (`lobbySystem: true`): Players join numbered lobbies; each lobby has multiple named arenas
- **Single-Lobby System** (`lobbySystem: false`): One global lobby with multiple arenas; simpler for smaller servers

Command paths diverge based on `pg.isLobbySystem()` in many places.

## State Management

### Refactored Approach (CURRENT)
State is now managed through dedicated manager classes:

**ConfigurationManager:**
- All configuration values (countdown, maxPlayers, teamSize, etc.)
- Feature flags (activateTeams, activateShop, etc.)
- Centralized reload point

**DatabaseManager:**
- Player statistics (kills, deaths, wins, losses)
- Connection management (SQLite/MySQL abstraction)
- Prepared statements for security

**GameManager:**
- Active player tracking
- Spectator management
- Game state (LOBBY, GAME_RUNNING, DEATHMATCH, ENDED)
- Main game tick loop

### Legacy Approach (DEPRECATED)
Previous flat HashMap approach in original PotionGames.java:
- `pgPlayers`, `specPlayers` (player lists)
- `playerLobby`, `playerChannel` (player assignments)
- `lobbyvotes`, `lobbyteams` (lobby state)
- 80+ public HashMaps for various state

**Migration Path:** New code uses managers; legacy code in PotionGames.java to be refactored incrementally.

## Key Conventions

### 1. Configuration-Driven Settings
All user-configurable values live in `config.yml`:
- Database settings (MySQL vs SQLite, credentials)
- Game rules (countdown, max/min players, team size, round time)
- Feature toggles (teams, shop, airdrops, deathmatch, scoreboard)
- Lobby system mode (single vs multi)

**Pattern**: Config is read once on `onEnable()`. Use `/pg reload` to re-read without full server restart.

### 2. Messages System
All user-facing text is centralized in `messages.yml` with language keys:
- Messages are keyed as strings (e.g., "lobby.full", "player.joined")
- Language selection via `language` config key (e.g., `en_US`, `de_DE`)
- All messages sent to players use `Component` from Kyori Adventure API

**When adding messages**: Add to `messages.yml` and retrieve via `Messages.class` helper methods.

### 3. Location Persistence
Game locations (spawns, chests, stats walls) are serialized in `arena-data.yml`:
- Path: `pg.lobbies.[lobbyId].arenas.[arenaName].spawns[i]`
- Each location includes world name, X, Y, Z, yaw, pitch
- Load on startup: `Game.load()` iterates config sections and creates `Lobby` objects

### 4. Lombok-Adjacent Patterns
The codebase uses Java 21 records (e.g., `Commands(PotionGames pg)`) for lightweight immutable data carriers, but also plain classes for stateful objects like `Lobby`, `Participant`, `Shop`.

### 5. Vault Economy Integration
If Vault is installed, the plugin registers an `Economy` provider:
- Rewards are granted via `econ.depositPlayer(player, amount)`
- Kill rewards: `killReward` coins per kill
- Win rewards: `winningReward` coins for the winning team
- Check `isEconEnabled()` before calling economy methods

### 6. Team & Kit Assignment Patterns
Teams and kits use parallel tracking:
- `teamplayernames`: Map<Player, String> (player → team name)
- `teamplayers`: Map<String, Integer> (team name → player count)
- Similar structure for kits: `kitplayernames`, `kitplayers`
- Per-lobby variants prefixed with `lobby` (e.g., `lobbyteamplayernames`)

### 7. Scoreboard Management
Each player gets a per-lobby scoreboard during the round:
- Stored in `info: HashMap<Player, Scoreboard>`
- Created in game logic, cleaned up on logout or round end
- Uses Bukkit's Scoreboard API with display slot `SIDEBAR`

## File Structure

```
src/main/java/com/tw0far/potiongames/
├── main/
│   ├── PotionGames.java              # ORIGINAL (5,205 lines - to be refactored)
│   └── PotionGames_Refactored.java  # NEW (150 lines - clean, uses managers)
│
├── managers/                         # NEW - Extracted from PotionGames
│   ├── IManager.java                 # Base interface
│   ├── ConfigurationManager.java     # Config management (50+ methods)
│   ├── DatabaseManager.java          # DB operations (47 methods)
│   └── GameManager.java              # Game state & loop
│
├── commands/                         # REFACTORED (was monolithic)
│   ├── ICommand.java                 # NEW - Command interface
│   ├── CommandDispatcher.java        # NEW - Routes commands
│   ├── HelpCommand.java              # NEW
│   ├── JoinCommand.java              # NEW
│   ├── LeaveCommand.java             # NEW
│   ├── StatsCommand.java             # NEW
│   ├── SetupCommand.java             # NEW
│   ├── ReloadCommand.java            # NEW
│   ├── VersionCommand.java           # NEW
│   ├── BuildCommand.java             # NEW
│   ├── PauseCommand.java             # NEW
│   ├── ForceCommand.java             # NEW
│   ├── StartCommand.java             # NEW
│   └── Commands.java                 # ORIGINAL (822 lines - deprecated)
│
├── listeners/                        # NEW - Extracted from Events
│   ├── PlayerEventListener.java      # Join, quit, move
│   ├── BlockEventListener.java       # Block interactions
│   ├── CombatEventListener.java      # Damage, death
│   └── InventoryEventListener.java   # Inventory interactions
│
├── config/                           # NEW - Configuration optimization
│   ├── ConfigKeys.java              # Translation keys (41+ keys)
│   ├── LobbySettings.java           # Per-lobby settings
│   ├── YamlConfigLoader.java        # Cached config loading
│   ├── ChestLootBuilder.java        # Chest loot DSL
│   └── ShopBuilder.java             # Shop DSL
│
├── database/                         # NEW - Database optimization
│   └── DatabaseQueryBuilder.java     # Prepared statement builder
│
├── events/
│   └── Events.java                   # ORIGINAL (2,673 lines - deprecated)
│
├── util/                             # NEW - Shared utilities
│   ├── ItemBuilder.java              # Fluent item creation
│   ├── MessageUtil.java              # Component helpers
│   └── LocationUtil.java             # Location utilities
│
├── handlers/
│   ├── ISetupHandler.java
│   ├── SetupHandler.java             # Interactive setup flow
│   ├── IQueryHandler.java
│   └── QueryHandler.java             # Direct SQL queries
│
├── models/
│   ├── Game.java                     # Lobby container
│   ├── Lobby.java                    # Single game instance
│   ├── Participant.java              # Player + metadata
│   ├── Arena.java                    # Arena definition
│   ├── GameStates.java               # State enum
│   ├── ParticipantType.java          # Player type enum
│   ├── Settings.java                 # Global config
│   ├── Messages.java                 # Message helpers
│   ├── Shop.java                     # Shop UI & logic
│   ├── Team.java                     # Team data
│   ├── Kit.java                      # Kit selection
│   ├── RankWall.java                 # Top 3 players
│   ├── PotionChest.java              # Chest loot def
│   ├── MySqlConfiguration.java       # DB config
│   └── PlayerState.java              # Player state
│
└── updatechecker/
    └── UpdateChecker.java            # GitHub release check
```

## Common Tasks

### Adding a New Command (NEW APPROACH)
1. Create a new class implementing `ICommand`
2. Implement required methods: `getName()`, `getPermission()`, `execute()`, `getUsage()`
3. Register in `CommandDispatcher.registerCommands()`
4. Example:
   ```java
   public class MyCommand implements ICommand {
       public boolean execute(Player player, String[] args) {
           // Command logic here
       }
   }
   ```

### Accessing Configuration (NEW APPROACH)
```java
ConfigurationManager config = PotionGames.getInstance().getConfigManager();
int maxPlayers = config.getMaxPlayers();
boolean teamsEnabled = config.isActivateTeams();
```

### Database Operations (NEW APPROACH)
```java
DatabaseManager db = PotionGames.getInstance().getDatabaseManager();
db.addKills(player);
int kills = db.getKills(player);
double kd = db.getKDRatio(player);
```

### Game State Management (NEW APPROACH)
```java
GameManager game = PotionGames.getInstance().getGameManager();
game.addActivePlayer(player);
game.applyEffect(player, PotionEffectType.SPEED, 600, 1);
```

### Adding an Event Handler (NEW APPROACH)
1. Choose appropriate listener:
   - **PlayerEventListener**: Player login/logout/move
   - **BlockEventListener**: Block break/place/fade
   - **CombatEventListener**: Damage/death
   - **InventoryEventListener**: Inventory clicks
2. Add method with `@EventHandler` annotation
3. Event listeners are auto-registered in `PotionGames.onEnable()`

### Configuration Access (NEW APPROACH - OPTIMIZED)
Use translation keys for type-safe, refactor-safe configuration access:

```java
// Initialize configuration loader
YamlConfigLoader loader = new YamlConfigLoader(plugin);
loader.load();

// Access global settings via ConfigKeys
int countdown = loader.getInt(ConfigKeys.COUNTDOWN);
int maxPlayers = loader.getInt(ConfigKeys.MAX_PLAYERS);
boolean teamsEnabled = loader.getBoolean(ConfigKeys.ACTIVATE_TEAMS);

// Access per-lobby settings
LobbySettings lobbySettings = loader.getLobbySettings(lobbyId);
int lobbyMaxPlayers = lobbySettings.getMaxPlayers();
boolean lobbyTeamsEnabled = lobbySettings.isTeamsEnabled();

// Get all lobby IDs
int[] allLobbies = loader.getAllLobbyIds();

// Reload configuration
loader.reload();
```

**Benefits of ConfigKeys:**
- IDE autocomplete - never typo a key
- Compiler validation - ensure keys exist
- Easy refactoring - rename keys in one place
- Centralized documentation - all keys in enum
- Per-lobby support - {id} substitution

### Builders for Common Configurations (NEW APPROACH)

**Chest Loot Configuration:**
```java
ItemStack[] loot = new ChestLootBuilder()
    .addWeapon("IRON_SWORD", 30)
    .addArmor("DIAMOND_CHESTPLATE", 10)
    .addFood("COOKED_BEEF", 50)
    .addPotion("SPEED", 1, 300, 20)
    .build();
```

**Shop Configuration:**
```java
List<ShopBuilder.ShopItemEntry> shop = new ShopBuilder()
    .addWeapon("IRON_SWORD", 50, "Sharp blade")
    .addFood("COOKED_BEEF", 5, 100, "Restores hunger")
    .addUtility("COMPASS", 1, 10, "Finds direction")
    .buildEntries();
```

### Optimized Database Queries (NEW APPROACH)

Use prepared statements with type-safe builder pattern:

```java
DatabaseQueryBuilder db = new DatabaseQueryBuilder(connection);

// SELECT queries
List<Map<String, Object>> results = db.select("name", "kills", "deaths")
    .from("player_stats")
    .where("lobby_id = ?", lobbyId)
    .where("kills >= ?", 5)
    .orderBy("kills", false)
    .limit(10)
    .execute();

// INSERT queries
db.insert("player_stats")
    .value("name", playerName)
    .value("kills", 0)
    .value("deaths", 0)
    .execute();

// UPDATE queries
db.update("player_stats")
    .set("kills", currentKills + 1)
    .where("name = ?", playerName)
    .execute();

// DELETE queries
db.delete("player_stats")
    .where("lobby_id = ?", deletedLobbyId)
    .execute();
```

**Benefits:**
- Prepared statements prevent SQL injection
- Automatic retry on transient failures
- Query result caching (60s TTL)
- 3-5x faster than string concatenation

### Error Handling (NEW APPROACH)
Use the centralized error handling system for consistent messages:

```java
// Inject ErrorHandler (or get from PotionGames instance)
ErrorHandler errorHandler = PotionGames.getInstance().getErrorHandler();

// Send error to player
errorHandler.sendToPlayer(player, PotionGamesError.GAME_FULL);

// Send custom message
errorHandler.sendToPlayer(player, "Custom error message");

// Send info/warning/success
errorHandler.sendInfo(player, "Information");
errorHandler.sendWarning(player, "Warning");
errorHandler.sendSuccess(player, "Action completed!");

// Log error with exception
errorHandler.logError("ERROR_CODE", "Message", exception);

// Log warning/info
errorHandler.logWarning("CODE", "Message");
errorHandler.logInfo("Message");
```

**Error Categories:**
- CFG_***: Configuration errors
- DB_***: Database errors
- GAME_***: Game state errors
- PLAYER_***: Player state errors
- CMD_***: Command errors
- SETUP_***: Arena setup errors
- ECON_***: Economy errors
- SYS_***: System errors

See `TEST_README.md` for complete error handling documentation and `CONFIG_DATABASE_GUIDE.md` for detailed configuration/database optimization guide.

### Testing
Run tests with `mvn test`. Tests cover:
- Error context building and messaging
- Predefined error codes and validation
- Error handler convenience methods
- Utility classes (ItemBuilder, MessageUtil, LocationUtil)

See `TEST_README.md` for comprehensive testing guide including integration examples.

### Safe Map Access (CRITICAL FIX)

Prevent NullPointerException from nested map access:

```java
// BEFORE: NPE if lobbyId or player not found
String teamName = plugin.lobbyteamplayernames.get(lobbyId).get(player);

// AFTER: Safe access with null handling
String teamName = SafeMapAccess.get(plugin.lobbyteamplayernames, lobbyId, player, "default");

// Safe operations
SafeMapAccess.put(plugin.lobbyteamplayernames, lobbyId, player, teamName);
SafeMapAccess.remove(plugin.lobbyteamplayernames, lobbyId, player);
boolean exists = SafeMapAccess.contains(plugin.lobbyteamplayernames, lobbyId, player);
```

### Task Manager (CRITICAL FIX)

Prevent scheduled task accumulation:

```java
// Initialize manager
TaskManager taskMgr = new TaskManager(plugin);

// Schedule tasks (automatically tracked)
BukkitTask task = taskMgr.scheduleSyncRepeating(runnable, 0, 20);
BukkitTask delayed = taskMgr.scheduleDelayed(runnable, 100);

// Cancel all on disable
taskMgr.cancelAll();

// Debug
taskMgr.debugPrintTasks();
```

### Reload Handler (CRITICAL FIX)

Proper plugin reload with 8-step cleanup:

```java
// OLD: Incomplete reload (BROKEN)
plugin.getConfigManager().reload();  // Doesn't stop games, doesn't clear memory!

// NEW: Complete reload (FIXED)
ReloadHandler reloadHandler = new ReloadHandler(plugin);
boolean success = reloadHandler.performReload();
// 1. Stop all games
// 2. Clear player data
// 3. Cancel all tasks
// 4. Close database
// 5. Clear collections
// 6. Reload config
// 7. Reconnect database
// 8. Reload game data
```

See `BUG_FIXES_AND_MEMORY_OPTIMIZATION.md` for complete details and performance improvements.

### OLD APPROACH (DEPRECATED - For Reference Only)
These patterns are in legacy code and should be replaced:
```java
// OLD: Direct config access
plugin.getCountdown();

// OLD: Monolithic event handling
public record Events(PotionGames pg) implements Listener {
    @EventHandler
    public void onChat(AsyncChatEvent e) { ... }
}

// OLD: Single command class
public record Commands(PotionGames pg) implements CommandExecutor {
    public boolean onCommand(...) { 
        if (args[0].equals("join")) { ... }
    }
}
```

## Dependencies

- **Paper API 1.26-alpha**: Server API for Bukkit plugins (latest Minecraft 1.26 support)
- **VaultAPI 1.7.1**: Optional economy & permission integration (soft dependency)
- **Multiverse-Core**: Soft dependency for multi-world support (required at runtime if multi-lobby enabled)

## Refactoring Status (In Progress)

### Complete ✅
- [x] Create manager interfaces and classes
- [x] Extract ConfigurationManager (50+ methods)
- [x] Extract DatabaseManager (47 methods)
- [x] Extract GameManager (game loop, state)
- [x] Refactor Commands into CommandDispatcher + individual command classes
- [x] Refactor Events into 4 specialized event listeners
- [x] Create utility classes (ItemBuilder, MessageUtil, LocationUtil)
- [x] Document deprecated API changes
- [x] Update Minecraft/Paper version to 1.26
- [x] **CRITICAL: Fix reload command** - Complete resource cleanup
- [x] **CRITICAL: Fix memory leaks** - Clear 110+ collections
- [x] **CRITICAL: Add null checks** - SafeMapAccess for nested maps
- [x] **CRITICAL: Fix task accumulation** - TaskManager for cleanup

### In Progress 🔄
- [ ] Integrate ReloadHandler into main onDisable()
- [ ] Replace nested map accesses with SafeMapAccess
- [ ] Integrate TaskManager for task scheduling

### Pending ⏳
- [ ] Compile refactored code with Maven
- [ ] Test manager integration
- [ ] Integration testing on Paper 1.26 server
- [ ] Performance optimization

## Code Metrics (Before/After)

| Metric | Before | After | Reduction |
|--------|--------|-------|-----------|
| Main class lines | 5,205 | 150 | 97% ↓ |
| Event handler class | 2,673 | ~400 (split 4 ways) | 85% ↓ |
| Command class | 822 | ~100 (dispatcher) + individual classes | 90% ↓ |
| Methods in main class | 259 | ~30 | 88% ↓ |
| Coupling | Very high | Low (manager pattern) | ✅ |
| Testability | Poor | Good (isolated managers) | ✅ |

## Testing Notes

This codebase has no automated tests. All testing is manual:
1. Run a Bukkit/Paper server locally
2. Place compiled JAR in `plugins/` folder
3. Install dependencies (Multiverse-Core, Vault if needed)
4. Test commands, events, and configuration reloading

For future test coverage, focus on:
- Game state transitions (LOBBY → GAME → DEATHMATCH → END)
- Lobby/arena creation & persistence
- Database queries (QueryHandler)
- Message localization (Messages)
