package com.tw0far.potiongames.handlers;

import org.bukkit.entity.Player;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;

public class SetupHandler implements ISetupHandler {

    private final PotionGames pg;

    public SetupHandler(PotionGames pg) {
        this.pg = pg;
    }

    @Override
    public void enableLobby(Player p) {
        enableLobby(p, 1);
    }

    @Override
    public void enableLobby(Player p, int lobbyId) {
        Lobby lobby = pg.game.getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        // Todo: Check if Lobby setup is complete
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
        boolean success = pg.game.addLobby(lobbyId, p.getLocation());
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
        boolean success = pg.game.removeLobby(lobbyId);
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
        Lobby lobby = pg.game.getLobby(lobbyId);
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
        Lobby lobby = pg.game.getLobby(lobbyId);
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
        Lobby lobby = pg.game.getLobby(lobbyId);
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
        Lobby lobby = pg.game.getLobby(lobbyId);
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
        Lobby lobby = pg.game.getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getSpawns().size();
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
        Lobby lobby = pg.game.getLobby(lobbyId);
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
        Lobby lobby = pg.game.getLobby(lobbyId);
        if (lobby == null) {
            p.sendMessage(Messages.LobbyDoesNotExist());
            return;
        }
        Arena arena = lobby.getArena(arenaName);
        if (arena == null) {
            p.sendMessage(Messages.ArenaDoesNotExist());
            return;
        }
        int spawnId = arena.getDeathmatchSpawns().size();
        boolean success = arena.removeDeathmatchSpawn(spawnId);
        if (success) {
            p.sendMessage(Messages.SpawnRemoved(spawnId, arenaName, lobbyId));
        } else {
            p.sendMessage(Messages.FileSaveFailed());
        }
    }
}
