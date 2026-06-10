package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.Arena;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Settings;
import com.tw0far.potiongames.managers.IItemStateManager;

import com.tw0far.potiongames.config.ConfigKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

/**
 * Handles inventory and interaction events (multi-lobby only).
 * Extracted from monolithic Events.java.
 */
public class InventoryEventListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger("Minecraft");
    private static int amount;
    private static int bottle;
    private final PotionGamesX plugin;
    
    public InventoryEventListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    private String getPlainDisplayName(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return null;
        }

        Component displayName = meta.displayName();
        if (displayName == null) {
            return null;
        }

        return PlainTextComponentSerializer.plainText().serialize(displayName);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        // Get lobby ID using Game class methods
        String s = plugin.getGame().getPlayerLobby(p);
        if (s == null) {
            s = plugin.getGame().getSpectatorLobby(p);
        }
        if (s != null) {
            GameStates lobbyState = plugin.getLobbyStateManager().getGameState(s);
            boolean canBuild = plugin.getLobbyStateManager().isBuildAllowed(s);
            if ((lobbyState == GameStates.WAITING || lobbyState == GameStates.PREPARING) && !canBuild) {
                handleArenaVoting(e, p, s);
                handleTeamSelection(e, p, s);
                handleShop(e, p, s);
            }
        }
        handleLobbySelection(e, p);
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
            return plugin.getGame().getLobby(selectedLobby);
        }

        String activeLobbyId = plugin.getGame().getPlayerLobby(player);
        if (activeLobbyId != null) {
            try {
                return plugin.getGame().getLobby(Integer.parseInt(activeLobbyId));
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
            player.sendMessage(Messages.ChooseLobbyFirst());
            return;
        }

        List<Arena> arenas = lobby.getArenas();
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ChooseArenaTitle());
        if (inv == null) return;

        int slot = 0;
        for (Arena arena : arenas) {
            if (slot >= inv.getSize()) {
                break;
            }
            ItemStack item = new ItemStack(Material.MAP);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                continue;
            }
            meta.displayName(Component.text(arena.getName()).color(NamedTextColor.AQUA));
            if (!item.setItemMeta(meta)) {
                continue;
            }
            inv.setItem(slot++, item);
        }

        player.openInventory(inv);
    }

    private boolean handleSetupSpawnAction(Player player, boolean addSpawn) {
        if (!player.hasPermission("pg.setup")) {
            player.sendMessage(Messages.PermissionNoUse());
            return true;
        }

        Lobby lobby = resolveSetupLobby(player);
        Arena arena = resolveSetupArena(player, lobby);
        if (lobby == null) {
            player.sendMessage(Messages.ChooseLobbyFirst());
            return true;
        }
        if (arena == null) {
            player.sendMessage(Messages.ChooseArenaFirst());
            return true;
        }

        if (addSpawn) {
            plugin.getSetupHandler().addSpawn(player, arena.getName(), lobby.getId());
        } else {
            if (arena.getSpawns().isEmpty()) {
                player.sendMessage(Messages.NoSpawnsToRemove());
                return true;
            }
            plugin.getSetupHandler().removeSpawn(player, arena.getName(), lobby.getId());
        }
        return true;
    }
    
    private void handleArenaVoting(InventoryClickEvent e, Player p, String s) {
        if (e.getView().title().equals(Messages.ArenaSelector())) {
            String displayname = getPlainDisplayName(e.getCurrentItem());
            if (displayname != null) {
                    
                    // Check if player has already voted using delegation
                    if (!plugin.getArenaStateManager().hasPlayerVotedInLobby(s, p)) {
                        // First time voting - add vote
                        p.closeInventory();
                        plugin.getArenaStateManager().addLobbyVote(s, displayname);
                        plugin.getArenaStateManager().recordPlayerVoteInLobby(s, p, displayname);
                        try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) { lobby.recordVote(p, displayname); } } catch (NumberFormatException ignored) { }
                    } else {
                        // Switching vote - remove old vote and add new
                        p.closeInventory();
                        String previousVote = plugin.getArenaStateManager().getPlayerVoteInLobby(s, p);
                        if (previousVote != null) {
                            plugin.getArenaStateManager().removeLobbyVote(s, previousVote);
                        }
                        plugin.getArenaStateManager().addLobbyVote(s, displayname);
                        plugin.getArenaStateManager().recordPlayerVoteInLobby(s, p, displayname);
                        try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) { lobby.recordVote(p, displayname); } } catch (NumberFormatException ignored) { }
                    }
                    
                    // Send feedback messages
                    p.sendMessage(Messages.ArenaSelector());
                    p.sendMessage(Settings.prefix.append(Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
                    p.sendMessage(Settings.prefix.append(Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getArenaStateManager().getLobbyVoteCount(s, displayname))).color(NamedTextColor.AQUA)));
                    p.sendMessage(Messages.ArenaSelector());
            }
        }
    }
    
    private void handleTeamSelection(InventoryClickEvent e, Player p, String s) {
        if (plugin.getLobbyStateManager().isActivateTeams(s)) {
            if (e.getView().title().equals(Messages.SelectorTeam())) {
                String displayname = getPlainDisplayName(e.getCurrentItem());
                if (displayname != null) {
                    int maxteamplayers = plugin.getArenaStateManager().getLobbyTeamSize(s);
                         
                    // Check if player already has a team using delegation
                    if (!plugin.getArenaStateManager().hasPlayerTeamInLobby(s, p)) {
                        // First time assigning team
                        if (displayname.equals(PlainTextComponentSerializer.plainText().serialize(Messages.RandomLabel()))) {
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
    
    private void assignRandomTeam(InventoryClickEvent e, Player p, String s, int maxteamplayers) {
        boolean teamfound = false;
        Map<Integer, Integer> lobbyTeamsMap = plugin.getArenaStateManager().getLobbyTeams(s);
        
        if (lobbyTeamsMap == null || lobbyTeamsMap.isEmpty()) {
            return;
        }
        
        while (!teamfound) {
            Random rnd = new Random();
            int rndTeam = rnd.nextInt(lobbyTeamsMap.size()) + 1;
            Integer teamPlayers = plugin.getArenaStateManager().getLobbyTeamPlayerCount(s, rndTeam);
            
            if (teamPlayers != null && teamPlayers < maxteamplayers) {
                teamfound = true;
                p.closeInventory();
                plugin.getArenaStateManager().incrementLobbyTeamPlayers(s, rndTeam);
                plugin.getArenaStateManager().recordPlayerTeamInLobby(s, p, Integer.toString(rndTeam));
                
                // Send feedback
                p.sendMessage(Messages.SelectorTeam());
                p.sendMessage(Settings.prefix.append(Component.text(Messages.raw("team.now_in", "You are now in team") + ": ").color(NamedTextColor.GREEN)).append(Component.text(rndTeam).color(NamedTextColor.LIGHT_PURPLE)));
                p.sendMessage(Settings.prefix.append(Component.text(Messages.raw("players.label", "Players") + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getArenaStateManager().getLobbyTeamPlayerCount(s, rndTeam))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
                p.sendMessage(Messages.SelectorTeam());
                
                if (plugin.getConfigManager().isActivateScoreboard()) {
                    var team = p.getScoreboard().getTeam("team");
                    if (team != null) {
                        team.prefix(Component.text(Integer.toString(rndTeam)).color(NamedTextColor.DARK_AQUA));
                    }
                }
            }
        }
    }
    
    private void assignSpecificTeam(InventoryClickEvent e, Player p, String s, String displayname, int maxteamplayers) {
        int teamId = Integer.parseInt(displayname);
        Integer currentPlayers = plugin.getArenaStateManager().getLobbyTeamPlayerCount(s, teamId);
        
        if (currentPlayers != null && currentPlayers < maxteamplayers) {
            p.closeInventory();
            plugin.getArenaStateManager().incrementLobbyTeamPlayers(s, teamId);
            plugin.getArenaStateManager().recordPlayerTeamInLobby(s, p, displayname);
            
            // Send feedback
            p.sendMessage(Messages.SelectorTeam());
            p.sendMessage(Settings.prefix.append(Component.text(Messages.raw("team.now_in", "You are now in team") + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
            p.sendMessage(Settings.prefix.append(Component.text(Messages.raw("players.label", "Players") + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getArenaStateManager().getLobbyTeamPlayerCount(s, teamId))).color(NamedTextColor.AQUA)).append(Component.text("/").color(NamedTextColor.GRAY)).append(Component.text(String.valueOf(maxteamplayers)).color(NamedTextColor.AQUA)));
            p.sendMessage(Messages.SelectorTeam());
            
            if (plugin.getConfigManager().isActivateScoreboard()) {
                var team = p.getScoreboard().getTeam("team");
                if (team != null) {
                    team.prefix(Component.text(displayname).color(NamedTextColor.DARK_AQUA));
                }
            }
        } else {
            p.closeInventory();
            p.sendMessage(Messages.SelectorTeam());
            p.sendMessage(Messages.TeamAlreadyFull());
            p.sendMessage(Messages.SelectorTeam());
        }
    }
    
    private void switchTeam(InventoryClickEvent e, Player p, String s, String displayname, int maxteamplayers) {
        p.closeInventory();
        
        // Get player's current team and remove from it
        String previousTeam = plugin.getArenaStateManager().getPlayerTeamInLobby(s, p);
        if (previousTeam != null) {
            plugin.getArenaStateManager().decrementLobbyTeamPlayers(s, Integer.parseInt(previousTeam));
            plugin.getArenaStateManager().removePlayerTeamInLobby(s, p);
        }
        
        // Assign to new team
        if (displayname.equals(PlainTextComponentSerializer.plainText().serialize(Messages.RandomLabel()))) {
            assignRandomTeam(e, p, s, maxteamplayers);
        } else {
            assignSpecificTeam(e, p, s, displayname, maxteamplayers);
        }
    }
    
    private void handleShop(InventoryClickEvent e, Player p, String s) {
        if (e.getView().title().equals(Messages.ShopLabel())) {
            String displayname = getPlainDisplayName(e.getCurrentItem());
            if (displayname != null) {
                amount = p.getTotalExperience() * 10;
                bottle = 0;
                ItemStack bottleItem = plugin.getBottle();
                for (ItemStack item : p.getInventory().getContents()) {
                    if (item != null && bottleItem != null && item.getType() == bottleItem.getType()) {
                        bottle += item.getAmount();
                    }
                }
                int shopitem = 1;
                // Get shop data from ItemStateManager
                var itemStateManager = plugin.getItemStateManager();
                ArrayList<String> shopItems = new ArrayList<>(itemStateManager.getShopItems());
                
                for (int i = 0; i < shopItems.size(); i++) {
                    int coinamount;
                                                Lobby __kl = plugin.getGame().getLobby(Integer.parseInt(s));
                                                String __kn = __kl != null && __kl.getParticipant(p) != null && __kl.getParticipant(p).getKit() != null
                                                    ? __kl.getParticipant(p).getKit().getName() : null;
                                                if (__kn != null && __kn.equals(itemStateManager.getShopKit(shopitem - 1))) {
                        coinamount = itemStateManager.getShopSale(shopitem - 1);
                    } else {
                        coinamount = itemStateManager.getShopCost(shopitem - 1);
                    }
                    if (displayname.equals(shopItems.get(shopitem - 1))) {
                        if (bottle >= 1) {
                            if (amount >= coinamount) {
                                amount = amount - coinamount;
                                bottle = bottle - 1;
                                ItemStack potionType = itemStateManager.getShopPotionType(shopitem - 1);
                                if (potionType == null) continue;
                                ItemStack randombarrier = new ItemStack(potionType);
                                PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                                if (randombarriermeta == null) {
                                    continue;
                                }
                                PotionEffect shopPotion = itemStateManager.getShopPotion(shopitem - 1);
                                if (shopPotion == null) continue;
                                        randombarriermeta.addCustomEffect(new PotionEffect(shopPotion.getType(), shopPotion.getDuration(), shopPotion.getAmplifier(), shopPotion.isAmbient(), shopPotion.hasParticles(), shopPotion.hasIcon()), true);
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
                    p.sendMessage(Messages.YouNotEnoughCoins());
                            }
                        } else {
                    p.sendMessage(Messages.YouNotEmptyBottle());
                        }
                    }
                    shopitem++;
                }
            }
            e.setCancelled(true);
        }
    }

    private void fillLootChest(Inventory inv, Random rnd, ChestLootProfile profile, IItemStateManager itemStateManager) {
        if (inv == null || rnd == null || profile == null || profile.factor <= 0.0) {
            return;
        }

        ArrayList<Integer> emptySlots = new ArrayList<>();
        for (int slot = 0; slot < inv.getSize(); slot++) {
            if (inv.getItem(slot) == null) {
                emptySlots.add(slot);
            }
        }

        if (emptySlots.isEmpty()) {
            return;
        }

        int baseRolls = rnd.nextInt(5) + 2;
        int rolls = Math.max(1, (int) Math.round(baseRolls * profile.factor));

        for (int i = 0; i < rolls && !emptySlots.isEmpty(); i++) {
            LootPool pool = pickLootPool(rnd, profile);
            ItemStack loot = pickLootItem(pool, itemStateManager, rnd);
            if (loot == null) {
                continue;
            }

            int slotIndex = rnd.nextInt(emptySlots.size());
            int slot = emptySlots.remove(slotIndex);
            inv.setItem(slot, loot);
        }
    }

    private LootPool pickLootPool(Random rnd, ChestLootProfile profile) {
        double total = profile.totalWeight();
        if (total <= 0.0) {
            return LootPool.FOOD1;
        }

        double selection = rnd.nextDouble() * total;
        double current = 0.0;
        for (LootPool pool : LootPool.values()) {
            current += profile.weight(pool);
            if (selection <= current) {
                return pool;
            }
        }
        return LootPool.WEAPONS2;
    }

    private ItemStack pickLootItem(LootPool pool, IItemStateManager itemStateManager, Random rnd) {
        return switch (pool) {
            case FOOD1 -> itemStateManager.getRandomFood1();
            case FOOD2 -> itemStateManager.getRandomFood2();
            case ARMOUR1 -> itemStateManager.getRandomArmor(1);
            case ARMOUR2 -> itemStateManager.getRandomArmor(2);
            case ARMOUR3 -> itemStateManager.getRandomArmor(3);
            case ARMOUR4 -> itemStateManager.getRandomArmor(4);
            case ARMOUR5 -> itemStateManager.getRandomArmor(5);
            case WEAPONS1 -> itemStateManager.getRandomWeapon(1);
            case WEAPONS2 -> itemStateManager.getRandomWeapon(2);
        };
    }

    private ChestLootProfile resolveLootProfile(ConfigurationSection chestSection, ChestLootProfile defaultProfile) {
        if (chestSection == null) {
            return defaultProfile;
        }

        ConfigurationSection lootSection = chestSection.getConfigurationSection("loot");
        if (lootSection == null) {
            return defaultProfile;
        }

        ChestLootProfile profile = new ChestLootProfile();
        profile.factor = lootSection.getDouble("factor", defaultProfile.factor);
        profile.food1 = lootSection.getDouble("food1", defaultProfile.food1);
        profile.food2 = lootSection.getDouble("food2", defaultProfile.food2);
        profile.armour1 = lootSection.getDouble("armour1", defaultProfile.armour1);
        profile.armour2 = lootSection.getDouble("armour2", defaultProfile.armour2);
        profile.armour3 = lootSection.getDouble("armour3", defaultProfile.armour3);
        profile.armour4 = lootSection.getDouble("armour4", defaultProfile.armour4);
        profile.armour5 = lootSection.getDouble("armour5", defaultProfile.armour5);
        profile.weapons1 = lootSection.getDouble("weapons1", defaultProfile.weapons1);
        profile.weapons2 = lootSection.getDouble("weapons2", defaultProfile.weapons2);
        return profile;
    }

    private ChestLootProfile defaultNormalLootProfile() {
        // Load from config if available, otherwise use defaults matching README spec
        var config = plugin.getConfig();
        if (config != null && config.contains("pg.chestloot.normal.loot")) {
            ChestLootProfile profile = new ChestLootProfile();
            var lootConfig = config.getConfigurationSection("pg.chestloot.normal.loot");
            if (lootConfig != null) {
                profile.factor = lootConfig.getDouble("factor", 1.0);
                profile.food1 = lootConfig.getDouble("food1", 20.0);
                profile.food2 = lootConfig.getDouble("food2", 10.0);
                profile.armour1 = lootConfig.getDouble("armour1", 15.0);
                profile.armour2 = lootConfig.getDouble("armour2", 15.0);
                profile.armour3 = lootConfig.getDouble("armour3", 7.0);
                profile.armour4 = lootConfig.getDouble("armour4", 5.0);
                profile.armour5 = lootConfig.getDouble("armour5", 3.0);
                profile.weapons1 = lootConfig.getDouble("weapons1", 20.0);
                profile.weapons2 = lootConfig.getDouble("weapons2", 5.0);
                return profile;
            }
        }
        
        // Built-in defaults (matches README spec: food1 20%, food2 10%, etc)
        ChestLootProfile profile = new ChestLootProfile();
        profile.factor = 1.0;
        profile.food1 = 20.0;
        profile.food2 = 10.0;
        profile.armour1 = 15.0;
        profile.armour2 = 15.0;
        profile.armour3 = 7.0;
        profile.armour4 = 5.0;
        profile.armour5 = 3.0;
        profile.weapons1 = 20.0;
        profile.weapons2 = 5.0;
        return profile;
    }

    private ChestLootProfile defaultCustomLootProfile() {
        ChestLootProfile profile = defaultNormalLootProfile();
        profile.factor = 0.0;
        return profile;
    }

    private static final class ChestLootProfile {
        private double factor;
        private double food1;
        private double food2;
        private double armour1;
        private double armour2;
        private double armour3;
        private double armour4;
        private double armour5;
        private double weapons1;
        private double weapons2;

        double totalWeight() {
            return food1 + food2 + armour1 + armour2 + armour3 + armour4 + armour5 + weapons1 + weapons2;
        }

        double weight(LootPool pool) {
            return switch (pool) {
                case FOOD1 -> food1;
                case FOOD2 -> food2;
                case ARMOUR1 -> armour1;
                case ARMOUR2 -> armour2;
                case ARMOUR3 -> armour3;
                case ARMOUR4 -> armour4;
                case ARMOUR5 -> armour5;
                case WEAPONS1 -> weapons1;
                case WEAPONS2 -> weapons2;
            };
        }
    }

    private enum LootPool {
        FOOD1,
        FOOD2,
        ARMOUR1,
        ARMOUR2,
        ARMOUR3,
        ARMOUR4,
        ARMOUR5,
        WEAPONS1,
        WEAPONS2
    }
    
    private void handleLobbySelection(InventoryClickEvent e, Player p) {
        if (e.getView().title().equals(Messages.LobbyListTitle())) {
            String displayname = getPlainDisplayName(e.getCurrentItem());
            if (displayname != null) {
                p.closeInventory();
                plugin.onJoinLobby(p, displayname);
            }
            e.setCancelled(true);
        }
        if (e.getView().title().equals(Messages.ChooseLobbyTitle())) {
            String lobbyName = getPlainDisplayName(e.getCurrentItem());
            if (lobbyName != null) {
                p.closeInventory();
                try {
                    int lobbyId = Integer.parseInt(lobbyName);
                    Lobby lobby = plugin.getGame().getLobby(lobbyId);
                    if (lobby != null) {
                        plugin.getSetupStateManager().setSelectedLobby(p, lobbyId);
                        plugin.getSetupStateManager().removeSelectedArena(p);
                        p.sendMessage(Messages.LobbySelected(lobbyId));
                    } else {
                        p.sendMessage(Messages.LobbyDoesNotExist());
                    }
                } catch (NumberFormatException ex) {
                    p.sendMessage(Messages.InvalidLobbySelection());
                }
            }
            e.setCancelled(true);
        }
        if (e.getView().title().equals(Settings.prefix.append(Component.text(Messages.raw("choose.arena", "Choose Arena")).color(NamedTextColor.DARK_AQUA)))) {
            String arenaName = getPlainDisplayName(e.getCurrentItem());
            if (arenaName != null) {
                p.closeInventory();
                Lobby lobby = resolveSetupLobby(p);
                if (lobby == null) {
                    p.sendMessage(Messages.ChooseLobbyFirst());
                } else {
                    Arena arena = lobby.getArena(arenaName);
                    if (arena != null) {
                        plugin.getSetupStateManager().setSelectedArena(p, arenaName);
                        p.sendMessage(Messages.ArenaSelected(arenaName));
                    } else {
                        p.sendMessage(Messages.ArenaNotExists());
                    }
                }
            }
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        
        // Handle all signs
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock() != null) {
            Material clickedType = e.getClickedBlock().getType();
            if (clickedType.name().contains("_SIGN")) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                String line1 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(0));
                String line2 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(1));
                String line3 = PlainTextComponentSerializer.plainText().serialize(sign.getSide(Side.FRONT).line(2));
                
                // Check Join Signs
                try {
                    int lobbyId = Integer.parseInt(line1);
                    String signKey = ConfigKeys.LOBBY_JOIN_SIGN.getKey(lobbyId);
                    if (e.getClickedBlock().getLocation().equals(Settings.lobbies.getLocation(signKey))) {
                        e.setCancelled(true);
                        if (plugin.getGame().getPlayerLobby(p) == null && plugin.getGame().getSpectatorLobby(p) == null) {
                            plugin.onJoinLobby(p, line1);
                        }
                        return;
                    }
                } catch (NumberFormatException ex) {
                    LOGGER.log(Level.WARNING, "[PotionGamesX] Invalid lobby ID on join sign", ex);
                }

                // Check Stats Signs
                if (line2 != null && line2.matches("PotionGamesX") && line3 != null && line3.matches("Stats")) {
                    e.setCancelled(true);
                    int wins = plugin.getDatabaseManager().getWins(p.getUniqueId().toString());
                    int losses = plugin.getDatabaseManager().getLosses(p.getUniqueId().toString());
                    int rounds = plugin.getDatabaseManager().getRounds(p.getUniqueId().toString());
                    int kills = plugin.getDatabaseManager().getKills(p.getUniqueId().toString());
                    int deaths = plugin.getDatabaseManager().getDeaths(p.getUniqueId().toString());
                    double kd = plugin.getDatabaseManager().getKD(p.getUniqueId().toString());
                    p.sendMessage(Messages.StatsLabel());
                    p.sendMessage(Messages.RoundsLabel(rounds));
                    p.sendMessage(Messages.WinsLabel(wins));
                    p.sendMessage(Messages.LossesLabel(losses));
                    p.sendMessage(Messages.KillsLabel(kills));
                    p.sendMessage(Messages.DeathsLabel(deaths));
                    p.sendMessage(Messages.KDLabel(kd));
                    p.sendMessage(Messages.StatsLabel());
                    return;
                }
            }
        }

        if (plugin.getConfigManager().isGameServer()) {
            if (plugin.getGame().isActivePlayer(p) || plugin.getGame().isInLobby(p)) {
                if (e.getAction() == Action.PHYSICAL) {
                    var clicked = e.getClickedBlock();
                    if (clicked != null && clicked.getType() == Material.FARMLAND) {
                        e.setCancelled(true);
                    }
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (e.getHand() == EquipmentSlot.HAND) {
                            var clickedBlock = e.getClickedBlock();
                            var normalChestBlock = Settings.chests.get("pg.chestblocks.normal");
                            if (clickedBlock != null && normalChestBlock != null
                                    && clickedBlock.getType().toString().equals(normalChestBlock.toString())) {
                            
                                String s = plugin.getGame().getPlayerLobby(p);
                                if (s != null && plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
                                    boolean hasLobbyChest = false;
                                    try { hasLobbyChest = plugin.getGame().getLobby(Integer.parseInt(s)) != null && plugin.getGame().getLobby(Integer.parseInt(s)).hasChestInventory(e.getClickedBlock().getLocation()); } catch (NumberFormatException ignored) { }
                                    if (!hasLobbyChest) {
                                        Inventory inv = Bukkit.createInventory(p, 27, Settings.prefix);
                                        if (inv == null) return;
                                        Random rnd = new Random();
                                        IItemStateManager itemStateManager = plugin.getItemStateManager();
                                        ChestLootProfile profile = resolveLootProfile(
                                                Settings.chests.getConfigurationSection("pg.chestloot.normal"),
                                                defaultNormalLootProfile());
                                        fillLootChest(inv, rnd, profile, itemStateManager);
                                        try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null && inv != null) lobby.setChestInventory(e.getClickedBlock().getLocation(), inv.getContents()); } catch (NumberFormatException ignored) { }
                                    }
                                    Inventory chestInv;
                                    try {
                                        Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s));
                                        if (lobby != null && lobby.hasChestInventory(e.getClickedBlock().getLocation())) {
                                            Inventory tmpInv = Bukkit.createInventory(null, 27);
                                            if (tmpInv == null) return;
                                            ItemStack[] items = lobby.getChestInventory(e.getClickedBlock().getLocation());
                                            if (items != null) tmpInv.setContents(items);
                                            chestInv = tmpInv;
                                        } else {
                                            chestInv = null;
                                        }
                                    } catch (NumberFormatException ignored) {
                                        chestInv = null;
                                    }
                                    if (chestInv != null) {
                                        p.openInventory(chestInv);
                                    }
                                    if (p.getActivePotionEffects().isEmpty()) {
                                        Random effect = new Random();
                                        int max = 3;
                                        int min = 0;
                                        int diff = max - min;
                                        int tries = effect.nextInt(diff + 1);
                                        ArrayList<PotionEffect> potions = new ArrayList<>(plugin.getItemStateManager().getPotions());
                                        while (tries != 0) {
                                            tries--;
                                            int potion = effect.nextInt(potions.size());
                                            p.addPotionEffect(potions.get(potion));
                                        }
                                    }
                                }
                            
                        }
                        int chestnumber = 1;
                        while (Settings.chests.contains("pg.customchests." + chestnumber)) {
                            int chestitem = 1;
                            ConfigurationSection customChest = Settings.chests.getConfigurationSection("pg.customchests." + chestnumber);
                            Object chestType = customChest.get("chesttype");
                            if (customChest != null && chestType != null && e.getClickedBlock().getType().toString().equals(chestType.toString())) {
                                if (customChest.getBoolean("activate")) {
                                    
                                        String s = null;
                                        for (int ii = 1; ii <= 27; ii++) {
                                            if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                                s = Integer.toString(ii);
                                            }
                                        }
                                        if (s != null && plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
                                    boolean hasLobbyChest = false;
                                    try { hasLobbyChest = plugin.getGame().getLobby(Integer.parseInt(s)) != null && plugin.getGame().getLobby(Integer.parseInt(s)).hasChestInventory(e.getClickedBlock().getLocation()); } catch (NumberFormatException ignored) { }
                                    if (!hasLobbyChest) {
                                                Inventory inv = Bukkit.createInventory(p, customChest.getInt("chestsize"), Settings.prefix);
                                                if (inv == null) return;
                                                Random rnd = new Random();
                                                IItemStateManager itemStateManager = plugin.getItemStateManager();
                                                ChestLootProfile profile = resolveLootProfile(customChest, defaultCustomLootProfile());
                                                while (customChest.contains(String.valueOf(chestitem))) {
                                                    inv.setItem(customChest.getInt(chestitem + ".slot") - 1, customChest.getItemStack(chestitem + ".item"));
                                                    chestitem++;
                                                }
                                                fillLootChest(inv, rnd, profile, itemStateManager);
                                                try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null && inv != null) lobby.setChestInventory(e.getClickedBlock().getLocation(), inv.getContents()); } catch (NumberFormatException ignored) { }
                                                if (inv != null) {
                                                    p.openInventory(inv);
                                                }
                                            } else {
                                                Inventory chestInv;
                                                try {
                                                    Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s));
                                                    if (lobby != null && lobby.hasChestInventory(e.getClickedBlock().getLocation())) {
                                                        Inventory tmpInv = Bukkit.createInventory(null, 27);
                                                        if (tmpInv == null) return;
                                                        ItemStack[] items = lobby.getChestInventory(e.getClickedBlock().getLocation());
                                                        if (items != null) tmpInv.setContents(items);
                                                        chestInv = tmpInv;
                                                    } else {
                                                        chestInv = null;
                                                    }
                                                } catch (NumberFormatException ignored) {
                                                    chestInv = null;
                                                }
                                                if (chestInv != null) {
                                                    p.openInventory(chestInv);
                                                }
                                            }
                                        }
                                    
                                }
                            }
                            chestnumber++;
                        }
                        var shopChestBlock = Settings.chests.get("pg.chestblocks.shop");
                        if (shopChestBlock != null && (e.getClickedBlock()).getType().toString().equals(shopChestBlock.toString())) {
                            
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                boolean isLobbyActivateShop = false;
                                try { isLobbyActivateShop = plugin.getGame().getLobby(Integer.parseInt(s)) != null && plugin.getGame().getLobby(Integer.parseInt(s)).isActivateShop(); } catch (NumberFormatException ignored) { }
                                if (isLobbyActivateShop) {
                                    if (plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
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
                                        inv = Bukkit.createInventory(p, 9 * 3, Settings.prefix.append(Component.text(Messages.ShopText())).color(NamedTextColor.DARK_AQUA));
                                        if (inv == null) return;
                                        try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) lobby.setChestInventory(e.getClickedBlock().getLocation(), inv.getContents()); } catch (NumberFormatException ignored) { }
                                        var itemStateManager = plugin.getItemStateManager();
                                        ArrayList<String> shopItems = new ArrayList<>(itemStateManager.getShopItems());
                                        int shopitem = 1;
                                        for (int i = 0; i < plugin.getConfigManager().getActivePotions(); i++) {
                                            int coinamount;
                    Lobby _kl = plugin.getGame().getLobby(Integer.parseInt(s));
                    String _kn = _kl != null && _kl.getParticipant(p) != null && _kl.getParticipant(p).getKit() != null
                        ? _kl.getParticipant(p).getKit().getName() : null;
                    if (_kn != null && _kn.equals(itemStateManager.getShopKit(shopitem - 1))) {
                                                    coinamount = itemStateManager.getShopSale(shopitem - 1);
                                                } else {
                                                    coinamount = itemStateManager.getShopCost(shopitem - 1);
                                                }
                                                ItemStack potionType = itemStateManager.getShopPotionType(shopitem - 1);
                                                if (potionType == null) { shopitem++; continue; }
                                                ItemStack randombarrier = new ItemStack(potionType);
                                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                            if (randombarriermeta == null) {
                                                continue;
                                            }
                                            randombarriermeta.displayName(Component.text(shopItems.get(shopitem - 1)));
                                            ArrayList<Component> lore = new ArrayList<>();
                                            PotionEffect shopPotion = itemStateManager.getShopPotion(shopitem - 1);
                                            if (shopPotion == null) { shopitem++; continue; }
                                            lore.add(Component.text(Messages.raw("duration.label", "Duration") + ": " + shopPotion.getDuration() / 20));
                                            lore.add(Component.text(Messages.PriceLabel() + ": " + coinamount + " " + Messages.raw("coins.label", "Coins")));
                                            randombarriermeta.lore(lore);
                                            if (!randombarrier.setItemMeta(randombarriermeta)) {
                                                continue;
                                            }
                                            inv.setItem(shopitem - 1, randombarrier);
                                            shopitem++;
                                        }
                                    }
                                    Inventory chestInv;
                                    try {
                                        Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s));
                                        if (lobby != null && lobby.hasChestInventory(e.getClickedBlock().getLocation())) {
                                            Inventory tmpInv = Bukkit.createInventory(null, 27);
                                            if (tmpInv == null) return;
                                            ItemStack[] items = lobby.getChestInventory(e.getClickedBlock().getLocation());
                                            if (items != null) tmpInv.setContents(items);
                                            chestInv = tmpInv;
                                        } else {
                                            chestInv = null;
                                        }
                                    } catch (NumberFormatException ignored) {
                                        chestInv = null;
                                    }
                                    if (chestInv != null) {
                                        p.openInventory(chestInv);
                                    }
                                }
                            
                        }
                        
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (e.getClickedBlock().getBlockData() instanceof Waterlogged) {
                                plugin.getBlockStateManager().trackLobbyWaterBlock(s, e.getClickedBlock().getLocation(), e.getClickedBlock().getBlockData());
                            }
                        
                    }
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (e.getHand() == EquipmentSlot.HAND) {
                        if (p.getInventory().getItemInMainHand().getType() == Material.MUSHROOM_STEW || p.getInventory().getItemInMainHand().getType() == Material.RABBIT_STEW || p.getInventory().getItemInMainHand().getType() == Material.BEETROOT_SOUP) {
                            
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
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
                                    if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                boolean isLobbyActivateAirdrops = false;
                                try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) isLobbyActivateAirdrops = lobby.isActivateAirdrops(); } catch (NumberFormatException ignored) { }
                                if (isLobbyActivateAirdrops) {
                                    if (plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
                                        boolean blocked = false;
                                        Location loc = p.getEyeLocation().add(0, 1, 0);
                                        while (loc.getY() <= 320) {
                                            if (loc.getBlock().getType() != Material.AIR) {
                                                p.sendMessage(Messages.YouBlockAbove());
                                                blocked = true;
                                                break;
                                            }
                                            loc.add(0, 1, 0);
                                        }
                                        if (!blocked) {
                                            Location ploc = p.getLocation();
                                            if (ploc == null) return;
                                            for (Player all : plugin.getGame().getPlayersInLobby(s)) {
                                                all.sendMessage(Messages.AirdropFallingAt(ploc.getBlockX() + " " + ploc.getBlockY() + " " + ploc.getBlockZ()));
                                            }
                                            p.sendMessage(Messages.AirdropFallingHere());
                                            BlockData b = Material.DRIED_KELP_BLOCK.createBlockData();
                                            Location spawnLoc = new Location(p.getWorld(), ploc.getX(), ploc.getY() + 100, ploc.getZ());
                                            p.getWorld().spawn(spawnLoc, FallingBlock.class, fallingBlock -> fallingBlock.setBlockData(b));
                                            try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) lobby.addPlacedBlock(ploc, b.getMaterial()); } catch (NumberFormatException ignored) { }
                                            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                                        }
                                    }
                                }
                            
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.MILK_BUCKET) {
                            
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
                                    plugin.clearEffects(p);
                                    p.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
                                }
                            
                        }
                        if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                            
                                String s = null;
                                for (int ii = 1; ii <= 27; ii++) {
                                    if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                        s = Integer.toString(ii);
                                    }
                                }
                                if (plugin.getLobbyStateManager().getGameState(s) == GameStates.INGAME) {
                                    Player result = null;
                                    double lastDistance = Double.MAX_VALUE;
                                    String playerTeam_p = null;
                                    try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) playerTeam_p = lobby.getPlayerTeam(p); } catch (NumberFormatException ignored) { }
                                    for (Player cp : p.getWorld().getPlayers()) {
                                        String playerTeam_cp = null;
                                        try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) playerTeam_cp = lobby.getPlayerTeam(cp); } catch (NumberFormatException ignored) { }
                                        if (plugin.getGame().getPlayerLobby(cp) != null && Objects.equals(playerTeam_p, playerTeam_cp)) {
                                            if (p == cp) {
                                                continue;
                                            }
                                            Location pl = p.getLocation();
                                            Location cl = cp.getLocation();
                                            if (pl == null || cl == null) continue;
                                            double distance = pl.distance(cl);
                                            if (distance < lastDistance) {
                                                lastDistance = distance;
                                                result = cp;
                                            }
                                        }
                                    }
                                    if (result != null) {
                                        p.sendActionBar(Messages.PlayerFinderDistance((int) lastDistance));
                                    } else {
                                        p.sendActionBar(Messages.NoPlayerFound()); 
                                    }
                                }
                            
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                        
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (plugin.getLobbyStateManager().getGameState(s) == GameStates.WAITING || plugin.getLobbyStateManager().getGameState(s) == GameStates.PREPARING) {
                                Map<String, Integer> lobbyVotesMap = new HashMap<>();
                                try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) lobbyVotesMap = lobby.getVotingMap(); } catch (NumberFormatException ignored) { }
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                if (randombarriermeta == null) {
                                    return;
                                }
                                randombarriermeta.displayName(Messages.RandomLabel());
                                    ArrayList<Component> randomlore = new ArrayList<>();
                                    randomlore.add(0, Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(lobbyVotesMap.getOrDefault("Random", 0))).color(NamedTextColor.AQUA)));
                                    randombarriermeta.lore(randomlore);
                                if (!randombarrier.setItemMeta(randombarriermeta)) {
                                    return;
                                }
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ArenaSelectorTitle());
                                if (inv == null) return;
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (String all : lobbyVotesMap.keySet()) {
                                    if (!all.matches("Random")) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        arenalore.add(0, Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(lobbyVotesMap.getOrDefault(all, 0))).color(NamedTextColor.AQUA)));
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        if (arenamapmeta == null) {
                                            continue;
                                        }
                                        arenamapmeta.displayName(Component.text(all).color(NamedTextColor.AQUA));
                                        arenamapmeta.lore(arenalore);
                                        if (!arenamap.setItemMeta(arenamapmeta)) {
                                            continue;
                                        }
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
                                if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (plugin.getLobbyStateManager().getGameState(s) == GameStates.WAITING || plugin.getLobbyStateManager().getGameState(s) == GameStates.PREPARING) {
                                Map<Integer, Integer> lobbyTeamsMap = new HashMap<>();
                                try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) lobbyTeamsMap = lobby.getTeamsMap(); } catch (NumberFormatException ignored) { }
                                Map<Player, String> lobbyTeamPlayerNamesMap = new HashMap<>();
                                try { Lobby lobby = plugin.getGame().getLobby(Integer.parseInt(s)); if (lobby != null) lobbyTeamPlayerNamesMap = lobby.getTeamPlayerNamesMap(); } catch (NumberFormatException ignored) { }
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                if (randombarriermeta == null) {
                                    return;
                                }
                                randombarriermeta.displayName(Messages.RandomLabel());
                                if (!randombarrier.setItemMeta(randombarriermeta)) {
                                    return;
                                }
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.SelectorTeam());
                                if (inv == null) return;
                                inv.setItem(0, randombarrier);
                                int slot = 1;
                                for (Integer all : lobbyTeamsMap.keySet()) {
                                    ArrayList<Component> arenalore = new ArrayList<>();
                                    arenalore.add(0, Component.text(Messages.raw("players.label", "Players") + ": ").color(NamedTextColor.GREEN).append(Component.text(String.valueOf(lobbyTeamsMap.getOrDefault(all, 0))).color(NamedTextColor.AQUA)));
                                    ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    if (arenamapmeta == null) {
                                        continue;
                                    }
                                    arenamapmeta.displayName(Component.text(Integer.toString(all)).color(NamedTextColor.AQUA));
                                    for (Player temp : lobbyTeamPlayerNamesMap.keySet()) {
                                        if (lobbyTeamPlayerNamesMap.getOrDefault(temp, "").equals(Integer.toString(all)) && temp != null) {
                                            arenalore.add(Component.text(temp.getName()).color(NamedTextColor.GRAY));
                                        }
                                    }
                                    arenamapmeta.lore(arenalore);
                                    if (!arenamap.setItemMeta(arenamapmeta)) {
                                        continue;
                                    }
                                    inv.setItem(slot, arenamap);
                                    slot++;
                                }
                                p.openInventory(inv);
                            }
                        
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.ENDER_CHEST) {
                        
                            String s = null;
                            for (int ii = 1; ii <= 27; ii++) {
                                if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                    s = Integer.toString(ii);
                                }
                            }
                            if (plugin.getLobbyStateManager().getGameState(s) == GameStates.WAITING || plugin.getLobbyStateManager().getGameState(s) == GameStates.PREPARING) {
                                ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                                ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                if (randombarriermeta == null) {
                                    return;
                                }
                                randombarriermeta.displayName(Messages.RandomLabel());
                                if (!randombarrier.setItemMeta(randombarriermeta)) {
                                    return;
                                }
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.KitSelector());
                                if (inv == null) return;
                                inv.setItem(0, randombarrier);
                                for (int i = 1; i <= plugin.getConfigManager().getActiveKits(); i++) {
                                    ItemStack arenamap = new ItemStack(Material.ARMOR_STAND);
                                    ItemMeta arenamapmeta = arenamap.getItemMeta();
                                    if (arenamapmeta == null) {
                                        continue;
                                    }
                                    arenamapmeta.displayName(Component.text(Settings.kitdata.getString("pg.kits." + i + ".name")));
                                    if (!arenamap.setItemMeta(arenamapmeta)) {
                                        continue;
                                    }
                                    inv.setItem(i, arenamap);
                                }
                                p.openInventory(inv);
                            }
                        
                    }
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.MAGMA_CREAM) {
                    
                        String s = null;
                        for (int ii = 1; ii <= 27; ii++) {
                            if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                                s = Integer.toString(ii);
                            }
                        }
                        if (plugin.getLobbyStateManager().getGameState(s) == GameStates.WAITING || plugin.getLobbyStateManager().getGameState(s) == GameStates.PREPARING) {
                            plugin.onLeaveLobby(p, s);
                        }
                    
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.EMERALD) {
                    if (p.hasPermission("pg.stats")) {
                        int wins = plugin.getDatabaseManager().getWins(p.getUniqueId().toString());
                        int losses = plugin.getDatabaseManager().getLosses(p.getUniqueId().toString());
                        int rounds = plugin.getDatabaseManager().getRounds(p.getUniqueId().toString());
                        int kills = plugin.getDatabaseManager().getKills(p.getUniqueId().toString());
                        int deaths = plugin.getDatabaseManager().getDeaths(p.getUniqueId().toString());
                        double kd = plugin.getDatabaseManager().getKD(p.getUniqueId().toString());
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
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.STICK) {
                        if (isNamedItem(p, Material.STICK, Messages.SetupAddDeleteLobbyLabel())) {
                            if (p.hasPermission("pg.setup")) {
                                Location lploc = p.getLocation();
                                if (lploc == null) return;
                                var world = lploc.getWorld();
                                if (world == null) return;
                                plugin.getConfig().set("pg.Lobby.world", world.getName());
                                plugin.getConfig().set("pg.Lobby.coords", lploc);
                                plugin.saveConfig();
                                p.sendMessage(Messages.LobbySuccessSet());
                            }
                            if (p.hasPermission("pg.setup")) {
                                
                                    p.getInventory().clear();
                                    plugin.getSetupStateManager().setAddlobby(true);
                                    e.setCancelled(true);
                                    p.sendMessage(Messages.LobbyEnabled());
                                
                            }
                        } else if (isNamedItem(p, Material.STICK, Messages.SetupAddDeleteArenaLabel())) {
                            p.getInventory().clear();
                            plugin.getSetupStateManager().setAddarena(true);
                            p.sendMessage(Messages.TypeArenaNameAdd());
                        } else if (isNamedItem(p, Material.STICK, Messages.SetupAddDeleteSpawnLabel())) {
                            if (handleSetupSpawnAction(p, true)) {
                                e.setCancelled(true);
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (isNamedItem(p, Material.CLOCK, Messages.ChooseLobbyLabel())) {
                            
                                Inventory inv = Bukkit.createInventory(null, 9 * 3, Messages.ChooseLobbyTitle());
                                if (inv == null) return;
                                for (int slot = 1; slot <= 27; slot++) {
                                    if (Settings.lobbies.contains("pg.lobbies." + slot)) {
                                        ArrayList<Component> arenalore = new ArrayList<>();
                                        ItemStack arenamap = new ItemStack(Material.MAP);
                                        ItemMeta arenamapmeta = arenamap.getItemMeta();
                                        if (arenamapmeta == null) {
                                            continue;
                                        }
                                        arenamapmeta.displayName(Component.text(Integer.toString(slot)));
                                        arenamapmeta.lore(arenalore);
                                        if (!arenamap.setItemMeta(arenamapmeta)) {
                                            continue;
                                        }
                                        inv.setItem(slot - 1, arenamap);
                                    }
                                }
                                p.openInventory(inv);
                            
                        } else if (isNamedItem(p, Material.CLOCK, Messages.ChooseArenaLabel())) {
                            openChooseArenaInventory(p, resolveSetupLobby(p));
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.OAK_SIGN) {
                        ItemMeta meta = p.getInventory().getItemInMainHand().getItemMeta();
                        if (meta != null && meta.displayName() != null && meta.displayName().equals(Messages.SetupJoinSignLabel())) {
                            plugin.getSetupHandler().setJoinSign(p);
                            e.setCancelled(true);
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.BARRIER) {
                        if (isNamedItem(p, Material.BARRIER, Messages.SetupLeaveModeLabel())) {
                            plugin.getSetupHandler().exitSetup(p);
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (plugin.getConfigManager().isGameServer()) {
            if (plugin.getGame().getActivePlayers().contains(p) || plugin.getGame().getPlayerLobby(p) != null) {
                
                    String s = null;
                    for (int ii = 1; ii <= 27; ii++) {
                        if (Objects.equals(plugin.getGame().getPlayerLobby(p), Integer.toString(ii))) {
                            s = Integer.toString(ii);
                        }
                    }
                    GameStates state = plugin.getLobbyStateManager().getGameState(s); e.setCancelled(state != GameStates.INGAME && !Objects.equals(plugin.getGame().getPlayerLobby(p), s));
                
            }
        }
    }
}
