package com.wafitz.pixelspacebase.levels.painters;

import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.ui.ChangingRoomTile;
import com.watabou.utils.Point;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** A walkable changing room with wet shower basins and harmless steam grilles. */
public class ChangingRoomPainter extends Painter {

    public static void paint(Level level, Room room) {
        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.EMPTY_SP);
        for (Room.Door door : room.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }

        Set<Integer> fixtures = new HashSet<>();
        ArrayList<Point> walls = new ArrayList<>();
        // Prefer showers along the far wall, leaving a dry aisle between basins.
        walls.add(new Point(room.left + 1, room.top));
        walls.add(new Point(room.right - 1, room.top));
        for (int x = room.left + 1; x < room.right; x++) walls.add(new Point(x, room.top));
        for (int y = room.top + 1; y < room.bottom; y++) {
            walls.add(new Point(room.left, y));
            walls.add(new Point(room.right, y));
        }
        for (int x = room.left + 1; x < room.right; x++) walls.add(new Point(x, room.bottom));

        int showers = 0;
        for (Point wall : walls) {
            if (showers == 2) break;
            int wallCell = level.pointToCell(wall);
            if (isDoor(room, wall.x, wall.y) || fixtures.contains(wallCell)) continue;
            int wetX = Math.max(room.left + 1, Math.min(room.right - 1, wall.x));
            int wetY = Math.max(room.top + 1, Math.min(room.bottom - 1, wall.y));
            int basin = wetX + wetY * level.width();
            if (level.map[basin] == Terrain.WATER) continue;
            level.map[basin] = Terrain.WATER;
            fixtures.add(wallCell);
            addTile(level, wall.x, wall.y, ChangingRoomTile.SHOWER);
            showers++;
        }

        for (int x = room.left + 1; x < room.right; x++) {
            int cell = x + room.bottom * level.width();
            if (!isDoor(room, x, room.bottom) && !fixtures.contains(cell)) {
                addTile(level, x, room.bottom, ChangingRoomTile.LOCKER);
            }
        }

        int steamVents = 0;
        for (int y = room.bottom - 1; y > room.top; y--) {
            for (int x = room.left + 1; x < room.right; x++) {
                int cell = x + y * level.width();
                if (steamVents < 2 && level.map[cell] == Terrain.EMPTY_SP) {
                    level.map[cell] = Terrain.INACTIVE_VENT;
                    steamVents++;
                }
                if (level.map[cell] != Terrain.WATER) {
                    addTile(level, x, y, level.map[cell] == Terrain.INACTIVE_VENT
                            ? ChangingRoomTile.VENT : ChangingRoomTile.FLOOR);
                }
            }
        }
    }

    public static void confineWater(Level level, Room room) {
        for (int cell = 0; cell < level.length(); cell++) {
            if (level.map[cell] != Terrain.WATER) continue;
            int x = cell % level.width();
            int y = cell / level.width();
            if (room == null || x <= room.left || x >= room.right
                    || y <= room.top || y >= room.bottom) {
                level.map[cell] = Terrain.EMPTY;
            }
        }
    }

    private static boolean isDoor(Room room, int x, int y) {
        for (Room.Door door : room.connected.values()) {
            if (door != null && door.x == x && door.y == y) return true;
        }
        return false;
    }

    private static void addTile(Level level, int x, int y, int kind) {
        ChangingRoomTile tile = new ChangingRoomTile();
        tile.setKind(kind);
        tile.pos(x, y);
        level.customTiles.add(tile);
    }
}
