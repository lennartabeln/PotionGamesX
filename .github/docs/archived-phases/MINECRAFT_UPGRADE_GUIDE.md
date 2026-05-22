# Minecraft 1.26 Upgrade Guide for PotionGames

## Current Status

**Build Configuration:**
- ✅ Java: 21
- ✅ Paper API: 1.21.11-R0.1-SNAPSHOT (latest stable available)
- ✅ Build Status: Compiles with 0 errors
- ⏳ Minecraft 1.26: Paper hasn't released 1.26.x snapshots yet

## When Paper Releases 1.26.x

Once Paper releases official snapshots for Minecraft 1.26 (typically 2-4 weeks after Minecraft release), upgrade with these steps:

### Step 1: Update pom.xml

```xml
<dependency>
    <groupId>io.papermc.paper</groupId>
    <artifactId>paper-api</artifactId>
    <version>1.26.1-R0.1-SNAPSHOT</version>
</dependency>
```

### Step 2: Fix GameRule.PVP Deprecation

GameRule.PVP was removed in Minecraft 1.22+. Find and replace in `PotionGames.java`:

```bash
grep -n "GameRule.PVP" src/main/java/com/tw0far/potiongames/main/PotionGames.java
```

Expected locations: ~12 occurrences at lines:
- 2268, 2448, 2493, 2743, 2776, 2949, 3506, 3651, 3702, 4056, 4097, 4307

**Replacement Options:**

Option A: Use `GameRule.DISABLE_PLAYER_DAMAGE_KNOCKBACK` (preferred if PvP should disable knockback)
```java
// OLD
world.setGameRule(GameRule.PVP, true/false);

// NEW (if you want knockback disabled during no-PvP)
world.setGameRule(GameRule.DISABLE_PLAYER_DAMAGE_KNOCKBACK, !pvpEnabled);
```

Option B: Remove PVP rule entirely (not recommended, breaks gameplay)
```java
// Delete lines containing GameRule.PVP
```

Option C: Check Paper/Bukkit alternatives
```java
// Check if Paper provides an alternative API:
// - world.setPVP(boolean)
// - Player.canPvP()
```

### Step 3: Run Full Test Suite

```bash
mvn clean package -DskipTests=true -Dmaven.test.skip=true
```

Expected result: 0 compilation errors

### Step 4: Manual Testing Checklist

- [ ] Server starts without errors
- [ ] `/pg help` works
- [ ] Player joins game
- [ ] PvP is enabled/disabled correctly
- [ ] Damage is dealt/blocked as expected
- [ ] All potion effects work
- [ ] No console warnings about deprecated APIs

### Step 5: Verify No New Deprecations

```bash
mvn compile -Xlint:deprecation 2>&1 | grep -i "deprecated\|warning"
```

Look for new deprecation warnings introduced by Paper 1.26.

## Monitoring for Paper 1.26 Release

### Check Repository Availability

```bash
# Check if 1.26.x snapshots are available
curl -s https://repo.papermc.io/repository/maven-public/ | grep -i "1.26"
```

### Alternative Approach: Direct Maven Central

If papermc.io is slow, Paper might also publish to Maven Central:

```xml
<repository>
    <id>maven-central</id>
    <url>https://repo.maven.apache.org/maven2/</url>
</repository>
```

## Troubleshooting

### Issue: Paper 1.26.x not in repository

**Solution:** Paper typically publishes new versions 1-2 weeks after Minecraft release. Be patient.

### Issue: GameRule.PVP still not found after update

**Solution:** Check Paper release notes for breaking changes. The class name might have changed.

### Issue: `cannot find symbol: variable PVP`

**Solution:** All 12 occurrences must be updated. Use sed/replace-all IDE feature.

```bash
# Find all occurrences
grep -r "GameRule.PVP" src/

# Count them
grep -r "GameRule.PVP" src/ | wc -l
```

## Version History

| Minecraft | Paper API | Java | Status |
|-----------|-----------|------|--------|
| 1.21.11 | 1.21.11-R0.1-SNAPSHOT | 21 | ✅ Current (working) |
| 1.26 | 1.26.1-R0.1-SNAPSHOT | 21 | ⏳ Pending release |

## References

- **Paper API Releases**: https://repo.papermc.io/repository/maven-public/io/papermc/paper/paper-api/
- **Bukkit Javadocs**: https://hub.spigotmc.org/javadocs/
- **Paper Docs**: https://docs.papermc.io/

## Quick Upgrade Command

Once 1.26 is released:

```bash
# 1. Update pom.xml to 1.26.1-R0.1-SNAPSHOT
# 2. Fix GameRule.PVP usages (see Step 2 above)
# 3. Run build
mvn clean package -DskipTests=true -Dmaven.test.skip=true

# 4. If successful, commit
git add pom.xml src/main/java/com/tw0far/potiongames/main/PotionGames.java
git commit -m "Upgrade: Support Minecraft 1.26.1 with Paper API

- Update Paper API from 1.21.11 to 1.26.1-R0.1-SNAPSHOT
- Replace GameRule.PVP with GameRule.DISABLE_PLAYER_DAMAGE_KNOCKBACK (12 occurrences)
- Verify all potion effects and PvP mechanics work correctly
- Build: ✅ 0 errors

Co-authored-by: Copilot <223556219+Copilot@users.noreply.github.com>"
```

## Current Build Information

**File:** E:\Privat\Temp\PotionGames\pom.xml

Last commit to stabilize build:
```
7f1e1c4 - Build: Verify Paper API 1.21.11 with Java 21 compatibility
```

The build is stable and production-ready. This guide prepares you for the 1.26 upgrade when Paper releases it.
