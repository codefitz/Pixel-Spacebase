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
package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.DungeonTilemap;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.levels.vents.Vent;
import com.wafitz.pixelspacebase.plants.Plant;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.tweeners.ScaleTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;

//TODO add in a proper set of vfx for plants growing/withering, lightedvent burning, discovering vents
public class TerrainFeaturesTilemap extends Tilemap {

    public static final int SIZE = 16;

    private static TerrainFeaturesTilemap instance;

    private int[] map;
    private float[] tileVariance;

    private SparseArray<Plant> plants;
    private SparseArray<Vent> vents;

    public TerrainFeaturesTilemap(SparseArray<Plant> plants, SparseArray<Vent> vents) {
        super(Assets.TERRAIN_FEATURES, new TextureFilm(Assets.TERRAIN_FEATURES, SIZE, SIZE));

        this.plants = plants;
        this.vents = vents;

        Random.seed(Dungeon.seedCurDepth());
        tileVariance = new float[Dungeon.level.map.length];
        for (int i = 0; i < tileVariance.length; i++)
            tileVariance[i] = Random.Float();
        Random.seed();

        map(Dungeon.level.map, Dungeon.level.width());

        instance = this;
    }

    @Override
    //we need to retain two arrays, map is the dungeon tilemap which we can reference.
    // Data is our own internal image representation of the tiles, which may differ.
    public void map(int[] data, int cols) {
        map = data;
        super.map(new int[data.length], cols);
    }

    @Override
    public synchronized void updateMap() {
        super.updateMap();
        for (int i = 0; i < data.length; i++)
            data[i] = getTileVisual(i, map[i]);
    }

    @Override
    public synchronized void updateMapCell(int cell) {
        //update in a 3x3 grid to account for neighbours which might also be affected
        if (Dungeon.level.insideMap(cell)) {
            super.updateMapCell(cell - mapWidth - 1);
            super.updateMapCell(cell + mapWidth + 1);
            for (int i : PathFinder.NEIGHBOURS9)
                data[cell + i] = getTileVisual(cell + i, map[cell + i]);

            //unless we're at the level's edge, then just do the one tile.
        } else {
            super.updateMapCell(cell);
            data[cell] = getTileVisual(cell, map[cell]);
        }
    }

    private int getTileVisual(int pos, int tile) {
        if (vents.get(pos) != null) {
            Vent vent = vents.get(pos);
            if (!vent.visible)
                //return -1;
                // wafitz.v4 All vents to be visible
                return (Vent.BLACK) + (vent.shape * 16);
            else
                return (vent.active ? vent.color : Vent.BLACK) + (vent.shape * 16);
        }

        if (plants.get(pos) != null) {
            return plants.get(pos).image + 7 * 16;
        }

        if (tile == Terrain.OFFVENT) {
            return 9 + 16 * ((Dungeon.depth - 1) / 5) + (tileVariance[pos] > 0.5f ? 1 : 0);
        } else if (tile == Terrain.LIGHTEDVENT) {
            return 11 + 16 * ((Dungeon.depth - 1) / 5) + (tileVariance[pos] > 0.5f ? 1 : 0);
        } else if (tile == Terrain.EMBERS) {
            return 13 + (tileVariance[pos] > 0.5f ? 1 : 0);
        }

        return -1;
    }

    public static Image tile(int pos, int tile) {
        Image img = new Image(instance.texture);
        img.frame(instance.tileset.get(instance.getTileVisual(pos, tile)));
        return img;
    }

    public void growPlant(final int pos) {
        final Image plant = tile(pos, map[pos]);
        plant.origin.set(8, 12);
        plant.scale.set(0);
        plant.point(DungeonTilemap.tileToWorld(pos));

        parent.add(plant);

        parent.add(new ScaleTweener(plant, new PointF(1, 1), 0.2f) {
            protected void onComplete() {
                plant.killAndErase();
                killAndErase();
                updateMapCell(pos);
            }
        });
    }

    @Override
    public boolean overlapsPoint(float x, float y) {
        return true;
    }

    @Override
    public boolean overlapsScreenPoint(int x, int y) {
        return true;
    }

    @Override
    protected boolean needsRender(int pos) {
        return data[pos] != -1;
    }
}
