package com.tw0far.potiongames.bootstrap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.sign.Side;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import com.tw0far.potiongames.util.SkullUtil;

import net.kyori.adventure.text.Component;

public class RankWallUpdater {
    private final PotionGames plugin;
    private final HashMap<Integer, String> rank = new HashMap<>();
    private final ArrayList<Location> rankhead = new ArrayList<>();
    private final ArrayList<Location> ranksign = new ArrayList<>();

    public RankWallUpdater(PotionGames plugin) {
        this.plugin = plugin;
    }

    public ScheduledTask start() {
        return plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> {
            if (plugin.getConfig().contains("pg.RankWall.headp1") && plugin.getConfig().contains("pg.RankWall.headp2") && plugin.getConfig().contains("pg.RankWall.headp3") && plugin.getConfig().contains("pg.RankWall.signp1") && plugin.getConfig().contains("pg.RankWall.signp2") && plugin.getConfig().contains("pg.RankWall.signp3")) {
                rank.clear();
                rankhead.clear();
                ranksign.clear();
                try (ResultSet rs = plugin.getDatabaseManager().query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3")) {
                    int ii = 0;
                    while (rs.next()) {
                        ii++;
                        rank.put(ii, rs.getString("UUID"));
                    }
                    rankhead.add(plugin.getConfig().getLocation("pg.RankWall.headp1"));
                    rankhead.add(plugin.getConfig().getLocation("pg.RankWall.headp2"));
                    rankhead.add(plugin.getConfig().getLocation("pg.RankWall.headp3"));
                    ranksign.add(plugin.getConfig().getLocation("pg.RankWall.signp1"));
                    ranksign.add(plugin.getConfig().getLocation("pg.RankWall.signp2"));
                    ranksign.add(plugin.getConfig().getLocation("pg.RankWall.signp3"));
                    for (int iii = 0; iii < rank.size(); iii++) {
                        int id = iii + 1;
                        Skull skull = (Skull) rankhead.get(iii).getBlock().getState();
                        UUID uuid = UUID.fromString(rank.get(id));
                        SkullUtil.setSkullOwner(skull, uuid);
                    }
                    for (int iii = 0; iii < rank.size(); iii++) {
                        int id = iii + 1;
                        BlockState state = ranksign.get(iii).getBlock().getState();
                        OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(rank.get(id)));
                        Sign sign = (Sign) state;
                        sign.getSide(Side.FRONT).line(0, Messages.SignPlace(id));
                        sign.getSide(Side.FRONT).line(1, Component.text(name.getName()));
                        sign.getSide(Side.FRONT).line(2, Messages.SignWins(plugin.getDatabaseManager().getWins(rank.get(id))));
                        sign.getSide(Side.FRONT).line(3, Messages.SignKD(plugin.getDatabaseManager().getKD(rank.get(id))));
                        sign.update();
                    }
                } catch (SQLException ex) {
                    plugin.getComponentLogger().info(Messages.RankwallCouldNotUpdate());
                }
            }
        }, 1, 1200);
    }
}
