package com.wafitz.pixelspacebase.ui;

import com.wafitz.pixelspacebase.SpacebaseTilemap;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Group;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.Gizmo;

import java.util.ArrayList;

/**
 * The scrolling water texture, clipped to horizontal runs of water cells.
 * Each run has an opaque base so semi-transparent textures never reveal space.
 */
public class WaterLayer extends Group {

    private static final int WATER_BASE_COLOR = 0xFF103A4C;

    private final String texture;
    private final ArrayList<WaterStrip> strips = new ArrayList<>();
    private int[] map;
    private boolean[] waterCells;
    private int mapWidth;
    private float scrollY;

    public WaterLayer(String texture, int[] map, int mapWidth) {
        this.texture = texture;
        map(map, mapWidth);
    }

    public synchronized void map(int[] map, int mapWidth) {
        this.map = map;
        this.mapWidth = mapWidth;
        rebuild();
    }

    public synchronized void updateMap() {
        rebuild();
    }

    public synchronized void updateMapCell(int cell) {
        if (cell >= 0 && cell < map.length
                && waterCells[cell] != isRenderableWater(cell)) {
            rebuild();
        }
    }

    public synchronized void offset(float y) {
        scrollY += y;
        for (WaterStrip strip : strips) {
            strip.offsetTo(strip.x, strip.y + scrollY);
        }
    }

    private void rebuild() {
        for (int i = length - 1; i >= 0; i--) {
            Gizmo child = members.get(i);
            if (child != null) {
                remove(child);
                child.destroy();
            }
        }
        strips.clear();

        waterCells = new boolean[map.length];
        for (int row = 0; row < map.length / mapWidth; row++) {
            int column = 0;
            while (column < mapWidth) {
                int cell = row * mapWidth + column;
                if (!isRenderableWater(cell)) {
                    column++;
                    continue;
                }

                int start = column;
                do {
                    waterCells[row * mapWidth + column] = true;
                    column++;
                } while (column < mapWidth && isRenderableWater(row * mapWidth + column));

                float x = start * SpacebaseTilemap.SIZE;
                float y = row * SpacebaseTilemap.SIZE;
                float width = (column - start) * SpacebaseTilemap.SIZE;

                ColorBlock base = new ColorBlock(width, SpacebaseTilemap.SIZE, WATER_BASE_COLOR);
                base.x = x;
                base.y = y;
                add(base);

                WaterStrip strip = new WaterStrip(width, SpacebaseTilemap.SIZE, texture);
                strip.x = x;
                strip.y = y;
                strip.offsetTo(x, y + scrollY);
                add(strip);
                strips.add(strip);
            }
        }
    }

    private boolean isRenderableWater(int cell) {
        return map[cell] == Terrain.WATER && Level.discoverable[cell];
    }

    private class WaterStrip extends SkinnedBlock {
        WaterStrip(float width, float height, String texturePath) {
            super(width, height, texturePath);
        }

        @Override
        protected NoosaScript script() {
            return NoosaScriptNoLighting.get();
        }
    }
}
