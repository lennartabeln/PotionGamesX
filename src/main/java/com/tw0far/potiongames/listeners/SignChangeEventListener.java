package com.tw0far.potiongames.listeners;

import com.tw0far.potiongames.models.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

/**
 * Handles sign change events.
 * Manages join signs and other custom sign interactions.
 */
public class SignChangeEventListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();

        String line1 = PlainTextComponentSerializer.plainText().serialize(e.line(0));

        if (line1 == null || line1.isEmpty()) {
            return;
        }

        // Check for PotionGamesX signs
        if (line1.equalsIgnoreCase("[PG]") || line1.equalsIgnoreCase("[PotionGamesX]")) {
            // Check if player has permission
            if (!p.hasPermission("pg.sign")) {
                e.setCancelled(true);
                p.sendMessage(Messages.PermissionNoUse());
                return;
            }

            // Format the sign with colors using component API when available
            Component c0 = Messages.SignPrefix();
            String line2 = PlainTextComponentSerializer.plainText().serialize(e.line(1));
            String line3 = PlainTextComponentSerializer.plainText().serialize(e.line(2));
            String line4 = PlainTextComponentSerializer.plainText().serialize(e.line(3));
            Component c1 = Component.text(line2 == null || line2.isEmpty() ? "" : line2).color(NamedTextColor.BLUE);
            Component c2 = Component.text(line3 == null || line3.isEmpty() ? "" : line3).color(NamedTextColor.AQUA);
            Component c3 = Component.text(line4 == null || line4.isEmpty() ? "" : line4).color(NamedTextColor.GREEN);

            e.line(0, c0);
            e.line(1, c1);
            e.line(2, c2);
            e.line(3, c3);

            p.sendMessage(Messages.SignSet());
        }
    }
}
