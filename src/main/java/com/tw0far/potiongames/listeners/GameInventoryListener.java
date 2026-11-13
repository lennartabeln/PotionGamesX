package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.PotionGamesX;
import com.tw0far.potiongames.models.GameStates;
import com.tw0far.potiongames.models.Kit;
import com.tw0far.potiongames.models.Lobby;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.models.Participant;
import com.tw0far.potiongames.models.Settings;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Inventory clicks for the in-game GUIs: arena voting, team selection,
 * kit selection and the shop.
 */
public class GameInventoryListener implements Listener {
    private final PotionGamesX plugin;

    public GameInventoryListener(PotionGamesX plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) {
            return;
        }
        // Protect every PotionGames GUI by title, even when the player left the
        // lobby while it was still open (otherwise its items can be taken).
        if (isGameGui(e.getView())) {
            e.setCancelled(true);
        }
        String s = plugin.getGame().getPlayerLobby(p);
        if (s != null) {
            GameStates lobbyState = plugin.getLobbyStateManager().getGameState(s);
            boolean canBuild = plugin.getLobbyStateManager().isBuildAllowed(s);
            if ((lobbyState == GameStates.WAITING || lobbyState == GameStates.PREPARING) && !canBuild) {
                handleArenaVoting(e, p, s);
                handleTeamSelection(e, p, s);
                handleKitSelection(e, p, s);
            }
        }
        handleShop(e, p, s);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        if (isGameGui(e.getView())) {
            e.setCancelled(true);
        }
    }

    /** True for every inventory this plugin opens as a menu (loot chests excluded). */
    private boolean isGameGui(InventoryView view) {
        Component title = view.title();
        return title.equals(Messages.ArenaSelectorTitle())
                || title.equals(Messages.SelectorTeamTitle())
                || title.equals(Messages.KitSelector())
                || title.equals(Messages.ShopTitle())
                || title.equals(Messages.LobbyListTitle())
                || title.equals(Messages.ChooseArenaTitle())
                || title.equals(Messages.ChooseLobbyTitle());
    }

    private void handleArenaVoting(InventoryClickEvent e, Player p, String s) {
        if (!e.getView().title().equals(Messages.ArenaSelectorTitle())) {
            return;
        }
        e.setCancelled(true);
        String displayname = InteractionSupport.plainName(e.getCurrentItem());
        if (displayname == null) {
            return;
        }

        p.closeInventory();
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby != null) {
            // Single source of truth for votes: the ArenaStateManager via Lobby#recordVote
            lobby.recordVote(p, displayname);
        }

        // Send feedback messages
        p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f);
        p.sendMessage(Messages.ArenaSelector());
        p.sendMessage(Settings.prefix.append(Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(displayname).color(NamedTextColor.LIGHT_PURPLE)));
        p.sendMessage(Settings.prefix.append(Component.text(Messages.VoteText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(String.valueOf(plugin.getArenaStateManager().getLobbyVoteCount(s, displayname))).color(NamedTextColor.AQUA)));
        p.sendMessage(Messages.ArenaSelector());
    }

    private void handleTeamSelection(InventoryClickEvent e, Player p, String s) {
        if (!e.getView().title().equals(Messages.SelectorTeamTitle())) {
            return;
        }
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby == null || !lobby.isActivateTeams()) {
            return;
        }
        e.setCancelled(true);
        String displayname = InteractionSupport.plainName(e.getCurrentItem());
        if (displayname == null) {
            return;
        }
        // The lobby owns the team size and the capacity check; the selector and
        // the round logic must not drift apart.
        int maxTeamPlayers = Math.max(1, lobby.getTeamSize());

        if (displayname.equals(Messages.RandomText())) {
            assignRandomTeam(lobby, p, maxTeamPlayers);
            return;
        }
        if (!isNumericTeamId(displayname)) {
            return;
        }
        assignSpecificTeam(lobby, p, Integer.parseInt(displayname), maxTeamPlayers);
    }

    private boolean isNumericTeamId(String displayname) {
        try {
            Integer.parseInt(displayname);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Picks a random team with free capacity. Teams without space are skipped,
     * so the player is never assigned to a full team.
     */
    private void assignRandomTeam(Lobby lobby, Player p, int maxTeamPlayers) {
        List<Integer> available = lobby.getAvailableTeams(maxTeamPlayers);
        if (available.isEmpty()) {
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1f, 1f);
            p.sendMessage(Messages.SelectorTeam());
            p.sendMessage(Messages.TeamAlreadyFull());
            p.sendMessage(Messages.SelectorTeam());
            return;
        }
        int rndTeam = available.get(new Random().nextInt(available.size()));
        assignSpecificTeam(lobby, p, rndTeam, maxTeamPlayers);
    }

    private void assignSpecificTeam(Lobby lobby, Player p, int teamId, int maxTeamPlayers) {
        if (!lobby.assignTeam(p, teamId, maxTeamPlayers)) {
            p.closeInventory();
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1f, 1f);
            p.sendMessage(Messages.SelectorTeam());
            p.sendMessage(Messages.TeamAlreadyFull());
            p.sendMessage(Messages.SelectorTeam());
            return;
        }
        p.closeInventory();
        String teamName = Integer.toString(teamId);
        p.sendMessage(Messages.SelectorTeam());
        p.sendMessage(Settings.prefix.append(Component.text(Messages.TeamNowInText() + ": ").color(NamedTextColor.GREEN)).append(Component.text(teamName).color(NamedTextColor.LIGHT_PURPLE)));
        p.sendMessage(Settings.prefix.append(Component.text(Messages.PlayersText() + ": ").color(NamedTextColor.GREEN))
                .append(Component.text(String.valueOf(lobby.getTeamCounts().getOrDefault(teamId, 0))).color(NamedTextColor.AQUA))
                .append(Component.text("/").color(NamedTextColor.GRAY))
                .append(Component.text(String.valueOf(maxTeamPlayers)).color(NamedTextColor.AQUA)));
        p.sendMessage(Messages.SelectorTeam());
        p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f);
        applyTeamScoreboardValue(p, teamName);
    }

    private void applyTeamScoreboardValue(Player p, String teamName) {
        if (plugin.getConfigManager().isActivateScoreboard() && plugin.getScoreboardManager() != null) {
            plugin.getScoreboardManager().updateTeamValue(p, teamName);
        }
    }

    private void handleKitSelection(InventoryClickEvent e, Player p, String s) {
        if (!e.getView().title().equals(Messages.KitSelector())) {
            return;
        }
        e.setCancelled(true);
        String displayname = InteractionSupport.plainName(e.getCurrentItem());
        if (displayname == null) {
            return;
        }
        Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
        if (lobby == null || !lobby.isActivateKits()) return;
        Participant participant = lobby.getParticipant(p);
        if (participant == null) return;
        int activeKits = plugin.getConfigManager().getActiveKits();
        if (displayname.equals(Messages.RandomText())) {
            if (activeKits < 1) return;
            int randomKitIndex = new Random().nextInt(activeKits) + 1;
            String kitName = Settings.kitdata.getString("pg.kits." + randomKitIndex + ".name");
            if (kitName != null) {
                participant.setKit(new Kit(kitName));
                p.closeInventory();
                p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f);
                p.sendMessage(Messages.KitNowHave(kitName));
            }
        } else {
            for (int i = 1; i <= activeKits; i++) {
                String kitName = Settings.kitdata.getString("pg.kits." + i + ".name");
                if (kitName != null && kitName.equals(displayname)) {
                    participant.setKit(new Kit(kitName));
                    p.closeInventory();
                    p.playSound(p.getLocation(), Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f);
                    p.sendMessage(Messages.KitNowHave(kitName));
                    break;
                }
            }
        }
    }

    private void handleShop(InventoryClickEvent e, Player p, String s) {
        if (!e.getView().title().equals(Messages.ShopTitle())) {
            return;
        }
        // The shop GUI is only open during a running game; always protect its items
        e.setCancelled(true);
        if (s == null) {
            return;
        }
        GameStates state = plugin.getLobbyStateManager().getGameState(s);
        if (state != GameStates.INGAME && state != GameStates.DEATHMATCH) {
            return;
        }
        // Only clicks inside the shop itself may buy something
        if (e.getClickedInventory() == null || !e.getClickedInventory().equals(e.getView().getTopInventory())) {
            return;
        }
        String displayname = InteractionSupport.plainName(e.getCurrentItem());
        if (displayname == null) {
            return;
        }

        // Count the player's actual coins and bottles
        ItemStack bottleItem = plugin.getBottle();
        ItemStack coinItem = plugin.getCoin();
        int bottles = 0;
        int coins = 0;
        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null) continue;
            if (bottleItem != null && item.getType() == bottleItem.getType()) bottles += item.getAmount();
            if (coinItem != null && item.getType() == coinItem.getType()) coins += item.getAmount();
        }

        var itemStateManager = plugin.getItemStateManager();
        List<String> shopItems = new ArrayList<>(itemStateManager.getShopItems());
        // Hidden entries must not be buyable either (see pg.activePotions).
        int activePotions = plugin.getConfigManager().getActivePotions();
        int entries = activePotions > 0 ? Math.min(shopItems.size(), activePotions) : shopItems.size();
        for (int idx = 0; idx < entries; idx++) {
            if (!displayname.equals(shopItems.get(idx))) {
                continue;
            }

            Lobby lobby = plugin.getGame().getLobby(InteractionSupport.parseIntSafe(s));
            String kitName = lobby != null && lobby.getParticipant(p) != null && lobby.getParticipant(p).getKit() != null
                ? lobby.getParticipant(p).getKit().getName() : null;
            int coinamount = itemStateManager.getShopPrice(idx, kitName);

            if (bottles < 1) {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1f, 1f);
                p.sendMessage(Messages.YouNotEmptyBottle());
                break;
            }
            if (coins < coinamount) {
                p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1f, 1f);
                p.sendMessage(Messages.YouNotEnoughCoins());
                break;
            }

            ItemStack potionType = itemStateManager.getShopPotionType(idx);
            PotionEffect shopPotion = itemStateManager.getShopPotion(idx);
            if (potionType == null || shopPotion == null) {
                break;
            }
            coins -= coinamount;

            ItemStack potionItem = new ItemStack(potionType);
            PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
            if (potionMeta == null) {
                break;
            }
            potionMeta.addCustomEffect(new PotionEffect(shopPotion.getType(), shopPotion.getDuration(), shopPotion.getAmplifier(), shopPotion.isAmbient(), shopPotion.hasParticles(), shopPotion.hasIcon()), true);
            potionMeta.displayName(Component.text(shopItems.get(idx)));
            potionItem.setItemMeta(potionMeta);
            p.getInventory().addItem(potionItem);
            for (int k = 0; k < coinamount; k++) {
                p.getInventory().removeItem(plugin.getCoin());
            }
            p.getInventory().removeItem(plugin.getBottle());
            break;
        }
    }
}
