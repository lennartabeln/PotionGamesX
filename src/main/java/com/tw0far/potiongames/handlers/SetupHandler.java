package com.tw0far.potiongames.handlers;

import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;

public class SetupHandler implements ISetupHandler {

    private final PotionGames pg;

    public SetupHandler(PotionGames pg) {
        this.pg = pg;
    }

    @Override
    public void setup(Player p) {
        PlayerInventory inventory = p.getInventory();
        pg.addSetupPlayer(p);
        pg.savePlayerInventory(p, inventory.getContents());
        pg.savePlayerArmor(p, inventory.getArmorContents());
        pg.savePlayerLevel(p, p.getLevel());
        pg.savePlayerExp(p, p.getExp());
        pg.savePlayerLocation(p, p.getLocation());
        pg.savePlayerGameMode(p, p.getGameMode());

        inventory.clear();
        inventory.setHelmet(null);
        inventory.setChestplate(null);
        inventory.setLeggings(null);
        inventory.setBoots(null);

        p.setHealth(20);
        p.setFoodLevel(20);
        p.setLevel(0);
        p.setExp(0);
        p.setGameMode(GameMode.ADVENTURE);
        pg.clearEffects(p);
        p.setFireTicks(0);
        p.setAllowFlight(true);
        p.setFlying(true);

        ItemStack addlobby = new ItemStack(Material.STICK);
        ItemMeta addlobbymeta = addlobby.getItemMeta();
        if (addlobbymeta != null) {
            addlobbymeta.displayName(Component.text("Add(Left)/Del(Right) Lobby").color(NamedTextColor.DARK_AQUA));
            addlobby.setItemMeta(addlobbymeta);
            inventory.setItem(1, addlobby);
        }

        ItemStack chooselobby = new ItemStack(Material.CLOCK);
        ItemMeta chooselobbymeta = chooselobby.getItemMeta();
        if (chooselobbymeta != null) {
            chooselobbymeta.displayName(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA));
            chooselobby.setItemMeta(chooselobbymeta);
            inventory.setItem(2, chooselobby);
        }

        ItemStack addarena = new ItemStack(Material.STICK);
        ItemMeta addarenameta = addarena.getItemMeta();
        if (addarenameta != null) {
            addarenameta.displayName(Component.text("Add(Left)/Del(Right) Arena").color(NamedTextColor.DARK_AQUA));
            addarena.setItemMeta(addarenameta);
            inventory.setItem(3, addarena);
        }

