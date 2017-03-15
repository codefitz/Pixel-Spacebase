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

import android.opengl.GLES20;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.DungeonTilemap;
import com.wafitz.pixelspacebase.items.Torch;
import com.wafitz.pixelspacebase.levels.vents.BlazingVent;
import com.wafitz.pixelspacebase.levels.vents.DisarmingVent;
import com.wafitz.pixelspacebase.levels.vents.DisintegrationVent;
import com.wafitz.pixelspacebase.levels.vents.DistortionVent;
import com.wafitz.pixelspacebase.levels.vents.ExplosiveVent;
import com.wafitz.pixelspacebase.levels.vents.FlockVent;
import com.wafitz.pixelspacebase.levels.vents.FrostVent;
import com.wafitz.pixelspacebase.levels.vents.GrimVent;
import com.wafitz.pixelspacebase.levels.vents.GrippingVent;
import com.wafitz.pixelspacebase.levels.vents.GuardianVent;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.wafitz.pixelspacebase.levels.vents.MalfunctioningVent;
import com.wafitz.pixelspacebase.levels.vents.OozeVent;
import com.wafitz.pixelspacebase.levels.vents.SpearVent;
import com.wafitz.pixelspacebase.levels.vents.SummoningVent;
import com.wafitz.pixelspacebase.levels.vents.TeleportationVent;
import com.wafitz.pixelspacebase.levels.vents.VenomVent;
import com.wafitz.pixelspacebase.levels.vents.WarpingVent;
import com.wafitz.pixelspacebase.levels.vents.WeakeningVent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import javax.microedition.khronos.opengles.GL10;

public class HallsLevel extends RegularLevel {

    {
        minRoomSize = 6;

        viewDistance = Math.max(25 - Dungeon.depth, 1);

        color1 = 0x801500;
        color2 = 0xa68521;
    }

    @Override
    public void create() {
        addItemToSpawn(new Torch());
        super.create();
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_HALLS;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_HALLS;
    }

    protected boolean[] water() {
        return Patch.generate(this, feeling == Feeling.WATER ? 0.55f : 0.40f, 6);
    }

    protected boolean[] lightedvent() {
        return Patch.generate(this, feeling == Feeling.LIGHTEDVENT ? 0.55f : 0.30f, 3);
    }

    @Override
    protected Class<?>[] ventClasses() {
        return new Class[]{BlazingVent.class, DisintegrationVent.class, FrostVent.class, SpearVent.class, VenomVent.class,
                ExplosiveVent.class, GrippingVent.class, LightningVent.class, OozeVent.class, WeakeningVent.class,
                MalfunctioningVent.class, FlockVent.class, GrimVent.class, GuardianVent.class, SummoningVent.class, TeleportationVent.class,
                DisarmingVent.class, DistortionVent.class, WarpingVent.class};
    }

    @Override
    protected float[] ventChances() {
        return new float[]{8, 8, 8, 8, 8,
                4, 4, 4, 4, 4,
                2, 2, 2, 2, 2, 2,
                1, 1, 1};
    }

    @Override
    protected void decorate() {

        for (int i = width() + 1; i < length() - width() - 1; i++) {
            if (map[i] == Terrain.EMPTY) {

                int count = 0;
                for (int j = 0; j < PathFinder.NEIGHBOURS8.length; j++) {
                    if ((Terrain.flags[map[i + PathFinder.NEIGHBOURS8[j]]] & Terrain.PASSABLE) > 0) {
                        count++;
                    }
                }

                if (Random.Int(80) < count) {
                    map[i] = Terrain.EMPTY_DECO;
                }

            } else if (map[i] == Terrain.WALL &&
                    map[i - 1] != Terrain.WALL_DECO && map[i - width()] != Terrain.WALL_DECO &&
                    Random.Int(20) == 0) {

                map[i] = Terrain.WALL_DECO;

            }
        }

        placeSign();
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(HallsLevel.class, "water_name");
            case Terrain.LIGHTEDVENT:
                return Messages.get(HallsLevel.class, "lighted_name");
            case Terrain.OFFVENT:
                return Messages.get(HallsLevel.class, "off_vent_name");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(HallsLevel.class, "statue_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.WATER:
                return Messages.get(HallsLevel.class, "water_desc");
            case Terrain.STATUE:
            case Terrain.STATUE_SP:
                return Messages.get(HallsLevel.class, "statue_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(HallsLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addHallsVisuals(this, visuals);
        return visuals;
    }

    static void addHallsVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WATER) {
                group.add(new Stream(i));
            }
        }
    }

    private static class Stream extends Group {

        private int pos;

        private float delay;

        Stream(int pos) {
            super();

            this.pos = pos;

            delay = Random.Float(2);
        }

        @Override
        public void update() {

            if (visible = Dungeon.visible[pos]) {

                super.update();

                if ((delay -= Game.elapsed) <= 0) {

                    delay = Random.Float(2);

                    PointF p = DungeonTilemap.tileToWorld(pos);
                    ((FireParticle) recycle(FireParticle.class)).reset(
                            p.x + Random.Float(DungeonTilemap.SIZE),
                            p.y + Random.Float(DungeonTilemap.SIZE));
                }
            }
        }

        @Override
        public void draw() {
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
            super.draw();
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        }
    }

    private static class FireParticle extends PixelParticle.Shrinking {

        public FireParticle() {
            super();

            color(0xEE7722);
            lifespan = 1f;

            acc.set(0, +80);
        }

        public void reset(float x, float y) {
            revive();

            this.x = x;
            this.y = y;

            left = lifespan;

            speed.set(0, -40);
            size = 4;
        }

        @Override
        public void update() {
            super.update();
            float p = left / lifespan;
            am = p > 0.8f ? (1 - p) * 5 : 1;
        }
    }
}
