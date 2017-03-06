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

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.items.quest.CeremonialCandle;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.ui.CustomTileVisual;
import com.watabou.utils.Point;

public class RitualSitePainter extends Painter {

    public static void paint(Level level, Room room) {

        for (Room.Door door : room.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }

        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.EMPTY);

        RitualMarker vis = new RitualMarker();
        Point c = room.center();
        vis.pos(c.x - 1, c.y - 1);

        level.customTiles.add(vis);

        fill(level, c.x - 1, c.y - 1, 3, 3, Terrain.EMPTY_DECO);

        level.addItemToSpawn(new CeremonialCandle());
        level.addItemToSpawn(new CeremonialCandle());
        level.addItemToSpawn(new CeremonialCandle());
        level.addItemToSpawn(new CeremonialCandle());

        CeremonialCandle.ritualPos = c.x + (level.width() * c.y);
    }

    public static class RitualMarker extends CustomTileVisual {

        {
            name = Messages.get(this, "name");

            tx = Assets.PRISON_QUEST;
            txX = txY = 0;
            tileW = tileH = 3;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }
    }

}
