/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.wafitz.pixelspacebase.levels.painters;

import com.wafitz.pixelspacebase.actors.blobs.HealingTank;
import com.wafitz.pixelspacebase.actors.blobs.WellWater;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.watabou.utils.Point;

public class MedicalPainter extends Painter {

    public static void paint(Level level, Room room) {

        fill(level, room, Terrain.WALL);

        Room.Door entrance = room.entrance();
        Point booth = boothCell(room, entrance);
        set(level, booth.x, booth.y, Terrain.HEALING_TANK);

        WellWater terminal = (WellWater) level.blobs.get(HealingTank.class);
        if (terminal == null) {
            terminal = new HealingTank();
        }
        terminal.device(level, level.pointToCell(booth), 1);
        level.blobs.put(HealingTank.class, terminal);

        entrance.set(Room.Door.Type.REGULAR);
    }

    private static Point boothCell(Room room, Room.Door entrance) {
        if (entrance.x == room.left) {
            return new Point(room.left + 1, entrance.y);
        } else if (entrance.x == room.right) {
            return new Point(room.right - 1, entrance.y);
        } else if (entrance.y == room.top) {
            return new Point(entrance.x, room.top + 1);
        } else if (entrance.y == room.bottom) {
            return new Point(entrance.x, room.bottom - 1);
        }

        return room.center();
    }
}
