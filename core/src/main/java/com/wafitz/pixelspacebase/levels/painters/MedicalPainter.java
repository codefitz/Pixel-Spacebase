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

import com.wafitz.pixelspacebase.Challenges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.blobs.Medical;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.mines.AlienEgg;
import com.wafitz.pixelspacebase.mines.KoltoPod;
import com.watabou.utils.Random;

public class MedicalPainter extends Painter {

    public static void paint(Level level, Room room) {

        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.OFFVENT);
        fill(level, room, 2, Terrain.LIGHTEDVENT);

        room.entrance().set(Room.Door.Type.REGULAR);

        if (Dungeon.isChallenged(Challenges.NO_FOOD)) {
            if (Random.Int(2) == 0) {
                level.mine(new KoltoPod.Device(), level.pointToCell(room.random()));
            }
        } else {
            int bushes = Random.Int(3);
            if (bushes == 0) {
                level.mine(new KoltoPod.Device(), level.pointToCell(room.random()));
            } else if (bushes == 1) {
                level.mine(new AlienEgg.Device(), level.pointToCell(room.random()));
            } else if (Random.Int(5) == 0) {
                int mine1, mine2;
                mine1 = level.pointToCell(room.random());
                level.mine(new KoltoPod.Device(), mine1);
                do {
                    mine2 = level.pointToCell(room.random());
                } while (mine2 == mine1);
                level.mine(new AlienEgg.Device(), mine2);
            }
        }

        Medical light = (Medical) level.blobs.get(Medical.class);
        if (light == null) {
            light = new Medical();
        }
        for (int i = room.top + 1; i < room.bottom; i++) {
            for (int j = room.left + 1; j < room.right; j++) {
                light.device(level, j + level.width() * i, 1);
            }
        }
        level.blobs.put(Medical.class, light);
    }
}
