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

import com.wafitz.pixelspacebase.Assets;
import com.watabou.noosa.TextureFilm;

public class ShieldedShockTrooperSprite extends MobSprite {

    public ShieldedShockTrooperSprite() {
        super();

        texture(Assets.OUTER_COLONY_SHOCK_TROOPER);

        TextureFilm frames = new TextureFilm(texture, 12, 16);

        idle = new Animation(2, true);
        // The sheet is 11 frames wide, so the shielded trooper's second row
        // starts at frame 11 (not 21).
        idle.frames(frames, 11, 11, 11, 12, 11, 11, 12, 12);

        run = new Animation(12, true);
        run.frames(frames, 15, 16, 17, 18);

        attack = new Animation(12, false);
        attack.frames(frames, 13, 14);

        die = new Animation(12, false);
        die.frames(frames, 19, 20, 21);

        play(idle);
    }
}
