package com.wafitz.pixelspacebase.levels;

import org.junit.Test;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;

public class StationFootprintTest {
    @Test
    public void removesOldUnusedPartitionWallsButKeepsTheMainRoomHull() {
        TestLevel level = new TestLevel();
        level.cleanWalls();
        assertEquals(Terrain.CHASM, level.map[27]);
        assertFalse(Level.discoverable[27]);
        assertTrue(Level.pit[27]);
        assertEquals(Terrain.WALL, level.map[25]);
        assertTrue(Level.discoverable[25]);
        assertEquals(Terrain.EMPTY, level.map[33]);
    }

    @Test
    public void hullIsMappedAtStartWithoutRevealingTheInterior() {
        TestLevel level = new TestLevel();
        level.cleanWalls();
        assertTrue(level.mapped[21]);
        assertFalse(level.mapped[33]);
        assertFalse(level.visited[33]);
        assertFalse(Level.fieldOfView[33]);
    }

    @Test
    public void openSpaceBesideTheHullDoesNotReceiveFog() {
        TestLevel level = new TestLevel();
        level.cleanWalls();
        assertFalse(Level.discoverable[20]);
        assertFalse(Level.discoverable[26]);
    }

    @Test
    public void fixedMapMaskDoesNotWrapNeighboursAcrossRows() {
        TestLevel level = new TestLevel();
        level.rooms = null;
        Arrays.fill(level.map, Terrain.WALL);
        level.map[19] = Terrain.EMPTY;
        level.buildFlagMaps();
        level.cleanWalls();
        assertTrue(Level.discoverable[18]);
        assertFalse(Level.discoverable[20]);
    }

    @Test
    public void intentionalFallChamberAndItsWallRingSurviveCleanup() throws Exception {
        TestLevel level = new TestLevel();
        Field center = Level.class.getDeclaredField("doorlessRoomCenter");
        center.setAccessible(true);
        center.setInt(level, 77);
        Field radius = Level.class.getDeclaredField("doorlessRoomRadius");
        radius.setAccessible(true);
        radius.setInt(level, 0);
        level.map[77] = Terrain.EMPTY;
        level.cleanWalls();
        assertTrue(Level.discoverable[77]);
        assertEquals(Terrain.WALL, level.map[76]);
        assertEquals(Terrain.WALL, level.map[66]);
    }

    private static class TestLevel extends MaintenanceLevel {
        TestLevel() {
            width = height = 10;
            length = width * height;
            map = new int[length];
            Arrays.fill(map, Terrain.WALL);
            visited = new boolean[length];
            mapped = new boolean[length];
            rooms = new ArrayList<>();
            Room room = new Room();
            room.set(1, 1, 5, 5);
            room.type = Room.Type.STANDARD;
            rooms.add(room);
            Room unused = new Room();
            unused.set(5, 1, 8, 5);
            rooms.add(unused);
            for (int y = 2; y < 5; y++) {
                for (int x = 2; x < 5; x++) map[x + y * width] = Terrain.EMPTY;
            }
            buildFlagMaps();
        }
        @Override protected boolean build() { return true; }
        @Override protected void decorate() { }
        @Override protected void createMobs() { }
        @Override protected void createItems() { }
    }
}
