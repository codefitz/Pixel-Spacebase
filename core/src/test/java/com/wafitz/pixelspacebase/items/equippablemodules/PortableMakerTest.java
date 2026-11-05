package com.wafitz.pixelspacebase.items.equippablemodules;

import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.watabou.utils.Bundle;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class PortableMakerTest {
    @Test public void unsetClearsMarkerWithoutChargeOrEquipmentAndKeepsTunnelAvailable() throws Exception {
        TestMaker maker = allocate(TestMaker.class);
        TestHero hero = allocate(TestHero.class);
        maker.returnDepth = 7;
        assertTrue(maker.actions(hero).contains("ZAP"));
        assertTrue(maker.actions(hero).contains("RETURN"));
        assertTrue(maker.actions(hero).contains("UNSET"));

        maker.execute(hero, new String("UNSET"));

        assertEquals(-1, maker.returnDepth);
        assertEquals(0, maker.charge);
        assertNull(maker.glowing());
        assertFalse(maker.actions(hero).contains("RETURN"));
        assertFalse(maker.actions(hero).contains("UNSET"));
        assertTrue(maker.actions(hero).contains("ZAP"));
        assertTrue(maker.actions(hero).contains("SET"));
        // A stale RETURN action after clearing must not attempt travel.
        maker.execute(hero, new String("RETURN"));
    }

    @Test public void oldMarkerRestoresAndCanBeCleared() throws Exception {
        TestMaker maker = allocate(TestMaker.class);
        maker.restoreFromBundle(savedMarker(9));
        assertEquals(9, maker.returnDepth);
        assertNotNull(maker.glowing());
        maker.execute(allocate(TestHero.class), "UNSET");
        assertEquals(-1, maker.returnDepth);
    }

    @Test public void missingOrClearedMarkerDoesNotBecomeDeckZero() throws Exception {
        for (int depth : new int[]{0, -1}) {
            TestMaker maker = allocate(TestMaker.class);
            maker.restoreFromBundle(savedMarker(depth));
            assertEquals(-1, maker.returnDepth);
            assertNull(maker.glowing());
        }
    }

    private Bundle savedMarker(final int depth) {
        return new Bundle() {
            @Override public boolean contains(String key) { return "depth".equals(key) && depth != 0; }
            @Override public int getInt(String key) { return "depth".equals(key) ? depth : 0; }
            @Override public boolean getBoolean(String key) { return false; }
            @Override public float getFloat(String key) { return 0; }
        };
    }

    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        Field field = unsafe.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return type.cast(unsafe.getMethod("allocateInstance", Class.class).invoke(field.get(null), type));
    }

    private static class TestMaker extends PortableMaker {
        @Override public boolean isEquipped(Hero hero) { return false; }
        @Override public void updateQuickslot() { }
    }

    private static class TestHero extends Hero {
        @Override public <T extends Buff> T buff(Class<T> type) { return null; }
    }
}
