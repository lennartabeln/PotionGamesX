# Copilot Instructions for PotionGames

## Overview

PotionGames is a Minecraft Paper 26.1.x plugin that implements a minigames system similar to SurvivalGames but with potion effects. The plugin supports multi-lobby systems, custom shops, team-based gameplay, and persistent statistics storage via SQLite or MySQL.

**Current Architecture**: Fully refactored to class-based OOP (Phase 7 complete). No monolithic singleton patterns.

## Build & Run

**Build:**
```bash
mvn -DskipTests clean package
```
The compiled JAR will be in `target/PotionGamesX-1.0.0.jar`

**Setup Requirements:**
- Java 25+
- Maven 3.8+
- Paper API 26.1.x (fetched by Maven from `https://repo.papermc.io`)
- VaultAPI 1.7.1 (optional, for economy features)
- Multiverse-Core (soft dependency, for multi-world support)

**Run Tests:**
```bash
mvn test
```
Manual integration tests only (no automated unit tests). See TEST_README.md for testing guide.

## Architecture (Phase 7 - Class-Based OOP)

### Domain Model Classes (Own State & Logic)

1. **Game.java** (Global state)
   - Collection of all active Lobby instances
   - Player tracking: active players, spectators, per-lobby assignments
   - Accessor methods: `getPlayerLobby()`, `isActivePlayer()`, `getPlayersInLobby()`
   - ~350 lines, owns 40+ state fields

2. **Lobby.java** (Per-lobby state)
   - Game state machine: LOBBY, GAME_RUNNING, DEATHMATCH, ENDED
   - Countdown, player teams, arena voting, stats tracking
   - Per-lobby shop, chests, loot tables
   - Tick-based game loop: handles rounds, timers, player checks
   - ~600 lines, owns 40+ state fields, delegates spawning/effects to effects subsystem

3. **Participant.java** (Player metadata)
   - Wrapper for Player + game state (team, kit, kills, deaths)
   - Stores player's active/spectator state per lobby

4. **Arena.java** (Arena definition)
   - Spawns, deathmatch spawns, votable arena metadata

5. **Shop, Team, Kit** (Game feature models)
   - Shop: potion shop UI with purchase logic
   - Team: team assignment and counting
   - Kit: kit selection and assignment

### Manager Classes (Coordinate cross-domain logic)

- **IConfigurationManager**: Load/reload config, fetch settings by key
- **IDatabaseManager**: Store/retrieve player stats (kills, deaths, wins)
- **ILobbyStateManager**: Per-lobby settings (activation flags, chest data, votes)
- **IPlayerStateManager**: Save/restore player inventory, level, XP
- **IArenaStateManager**: Arena voting and team assignments
- **IItemStateManager**: Loot tables, shop items, potions
- **IBlockStateManager**: Track placed/broken blocks per lobby
- **ISetupStateManager**: Temporary setup mode player state

### Command System (11 command classes)

- **CommandDispatcher**: Routes `/pg` subcommands to handlers
- **Individual classes**: JoinCommand, LeaveCommand, StartCommand, PauseCommand, BuildCommand, ForceCommand, StatsCommand, HelpCommand, VersionCommand, SetupCommand, ReloadCommand
- Pattern: Implement `ICommand` interface, use delegation methods from Game/Lobby/managers

### Event Listeners (4+ listeners)

- **PlayerEventListener**: Join, quit, move events
- **BlockEventListener**: Block break/place/fade, bucket, leaves decay
- **CombatEventListener**: Death, damage, entity tracking
- **InventoryEventListener**: Shop, team selection, arena voting, kit selection
- **Bootstrap Initializers**: ChestLootInitializer, RankWallUpdater, EnableBootstrapInitializer, etc.

### Utility Classes

- **ItemBuilder**: Fluent item creation
- **MessageUtil**: Component/text formatting
- **LocationUtil**: Serialization utilities
- **BlockTracker**: Persistent block tracking

### Lobby-Based Configuration Model

The plugin uses per-lobby configuration:
- Default settings in `pg.defaults.*`
- Per-lobby overrides in `pg.lobbies.<id>.settings.*`
- Fallback to defaults if per-lobby setting missing

## State Management (Phase 7 Architecture)

### Domain Model Ownership (CURRENT - Phase 7)

**Game.java** owns:
- Active players (pgPlayers)
- Spectator players (specPlayers)
- Player → Lobby mapping (playerLobby, playerChannel)
- Lobby instances (lobbies collection)
- Global state collections that don't belong in a specific lobby

