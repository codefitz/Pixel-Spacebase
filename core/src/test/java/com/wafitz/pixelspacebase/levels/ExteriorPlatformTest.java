package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.levels.painters.ExteriorPlatformPainter;
import org.junit.Test;
import java.util.Arrays;
import static org.junit.Assert.*;

public class ExteriorPlatformTest {
    @Test public void skipsFirstFloorAndBossLevels() {
        assertFalse(RegularLevel.shouldAssignExteriorPlatform(1, false));
        assertFalse(RegularLevel.shouldAssignExteriorPlatform(21, false));
        assertFalse(RegularLevel.shouldAssignExteriorPlatform(5, true));
        assertTrue(RegularLevel.shouldAssignExteriorPlatform(2, false));
    }

    @Test public void platformFallsAlwaysLandInSealedRoom() {
        TestLevel level = new TestLevel();
        for (int attempt = 0; attempt < 100; attempt++) {
            assertEquals(85, level.fallLandingCell(true));
        }
    }

    @Test public void ordinaryFallsUseSealedRoomChanceOrSafeFloorOnly() {
        TestLevel level = new TestLevel();
        for (int attempt = 0; attempt < 100; attempt++) {
            int cell = level.fallLandingCell(false);
            assertTrue(cell == 85 || cell == 17);
        }
    }

    @Test public void reservesUnusedRoomWithoutReplacingRewardsOrMainRoute() {
        Room main = room(1, 1, 7, 7, Room.Type.STANDARD);
        Room unused = room(7, 1, 13, 7, Room.Type.NULL);
        main.addNeigbour(unused);
        Room reward = room(1, 7, 7, 13, Room.Type.VAULT);
        assertTrue(RegularLevel.assignExteriorPlatform(Arrays.asList(main, unused, reward)));
        assertEquals(Room.Type.STANDARD, main.type);
        assertEquals(Room.Type.VAULT, reward.type);
        assertEquals(Room.Type.EXTERIOR_PLATFORM, unused.type);
        assertTrue(main.connected.containsKey(unused));
        assertEquals(1, unused.connected.size());
    }

    @Test public void rejectsLayoutsWithoutAnUnusedRoom() {
        assertFalse(RegularLevel.assignExteriorPlatform(Arrays.asList(
                room(1, 1, 7, 7, Room.Type.STANDARD))));
    }

    @Test public void everyDoorOrientationHasWalkableDeckAndJumpEdge() {
        for (int side = 0; side < 4; side++) {
            TestLevel level = new TestLevel();
            Room room = room(2, 2, 8, 8, Room.Type.EXTERIOR_PLATFORM);
            Room.Door door = side == 0 ? new Room.Door(5, 2)
                    : side == 1 ? new Room.Door(8, 5)
                    : side == 2 ? new Room.Door(5, 8) : new Room.Door(2, 5);
            room.connected.put(new Room(), door);
            ExteriorPlatformPainter.paint(level, room);
            int dx = side == 1 ? -1 : side == 3 ? 1 : 0;
            int dy = side == 0 ? 1 : side == 2 ? -1 : 0;
            assertEquals(Room.Door.Type.UNLOCKED, door.type);
            for (int step = 1; step <= 2; step++) {
                int cell = door.x + dx * step + (door.y + dy * step) * level.width();
                assertEquals(Terrain.EMPTY_SP, level.map[cell]);
                assertTrue(level.isVacuum(cell));
            }
            int edge = door.x + dx * 3 + (door.y + dy * 3) * level.width();
            assertEquals(Terrain.CHASM, level.map[edge]);
            assertEquals(Terrain.WALL, level.map[room.left + room.top * level.width()]);
        }
    }

    private static Room room(int left, int top, int right, int bottom, Room.Type type) {
        Room room = new Room();
        room.set(left, top, right, bottom);
        room.type = type;
        return room;
    }

    private static class TestLevel extends Level {
        TestLevel() {
            width = height = 16;
            length = width * height;
            map = new int[length];
            vacuum = new boolean[length];
            Arrays.fill(map, Terrain.WALL);
        }
        @Override protected boolean build() { return true; }
        @Override protected void decorate() { }
        @Override protected void createMobs() { }
        @Override protected void createItems() { }
        @Override public int doorlessRoomLandingCell() { return 85; }
        @Override public int randomRespawnCell() { return 17; }
    }
}
