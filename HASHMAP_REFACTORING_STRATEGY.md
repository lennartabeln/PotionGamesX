# PotionGames HashMap/ArrayList Refactoring Strategy

**Status:** Phase 1 - Foundation Complete  
**Date:** April 27, 2026  
**Scope:** Consolidate 100+ public collection fields into proper domain classes

---

## Executive Summary

PotionGames.java currently contains **100+ public HashMaps and ArrayLists** that duplicate logic already implemented in proper domain classes (Game, Lobby, Participant). This document outlines a **pragmatic migration strategy** that:

1. **Maintains backward compatibility** - Old code continues to work
2. **Gradual migration** - Refactor incrementally, not all-at-once
3. **Reduces technical debt** - Move logic to proper classes
4. **Improves maintainability** - Clearer separation of concerns

---

## Current Problems

### 1. State Duplication
```java
// Current: State stored in PotionGames.java
pg.pgPlayers.add(player);
pg.playerLobby.put(player, "lobby1");
pg.teamplayernames.put(player, "team-red");

// Should be: State in proper classes
Game game = pg.getGame();
Lobby lobby = game.getLobbyById("lobby1");
game.addActivePlayer(player);
lobby.addTeamMember(player, "team-red");
```

### 2. Difficult to Track Changes
- No central place to see all player state
- Hard to reason about state consistency
- Each listener/command duplicates state logic

### 3. Memory Overhead
- 100+ collections * ~100 lobbies = thousands of HashMaps
- Each HashMap has memory overhead even if empty
- No cleanup guarantees when lobbies end

---

## Proposed Architecture

### Phase 1: Enhance Core Classes ✅ COMPLETE

**Added to Game.java:**
```java
private List<Player> activePlayers = new ArrayList<>();
private List<Player> spectators = new ArrayList<>();

public void addActivePlayer(Player p)
public void removeActivePlayer(Player p)
public void addSpectator(Player p)
public void removeSpectator(Player p)
public List<Player> getActivePlayers()
public List<Player> getSpectators()
public boolean isActivePlayer(Player p)
public boolean isSpectator(Player p)
public Lobby getLobbyContainingPlayer(Player p)
```

**Status:** ✅ Complete - Game.java now tracks active players

### Phase 2: Extend Lobby Class (NEXT)

Should add:
```java
public void addTeamMember(Player p, String team)
public void removeTeamMember(Player p)
public String getTeamAssignment(Player p)
public void setArenaVote(Player p, Arena arena)
public Arena getArenaVote(Player p)
public void setKitChoice(Player p, String kit)
public String getKitChoice(Player p)
```

### Phase 3: Create BlockTracker (NEXT)

Move all block tracking (placed, broken, water, liquid) into:
```java
public class BlockTracker {
    public void recordPlacedBlock(Location loc, Material original)
    public Material getOriginalBlockType(Location loc)
    public void recordBrokenBlock(Location loc, Material broken)
    public void restore(World world)  // Cleanup on round end
}
```

---

## Migration Path

### HIGH PRIORITY (Used Most Frequently)

#### 1. Player State (pgPlayers, specPlayers)
**Current Implementation:**
```java
pg.pgPlayers.add(player);        // 50+ places
pg.specPlayers.add(player);      // 30+ places
```

**Refactored To:**
```java
pg.getGame().addActivePlayer(player);
pg.getGame().addSpectator(player);
```

**Files to Update:** All 11 command classes + 4 event listeners

---

### MEDIUM PRIORITY (Configuration Values)

#### 2. Game Configuration
**Current:**
```java
pg.countdown = 60;
pg.maxPlayers = 24;
pg.teamSize = 2;
```

**Refactored To:**
```java
// Already in ConfigurationManager!
Settings.getCountdown()
Settings.getMaxPlayers()
Settings.getTeamSize()
```

**Status:** Already done via ConfigurationManager

---

### LOW PRIORITY (Rarely Used)

#### 3. Dead Code Fields
Remove these after verification they're unused:
- `richkidPlayers` - Not used anywhere
- `voted`, `teams`, `kits`, `teamed`, `kited` - Replaced by Participant
- `channels`, `playerChannel` - Not used in modern version

---

## Implementation Phases

### Phase 1: Foundation ✅ DONE
- [x] Enhanced Game.java with player tracking
- [x] Added 10+ accessor methods
- [x] Maintained backward compatibility (old fields still exist)
- [x] JAR builds successfully

### Phase 2: Migrate Commands (NEXT)
- [ ] Update all 11 command classes to use new accessors
- [ ] Test each command thoroughly
- [ ] Estimate: 2-3 hours

### Phase 3: Migrate Event Listeners (NEXT)
- [ ] Update PlayerEventListener (join, quit)
- [ ] Update CombatEventListener (damage, death)
- [ ] Update InventoryEventListener (shop, selection)
- [ ] Update BlockEventListener (block tracking)
- [ ] Estimate: 4-6 hours

### Phase 4: Cleanup Unused Fields (LATER)
- [ ] Identify truly dead fields
- [ ] Remove with @Deprecated warnings
- [ ] Document migration path
- [ ] Estimate: 2-3 hours

### Phase 5: Extended Refactoring (v2.0+)
- [ ] Move voting to Lobby
- [ ] Move team assignment to Lobby
- [ ] Create BlockManager for block tracking
- [ ] Remove 100% of collection duplicates

