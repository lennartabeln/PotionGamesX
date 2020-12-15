package com.asga.potiongames.events;

import com.asga.potiongames.gamestates.GameStates;
import com.asga.potiongames.main.PotionGames;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Events implements Listener {
    private final PotionGames pg;
    private int amount;
    private int bottle;
    private final String name1 = "JUMP";
    private final PotionEffectType potion1 = PotionEffectType.JUMP;
    private final Material material1 = Material.POTION;
    private final int time1 = 60;
    private final String kit1 = "Looter";
    private final String name2 = "DAMAGE_RESISTANCE";
    private final PotionEffectType potion2 = PotionEffectType.DAMAGE_RESISTANCE;
    private final Material material2 = Material.POTION;
    private final int time2 = 60;
    private final String kit2 = "Tank";
    private final String name3 = "SPEED";
    private final PotionEffectType potion3 = PotionEffectType.SPEED;
    private final Material material3 = Material.POTION;
    private final int time3 = 60;
    private final String kit3 = "Looter";
    private final String name4 = "ABSORPTION";
    private final PotionEffectType potion4 = PotionEffectType.ABSORPTION;
    private final Material material4 = Material.POTION;
    private final int time4 = 60;
    private final String kit4 = "Tank";
    private final String name5 = "FIRE_RESISTANCE";
    private final PotionEffectType potion5 = PotionEffectType.FIRE_RESISTANCE;
    private final Material material5 = Material.POTION;
    private final int time5 = 60;
    private final String kit5 = "Tank";
    private final String name6 = "HEAL";
    private final PotionEffectType potion6 = PotionEffectType.HEAL;
    private final Material material6 = Material.POTION;
    private final int time6 = 60;
    private final String kit6 = "Healer";
    private final String name7 = "HEALTH_BOOST";
    private final PotionEffectType potion7 = PotionEffectType.HEALTH_BOOST;
    private final Material material7 = Material.POTION;
    private final int time7 = 60;
    private final String kit7 = "Healer";
    private final String name8 = "INVISIBILITY";
    private final PotionEffectType potion8 = PotionEffectType.INVISIBILITY;
    private final Material material8 = Material.POTION;
    private final int time8 = 60;
    private final String kit8 = "Ghost";
    private final String name9 = "REGENERATION";
    private final PotionEffectType potion9 = PotionEffectType.REGENERATION;
    private final Material material9 = Material.POTION;
    private final int time9 = 60;
    private final String kit9 = "Healer";
    private final String name10 = "SATURATION";
    private final PotionEffectType potion10 = PotionEffectType.SATURATION;
    private final Material material10 = Material.POTION;
    private final int time10 = 60;
    private final String kit10 = "Looter";
    private final String name11 = "INCREASE_DAMAGE";
    private final PotionEffectType potion11 = PotionEffectType.INCREASE_DAMAGE;
    private final Material material11 = Material.POTION;
    private final int time11 = 60;
    private final String kit11 = "Fighter";
    private final String name12 = "DOLPHINS_GRACE";
    private final PotionEffectType potion12 = PotionEffectType.DOLPHINS_GRACE;
    private final Material material12 = Material.POTION;
    private final int time12 = 60;
    private final String kit12 = "Looter";
    private final String name13 = "NIGHT_VISION";
    private final PotionEffectType potion13 = PotionEffectType.NIGHT_VISION;
    private final Material material13 = Material.POTION;
    private final int time13 = 60;
    private final String kit13 = "Ghost";
    private final String name14 = "WATER_BREATHING";
    private final PotionEffectType potion14 = PotionEffectType.WATER_BREATHING;
    private final Material material14 = Material.POTION;
    private final int time14 = 60;
    private final String kit14 = "Ghost";
    private final String name15 = "WEAKNESS";
    private final PotionEffectType potion15 = PotionEffectType.WEAKNESS;
    private final Material material15 = Material.SPLASH_POTION;
    private final int time15 = 60;
    private final String kit15 = "Fighter";
    private final String name16 = "WITHER";
    private final PotionEffectType potion16 = PotionEffectType.WITHER;
    private final Material material16 = Material.SPLASH_POTION;
    private final int time16 = 60;
    private final String kit16 = "Fighter";
    private final String name17 = "GLOWING";
    private final PotionEffectType potion17 = PotionEffectType.GLOWING;
    private final Material material17 = Material.SPLASH_POTION;
    private final int time17 = 60;
    private final String kit17 = "Fighter";
    private final String name18 = "BLINDNESS";
    private final PotionEffectType potion18 = PotionEffectType.BLINDNESS;
    private final Material material18 = Material.SPLASH_POTION;
    private final int time18 = 60;
    private final String kit18 = "Fighter";
    private final String name19 = "CONFUSION";
    private final PotionEffectType potion19 = PotionEffectType.CONFUSION;
    private final Material material19 = Material.SPLASH_POTION;
    private final int time19 = 60;
    private final String kit19 = "Fighter";
    private int cost1 = 4;
    private int cost2 = 4;
    private int cost3 = 4;
    private int cost4 = 4;
    private int cost5 = 4;
    private int cost6 = 4;
    private int cost7 = 4;
    private int cost8 = 4;
    private int cost9 = 4;
    private int cost10 = 4;
    private int cost11 = 4;
    private int cost12 = 4;
    private int cost13 = 4;
    private int cost14 = 4;
    private int cost15 = 4;
    private int cost16 = 4;
    private int cost17 = 4;
    private int cost18 = 4;
    private int cost19 = 4;

    public Events(PotionGames pg) {
        this.pg = pg;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        e.getRecipients().clear();
        if (pg.playerChannel.get(p).equals("Local")) {
            pg.pgPlayers.forEach(ent -> {
                if (ent != null) {
                    e.getRecipients().add(ent);
                }
            });
            pg.specPlayers.forEach(ent -> {
                if (ent != null) {
                    e.getRecipients().add(ent);
                }
            });
            e.getRecipients().add(p);
            String message = ChatColor.WHITE + e.getMessage();
            if (!p.isOp()) {
                if (pg.pgPlayers.contains(p)) {
                    e.setCancelled(true);
                    for (Player pgchat : pg.pgPlayers) {
                        pgchat.sendMessage(pg.prefix + p.getDisplayName() + ": " + message);
                    }
                    for (Player pgchat : pg.specPlayers) {
                        pgchat.sendMessage(pg.prefix + p.getDisplayName() + ": " + message);
                    }
                } else if (pg.specPlayers.contains(p)) {
                    e.setCancelled(true);
                }
            } else {
                if (pg.pgPlayers.contains(p)) {
                    e.setCancelled(true);
                    for (Player pgchat : pg.pgPlayers) {
                        pgchat.sendMessage(pg.prefix + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                    }
                    for (Player pgchat : pg.specPlayers) {
                        pgchat.sendMessage(pg.prefix + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                    }
                } else if (pg.specPlayers.contains(p)) {
                    e.setCancelled(true);
                    for (Player pgchat : pg.pgPlayers) {
                        pgchat.sendMessage(pg.prefix + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + pg.chat.get(8) + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                    }
                    for (Player pgchat : pg.specPlayers) {
                        pgchat.sendMessage(pg.prefix + ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + pg.chat.get(8) + ChatColor.DARK_GRAY + "] " + ChatColor.DARK_AQUA + p.getDisplayName() + ": " + message);
                    }
                }
            }
            return;
        }
        pg.getChannel(p).forEach(player -> e.getRecipients().add(player));
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        switch (e.getBlock().getType()) {
            case ACACIA_LEAVES:
            case BIRCH_LEAVES:
            case DARK_OAK_LEAVES:
            case JUNGLE_LEAVES:
            case OAK_LEAVES:
            case SPRUCE_LEAVES:
                e.setCancelled(true);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        switch (e.getBlock().getType()) {
            case ICE:
            case SNOW:
                e.setCancelled(true);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        if (e.getBucket() != Material.WATER_BUCKET && pg.pgPlayers.contains(e.getPlayer())
                || e.getBucket() != Material.LAVA_BUCKET && pg.pgPlayers.contains(e.getPlayer())) {
            Block block = e.getBlockClicked().getRelative(e.getBlockFace());
            Location loc = e.getBlockClicked().getRelative(e.getBlockFace()).getLocation();
            pg.getLiquidPlaced().put(loc, block);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (pg.pgPlayers.contains(e.getPlayer())) {
            if (!pg.isBuild()) {
                if (pg.getGamestate() == GameStates.INGAME) {
                    if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE
                            || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.GRASS
                            || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH
                            || e.getBlock().getType() == Material.ACACIA_LEAVES
                            || e.getBlock().getType() == Material.BIRCH_LEAVES
                            || e.getBlock().getType() == Material.DARK_OAK_LEAVES
                            || e.getBlock().getType() == Material.JUNGLE_LEAVES
                            || e.getBlock().getType() == Material.OAK_LEAVES
                            || e.getBlock().getType() == Material.SPRUCE_LEAVES
                            || e.getBlock().getType() == Material.WARPED_FUNGUS
                            || e.getBlock().getType() == Material.CRIMSON_FUNGUS
                            || e.getBlock().getType() == Material.BROWN_MUSHROOM
                            || e.getBlock().getType() == Material.RED_MUSHROOM) {
                        pg.getPlacedBlocks().put(e.getBlock().getLocation(), e.getBlock().getType());
                        e.setCancelled(false);
                    } else {
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (pg.pgPlayers.contains(e.getPlayer())) {
            if (!pg.isBuild()) {
                if (pg.getGamestate() == GameStates.INGAME) {
                    if (e.getBlock().getType() == Material.COBWEB || e.getBlock().getType() == Material.FIRE
                            || e.getBlock().getType() == Material.CAKE || e.getBlock().getType() == Material.GRASS
                            || e.getBlock().getType() == Material.TALL_GRASS || e.getBlock().getType() == Material.DEAD_BUSH
                            || e.getBlock().getType() == Material.ACACIA_LEAVES
                            || e.getBlock().getType() == Material.BIRCH_LEAVES
                            || e.getBlock().getType() == Material.DARK_OAK_LEAVES
                            || e.getBlock().getType() == Material.JUNGLE_LEAVES
                            || e.getBlock().getType() == Material.OAK_LEAVES
                            || e.getBlock().getType() == Material.SPRUCE_LEAVES
                            || e.getBlock().getType() == Material.WARPED_FUNGUS
                            || e.getBlock().getType() == Material.CRIMSON_FUNGUS
                            || e.getBlock().getType() == Material.BROWN_MUSHROOM
                            || e.getBlock().getType() == Material.RED_MUSHROOM) {
                        pg.getBreakedBlocks().put(e.getBlock().getLocation(), e.getBlock().getType());
                        e.setCancelled(false);
                    } else {
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (p.getKiller() != null) {
            pg.addDeaths(p.getUniqueId().toString(), 1);
            pg.addLosts(p.getUniqueId().toString(), 1);
            pg.addKills(p.getKiller().getUniqueId().toString(), 1);
        } else {
            pg.addDeaths(p.getUniqueId().toString(), 1);
            pg.addLosts(p.getUniqueId().toString(), 1);
        }
        if (pg.pgPlayers.contains(p)) {
            e.setKeepLevel(true);
            pg.pgPlayers.remove(p);
            pg.specPlayers.add(p);
            if (pg.isActivateTeams()) {
                String teamname = "";
                for (int i = 1; i <= pg.getTeamAmount(); i++) {
                    if (pg.teamplayernames.containsKey(Integer.toString(i)) && pg.teamplayernames.containsValue(p)) {
                        teamname = String.valueOf(i);
                    }
                }
                pg.teamplayernames.remove(teamname, p);
                int teamamount = pg.teamplayers.get(teamname) - 1;
                pg.teamplayers.put(teamname, teamamount);
                if (pg.teamplayers.get(teamname) == 0) {
                    pg.teams.remove(teamname);
                }
            }
            int amountPlayers = pg.getPlayerAmount();
            int player = pg.pgPlayers.size();
            try {
                Player killer = p.getKiller();
                assert killer != null;
                killer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30 * 20, 0));
                if (pg.kitplayernames.containsKey("Rich Kid") && pg.kitplayernames.containsValue(p)) {
                    for (int i = 0; i < 10; i++)
                        killer.getInventory().addItem(pg.getCoin());
                } else {
                    for (int i = 0; i < 5; i++)
                        killer.getInventory().addItem(pg.getCoin());
                }
                e.setDeathMessage(pg.prefix + ChatColor.DARK_RED + p.getName() + ChatColor.GRAY + " " + pg.chat.get(9) + " " + ChatColor.DARK_GREEN + killer.getName() + " " + ChatColor.GRAY + "[" + ChatColor.AQUA + player + ChatColor.GRAY + "/" + ChatColor.AQUA + amountPlayers + ChatColor.GRAY + "]");
            } catch (Exception ex) {
                e.setDeathMessage(pg.prefix + ChatColor.DARK_RED + p.getName() + ChatColor.GRAY + " " + pg.chat.get(10) + " " + ChatColor.GRAY + "[" + ChatColor.AQUA + player + ChatColor.GRAY + "/" + ChatColor.AQUA + amountPlayers + ChatColor.GRAY + "]");
            }
            p.setGameMode(GameMode.SPECTATOR);
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setLevel(0);
            p.setExp(0);
            p.setFireTicks(0);
            p.setHealth(20);
            p.setFoodLevel(20);
            p.setCanPickupItems(false);
            p.setCollidable(false);
            p.getWorld().strikeLightning(p.getLocation());
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Player p = (Player) e.getEntity();
        if (pg.pgPlayers.contains(p)) {
            e.setCancelled(e.getDamager() instanceof LightningStrike && e.getEntity() instanceof Player || e.getDamager() instanceof Firework && e.getEntity() instanceof Player);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getHand() == EquipmentSlot.HAND) {
                if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPRUCE_WALL_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.ACACIA_WALL_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.BIRCH_WALL_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.DARK_OAK_WALL_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.JUNGLE_WALL_SIGN
                        || Objects.requireNonNull(e.getClickedBlock()).getType() == Material.OAK_WALL_SIGN) {
                    if (!pg.pgPlayers.contains(p) && !pg.specPlayers.contains(p)) {
                        Sign sign = (Sign) e.getClickedBlock().getState();
                        String line1 = sign.getLine(0);
                        if (line1.matches("PotionGames")) {
                            if (!pg.pgPlayers.contains(p) && !pg.specPlayers.contains(p)) {
                                pg.onJoin(p);
                            }
                        }
                    }
                }
            }
        }
        if (pg.pgPlayers.contains(p)) {
            if (e.getAction() == Action.PHYSICAL && Objects.requireNonNull(e.getClickedBlock()).getType() == Material.FARMLAND) {
                e.setCancelled(true);
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (Objects.requireNonNull(e.getClickedBlock()).getType() == Material.END_PORTAL_FRAME) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            if (!pg.chests.containsKey(e.getClickedBlock().getLocation())) {
                                Inventory inv;
                                inv = Bukkit.createInventory(p, 27, ChatColor.DARK_PURPLE + "Potion" + ChatColor.GOLD + "Games");
                                ArrayList<ItemStack> food1 = new ArrayList<>();
                                food1.add(new ItemStack(Material.CAKE, 1));
                                food1.add(new ItemStack(Material.BREAD, 3));
                                food1.add(new ItemStack(Material.PUMPKIN_PIE, 3));
                                food1.add(new ItemStack(Material.COOKIE, 3));
                                food1.add(new ItemStack(Material.BAKED_POTATO, 3));
                                ArrayList<ItemStack> food2 = new ArrayList<>();
                                food2.add(new ItemStack(Material.RABBIT_STEW, 1));
                                food2.add(new ItemStack(Material.MUSHROOM_STEW, 1));
                                food2.add(new ItemStack(Material.BEETROOT_SOUP, 1));
                                food2.add(new ItemStack(Material.GOLDEN_CARROT, 1));
                                ArrayList<ItemStack> armour1 = new ArrayList<>();
                                armour1.add(new ItemStack(Material.LEATHER_HELMET, 1));
                                armour1.add(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
                                armour1.add(new ItemStack(Material.LEATHER_LEGGINGS, 1));
                                armour1.add(new ItemStack(Material.LEATHER_BOOTS, 1));
                                ArrayList<ItemStack> armour2 = new ArrayList<>();
                                armour2.add(new ItemStack(Material.CHAINMAIL_HELMET, 1));
                                armour2.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
                                armour2.add(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
                                armour2.add(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
                                ArrayList<ItemStack> armour3 = new ArrayList<>();
                                armour3.add(new ItemStack(Material.GOLDEN_HELMET, 1));
                                armour3.add(new ItemStack(Material.GOLDEN_CHESTPLATE, 1));
                                armour3.add(new ItemStack(Material.GOLDEN_LEGGINGS, 1));
                                armour3.add(new ItemStack(Material.GOLDEN_BOOTS, 1));
                                ArrayList<ItemStack> armour4 = new ArrayList<>();
                                armour4.add(new ItemStack(Material.IRON_HELMET, 1));
                                armour4.add(new ItemStack(Material.IRON_CHESTPLATE, 1));
                                armour4.add(new ItemStack(Material.IRON_LEGGINGS, 1));
                                armour4.add(new ItemStack(Material.IRON_BOOTS, 1));
                                ArrayList<ItemStack> armour5 = new ArrayList<>();
                                armour5.add(new ItemStack(Material.DIAMOND_HELMET, 1));
                                armour5.add(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
                                armour5.add(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
                                armour5.add(new ItemStack(Material.DIAMOND_BOOTS, 1));
                                ArrayList<ItemStack> weapons1 = new ArrayList<>();
                                weapons1.add(new ItemStack(Material.FISHING_ROD, 1));
                                weapons1.add(new ItemStack(Material.BOW, 1));
                                weapons1.add(new ItemStack(Material.ARROW, 5));
                                weapons1.add(new ItemStack(Material.SPECTRAL_ARROW, 1));
                                weapons1.add(new ItemStack(Material.SHIELD, 1));
                                ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL, 1);
                                ItemMeta itemMeta = itemStack.getItemMeta();
                                if (itemMeta instanceof Damageable) {
                                    ((Damageable) itemMeta).setDamage(60);
                                }
                                itemStack.setItemMeta(itemMeta);
                                weapons1.add(itemStack);
                                weapons1.add(new ItemStack(Material.COBWEB, 3));
                                weapons1.add(new ItemStack(Material.WATER_BUCKET, 1));
                                weapons1.add(new ItemStack(Material.LAVA_BUCKET, 1));
                                weapons1.add(new ItemStack(Material.WOODEN_SWORD, 1));
                                weapons1.add(new ItemStack(Material.STONE_SWORD, 1));
                                weapons1.add(new ItemStack(Material.WOODEN_AXE, 1));
                                weapons1.add(new ItemStack(Material.STONE_AXE, 1));
                                ArrayList<ItemStack> weapons2 = new ArrayList<>();
                                weapons2.add(new ItemStack(Material.GOLDEN_SWORD, 1));
                                weapons2.add(new ItemStack(Material.IRON_SWORD, 1));
                                weapons2.add(new ItemStack(Material.GOLDEN_AXE, 1));
                                weapons2.add(new ItemStack(Material.IRON_AXE, 1));
                                weapons2.add(new ItemStack(Material.DIAMOND_SWORD, 1));
                                weapons2.add(new ItemStack(Material.DIAMOND_AXE, 1));
                                ArrayList<ItemStack> potions1 = new ArrayList<>();
                                potions1.add(new ItemStack(Material.GLASS_BOTTLE, 1));
                                ArrayList<ItemStack> potions2 = new ArrayList<>();
                                potions2.add(pg.getCoin());
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
                                        int item = rnd.nextInt(5);
                                        if (item < 3) {
                                            int item1 = rnd.nextInt(food1.size());
                                            inv.setItem(slot, food1.get(item1));
                                        } else if (item < 4) {
                                            int item1 = rnd.nextInt(potions1.size());
                                            inv.setItem(slot, potions1.get(item1));
                                        } else {
                                            int item1 = rnd.nextInt(potions2.size());
                                            inv.setItem(slot, potions2.get(item1));
                                        }
                                    } else if (roll < 30) {
                                        int item2 = rnd.nextInt(food2.size());
                                        inv.setItem(slot, food2.get(item2));
                                    } else if (roll < 45) {
                                        int item3 = rnd.nextInt(armour1.size());
                                        inv.setItem(slot, armour1.get(item3));
                                    } else if (roll < 60) {
                                        int item4 = rnd.nextInt(armour2.size());
                                        inv.setItem(slot, armour2.get(item4));
                                    } else if (roll < 67) {
                                        int item5 = rnd.nextInt(armour3.size());
                                        inv.setItem(slot, armour3.get(item5));
                                    } else if (roll < 72) {
                                        int item6 = rnd.nextInt(armour4.size());
                                        inv.setItem(slot, armour4.get(item6));
                                    } else if (roll < 75) {
                                        int item7 = rnd.nextInt(armour5.size());
                                        inv.setItem(slot, armour5.get(item7));
                                    } else if (roll < 95) {
                                        int item8 = rnd.nextInt(weapons1.size());
                                        inv.setItem(slot, weapons1.get(item8));
                                    } else {
                                        int item9 = rnd.nextInt(weapons2.size());
                                        inv.setItem(slot, weapons2.get(item9));
                                    }
                                }
                                pg.chests.put(e.getClickedBlock().getLocation(), inv);
                            }
                            p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                            if (p.getActivePotionEffects().isEmpty()) {
                                Random effect = new Random();
                                int max = 3;
                                int min = 0;
                                int diff = max - min;
                                int tries = effect.nextInt(diff + 1);
                                while (tries != 0) {
                                    tries--;
                                    ArrayList<PotionEffect> potions = new ArrayList<>();
                                    potions.add(new PotionEffect(PotionEffectType.SPEED, 40 * 20, 2));
                                    potions.add(new PotionEffect(PotionEffectType.SLOW, 40 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.HEAL, 1, 0));
                                    potions.add(new PotionEffect(PotionEffectType.HARM, 1, 0));
                                    potions.add(new PotionEffect(PotionEffectType.JUMP, 40 * 20, 3));
                                    potions.add(new PotionEffect(PotionEffectType.CONFUSION, 40 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 1));
                                    potions.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 60 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.INVISIBILITY, 40 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.HUNGER, 10 * 20, 1));
                                    potions.add(new PotionEffect(PotionEffectType.WEAKNESS, 60 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.POISON, 10 * 20, 1));
                                    potions.add(new PotionEffect(PotionEffectType.WITHER, 10 * 20, 1));
                                    potions.add(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 2));
                                    potions.add(new PotionEffect(PotionEffectType.GLOWING, 20 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.HEALTH_BOOST, 60 * 20, 2));
                                    potions.add(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 60 * 20, 2));
                                    potions.add(new PotionEffect(PotionEffectType.SATURATION, 40 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 60 * 20, 0));
                                    potions.add(new PotionEffect(PotionEffectType.WATER_BREATHING, 60 * 20, 2));
                                    int potion = effect.nextInt(potions.size());
                                    p.addPotionEffect(potions.get(potion));
                                }
                            }
                        }
                    }
                    if (e.getClickedBlock().getType() == Material.TARGET) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            if (!pg.chests.containsKey(e.getClickedBlock().getLocation())) {
                                Inventory inv;
                                inv = Bukkit.createInventory(p, 9, pg.prefix);
                                pg.chests.put(e.getClickedBlock().getLocation(), inv);
                                p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                                ItemStack arrow = new ItemStack(Material.ARROW);
                                ItemStack firebow = new ItemStack(Material.BOW);
                                ItemMeta firebowmeta = firebow.getItemMeta();
                                assert firebowmeta != null;
                                firebowmeta.setDisplayName(ChatColor.LIGHT_PURPLE + pg.chat.get(11));
                                firebow.setItemMeta(firebowmeta);
                                firebow.addEnchantment(Enchantment.ARROW_FIRE, 1);
                                inv.setItem(1, arrow);
                                inv.setItem(2, arrow);
                                inv.setItem(6, arrow);
                                inv.setItem(7, arrow);
                                inv.setItem(4, firebow);
                            } else {
                                p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                            }
                        }
                    }
                    if (e.getClickedBlock().getType() == Material.NETHERITE_BLOCK) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            if (!pg.chests.containsKey(e.getClickedBlock().getLocation())) {
                                Inventory inv;
                                inv = Bukkit.createInventory(p, 9, pg.prefix);
                                pg.chests.put(e.getClickedBlock().getLocation(), inv);
                                p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                                ItemStack ingot = new ItemStack(Material.NETHERITE_INGOT);
                                inv.setItem(1, ingot);
                                inv.setItem(3, ingot);
                                inv.setItem(5, ingot);
                                inv.setItem(7, ingot);
                            } else {
                                p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                            }
                        }
                    }
                    if (e.getClickedBlock().getType() == Material.COMPOSTER) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            for (ItemStack item : p.getInventory().getContents()) {
                                if (item != null) {
                                    if (item.getType() == pg.getCoin().getType())
                                        amount = item.getAmount();
                                    if (item.getType() == Material.GLASS_BOTTLE)
                                        bottle = item.getAmount();
                                }
                            }
                            Inventory inv;
                            inv = Bukkit.createInventory(p, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(49));
                            if (pg.kitplayernames.containsKey(kit1) && pg.kitplayernames.containsValue(p))
                                cost1 = cost1 / 2;
                            pg.chests.put(e.getClickedBlock().getLocation(), inv);
                            ItemStack randombarrier = new ItemStack(material1);
                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.setDisplayName(name1);
                            ArrayList<String> lore = new ArrayList<>();
                            lore.add(pg.chat.get(50) + ": " + time1);
                            lore.add(pg.chat.get(51) + ": " + cost1 + " " + pg.chat.get(52));
                            randombarriermeta.setLore(lore);
                            randombarrier.setItemMeta(randombarriermeta);
                            inv.setItem(0, randombarrier);

                            if (pg.kitplayernames.containsKey(kit2) && pg.kitplayernames.containsValue(p))
                                cost2 = cost2 / 2;
                            ItemStack randombarrier2 = new ItemStack(material2);
                            ItemMeta randombarriermeta2 = randombarrier2.getItemMeta();
                            assert randombarriermeta2 != null;
                            randombarriermeta2.setDisplayName(name2);
                            ArrayList<String> lore2 = new ArrayList<>();
                            lore2.add(pg.chat.get(50) + ": " + time2);
                            lore2.add(pg.chat.get(51) + ": " + cost2 + " " + pg.chat.get(52));
                            randombarriermeta2.setLore(lore2);
                            randombarrier2.setItemMeta(randombarriermeta2);
                            inv.setItem(1, randombarrier2);

                            if (pg.kitplayernames.containsKey(kit3) && pg.kitplayernames.containsValue(p))
                                cost3 = cost3 / 2;
                            ItemStack randombarrier3 = new ItemStack(material3);
                            ItemMeta randombarriermeta3 = randombarrier3.getItemMeta();
                            assert randombarriermeta3 != null;
                            randombarriermeta3.setDisplayName(name3);
                            ArrayList<String> lore3 = new ArrayList<>();
                            lore3.add(pg.chat.get(50) + ": " + time3);
                            lore3.add(pg.chat.get(51) + ": " + cost3 + " " + pg.chat.get(52));
                            randombarriermeta3.setLore(lore3);
                            randombarrier3.setItemMeta(randombarriermeta3);
                            inv.setItem(2, randombarrier3);

                            if (pg.kitplayernames.containsKey(kit4) && pg.kitplayernames.containsValue(p))
                                cost4 = cost4 / 2;
                            ItemStack randombarrier4 = new ItemStack(material4);
                            ItemMeta randombarriermeta4 = randombarrier4.getItemMeta();
                            assert randombarriermeta4 != null;
                            randombarriermeta4.setDisplayName(name4);
                            ArrayList<String> lore4 = new ArrayList<>();
                            lore4.add(pg.chat.get(50) + ": " + time4);
                            lore4.add(pg.chat.get(51) + ": " + cost4 + " " + pg.chat.get(52));
                            randombarriermeta4.setLore(lore4);
                            randombarrier4.setItemMeta(randombarriermeta4);
                            inv.setItem(3, randombarrier4);

                            if (pg.kitplayernames.containsKey(kit5) && pg.kitplayernames.containsValue(p))
                                cost5 = cost5 / 2;
                            ItemStack randombarrier5 = new ItemStack(material5);
                            ItemMeta randombarriermeta5 = randombarrier5.getItemMeta();
                            assert randombarriermeta5 != null;
                            randombarriermeta5.setDisplayName(name5);
                            ArrayList<String> lore5 = new ArrayList<>();
                            lore5.add(pg.chat.get(50) + ": " + time5);
                            lore5.add(pg.chat.get(51) + ": " + cost5 + " " + pg.chat.get(52));
                            randombarriermeta5.setLore(lore5);
                            randombarrier5.setItemMeta(randombarriermeta5);
                            inv.setItem(4, randombarrier5);

                            if (pg.kitplayernames.containsKey(kit6) && pg.kitplayernames.containsValue(p))
                                cost6 = cost6 / 2;
                            ItemStack randombarrier6 = new ItemStack(material6);
                            ItemMeta randombarriermeta6 = randombarrier6.getItemMeta();
                            assert randombarriermeta6 != null;
                            randombarriermeta6.setDisplayName(name6);
                            ArrayList<String> lore6 = new ArrayList<>();
                            lore6.add(pg.chat.get(50) + ": " + time6);
                            lore6.add(pg.chat.get(51) + ": " + cost6 + " " + pg.chat.get(52));
                            randombarriermeta6.setLore(lore6);
                            randombarrier6.setItemMeta(randombarriermeta6);
                            inv.setItem(5, randombarrier6);

                            if (pg.kitplayernames.containsKey(kit7) && pg.kitplayernames.containsValue(p))
                                cost7 = cost7 / 2;
                            ItemStack randombarrier7 = new ItemStack(material7);
                            ItemMeta randombarriermeta7 = randombarrier7.getItemMeta();
                            assert randombarriermeta7 != null;
                            randombarriermeta7.setDisplayName(name7);
                            ArrayList<String> lore7 = new ArrayList<>();
                            lore7.add(pg.chat.get(50) + ": " + time7);
                            lore7.add(pg.chat.get(51) + ": " + cost7 + " " + pg.chat.get(52));
                            randombarriermeta7.setLore(lore7);
                            randombarrier7.setItemMeta(randombarriermeta7);
                            inv.setItem(6, randombarrier7);

                            if (pg.kitplayernames.containsKey(kit8) && pg.kitplayernames.containsValue(p))
                                cost8 = cost8 / 2;
                            ItemStack randombarrier8 = new ItemStack(material8);
                            ItemMeta randombarriermeta8 = randombarrier8.getItemMeta();
                            assert randombarriermeta8 != null;
                            randombarriermeta8.setDisplayName(name8);
                            ArrayList<String> lore8 = new ArrayList<>();
                            lore8.add(pg.chat.get(50) + ": " + time8);
                            lore8.add(pg.chat.get(51) + ": " + cost8 + " " + pg.chat.get(52));
                            randombarriermeta8.setLore(lore8);
                            randombarrier8.setItemMeta(randombarriermeta8);
                            inv.setItem(7, randombarrier8);

                            if (pg.kitplayernames.containsKey(kit9) && pg.kitplayernames.containsValue(p))
                                cost9 = cost9 / 2;
                            ItemStack randombarrier9 = new ItemStack(material9);
                            ItemMeta randombarriermeta9 = randombarrier9.getItemMeta();
                            assert randombarriermeta9 != null;
                            randombarriermeta9.setDisplayName(name9);
                            ArrayList<String> lore9 = new ArrayList<>();
                            lore9.add(pg.chat.get(50) + ": " + time9);
                            lore9.add(pg.chat.get(51) + ": " + cost9 + " " + pg.chat.get(52));
                            randombarriermeta9.setLore(lore9);
                            randombarrier9.setItemMeta(randombarriermeta9);
                            inv.setItem(8, randombarrier9);

                            if (pg.kitplayernames.containsKey(kit10) && pg.kitplayernames.containsValue(p))
                                cost10 = cost10 / 2;
                            ItemStack randombarrier10 = new ItemStack(material10);
                            ItemMeta randombarriermeta10 = randombarrier10.getItemMeta();
                            assert randombarriermeta10 != null;
                            randombarriermeta10.setDisplayName(name10);
                            ArrayList<String> lore10 = new ArrayList<>();
                            lore10.add(pg.chat.get(50) + ": " + time10);
                            lore10.add(pg.chat.get(51) + ": " + cost10 + " " + pg.chat.get(52));
                            randombarriermeta10.setLore(lore10);
                            randombarrier10.setItemMeta(randombarriermeta10);
                            inv.setItem(9, randombarrier10);

                            if (pg.kitplayernames.containsKey(kit11) && pg.kitplayernames.containsValue(p))
                                cost11 = cost11 / 2;
                            ItemStack randombarrier11 = new ItemStack(material11);
                            ItemMeta randombarriermeta11 = randombarrier11.getItemMeta();
                            assert randombarriermeta11 != null;
                            randombarriermeta11.setDisplayName(name11);
                            ArrayList<String> lore11 = new ArrayList<>();
                            lore11.add(pg.chat.get(50) + ": " + time11);
                            lore11.add(pg.chat.get(51) + ": " + cost11 + " " + pg.chat.get(52));
                            randombarriermeta11.setLore(lore11);
                            randombarrier11.setItemMeta(randombarriermeta11);
                            inv.setItem(10, randombarrier11);

                            if (pg.kitplayernames.containsKey(kit12) && pg.kitplayernames.containsValue(p))
                                cost12 = cost12 / 2;
                            ItemStack randombarrier12 = new ItemStack(material12);
                            ItemMeta randombarriermeta12 = randombarrier12.getItemMeta();
                            assert randombarriermeta12 != null;
                            randombarriermeta12.setDisplayName(name12);
                            ArrayList<String> lore12 = new ArrayList<>();
                            lore12.add(pg.chat.get(50) + ": " + time12);
                            lore12.add(pg.chat.get(51) + ": " + cost12 + " " + pg.chat.get(52));
                            randombarriermeta12.setLore(lore12);
                            randombarrier12.setItemMeta(randombarriermeta12);
                            inv.setItem(11, randombarrier12);

                            if (pg.kitplayernames.containsKey(kit13) && pg.kitplayernames.containsValue(p))
                                cost13 = cost13 / 2;
                            ItemStack randombarrier13 = new ItemStack(material13);
                            ItemMeta randombarriermeta13 = randombarrier13.getItemMeta();
                            assert randombarriermeta13 != null;
                            randombarriermeta13.setDisplayName(name13);
                            ArrayList<String> lore13 = new ArrayList<>();
                            lore13.add(pg.chat.get(50) + ": " + time13);
                            lore13.add(pg.chat.get(51) + ": " + cost13 + " " + pg.chat.get(52));
                            randombarriermeta13.setLore(lore13);
                            randombarrier13.setItemMeta(randombarriermeta13);
                            inv.setItem(12, randombarrier13);

                            if (pg.kitplayernames.containsKey(kit14) && pg.kitplayernames.containsValue(p))
                                cost14 = cost14 / 2;
                            ItemStack randombarrier14 = new ItemStack(material14);
                            ItemMeta randombarriermeta14 = randombarrier14.getItemMeta();
                            assert randombarriermeta14 != null;
                            randombarriermeta14.setDisplayName(name14);
                            ArrayList<String> lore14 = new ArrayList<>();
                            lore14.add(pg.chat.get(50) + ": " + time14);
                            lore14.add(pg.chat.get(51) + ": " + cost14 + " " + pg.chat.get(52));
                            randombarriermeta14.setLore(lore14);
                            randombarrier14.setItemMeta(randombarriermeta14);
                            inv.setItem(13, randombarrier14);

                            if (pg.kitplayernames.containsKey(kit15) && pg.kitplayernames.containsValue(p))
                                cost15 = cost15 / 2;
                            ItemStack randombarrier15 = new ItemStack(material15);
                            ItemMeta randombarriermeta15 = randombarrier15.getItemMeta();
                            assert randombarriermeta15 != null;
                            randombarriermeta15.setDisplayName(name15);
                            ArrayList<String> lore15 = new ArrayList<>();
                            lore15.add(pg.chat.get(50) + ": " + time15);
                            lore15.add(pg.chat.get(51) + ": " + cost15 + " " + pg.chat.get(52));
                            randombarriermeta15.setLore(lore15);
                            randombarrier15.setItemMeta(randombarriermeta15);
                            inv.setItem(14, randombarrier15);

                            if (pg.kitplayernames.containsKey(kit16) && pg.kitplayernames.containsValue(p))
                                cost16 = cost16 / 2;
                            ItemStack randombarrier16 = new ItemStack(material16);
                            ItemMeta randombarriermeta16 = randombarrier16.getItemMeta();
                            assert randombarriermeta16 != null;
                            randombarriermeta16.setDisplayName(name16);
                            ArrayList<String> lore16 = new ArrayList<>();
                            lore16.add(pg.chat.get(50) + ": " + time16);
                            lore16.add(pg.chat.get(51) + ": " + cost16 + " " + pg.chat.get(52));
                            randombarriermeta16.setLore(lore16);
                            randombarrier16.setItemMeta(randombarriermeta16);
                            inv.setItem(15, randombarrier16);

                            if (pg.kitplayernames.containsKey(kit17) && pg.kitplayernames.containsValue(p))
                                cost17 = cost17 / 2;
                            ItemStack randombarrier17 = new ItemStack(material17);
                            ItemMeta randombarriermeta17 = randombarrier17.getItemMeta();
                            assert randombarriermeta17 != null;
                            randombarriermeta17.setDisplayName(name17);
                            ArrayList<String> lore17 = new ArrayList<>();
                            lore17.add(pg.chat.get(50) + ": " + time17);
                            lore17.add(pg.chat.get(51) + ": " + cost17 + " " + pg.chat.get(52));
                            randombarriermeta17.setLore(lore17);
                            randombarrier17.setItemMeta(randombarriermeta17);
                            inv.setItem(16, randombarrier17);

                            if (pg.kitplayernames.containsKey(kit18) && pg.kitplayernames.containsValue(p))
                                cost18 = cost18 / 2;
                            ItemStack randombarrier18 = new ItemStack(material18);
                            ItemMeta randombarriermeta18 = randombarrier18.getItemMeta();
                            assert randombarriermeta18 != null;
                            randombarriermeta18.setDisplayName(name18);
                            ArrayList<String> lore18 = new ArrayList<>();
                            lore18.add(pg.chat.get(50) + ": " + time18);
                            lore18.add(pg.chat.get(51) + ": " + cost18 + " " + pg.chat.get(52));
                            randombarriermeta18.setLore(lore18);
                            randombarrier18.setItemMeta(randombarriermeta18);
                            inv.setItem(17, randombarrier18);

                            if (pg.kitplayernames.containsKey(kit19) && pg.kitplayernames.containsValue(p))
                                cost19 = cost19 / 2;
                            ItemStack randombarrier19 = new ItemStack(material19);
                            ItemMeta randombarriermeta19 = randombarrier18.getItemMeta();
                            assert randombarriermeta19 != null;
                            randombarriermeta19.setDisplayName(name19);
                            ArrayList<String> lore19 = new ArrayList<>();
                            lore19.add(pg.chat.get(50) + ": " + time19);
                            lore19.add(pg.chat.get(51) + ": " + cost19 + " " + pg.chat.get(52));
                            randombarriermeta19.setLore(lore19);
                            randombarrier19.setItemMeta(randombarriermeta19);
                            inv.setItem(18, randombarrier19);
                        }
                        p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                    }
                }
            }
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (e.getHand() == EquipmentSlot.HAND) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.MUSHROOM_STEW || p.getInventory().getItemInMainHand().getType() == Material.RABBIT_STEW || p.getInventory().getItemInMainHand().getType() == Material.BEETROOT_SOUP) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            double health = p.getHealth();
                            int foodlvl = p.getFoodLevel();
                            if (health == 20 && foodlvl >= 13) {
                                p.setFoodLevel(20);
                                p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                            } else if (health == 20 && foodlvl < 13) {
                                p.setFoodLevel(foodlvl + 7);
                                p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                            } else {
                                if (health < 20 && health >= 13) {
                                    p.setHealth(20);
                                    p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                } else if (health < 20 && health < 13) {
                                    p.setHealth(health + 7);
                                    p.getInventory().setItemInMainHand(new ItemStack(Material.BOWL));
                                }
                            }
                        }
                    }
                    if (p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
                        if (pg.getGamestate() == GameStates.INGAME) {
                            Player result = null;
                            double lastDistance = Double.MAX_VALUE;
                            for (Player cp : p.getWorld().getPlayers()) {
                                if (pg.getPgPlayers().contains(cp)) {
                                    if (p == cp)
                                        continue;
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
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(pg.prefix + ChatColor.GREEN + pg.chat.get(12) + ": " + ChatColor.AQUA + (int) lastDistance));
                            } else {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(pg.prefix + ChatColor.RED + pg.chat.get(13)));
                            }
                        }
                    }
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.PAPER) {
                    if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                        Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(14));
                        ArrayList<String> randomlore = new ArrayList<>();
                        randomlore.add(0, ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.votes.get(pg.chat.get(42)).toString());
                        ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                        ItemMeta randombarriermeta = randombarrier.getItemMeta();
                        assert randombarriermeta != null;
                        randombarriermeta.setDisplayName(pg.chat.get(42));
                        randombarriermeta.setLore(randomlore);
                        randombarrier.setItemMeta(randombarriermeta);
                        inv.setItem(0, randombarrier);
                        int slot = 1;
                        for (String all : pg.arenas) {
                            ArrayList<String> arenalore = new ArrayList<>();
                            arenalore.add(0, ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.votes.get(all).toString());
                            ItemStack arenamap = new ItemStack(Material.MAP);
                            ItemMeta arenamapmeta = arenamap.getItemMeta();
                            assert arenamapmeta != null;
                            arenamapmeta.setDisplayName(all);
                            arenamapmeta.setLore(arenalore);
                            arenamap.setItemMeta(arenamapmeta);
                            inv.setItem(slot, arenamap);
                            slot++;
                        }
                        p.openInventory(inv);
                    }
                }
                if (pg.isActivateTeams()) {
                    if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                        if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                            Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(43));
                            ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                            ItemMeta randombarriermeta = randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.setDisplayName(pg.chat.get(42));
                            randombarrier.setItemMeta(randombarriermeta);
                            inv.setItem(0, randombarrier);
                            int slot = 1;
                            for (String i : pg.teams) {
                                ArrayList<String> arenalore = new ArrayList<>();
                                arenalore.add(0, ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(i).toString());
                                ItemStack arenamap = new ItemStack(Material.PLAYER_HEAD);
                                ItemMeta arenamapmeta = arenamap.getItemMeta();
                                assert arenamapmeta != null;
                                arenamapmeta.setDisplayName(i);
                                arenamapmeta.setLore(arenalore);
                                arenamap.setItemMeta(arenamapmeta);
                                inv.setItem(slot, arenamap);
                                slot++;
                            }
                            p.openInventory(inv);
                        }
                    }
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.ENDER_CHEST) {
                    if (pg.getGamestate() == GameStates.WAITING || pg.getGamestate() == GameStates.PREPARING) {
                        Inventory inv = Bukkit.createInventory(null, 9 * 3, pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(62));
                        ItemStack randombarrier = new ItemStack(Material.COMMAND_BLOCK);
                        ItemMeta randombarriermeta = randombarrier.getItemMeta();
                        assert randombarriermeta != null;
                        randombarriermeta.setDisplayName(pg.chat.get(42));
                        randombarrier.setItemMeta(randombarriermeta);
                        inv.setItem(0, randombarrier);
                        int slot = 1;
                        for (String i : pg.kits) {
                            ItemStack arenamap = new ItemStack(Material.ARMOR_STAND);
                            ItemMeta arenamapmeta = arenamap.getItemMeta();
                            assert arenamapmeta != null;
                            arenamapmeta.setDisplayName(i);
                            arenamap.setItemMeta(arenamapmeta);
                            inv.setItem(slot, arenamap);
                            slot++;
                        }
                        p.openInventory(inv);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (pg.getGamestate() == GameStates.WAITING && !pg.isBuild() && pg.pgPlayers.contains(p) || pg.getGamestate() == GameStates.PREPARING && !pg.isBuild() && pg.pgPlayers.contains(p)) {
            if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(14))) {
                if (e.getCurrentItem() != null) {
                    if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                        String displayname = e.getCurrentItem().getItemMeta().getDisplayName();
                        if (!pg.voted.contains(p.getName())) {
                            p.closeInventory();
                            int votes = pg.votes.get(displayname);
                            votes++;
                            pg.votes.put(displayname, votes);
                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(16) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.votes.get(displayname));
                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                            pg.voted.add(e.getWhoClicked().getName());
                            pg.voteplayernames.put(displayname, p);
                        } else {
                            p.closeInventory();
                            String arenaname = "";
                            for (int i = 0; i <= pg.arenas.size(); i++) {
                                if (pg.getConfig().contains("pg.arenas." + i)) {
                                    String name = pg.getConfig().getString("pg.arenas." + i + ".name");
                                    if (pg.voteplayernames.containsKey(name) && pg.voteplayernames.containsValue(p)) {
                                        arenaname = name;
                                    }
                                } else {
                                    arenaname = pg.chat.get(42);
                                }
                            }
                            pg.voteplayernames.remove(arenaname, p);
                            int votes = pg.votes.get(arenaname) - 1;
                            pg.votes.put(arenaname, votes);
                            pg.voted.remove(p.getName());
                            votes = pg.votes.get(displayname);
                            votes++;
                            pg.votes.put(displayname, votes);
                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(16) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(15) + ": " + ChatColor.AQUA + pg.votes.get(displayname));
                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(14) + "--------------");
                            pg.voted.add(e.getWhoClicked().getName());
                            pg.voteplayernames.put(displayname, p);
                        }
                    }
                }
            }
            if (pg.isActivateTeams()) {
                if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(43))) {
                    if (e.getCurrentItem() != null) {
                        if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                            String displayname = e.getCurrentItem().getItemMeta().getDisplayName();
                            int maxteamplayers = pg.getTeamSize();
                            if (!pg.teamed.contains(p.getName())) {
                                if (displayname.equals(pg.chat.get(42))) {
                                    boolean teamfound = false;
                                    while (!teamfound) {
                                        Random rnd = new Random();
                                        int rndTeam = rnd.nextInt(pg.teams.size() + 1);
                                        if (pg.teamplayers.get(Integer.toString(rndTeam)) < maxteamplayers) {
                                            teamfound = true;
                                            p.closeInventory();
                                            int players = pg.teamplayers.get(Integer.toString(rndTeam));
                                            players++;
                                            pg.teamplayers.put(Integer.toString(rndTeam), players);
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(Integer.toString(rndTeam)) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                            pg.teamed.add(e.getWhoClicked().getName());
                                            pg.teamplayernames.put(Integer.toString(rndTeam), p);
                                        }
                                    }
                                } else {
                                    if (pg.teamplayers.get(displayname) < maxteamplayers) {
                                        p.closeInventory();
                                        int players = pg.teamplayers.get(displayname);
                                        players++;
                                        pg.teamplayers.put(displayname, players);
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(displayname) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                        pg.teamed.add(e.getWhoClicked().getName());
                                        pg.teamplayernames.put(displayname, p);
                                    } else {
                                        p.closeInventory();
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(47));
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                    }
                                }
                            } else {
                                p.closeInventory();
                                String teamname = "";
                                for (int i = 1; i <= pg.getTeamAmount(); i++) {
                                    if (pg.teamplayernames.containsKey(Integer.toString(i)) && pg.teamplayernames.containsValue(p)) {
                                        teamname = String.valueOf(i);
                                    }
                                }
                                pg.teamplayernames.remove(teamname, p);
                                int teamamount = pg.teamplayers.get(teamname) - 1;
                                pg.teamplayers.put(teamname, teamamount);
                                pg.teamed.remove(p.getName());
                                if (displayname.equals(pg.chat.get(42))) {
                                    boolean teamfound = false;
                                    while (!teamfound) {
                                        Random rnd = new Random();
                                        int rndTeam = rnd.nextInt(pg.teams.size() + 1);
                                        if (pg.teamplayers.get(Integer.toString(rndTeam)) < maxteamplayers) {
                                            teamfound = true;
                                            p.closeInventory();
                                            int players = pg.teamplayers.get(Integer.toString(rndTeam));
                                            players++;
                                            pg.teamplayers.put(Integer.toString(rndTeam), players);
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + rndTeam);
                                            p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(Integer.toString(rndTeam)) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                            p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                            pg.teamed.add(e.getWhoClicked().getName());
                                            pg.teamplayernames.put(Integer.toString(rndTeam), p);
                                        }
                                    }
                                } else {
                                    if (pg.teamplayers.get(displayname) < maxteamplayers) {
                                        p.closeInventory();
                                        int players = pg.teamplayers.get(displayname);
                                        players++;
                                        pg.teamplayers.put(displayname, players);
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(45) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                        p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(44) + ": " + ChatColor.AQUA + pg.teamplayers.get(displayname) + ChatColor.GRAY + "/" + ChatColor.AQUA + maxteamplayers);
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                        pg.teamed.add(e.getWhoClicked().getName());
                                        pg.teamplayernames.put(displayname, p);
                                    } else {
                                        p.closeInventory();
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(47));
                                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(43) + "--------------");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(62))) {
                if (e.getCurrentItem() != null) {
                    if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).hasDisplayName()) {
                        String displayname = e.getCurrentItem().getItemMeta().getDisplayName();
                        if (!pg.kited.contains(p.getName())) {
                            if (displayname.equals(pg.chat.get(42))) {
                                Random rnd = new Random();
                                int rndKit = rnd.nextInt(pg.kits.size() + 1);
                                p.closeInventory();
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + pg.kits.get(rndKit));
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                pg.kited.add(e.getWhoClicked().getName());
                                pg.kitplayernames.put(Integer.toString(rndKit), p);
                            } else {
                                p.closeInventory();
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                pg.kited.add(e.getWhoClicked().getName());
                                pg.kitplayernames.put(displayname, p);
                            }
                        } else {
                            p.closeInventory();
                            String kitname = "";
                            for (int i = 0; i <= pg.kits.size(); i++) {
                                if (pg.kitplayernames.containsKey(Integer.toString(i)) && pg.kitplayernames.containsValue(p)) {
                                    kitname = String.valueOf(i);
                                }
                            }
                            pg.teamplayernames.remove(kitname, p);
                            if (displayname.equals(pg.chat.get(42))) {
                                Random rnd = new Random();
                                int rndKit = rnd.nextInt(pg.kits.size() + 1);
                                p.closeInventory();
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + pg.kits.get(rndKit));
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                pg.kited.add(e.getWhoClicked().getName());
                                pg.kitplayernames.put(Integer.toString(rndKit), p);
                            } else {
                                p.closeInventory();
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + displayname);
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                pg.kited.add(e.getWhoClicked().getName());
                                pg.kitplayernames.put(displayname, p);
                            }
                        }
                    }
                }
            }
            e.setCancelled(true);
        } else {
            e.setCancelled(false);
        }
        if (e.getView().getTitle().equalsIgnoreCase(pg.prefix + ChatColor.DARK_AQUA + pg.chat.get(49))) {
            if (e.getCurrentItem() != null) {
                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name1)) {
                    if (bottle >= 1) {
                        if (amount >= cost1) {
                            amount = amount - cost1;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material11);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion1, time1 * 20, 2), true);
                            randombarriermeta.setDisplayName(name1);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost1; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name2)) {
                    if (bottle >= 1) {
                        if (amount >= cost2) {
                            amount = amount - cost2;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material2);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion2, time2 * 20, 2), true);
                            randombarriermeta.setDisplayName(name2);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost2; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name3)) {
                    if (bottle >= 1) {
                        if (amount >= cost3) {
                            amount = amount - cost3;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material3);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion3, time3 * 20, 2), true);
                            randombarriermeta.setDisplayName(name3);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost3; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name4)) {
                    if (bottle >= 1) {
                        if (amount >= cost4) {
                            amount = amount - cost4;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material4);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion4, time4 * 20, 2), true);
                            randombarriermeta.setDisplayName(name4);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost4; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name5)) {
                    if (bottle >= 1) {
                        if (amount >= cost5) {
                            amount = amount - cost5;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material5);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion5, time5 * 20, 2), true);
                            randombarriermeta.setDisplayName(name5);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost5; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name6)) {
                    if (bottle >= 1) {
                        if (amount >= cost6) {
                            amount = amount - cost6;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material6);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion6, time6 * 20, 2), true);
                            randombarriermeta.setDisplayName(name6);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost6; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name7)) {
                    if (bottle >= 1) {
                        if (amount >= cost7) {
                            amount = amount - cost7;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material7);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion7, time7 * 20, 2), true);
                            randombarriermeta.setDisplayName(name7);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost7; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name8)) {
                    if (bottle >= 1) {
                        if (amount >= cost8) {
                            amount = amount - cost8;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material8);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion8, time8 * 20, 2), true);
                            randombarriermeta.setDisplayName(name8);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost8; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name9)) {
                    if (bottle >= 1) {
                        if (amount >= cost9) {
                            amount = amount - cost9;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material9);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion9, time9 * 20, 2), true);
                            randombarriermeta.setDisplayName(name9);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost9; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name10)) {
                    if (bottle >= 1) {
                        if (amount >= cost10) {
                            amount = amount - cost10;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material10);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion10, time10 * 20, 2), true);
                            randombarriermeta.setDisplayName(name10);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost10; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name11)) {
                    if (bottle >= 1) {
                        if (amount >= cost11) {
                            amount = amount - cost11;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material11);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion11, time11 * 20, 2), true);
                            randombarriermeta.setDisplayName(name11);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost11; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name12)) {
                    if (bottle >= 1) {
                        if (amount >= cost12) {
                            amount = amount - cost12;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material12);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion12, time12 * 20, 2), true);
                            randombarriermeta.setDisplayName(name12);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost12; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name13)) {
                    if (bottle >= 1) {
                        if (amount >= cost13) {
                            amount = amount - cost13;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material13);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion13, time13 * 20, 2), true);
                            randombarriermeta.setDisplayName(name13);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost13; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name14)) {
                    if (bottle >= 1) {
                        if (amount >= cost14) {
                            amount = amount - cost14;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material14);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion14, time14 * 20, 2), true);
                            randombarriermeta.setDisplayName(name14);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost14; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name15)) {
                    if (bottle >= 1) {
                        if (amount >= cost15) {
                            amount = amount - cost15;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material15);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion15, time15 * 20, 2), true);
                            randombarriermeta.setDisplayName(name15);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost15; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name16)) {
                    if (bottle >= 1) {
                        if (amount >= cost16) {
                            amount = amount - cost16;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material16);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion16, time16 * 20, 2), true);
                            randombarriermeta.setDisplayName(name16);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost16; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name17)) {
                    if (bottle >= 1) {
                        if (amount >= cost17) {
                            amount = amount - cost17;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material17);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion17, time17 * 20, 2), true);
                            randombarriermeta.setDisplayName(name17);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost17; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name18)) {
                    if (bottle >= 1) {
                        if (amount >= cost18) {
                            amount = amount - cost18;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material18);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion18, time18 * 20, 2), true);
                            randombarriermeta.setDisplayName(name18);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost18; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }

                if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name19)) {
                    if (bottle >= 1) {
                        if (amount >= cost19) {
                            amount = amount - cost19;
                            bottle = bottle - 1;
                            ItemStack randombarrier = new ItemStack(material19);
                            PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                            assert randombarriermeta != null;
                            randombarriermeta.addCustomEffect(new PotionEffect(potion19, time19 * 20, 2), true);
                            randombarriermeta.setDisplayName(name19);
                            randombarrier.setItemMeta(randombarriermeta);
                            p.getInventory().addItem(randombarrier);
                            for (int i = 0; i < cost19; i++)
                                p.getInventory().removeItem(pg.getCoin());
                            for (int i = 0; i < 1; i++)
                                p.getInventory().removeItem(pg.getBottle());
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                        }
                    } else {
                        p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                    }
                }
            }
            e.setCancelled(true);
        } else {
            e.setCancelled(false);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        e.setCancelled(pg.getGamestate() != GameStates.INGAME && pg.pgPlayers.contains(e.getPlayer()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        pg.joinChannel(p, "General");
        pg.createPlayer(p.getUniqueId().toString());
        if (pg.isStartOnJoin()) {
            pg.onJoin(p);
            e.setJoinMessage(null);
        }
        if (p.isOp()) {
            String latest = "";
            try {
                URL url = new URL("https://raw.githubusercontent.com/andersspielen/PotionGames/master/version.txt");
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(url.openStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                    stringBuilder.append(System.lineSeparator());
                }
                bufferedReader.close();
                latest = stringBuilder.toString().trim();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            boolean upToDate = pg.getDescription().getVersion().equals(latest);
            if (!upToDate) {
                p.sendMessage(pg.prefix + "There is a newer version available: " + latest + ", you're on: " + pg.getDescription().getVersion() + " - Download it here: https://github.com/andersspielen/PotionGames/releases/latest");
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!pg.isMove() && pg.pgPlayers.contains(p)) {
            if (e.getFrom().getX() != Objects.requireNonNull(e.getTo()).getX() || e.getFrom().getZ() != e.getTo().getZ()) {
                Location loc = new Location(p.getWorld(), e.getFrom().getX(), e.getTo().getY(), e.getFrom().getZ());
                loc.setYaw(e.getTo().getYaw());
                loc.setPitch(e.getTo().getPitch());
                p.teleport(loc);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (pg.isStartOnJoin() && pg.pgPlayers.contains(p) || !pg.isStartOnJoin() && pg.pgPlayers.contains(p) || pg.isStartOnJoin() && pg.specPlayers.contains(p) || !pg.isStartOnJoin() && pg.specPlayers.contains(p)) {
            pg.onLeave(p);
            e.setQuitMessage(null);
        }
    }

    @EventHandler
    public void onPing(ServerListPingEvent e) {
        if (pg.isStartOnJoin()) {
            e.setMaxPlayers(pg.getMaxPlayers());
            if (pg.getGamestate() != GameStates.WAITING && pg.getGamestate() != GameStates.PREPARING) {
                e.setMotd("" + ChatColor.DARK_RED + pg.getGamestate());
            } else {
                e.setMotd("" + ChatColor.DARK_GREEN + pg.getGamestate());
            }
        }
    }

}
