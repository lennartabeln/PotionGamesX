package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.GameStates;
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
            if (s == null && plugin.game.isActivePlayer(p)) {
                s = "0"; // Single-lobby mode
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
                    p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(16) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                    p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getLobbyVoteCount(s, displayname))).color(NamedTextColor.AQUA)));
                    p.sendMessage(Messages.ArenaSelector());
                }
            }
        }
    }
    
    private void handleTeamSelection(InventoryClickEvent e, Player p, String s) {
        if (plugin.isActivateTeams(s)) {
            if (e.getView().title().equals(Settings.prefix.append(Component.text(plugin.chatmessages.get(43)).color(NamedTextColor.DARK_AQUA)))) {
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
                p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(rndTeam).color(NamedTextColor.LIGHT_PURPLE)));
                p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getLobbyTeamPlayerCount(s, rndTeam))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
                
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
            p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
            p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(45) + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
            p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getLobbyTeamPlayerCount(s, teamId))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
            p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
            
            if (plugin.isActivateScoreboard()) {
                Objects.requireNonNull(p.getScoreboard().getTeam("team")).prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
            }
        } else {
            p.closeInventory();
            p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
            p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(47)).color(NamedTextColor.RED)));
            p.sendMessage(Settings.prefix.append(Component.text("--------------" + plugin.chatmessages.get(43) + "--------------").color(NamedTextColor.GRAY)));
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
        if (e.getView().title().equals(Settings.prefix.append(Component.text(plugin.chatmessages.get(49)).color(NamedTextColor.DARK_AQUA)))) {
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
                    if (plugin.kitplayernames.containsKey(p) && plugin.kitplayernames.containsValue(shopKits.get(shopitem - 1))) {
                        coinamount = shopSales.get(shopitem - 1);
                    } else {
                        coinamount = shopCosts.get(shopitem - 1);
                    }
                    if (Objects.requireNonNull(PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName())).matches(shopItems.get(shopitem - 1))) {
                        if (bottle >= 1) {
                            if (amount >= coinamount) {
                                amount = amount - coinamount;
                                bottle = bottle - 1;
                                ItemStack randombarrier = new ItemStack(plugin.shoppotiontype.get(shopitem - 1));
                                PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.addCustomEffect(new PotionEffect(plugin.shoppotion.get(shopitem - 1).getType(), plugin.shoppotion.get(shopitem - 1).getDuration(), plugin.shoppotion.get(shopitem - 1).getAmplifier()), true);
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
                                p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(53)).color(NamedTextColor.RED)));
                            }
                        } else {
                            p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(54)).color(NamedTextColor.RED)));
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
                Component lobby = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).displayName();
                p.sendMessage(Settings.prefix.append(lobby.color(NamedTextColor.AQUA)).append(Component.text(" successfully chosen!").color(NamedTextColor.GREEN)));
            }
            e.setCancelled(true);
        }
        if (e.getView().title().equals(Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)))) {
            if (e.getCurrentItem() != null) {
                p.closeInventory();
                Component arena = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).displayName();
                p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" successfully chosen!").color(NamedTextColor.GREEN)));
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
                            if (plugin.isLobbySystem()) {
                                String s = plugin.game.getPlayerLobby(p);
                                if (s != null && plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                    if (!plugin.lobbychests.containsKey(e.getClickedBlock().getLocation())) {
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
                                                if (plugin.lobbyActivateShop.getOrDefault(s, false)) {
                                                    ArrayList<ItemStack> potions1 = new ArrayList<>();
                                                    potions1.add(new ItemStack(Material.GLASS_BOTTLE, 1));
                                                    ArrayList<ItemStack> potions2 = new ArrayList<>();
                                                    potions2.add(plugin.getCoin());
                                                    int item = rnd.nextInt(5);
                                                    if (item < 3) {
                                                        int item1 = rnd.nextInt(plugin.food1.size());
                                                        inv.setItem(slot, plugin.food1.get(item1));
                                                    } else if (item < 4) {
                                                        int item1 = 0;
                                                        inv.setItem(slot, potions1.get(item1));
                                                    } else {
                                                        int item1 = 0;
                                                        inv.setItem(slot, potions2.get(item1));
                                                    }
                                                } else {
                                                    int item1 = rnd.nextInt(plugin.food1.size());
                                                    inv.setItem(slot, plugin.food1.get(item1));
                                                }
                                            } else if (roll < 30) {
                                                int item2 = rnd.nextInt(plugin.food2.size());
                                                inv.setItem(slot, plugin.food2.get(item2));
                                            } else if (roll < 45) {
                                                int item3 = rnd.nextInt(plugin.armour1.size());
                                                inv.setItem(slot, plugin.armour1.get(item3));
                                            } else if (roll < 60) {
                                                int item4 = rnd.nextInt(plugin.armour2.size());
                                                inv.setItem(slot, plugin.armour2.get(item4));
                                            } else if (roll < 67) {
                                                int item5 = rnd.nextInt(plugin.armour3.size());
                                                inv.setItem(slot, plugin.armour3.get(item5));
                                            } else if (roll < 72) {
                                                int item6 = rnd.nextInt(plugin.armour4.size());
                                                inv.setItem(slot, plugin.armour4.get(item6));
                                            } else if (roll < 75) {
                                                int item7 = rnd.nextInt(plugin.armour5.size());
                                                inv.setItem(slot, plugin.armour5.get(item7));
                                            } else if (roll < 95) {
                                                int item8 = rnd.nextInt(plugin.weapons1.size());
                                                inv.setItem(slot, plugin.weapons1.get(item8));
                                            } else {
                                                int item9 = rnd.nextInt(plugin.weapons2.size());
                                                inv.setItem(slot, plugin.weapons2.get(item9));
                                            }
                                        }
                                        plugin.lobbychests.put(e.getClickedBlock().getLocation(), s);
                                        plugin.lobbychestsdata.put(e.getClickedBlock().getLocation(), inv);
                                    }
                                    p.openInventory(plugin.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                    if (p.getActivePotionEffects().isEmpty()) {
                                        Random effect = new Random();
                                        int max = 3;
                                        int min = 0;
                                        int diff = max - min;
                                        int tries = effect.nextInt(diff + 1);
                                        while (tries != 0) {
                                            tries--;
                                            int potion = effect.nextInt(plugin.potions.size());
                                            p.addPotionEffect(plugin.potions.get(potion));
                                        }
                                    }
                                }
                            } else {
                                if (plugin.getGamestate() == GameStates.INGAME) {
                                    if (!plugin.chests.containsKey(e.getClickedBlock().getLocation())) {
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
                                                if (plugin.isActivateShop()) {
                                                    ArrayList<ItemStack> potions1 = new ArrayList<>();
                                                    potions1.add(new ItemStack(Material.GLASS_BOTTLE, 1));
                                                    ArrayList<ItemStack> potions2 = new ArrayList<>();
                                                    potions2.add(plugin.getCoin());
                                                    int item = rnd.nextInt(5);
                                                    if (item < 3) {
                                                        int item1 = rnd.nextInt(plugin.food1.size());
                                                        inv.setItem(slot, plugin.food1.get(item1));
                                                    } else if (item < 4) {
                                                        int item1 = 0;
                                                        inv.setItem(slot, potions1.get(item1));
                                                    } else {
                                                        int item1 = 0;
                                                        inv.setItem(slot, potions2.get(item1));
                                                    }
                                                } else {
                                                    int item1 = rnd.nextInt(plugin.food1.size());
                                                    inv.setItem(slot, plugin.food1.get(item1));
                                                }
                                            } else if (roll < 30) {
                                                int item2 = rnd.nextInt(plugin.food2.size());
                                                inv.setItem(slot, plugin.food2.get(item2));
                                            } else if (roll < 45) {
                                                int item3 = rnd.nextInt(plugin.armour1.size());
                                                inv.setItem(slot, plugin.armour1.get(item3));
                                            } else if (roll < 60) {
                                                int item4 = rnd.nextInt(plugin.armour2.size());
                                                inv.setItem(slot, plugin.armour2.get(item4));
                                            } else if (roll < 67) {
                                                int item5 = rnd.nextInt(plugin.armour3.size());
                                                inv.setItem(slot, plugin.armour3.get(item5));
                                            } else if (roll < 72) {
                                                int item6 = rnd.nextInt(plugin.armour4.size());
                                                inv.setItem(slot, plugin.armour4.get(item6));
                                            } else if (roll < 75) {
                                                int item7 = rnd.nextInt(plugin.armour5.size());
                                                inv.setItem(slot, plugin.armour5.get(item7));
                                            } else if (roll < 95) {
                                                int item8 = rnd.nextInt(plugin.weapons1.size());
                                                inv.setItem(slot, plugin.weapons1.get(item8));
                                            } else {
                                                int item9 = rnd.nextInt(plugin.weapons2.size());
                                                inv.setItem(slot, plugin.weapons2.get(item9));
                                            }
                                        }
                                        plugin.chests.put(e.getClickedBlock().getLocation(), inv);
                                    }
                                    p.openInventory(plugin.chests.get(e.getClickedBlock().getLocation()));
                                    if (p.getActivePotionEffects().isEmpty()) {
                                        Random effect = new Random();
                                        int max = 3;
                                        int min = 0;
                                        int diff = max - min;
                                        int tries = effect.nextInt(diff + 1);
                                        while (tries != 0) {
                                            tries--;
                                            int potion = effect.nextInt(plugin.potions.size());
                                            p.addPotionEffect(plugin.potions.get(potion));
                                        }
                                    }
                                }
                            }
                        }
                        int chestnumber = 1;
                        int chestitem = 1;
                        while (Settings.chestdata.contains("pg.customchests." + chestnumber)) {
                            if (e.getClickedBlock().getType().toString().equals(Objects.requireNonNull(Settings.chestdata.get("pg.customchests." + chestnumber + ".chesttype")).toString())) {
                                if (Settings.chestdata.getBoolean("pg.customchests." + chestnumber + ".activate")) {
                                    if (plugin.isLobbySystem()) {
                                        String s = null;
                                        for (int ii = 1; ii <= 27; ii++) {
                                            if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                                s = Integer.toString(ii);
                                            }
                                        }
                                        if (plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                            if (!plugin.lobbychests.containsKey(e.getClickedBlock().getLocation())) {
                                                Inventory inv;
                                                inv = Bukkit.createInventory(p, Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + ".chestsize"), Settings.prefix);
                                                plugin.lobbychests.put(e.getClickedBlock().getLocation(), s);
                                                plugin.lobbychestsdata.put(e.getClickedBlock().getLocation(), inv);
                                                p.openInventory(plugin.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                                while (Settings.chestdata.contains("pg.customchests." + chestnumber + "." + chestitem)) {
                                                    inv.setItem(Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + chestitem + ".slot") - 1, Settings.chestdata.getItemStack("pg.customchests." + chestnumber + "." + chestitem + ".item"));
                                                    chestitem++;
                                                }
                                            } else {
                                                p.openInventory(plugin.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                            }
                                        }
                                    } else {
                                        if (plugin.getGamestate() == GameStates.INGAME) {
                                            if (!plugin.chests.containsKey(e.getClickedBlock().getLocation())) {
                                                Inventory inv;
                                                inv = Bukkit.createInventory(p, Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + ".chestsize"), Settings.prefix);
                                                plugin.chests.put(e.getClickedBlock().getLocation(), inv);
                                                p.openInventory(plugin.chests.get(e.getClickedBlock().getLocation()));
                                                while (Settings.chestdata.contains("pg.customchests." + chestnumber + "." + chestitem)) {
                                                    inv.setItem(Settings.chestdata.getInt("pg.customchests." + chestnumber + "." + chestitem + ".slot") - 1, Settings.chestdata.getItemStack("pg.customchests." + chestnumber + "." + chestitem + ".item"));
                                                    chestitem++;
                                                }
                                            } else {
                                                p.openInventory(plugin.chests.get(e.getClickedBlock().getLocation()));
                                            }
                                        }
                                    }
                                }
                            }
                            chestnumber++;
                        }
                        if ((e.getClickedBlock()).getType().toString().equals(Objects.requireNonNull(Settings.chestdata.get("pg.chestblocks.shop")).toString())) {
                            if (plugin.isLobbySystem()) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.lobbyActivateShop.getOrDefault(s, false)) {
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
                                        inv = Bukkit.createInventory(p, 9 * 3, Settings.prefix.append(Component.text(plugin.chatmessages.get(49))).color(NamedTextColor.DARK_AQUA));
                                        plugin.lobbychests.put(e.getClickedBlock().getLocation(), s);
                                        plugin.lobbychestsdata.put(e.getClickedBlock().getLocation(), inv);
                                        int shopitem = 1;
                                        for (int i = 0; i < plugin.getActivePotions(); i++) {
                                            int coinamount;
                                            if (plugin.kitplayernames.containsKey(p) && plugin.kitplayernames.containsValue(plugin.shopkit.get(shopitem - 1))) {
                                                coinamount = plugin.shopsale.get(shopitem - 1);
                                            } else {
                                                coinamount = plugin.shopcost.get(shopitem - 1);
                                            }
                                            ItemStack randombarrier = new ItemStack(plugin.shoppotiontype.get(shopitem - 1));
                                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.displayName(Component.text(plugin.shop.get(shopitem - 1)));
                                            ArrayList<Component> lore = new ArrayList<>();
                                            lore.add(Component.text(plugin.chatmessages.get(50) + ": " + plugin.shoppotion.get(shopitem - 1).getDuration() / 20));
                                            lore.add(Component.text(plugin.chatmessages.get(51) + ": " + coinamount + " " + plugin.chatmessages.get(52)));
                                            randombarriermeta.lore(lore);
                                            randombarrier.setItemMeta(randombarriermeta);
                                            inv.setItem(shopitem - 1, randombarrier);
                                            shopitem++;
                                        }
                                    }
                                    p.openInventory(plugin.lobbychestsdata.get(e.getClickedBlock().getLocation()));
                                }
                            } else {
                                if (plugin.isActivateShop()) {
                                    if (plugin.getGamestate() == GameStates.INGAME) {
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
                                        inv = Bukkit.createInventory(p, 9 * 3, Settings.prefix.append(Component.text(plugin.chatmessages.get(49))).color(NamedTextColor.DARK_AQUA));
                                        plugin.chests.put(e.getClickedBlock().getLocation(), inv);
                                        int shopitem = 1;
                                        for (int i = 0; i < plugin.getActivePotions(); i++) {
                                            int coinamount;
                                            if (plugin.kitplayernames.containsKey(p) && plugin.kitplayernames.containsValue(plugin.shopkit.get(shopitem - 1))) {
                                                coinamount = plugin.shopsale.get(shopitem - 1);
                                            } else {
                                                coinamount = plugin.shopcost.get(shopitem - 1);
                                            }
                                            ItemStack randombarrier = new ItemStack(plugin.shoppotiontype.get(shopitem - 1));
                                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                            assert randombarriermeta != null;
                                            randombarriermeta.displayName(Component.text(plugin.shop.get(shopitem - 1)));
                                            ArrayList<Component> lore = new ArrayList<>();
                                            lore.add(Component.text(plugin.chatmessages.get(50) + ": " + plugin.shoppotion.get(shopitem - 1).getDuration() / 20));
                                            lore.add(Component.text(plugin.chatmessages.get(51) + ": " + coinamount + " " + plugin.chatmessages.get(52)));
                                            randombarriermeta.lore(lore);
                                            randombarrier.setItemMeta(randombarriermeta);
                                            inv.setItem(shopitem - 1, randombarrier);
                                            shopitem++;
                                        }
                                    }
                                    p.openInventory(plugin.chests.get(e.getClickedBlock().getLocation()));
                                }
                            }
                        }
                        if (plugin.isLobbySystem()) {
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (e.getClickedBlock().getBlockData() instanceof Waterlogged) {
                                plugin.lobbyWaterBlocksData.put(e.getClickedBlock().getLocation(), e.getClickedBlock().getBlockData());
                                plugin.lobbyWaterBlocks.put(s, plugin.lobbyWaterBlocksData);
                            }
                        } else {
                            if (e.getClickedBlock().getBlockData() instanceof Waterlogged) {
                                plugin.waterBlocks.put(e.getClickedBlock().getLocation(), e.getClickedBlock().getBlockData());
                            }
                        }
                    }
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (e.getHand() == EquipmentSlot.HAND) {
                        if (p.getInventory().getItemInMainHand().getType() == Material.MUSHROOM_STEW || p.getInventory().getItemInMainHand().getType() == Material.RABBIT_STEW || p.getInventory().getItemInMainHand().getType() == Material.BEETROOT_SOUP) {
                            if (plugin.isLobbySystem()) {
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
                            } else {
                                if (plugin.getGamestate() == GameStates.INGAME) {
                                    double health = p.getHealth();
                                    int foodlvl = p.getFoodLevel();
                                    if (health == 20 && foodlvl >= 13) {
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
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.REDSTONE_TORCH) {
                            if (plugin.isLobbySystem()) {
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.lobbyActivateAirdrops.get(s)) {
                                    if (plugin.getLobbyGameState(s) == GameStates.INGAME) {
                                        boolean blocked = false;
                                        Location loc = p.getEyeLocation().add(0, 1, 0);
                                        while (loc.getY() <= 320) {
                                            if (loc.getBlock().getType() != Material.AIR) {
                                                p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(96)).color(NamedTextColor.RED)));
                                                blocked = true;
                                                break;
                                            }
                                            loc.add(0, 1, 0);
                                        }
                                        if (!blocked) {
                                            for (Player all : plugin.playerLobby.keySet()) {
                                                if (plugin.playerLobby.get(all).equals(s)) {
                                                    all.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(98) + ": ").color(NamedTextColor.GREEN)).append(Component.text(p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ()).color(NamedTextColor.AQUA)));
                                                }
                                            }
                                            p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(97)).color(NamedTextColor.GREEN)));
                                            BlockData b = Material.DRIED_KELP_BLOCK.createBlockData();
                                            Location spawnLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 100, p.getLocation().getZ());
                                            p.getWorld().spawn(spawnLoc, FallingBlock.class, fallingBlock -> fallingBlock.setBlockData(b));
                                            plugin.lobbyPlacedBlocksData.put(p.getLocation(), b.getMaterial());
                                            plugin.lobbyPlacedBlocks.put(s, plugin.lobbyPlacedBlocksData);
                                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                                        }
                                    }
                                }
                            } else {
                                if (plugin.isActivateAirdrops()) {
                                    if (plugin.getGamestate() == GameStates.INGAME) {
                                        boolean blocked = false;
                                        Location loc = p.getEyeLocation().add(0, 1, 0);
                                        while (loc.getY() < 256) {
                                            if (loc.getBlock().getType() != Material.AIR) {
                                                p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(96)).color(NamedTextColor.RED)));
                                                blocked = true;
                                                break;
                                            }
                                            loc.add(0, 1, 0);
                                        }
                                        if (!blocked) {
                                            for (Player all : plugin.pgPlayers) {
                                                all.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(98) + ": ").color(NamedTextColor.GREEN)).append(Component.text(p.getLocation().getBlockX() + " " + p.getLocation().getBlockY() + " " + p.getLocation().getBlockZ()).color(NamedTextColor.AQUA)));
                                            }
                                            p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(97)).color(NamedTextColor.GREEN)));
                                            BlockData b = Material.DRIED_KELP_BLOCK.createBlockData();
                                            Location spawnLoc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 100, p.getLocation().getZ());
                                            p.getWorld().spawn(spawnLoc, FallingBlock.class, fallingBlock -> fallingBlock.setBlockData(b));
                                            plugin.placedBlocks.put(p.getLocation(), b.getMaterial());
                                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                                        }
                                    }
                                }
                            }
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.MILK_BUCKET) {
                            if (plugin.isLobbySystem()) {
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
                            } else {
                                if (plugin.getGamestate() == GameStates.INGAME) {
                                    plugin.clearEffects(p);
                                    p.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
                                }
                            }
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                            if (plugin.isLobbySystem()) {
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
                                        if (plugin.playerLobby.containsKey(cp) && !Objects.equals(SafeMapAccess.get(plugin.lobbyteamplayernames, s, p, null), SafeMapAccess.get(plugin.lobbyteamplayernames, s, cp, null))) {
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
                                        p.sendActionBar(Settings.prefix.append(Component.text(plugin.chatmessages.get(12) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf((int) lastDistance)).color(NamedTextColor.AQUA)));
                                    } else {
                                        p.sendActionBar(Settings.prefix.append(Component.text(plugin.chatmessages.get(13)).color(NamedTextColor.RED))); 
                                    }
                                }
                            } else {
                                if (plugin.getGamestate() == GameStates.INGAME) {
                                    Player result = null;
                                    double lastDistance = Double.MAX_VALUE;
                                    for (Player cp : p.getWorld().getPlayers()) {
                                        if (plugin.pgPlayers.contains(cp) && !Objects.equals(plugin.teamplayernames.get(p), plugin.teamplayernames.get(cp))) {
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
                                        p.sendActionBar(Settings.prefix.append(Component.text(plugin.chatmessages.get(12) + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf((int) lastDistance)).color(NamedTextColor.AQUA)));
                                    } else {
                                        p.sendActionBar(Settings.prefix.append(Component.text(plugin.chatmessages.get(13)).color(NamedTextColor.RED)));
                                    }
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                        if (plugin.isLobbySystem()) {
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
                                randomlore.add(0, Component.text(plugin.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(SafeMapAccess.get(plugin.lobbyvotes, s, "Random", 0))).color(NamedTextColor.AQUA)));
                                randombarriermeta.lore(randomlore);
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ArenaSelectorTitle());
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : plugin.lobbyvotes.get(s).keySet()) {
                                    if (!all.matches("Random")) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        arenalore.add(0, Component.text(plugin.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(SafeMapAccess.get(plugin.lobbyvotes, s, all, 0))).color(NamedTextColor.AQUA)));
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
                        } else {
                            if (plugin.getGamestate() == GameStates.WAITING || plugin.getGamestate() == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                ArrayList<Component> randomlore = new ArrayList<>();
                                randomlore.add(0, Component.text(plugin.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(plugin.votes.get("Random"))).color(NamedTextColor.AQUA)));
                                randombarriermeta.lore(randomlore);
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ArenaSelectorTitle());
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : plugin.arenas) {
                                    if (!all.matches("Random")) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        arenalore.add(0, Component.text(plugin.chatmessages.get(15) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(plugin.votes.get(all))).color(NamedTextColor.AQUA)));
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
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (plugin.isLobbySystem()) {
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
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(plugin.chatmessages.get(43)).color(NamedTextColor.DARK_AQUA)));
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (Integer all : plugin.lobbyteams.get(s).keySet()) {
                                    ArrayList<Component> arenalore = new ArrayList<>();
                                    arenalore.add(0, Component.text(plugin.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(SafeMapAccess.get(plugin.lobbyteams, s, all, 0))).color(NamedTextColor.AQUA)));
                                    ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.displayName(Component.text(Integer.toString(all)).color(NamedTextColor.AQUA));
                                    for (Player temp : plugin.lobbyteamplayernames.get(s).keySet()) {
                                        if (SafeMapAccess.get(plugin.lobbyteamplayernames, s, temp, "").equals(Integer.toString(all)) && temp != null) {
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
                        } else {
                            if (plugin.getGamestate() == GameStates.WAITING || plugin.getGamestate() == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(plugin.chatmessages.get(43)).color(NamedTextColor.DARK_AQUA)));
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : plugin.teams) {
                                    ArrayList<Component> arenalore = new ArrayList<>();
                                    arenalore.add(0, Component.text(plugin.chatmessages.get(44) + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(plugin.teamplayers.get(all))).color(NamedTextColor.AQUA)));
                                    ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    assert arenamapmeta != null;
                                    arenamapmeta.displayName(Component.text(all).color(NamedTextColor.AQUA));
                                    for (Player temp : plugin.teamplayernames.keySet()) {
                                        if (plugin.teamplayernames.get(temp).equals(all) && temp != null) {
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
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.ENDER_CHEST) {
                        if (plugin.isLobbySystem()) {
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
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(plugin.chatmessages.get(62)).color(NamedTextColor.DARK_AQUA)));
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
                        } else {
                            if (plugin.getGamestate() == GameStates.WAITING || plugin.getGamestate() == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.displayName(Messages.RandomLabel());
                                randombarrier.setItemMeta(randombarriermeta);
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text(plugin.chatmessages.get(62)).color(NamedTextColor.DARK_AQUA)));
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
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.MAGMA_CREAM) {
                    if (plugin.isLobbySystem()) {
                        String s = null;
                        for (int ii = 1; ii <= 27; ii++) {
                            if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                                s = Integer.toString(ii);
                            }
                        }
                        if (plugin.getLobbyGameState(s) == GameStates.WAITING || plugin.getLobbyGameState(s) == GameStates.PREPARING) {
                            plugin.onLeaveLobby(p, s);
                        }
                    } else {
                        if (plugin.getGamestate() == GameStates.WAITING || plugin.getGamestate() == GameStates.PREPARING) {
                            plugin.onLeave(p);
                        }
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
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Add(Left)/Del(Right) Lobby").color(NamedTextColor.DARK_AQUA))) {
                            if (p.hasPermission("pg.setup")) {
                                if (!plugin.isLobbySystem()) {
                                    plugin.getConfig().set("pg.Lobby.world", Objects.requireNonNull(p.getLocation().getWorld()).getName());
                                    plugin.getConfig().set("pg.Lobby.coords", Objects.requireNonNull(p.getLocation()));
                                    plugin.saveConfig();
                                    p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(24)).color(NamedTextColor.GREEN)));
                                }
                            }
                            if (p.hasPermission("pg.setup")) {
                                if (plugin.isLobbySystem()) {
                                    p.getInventory().clear();
                                    plugin.setAddlobby(true);
                                    e.setCancelled(true);
                                    p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(69)).color(NamedTextColor.GREEN)));
                                }
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Add(Left)/Del(Right) Arena").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().clear();
                            plugin.setAddarena(true);
                            p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(70)).color(NamedTextColor.GREEN)));
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Add(Left)/Del(Right) Spawn").color(NamedTextColor.DARK_AQUA))) {
                            if (p.hasPermission("pg.setup")) {
                                // TODO: Implement spawn setup logic - requires arena and lobby context
                                p.sendMessage(Settings.prefix.append(Component.text("Spawn setup not yet implemented").color(NamedTextColor.YELLOW)));
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA))) {
                            if (plugin.isLobbySystem()) {
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
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA))) {
                            // TODO: Choose Arena functionality (LEFT_CLICK) - requires 'lobby' variable from context
                            // This section needs refactoring to properly obtain the lobby ID from player state
                            /*
                            Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)));
                            if (plugin.isLobbySystem()) {
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
                            } else {
                                for (int slot = 1; slot < 27; slot++) {
                                    if (Settings.arenadata.contains("pg.arenas." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Settings.arenadata.getString("pg.arenas." + slot + ".name")));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
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
                                    if (!plugin.isLobbySystem()) {
                                        plugin.getConfig().set("pg.Lobby.sign", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                                        plugin.saveConfig();
                                        p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(35)).color(NamedTextColor.GREEN)));
                                    }
                                }
                                // TODO: Set Join-Sign for lobby system - requires 'lobby' variable from context
                                // This section needs refactoring to properly obtain the lobby ID from player state
                                /*
                                if (p.hasPermission("pg.setup")) {
                                    if (plugin.isLobbySystem()) {
                                        Settings.arenadata.set("pg.lobbies." + lobby + ".sign", Objects.requireNonNull(p.getTargetBlock(null, 5).getLocation()));
                                        try {
                                            Settings.arenadata.save(Settings.arenadatafile);
                                        } catch (IOException ex) {
                                            Bukkit.getConsoleSender().sendMessage(Settings.prefix.append(Component.text(" " + plugin.chatmessages.get(63) + ": " + ex.getMessage()).color(NamedTextColor.RED)));
                                        }
                                        p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(35)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: ")).append(lobby).append(Component.text(")")).color(NamedTextColor.GRAY));
                                    }
                                }
                                */
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Leave Setup-Mode").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().getItemInMainHand().setAmount(0);
                            p.getInventory().setContents(plugin.inv.get(p.getName()));
                            p.getInventory().setArmorContents(plugin.armor.get(p.getName()));
                            p.teleport(plugin.loc.get(p.getName()));
                            p.setLevel(plugin.lvl.get(p.getName()));
                            p.setExp(plugin.exp.get(p.getName()));
                            p.setGameMode(plugin.gm.get(p.getName()));
                            plugin.inv.remove(p.getName());
                            plugin.armor.remove(p.getName());
                            plugin.loc.remove(p.getName());
                            plugin.lvl.remove(p.getName());
                            plugin.exp.remove(p.getName());
                            plugin.gm.remove(p.getName());
                            plugin.setupPlayer.remove(p);
                        }
                    }
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.STICK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Add(Left)/Del(Right) Lobby").color(NamedTextColor.DARK_AQUA))) {
                            if (plugin.isLobbySystem()) {
                                p.getInventory().clear();
                                plugin.setDellobby(true);
                                p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(71)).color(NamedTextColor.GREEN)));
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Add(Left)/Del(Right) Arena").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().clear();
                            plugin.setDelarena(true);
                            p.sendMessage(Settings.prefix.append(Component.text(plugin.chatmessages.get(72)).color(NamedTextColor.GREEN)));
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Add(Left)/Del(Right) Spawn").color(NamedTextColor.DARK_AQUA))) {
                            // TODO: Add/Delete Spawn functionality - requires 'lobby' and 'arena' variables from context
                            // This section needs refactoring to properly obtain the lobby ID and arena from player state
                            /*
                            if (p.hasPermission("pg.setup")) {
                                if (!plugin.isLobbySystem()) {
                                    int arenaNumber = 1;
                                    int spawnNumber = 1;
                                    try {
                                        int i = 1;
                                        boolean arenaName = false;
                                        while (!arenaName) {
                                            if (arena.toString().matches(Objects.requireNonNull(Settings.arenadata.getString("pg.arenas." + i + ".name")))) {
                                                arenaNumber = i;
                                                arenaName = true;
                                            } else {
                                                i++;
                                            }
                                        }
                                        int max = 1;
                                        while (Settings.arenadata.contains("pg.arenas." + arenaNumber + ".spawns." + max)) {
                                            spawnNumber = max;
                                            max++;
                                        }
                                        Settings.arenadata.set("pg.arenas." + arenaNumber + ".spawns." + spawnNumber, null);
                                        Settings.arenadata.save(Settings.arenadatafile);
                                        p.sendMessage(Settings.prefix.append(Component.text(String.valueOf(spawnNumber)).color(NamedTextColor.AQUA)).append(Component.text(" " + plugin.chatmessages.get(28)).color(NamedTextColor.GREEN)));
                                    } catch (Exception ex) {
                                        p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + plugin.chatmessages.get(31)).color(NamedTextColor.RED)));
                                    }
                                }
                            }
                            if (p.hasPermission("pg.setup")) {
                                if (plugin.isLobbySystem()) {
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
                                        p.sendMessage(Settings.prefix.append(Component.text(String.valueOf(spawnNumber)).color(NamedTextColor.AQUA)).append(Component.text(" " + plugin.chatmessages.get(28)).color(NamedTextColor.GREEN)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                                    } catch (Exception ex) {
                                        p.sendMessage(Settings.prefix.append(arena.color(NamedTextColor.AQUA)).append(Component.text(" " + plugin.chatmessages.get(31)).color(NamedTextColor.RED)).append(Component.text(" (Lobby: " + lobby + ")").color(NamedTextColor.GRAY)));
                                    }
                                }
                            }
                            */
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Choose Lobby").color(NamedTextColor.DARK_AQUA))) {
                            if (plugin.isLobbySystem()) {
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
                            }
                        } else if (p.getInventory().getItemInMainHand().getItemMeta().displayName().equals(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA))) {
                            // TODO: Choose Arena functionality (RIGHT_CLICK) - requires 'lobby' variable from context
                            // This section needs refactoring to properly obtain the lobby ID from player state
                            /*
                            Inventory inv = Bukkit.createInventory(null, 9 * 3, Settings.prefix.append(Component.text("Choose Arena").color(NamedTextColor.DARK_AQUA)));
                            if (plugin.isLobbySystem()) {
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
                            } else {
                                for (int slot = 1; slot < 27; slot++) {
                                    if (Settings.arenadata.contains("pg.arenas." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        assert arenamapmeta != null;
                                        arenamapmeta.displayName(Component.text(Settings.arenadata.getString("pg.arenas." + slot + ".name")));
                                        arenamapmeta.lore(arenalore);
                                        arenamap.setItemMeta(arenamapmeta);
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                            }
                            p.openInventory(inv);
                            */
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                        if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).displayName().equals(Component.text("Leave Setup-Mode").color(NamedTextColor.DARK_AQUA))) {
                            p.getInventory().getItemInMainHand().setAmount(0);
                            p.getInventory().setContents(plugin.inv.get(p.getName()));
                            p.getInventory().setArmorContents(plugin.armor.get(p.getName()));
                            p.teleport(plugin.loc.get(p.getName()));
                            p.setLevel(plugin.lvl.get(p.getName()));
                            p.setExp(plugin.exp.get(p.getName()));
                            p.setGameMode(plugin.gm.get(p.getName()));
                            plugin.inv.remove(p.getName());
                            plugin.armor.remove(p.getName());
                            plugin.loc.remove(p.getName());
                            plugin.lvl.remove(p.getName());
                            plugin.exp.remove(p.getName());
                            plugin.gm.remove(p.getName());
                            plugin.setupPlayer.remove(p);
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
                        if (plugin.isLobbySystem()) {
                            if (e.getClickedBlock().getLocation().equals(Settings.arenadata.getLocation("pg.lobbies." + line1 + ".sign"))) {
                                if (!plugin.playerLobby.containsKey(p) && !plugin.specLobby.containsKey(p)) {
                                    e.setCancelled(true);
                                    plugin.onJoinLobby(p, line1);
                                }
                            }
                        } else {
                            if (e.getClickedBlock().getLocation().equals(plugin.getConfig().getLocation("pg.Lobby.sign"))) {
                                if (!plugin.pgPlayers.contains(p) && !plugin.specPlayers.contains(p)) {
                                    e.setCancelled(true);
                                    plugin.onJoin(p);
                                }
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
            if (plugin.pgPlayers.contains(p) || plugin.playerLobby.containsKey(p)) {
                if (plugin.isLobbySystem()) {
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
                        if (Objects.equals(plugin.game.getPlayerLobby(p), Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    GameStates state = plugin.getLobbyGameState(s); e.setCancelled(state != GameStates.INGAME && !Objects.equals(plugin.game.getPlayerLobby(p), s));
                } else {
                    e.setCancelled(plugin.getGamestate() != GameStates.INGAME && plugin.pgPlayers.contains(p));
                }
            }
        }
    }
}






