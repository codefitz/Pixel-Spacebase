package com.wafitz.pixelspacebase.sprites;

import com.wafitz.pixelspacebase.Assets;
import com.watabou.noosa.TextureFilm;

/** The existing blue safety terminal artwork identifies a fixed rinse station. */
public class DecontaminationStationSprite extends MobSprite {
    public DecontaminationStationSprite() {
        texture(Assets.ITEMS);
        TextureFilm frames = new TextureFilm(texture, 16, 16);
        idle = new Animation(1, true);
        idle.frames(frames, ItemSpriteSheet.BLUETERMINAL);
        run = idle.clone();
        attack = idle.clone();
        die = idle.clone();
        play(idle);
    }
}