**Lobby.java** owns:
- Per-lobby game state (countdown, state machine)
- Per-lobby player assignments (teams, kits, votes)
- Per-lobby shop data, chests, loot tables
- Per-lobby block tracking
- Tick-based game loop (update method called by PotionGames)

**Manager Classes** coordinate domain logic:
- ConfigurationManager: Provides read-only access to config
- DatabaseManager: Persistence for player stats
- ArenaStateManager: Arena voting coordination
- ItemStateManager: Loot table management
- etc.

### Delegation Pattern (CRITICAL)

All code should use accessor methods on Game/Lobby, NOT direct HashMap access:

```java
// ✅ CORRECT: Use delegation methods
String lobbyId = game.getPlayerLobby(player);
Lobby lobby = game.getLobby(lobbyId);
String team = lobby.getPlayerTeam(player);

// ❌ WRONG: Direct HashMap access (bypasses logic, causes NPE)
String team = plugin.lobbyteamplayernames.get(lobbyId).get(player);
```

### Migration Status (Phase 7.5)

**45+ HashMap references migrated to delegation** across:
- InventoryEventListener: Shop data, chest inventory, team data
- CombatEventListener: Team checks, spectator state
- ChatEventListener: Team chat filtering

**Remaining direct accesses** in PotionGames.java are intentional:
- Global config fields (to be accessed via ConfigurationManager)
- Message templates (not per-lobby state)
- Potion definitions (global constants)

## Key Conventions

### 1. Accessing Game/Lobby State (CRITICAL - Phase 7 Pattern)

Always use delegation methods, never direct HashMap access:

```java
// ✅ Player lists - use Game
if (game.isActivePlayer(player)) { ... }
for (Player p : game.getPlayersInLobby(lobbyId)) { ... }

// ✅ Lobby data - use Lobby
String team = lobby.getPlayerTeam(player);
Inventory chest = lobby.getChestInventory(location);
boolean isShop = lobby.hasShop(location);

// ✅ Config - use managers
int countdown = configManager.getCountdown();
boolean teamsEnabled = configManager.isActivateTeams();

// ✅ Database - use manager
int kills = databaseManager.getKills(player);
databaseManager.addKill(player);
```

### 2. Implementing Commands

All commands extend ICommand interface:
```java
public class MyCommand implements ICommand {
    public MyCommand(PotionGames plugin) { this.plugin = plugin; }
    
    @Override
    public String getName() { return "mycommand"; }
    
    @Override
    public String getPermission() { return "pg.mycommand"; }
    
    @Override
    public boolean requiresGameServer() { return true; }
    
    @Override
    public boolean execute(Player player, String[] args) {
        // Use delegation methods from game/lobby
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            player.sendMessage("You're not in a game!");
            return true;
        }
        // ... logic
        return true;
    }
    
    @Override
    public String getUsage() { return "/pg mycommand"; }
}
```

Commands are auto-registered in CommandDispatcher constructor.

### 3. Implementing Event Listeners

All listeners are registered in PotionGames.onEnable():
```java
pm.registerEvents(new PlayerEventListener(plugin), plugin);
pm.registerEvents(new BlockEventListener(plugin), plugin);
pm.registerEvents(new CombatEventListener(plugin), plugin);
pm.registerEvents(new InventoryEventListener(plugin), plugin);
```

Use @EventHandler annotation:
```java
public class MyListener implements Listener {
    public MyListener(PotionGames plugin) { this.plugin = plugin; }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) return;
        
        Lobby lobby = plugin.getGame().getLobby(lobbyId);
        // Use delegation methods
        lobby.addDeath(player);
    }
}
```

### 4. Configuration-Driven Settings

All user-configurable values live in `config.yml`:
- Database settings (MySQL vs SQLite, credentials)
- Game rules (countdown, max/min players, team size, round time)
- Feature toggles (teams, shop, airdrops, deathmatch, scoreboard)

Access via ConfigurationManager:
```java
IConfigurationManager config = plugin.getConfigManager();
int maxPlayers = config.getMaxPlayers();
boolean teamsEnabled = config.isActivateTeams();
config.reload(); // Re-read config without server restart
```

### 5. Messages System

All user-facing text is in `messages.yml` with language keys:
- Keyed as strings (e.g., "lobby.full", "player.joined")
- Language selection via `language` config key (e.g., `en_US`, `de_DE`)
- Sent using Kyori Adventure API Component

