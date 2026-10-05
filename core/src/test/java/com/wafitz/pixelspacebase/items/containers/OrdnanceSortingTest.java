package com.wafitz.pixelspacebase.items.containers;

import com.wafitz.pixelspacebase.QuickSlot;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Item;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class OrdnanceSortingTest {
    @Test public void revealedGrenadeMovesImmediatelyAndKeepsQuickslot() throws Exception {
        checkSorting(false, false);
    }

    @Test public void revealedGrenadeMergesEvenWhenKitIsFull() throws Exception {
        checkSorting(true, true);
    }

    @Test public void fullKitLeavesGrenadeInOriginalContainer() throws Exception {
        checkSorting(true, false);
    }

    private void checkSorting(boolean full, boolean merge) throws Exception {
        Hero oldHero = SpacebaseRun.hero;
        QuickSlot oldSlots = SpacebaseRun.quickslot;
        SpacebaseRun.hero = null;
        SpacebaseRun.quickslot = new QuickSlot();
        try {
            Container backpack = allocate(Container.class);
            backpack.items = new ArrayList<>();
            OrdnanceKit kit = allocate(OrdnanceKit.class);
            kit.items = new ArrayList<>(); kit.size = full ? 1 : 12;
            Container source = allocate(Container.class);
            source.items = new ArrayList<>();
            backpack.items.add(kit); backpack.items.add(source);
            TestItem grenade = allocate(TestItem.class);
            grenade.ordnance = true; grenade.stackable = true; grenade.quantity(3);
            source.items.add(grenade);
            TestItem goo = allocate(TestItem.class);
            goo.quantity(1); source.items.add(goo);
            TestItem existing = allocate(TestItem.class);
            existing.ordnance = merge; existing.stackable = true; existing.quantity(2);
            if (full) kit.items.add(existing);
            SpacebaseRun.quickslot.setSlot(0, grenade);
            backpack.sortOrdnanceItems();
            assertTrue(source.items.contains(goo));
            if (full && !merge) {
                assertTrue(source.items.contains(grenade));
                assertSame(grenade, SpacebaseRun.quickslot.getItem(0));
                assertEquals(3, grenade.quantity());
            } else {
                assertFalse(source.items.contains(grenade));
                assertEquals(1, kit.items.size());
                Item stored = kit.items.get(0);
                assertEquals(merge ? 5 : 3, stored.quantity());
                assertSame(stored, SpacebaseRun.quickslot.getItem(0));
                backpack.sortOrdnanceItems();
                assertEquals(merge ? 5 : 3, stored.quantity());
            }
        } finally {
            SpacebaseRun.hero = oldHero;
            SpacebaseRun.quickslot = oldSlots;
        }
    }

    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        Field field = unsafe.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return type.cast(unsafe.getMethod("allocateInstance", Class.class).invoke(field.get(null), type));
    }

    private static class TestItem extends Item {
        boolean ordnance;
        @Override public boolean goesInOrdnanceKit() { return ordnance; }
        @Override public boolean isSimilar(Item item) {
            return item instanceof TestItem && ordnance == ((TestItem) item).ordnance;
        }
        @Override public void updateQuickslot() { }
    }
}
