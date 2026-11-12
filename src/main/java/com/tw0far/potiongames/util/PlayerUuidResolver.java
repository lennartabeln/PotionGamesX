package com.tw0far.potiongames.util;

import java.util.UUID;
import java.util.function.Function;

/** Resolves a player name to a UUID without forcing a profile/network lookup. */
public final class PlayerUuidResolver {
    private PlayerUuidResolver() { }

    public static UUID resolve(String name, Function<String, UUID> onlineLookup,
            Function<String, UUID> cachedOfflineLookup) {
        if (name == null || name.isBlank()) {
            return null;
        }
        UUID uuid = onlineLookup.apply(name);
        return uuid != null ? uuid : cachedOfflineLookup.apply(name);
    }
}