Use Messages helper class:
```java
Component msg = Messages.get("lobby.full");
player.sendMessage(msg);
```

### 6. Location Persistence

Game locations (spawns, chests, stats walls) serialized in `arena-data.yml`:
- Path: `pg.lobbies.[lobbyId].arenas.[arenaName].spawns[i]`
- Each location: world, X, Y, Z, yaw, pitch
- Load on startup: `Game.load()` creates Lobby objects

### 7. Team & Kit Patterns

Teams and kits use per-lobby tracking in Lobby:
```java
// Get team
String team = lobby.getPlayerTeam(player);

// Assign team
lobby.setPlayerTeam(player, teamName);

// Count
int count = lobby.getLobbyTeamPlayerCount(teamId);
```

Similar methods for kits.

### 8. Vault Economy Integration

If Vault is installed, check before using:
```java
if (econ != null && econ.isEnabled()) {
    EconomyResponse resp = econ.depositPlayer(player, amount);
    if (resp.transactionSucceeded()) { ... }
}
```

Kill rewards: `killReward` coins per kill  
Win rewards: `winningReward` coins for winning team

### 9. Scoreboard Management

Each player gets per-lobby scoreboard during round:
```java
Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
Objective obj = sb.registerNewObjective("potiongames", Criteria.DUMMY);
obj.setDisplaySlot(DisplaySlot.SIDEBAR);
player.setScoreboard(sb);
```

Clean up on logout or round end.

### 10. Block Tracking (Lobby-Owned)

Lobby tracks placed/broken blocks per player:
```java
lobby.trackBlockPlace(player, block);
lobby.trackBlockBreak(player, block);
// On logout: lobby.clearPlayerBlocks(player);
```

## File Structure

```
src/main/java/com/tw0far/potiongames/
├── main/
│   └── PotionGames.java                 # Main plugin class (1502 lines, delegating)
│
├── models/                              # Domain model classes (own state)
│   ├── Game.java                        # Global player/lobby tracking (~350 lines)
│   ├── Lobby.java                       # Per-lobby game state & tick loop (~600 lines)
│   ├── Participant.java                 # Player wrapper (team, kit, kills, deaths)
│   ├── Arena.java                       # Arena definition (spawns, metadata)
│   ├── Shop.java                        # Shop UI & logic
│   ├── Team.java                        # Team data
│   ├── Kit.java                         # Kit data
│   ├── RankWall.java                    # Top 3 players display
│   ├── BlockTracker.java                # Block tracking per player
│   ├── GameStates.java                  # State enum (LOBBY, GAME_RUNNING, DEATHMATCH, ENDED)
│   ├── ParticipantType.java             # Player type enum (ACTIVE, SPECTATOR)
│   ├── Messages.java                    # Message key constants
│   ├── Settings.java                    # Global settings
│   ├── LobbyConfig.java                 # Per-lobby config
│   ├── LootTable.java                   # Loot definitions
│   ├── PotionChest.java                 # Chest metadata
│   ├── RankWallItem.java                # RankWall item
│   ├── MySqlConfiguration.java          # Database config
│   └── PlayerState.java                 # Player inv/level save
│
├── managers/                            # Coordination classes
│   ├── IConfigurationManager            # Interface
│   ├── ConfigurationManager             # Config loading/access
│   ├── IDatabaseManager                 # Interface
│   ├── DatabaseManager                  # Player stats storage
│   ├── ILobbyStateManager               # Interface
│   ├── LobbyStateManager                # Per-lobby settings
│   ├── IPlayerStateManager              # Interface
│   ├── PlayerStateManager               # Player inv/level
│   ├── IArenaStateManager               # Interface
│   ├── ArenaStateManager                # Arena voting
│   ├── IItemStateManager                # Interface
│   ├── ItemStateManager                 # Loot/shop items
│   ├── IBlockStateManager               # Interface
│   ├── BlockStateManager                # Block tracking
│   ├── ISetupStateManager               # Interface
│   └── SetupStateManager                # Setup mode temp state
│
├── commands/                            # Command handlers
│   ├── ICommand.java                    # Command interface
│   ├── CommandDispatcher.java           # Routes /pg subcommands
│   ├── JoinCommand.java
│   ├── LeaveCommand.java
│   ├── StartCommand.java
│   ├── PauseCommand.java
│   ├── BuildCommand.java
│   ├── ForceCommand.java
│   ├── StatsCommand.java
│   ├── HelpCommand.java
│   ├── VersionCommand.java
│   ├── SetupCommand.java
│   └── ReloadCommand.java
│
├── listeners/                           # Event handlers
│   ├── PlayerEventListener.java         # Join, quit, move
│   ├── BlockEventListener.java          # Block break/place/fade
│   ├── CombatEventListener.java         # Death, damage
│   └── InventoryEventListener.java      # Shop, teams, voting, kits
│
├── bootstrap/                           # Startup logic
│   ├── EnableBootstrapInitializer       # Load lobbies/arenas
│   ├── ChestLootInitializer             # Init chest loot
│   ├── RankWallUpdater                  # Init rank walls
│   ├── BootstrapInitializer             # Bootstrap base
│   └── EnableBootstrapContext           # Setup context
│
├── handlers/                            # Complex workflows
│   ├── ISetupHandler.java               # Setup interface
│   ├── SetupHandler.java                # Interactive setup flow
│   └── JoinLobbyHandler.java            # Player join logic
│
├── config/                              # Config optimization
│   ├── ConfigKeys.java                  # Translation keys
│   ├── LobbySettings.java               # Per-lobby settings
│   └── YamlConfigLoader.java            # Cached config loading
│
├── database/                            # Database utilities
│   └── DatabaseQueryBuilder.java        # Prepared statement builder
│
├── util/                                # Shared utilities
│   ├── ItemBuilder.java                 # Fluent item creation
│   ├── MessageUtil.java                 # Component helpers
│   └── LocationUtil.java                # Location utilities
│
├── error/                               # Error handling
│   ├── ErrorContext.java                # Error builder
│   ├── PotionGamesError.java            # Error codes
│   └── ErrorHandler.java                # Error facade
│
└── updatechecker/
    └── UpdateChecker.java               # GitHub version check
```