---

## Risk Assessment & Mitigations

| Risk | Impact | Mitigation |
|------|--------|-----------|
| Breaking changes | HIGH | Keep old fields, add new accessors alongside |
| Performance regression | MEDIUM | Use unmodifiable views, no defensive copies |
| Incomplete refactoring | MEDIUM | Each phase has clear acceptance criteria |
| Test failures | MEDIUM | Mockito already failing, won't regress further |

---

## Code Examples

### Before & After: JoinCommand

**BEFORE:**
```java
public class JoinCommand implements ICommand {
    public boolean execute(Player player, String[] args) {
        PotionGames pg = PotionGames.getInstance();
        
        // Add to active players
        if (pg.pgPlayers.contains(player)) {
            player.sendMessage("Already playing!");
            return false;
        }
        pg.pgPlayers.add(player);
        
        // Get lobby
        String lobbyId = args[0];
        if (!pg.lobbySystem) {
            lobbyId = "1";  // Default lobby
        }
        pg.playerLobby.put(player, lobbyId);
        
        // Teleport
        // ... more logic
        return true;
    }
}
```

**AFTER:**
```java
public class JoinCommand implements ICommand {
    public boolean execute(Player player, String[] args) {
        Game game = PotionGames.getInstance().getGame();
        
        // Check if already playing
        if (game.isActivePlayer(player)) {
            player.sendMessage("Already playing!");
            return false;
        }
        
        // Get lobby
        String lobbyId = args[0];
        if (!PotionGames.getInstance().isLobbySystem()) {
            lobbyId = "1";  // Default lobby
        }
        Lobby lobby = game.getLobbyById(Integer.parseInt(lobbyId));
        if (lobby == null) {
            player.sendMessage("Lobby not found!");
            return false;
        }
        
        // Add to game (maintains indices)
        game.addActivePlayer(player);
        game.addPlayerToLobby(player, lobby);
        
        // Teleport
        lobby.join(player);
        return true;
    }
}
```

**Benefits:**
- Clear intent: `isActivePlayer()` vs `pgPlayers.contains()`
- Type-safe: `Lobby` object vs String ID
- Maintainable: Logic in proper class
- Testable: Can mock Game and Lobby independently

---

## Testing Strategy

### Unit Tests
- Test Game.addActivePlayer() with multiple players
- Test Game.addSpectator() transitions
- Test Lobby.addTeamMember() with team constraints
- Test BlockTracker.restore() cleanup

### Integration Tests
- Player join → Game tracks → Event fires
- Player leave → Game untracks → Cleanup
- Lobby end → BlockTracker restores blocks

### Regression Tests
- All existing commands work as before
- All existing event handlers work as before
- Configuration loading unchanged
- Database operations unchanged

---

## Rollback Plan

If issues arise:
1. Keep old HashMap fields in place indefinitely
2. New accessors delegate to old fields
3. No data migration needed
4. Easy to toggle between old and new code

---

## Success Criteria

- [x] Phase 1: Game.java enhanced, JAR builds
- [ ] Phase 2: All commands migrated, tests pass
- [ ] Phase 3: All listeners migrated, tests pass
- [ ] Phase 4: Dead fields identified and removed
- [ ] Final: PotionGames.java reduced from 5,100+ to <1,000 lines
- [ ] Final: Zero duplication between classes and plugin class

---

## Timeline Estimate

- **Phase 1:** 1 hour (DONE)
- **Phase 2:** 2-3 hours
- **Phase 3:** 4-6 hours
- **Phase 4:** 1-2 hours
- **Total:** 8-12 hours spread over multiple sessions

---

## Next Steps

1. **Review this strategy** - Confirm approach with user
2. **Start Phase 2** - Migrate commands one-by-one
3. **Test thoroughly** - Each command migrated
4. **Document changes** - Keep migration guide up-to-date
5. **Collect metrics** - Measure code quality improvements

---

## Appendix: Field Classification

### To Migrate to Game (10 fields)
- pgPlayers → activePlayers ✅
- specPlayers → spectators ✅
- playerLobby → lobbiesByPlayer (already exists)
- specLobby → (to be implemented)

### To Migrate to Lobby (30+ fields)
- lobbyteamplayernames → teams mapping
- lobbyvoteplayernames → votes mapping
- lobbyteamSize, lobbyteamAmount → config
- lobbyPlacedBlocks, lobbyBreakedBlocks → BlockTracker

### To Migrate to ConfigurationManager (15 fields)
- countdown, reset, teamSize, maxPlayers → ✅ Already done
- roundTime, activePotions, activeKits → ✅ Already done
- language, lobbySystem, gameServer → ✅ Already done

### To Delete (Dead Code)
- richkidPlayers - Never used
- channels, playerChannel - Old architecture
- voted, teams, kits, teamed, kited - Replaced by Participant

### To Keep (Infrastructure)
- info (Scoreboards) - Too complex to migrate now
- chests (Location inventories) - Arena-specific
- coin, bottle (ItemStack) - Config items

---

## Document Version History

| Date | Author | Changes |
|------|--------|---------|
| 2026-04-27 | Copilot | Initial strategy document, Phase 1 complete |

