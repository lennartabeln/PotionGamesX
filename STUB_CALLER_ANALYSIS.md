# Stub Method Restoration - Caller Analysis

## Methods Restored and Where They're Called

### 1. joinChannel(Player player, String channelName)
**Location**: Line 2719 in PotionGames.java

**Callers**:
- `PlayerEventListener.java`: Called when player logs in (joins Global channel)
- `onJoinLobby()`: Called when player joins a lobby (joins Local channel)  
- `onLeaveLobby()`: Called when player leaves a lobby (rejoins Global channel)

**Implementation**: Manages player chat channels by updating channels HashMap

---

### 2. chestData()
**Location**: Line 2737 in PotionGames.java

**Callers**:
- `onEnable()`: Line 1584 - called during plugin initialization

**Implementation**: Populates loot tables with ItemStack arrays:
- food1, food2 (edibles)
- armour1, armour2, armour3, armour4, armour5 (armor sets)
- weapons1, weapons2 (weapons)

---

### 3. clearEffects(Player all)
**Location**: Line 2797 in PotionGames.java

**Callers**:
- `SetupCommand.java`: Clears effects when player enters setup mode
- `InventoryEventListener.java`: Clears effects when shopping
- `onJoinLobby()`: Clears effects when joining a lobby

**Implementation**: Removes all active potion effects from a player:
- Checks configured potion effects from chestdata.yml
- Also removes all known Bukkit potion effect types

---

### 4. onJoinLobby(Player p, String s)
**Location**: Line 2831 in PotionGames.java

**Callers**:
- `InventoryEventListener.java`: When player clicks lobby entrance

**Implementation**: Prepares player for lobby gameplay:
1. Sets up scoreboard with teams, kits, map, kills tracking
2. Saves current player state (inventory, armor, location, level, exp, gamemode)
3. Clears inventory and applies default state
4. Removes all potion effects
5. Increments lobby player count
6. Teleports player to lobby spawn location

---

### 5. onLeaveLobby(Player p, String s)
**Location**: Line 2900 in PotionGames.java

**Callers**:
- `PlayerEventListener.java`: When player quits the server
- `InventoryEventListener.java`: When player clicks leave action

**Implementation**: Restores player to pre-lobby state:
1. Clears scoreboard
2. Records loss if game was in progress
3. Restores inventory, armor, location, level, exp, gamemode
4. Removes team assignments
5. Removes kit assignments
6. Removes voting data
7. Decrements lobby player count
8. Cleans up empty lobby data

---

### 6. tick()
**Location**: Line 2992 in PotionGames.java

**Callers**:
- `onEnable()`: Line 1610 - schedules the game loop

**Implementation**: 
- Schedules a synchronous repeating task
- Acts as compatibility wrapper for game loop
- Delegates to GameManager or individual Lobby game loops

---

## Build Impact

### Before Restoration
Build failed with errors:
- "Cannot find symbol: method onLeaveLobby(Player, String)"
- "Cannot find symbol: method onJoinLobby(Player, String)"
- "Cannot find symbol: method clearEffects(Player)"
- "Cannot find symbol: method joinChannel(Player, String)"
- "Cannot find symbol: method tick()"
- "Cannot find symbol: method chestData()"

### After Restoration
- All 6 methods now available
- Callers can compile without errors
- Each stub provides functional implementation
- No build errors related to missing methods

---

## Migration Notes

These stubs are temporary bridges during the refactoring from the monolithic PotionGames.java to the manager-based architecture. Eventually:

1. `joinChannel()` - Can be moved to ChannelManager
2. `chestData()` - Can be moved to ItemStateManager or config loader
3. `clearEffects()` - Can be moved to a utility class or PotionManager
4. `onJoinLobby()` - Logic can move to Lobby.onPlayerJoin()
5. `onLeaveLobby()` - Logic can move to Lobby.onPlayerLeave()
6. `tick()` - Game loop can move entirely to GameManager/Lobby classes

The stubs provide a stable interface for the current code while the underlying architecture is being modernized.
