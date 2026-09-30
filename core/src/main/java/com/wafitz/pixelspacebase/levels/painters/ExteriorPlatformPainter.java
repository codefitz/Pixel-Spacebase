package com.wafitz.pixelspacebase.levels.painters;

import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;

/** A short exterior deck with open space at its edge, reached through a normal door. */
public class ExteriorPlatformPainter extends Painter {
    public static void paint(Level level, Room room) {
        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.CHASM);
        Room.Door door = room.entrance();
        door.set(Room.Door.Type.UNLOCKED);
        int dx = door.x == room.left ? 1 : door.x == room.right ? -1 : 0;
        int dy = door.y == room.top ? 1 : door.y == room.bottom ? -1 : 0;
        for (int step = 1; step <= 2; step++) {
            for (int side = -1; side <= 1; side++) {
                int x = door.x + dx * step + dy * side;
                int y = door.y + dy * step + dx * side;
                if (x <= room.left || x >= room.right || y <= room.top || y >= room.bottom) continue;
                int cell = x + y * level.width();
                level.map[cell] = Terrain.EMPTY_SP;
                level.setVacuum(cell);
            }
        }
    }
}
