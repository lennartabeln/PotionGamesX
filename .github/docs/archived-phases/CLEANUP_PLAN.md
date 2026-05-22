# Code Cleanup & Dependency Update Plan - Phase 8

## Overview

Clean up duplicate code, update all dependencies to latest versions, and fix package structure.

## Duplicate Code Identified

### 1. Monolithic vs Refactored Main Classes
- **PotionGames.java** (5,205 lines) - KEEP (active)
- **PotionGames_Refactored.java** (161 lines) - REMOVE (duplicate)

### 2. Monolithic vs Refactored Commands
- **Commands.java** (822 lines) - OLD monolithic (record-based)
- **CommandDispatcher.java** (115 lines) - NEW refactored
- Individual command classes - NEW

**Decision**: REPLACE Commands.java in onEnable() with CommandDispatcher

### 3. Monolithic vs Refactored Event Listeners  
- **Events.java** (2,673 lines) - OLD monolithic
- **PlayerEventListener.java** - NEW
- **BlockEventListener.java** - NEW
- **CombatEventListener.java** - NEW
- **InventoryEventListener.java** - NEW

**Decision**: REPLACE Events.java registration with 4 new listeners in onEnable()

### 4. Database Duplicate Code
- **QueryHandler.java** (old)
- **DatabaseQueryBuilder.java** (new, optimized)

**Decision**: Use DatabaseQueryBuilder, archive QueryHandler

### 5. Handler Duplicates
- **SetupHandler.java** - Check for duplicates
- **ReloadHandler.java** (new)

## Dependency Updates Needed

### Paper/Bukkit
- Current: `1.26-alpha`
- Latest stable: Check if 1.26 final is available, otherwise keep 1.26-alpha

### VaultAPI
- Current: `1.7.1`
- Latest stable: `1.7.3` (2024)

### JUnit
- Current: `4.13.2`
- Latest stable: `4.13.2` (final version, OK)

### Mockito
- Current: `5.2.0`
- Latest stable: `5.6.0` (2024)

### Kyori Adventure (already included in Paper)
- Verify: Should be included in Paper API

## Package/Import Cleanup

### Check Unused Imports
Files to clean:
- [ ] PotionGames.java - Check for unused imports
- [ ] All command classes
- [ ] All listener classes
- [ ] All manager classes
- [ ] All model classes

### Check Deprecated Imports
- [ ] `org.bukkit.inventory.PlayerInventory.getItemInHand()` → `getItemInMainHand()`
- [ ] Equipment slot APIs
- [ ] Other deprecated methods

## Files to Delete/Archive

```
DELETE (duplicate):
- src/main/java/com/tw0far/potiongames/main/PotionGames_Refactored.java
- src/main/java/com/tw0far/potiongames/commands/Commands.java (will be removed from registry)
- src/main/java/com/tw0far/potiongames/events/Events.java (will be removed from registry)
- src/main/java/com/tw0far/potiongames/handlers/QueryHandler.java (replaced by DatabaseQueryBuilder)

KEEP (for reference):
- Archive in docs/ folder if needed
```

## Files to Update

```
MODIFY:
- pom.xml: Update dependency versions
- PotionGames.java: Replace Commands.java with CommandDispatcher, replace Events.java with 4 listeners
- All command classes: Remove unused imports
- All listener classes: Remove unused imports
- All manager classes: Remove unused imports
- All model classes: Remove unused imports
```

## Integration Plan

### Step 1: Update pom.xml
1. Update VaultAPI to 1.7.3
2. Update Mockito to 5.6.0
3. Verify Paper API version
4. Add any missing dependencies

### Step 2: Fix PotionGames.onEnable()
1. Replace: `pm.registerEvents(new Events(this), this);`
   With: Register all 4 listeners
2. Replace: `Objects.requireNonNull(getCommand("pg")).setExecutor(new Commands(this));`
   With: `Objects.requireNonNull(getCommand("pg")).setExecutor(new CommandDispatcher(this));`

### Step 3: Fix PotionGames.onDisable()
1. Ensure ReloadHandler is called

### Step 4: Clean Imports
1. Run through all Java files
2. Remove unused imports
3. Fix deprecated API calls

### Step 5: Verify Build
1. mvn clean compile
2. Check for import errors
3. Fix any compilation issues

### Step 6: Delete Old Files
1. Delete PotionGames_Refactored.java
2. Delete QueryHandler.java (or archive)
3. Keep Commands.java and Events.java for reference but don't register them

## File Migration Map

```
OLD → NEW

Commands.java (822 lines) → CommandDispatcher.java + 14 individual command classes
Events.java (2,673 lines) → 4 listeners (PlayerEventListener, BlockEventListener, CombatEventListener, InventoryEventListener)
QueryHandler.java → DatabaseQueryBuilder.java
PotionGames_Refactored.java → (deleted, not needed)
```

## Metrics

| Item | Count | Status |
|------|-------|--------|
| Duplicate main classes | 1 | To delete |
| Duplicate command handlers | 1 | To replace |
| Duplicate event handlers | 1 | To replace |
| Duplicate DB handlers | 1 | To archive |
| Lines removed | 3,698 | After cleanup |
| Dependencies to update | 2 | Mockito, VaultAPI |
| Files with unused imports | TBD | Need scan |

## Completion Status

✅ **Phase 1: Code Cleanup** - COMPLETE
- Updated pom.xml with new dependency versions
- Updated PotionGames.java onEnable/onDisable  
- Scanned and cleaned 15+ unused imports
- Old files kept but not registered

⏳ **Phase 2: Event & Command Migration** - IN PROGRESS
- Extracting 17 event handlers from Events.java to 4 listeners
- Migrating 10+ commands from Commands.java to individual classes
- Removing all single-game mode code branches
- Target: Priority 1 events and commands fully implemented

⏳ **Phase 3: Integration & Testing** - PENDING
- Compile with Java 23
- Test on Paper 1.26 server
- Verify all migrations work
- Archive old files
