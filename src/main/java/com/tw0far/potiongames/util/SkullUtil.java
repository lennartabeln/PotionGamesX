package com.tw0far.potiongames.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

public final class SkullUtil {
    private SkullUtil() {}

    public static void setSkullOwner(Skull skull, UUID uuid) {
        if (skull == null || uuid == null) return;

        // Try modern API: Skull#setOwnerProfile(PlayerProfile)
        try {
            Class<?> profileClass = Class.forName("org.bukkit.profile.PlayerProfile");
            Method setOwnerProfile = null;
            try {
                setOwnerProfile = skull.getClass().getMethod("setOwnerProfile", profileClass);
            } catch (NoSuchMethodException ignored) {}

            Object profile = null;
            if (setOwnerProfile != null) {
                // Try server factory methods: Server#createPlayerProfile(UUID) or similar
                Object server = Bukkit.getServer();
                Method creator = null;
                for (Method m : server.getClass().getMethods()) {
                    if (m.getReturnType().equals(profileClass) && m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(UUID.class)) {
                        creator = m;
                        break;
                    }
                }
                if (creator == null) {
                    // Try static helpers on Bukkit class
                    for (Method m : Bukkit.class.getMethods()) {
                        if (m.getReturnType().equals(profileClass) && m.getParameterCount() == 1 && m.getParameterTypes()[0].equals(UUID.class)) {
                            creator = m;
                            break;
                        }
                    }
                }

                if (creator != null) {
                    if (creator.getParameterCount() == 1) {
                        if (creator.getDeclaringClass().equals(Bukkit.class)) {
                            profile = creator.invoke(null, uuid);
                        } else {
                            profile = creator.invoke(server, uuid);
                        }
                    }
                }

                if (profile != null) {
                    setOwnerProfile.invoke(skull, profile);
                    skull.update();
                    return;
                }
            }
        } catch (Throwable t) {
            // ignore and fallback
            Bukkit.getLogger().log(Level.FINE, "SkullUtil: modern profile set failed", t);
        }

        // Fallback to deprecated API: setOwningPlayer
        try {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            voidSetOwningPlayer(skull, op);
            skull.update();
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.WARNING, "SkullUtil: failed to set skull owner", t);
        }
    }

    // Separate wrapper to avoid deprecation warning at call site
    @SuppressWarnings("deprecation")
    private static void voidSetOwningPlayer(Skull skull, OfflinePlayer player) {
        skull.setOwningPlayer(player);
    }
}
