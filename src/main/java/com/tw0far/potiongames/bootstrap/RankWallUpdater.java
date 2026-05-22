package com.tw0far.potiongames.bootstrap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class RankWallUpdater {
    private final PotionGames plugin;

    public RankWallUpdater(PotionGames plugin) {
        this.plugin = plugin;
    }

    public int start() {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (plugin.getConfig().contains("pg.RankWall.headp1") && plugin.getConfig().contains("pg.RankWall.headp2") && plugin.getConfig().contains("pg.RankWall.headp3") && plugin.getConfig().contains("pg.RankWall.signp1") && plugin.getConfig().contains("pg.RankWall.signp2") && plugin.getConfig().contains("pg.RankWall.signp3")) {
                try (ResultSet rs = plugin.getDatabaseManager().query("SELECT UUID FROM Stats ORDER BY WINS DESC LIMIT 3")) {
                    int ii = 0;
                    while (rs.next()) {
                        ii++;
                        plugin.getRank().put(ii, rs.getString("UUID"));
                    }
                    plugin.getRankhead().add(plugin.getConfig().getLocation("pg.RankWall.headp1"));
                    plugin.getRankhead().add(plugin.getConfig().getLocation("pg.RankWall.headp2"));
                    plugin.getRankhead().add(plugin.getConfig().getLocation("pg.RankWall.headp3"));
                    plugin.getRanksign().add(plugin.getConfig().getLocation("pg.RankWall.signp1"));
                    plugin.getRanksign().add(plugin.getConfig().getLocation("pg.RankWall.signp2"));
                    plugin.getRanksign().add(plugin.getConfig().getLocation("pg.RankWall.signp3"));
                    for (int iii = 0; iii < plugin.getRank().size(); iii++) {
                        int id = iii + 1;
                        Skull skull = (Skull) plugin.getRankhead().get(iii).getBlock().getState();
                        UUID uuid = UUID.fromString(plugin.getRank().get(id));
                        OfflinePlayer name = Bukkit.getOfflinePlayer(uuid);
                        skull.setOwningPlayer(name);
                        skull.update();
                    }
                    for (int iii = 0; iii < plugin.getRank().size(); iii++) {
                        int id = iii + 1;
                        BlockState state = plugin.getRanksign().get(iii).getBlock().getState();
                        OfflinePlayer name = Bukkit.getOfflinePlayer(UUID.fromString(plugin.getRank().get(id)));
                        Sign sign = (Sign) state;
                        sign.getSide(Side.FRONT).line(0, Messages.SignPlace(id));
                        sign.getSide(Side.FRONT).line(1, Component.text(name.getName()));
                        sign.getSide(Side.FRONT).line(2, Messages.SignWins(plugin.getWins(plugin.getRank().get(id))));
                        sign.getSide(Side.FRONT).line(3, Messages.SignKD(plugin.getKD(plugin.getRank().get(id))));
                        sign.update();
                    }
                } catch (SQLException ex) {
                    Bukkit.getConsoleSender().sendMessage(Messages.RankwallCouldNotUpdate());
                }
            }
        }, 0, 1200);
    }
}
