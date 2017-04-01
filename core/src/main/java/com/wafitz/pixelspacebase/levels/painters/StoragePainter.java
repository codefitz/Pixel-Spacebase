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

import com.wafitz.pixelspacebase.items.DroneController;
import com.wafitz.pixelspacebase.items.ExperimentalTech.Firestarter;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.watabou.utils.Random;

public class StoragePainter extends Painter {

    public static void paint(Level level, Room room) {

        final int floor = Terrain.EMPTY_SP;

        fill(level, room, Terrain.WALL);
        fill(level, room, 1, floor);

        boolean droneController = Random.Int(2) == 0;

        int n = Random.IntRange(3, 4);
        for (int i = 0; i < n; i++) {
            int pos;
            do {
                pos = level.pointToCell(room.random());
            } while (level.map[pos] != floor);
            if (droneController) {
                level.drop(new DroneController(), pos);
                droneController = false;
            } else
                level.drop(prize(level), pos);
        }

        room.entrance().set(Room.Door.Type.BARRICADE);
        level.addItemToSpawn(new Firestarter());
    }

    private static Item prize(Level level) {

        if (Random.Int(2) != 0) {
            Item prize = level.findPrizeItem();
            if (prize != null)
                return prize;
        }

        return Generator.random(Random.oneOf(
                Generator.Category.EXPERIMENTALTECH,
                Generator.Category.SCRIPT,
                Generator.Category.FOOD,
                Generator.Category.PARTS
        ));
    }
}
