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
package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Bones;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Arp;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.Room.Type;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.noosa.Group;
import com.watabou.utils.Graph;
import com.watabou.utils.Random;

import java.util.List;

public class LastWorkshopLevel extends RegularLevel {

    {
        color1 = 0x4b6636;
        color2 = 0xf2f2f2;
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_CITY;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_CITY;
    }

    @Override
    protected boolean build() {

        feeling = Feeling.CHASM;
        viewDistance = 4;

        initRooms();

        int distance;
        int retry = 0;
        int minDistance = (int) Math.sqrt(rooms.size());
        do {
            int innerRetry = 0;
            do {
                if (innerRetry++ > 10) {
                    return false;
                }
                roomEntrance = Random.element(rooms);
            } while (roomEntrance.width() < 4 || roomEntrance.height() < 4);

            innerRetry = 0;
            do {
                if (innerRetry++ > 10) {
                    return false;
                }
                roomExit = Random.element(rooms);
            }
            while (roomExit == roomEntrance || roomExit.width() < 6 || roomExit.height() < 6 || roomExit.top == 0);

            Graph.buildDistanceMap(rooms, roomExit);
            distance = Graph.buildPath(rooms, roomEntrance, roomExit).size();

            if (retry++ > 10) {
                return false;
            }

        } while (distance < minDistance);

        roomEntrance.type = Type.ENTRANCE;
        roomExit.type = Type.EXIT;

        Graph.buildDistanceMap(rooms, roomExit);
        List<Room> path = Graph.buildPath(rooms, roomEntrance, roomExit);

        Graph.setPrice(path, roomEntrance.distance);

        Graph.buildDistanceMap(rooms, roomExit);
        path = Graph.buildPath(rooms, roomEntrance, roomExit);

        Room room = roomEntrance;
        for (Room next : path) {
            room.connect(next);
            room = next;
        }

        Room roomWorkshop = null;
        int workshopSquare = 0;
        for (Room r : rooms) {
            if (r.type == Type.NULL && r.connected.size() > 0) {
                r.type = Type.PASSAGE;
                if (r.square() > workshopSquare) {
                    roomWorkshop = r;
                    workshopSquare = r.square();
                }
            }
        }

        if (roomWorkshop == null || workshopSquare < 54) {
            return false;
        } else {
            roomWorkshop.type = Arp.Quest.isCompleted() ? Room.Type.WORKSHOP : Room.Type.STANDARD;
        }

        paint();

        paintWater();
        paintLightedVents();

        return true;
    }

    @Override
    protected void decorate() {

        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {

                map[i] = Terrain.EMPTY_DECO;

            } else if (map[i] == Terrain.WALL && Random.Int(8) == 0) {

                map[i] = Terrain.WALL_DECO;

            } else if (map[i] == Terrain.SECRET_DOOR) {

                map[i] = Terrain.DOOR;

            }
        }

        if (Arp.Quest.isCompleted()) {
            placeSign();
        }
    }

    @Override
    protected void createMobs() {
    }

    public Actor respawner() {
        return null;
    }

    @Override
    protected void createItems() {
        Item item = Bones.get();
        if (item != null) {
            int pos;
            do {
                pos = pointToCell(roomEntrance.random());
            } while (pos == entrance || map[pos] == Terrain.SIGN);
            drop(item, pos).type = Heap.Type.REMAINS;
        }
    }

    @Override
    public int randomRespawnCell() {
        return pointToCell(roomEntrance.random());
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(CityLevel.class, "water_name");
            case Terrain.OFFVENT:
                return Messages.get(CityLevel.class, "off_vent_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.ENTRANCE:
                return Messages.get(CityLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(CityLevel.class, "exit_desc");
            case Terrain.WALL_DECO:
            case Terrain.EMPTY_DECO:
                return Messages.get(CityLevel.class, "deco_desc");
            case Terrain.EMPTY_SP:
                return Messages.get(CityLevel.class, "sp_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(CityLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(CityLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    protected boolean[] water() {
        return Patch.generate(this, 0.35f, 4);
    }

    @Override
    protected boolean[] lightedvent() {
        return Patch.generate(this, 0.30f, 3);
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        CityLevel.addCityVisuals(this, visuals);
        return visuals;
    }
}
