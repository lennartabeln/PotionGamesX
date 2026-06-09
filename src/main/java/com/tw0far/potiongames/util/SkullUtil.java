package com.tw0far.potiongames.util;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.block.Skull;

import java.util.UUID;

public final class SkullUtil {
    private SkullUtil() {}

    public static void setSkullOwner(Skull skull, UUID uuid) {
        if (skull == null || uuid == null) return;

        PlayerProfile profile = Bukkit.createProfile(uuid);
        if (profile != null) {
            skull.setProfile(ResolvableProfile.resolvableProfile(profile));
            skull.update();
        }
    }
}
