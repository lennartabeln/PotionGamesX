# Phase 8.3 - Quick Admin Guide: Per-Lobby Configuration

## What Changed?

You can now set **global defaults** in `config.yml` and **override** them for individual lobbies in `arena-data.yml`.

## How It Works

### Example: Global Defaults (config.yml)
```yaml
pg:
  countdown: 60          # All lobbies start with 60s countdown by default
  maxPlayers: 24         # All lobbies allow 24 players by default
  teamSize: 2
  roundTime: 30          # 30-minute rounds by default
  activateTeams: true
  activateShop: true
```

### Example: Per-Lobby Overrides (arena-data.yml)
```yaml
pg:
  lobbies:
    0:  # VIP Lobby - faster, more intense
      settings:
        countdown: 120      # Longer countdown for serious players
        maxPlayers: 32      # More players allowed
        teamSize: 4         # Larger teams
        roundTime: 45       # Longer rounds
      arenas:
        - arena1
        - arena2
    
    1:  # Casual Lobby - no overrides, uses all global defaults
      arenas:
        - arena3
        - arena4
        
    2:  # Quick Matches
      settings:
        countdown: 30       # Very short countdown
        roundTime: 15       # Quick 15-minute rounds
        activateTeams: false  # No teams - free-for-all
      arenas:
        - arena5
```

## Result

- **Lobby 0** (VIP): Uses overrides (120s countdown, 32 players, teams of 4, 45-min rounds)
- **Lobby 1** (Casual): Uses global defaults (60s, 24 players, teams of 2, 30-min rounds)
- **Lobby 2** (Quick): Uses overrides + inherits unspecified values from global

## Configuration Keys You Can Override

Per-lobby in `arena-data.yml`:
- `countdown` - Countdown timer before game starts (seconds)
- `maxPlayers` - Maximum players allowed
- `minPlayers` - Minimum players to start game
- `teamSize` - Size of each team
- `roundTime` - Game duration (seconds)
- `activateTeams` - Enable/disable teams
- `activateKits` - Enable/disable kit selection
- `activateShop` - Enable/disable shop
- `activateAirdrops` - Enable/disable airdrops

## How to Set Up

1. **Edit `config.yml`** - Set your global defaults:
   ```yaml
   pg:
     countdown: 60
     maxPlayers: 24
     # ... etc
   ```

2. **Create lobbies** - In `arena-data.yml`, lobbies without settings inherit all defaults

3. **Override specific lobbies** - Add `settings:` section to override just what you need

4. **Restart or reload** - Use `/pg reload` to load new configuration

## Tips

✅ **Do this**:
- Set reasonable global defaults in `config.yml`
- Override only what differs per-lobby in `arena-data.yml`
- Use defaults for most lobbies, special configs for VIP lobbies

❌ **Don't do this**:
- Don't duplicate all settings in every lobby (defeats the purpose)
- Don't set values that contradict each other (e.g., maxPlayers < minPlayers)

## Troubleshooting

**Lobby not using override?**
- Check `arena-data.yml` syntax (YAML indentation matters!)
- Verify lobby ID matches the number in config
- Reload with `/pg reload`

**Lobby using wrong values?**
- Remember: Only settings in the lobby `settings:` section override globals
- Anything not specified uses the global default

**Forgot what the defaults are?**
- Check `config.yml` `pg:` section at the root level
- Those are your global defaults
