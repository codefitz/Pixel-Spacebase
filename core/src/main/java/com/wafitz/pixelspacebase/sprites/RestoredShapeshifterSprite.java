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
package com.wafitz.pixelspacebase.sprites;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.DungeonTilemap;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PointF;

public class RestoredShapeshifterSprite extends Image {

    private static final int FRAME_WIDTH = 12;
    private static final int FRAME_HEIGHT = 15;

    private final int pos;

    public RestoredShapeshifterSprite(int pos) {
        super(HeroClass.SHAPESHIFTER.spritesheet());

        this.pos = pos;
        frame(new TextureFilm(texture, FRAME_WIDTH, FRAME_HEIGHT).get(0));
        place(pos);
        visible = Dungeon.visible[pos];
    }

    private void place(int cell) {
        int csize = DungeonTilemap.SIZE;
        PointF p = new PointF(
                PixelScene.align(Camera.main, ((cell % Dungeon.level.width()) + 0.5f) * csize - width * 0.5f),
                PixelScene.align(Camera.main, ((cell / Dungeon.level.width()) + 1.0f) * csize - height)
        );
        point(p);
    }

    @Override
    public void update() {
        super.update();
        visible = Dungeon.visible[pos];
    }

    public static void show(int pos) {
        GameScene.effect(new RestoredShapeshifterSprite(pos));
    }
}
