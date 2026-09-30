package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.levels.painters.ChangingRoomPainter;
import com.wafitz.pixelspacebase.ui.ChangingRoomTile;
import com.wafitz.pixelspacebase.ui.CustomTileVisual;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HabitatChangingRoomTest {

    @Test
    public void assignsExactlyOneConnectedRoomWithoutReplacingImportantRooms() {
        for (int seed = 0; seed < 100; seed++) {
            Random.seed(seed);
            Room entrance = room(1, 1, 7, 7, Room.Type.ENTRANCE);
            Room exit = room(8, 1, 14, 7, Room.Type.EXIT);
            Room workshop = room(1, 8, 8, 15, Room.Type.WORKSHOP);
            Room small = room(9, 8, 12, 11, Room.Type.STANDARD);
            Room disconnected = room(1, 16, 8, 23, Room.Type.STANDARD);
            Room candidateA = room(9, 12, 14, 17, Room.Type.STANDARD);
            Room candidateB = room(9, 18, 13, 22, Room.Type.STANDARD);
            candidateA.connected.put(entrance, new Room.Door(11, 12));
            candidateB.connected.put(exit, new Room.Door(11, 18));
            List<Room> rooms = Arrays.asList(entrance, exit, workshop, small,
                    disconnected, candidateA, candidateB);

            assertTrue(HabitationRingLevel.assignChangingRoom(rooms));
            assertTrue(HabitationRingLevel.assignChangingRoom(rooms));
            int count = 0;
            for (Room r : rooms) if (r.type == Room.Type.CHANGING_ROOM) count++;
            assertEquals(1, count);
            assertEquals(Room.Type.ENTRANCE, entrance.type);
            assertEquals(Room.Type.EXIT, exit.type);
            assertEquals(Room.Type.WORKSHOP, workshop.type);
            assertEquals(Room.Type.STANDARD, small.type);
            assertEquals(Room.Type.STANDARD, disconnected.type);
        }
        Random.seed();
    }

    @Test
    public void failedReservationRequestsAnotherLayout() {
        assertFalse(HabitationRingLevel.assignChangingRoom(Arrays.asList(
                room(1, 1, 9, 9, Room.Type.ENTRANCE),
                room(10, 1, 13, 4, Room.Type.STANDARD))));
    }

    @Test
    public void paintsShowersWaterAndTwoSteamVentsAroundEveryEntranceOrientation() {
        for (int size = 4; size <= 10; size++) {
            for (int side = 0; side < 4; side++) {
                TestLevel level = new TestLevel(16, 16);
                Room room = room(2, 2, 2 + size, 2 + size, Room.Type.CHANGING_ROOM);
                Room.Door door = side == 0 ? new Room.Door(3, room.top)
                        : side == 1 ? new Room.Door(room.right, 3)
                        : side == 2 ? new Room.Door(3, room.bottom)
                        : new Room.Door(room.left, 3);
                room.connected.put(new Room(), door);
                ChangingRoomPainter.paint(level, room);

                assertEquals(2, count(level, Terrain.WATER));
                assertEquals(2, count(level, Terrain.INACTIVE_VENT));
                int showers = 0;
                int vents = 0;
                int lockers = 0;
                for (CustomTileVisual visual : level.customTiles) {
                    ChangingRoomTile tile = (ChangingRoomTile) visual;
                    int cell = tile.tileX + tile.tileY * level.width();
                    assertTrue(level.map[cell] != Terrain.WATER);
                    assertFalse(tile.tileX == door.x && tile.tileY == door.y);
                    if (tile.kind() == ChangingRoomTile.SHOWER) showers++;
                    if (tile.kind() == ChangingRoomTile.VENT) vents++;
                    if (tile.kind() == ChangingRoomTile.LOCKER) lockers++;
                }
                assertEquals(2, showers);
                assertEquals(2, vents);
                assertTrue(lockers >= 2);
            }
        }
    }

    @Test
    public void removesWaterFromOtherPaintersWhileKeepingBasinsAndImportantTerrain() {
        TestLevel level = new TestLevel(16, 16);
        Room room = room(2, 2, 8, 8, Room.Type.CHANGING_ROOM);
        room.connected.put(new Room(), new Room.Door(5, 8));
        ChangingRoomPainter.paint(level, room);
        level.map[0] = Terrain.WATER;
        level.map[10 + 10 * level.width()] = Terrain.WATER;
        int exit = 12 + 12 * level.width();
        level.map[exit] = Terrain.EXIT;
        ChangingRoomPainter.confineWater(level, room);

        assertEquals(2, count(level, Terrain.WATER));
        assertEquals(Terrain.EMPTY, level.map[0]);
        assertEquals(Terrain.EMPTY, level.map[10 + 10 * level.width()]);
        assertEquals(Terrain.EXIT, level.map[exit]);
        for (int cell = 0; cell < level.length(); cell++) {
            if (level.map[cell] == Terrain.WATER) {
                assertTrue(room.inside(new Point(cell % level.width(), cell / level.width())));
            }
        }
    }

    private static Room room(int left, int top, int right, int bottom, Room.Type type) {
        Room room = new Room();
        room.set(left, top, right, bottom);
        room.type = type;
        return room;
    }

    private static int count(Level level, int terrain) {
        int result = 0;
        for (int tile : level.map) if (tile == terrain) result++;
        return result;
    }

    private static class TestLevel extends Level {
        TestLevel(int levelWidth, int levelHeight) {
            width = levelWidth;
            height = levelHeight;
            length = width * height;
            map = new int[length];
            Arrays.fill(map, Terrain.WALL);
            customTiles = new HashSet<>();
        }

        @Override protected boolean build() { return true; }
        @Override protected void decorate() { }
        @Override protected void createMobs() { }
        @Override protected void createItems() { }
    }
}
