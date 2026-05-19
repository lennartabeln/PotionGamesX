package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.util.SafeMapAccess;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Handles inventory and interaction events (multi-lobby only).
 * Extracted from monolithic Events.java.
 */
public class InventoryEventListener implements Listener {
    private static int amount;
    private static int bottle;
    private final PotionGames plugin;
    
    public InventoryEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (plugin.isGameServer()) {
            // Get lobby ID using Game class methods
            String s = plugin.game.getPlayerLobby(p);
            if (s == null) {
                s = plugin.game.getSpectatorLobby(p);
            }
            if (s != null) {
                GameStates lobbyState = plugin.getLobbyGameState(s);
                boolean canBuild = plugin.isLobbyBuildAllowed(s);
                if ((lobbyState == GameStates.WAITING || lobbyState == GameStates.PREPARING) && !canBuild) {
                    handleArenaVoting(e, p, s);
                    handleTeamSelection(e, p, s);
                    handleShop(e, p, s);
                }
            }
            handleLobbySelection(e, p);
        }
    }

    private boolean isNamedItem(Player player, Material material, Component displayName) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != material || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && Objects.equals(meta.displayName(), displayName);
    }

    private Lobby resolveSetupLobby(Player player) {
        Integer selectedLobby = plugin.getSetupStateManager().getSelectedLobby(player);
        if (selectedLobby != null) {
            return plugin.getLobbyById(selectedLobby);
        }

        String activeLobbyId = plugin.game.getPlayerLobby(player);
        if (activeLobbyId != null) {
            try {
                return plugin.getLobbyById(Integer.parseInt(activeLobbyId));
            } catch (NumberFormatException ignored) {
            }
        }

        return null;
    }

    private Arena resolveSetupArena(Player player, Lobby lobby) {
        if (lobby == null) {
            return null;
        }

        String selectedArena = plugin.getSetupStateManager().getSelectedArena(player);
        if (selectedArena != null) {
            Arena arena = lobby.getArena(selectedArena);
            if (arena != null) {
                return arena;
            }
        }

        return lobby.getCurrentArena();
    }

    private void openChooseArenaInventory(Player player, Lobby lobby) {
        if (lobby == null) {
            player.sendMessage(Settings.prefix.append(Component.text("Choose a lobby first!").color(NamedTextColor.YELLOW)));
            return;
        }

        List<Arena> arenas = lobby.getArenas();
        Inventory inv = Bukkit.createInventory(null, 9 * 3,
                Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)));

        int slot = 0;
        for (Arena arena : arenas) {
            if (slot >= inv.getSize()) {
                break;
            }
            ItemStack item = new ItemStack(Material.MAP);
            ItemMeta meta = item.getItemMeta();
            assert meta != null;
            meta.displayName(Component.text(arena.getName()).color(NamedTextColor.AQUA));
            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    private boolean handleSetupSpawnAction(Player player, boolean addSpawn) {
        if (!player.hasPermission("pg.setup")) {
            player.sendMessage(Settings.prefix.append(Component.text("You don't have permission to use this!").color(NamedTextColor.RED)));
            return true;
        }

        Lobby lobby = resolveSetupLobby(player);
        Arena arena = resolveSetupArena(player, lobby);
        if (lobby == null) {
            player.sendMessage(Settings.prefix.append(Component.text("Choose a lobby first!").color(NamedTextColor.YELLOW)));
            return true;
        }
        if (arena == null) {
            player.sendMessage(Settings.prefix.append(Component.text("Choose an arena first!").color(NamedTextColor.YELLOW)));
            return true;
        }

        if (addSpawn) {
            plugin.setupHandler.addSpawn(player, arena.getName(), lobby.getId());
        } else {
            if (arena.getSpawns().isEmpty()) {
                player.sendMessage(Settings.prefix.append(Component.text("No spawns to remove!").color(NamedTextColor.YELLOW)));
                return true;
            }
            plugin.setupHandler.removeSpawn(player, arena.getName(), lobby.getId());
        }
        return true;
    }
    
    private void handleArenaVoting(InventoryClickEvent e, Player p, String s) {
        if (e.getView().title().equals(Messages.ArenaSelector())) {
            if (e.getCurrentItem() != null) {
                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                    String displayname = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                    
                    // Check if player has already voted using delegation
                    if (!plugin.hasPlayerVotedInLobby(s, p)) {
                        // First time voting - add vote
                        p.closeInventory();
                        plugin.addLobbyVote(s, displayname);
                        plugin.recordPlayerVoteInLobby(s, p, displayname);
                    } else {
                        // Switching vote - remove old vote and add new
                        p.closeInventory();
                        String previousVote = plugin.getPlayerVoteInLobby(s, p);
                        if (previousVote != null) {
                            plugin.removeLobbyVote(s, previousVote);
                        }
                        plugin.addLobbyVote(s, displayname);
                        plugin.recordPlayerVoteInLobby(s, p, displayname);
                    }
                    
                    // Send feedback messages
                    p.sendMessage(Messages.ArenaSelector());
                    p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(16) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                    p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(15) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getLobbyVoteCount(s, displayname))).color(NamedTextColor.AQUA)));
                    p.sendMessage(Messages.ArenaSelector());
                }
            }
        }
    }
    
    private void handleTeamSelection(InventoryClickEvent e, Player p, String s) {
        if (plugin.isActivateTeams(s)) {
            if (e.getView().title().equals(Settings.prefix.append(Component.text(plugin.getChatmessages().get(43)).color(NamedTextColor.DARK_AQUA)))) {
                if (e.getCurrentItem() != null) {
                    if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                        String displayname = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
                        int maxteamplayers = plugin.getLobbyTeamSize(s);
                        
                        // Check if player already has a team using delegation
                        if (!plugin.hasPlayerTeamInLobby(s, p)) {
                            // First time assigning team
                            if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
                                assignRandomTeam(e, p, s, maxteamplayers);
                            } else {
                                assignSpecificTeam(e, p, s, displayname, maxteamplayers);
                            }
                        } else {
                            // Switching teams
                            switchTeam(e, p, s, displayname, maxteamplayers);
                        }
                    }
                }
            }
        }
    }
    
    private void assignRandomTeam(InventoryClickEvent e, Player p, String s, int maxteamplayers) {
        boolean teamfound = false;
        Map<Integer, Integer> lobbyTeamsMap = plugin.getLobbyTeams(s);
        
        if (lobbyTeamsMap == null || lobbyTeamsMap.isEmpty()) {
            return;
        }
        
        while (!teamfound) {
            Random rnd = new Random();
            int rndTeam = rnd.nextInt(lobbyTeamsMap.size()) + 1;
            Integer teamPlayers = plugin.getLobbyTeamPlayerCount(s, rndTeam);
            
            if (teamPlayers != null && teamPlayers < maxteamplayers) {
                teamfound = true;
                p.closeInventory();
                plugin.incrementLobbyTeamPlayers(s, rndTeam);
                plugin.recordPlayerTeamInLobby(s, p, Integer.toString(rndTeam));
                
                // Send feedback
                p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.getChatmessages().get(43) + "--------------").color(NamedTextColor.GRAY)));
                p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(rndTeam).color(NamedTextColor.LIGHT_PURPLE)));
                p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getLobbyTeamPlayerCount(s, rndTeam))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.getChatmessages().get(43) + "--------------").color(NamedTextColor.GRAY)));
                
                if (plugin.isActivateScoreboard()) {
                    Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(Integer.toString(rndTeam)).color(NamedTextColor.DARK_AQUA));
                }
            }
        }
    }
    
    private void assignSpecificTeam(InventoryClickEvent e, Player p, String s, String displayname, int maxteamplayers) {
        int teamId = Integer.parseInt(displayname);
        Integer currentPlayers = plugin.getLobbyTeamPlayerCount(s, teamId);
        
        if (currentPlayers != null && currentPlayers < maxteamplayers) {
            p.closeInventory();
            plugin.incrementLobbyTeamPlayers(s, teamId);
            plugin.recordPlayerTeamInLobby(s, p, displayname);
            
            // Send feedback
            p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.getChatmessages().get(43) + "--------------").color(NamedTextColor.GRAY)));
            p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
            p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getLobbyTeamPlayerCount(s, teamId))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
            p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.getChatmessages().get(43) + "--------------").color(NamedTextColor.GRAY)));
            
            if (plugin.isActivateScoreboard()) {
                Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
            }
        } else {
            p.closeInventory();
            p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.getChatmessages().get(43) + "--------------").color(NamedTextColor.GRAY)));
            p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(47)).color(NamedTextColor.RED)));
            p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.getChatmessages().get(43) + "--------------").color(NamedTextColor.GRAY)));
        }
    }
    
    private void switchTeam(InventoryClickEvent e, Player p, String s, String displayname, int maxteamplayers) {
        p.closeInventory();
        
        // Get player's current team and remove from it
        String previousTeam = plugin.getPlayerTeamInLobby(s, p);
        if (previousTeam != null) {
            plugin.decrementLobbyTeamPlayers(s, Integer.parseInt(previousTeam));
            plugin.removePlayerTeamInLobby(s, p);
        }
        
        // Assign to new team
        if (e.getCurrentItem().getItemMeta().displayName().equals(Messages.RandomLabel())) {
            assignRandomTeam(e, p, s, maxteamplayers);
        } else {
            assignSpecificTeam(e, p, s, displayname, maxteamplayers);
        }
    }
    
    private void handleShop(InventoryClickEvent e, Player p, String s) {
        if (e.getView().title().equals(Settings.prefix.append(Component.text(plugin.getChatmessages().get(49)).color(NamedTextColor.DARK_AQUA)))) {
            if (e.getCurrentItem() != null) {
                amount = (int) (p.getTotalExperience() * 10);
                bottle = 0;
                for (ItemStack item : p.getInventory().getContents()) {
                    if (item != null && item.equals(plugin.getBottle())) {
                        bottle += item.getAmount();
                    }
                }
                int shopitem = 1;
                // Get shop data from Game via delegation
                ArrayList<String> shopItems = plugin.getGameShopItems();
                ArrayList<String> shopKits = plugin.getGameShopKits();
                ArrayList<Integer> shopCosts = plugin.getGameShopCosts();
                ArrayList<Integer> shopSales = plugin.getGameShopSales();
                
                for (int i = 0; i < shopItems.size(); i++) {
                    int coinamount;
                    if (plugin.hasPlayerKit(p) && plugin.getPlayerKit(p) != null && plugin.getPlayerKit(p).equals(shopKits.get(shopitem - 1))) {
                        coinamount = shopSales.get(shopitem - 1);
                    } else {
                        coinamount = shopCosts.get(shopitem - 1);
                    }
                    if (Objects.requireNonNull(PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName())).matches(shopItems.get(shopitem - 1))) {
                        if (bottle >= 1) {
                            if (amount >= coinamount) {
                                amount = amount - coinamount;
                                bottle = bottle - 1;
                                        ItemStack randombarrier = new ItemStack(plugin.getShoppotiontype().get(shopitem - 1));
                                PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                        randombarriermeta.addCustomEffect(new PotionEffect(plugin.getShoppotion().get(shopitem - 1).getType(), plugin.getShoppotion().get(shopitem - 1).getDuration(), plugin.getShoppotion().get(shopitem - 1).getAmplifier()), true);
                                randombarriermeta.displayName(Component.text(shopItems.get(shopitem - 1)));
                                randombarrier.setItemMeta(randombarriermeta);
                                p.getInventory().addItem(randombarrier);
                                for (int k = 0; k < coinamount; k++) {
                                    p.getInventory().removeItem(plugin.getCoin());
                                }
                                for (int k = 0; k < 1; k++) {
                                    p.getInventory().removeItem(plugin.getBottle());
                                }
                            } else {
                    p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(53)).color(NamedTextColor.RED)));
                            }
                        } else {
                    p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(54)).color(NamedTextColor.RED)));
                        }
                    }
                    shopitem++;
                }
            }
            e.setCancelled(true);
        }
    }
    
    private void handleLobbySelection(InventoryClickEvent e, Player p) {
        if (e.getView().title().equals(Settings.prefix.append(Component.text("Lobby List").color(NamedTextColor.DARK_AQUA)))) {
            if (e.getCurrentItem() != null) {
                p.closeInventory();
                plugin.onJoinLobby(p, PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).displayName()));
            }
            e.setCancelled(true);
        }
        if (e.getView().title().equals(Settings.prefix.append(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA)))) {
            if (e.getCurrentItem() != null) {
                p.closeInventory();
                String lobbyName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).displayName());
                try {
                    int lobbyId = Integer.parseInt(lobbyName);
                    Lobby lobby = plugin.getLobbyById(lobbyId);
                    if (lobby != null) {
                        plugin.getSetupStateManager().setSelectedLobby(p, lobbyId);
                        plugin.getSetupStateManager().removeSelectedArena(p);
                        p.sendMessage(Settings.prefix.append(Component.text("Lobby " + lobbyId + " selected!").color(NamedTextColor.GREEN)));
                        openChooseArenaInventory(p, lobby);
                    } else {
                        p.sendMessage(Settings.prefix.append(Component.text("This lobby does not exists!").color(NamedTextColor.RED)));
                    }
                } catch (NumberFormatException ex) {
                    p.sendMessage(Settings.prefix.append(Component.text("Invalid lobby selection!").color(NamedTextColor.RED)));
                }
            }
            e.setCancelled(true);
        }
        if (e.getView().title().equals(Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)))) {
            if (e.getCurrentItem() != null) {
                p.closeInventory();
                String arenaName = PlainTextComponentSerializer.plainText().serialize(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).displayName());
                Lobby lobby = resolveSetupLobby(p);
                if (lobby == null) {
                    p.sendMessage(Settings.prefix.append(Component.text("Choose a lobby first!").color(NamedTextColor.YELLOW)));
                } else {
                    Arena arena = lobby.getArena(arenaName);
                    if (arena != null) {
                        plugin.getSetupStateManager().setSelectedArena(p, arenaName);
                        p.sendMessage(Settings.prefix.append(Component.text("Arena " + arenaName + " selected!").color(NamedTextColor.GREEN)));
                    } else {
                        p.sendMessage(Settings.prefix.append(Component.text("This arena does not exists!").color(NamedTextColor.RED)));
                    }
                }
            }
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (plugin.isGameServer()) {
            if (plugin.game.isActivePlayer(p) || plugin.game.isInLobby(p)) {
                if (e.getAction() == Action.PHYSICAL && Objects.requireNonNull(e.getClickedBlock()).getType() == Material.FARMLAND) {
                    e.setCancelled(true);
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (e.getHand() == EquipmentSlot.HAND) {
                        if ((Objects.requireNonNull(e.getClickedBlock())).getType().toString().equals(Objects.requireNonNull(Settings.chestdata.get("pg.chestblocks.normal")).toString())) {
                            
                                String s = plugin.game.getPlayerLobby(p);
                                if (s != null && plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                    if (!plugin.hasLobbyChest(s, e.getClickedBlock().getLocation())) {
                                        Inventory inv;
                                        inv = Bukkit.createInventory(p, 27, Settings.prefix);
                                        plugin.chestData();
                                        Random rnd = new Random();
                                        int max = 6;
                                        int min = 2;
                                        int diff = max - min;
                                        int tries = rnd.nextInt(diff + 1);
                                        tries += min;
                                        while (tries != 0) {
                                            tries--;
                                            int slot = rnd.nextInt(27);
                                            int roll = rnd.nextInt(100);
                                            if (roll < 20) {
                                                if (plugin.isLobbyActivateShop(s)) {
                                                    ArrayList<ItemStack> potions1 = new ArrayList<>();
                                                    potions1.add(new ItemStack(Material.GLASS_BOTTLE, 1));
                                                    ArrayList<ItemStack> potions2 = new ArrayList<>();
                                                    potions2.add(plugin.getCoin());
                                                    int item = rnd.nextInt(5);
                                                    if (item < 3) {
                                                        int item1 = rnd.nextInt(plugin.getFoodTier1().size());
                                                        inv.setItem(slot, plugin.getFoodTier1().get(item1));
                                                    } else if (item < 4) {
                                                        int item1 = 0;
                                                        inv.setItem(slot, potions1.get(item1));
                                                    } else {
                                                        int item1 = 0;
                                                        inv.setItem(slot, potions2.get(item1));
                                                    }
                                                } else {
                                                    int item1 = rnd.nextInt(plugin.getFoodTier1().size());
                                                    inv.setItem(slot, plugin.getFoodTier1().get(item1));
                                                }
                                            } else if (roll < 30) {
                                                int item2 = rnd.nextInt(plugin.getFoodTier2().size());
                                                inv.setItem(slot, plugin.getFoodTier2().get(item2));
                                            } else if (roll < 45) {
                                                int item3 = rnd.nextInt(plugin.getArmourTier1().size());
                                                inv.setItem(slot, plugin.getArmourTier1().get(item3));
                                            } else if (roll < 60) {
                                                int item4 = rnd.nextInt(plugin.getArmourTier2().size());
                                                inv.setItem(slot, plugin.getArmourTier2().get(item4));
                                            } else if (roll < 67) {
                                                int item5 = rnd.nextInt(plugin.getArmourTier3().size());
                                                inv.setItem(slot, plugin.getArmourTier3().get(item5));
                                            } else if (roll < 72) {
                                                int item6 = rnd.nextInt(plugin.getArmourTier4().size());
                                                inv.setItem(slot, plugin.getArmourTier4().get(item6));
                                            } else if (roll < 75) {
                                                int item7 = rnd.nextInt(plugin.getArmourTier5().size());
                                                inv.setItem(slot, plugin.getArmourTier5().get(item7));
                                            } else if (roll < 95) {
                                                int item8 = rnd.nextInt(plugin.getWeaponsTier1().size());
                                                inv.setItem(slot, plugin.getWeaponsTier1().get(item8));
                                            } else {
                                                int item9 = rnd.nextInt(plugin.getWeaponsTier2().size());
                                                inv.setItem(slot, plugin.getWeaponsTier2().get(item9));
                                            }
                                        }
                                        plugin.setLobbyChestInventory(s, e.getClickedBlock().getLocation(), inv);
                                    }
                                    Inventory chestInv = plugin.getLobbyChestInventory(s, e.getClickedBlock().getLocation());
                                    if (chestInv != null) {
                                        p.openInventory(chestInv);
                                    }
                                    if (p.getActivePotionEffects().isEmpty()) {
                                        Random effect = new Random();
                                        int max = 3;
                                        int min = 0;
                                        int diff = max - min;
                                        int tries = effect.nextInt(diff + 1);
                                        while (tries != 0) {
                                            tries--;
                                            int potion = effect.nextInt(plugin.getPotions().size());
                                            p.addPotionEffect(plugin.getPotions().get(potion));
                                        }
                                    }
                                }
                            
                        }
                        int chestnumber = 1;
                        int chestitem = 1;
                        while (Settings.chestdata.contains("pg.customchests." + chestnumber)) {
                            if (e.getClickedBlock().getType().toString().equals(Objects.requireNonNull(Settings.chestdata.get("pg.customchests." + chestnumber + ".chesttype")).toString())) {
                                if (Settings.chestdata.getBoolean("pg.customchests." + chestnumber + ".activate")) {
                                    
                                        String s = null;
                                        for (int ii = 1; ii <= 27; ii++) {
                                            if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                                s = Integer.toString(ii);
                                            }
                                        }
                                        if (plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                            if (!plugin.hasLobbyChest(s, e.getClickedBlock().getLocation())) {
                                                Inventory inv;
                                                inv = Bukkit.createInventory(p, Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + ".chestsize"), Settings.prefix);
                                                plugin.setLobbyChestInventory(s, e.getClickedBlock().getLocation(), inv);
                                                p.openInventory(inv);
                                                while (Settings.chestdata.contains("pg.customchests." + chestnumber + "." + chestitem)) {
                                                    inv.setItem(Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + chestitem + ".slot") - 1, Settings.chestdata.getItemStack("pg.customchests." + chestnumber + "." + chestitem + ".item"));
                                                    chestitem++;
                                                }
                                            } else {
                                                Inventory chestInv = plugin.getLobbyChestInventory(s, e.getClickedBlock().getLocation());
                                                if (chestInv != null) {
                                                    p.openInventory(chestInv);
                                                }
                                            }
                                        }
                                    
                                }
                            }
                            chestnumber++;
                        }
                        if ((e.getClickedBlock()).getType().toString().equals(Objects.requireNonNull(Settings.chestdata.get("pg.chestblocks.shop")).toString())) {
                            
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.isLobbyActivateShop(s)) {
                                    if (plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                        for (ItemStack item : p.getInventory().getContents()) {
                                            if (item != null) {
                                                if (item.getType() == plugin.getCoin().getType()) {
                                                    amount = item.getAmount();
                                                }
                                                if (item.getType() == Material.GLASS_BOTTLE) {
                                                    bottle = item.getAmount();
                                                }
                                            }
                                        }
                                        Inventory inv;
                                        inv = Bukkit.createInventory(p, 9 * 3, Settings.prefix.append(Component.text(plugin.getChatmessages().get(49))).color(NamedTextColor.DARK_AQUA));
                                        plugin.setLobbyChestInventory(s, e.getClickedBlock().getLocation(), inv);
                                        int shopitem = 1;
                                        for (int i = 0; i < plugin.getActivePotions(); i++) {
                                            int coinamount;
                                                if (plugin.hasPlayerKit(p) && plugin.getPlayerKit(p) != null && plugin.getPlayerKit(p).equals(plugin.getShopkit().get(shopitem - 1))) {
                                                    coinamount = plugin.getShopsale().get(shopitem - 1);
                                                } else {
                                                    coinamount = plugin.getShopcost().get(shopitem - 1);
                                                }
                                                ItemStack randombarrier = new ItemStack(plugin.getShoppotiontype().get(shopitem - 1));
                                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                                randombarriermeta.displayName(Component.text(plugin.getShop().get(shopitem - 1)));
                                            ArrayList<Component> lore = new ArrayList<>();
                                            lore.add(Component.text(plugin.getChatmessages().get(50) + ": " + plugin.getShoppotion().get(shopitem - 1).getDuration() / 20));
                                            lore.add(Component.text(plugin.getChatmessages().get(51) + ": " + coinamount + " " + plugin.getChatmessages().get(52)));
                                            randombarriermeta.lore(lore);
                                            randombarrier.setItemMeta(randombarriermeta);
                                            inv.setItem(shopitem - 1, randombarrier);
                                            shopitem++;
                                        }
                                    }
                                    Inventory chestInv = plugin.getLobbyChestInventory(s, e.getClickedBlock().getLocation());
                                    if (chestInv != null) {
                                        p.openInventory(chestInv);
                                    }
                                }
                            
                        }
                        
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (e.getClickedBlock().getBlockData() instanceof Waterlogged) {
                                plugin.trackLobbyWaterBlock(s, e.getClickedBlock().getLocation(), e.getClickedBlock().getBlockData());
                            }
                        
                    }
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (e.getHand() == EquipmentSlot.HAND) {
                        if (p.getInventory().getItemInMainHand().getType() == Material.MUSHROOM_STEW || p.getInventory().getItemInMainHand().getType() == Material.RABBIT_STEW || p.getInventory().getItemInMainHand().getType() == Material.BEETROOT_SOUP) {
                            
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                    double health = p.getHealth();
                                    int foodlvl = p.getFoodLevel();
                                    if (health >= 20 && foodlvl >= 13) {
                                        p.setFoodLevel(20);
                                        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                    } else if (foodlvl < 13) {
                                        p.setFoodLevel(foodlvl + 7);
                                        p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                    } else {
                                        if (health < 20 && health >= 13) {
                                            p.setHealth(20);
                                            p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                        } else if (health < 13) {
                                            p.setHealth(health + 7);
                                            p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                        }
                                    }
                                }
                            
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.REDSTONE_TORCH) {
                            
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.isLobbyActivateAirdrops(s)) {
                                    if (plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                        boolean blocked = false;
                                        Location loc = p.getEyeLocation().add(0, 1, 0);
                                        while (loc.getY() <= 320) {
                                            if (loc.getBlock().getType() != Material.AIR) {
                                                p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(96)).color(NamedTextColor.RED)));
                                                blocked = true;
                                                break;
                                            }
                                            loc.add(0, 1, 0);
                                        }
                                        if (!blocked) {
                                            for (Player all : plugin.getPlayersInLobby(s)) {
                                                all.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(98) + ": ").color(NamedTextColor.GREEN)).append(Component.text(p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ()).color(NamedTextColor.AQUA)));
                                            }
                                            p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(97)).color(NamedTextColor.GREEN)));
                                            BlockData b = Material.DRIED_KELP_BLOCK.createBlockData();
                                            Location spawnLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 100, p.getLocation().getZ());
                                            p.getWorld().spawn(spawnLoc, FallingBlock.class, fallingBlock -> fallingBlock.setBlockData(b));
                                            plugin.addLobbyPlacedBlock(s, p.getLocation(), b.getMaterial());
                                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                                        }
                                    }
                                }
                            
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.MILK_BUCKET) {
                            
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                    plugin.clearEffects(p);
                                    p.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
                                }
                            
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                            
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                    Player result = null;
                                    double lastDistance = Double.MAX_VALUE;
                                    for (Player cp : p.getWorld().getPlayers()) {
                                        if (plugin.game.getPlayerLobby(cp) != null && Objects.equals(plugin.getPlayerTeam(s, p), plugin.getPlayerTeam(s, cp))) {
                                            if (p == cp) {
                                                continue;
                                            }
                                            double distance = p.getLocation().distance(cp.getLocation());
                                            if (distance < lastDistance) {
                                                lastDistance = distance;
                                                result = cp;
                                            }
                                        } else {
                                            result = null;
                                        }
                                    }
                                    if (result != null) {
                                        p.sendActionBar(Settings.prefix.append(Component.text(plugin.getChatmessages().get(12) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf((int) lastDistance)).color(NamedTextColor.AQUA)));
                                    } else {
                                        p.sendActionBar(Settings.prefix.append(Component.text(plugin.getChatmessages().get(13)).color(NamedTextColor.RED))); 
                                    }
                                }
                            
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                        
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (plugin.getLobbyGameState(s) == GameStates.WAITING || plugin.getLobbyGameState(s) == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                ArrayList<Component> randomlore = new ArrayList<>();
                                randomlore.add(0, Component.text(plugin.getChatmessages().get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(SafeMapAccess.get(plugin.getLobbyvotes(), s, "Random", 0))).color(NamedTextColor.AQUA)));
                                randombarriermeta.lore(randomlore);
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ArenaSelectorTitle());
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : plugin.getLobbyvotes().get(s).keySet()) {
                                    if (!all.matches("Random")) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        arenalore.add(0, Component.text(plugin.getChatmessages().get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(SafeMapAccess.get(plugin.getLobbyvotes(), s, all, 0))).color(NamedTextColor.AQUA)));
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(all).color(NamedTextColor.AQUA));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot, arenamap);
                                        slot++;
                                    }
                                }
                                p.openInventory(inv);
                            }
                        
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (plugin.getLobbyGameState(s) == GameStates.WAITING || plugin.getLobbyGameState(s) == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(plugin.getChatmessages().get(43)).color(NamedTextColor.DARK_AQUA)));
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (Integer all : plugin.getLobbiesTeamsMap(s).keySet()) {
                                    ArrayList<Component> arenalore = new ArrayList<>();
                                    arenalore.add(0, Component.text(plugin.getChatmessages().get(44) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(SafeMapAccess.get(plugin.getLobbiesTeamsMap(s), all, 0))).color(NamedTextColor.AQUA)));
                                    ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.displayName(Component.text(Integer.toString(all)).color(NamedTextColor.AQUA));
                                    for (Player temp : plugin.getLobbiesTeamPlayerNamesMap(s).keySet()) {
                                        if (SafeMapAccess.get(plugin.getLobbiesTeamPlayerNamesMap(s), temp, "").equals(Integer.toString(all)) && temp != null) {
                                            arenalore.add(Component.text(temp.getName()).color(NamedTextColor.GRAY));
                                        }
                                    }
                                    arenamapmeta.lore(arenalore);
                                    arenamap.setItemMeta(arenamapmeta);
                                    inv.setItem(slot, arenamap);
                                    slot++;
                                }
                                p.openInventory(inv);
                            }
                        
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.ENDER_CHEST) {
                        
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (plugin.getLobbyGameState(s) == GameStates.WAITING || plugin.getLobbyGameState(s) == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(plugin.getChatmessages().get(62)).color(NamedTextColor.DARK_AQUA)));
                                inv.setItem(0, randombarrier);
                                for (int i = 1; i <= plugin.getActiveKits(); i++) {
                                    ItemStack arenamap = new ItemStack(Material.ARMOR_STAND);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.displayName(Component.text(Settings.kitdata.getString("pg.kits." + i + ".name")));
                                    arenamap.setItemMeta(arenamapmeta);
                                    inv.setItem(i, arenamap);
                                }
                                p.openInventory(inv);
                            }
                        
                    }
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.MAGMA_CREAM) {
                    
                        String s = null;
                        for (int ii = 1; ii <= 27; ii++) {
                            if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                s = Integer.toString(ii);
                            }
                        }
                        if (plugin.getLobbyGameState(s) == GameStates.WAITING || plugin.getLobbyGameState(s) == GameStates.PREPARING) {
                            plugin.onLeaveLobby(p, s);
                        }
                    
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.EMERALD) {
                    if (p.hasPermission("pg.stats")) {
                        int wins = plugin.getWins(p.getUniqueId().toString());
                        int losses = plugin.getLosses(p.getUniqueId().toString());
                        int rounds = plugin.getRounds(p.getUniqueId().toString());
                        int kills = plugin.getKills(p.getUniqueId().toString());
                        int deaths = plugin.getDeaths(p.getUniqueId().toString());
                        double kd = plugin.getKD(p.getUniqueId().toString());
                        p.sendMessage(Messages.StatsLabel());
                        p.sendMessage(Messages.RoundsLabel(rounds));
                        p.sendMessage(Messages.WinsLabel(wins));
                        p.sendMessage(Messages.LossesLabel(losses));
                        p.sendMessage(Messages.KillsLabel(kills));
                        p.sendMessage(Messages.DeathsLabel(deaths));
                        p.sendMessage(Messages.KDLabel(kd));
                        p.sendMessage(Messages.StatsLabel());
                    }
                }
            }
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR)) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.STICK) {
                        if (isNamedItem(p, Material.STICK, Component.text("Add(Left)/Del(Right) Lobby").color(NamedTextColor.DARK_AQUA))) {
                            if (p.hasPermission("pg.setup")) {
                                plugin.getConfig().set("pg.Lobby.world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                                plugin.getConfig().set("pg.Lobby.coords", Objects.requireNonNull(p.getLocation()));
                                plugin.saveConfig();
                                p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(24)).color(NamedTextColor.GREEN)));
                            }
                            if (p.hasPermission("pg.setup")) {
                                
                                    p.getInventory().clear();
                                    plugin.setAddlobby(true);
                                    e.setCancelled(true);
                                    p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(69)).color(NamedTextColor.GREEN)));
                                
                            }
                        } else if (isNamedItem(p, Material.STICK, Component.text("Add(Left)/Del(Right) Arena").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().clear();
                            plugin.setAddarena(true);
                            p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(70)).color(NamedTextColor.GREEN)));
                        } else if (isNamedItem(p, Material.STICK, Component.text("Add(Left)/Del(Right) Spawn").color(NamedTextColor.DARK_AQUA))) {
                            if (handleSetupSpawnAction(p, true)) {
                                e.setCancelled(true);
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (isNamedItem(p, Material.CLOCK, Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA))) {
                            
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA)));
                                for (int slot = 1; slot <= 27; slot++) {
                                    if (Settings.arenadata.contains("pg.lobbies." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Integer.toString(slot)));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                                p.openInventory(inv);
                            
                        } else if (isNamedItem(p, Material.CLOCK, Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA))) {
                            openChooseArenaInventory(p, resolveSetupLobby(p));
                            /*
                            Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)));
                            
                                for (int slot = 1; slot < 27; slot++) {
                                    if (Settings.arenadata.contains("pg.lobbies." + lobby + "." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Settings.arenadata.getString("pg.lobbies." + lobby + "." + slot + ".name")));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            
                            p.openInventory(inv);
                            */
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.OAK_SIGN) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Set Join-Sign").color(NamedTextColor.DARK_AQUA))) {
                            if (p.getTargetBlock(null, 5).getState() instanceof org.bukkit.block.Sign) {
                                if (p.hasPermission("pg.setup")) {
                                plugin.getConfig().set("pg.Lobby.sign", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                                plugin.saveConfig();
                                p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(35)).color(NamedTextColor.GREEN)));
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Leave Setup-Mode").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().getItemInMainHand().setAmount(0);
                            p.getInventory().setContents(plugin.getSetupStateManager().getPlayerInventory(p));
                            p.getInventory().setArmorContents(plugin.getSetupStateManager().getPlayerArmor(p));
                            p.teleport(plugin.getSetupStateManager().getPlayerLocation(p));
                            p.setLevel(plugin.getSetupStateManager().getPlayerLevel(p));
                            p.setExp(plugin.getSetupStateManager().getPlayerExp(p));
                            p.setGameMode(plugin.getSetupStateManager().getPlayerGameMode(p));
                            plugin.getSetupStateManager().removeSavedInventory(p);
                            plugin.getSetupStateManager().removeSavedArmor(p);
                            plugin.getSetupStateManager().removeSavedLocation(p);
                            plugin.getSetupStateManager().removeSavedLevel(p);
                            plugin.getSetupStateManager().removeSavedExp(p);
                            plugin.getSetupStateManager().removeSavedGameMode(p);
                            plugin.getSetupStateManager().removeSetupPlayer(p);
                        }
                    }
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.STICK) {
                        if (isNamedItem(p, Material.STICK, Component.text("Add(Left)/Del(Right) Lobby").color(NamedTextColor.DARK_AQUA))) {
                            
                                p.getInventory().clear();
                                plugin.setDellobby(true);
                                p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(71)).color(NamedTextColor.GREEN)));
                            
                        } else if (isNamedItem(p, Material.STICK, Component.text("Add(Left)/Del(Right) Arena").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().clear();
                            plugin.setDelarena(true);
                            p.sendMessage(Settings.prefix.append(Component.text(plugin.getChatmessages().get(72)).color(NamedTextColor.GREEN)));
                        } else if (isNamedItem(p, Material.STICK, Component.text("Add(Left)/Del(Right) Spawn").color(NamedTextColor.DARK_AQUA))) {
                            if (handleSetupSpawnAction(p, false)) {
                                e.setCancelled(true);
                            }
                            /*
                            if (p.hasPermission("pg.setup")) {
                                int arenaNumber = 1;
                                int spawnNumber = 1;
                                try {
                                    int i = 1;
                                    boolean arenaName = false;
                                    while (!arenaName) {
                                        if (arena.toString().matches(Objects.requireNonNull(Settings.arenadata.getString("pg.lobbies." + lobby + "." + i + ".name")))) {
                                            arenaNumber = i;
                                            arenaName = true;
                                        } else {
                                            i++;
                                        }
                                    }
                                    int max = 1;
                                    while (Settings.arenadata.contains("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + max)) {
                                        spawnNumber = max;
                                        max++;
                                    }
                                    Settings.arenadata.set("pg.lobbies." + lobby + "." + arenaNumber + ".spawns." + spawnNumber, null);
                                    Settings.arenadata.save(Settings.arenadatafile);
                                    p.sendMessage(Settings.prefix.append(Component.text(String.valueOf(spawnNumber)).color(NamedTextColor.AQUA)).append(Component.text(" " + plugin.getChatmessages().get(28)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                                } catch (Exception ex) {
                                    p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + plugin.getChatmessages().get(31)).color(NamedTextColor.RED)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                                }
                            }
                            */
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (isNamedItem(p, Material.CLOCK, Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA))) {
                            
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA)));
                                for (int slot = 1; slot <= 27; slot++) {
                                    if (Settings.arenadata.contains("pg.lobbies." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Integer.toString(slot)));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                                p.openInventory(inv);
                            
                        } else if (isNamedItem(p, Material.CLOCK, Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA))) {
                            openChooseArenaInventory(p, resolveSetupLobby(p));
                            /*
                            Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)));
                            
                                for (int slot = 1; slot < 27; slot++) {
                                    if (Settings.arenadata.contains("pg.lobbies." + lobby + "." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Settings.arenadata.getString("pg.lobbies." + lobby + "." + slot + ".name")));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            
                            p.openInventory(inv);
                            */
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Leave Setup-Mode").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().getItemInMainHand().setAmount(0);
                            p.getInventory().setContents(plugin.getSetupStateManager().getPlayerInventory(p));
                            p.getInventory().setArmorContents(plugin.getSetupStateManager().getPlayerArmor(p));
                            p.teleport(plugin.getSetupStateManager().getPlayerLocation(p));
                            p.setLevel(plugin.getSetupStateManager().getPlayerLevel(p));
                            p.setExp(plugin.getSetupStateManager().getPlayerExp(p));
                            p.setGameMode(plugin.getSetupStateManager().getPlayerGameMode(p));
                            plugin.getSetupStateManager().removeSavedInventory(p);
                            plugin.getSetupStateManager().removeSavedArmor(p);
                            plugin.getSetupStateManager().removeSavedLocation(p);
                            plugin.getSetupStateManager().removeSavedLevel(p);
                            plugin.getSetupStateManager().removeSavedExp(p);
                            plugin.getSetupStateManager().removeSavedGameMode(p);
                            plugin.getSetupStateManager().removeSetupPlayer(p);
                        }
                    }
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_WALL_SIGN || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_WALL_SIGN) {
                        Sign sign = (Sign) e.getClickedBlock().getState();
                        String line1 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(0));
                        String line2 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(1));
                        String line3 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(2));
                        
                            if (e.getClickedBlock().getLocation().equals(Settings.arenadata.getLocation("pg.lobbies." + line1 + ".sign"))) {
                                if (plugin.game.getPlayerLobby(p) == null && plugin.game.getSpectatorLobby(p) == null) {
                                    e.setCancelled(true);
                                    plugin.onJoinLobby(p, line1);
                                }
                            }
                        
                        if (line2.matches("PotionGames") && line3.matches("Stats")) {
                            int wins = plugin.getWins(p.getUniqueId().toString());
                            int losses = plugin.getLosses(p.getUniqueId().toString());
                            int rounds = plugin.getRounds(p.getUniqueId().toString());
                            int kills = plugin.getKills(p.getUniqueId().toString());
                            int deaths = plugin.getDeaths(p.getUniqueId().toString());
                            double kd = plugin.getKD(p.getUniqueId().toString());
                            p.sendMessage(Messages.StatsLabel());
                            p.sendMessage(Messages.RoundsLabel(rounds));
                            p.sendMessage(Messages.WinsLabel(wins));
                            p.sendMessage(Messages.LossesLabel(losses));
                            p.sendMessage(Messages.KillsLabel(kills));
                            p.sendMessage(Messages.DeathsLabel(deaths));
                            p.sendMessage(Messages.KDLabel(kd));
                            p.sendMessage(Messages.StatsLabel());
                        }
                        if (line1.matches("Place #1") || line1.matches("Place #2") || line1.matches("Place #3")) {
                            Player pstats = Bukkit.getPlayer(line2.toString());
                            p.sendMessage(Messages.StatsLabel());
                            if (pstats != null) {
                                int wins = plugin.getWins(pstats.getUniqueId().toString());
                                int losses = plugin.getLosses(pstats.getUniqueId().toString());
                                int rounds = plugin.getRounds(pstats.getUniqueId().toString());
                                int kills = plugin.getKills(pstats.getUniqueId().toString());
                                int deaths = plugin.getDeaths(pstats.getUniqueId().toString());
                                double kd = plugin.getKD(pstats.getUniqueId().toString());
                                p.sendMessage(Messages.RoundsLabel(rounds));
                                p.sendMessage(Messages.WinsLabel(wins));
                                p.sendMessage(Messages.LossesLabel(losses));
                                p.sendMessage(Messages.KillsLabel(kills));
                                p.sendMessage(Messages.DeathsLabel(deaths));
                                p.sendMessage(Messages.KDLabel(kd));
                            } else {
                                p.sendMessage(Messages.NoPlayerFound());
                            }
                            p.sendMessage(Messages.StatsLabel());
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (plugin.isGameServer()) {
            if (plugin.game.getActivePlayers().contains(p) || plugin.game.getPlayerLobby(p) != null) {
                
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
                        if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    GameStates state = plugin.getLobbyGameState(s); e.setCancelled(state != GameStates.INGAME && !Objects.equals(plugin.game.getPlayerLobby(p), s));
                
            }
        }
    }
}








