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
    void everyDrawStaysInsideTheActiveKits() {
        List<String> dealt = Lobby.dealKits(KITS, 40, new Random(11));

        assertEquals(40, dealt.size());
        assertTrue(KITS.containsAll(dealt));
    }

    @Test
    void kitsAreNotLimitedSoDuplicatesAreAllowed() {
        // 40 draws from 6 kits: a duplicate-free result is practically impossible.
        List<String> dealt = Lobby.dealKits(KITS, 40, new Random(5));

        assertTrue(dealt.size() > new HashSet<>(dealt).size());
    }

    @Test
    void drawsAreIndependentPerPlayer() {
        List<String> dealt = Lobby.dealKits(KITS, 2, new Random(3));

        assertEquals(2, dealt.size());
        assertTrue(KITS.containsAll(dealt));
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
