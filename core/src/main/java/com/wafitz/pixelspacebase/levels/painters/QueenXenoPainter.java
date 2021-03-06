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

import com.wafitz.pixelspacebase.actors.mobs.npcs.QueenXeno;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.Parts;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.watabou.utils.Random;

public class QueenXenoPainter extends Painter {

    public static void paint(Level level, Room room) {

        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.EMPTY_SP);

        Room.Door entrance = room.entrance();
        entrance.set(Room.Door.Type.HIDDEN);
        int door = entrance.x + entrance.y * level.width();

        for (int i = room.left + 1; i < room.right; i++) {
            addChest(level, (room.top + 1) * level.width() + i, door);
            addChest(level, (room.bottom - 1) * level.width() + i, door);
        }

        for (int i = room.top + 2; i < room.bottom - 1; i++) {
            addChest(level, i * level.width() + room.left + 1, door);
            addChest(level, i * level.width() + room.right - 1, door);
        }

        QueenXeno king = new QueenXeno();
        king.pos = level.pointToCell(room.random(1));
        level.mobs.add(king);
    }

    private static void addChest(Level level, int pos, int door) {

        if (pos == door - 1 ||
                pos == door + 1 ||
                pos == door - level.width() ||
                pos == door + level.width()) {
            return;
        }

        Item prize = new Parts(Random.IntRange(1, 25));

        level.drop(prize, pos).type = Heap.Type.CHEST;
    }
}
