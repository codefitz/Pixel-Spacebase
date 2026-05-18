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
package com.wafitz.pixelspacebase.windows;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.SpacebaseTilemap;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.ui.CustomTileVisual;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Window;
import com.watabou.noosa.Image;

public class WndInfoCell extends Window {

    private static final float GAP = 2;

    private static final int WIDTH = 120;

    public WndInfoCell(int cell) {

        super();

        int tile = SpacebaseRun.level.map[cell];
        if (Level.water[cell]) {
            tile = Terrain.WATER;
        } else if (Level.pit[cell]) {
            tile = Terrain.CHASM;
        }

        CustomTileVisual vis = null;
        int x = cell % SpacebaseRun.level.width();
        int y = cell / SpacebaseRun.level.width();
        for (CustomTileVisual i : SpacebaseRun.level.customTiles) {
            if ((x >= i.tileX && x < i.tileX + i.tileW) &&
                    (y >= i.tileY && y < i.tileY + i.tileH)) {
                if (i.desc() != null) {
                    vis = i;
                    break;
                }
            }
        }


        String desc = "";

        IconTitle titlebar = new IconTitle();
        if (vis != null) {
            titlebar.icon(new Image(vis));
            titlebar.label(vis.name);
            desc += vis.desc();
        } else {

            // wafitz.v4: We want it to display custom wet tile

            /*if (tile == Terrain.WATER) {
                Image water = new Image(SpacebaseRun.level.waterTex());
                water.frame(0, 0, SpacebaseTilemap.SIZE, SpacebaseTilemap.SIZE);
                titlebar.icon(water);
            } else {*/
                titlebar.icon(SpacebaseTilemap.tile(cell, tile));
            //}
            titlebar.label(SpacebaseRun.level.tileName(tile));
            desc += SpacebaseRun.level.tileDesc(tile);

        }
        titlebar.setRect(0, 0, WIDTH, 0);
        add(titlebar);

        RenderedTextMultiline info = PixelScene.renderMultiline(6);
        add(info);

        for (Blob blob : SpacebaseRun.level.blobs.values()) {
            if (blob.volume > 0 && blob.cur[cell] > 0 && blob.tileDesc() != null) {
                if (desc.length() > 0) {
                    desc += "\n\n";
                }
                desc += blob.tileDesc();
            }
        }

        info.text(desc);
        info.maxWidth(WIDTH);
        info.setPos(titlebar.left(), titlebar.bottom() + GAP);

        resize(WIDTH, (int) (info.top() + info.height()));
    }
}
