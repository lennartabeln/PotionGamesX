package com.tw0far.potiongames.models;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Random kit distribution at round start. */
class KitDealTest {

    private static final List<String> KITS =
            List.of("Rich Kid", "Fighter", "Healer", "Looter", "Ghost", "Tank");

    @Test
    void everyPlayerGetsADifferentKitWhileKitsAreLeft() {
        for (int seed = 0; seed < 50; seed++) {
            List<String> dealt = Lobby.dealKits(KITS, 6, new Random(seed));

            assertEquals(6, dealt.size());
            assertEquals(new HashSet<>(KITS), new HashSet<>(dealt),
                    "seed " + seed + " dealt " + dealt);
        }
    }

    @Test
    void morePlayersThanKitsRepeatButStayWithinThePool() {
        List<String> dealt = Lobby.dealKits(KITS, 9, new Random(7));

        assertEquals(9, dealt.size());
        assertTrue(KITS.containsAll(dealt));
        assertEquals(new HashSet<>(KITS), new HashSet<>(dealt.subList(0, 6)));
        assertTrue(new HashSet<>(dealt.subList(6, 9)).size() <= 3);
    }

    @Test
    void onlyTheActiveKitsAreDealt() {
        List<String> active = new ArrayList<>(KITS.subList(0, 2));

        List<String> dealt = Lobby.dealKits(active, 2, new Random(3));

        assertEquals(2, dealt.size());
        assertTrue(active.containsAll(dealt));
        assertFalse(dealt.contains("Healer"));
    }

    @Test
    void nobodyAskedMeansNothingIsDealt() {
        assertTrue(Lobby.dealKits(KITS, 0, new Random(1)).isEmpty());
    }
}
