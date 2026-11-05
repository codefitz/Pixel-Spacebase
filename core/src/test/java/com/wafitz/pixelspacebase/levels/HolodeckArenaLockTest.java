package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.HolodeckMonarch;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.watabou.utils.SparseArray;
import com.wafitz.pixelspacebase.mines.Mine;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class HolodeckArenaLockTest {
    private static final int CHAMBER = 5 * 32 + 5;
    private static final int ARENA = 3 * 32 + 15;
    private static final int DOOR = 17 * 32 + 15;

    @Test public void sealedChamberIsNeverAnArenaEntryOrBossSpawn() throws Exception {
        for (Level level : new Level[]{new HolodeckBossLevel(), new EngineeringBossLevel()}) {
            fixture(level);
            if (level instanceof HolodeckBossLevel) set(level, "arenaDoor", DOOR);
            Method arena = level.getClass().getDeclaredMethod("outsideEntraceRoom", int.class);
            arena.setAccessible(true);
            assertTrue((Boolean) arena.invoke(level, ARENA));
            assertFalse((Boolean) arena.invoke(level, CHAMBER));
            Hero previousHero = SpacebaseRun.hero;
            try {
                SpacebaseRun.hero = allocate(TestHero.class);
                SpacebaseRun.hero.pos = CHAMBER;
                level.press(CHAMBER, SpacebaseRun.hero);
                assertFalse(level.locked);
                assertTrue(level.mobs.isEmpty());
            } finally { SpacebaseRun.hero = previousHero; }
        }
    }

    @Test public void restoredTrappedMonarchMovesIntoArenaWithoutResettingFight() throws Exception {
        HolodeckBossLevel level = new HolodeckBossLevel();
        fixture(level);
        set(level, "arenaDoor", DOOR);
        set(level, "enteredArena", true);
        level.locked = true;
        level.map[DOOR] = Terrain.LOCKED_DOOR;
        HolodeckMonarch monarch = allocate(HolodeckMonarch.class);
        monarch.pos = CHAMBER;
        monarch.HP = 73;
        level.mobs.add(monarch);
        level.restoreArenaState();
        assertEquals(ARENA, monarch.pos);
        assertEquals(73, monarch.HP);
        assertEquals(1, level.mobs.size());
        assertTrue(level.locked);
        assertEquals(Terrain.LOCKED_DOOR, level.map[DOOR]);
        level.restoreArenaState();
        assertEquals(ARENA, monarch.pos);
    }

    @Test public void completedSaveReopensStaleDoorAndDoesNotRestartBoss() throws Exception {
        HolodeckBossLevel level = new HolodeckBossLevel();
        fixture(level);
        set(level, "arenaDoor", DOOR);
        level.rescueBossDefeated = true;
        level.locked = true;
        level.map[DOOR] = Terrain.LOCKED_DOOR;
        level.restoreArenaState();
        assertFalse(level.locked);
        assertEquals(Terrain.DOOR, level.map[DOOR]);
        assertTrue(Level.passable[DOOR]);
        Hero previousHero = SpacebaseRun.hero;
        try {
            SpacebaseRun.hero = allocate(TestHero.class);
            SpacebaseRun.hero.pos = ARENA;
            level.press(ARENA, SpacebaseRun.hero);
            assertFalse(level.locked);
            assertTrue(level.mobs.isEmpty());
        } finally { SpacebaseRun.hero = previousHero; }
    }

    private void fixture(Level level) throws Exception {
        level.width = level.height = 32;
        level.length = 32 * 32;
        level.map = new int[level.length];
        Arrays.fill(level.map, Terrain.WALL);
        level.map[CHAMBER] = level.map[ARENA] = Terrain.EMPTY;
        level.mobs = new HashSet<Mob>();
        level.vents = new SparseArray<>();
        level.mines = new SparseArray<Mine>() {
            @Override public Mine get(int cell) { return null; }
        };
        set(level, "doorlessRoomCenter", CHAMBER);
        set(level, "doorlessRoomRadius", 0);
        level.buildFlagMaps();
    }

    private void set(Level level, String name, Object value) throws Exception {
        Class<?> type = name.startsWith("doorless") ? Level.class : level.getClass();
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        field.set(level, value);
    }

    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        Field field = unsafe.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return type.cast(unsafe.getMethod("allocateInstance", Class.class).invoke(field.get(null), type));
    }

    private static class TestHero extends Hero {
        @Override public <T extends Buff> T buff(Class<T> type) { return null; }
    }
}
