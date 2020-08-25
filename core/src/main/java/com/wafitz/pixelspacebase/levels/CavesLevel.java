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
import com.wafitz.pixelspacebase.actors.mobs.npcs.Leonard;
import com.wafitz.pixelspacebase.levels.Room.Type;
import com.wafitz.pixelspacebase.levels.painters.Painter;
import com.wafitz.pixelspacebase.levels.vents.ConfusionVent;
import com.wafitz.pixelspacebase.levels.vents.ExplosiveVent;
import com.wafitz.pixelspacebase.levels.vents.FireVent;
import com.wafitz.pixelspacebase.levels.vents.FlashingVent;
import com.wafitz.pixelspacebase.levels.vents.FlockVent;
import com.wafitz.pixelspacebase.levels.vents.FrostVent;
import com.wafitz.pixelspacebase.levels.vents.GrippingVent;
import com.wafitz.pixelspacebase.levels.vents.GuardianVent;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.wafitz.pixelspacebase.levels.vents.OozeVent;
import com.wafitz.pixelspacebase.levels.vents.ParalyticVent;
import com.wafitz.pixelspacebase.levels.vents.PitfallVent;
import com.wafitz.pixelspacebase.levels.vents.PoisonVent;
import com.wafitz.pixelspacebase.levels.vents.RockfallVent;
import com.wafitz.pixelspacebase.levels.vents.SpearVent;
import com.wafitz.pixelspacebase.levels.vents.SummoningVent;
import com.wafitz.pixelspacebase.levels.vents.TeleportationVent;
import com.wafitz.pixelspacebase.levels.vents.VenomVent;
import com.wafitz.pixelspacebase.levels.vents.WarpingVent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class CavesLevel extends RegularLevel {

    {
        color1 = 0x534f3e;
        color2 = 0xb9d661;

        viewDistance = 6;
    }

    @Override
    public String tilesTex() {
        return Assets.TILES_CAVES;
    }

    @Override
    public String waterTex() {
        return Assets.WATER_CAVES;
    }

    protected boolean[] water() {
        return Patch.generate(this, feeling == Feeling.WATER ? 0.60f : 0.45f, 6);
    }

    protected boolean[] lightedvent() {
        return Patch.generate(this, feeling == Feeling.LIGHTEDVENT ? 0.55f : 0.35f, 3);
    }

    @Override
    protected Class<?>[] ventClasses() {
        return new Class[]{FireVent.class, FrostVent.class, PoisonVent.class, SpearVent.class, VenomVent.class,
                ExplosiveVent.class, FlashingVent.class, GrippingVent.class, ParalyticVent.class, LightningVent.class, RockfallVent.class, OozeVent.class,
                ConfusionVent.class, FlockVent.class, GuardianVent.class, PitfallVent.class, SummoningVent.class, TeleportationVent.class,
                WarpingVent.class};
    }

    @Override
    protected float[] ventChances() {
        return new float[]{8, 8, 8, 8, 8,
                4, 4, 4, 4, 4, 4, 4,
                2, 2, 2, 2, 2, 2,
                1};
    }

    @Override
    protected boolean assignRoomType() {
        if (!super.assignRoomType()) return false;

        return !(!Leonard.Quest.spawn(rooms) && Dungeon.depth == 14);

    }

    @Override
    protected void decorate() {

        for (Room room : rooms) {
            if (room.type != Room.Type.STANDARD) {
                continue;
            }

            if (room.width() <= 3 || room.height() <= 3) {
                continue;
            }

            int s = room.square();

            if (Random.Int(s) > 8) {
                int corner = (room.left + 1) + (room.top + 1) * width();
                if (map[corner - 1] == Terrain.WALL && map[corner - width()] == Terrain.WALL) {
                    map[corner] = Terrain.WALL;
                    vents.remove(corner);
                }
            }

            if (Random.Int(s) > 8) {
                int corner = (room.right - 1) + (room.top + 1) * width();
                if (map[corner + 1] == Terrain.WALL && map[corner - width()] == Terrain.WALL) {
                    map[corner] = Terrain.WALL;
                    vents.remove(corner);
                }
            }

            if (Random.Int(s) > 8) {
                int corner = (room.left + 1) + (room.bottom - 1) * width();
                if (map[corner - 1] == Terrain.WALL && map[corner + width()] == Terrain.WALL) {
                    map[corner] = Terrain.WALL;
                    vents.remove(corner);
                }
            }

            if (Random.Int(s) > 8) {
                int corner = (room.right - 1) + (room.bottom - 1) * width();
                if (map[corner + 1] == Terrain.WALL && map[corner + width()] == Terrain.WALL) {
                    map[corner] = Terrain.WALL;
                    vents.remove(corner);
                }
            }

            for (Room n : room.connected.keySet()) {
                if ((n.type == Room.Type.STANDARD || n.type == Room.Type.TUNNEL) && Random.Int(3) == 0) {
                    Painter.set(this, room.connected.get(n), Terrain.EMPTY_DECO);
                }
            }
        }

        for (int i = width() + 1; i < length() - width(); i++) {
            if (map[i] == Terrain.EMPTY) {
                int n = 0;
                if (map[i + 1] == Terrain.WALL) {
                    n++;
                }
                if (map[i - 1] == Terrain.WALL) {
                    n++;
                }
                if (map[i + width()] == Terrain.WALL) {
                    n++;
                }
                if (map[i - width()] == Terrain.WALL) {
                    n++;
                }
                if (Random.Int(6) <= n) {
                    map[i] = Terrain.EMPTY_DECO;
                }
            }
        }

        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.WALL && Random.Int(12) == 0) {
                map[i] = Terrain.WALL_DECO;
            }
        }

        placeSign();

        if (Dungeon.bossLevel(Dungeon.depth + 1)) {
            return;
        }

        for (Room r : rooms) {
            if (r.type == Type.STANDARD) {
                for (Room n : r.neigbours) {
                    if (n.type == Type.STANDARD && !r.connected.containsKey(n)) {
                        Rect w = r.intersect(n);
                        if (w.left == w.right && w.bottom - w.top >= 5) {

                            w.top += 2;
                            w.bottom -= 1;

                            w.right++;

                            Painter.fill(this, w.left, w.top, 1, w.height(), Terrain.CHASM);

                        } else if (w.top == w.bottom && w.right - w.left >= 5) {

                            w.left += 2;
                            w.right -= 1;

                            w.bottom++;

                            Painter.fill(this, w.left, w.top, w.width(), 1, Terrain.CHASM);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case Terrain.LIGHTEDVENT:
                return Messages.get(CavesLevel.class, "lightedvent_name");
            case Terrain.OFFVENT:
                return Messages.get(CavesLevel.class, "off_vent_name");
            case Terrain.WATER:
                return Messages.get(CavesLevel.class, "water_name");
            default:
                return super.tileName(tile);
        }
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case Terrain.ENTRANCE:
                return Messages.get(CavesLevel.class, "entrance_desc");
            case Terrain.EXIT:
                return Messages.get(CavesLevel.class, "exit_desc");
            case Terrain.OFFVENT:
                return Messages.get(CavesLevel.class, "off_vent_desc");
            case Terrain.WALL_DECO:
                return Messages.get(CavesLevel.class, "wall_deco_desc");
            case Terrain.BOOKSHELF:
                return Messages.get(CavesLevel.class, "bookshelf_desc");
            default:
                return super.tileDesc(tile);
        }
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        addCavesVisuals(this, visuals);
        return visuals;
    }

    public static void addCavesVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); i++) {
            if (level.map[i] == Terrain.WALL_DECO) {
                group.add(new Vein(i));
            }
        }
    }

    private static class Vein extends Group {

        private int pos;

        private float delay;

        public Vein(int pos) {
            super();

            this.pos = pos;

            delay = Random.Float(2);
        }

        @Override
        public void update() {

            if (visible = Dungeon.visible[pos]) {

                super.update();

                if ((delay -= Game.elapsed) <= 0) {

                    //pickaxe can remove the ore, should remove the sparkling too.
                    if (Dungeon.level.map[pos] != Terrain.WALL_DECO) {
                        kill();
                        return;
                    }

                    delay = Random.Float();

                    PointF p = DungeonTilemap.tileToWorld(pos);
                    ((Sparkle) recycle(Sparkle.class)).reset(
                            p.x + Random.Float(DungeonTilemap.SIZE),
                            p.y + Random.Float(DungeonTilemap.SIZE));
                }
            }
        }
    }

    public static final class Sparkle extends PixelParticle {

        public void reset(float x, float y) {
            revive();

            this.x = x;
            this.y = y;

            left = lifespan = 0.5f;
        }

        @Override
        public void update() {
            super.update();

            float p = left / lifespan;
            size((am = p < 0.5f ? p * 2 : (1 - p) * 2) * 2);
        }
    }
}
