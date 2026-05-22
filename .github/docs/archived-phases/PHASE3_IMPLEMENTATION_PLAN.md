# PHASE 3: HashMap Migration Implementation Plan

**Objective:** Migrate 100 public collection fields from PotionGames.java to proper domain classes

**Approach:** Aggressive refactoring in sub-phases focusing on highest-impact, most-used fields first

---

## Field Distribution

```
Total Fields: 100
├─ HIGH Priority: 12 fields (most-used, breaking core logic)
├─ MEDIUM Priority: 60 fields (lobby-specific state)
├─ LOW Priority: 20 fields (unused or dead code)
└─ REMOVE: 8 fields (shop/loot - should be YAML)
```

### Target Classes
- **Game.java**: Global player/lobby tracking (12 fields)
- **Lobby.java**: Lobby-specific state (60+ fields)
- **Arena.java**: Block restoration tracking (8 fields)
- **PlayerState.java**: Per-player state (6 fields)
- **Remove/Refactor**: Shop loot definitions (8 fields)

---

## PHASE 3.1: HIGH PRIORITY (Hours 1-4)

### Step 1: Add Accessors to Game.java

**Current:** pgPlayers, specPlayers in PotionGames
**Target:** Game.activePlayers, Game.spectators with proper accessors

```java
// Game.java additions
private List<Player> activePlayers = new ArrayList<>();
private List<Player> spectators = new ArrayList<>();
private Map<Player, String> playerLobbies = new HashMap<>();
private Map<Player, String> playerChannels = new HashMap<>();
private Map<String, List<Player>> channels = new HashMap<>();

public void addActivePlayer(Player p) { activePlayers.add(p); }
public void removeActivePlayer(Player p) { activePlayers.remove(p); }
public List<Player> getActivePlayers() { return unmodifiableList(activePlayers); }
public boolean isActivePlayer(Player p) { return activePlayers.contains(p); }
// ... 10+ more accessors
```

### Step 2: Add Accessors to Lobby.java

**Current:** lobbyteamplayernames, lobbyteams, lobbyvotes in PotionGames
**Target:** Lobby class with proper state management

```java
// Lobby.java additions
private Map<Player, String> teamAssignments = new HashMap<>();
private Map<String, Integer> teamCounts = new HashMap<>();
private List<String> teamList = new ArrayList<>();
private Map<Player, String> voteAssignments = new HashMap<>();
private Map<String, Integer> voteCounts = new HashMap<>();

public void assignTeam(Player p, String teamName) { teamAssignments.put(p, teamName); }
public String getTeam(Player p) { return teamAssignments.get(p); }
public Set<Player> getTeamMembers(String teamName) { /* filter teamAssignments */ }
// ... 15+ more accessors
```

### Step 3: Add Accessors to Arena.java

**Current:** placedBlocks, breakedBlocks, waterBlocks in PotionGames
**Target:** Arena class with block restoration logic

```java
// Arena.java additions
private Map<Location, Material> placedBlocks = new HashMap<>();
private Map<Location, Material> breakedBlocks = new HashMap<>();
private Map<Location, BlockData> waterBlocks = new HashMap<>();

public void recordPlacedBlock(Location loc, Material type) { placedBlocks.put(loc, type); }
public void recordBrokenBlock(Location loc, Material type) { breakedBlocks.put(loc, type); }
public void restoreAllBlocks() { /* restore from maps */ }
// ... 8+ more accessors
```

---

## PHASE 3.2: MEDIUM PRIORITY (Hours 5-8)

### Migrate Lobby-Specific Fields

Target all `lobby*` prefixed fields (60+ fields):
- `lobbyteams`, `lobbyteamplayernames`, `lobbyteamSize`
- `lobbyVote`, `lobbyVoted`, `lobbyvotes`
- `lobbyStates`, `lobbyCountdown`, `lobbyRoundTime`
- `lobbyActivateTeams`, `lobbyActivateKits`, `lobbyActivateShop`
- Etc.

