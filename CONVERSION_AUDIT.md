# Event & Command Conversion Audit

## Current State Analysis

### Listeners Status (NEW - Skeleton implementations)

| Listener | Lines | Implementation | Status |
|----------|-------|---|---|
| PlayerEventListener | 50 | Stubs (TODO) | ❌ Not Complete |
| BlockEventListener | 51 | Stubs (TODO) | ❌ Not Complete |
| CombatEventListener | 35 | Stubs (TODO) | ❌ Not Complete |
| InventoryEventListener | 35 | Stubs (TODO) | ❌ Not Complete |
| **Total** | **171** | | ❌ 0% Complete |

### Commands Status (NEW - Skeleton implementations)

| Command | Lines | Implementation | Status |
|---------|-------|---|---|
| JoinCommand | 55 | Stubs (TODO) | ❌ Not Complete |
| LeaveCommand | 45 | Stubs (TODO) | ❌ Not Complete |
| SetupCommand | 43 | Stubs (TODO) | ❌ Not Complete |
| BuildCommand | 52 | Stubs (TODO) | ❌ Not Complete |
| StartCommand | 49 | Stubs (TODO) | ❌ Not Complete |
| PauseCommand | 43 | Stubs (TODO) | ❌ Not Complete |
| ForceCommand | 43 | Stubs (TODO) | ❌ Not Complete |
| StatsCommand | 57 | Stubs (TODO) | ❌ Not Complete |
| HelpCommand | 41 | Stubs (TODO) | ❌ Not Complete |
| VersionCommand | 52 | Stubs (TODO) | ❌ Not Complete |
| ReloadCommand | 67 | **PARTIAL** (Uses ReloadHandler) | ⚠️ 50% Complete |
| **Total** | **547** | | ❌ 9% Complete (ReloadCommand only) |

### Old Code Status (Monolithic - Fully implemented)

| Class | Lines | Events | Commands | Status |
|-------|-------|--------|----------|--------|
| Events.java | 2,673 | **All 40+ events** | - | ✅ Complete |
| Commands.java | 822 | - | **All 10 subcommands** | ✅ Complete |
| **Total** | **3,495** | | | ✅ 100% Complete |

## What Needs to be Done

### Task 1: Complete All Event Listeners

**Source**: Events.java (2,673 lines, 40+ event handlers)

**Target**: 4 listener classes

**Events to migrate**:

#### PlayerEventListener (join, quit, move)
```
- PlayerJoinEvent (onJoin, setup player state, DB insert, startOnJoin logic)
- PlayerQuitEvent (onLeave, cleanup, inventory restore, DB update)
- PlayerMoveEvent (movement restrictions, invisible borders, compass features)
- PlayerItemHeldEvent (compass click handling)
- PlayerInteractEvent (compass functionality, sign clicks)
- PlayerChatEvent (async chat, team prefixes)
```

#### BlockEventListener (build/destroy)
```
- BlockPlaceEvent (track placed blocks, prevent building outside arenas)
- BlockBreakEvent (track broken blocks, prevent break outside arenas)
- BlockFadeEvent (prevent fire spread, ice melt)
- BlockSpreadEvent (prevent water/lava spread)
- BlockFormEvent (prevent snow/ice formation)
```

#### CombatEventListener (PvP/death)
```
- EntityDamageEvent (damage blocking, invincibility, deathmatch rules)
- EntityDamageByEntityEvent (player damage, kill tracking, rewards)
- PlayerDeathEvent (death handling, drops, spectator mode, stats)
- EntityExplodeEvent (TNT/creeper explosions, block tracking)
```

#### InventoryEventListener (shops/items)
```
- InventoryClickEvent (shop GUI, item purchases, team selection, kit selection)
- InventoryDragEvent (prevent item dragging in GUIs)
- PrepareItemCraftEvent (prevent crafting)
- InventoryOpenEvent (shop opening, loot chest access)
```

