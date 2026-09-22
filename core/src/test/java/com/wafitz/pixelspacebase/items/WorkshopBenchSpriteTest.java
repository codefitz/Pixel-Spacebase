package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class WorkshopBenchSpriteTest {

    @Test
    public void workshopBenchesUseDistinctImages() {
        Heap upgrade = new Heap();
        upgrade.type = Heap.Type.WORKSHOP_UPGRADE;
        Heap breakdown = new Heap();
        breakdown.type = Heap.Type.MAKER_BENCH;
        Heap terminal = new Heap();
        terminal.type = Heap.Type.CMD_TERMINAL;

        assertEquals(ItemSpriteSheet.UPGRADE_BENCH, upgrade.image());
        assertEquals(ItemSpriteSheet.BREAKDOWN_BENCH, breakdown.image());
        assertEquals(ItemSpriteSheet.REDTERMINAL, terminal.image());
        assertNotEquals(upgrade.image(), breakdown.image());
        assertNotEquals(upgrade.image(), terminal.image());
        assertNotEquals(breakdown.image(), terminal.image());
    }
}
