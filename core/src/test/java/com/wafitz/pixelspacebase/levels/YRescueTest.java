package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.actors.buffs.YRescueJourney;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.npcs.YRescuer;
import com.watabou.utils.Bundle;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.reflect.Field;
import static org.junit.Assert.*;

public class YRescueTest {
    @Test public void destinationsIncludeUnexploredCommandButNeverBossesOrEnding() {
        int before = Statistics.deepestFloor;
        try {
            Statistics.deepestFloor = 2;
            ArrayList<Integer> destinations = YRescuer.rescueDestinations(1);
            assertTrue(destinations.contains(24));
            assertTrue(destinations.contains(6));
            assertFalse(destinations.contains(1));
            assertFalse(destinations.contains(26));
            for (int depth : destinations) {
                assertFalse(SpacebaseRun.bossLevel(depth));
                assertTrue(SpacebaseRun.canVisitDepth(depth));
            }
        } finally { Statistics.deepestFloor = before; }
    }

    @Test public void ticketPersistsAndAnotherFallPreservesAnActiveOrigin() {
        YRescueJourney original = new YRescueJourney();
        original.recordFall(12);
        original.rescueDepth = 24;
        original.phase = YRescueJourney.Phase.VISITING;
        IntBundle saved = new IntBundle();
        original.storeInBundle(saved);
        YRescueJourney restored = new YRescueJourney();
        restored.restoreFromBundle(saved);
        assertEquals(12, restored.sourceDepth);
        assertEquals(24, restored.rescueDepth);
        restored.recordFall(23);
        assertEquals(12, restored.sourceDepth);
        assertEquals(24, restored.rescueDepth);
    }

    @Test public void legacyTicketDoesNotInventDeckZero() {
        YRescueJourney ticket = new YRescueJourney();
        ticket.restoreFromBundle(new IntBundle());
        assertEquals(-1, ticket.sourceDepth);
        assertEquals(-1, ticket.rescueDepth);
    }

    @Test public void randomPlacementUsesConnectedFloorButAllowsNearbyEnemies() throws Exception {
        boolean[] before = Level.passable;
        try {
            TestLevel level = new TestLevel();
            Level.passable = new boolean[level.length()];
            for (int cell : new int[]{10, 11, 12, 19, 20, 21, 70}) Level.passable[cell] = true;
            // Position-only fixture: avoid Mob's localized-name initializer, which needs Android.
            Class<?> allocator = Class.forName("sun.misc.Unsafe");
            Field field = allocator.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Mob enemy = (Mob) allocator.getMethod("allocateInstance", Class.class)
                    .invoke(field.get(null), TestMob.class);
            enemy.pos = 11;
            level.mobs.add(enemy);
            boolean nearEnemy = false;
            for (int attempt = 0; attempt < 200; attempt++) {
                int cell = YRescuer.randomReachableCell(level, 10);
                assertTrue(cell == 12 || cell == 19 || cell == 20 || cell == 21);
                if (cell == 12 || cell == 20) nearEnemy = true;
            }
            assertTrue(nearEnemy);
        } finally { Level.passable = before; }
    }

    private static class IntBundle extends Bundle {
        private final HashMap<String, Integer> values = new HashMap<>();
        private final HashMap<String, String> strings = new HashMap<>();
        private final HashMap<String, Boolean> flags = new HashMap<>();
        @Override public void put(String key, String value) { strings.put(key, value); }
        @Override public String getString(String key) { return strings.get(key); }
        @Override public void put(String key, boolean value) { flags.put(key, value); }
        @Override public boolean getBoolean(String key) { return Boolean.TRUE.equals(flags.get(key)); }
        @Override public void put(String key, int value) { values.put(key, value); }
        @Override public int getInt(String key) { return values.containsKey(key) ? values.get(key) : 0; }
        @Override public boolean contains(String key) { return values.containsKey(key) || strings.containsKey(key) || flags.containsKey(key); }
        @Override public void put(String key, float value) { }
        @Override public float getFloat(String key) { return 0; }
    }

    private static class TestMob extends Mob { }

    private static class TestLevel extends Level {
        TestLevel() {
            width = height = 9;
            length = width * height;
            map = new int[length];
            mobs = new java.util.HashSet<>();
            entrance = 10;
        }
        @Override protected boolean build() { return true; }
        @Override protected void decorate() { }
        @Override protected void createMobs() { }
        @Override protected void createItems() { }
    }
}
