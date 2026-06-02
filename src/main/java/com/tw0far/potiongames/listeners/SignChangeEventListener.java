package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.main.PotionGames;
import com.tw0far.potiongames.models.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import java.lang.reflect.Method;

/**
 * Handles sign change events.
 * Manages join signs and other custom sign interactions.
 */
public class SignChangeEventListener implements Listener {
    private final PotionGames plugin;
    
    public SignChangeEventListener(PotionGames plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    @SuppressWarnings("deprecation") // Using SignChangeEvent legacy methods; migrate to Component-based API when ready
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();
        
        String line1 = e.getLine(0);
        
        if (line1 == null) {
            return;
        }
        
        // Check for PotionGames signs
        if (line1.equalsIgnoreCase("[PG]") || line1.equalsIgnoreCase("[PotionGames]")) {
            // Check if player has permission
            if (!p.hasPermission("pg.sign")) {
                e.setCancelled(true);
                p.sendMessage(Messages.PermissionNoUse());
                return;
            }
            
            // Format the sign with colors using component API when available
            Component c0 = Messages.SignPrefix();
            String line2 = e.getLine(1);
            String line3 = e.getLine(2);
            String line4 = e.getLine(3);
            Component c1 = Component.text(line2 == null ? "" : line2).color(NamedTextColor.BLUE);
            Component c2 = Component.text(line3 == null ? "" : line3).color(NamedTextColor.AQUA);
            Component c3 = Component.text(line4 == null ? "" : line4).color(NamedTextColor.GREEN);
            
            // Try to use component-based method if available, otherwise fall back to legacy setLine
            try {
                Method m = e.getClass().getMethod("line", int.class, Component.class);
                m.invoke(e, 0, c0);
                m.invoke(e, 1, c1);
                m.invoke(e, 2, c2);
                m.invoke(e, 3, c3);
            } catch (NoSuchMethodException ex1) {
                try {
                    Method m2 = e.getClass().getMethod("setLine", int.class, Component.class);
                    m2.invoke(e, 0, c0);
                    m2.invoke(e, 1, c1);
                    m2.invoke(e, 2, c2);
                    m2.invoke(e, 3, c3);
                } catch (Exception ex2) {
                    // Fallback to legacy string API
                    e.setLine(0, ChatColor.GOLD + "[PG]");
                    e.setLine(1, ChatColor.BLUE + e.getLine(1));
                    e.setLine(2, ChatColor.AQUA + e.getLine(2));
                    e.setLine(3, ChatColor.GREEN + e.getLine(3));
                }
            } catch (Exception ex) {
                // Any other reflection issue: fallback
                e.setLine(0, ChatColor.GOLD + "[PG]");
                e.setLine(1, ChatColor.BLUE + e.getLine(1));
                e.setLine(2, ChatColor.AQUA + e.getLine(2));
                e.setLine(3, ChatColor.GREEN + e.getLine(3));
            }
            
            p.sendMessage(Messages.SignSet());
        }
    }
}
