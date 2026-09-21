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
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.SpacebaseTilemap;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Quartermaster;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.keys.SecurityKey;
import com.wafitz.pixelspacebase.levels.Room.Type;
import com.wafitz.pixelspacebase.levels.vents.AlarmVent;
import com.wafitz.pixelspacebase.levels.vents.ChillingVent;
import com.wafitz.pixelspacebase.levels.vents.ConfusionVent;
import com.wafitz.pixelspacebase.levels.vents.FireVent;
import com.wafitz.pixelspacebase.levels.vents.FlashingVent;
import com.wafitz.pixelspacebase.levels.vents.FlockVent;
import com.wafitz.pixelspacebase.levels.vents.GrippingVent;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.wafitz.pixelspacebase.levels.vents.OozeVent;
import com.wafitz.pixelspacebase.levels.vents.ParalyticVent;
import com.wafitz.pixelspacebase.levels.vents.PoisonVent;
import com.wafitz.pixelspacebase.levels.vents.SpearVent;
import com.wafitz.pixelspacebase.levels.vents.SummoningVent;
import com.wafitz.pixelspacebase.levels.vents.TeleportationVent;
import com.wafitz.pixelspacebase.levels.vents.ToxicVent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class SecurityBlockLevel extends RegularLevel {

    private static final int SECURITY_LOCK_CHANCE = 75;

    {
        color1 = 0x6a723d;
        color2 = 0x88924c;
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_SECURITY_BLOCK;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_SECURITY_BLOCK;
    }

    protected boolean[] water() {
        return Patch.generate(this, feeling == Feeling.WATER ? 0.65f : 0.45f, 4);
    }

    protected boolean[] lightedvent() {
        return Patch.generate(this, feeling == Feeling.LIGHTEDVENT ? 0.60f : 0.40f, 3);
    }

    @Override
    protected Class<?>[] ventClasses() {
        return new Class[]{ChillingVent.class, FireVent.class, PoisonVent.class, SpearVent.class, ToxicVent.class,
                AlarmVent.class, FlashingVent.class, GrippingVent.class, ParalyticVent.class, LightningVent.class, OozeVent.class,
                ConfusionVent.class, FlockVent.class, SummoningVent.class, TeleportationVent.class,};
    }

    @Override
    protected float[] ventChances() {
        return new float[]{4, 4, 4, 4,
                2, 2, 2, 2, 2, 2,
                1, 1, 1, 1};
    }

    @Override
    protected boolean assignRoomType() {
        if (!super.assignRoomType()) return false;

        for (Room r : rooms) {
            if (r.type == Type.TUNNEL) {
                r.type = Type.PASSAGE;
            }
        }

        return Quartermaster.Quest.spawn(this, roomEntrance, rooms);
    }

    @Override
    protected void decorate() {

        secureDoors();

        for (int i = width() + 1; i < length() - width() - 1; i++) {
            if (map[i] == Terrain.EMPTY) {

                float c = 0.05f;
                if (map[i + 1] == Terrain.WALL && map[i + width()] == Terrain.WALL) {
                    c += 0.2f;
                }
                if (map[i - 1] == Terrain.WALL && map[i + width()] == Terrain.WALL) {
                    c += 0.2f;
                }
                if (map[i + 1] == Terrain.WALL && map[i - width()] == Terrain.WALL) {
                    c += 0.2f;
                }
                if (map[i - 1] == Terrain.WALL && map[i - width()] == Terrain.WALL) {
                    c += 0.2f;
                }

                if (Random.Float() < c) {
                    map[i] = Terrain.EMPTY_DECO;
                }
            }
        }

        for (int i = 0; i < width(); i++) {
            if (map[i] == Terrain.WALL &&
                    (map[i + width()] == Terrain.EMPTY || map[i + width()] == Terrain.EMPTY_SP) &&
                    Random.Int(6) == 0) {

                map[i] = Terrain.WALL_DECO;
            }
        }

        for (int i = width(); i < length() - width(); i++) {
            if (map[i] == Terrain.WALL &&
                    map[i - width()] == Terrain.WALL &&
                    (map[i + width()] == Terrain.EMPTY || map[i + width()] == Terrain.EMPTY_SP) &&
                    Random.Int(3) == 0) {

                map[i] = Terrain.WALL_DECO;
            }
        }

        placeSign();
    }

    private void secureDoors() {
        int firstDoor = -1;
        int lockedDoors = 0;

        for (int i = width() + 1; i < length() - width() - 1; i++) {
            if (map[i] == Terrain.DOOR) {
                if (firstDoor == -1) {
                    firstDoor = i;
                }
                if (Random.Int(100) < SECURITY_LOCK_CHANCE) {
                    map[i] = Terrain.LOCKED_DOOR;
                    lockedDoors++;
                }
            }
        }

        if (lockedDoors == 0 && firstDoor != -1) {
            map[firstDoor] = Terrain.LOCKED_DOOR;
        }
    }

    @Override
    protected void createItems() {
        super.createItems();
        drop(new SecurityKey(SpacebaseRun.depth), randomEntranceDropCell()).type = Heap.Type.HEAP;
    }

    private int randomEntranceDropCell() {
        while (true) {
            int pos = pointToCell(roomEntrance.random());
            if (pos != entrance
                    && map[pos] != Terrain.SIGN
                    && vents.get(pos) == null
                    && heaps.get(pos) == null
                    && Level.passable[pos]) {
                return pos;
            }
        }
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(SecurityBlockLevel.class, "water_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(SecurityBlockLevel.class, "water_desc");
            case Terrain.EMPTY_DECO:
                return Messages.get(SecurityBlockLevel.class, "empty_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(SecurityBlockLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addPrisonVisuals(this, visuals);
        return visuals;
    }

    static void addPrisonVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL_DECO) {
                group.add(new SecurityCameraLight(level, i));
            }
        }
    }

    private static class SecurityCameraLight extends ColorBlock {

        private final Level level;
        private final int pos;
        private float phase;

        SecurityCameraLight(Level level, int pos) {
            super(0.75f, 0.75f, 0xFFFF3030);

            this.level = level;
            this.pos = pos;
            phase = (pos % 7) * 0.2f;

            PointF p = SpacebaseTilemap.tileToWorld(pos);
            // Indicator socket at source pixel (35, 33) in the 64px camera tile.
            x = p.x + 35 / 4f;
            y = p.y + 33 / 4f;
            visible = false;
        }

        @Override
        public void update() {
            super.update();
            if (level.map[pos] != Terrain.WALL_DECO) {
                killAndErase();
                return;
            }
            phase = (phase + Game.elapsed) % 1.4f;
            visible = SpacebaseRun.visible[pos] && phase < 0.35f;
        }
    }
}