        ItemStack choosearena = new ItemStack(Material.CLOCK);
        ItemMeta choosearenameta = choosearena.getItemMeta();
        if (choosearenameta != null) {
            choosearenameta.displayName(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA));
            choosearena.setItemMeta(choosearenameta);
            inventory.setItem(4, choosearena);
        }

        ItemStack addspawn = new ItemStack(Material.STICK);
        ItemMeta addspawnmeta = addspawn.getItemMeta();
        if (addspawnmeta != null) {
            addspawnmeta.displayName(Component.text("Add(Left)/Del(Right) Spawn").color(NamedTextColor.DARK_AQUA));
            addspawn.setItemMeta(addspawnmeta);
            inventory.setItem(5, addspawn);
        }

        ItemStack signsetup = new ItemStack(Material.OAK_SIGN);
        ItemMeta signsetupmeta = signsetup.getItemMeta();
        if (signsetupmeta != null) {
            signsetupmeta.displayName(Component.text("Set Join-Sign").color(NamedTextColor.DARK_AQUA));
            signsetup.setItemMeta(signsetupmeta);
            inventory.setItem(6, signsetup);
        }

        ItemStack leavesetup = new ItemStack(Material.BARRIER);
        ItemMeta leavesetupmeta = leavesetup.getItemMeta();
        if (leavesetupmeta != null) {
            leavesetupmeta.displayName(Component.text("Leave Setup-Mode").color(NamedTextColor.DARK_AQUA));
            leavesetup.setItemMeta(leavesetupmeta);
            inventory.setItem(7, leavesetup);
        }
    }

    @Override
    public void enableLobby(Player p) {
        enableLobby(p, 1);
    }

    @Override
    public void enableLobby(Player p, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        if (lobby.getArenas().isEmpty() || lobby.getArenas().stream().noneMatch(arena -> !arena.getSpawns().isEmpty())) {
            p.sendMessage(Settings.prefix.append(Component.text("Lobby setup is incomplete: add at least one arena with one spawn before enabling the lobby.").color(NamedTextColor.RED)));
            return;
        }
        boolean success = lobby.enable();
        if (success) {
            if (lobby.isEnabled()) {
                p.sendMessage(Messages.LobbyEnabled());
            } else {
                p.sendMessage(Messages.LobbyDisabled());
            }
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void addLobby(Player p) {
        addLobby(p, 1);
    }

    @Override
    public void addLobby(Player p, int lobbyId) {
        boolean success = pg.getGame().addLobby(lobbyId, p.getLocation());
        if (success) {
            p.sendMessage(Messages.LobbyAdded(lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void removeLobby(Player p) {
        removeLobby(p, 1);
    }

    @Override
    public void removeLobby(Player p, int lobbyId) {
        boolean success = pg.getGame().removeLobby(lobbyId);
        if (success) {
            p.sendMessage(Messages.LobbyRemoved(lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void setJoinSign(Player p) {
        setJoinSign(p, 1);
    }

    @Override
    public void setJoinSign(Player p, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        boolean success = lobby.setJoinSign(p.getTargetBlock(null, 5).getLocation());
        if (success) {
            p.sendMessage(Messages.SignSet());
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void addArena(Player p, String arenaName) {
        addArena(p, arenaName, 1);
    }

    @Override
    public void addArena(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        boolean success = lobby.addArena(arenaName);
        if (success) {
            p.sendMessage(Messages.ArenaAdded(arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void removeArena(Player p, String arenaName) {
        removeArena(p, arenaName, 1);
    }

    @Override
    public void removeArena(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        boolean success = lobby.removeArena(arenaName);
        if (success) {
            p.sendMessage(Messages.ArenaRemoved(arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void addSpawn(Player p, String arenaName) {
        addSpawn(p, arenaName, 1);
    }

    @Override
    public void addSpawn(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getSpawns().size() + 1;
        boolean success = arena.addSpawn(spawnId, p.getLocation());
        if (success) {
            p.sendMessage(Messages.SpawnAdded(spawnId, arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void removeSpawn(Player p, String arenaName) {
        removeSpawn(p, arenaName, 1);
    }

    @Override
    public void removeSpawn(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getSpawns().size() - 1;
        if (spawnId < 0) {
            p.sendMessage(Settings.prefix.append(Component.text("No spawns are configured for that arena.").color(NamedTextColor.RED)));
            return;
        }
        boolean success = arena.removeSpawn(spawnId);
        if (success) {
            p.sendMessage(Messages.SpawnRemoved(spawnId, arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void addDeathmatchSpawn(Player p, String arenaName) {
        addDeathmatchSpawn(p, arenaName, 1);
    }

    @Override
    public void addDeathmatchSpawn(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getDeathmatchSpawns().size() + 1;
        boolean success = arena.addDeathmatchSpawn(spawnId, p.getLocation());
        if (success) {
            p.sendMessage(Messages.SpawnAdded(spawnId, arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }

    @Override
    public void removeDeathmatchSpawn(Player p, String arenaName) {
        removeDeathmatchSpawn(p, arenaName, 1);
    }

    @Override
    public void removeDeathmatchSpawn(Player p, String arenaName, int lobbyId) {
        Lobby lobby = pg.getGame().getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getDeathmatchSpawns().size() - 1;
        if (spawnId < 0) {
            p.sendMessage(Settings.prefix.append(Component.text("No deathmatch spawns are configured for that arena.").color(NamedTextColor.RED)));
            return;
        }
        boolean success = arena.removeDeathmatchSpawn(spawnId);
        if (success) {
            p.sendMessage(Messages.SpawnRemoved(spawnId, arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }
}