## Common Tasks

### Adding a New Command (Phase 7 Pattern)

1. Create a new class implementing `ICommand`
2. Implement 5 required methods: `getName()`, `getPermission()`, `requiresGameServer()`, `execute()`, `getUsage()`
3. Command is auto-registered in CommandDispatcher
4. Use delegation methods from Game/Lobby

Example:
```java
public class MyCommand implements ICommand {
    private final PotionGames plugin;
    
    public MyCommand(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getName() { return "mycommand"; }
    
    @Override
    public String getPermission() { return "pg.mycommand"; }
    
    @Override
    public boolean requiresGameServer() { return true; }
    
    @Override
    public boolean execute(Player player, String[] args) {
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId == null) {
            player.sendMessage("Not in a game!");
            return true;
        }
        Lobby lobby = plugin.getGame().getLobby(lobbyId);
        // ... your logic using lobby delegation methods
        return true;
    }
    
    @Override
    public String getUsage() { return "/pg mycommand"; }
}
```

### Implementing Event Listeners (Phase 7 Pattern)

Register in `PotionGames.onEnable()`, then use delegation:

```java
public class MyListener implements Listener {
    private final PotionGames plugin;
    
    public MyListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onCustomEvent(CustomEvent event) {
        Player player = event.getPlayer();
        String lobbyId = plugin.getGame().getPlayerLobby(player);
        if (lobbyId != null) {
            Lobby lobby = plugin.getGame().getLobby(lobbyId);
            // Use lobby delegation methods
        }
    }
}
```

Then register in main class:
```java
pm.registerEvents(new MyListener(this), this);
```

### Accessing Game State (CRITICAL - Phase 7)

Always use delegation, never direct HashMap access:

```java
// Player checks
if (plugin.getGame().isActivePlayer(player)) { }
if (plugin.getGame().isSpectatorPlayer(player)) { }

// Lobby access
String lobbyId = plugin.getGame().getPlayerLobby(player);
Lobby lobby = plugin.getGame().getLobby(lobbyId);

// Lobby state queries
String team = lobby.getPlayerTeam(player);
int kills = lobby.getPlayerKills(player);
boolean hasShop = lobby.hasShop(location);

// Player tracking
for (Player p : lobby.getActivePlayers()) { }
for (Player s : lobby.getSpectators()) { }
```

### Accessing Configuration

Use ConfigurationManager:
```java
IConfigurationManager config = plugin.getConfigManager();
int countdown = config.getCountdown();
int maxPlayers = config.getMaxPlayers();
boolean teamsEnabled = config.isActivateTeams();
```

