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

import com.wafitz.pixelspacebase.actors.mobs.npcs.Leonard;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.levels.vents.FireVent;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class LeonardPainter extends Painter {

    public static void paint(Level level, Room room) {

        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.VENT);
        fill(level, room, 2, Terrain.EMPTY_SP);

        for (int i = 0; i < 2; i++) {
            int pos;
            do {
                pos = level.pointToCell(room.random());
            } while (level.map[pos] != Terrain.EMPTY_SP);
            level.drop(
                    Generator.random(Random.oneOf(
                            Generator.Category.ARMOR,
                            Generator.Category.WEAPON
                    )), pos);
        }

        for (Room.Door door : room.connected.values()) {
            door.set(Room.Door.Type.UNLOCKED);
            drawInside(level, room, door, 1, Terrain.EMPTY);
        }

        Leonard npc = new Leonard();
        do {
            npc.pos = level.pointToCell(room.random(1));
        } while (level.heaps.get(npc.pos) != null);
        level.mobs.add(npc);

        for (Point p : room.getPoints()) {
            int cell = level.pointToCell(p);
            if (level.map[cell] == Terrain.VENT) {
                level.setVent(new FireVent().reveal(), cell);
            }
        }
    }
}
