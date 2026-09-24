/*
 * Pixel Spacebase, Copyright (C) 2017 Wes Fitzpatrick
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.wafitz.pixelspacebase.sprites;

import com.wafitz.pixelspacebase.Assets;
import com.watabou.noosa.TextureFilm;

public class HolodeckLegionarySprite extends MobSprite {

    public HolodeckLegionarySprite() {
        super();

        texture(Assets.HOLODECK_LEGIONARY);

        TextureFilm frames = new TextureFilm(texture, 24, 24);

        idle = new Animation(8, true);
        idle.frames(frames, 0, 1);

        run = new Animation(12, true);
        run.frames(frames, 0, 1, 2, 3, 4, 5);

        attack = new Animation(12, false);
        attack.frames(frames, 1, 2, 3);

        die = new Animation(10, false);
        die.frames(frames, 5, 4, 3);

        play(idle);
    }
}
