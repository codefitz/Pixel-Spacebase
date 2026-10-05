package com.wafitz.pixelspacebase.sprites;

import com.wafitz.pixelspacebase.Assets;
import com.watabou.noosa.TextureFilm;

public class AlienSprite extends MobSprite {
    public AlienSprite() {
        texture(Assets.ALIEN_COLONIST);
        TextureFilm film = new TextureFilm(texture, 16, 16);
        idle = new Animation(1, true); idle.frames(film, 0);
        run = idle.clone(); attack = idle.clone(); die = idle.clone();
        idle();
    }
}
