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
import com.wafitz.pixelspacebase.actors.mobs.RupturedCrewSuit;
import com.wafitz.pixelspacebase.items.plasmids.FireGrenade;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.Parts;
import com.wafitz.pixelspacebase.items.quest.Lazer;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.ui.CustomTileVisual;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MassGravePainter extends Painter {

    public static void paint(Level level, Room room) {

        Room.Door entrance = room.entrance();
        entrance.set(Room.Door.Type.BARRICADE);
        level.addItemToSpawn(new FireGrenade());

        fill(level, room, Terrain.WALL);
        fill(level, room, 1, Terrain.EMPTY_SP);

        addHolodeckTiles(level, room, entrance);

        //50% 1 skeleton, 50% 2 skeletons
        for (int i = 0; i <= Random.Int(2); i++) {
            RupturedCrewSuit skele = new RupturedCrewSuit();

            int pos;
            do {
                pos = level.pointToCell(room.random());
            } while (level.map[pos] != Terrain.EMPTY_SP || level.findMob(pos) != null);
            skele.pos = pos;
            level.mobs.add(skele);
        }

        ArrayList<Item> items = new ArrayList<>();
        //100% corpse dust, 2x100% 1 coin, 2x30% coins, 1x60% random item, 1x30% armor
        items.add(new Lazer());
        items.add(new Parts(1));
        items.add(new Parts(1));
        if (Random.Float() <= 0.3f) items.add(new Parts());
        if (Random.Float() <= 0.3f) items.add(new Parts());
        if (Random.Float() <= 0.6f) items.add(Generator.random());
        if (Random.Float() <= 0.3f) items.add(Generator.randomArmor());

        for (Item item : items) {
            int pos;
            do {
                pos = level.pointToCell(room.random());
            } while (level.map[pos] != Terrain.EMPTY_SP || level.heaps.get(pos) != null);
            Heap h = level.drop(item, pos);
            h.type = Heap.Type.EMPTY_SPACESUIT;
        }
    }

    private static void addHolodeckTiles(Level level, Room room, Point entrance) {
        for (int x = room.left; x <= room.right; x++) {
            for (int y = room.top; y <= room.bottom; y++) {
                if (x == entrance.x && y == entrance.y) {
                    continue;
                }

                HolodeckRetainerTiles vis = new HolodeckRetainerTiles();
                int ofsX = 0;
                int ofsY = 0;
                if (x == room.right) {
                    ofsX = 2;
                } else if (x != room.left) {
                    ofsX = 1;
                }

                if (y == room.bottom) {
                    ofsY = 2;
                } else if (y != room.top) {
                    ofsY = 1;
                }

                vis.offset(ofsX, ofsY);
                vis.pos(x, y);
                level.customTiles.add(vis);
            }
        }
    }

    public static class HolodeckRetainerTiles extends CustomTileVisual {
        {
            name = Messages.get(this, "name");

            tx = Assets.HOLODECK_RETAINER_TILES;
            txX = 0;
            txY = 0;
        }

        void offset(int x, int y) {
            ofsX = x;
            ofsY = y;
        }

        @Override
        public String desc() {
            if (ofsX == 1 && ofsY == 1) {
                return Messages.get(this, "desc");
            } else {
                return null;
            }
        }
    }
}