### Task 2: Complete All Commands

**Source**: Commands.java (822 lines, 10+ subcommands)

**Target**: 14 individual command classes

**Commands to migrate**:

```
✅ ReloadCommand          - DONE (uses ReloadHandler)
❌ JoinCommand            - TO DO (lobby join logic)
❌ LeaveCommand           - TO DO (lobby leave logic)
❌ SetupCommand           - TO DO (arena setup flow)
❌ BuildCommand           - TO DO (toggle build mode)
❌ StartCommand           - TO DO (start game countdown)
❌ PauseCommand           - TO DO (pause/resume game)
❌ ForceCommand           - TO DO (force start game)
❌ StatsCommand           - TO DO (show player stats from DB)
❌ HelpCommand            - TO DO (show commands list)
❌ VersionCommand         - TO DO (show plugin version)
+ AddarenaCommand         - NEW (add arena)
+ RemovearenaCommand      - NEW (remove arena)
+ AddspawnCommand         - NEW (add spawn point)
```

### Task 3: Remove Single-Game Mode Legacy Code

**Files with single-game branches**: PotionGames.java (5,200+ lines)

**Conditional removals**:

```java
// Use lobby-based flow only:
// - route through lobby IDs
// - keep lobby-owned state
// - remove legacy global state
```

**Methods/fields to remove**:

```
REMOVE:
- single-game specific logic in onJoin(), onLeave()
- legacy global lobby management code
- all mode-based branches

KEEP:
- Multi-lobby system (all logic)
- Lobby management
- Arena management
- Team/Kit per-lobby
```

## Implementation Strategy

### Phase 1: Event Migration (High Priority)
1. Analyze Events.java event by event
2. Extract logic into appropriate listener
3. Test each listener
4. Deprecate Events.java

### Phase 2: Command Migration (High Priority)
1. Analyze Commands.java command by command
2. Extract logic into individual command class
3. Test each command through CommandDispatcher
4. Deprecate Commands.java

### Phase 3: Legacy Code Removal (Medium Priority)
1. Identify all single-game specific code
2. Remove conditional branches
3. Simplify to multi-lobby only
4. Test all flows

### Phase 4: Optimization (Low Priority)
1. Remove duplicate code between old and new
2. Consolidate repeated logic
3. Optimize hot paths
4. Profile performance

## Metrics Summary

### Completion Status

| Area | Implemented | Total | % | Status |
|------|---|---|---|---|
| Events | 0 | 40+ | 0% | ❌ Skeleton |
| Commands | 1 | 11 | 9% | ⚠️ Skeleton |
| Legacy Removal | 0 | 100+ branches | 0% | ❌ Not Started |
| **Overall** | | | **3%** | ❌ Early Stage |

### Work Remaining

```
Events:        2,673 lines (40+ handlers) → Migrate to 4 listeners
Commands:      822 lines (10+ handlers) → Migrate to 14 classes  
Legacy code:   100+ conditional branches → Remove single-game mode
Total LOC:     3,698+ lines to process

Estimated work:
- Full event migration: ~2,000 lines of logic extraction
- Full command migration: ~700 lines of logic extraction
- Legacy removal: ~500 lines of deletion
- Testing & optimization: Variable
```

## Recommendation

**Current state**: Early stage skeleton architecture created, but NOT functionally complete.

**What works**:
- ✅ Class structure in place
- ✅ CommandDispatcher routing works
- ✅ Listener registration works
- ✅ ReloadCommand migrated (as example)

**What doesn't work**:
- ❌ Only ReloadCommand functional (9% of commands)
- ❌ Zero listener implementations (0% of events)
- ❌ Plugin still depends entirely on old Events.java and Commands.java
- ❌ All legacy single-game code still present

**Next action**: Migrate all events and commands from old classes to new ones, starting with highest-impact handlers (join/quit, damage/death, shop, start/pause).
