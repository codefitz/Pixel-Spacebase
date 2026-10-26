package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Leonard;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

public class EngineeringSparksTest {
    @Test public void onlyTheLocalQuestDeckGetsSparksRegardlessOfCurrentDepth() {
        int previousDepth = SpacebaseRun.depth;
        try {
            TestLevel questDeck = new TestLevel();
            Room room = new Room();
            room.type = Room.Type.LEONARD;
            questDeck.rooms.add(room);
            TestLevel ordinaryDeck = new TestLevel();
            for (int depth = 11; depth <= 15; depth++) {
                SpacebaseRun.depth = depth;
                Group questVisuals = new Group();
                EngineeringLevel.addCavesVisuals(questDeck, questVisuals);
                assertEquals(2, questVisuals.length);
                Group ordinaryVisuals = new Group();
                EngineeringLevel.addCavesVisuals(ordinaryDeck, ordinaryVisuals);
                assertEquals(0, ordinaryVisuals.length);
            }
        } finally { SpacebaseRun.depth = previousDepth; }
    }

    @Test public void restoredLeonardNpcWorksWithoutRoomMetadata() throws Exception {
        TestLevel level = new TestLevel();
        level.rooms = null;
        level.mobs.add(allocate(Leonard.class));
        Group visuals = new Group();
        EngineeringLevel.addCavesVisuals(level, visuals);
        assertEquals(2, visuals.length);
    }

    @Test public void restoredQuestRoomWorksWithoutAnNpcAndDoesNotChangeTerrain() {
        TestLevel level = new TestLevel();
        Room room = new Room();
        room.type = Room.Type.LEONARD;
        level.rooms.add(room);
        level.mobs = null;
        int[] before = level.map.clone();
        Group visuals = new Group();
        EngineeringLevel.addCavesVisuals(level, visuals);
        assertEquals(2, visuals.length);
        assertArrayEquals(before, level.map);
    }

    @Test public void bossVisualsNeverInheritEngineeringQuestSparks() throws Exception {
        EngineeringBossLevel boss = allocate(EngineeringBossLevel.class);
        boss.mobs = new HashSet<>();
        boss.mobs.add(allocate(Leonard.class));
        Group visuals = new Group();
        EngineeringLevel.addCavesVisuals(boss, visuals);
        assertEquals(0, visuals.length);
    }

    @Test public void salvagedPanelsStillStopSparking() throws Exception {
        Level previousLevel = SpacebaseRun.level;
        boolean[] previousVisible = SpacebaseRun.visible;
        float previousElapsed = Game.elapsed;
        try {
            TestLevel level = new TestLevel();
            level.mobs.add(allocate(Leonard.class));
            SpacebaseRun.level = level;
            SpacebaseRun.visible = new boolean[level.length()];
            Arrays.fill(SpacebaseRun.visible, true);
            Visuals visuals = new Visuals();
            EngineeringLevel.addCavesVisuals(level, visuals);
            level.map[0] = level.map[8] = Terrain.WALL;
            Game.elapsed = 3f; // Exceeds the maximum initial particle delay.
            visuals.update();
            assertFalse(visuals.effect(0).exists);
            assertFalse(visuals.effect(1).exists);
        } finally {
            SpacebaseRun.level = previousLevel;
            SpacebaseRun.visible = previousVisible;
            Game.elapsed = previousElapsed;
        }
    }

    private static <T> T allocate(Class<T> type) throws Exception {
        Class<?> unsafe = Class.forName("sun.misc.Unsafe");
        Field field = unsafe.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        return type.cast(unsafe.getMethod("allocateInstance", Class.class).invoke(field.get(null), type));
    }

    private static class TestLevel extends EngineeringLevel {
        TestLevel() {
            width = height = 3;
            length = 9;
            map = new int[length];
            Arrays.fill(map, Terrain.EMPTY);
            map[0] = map[8] = Terrain.WALL_DECO;
            rooms = new ArrayList<>();
            mobs = new HashSet<>();
        }
    }

    private static class Visuals extends Group {
        Group effect(int index) { return (Group) members.get(index); }
    }
}
