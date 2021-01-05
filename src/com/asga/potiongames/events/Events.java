package com.asga.potiongames.events;

import com.asga.potiongames.gamestates.GameStates;
import com.asga.potiongames.main.PotionGames;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
        FileConfiguration kitdata = YamlConfiguration.loadConfiguration(pg.kitdatafile);
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
                    Sign sign = (Sign) e.getClickedBlock().getState();
                    String line2 = sign.getLine(1);
                    String line3 = sign.getLine(2);
                    if (line2.matches("PotionGames") && line3.matches("Join")) {
                        if (!pg.pgPlayers.contains(p) && !pg.specPlayers.contains(p))
                            pg.onJoin(p);
                    }
                    if (line2.matches("PotionGames") && line3.matches("Stats")) {
                        int wins = pg.getWins(p.getUniqueId().toString());
                        int losts = pg.getLosts(p.getUniqueId().toString());
                        int kills = pg.getKills(p.getUniqueId().toString());
                        int deaths = pg.getDeaths(p.getUniqueId().toString());
                        double kd = pg.getKD(p.getUniqueId().toString());
                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(56) + "--------------");
                        p.sendMessage(pg.prefix + pg.chat.get(57) + ": " + ChatColor.AQUA + wins);
                        p.sendMessage(pg.prefix + pg.chat.get(58) + ": " + ChatColor.AQUA + losts);
                        p.sendMessage(pg.prefix + pg.chat.get(59) + ": " + ChatColor.AQUA + kills);
                        p.sendMessage(pg.prefix + pg.chat.get(60) + ": " + ChatColor.AQUA + deaths);
                        p.sendMessage(pg.prefix + pg.chat.get(61) + ": " + ChatColor.AQUA + kd);
                        p.sendMessage(pg.prefix + "--------------" + pg.chat.get(56) + "--------------");
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
                                        if (pg.isActivateShop()) {
                                            ArrayList<ItemStack> potions1 = new ArrayList<>();
                                            potions1.add(new ItemStack(Material.GLASS_BOTTLE, 1));
                                            ArrayList<ItemStack> potions2 = new ArrayList<>();
                                            potions2.add(pg.getCoin());
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
                                        } else {
                                            int item1 = rnd.nextInt(food1.size());
                                            inv.setItem(slot, food1.get(item1));
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
                                inv.setItem(2, ingot);
                                inv.setItem(6, ingot);
                            } else {
                                p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                            }
                        }
                    }
                    if (pg.isActivateShop()) {
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
                                pg.chests.put(e.getClickedBlock().getLocation(), inv);
                                int shopitem = 1;
                                for (int i = 0; i < pg.getActivePotions(); i++) {
                                    int coinamount;
                                    if (pg.kitplayernames.containsKey(pg.shopkit.get(shopitem - 1)) && pg.kitplayernames.containsValue(p)) {
                                        coinamount = pg.shopsale.get(shopitem - 1);
                                    } else {
                                        coinamount = pg.shopcost.get(shopitem - 1);
                                    }
                                    ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                    ItemMeta randombarriermeta = randombarrier.getItemMeta();
                                    assert randombarriermeta != null;
                                    randombarriermeta.setDisplayName(pg.shop.get(shopitem - 1));
                                    ArrayList<String> lore = new ArrayList<>();
                                    lore.add(pg.chat.get(50) + ": " + pg.shoppotion.get(shopitem - 1).getDuration());
                                    lore.add(pg.chat.get(51) + ": " + coinamount + " " + pg.chat.get(52));
                                    randombarriermeta.setLore(lore);
                                    randombarrier.setItemMeta(randombarriermeta);
                                    inv.setItem(shopitem - 1, randombarrier);
                                    shopitem++;
                                }
                            }
                            p.openInventory(pg.chests.get(e.getClickedBlock().getLocation()));
                        }
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
                        for (int i = 1; i <= pg.getActiveKits(); i++) {
                            ItemStack arenamap = new ItemStack(Material.ARMOR_STAND);
                            ItemMeta arenamapmeta = arenamap.getItemMeta();
                            assert arenamapmeta != null;
                            arenamapmeta.setDisplayName(kitdata.getString("pg.kits." + i + ".name"));
                            arenamap.setItemMeta(arenamapmeta);
                            inv.setItem(i, arenamap);
                        }
                        p.openInventory(inv);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        FileConfiguration arenadata = YamlConfiguration.loadConfiguration(pg.arenadatafile);
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
                                if (arenadata.contains("pg.arenas." + i)) {
                                    String name = arenadata.getString("pg.arenas." + i + ".name");
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
                                int rndKit = rnd.nextInt(pg.getActiveKits());
                                p.closeInventory();
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + pg.kits.get(rndKit));
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                pg.kited.add(e.getWhoClicked().getName());
                                pg.kitplayernames.put(pg.kits.get(rndKit), p);
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
                            for (int i = 0; i <= pg.getActiveKits(); i++) {
                                if (pg.kitplayernames.containsKey(Integer.toString(i)) && pg.kitplayernames.containsValue(p)) {
                                    kitname = String.valueOf(i);
                                }
                            }
                            pg.teamplayernames.remove(kitname, p);
                            if (displayname.equals(pg.chat.get(42))) {
                                Random rnd = new Random();
                                int rndKit = rnd.nextInt(pg.getActiveKits() + 1);
                                p.closeInventory();
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                p.sendMessage(pg.prefix + ChatColor.GREEN + pg.chat.get(46) + ": " + ChatColor.LIGHT_PURPLE + pg.kits.get(rndKit));
                                p.sendMessage(pg.prefix + "--------------" + pg.chat.get(62) + "--------------");
                                pg.kited.add(e.getWhoClicked().getName());
                                pg.kitplayernames.put(pg.kits.get(rndKit), p);
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
                int shopitem = 1;
                for (int i = 0; i < pg.shop.size(); i++) {
                    int coinamount;
                    if (pg.kitplayernames.containsKey(pg.shopkit.get(shopitem - 1)) && pg.kitplayernames.containsValue(p)) {
                        coinamount = pg.shopsale.get(shopitem - 1);
                    } else {
                        coinamount = pg.shopcost.get(shopitem - 1);
                    }
                    if (Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(pg.shop.get(shopitem - 1))) {
                        if (bottle >= 1) {
                            if (amount >= coinamount) {
                                amount = amount - coinamount;
                                bottle = bottle - 1;
                                ItemStack randombarrier = new ItemStack(pg.shoppotiontype.get(shopitem - 1));
                                PotionMeta randombarriermeta = (PotionMeta) randombarrier.getItemMeta();
                                assert randombarriermeta != null;
                                randombarriermeta.addCustomEffect(new PotionEffect(pg.shoppotion.get(shopitem - 1).getType(), pg.shoppotion.get(shopitem - 1).getDuration(), pg.shoppotion.get(shopitem - 1).getAmplifier()), true);
                                randombarriermeta.setDisplayName(pg.shop.get(shopitem - 1));
                                randombarrier.setItemMeta(randombarriermeta);
                                p.getInventory().addItem(randombarrier);
                                for (int k = 0; k < coinamount; k++)
                                    p.getInventory().removeItem(pg.getCoin());
                                for (int k = 0; k < 1; k++)
                                    p.getInventory().removeItem(pg.getBottle());
                            } else {
                                p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(53));
                            }
                        } else {
                            p.sendMessage(pg.prefix + ChatColor.RED + pg.chat.get(54));
                        }
                    }
                    shopitem++;
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
                URL url = new URL("https://raw.githubusercontent.com/andersspielen/PotionGamesIssues/master/version.txt");
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
                p.sendMessage(pg.prefix + "There is a newer version available: " + latest + ", you're on: " + pg.getDescription().getVersion() + " - Download it here: https://github.com/andersspielen/PotionGamesIssues/releases/latest");
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
