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
package com.wafitz.pixelspacebase.effects.particles;

import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Game;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Random;

/** Small binary glyphs that scroll up from an active terminal. */
public class CodeParticle extends BitmapText {

    public static final Emitter.Factory FACTORY = new Emitter.Factory() {
        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((CodeParticle) emitter.recycle(CodeParticle.class)).reset(x, y);
        }
    };

    private static final int[] COLORS = {
            0xFF63E8EA,
            0xFF8DFFA6,
            0xFFC1FFF2
    };

    private float lifespan;
    private float left;

    public CodeParticle() {
        super(PixelScene.pixelFont);
    }

    public void reset(float x, float y) {
        revive();
        resetColor();

        text(Random.Int(2) == 0 ? "0" : "1");
        color(COLORS[Random.Int(COLORS.length)]);

        this.x = x;
        this.y = y;
        speed.set(Random.Float(-1.5f, 1.5f), Random.Float(-20, -14));
        acc.set(0, 0);
        angle = 0;
        angularSpeed = 0;
        scale.set(1);

        left = lifespan = Random.Float(0.65f, 0.95f);
        alpha(0.9f);
    }

    @Override
    public void update() {
        super.update();

        if ((left -= Game.elapsed) <= 0) {
            kill();
        } else {
            alpha(0.9f * left / lifespan);
        }
    }
}
