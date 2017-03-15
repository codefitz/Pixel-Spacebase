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
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.DungeonTilemap;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Imp;
import com.wafitz.pixelspacebase.levels.Room.Type;
import com.wafitz.pixelspacebase.levels.vents.BlazingVent;
import com.wafitz.pixelspacebase.levels.vents.DisarmingVent;
import com.wafitz.pixelspacebase.levels.vents.ExplosiveVent;
import com.wafitz.pixelspacebase.levels.vents.FlockVent;
import com.wafitz.pixelspacebase.levels.vents.FrostVent;
import com.wafitz.pixelspacebase.levels.vents.GrippingVent;
import com.wafitz.pixelspacebase.levels.vents.GuardianVent;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.wafitz.pixelspacebase.levels.vents.MalfunctioningVent;
import com.wafitz.pixelspacebase.levels.vents.OozeVent;
import com.wafitz.pixelspacebase.levels.vents.PitfallVent;
import com.wafitz.pixelspacebase.levels.vents.RockfallVent;
import com.wafitz.pixelspacebase.levels.vents.SpearVent;
import com.wafitz.pixelspacebase.levels.vents.SummoningVent;
import com.wafitz.pixelspacebase.levels.vents.TeleportationVent;
import com.wafitz.pixelspacebase.levels.vents.VenomVent;
import com.wafitz.pixelspacebase.levels.vents.WarpingVent;
import com.wafitz.pixelspacebase.levels.vents.WeakeningVent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class CityLevel extends RegularLevel {

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

    protected boolean[] water() {
        return Patch.generate(this, feeling == Feeling.WATER ? 0.65f : 0.45f, 4);
    }

    protected boolean[] lightedvent() {
        return Patch.generate(this, feeling == Feeling.LIGHTEDVENT ? 0.60f : 0.40f, 3);
    }

    @Override
    protected Class<?>[] ventClasses() {
        return new Class[]{BlazingVent.class, FrostVent.class, SpearVent.class, VenomVent.class,
                ExplosiveVent.class, GrippingVent.class, LightningVent.class, RockfallVent.class, OozeVent.class, WeakeningVent.class,
                MalfunctioningVent.class, FlockVent.class, GuardianVent.class, PitfallVent.class, SummoningVent.class, TeleportationVent.class,
                DisarmingVent.class, WarpingVent.class};
    }

    @Override
    protected float[] ventChances() {
        return new float[]{8, 8, 8, 8,
                4, 4, 4, 4, 4, 4,
                2, 2, 2, 2, 2, 2,
                1, 1};
    }

    @Override
    protected boolean assignRoomType() {
        if (!super.assignRoomType()) return false;

        for (Room r : rooms) {
            if (r.type == Type.TUNNEL) {
                r.type = Type.PASSAGE;
            }
        }

        return true;
    }

    @Override
    protected void decorate() {

        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
                map[i] = Terrain.EMPTY_DECO;
            } else if (map[i] == Terrain.WALL && Random.Int(8) == 0) {
                map[i] = Terrain.WALL_DECO;
            }
        }

        placeSign();
    }

    @Override
    protected void createItems() {
        super.createItems();

        Imp.Quest.spawn(this);
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
    public Group addVisuals() {
        super.addVisuals();
        addCityVisuals(this, visuals);
        return visuals;
    }

    static void addCityVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL_DECO) {
                group.add(new Smoke(i));
            }
        }
    }

    private static class Smoke extends Emitter {

        private int pos;

        private static final Emitter.Factory factory = new Factory() {

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                SmokeParticle p = (SmokeParticle) emitter.recycle(SmokeParticle.class);
                p.reset(x, y);
            }
        };

        Smoke(int pos) {
            super();

            this.pos = pos;

            PointF p = DungeonTilemap.tileCenterToWorld(pos);
            pos(p.x - 4, p.y - 2, 4, 0);

            pour(factory, 0.2f);
        }

        @Override
        public void update() {
            if (visible = Dungeon.visible[pos]) {
                super.update();
            }
        }
    }

    private static final class SmokeParticle extends PixelParticle {

        public SmokeParticle() {
            super();

            color(0x000000);
            speed.set(Random.Float(8), -Random.Float(8));
        }

        public void reset(float x, float y) {
            revive();

            this.x = x;
            this.y = y;

            left = lifespan = 2f;
        }

        @Override
        public void update() {
            super.update();
            float p = left / lifespan;
            am = p > 0.8f ? 1 - p : p * 0.25f;
            size(8 - p * 4);
        }
    }
}