package com.tw0far.potiongames.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The shop price depends on the player's kit: matching kit pays the sale price,
 * everyone else the normal cost. A missing sale must never make the potion free.
 */
class ShopPriceTest {

    private ItemStateManager manager(int cost, int sale, String kit) {
        ItemStateManager manager = new ItemStateManager();
        manager.getShopKitsRaw().add(kit);
        manager.getShopCostsRaw().add(cost);
        manager.getShopSalesRaw().add(sale);
        return manager;
    }

    @Test
    void matchingKitPaysTheSalePrice() {
        assertEquals(2, manager(4, 2, "Looter").getShopPrice(0, "Looter"));
    }

    @Test
    void otherKitPaysTheNormalCost() {
        assertEquals(4, manager(4, 2, "Looter").getShopPrice(0, "Tank"));
    }

    @Test
    void noKitPaysTheNormalCost() {
        assertEquals(4, manager(4, 2, "Looter").getShopPrice(0, null));
    }

    @Test
    void zeroSaleFallsBackToCostInsteadOfBeingFree() {
        assertEquals(4, manager(4, 0, "Looter").getShopPrice(0, "Looter"));
    }

    @Test
    void missingSaleFallsBackToCost() {
        assertEquals(4, manager(4, -1, "Looter").getShopPrice(0, "Looter"));
    }

    @Test
    void missingKitInConfigNeverMatches() {
        assertEquals(5, manager(5, 1, null).getShopPrice(0, "Fighter"));
    }

    @Test
    void outOfRangeIndexIsFreeOfCharge() {
        assertEquals(0, new ItemStateManager().getShopPrice(3, "Fighter"));
    }
}
