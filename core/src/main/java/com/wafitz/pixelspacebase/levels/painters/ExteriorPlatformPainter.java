package com.wafitz.pixelspacebase.levels.painters;

import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;

/** An exposed bridge extending from a normal room into open space. */
public class ExteriorPlatformPainter extends Painter {
    private static final int EDGE_CLEARANCE = 2;

    public static void paint(Level level, Room room) {
        fill(level, room, Terrain.CHASM);
        Room.Door door = room.entrance();
        door.set(Room.Door.Type.UNLOCKED);
        int dx = door.x == room.left ? 1 : door.x == room.right ? -1 : 0;
        int dy = door.y == room.top ? 1 : door.y == room.bottom ? -1 : 0;

        // Keep the bridge narrow and exposed along both sides for its full length.
        int length = dx != 0 ? room.width() : room.height();
        // Leave open space at the tip, clear of the unrevealable outer map border.
        int edgeDistance = dx > 0 ? level.width() - 1 - door.x
                : dx < 0 ? door.x
                : dy > 0 ? level.height() - 1 - door.y : door.y;
        length = Math.min(length, Math.max(0, edgeDistance - EDGE_CLEARANCE));
        for (int step = 1; step <= length; step++) {
            for (int side = -1; side <= 1; side++) {
                int x = door.x + dx * step + dy * side;
                int y = door.y + dy * step + dx * side;
                if (x < room.left || x > room.right || y < room.top || y > room.bottom) continue;
                int cell = x + y * level.width();
                level.map[cell] = Terrain.EMPTY_SP;
                level.setVacuum(cell);
            }
        }
    }
}