**Each field mapping:**
```
pg.lobbyteamplayernames.get(lobbyId).get(player)
                    ↓
lobby.getTeam(player)
```

### Migrate Block Restoration Fields

- `lobbyPlacedBlocks`, `lobbyBreakedBlocks`
- `lobbyWaterBlocks`, `lobbyLiquidPlaced`
- `lobbyPlacedBlocksData`, etc.

---

## PHASE 3.3: LOW PRIORITY & CLEANUP (Hours 9-10)

### Low Priority Fields
- Unused fields (richkidPlayers, setupPlayer, worlds)
- Legacy fields (ranked, rankhead, ranksign)
- Shop/loot definitions (food1, armour1, weapons1, shop, potions)

### Action
- Analyze if truly unused (grep for usages)
- If unused: Remove entirely
- If used minimally: Keep but mark @Deprecated
- If shop-related: Externalize to YAML configuration

---

## Implementation Order

### Phase 3.1: HIGH PRIORITY (4 hours)
1. Enhance Game.java (1 hour)
   - Add activePlayers, spectators lists
   - Add playerLobbies, playerChannels maps
   - Add 15+ accessor methods
   - All read-only (return unmodifiableList/unmodifiableMap)

2. Enhance Lobby.java (1 hour)
   - Add team assignment tracking
   - Add vote tracking
   - Add 20+ accessor methods

3. Enhance Arena.java (1 hour)
   - Add block restoration maps
   - Add 8+ accessor methods

4. Update ONE command (1 hour)
   - Pick simplest: JoinCommand or LeaveCommand
   - Replace all pg.pgPlayers with game.addActivePlayer()
   - Test build

### Phase 3.2: MEDIUM PRIORITY (4 hours)
1. Update all remaining commands (2 hours)
   - Search/replace all pg.player* references
   - Test build after each major change

2. Update all event listeners (2 hours)
   - PlayerEventListener: pg.pgPlayers → game API
   - CombatEventListener: All field accesses
   - BlockEventListener: All field accesses
   - InventoryEventListener: All field accesses

### Phase 3.3: LOW PRIORITY (2 hours)
1. Identify unused fields (30 min)
   - Grep for each field name
   - Count occurrences

2. Remove/deprecate (1.5 hours)
   - Add @Deprecated to fields still used (backward compat)
   - Remove truly unused fields
   - Add migration comments

---

## Validation Strategy

### After Each Phase:
1. Run: `mvn clean compile -q`
2. Verify: 0 compilation errors
3. Run: `mvn test` (if tests exist)
4. Build JAR: `mvn package -DskipTests`

### Expected Metrics:
- **After 3.1:** ~2,500 lines in PotionGames.java (currently 3,819)
- **After 3.2:** ~1,500 lines in PotionGames.java
- **After 3.3:** ~1,000 lines in PotionGames.java (Goal!)

---

## High-Risk Areas

1. **Nested Map Access:** e.g., `lobbyteamplayernames.get(lobbyId).get(player)`
   - Must handle null safely
   - Use SafeMapAccess utility or proper null checks

2. **Backward Compatibility:** Keep old fields alive during migration
   - Use @Deprecated
   - Delegate to new accessors internally

3. **Test Coverage:** Commands/listeners have no automated tests
   - Manual testing critical
   - Build verification mandatory after each phase

---

## Success Criteria

- [ ] Phase 3.1: Game, Lobby, Arena have proper accessors (0 compile errors)
- [ ] Phase 3.2: All commands/listeners updated to use new API (0 compile errors)
- [ ] Phase 3.3: Cleanup complete, dead code removed
- [ ] JAR builds successfully
- [ ] PotionGames.java under 1,500 lines (from 3,819)
- [ ] All 100+ fields either migrated or marked @Deprecated

---

## Status
**Current:** 100 fields identified and tracked in SQL
**Next:** Start Phase 3.1 - Enhance Game.java with accessors
**Priority:** HIGH (core refactoring task)

