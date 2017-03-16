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

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.blobs.Craft;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.keys.IronKey;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class LaboratoryPainter extends Painter {

    public static void paint(Level level, Room room) {

        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.EMPTY_SP);

        Room.Door entrance = room.entrance();

        Point pot = null;
        if (entrance.x == room.left) {
            pot = new Point(room.right - 1, Random.Int(2) == 0 ? room.top + 1 : room.bottom - 1);
        } else if (entrance.x == room.right) {
            pot = new Point(room.left + 1, Random.Int(2) == 0 ? room.top + 1 : room.bottom - 1);
        } else if (entrance.y == room.top) {
            pot = new Point(Random.Int(2) == 0 ? room.left + 1 : room.right - 1, room.bottom - 1);
        } else if (entrance.y == room.bottom) {
            pot = new Point(Random.Int(2) == 0 ? room.left + 1 : room.right - 1, room.top + 1);
        }
        set(level, pot, Terrain.ALCHEMY);

        Craft craft = new Craft();
        craft.gadget(level, pot.x + level.width() * pot.y, 1);
        level.blobs.put(Craft.class, craft);

        int n = Random.IntRange(2, 3);
        for (int i = 0; i < n; i++) {
            int pos;
            do {
                pos = level.pointToCell(room.random());
            } while (
                    level.map[pos] != Terrain.EMPTY_SP ||
                            level.heaps.get(pos) != null);
            level.drop(prize(level), pos);
        }

        entrance.set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }

    private static Item prize(Level level) {

        Item prize = level.findPrizeItem(ExperimentalTech.class);
        if (prize == null)
            prize = Generator.random(Generator.Category.EXPERIMENTALTECH);

        return prize;
    }
}