### Database Operations

Use DatabaseManager:
```java
IDatabaseManager db = plugin.getDatabaseManager();
db.addKill(player);
int kills = db.getKills(player);
double kd = db.getKDRatio(player);
```

### Accessing Messages

Use Messages helper class:
```java
Component msg = Messages.get("lobby.full");
player.sendMessage(msg);

Component msg2 = Messages.getFormatted("player.joined", player.getName());
Bukkit.broadcastMessage(msg2);
```

### Item & Inventory Helpers

Use ItemBuilder:
```java
ItemStack sword = new ItemBuilder(Material.IRON_SWORD)
    .setName("§6Legendary Blade")
    .addEnchantment(Enchantment.SHARPNESS, 3)
    .addLore("§7A powerful weapon")
    .setUnbreakable(true)
    .build();
```

### Error Handling

Use delegation if handler exists, or send direct message:
```java
if (!plugin.getGame().isActivePlayer(player)) {
    player.sendMessage(Component.text("You're not in a game!", NamedTextColor.RED));
    return true;
}
```

## Dependencies

- **Paper API 26.1.x**: Latest Bukkit API for Minecraft 26.1 servers
- **VaultAPI 1.7.1**: Optional economy & permission integration (soft dependency)
- **Multiverse-Core**: Soft dependency for multi-world support (required at runtime for multi-lobby)
- **Maven 3.8+**: Build tool

## Current Refactoring Status (Phase 7 - Class-Based OOP)

### Complete ✅

- [x] **Architecture**: Game and Lobby own all domain state (Phase 7.0)
- [x] **Managers**: 8 manager classes coordinate cross-domain logic
- [x] **Commands**: 11 individual command classes + dispatcher
- [x] **Event System**: 4+ listener classes (PlayerEventListener, BlockEventListener, CombatEventListener, InventoryEventListener)
- [x] **Delegation**: 45+ HashMap accesses migrated from direct access to delegation methods
- [x] **Compilation**: 0 errors, 32 safe warnings (deprecated Bukkit APIs)
- [x] **Code Reduction**: PotionGames.java reduced from ~5,200 to 1,502 lines (71% reduction)
- [x] **Bootstrap**: Lobby loading, chest loot init, rank wall setup
- [x] **Handlers**: JoinLobbyHandler, SetupHandler for complex workflows

### In Progress 🔄

- [ ] Phase 7.6: Remove 100+ legacy fields from PotionGames.java
- [ ] Phase 8: Further simplification and architectural cleanup

### Known Issues

1. **Legacy Code Still Present**: Old Events.java and Commands.java remain but are superseded
2. **Direct HashMap Fields**: PotionGames.java still contains 60+ fields that should be manager-owned (Phase 7.6 target)
3. **No Automated Tests**: Manual integration testing only

## Code Metrics (Phase 7 vs Phase 1)

| Metric | Phase 1 | Phase 7 | Reduction |
|--------|---------|---------|-----------|
| PotionGames.java lines | 5,205 | 1,502 | 71% ↓ |
| Event handler class | 2,673 (monolithic) | ~400 (split 4 ways) | 85% ↓ |
| Command class | 822 (monolithic) | ~100 (dispatcher) | 90% ↓ |
| Methods in main class | 259 | ~50 | 81% ↓ |
| Coupling | Very high | Low (OOP pattern) | ✅ |
| Testability | Poor | Good (separated concerns) | ✅ |
| Maintainability | Poor | Good (class-based) | ✅ |

## Testing Notes

This codebase currently has no automated unit tests. Testing is manual:

1. Run a Paper 26.1.x server locally
2. Place compiled JAR in `plugins/` folder
3. Install optional dependencies (Multiverse-Core, Vault if needed)
4. Test manually:
   - Commands: `/pg help`, `/pg join 1`, `/pg stats`, etc.
   - Events: kill player, break blocks, open shop, etc.
   - Configuration reload: `/pg reload`
   - Multi-lobby: join different lobbies
   - Deathmatch: trigger deathmatch phase
   - Database: verify stats persistence

For future test coverage, focus on:
- Game state transitions (LOBBY → GAME_RUNNING → DEATHMATCH → ENDED)
- Lobby/arena creation & persistence
- Player team/kit assignment
- Shop purchase logic
- Message localization
- Database CRUD operations
