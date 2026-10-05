package com.wafitz.pixelspacebase.sprites;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.npcs.NPC;
import com.wafitz.pixelspacebase.actors.mobs.npcs.StationCat;
import com.wafitz.pixelspacebase.actors.mobs.npcs.YRescuer;
import com.watabou.noosa.TextureFilm;

/** Animation layouts adapted from Pixel Dungeon, Copyright 2012-2015 Oleg Dolya, GPL-3.0-or-later. */
public class PixelDungeonMobSprite extends MobSprite {
    public static CharSprite forMob(Mob mob) {
        // Special boss/quest sprites expose additional animation methods; keep those compatible.
        if (!PixelDungeonSkins.active() || mob.properties().contains(Char.Property.BOSS)
                || mob.getClass().getSimpleName().equals("QueenXeno")) return null;
        return new PixelDungeonMobSprite(mob);
    }

    public PixelDungeonMobSprite(Mob mob) {
        String type = mob.getClass().getSimpleName();
        if (mob instanceof YRescuer) {
            texture(Assets.PD_ART + "shopkeeper.png");
            TextureFilm frames = new TextureFilm(texture, 14, 14);
            idle = new Animation(10, true); idle.frames(frames, 1, 1, 1, 1, 1, 0, 0, 0, 0);
            run = idle.clone(); attack = idle.clone(); die = new Animation(20, false); die.frames(frames, 0);
        } else if (mob instanceof StationCat) {
            texture(Assets.PD_ART + "pet.png");
            TextureFilm frames = new TextureFilm(texture, 16, 16);
            idle = new Animation(2, true); idle.frames(frames, 0, 0, 0, 0, 0, 0, 1);
            run = new Animation(10, true); run.frames(frames, 2, 3, 4, 5, 6);
            attack = new Animation(10, false); attack.frames(frames, 2, 3, 4, 5, 6);
            die = new Animation(20, false); die.frames(frames, 0);
        } else if (mob instanceof NPC || !mob.hostile) {
            texture(Assets.PD_ART + "sheep.png");
            TextureFilm frames = new TextureFilm(texture, 16, 15);
            idle = new Animation(8, true); idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0);
            run = idle.clone(); attack = idle.clone(); die = new Animation(20, false); die.frames(frames, 0);
        } else if (type.equals("Xenomorph") || type.equals("Albino")) {
            texture(Assets.PD_ART + "rat.png");
            TextureFilm frames = new TextureFilm(texture, 16, 15);
            idle = new Animation(2, true); idle.frames(frames, 0, 0, 0, 1);
            run = new Animation(10, true); run.frames(frames, 6, 7, 8, 9, 10);
            attack = new Animation(15, false); attack.frames(frames, 2, 3, 4, 5, 0);
            die = new Animation(10, false); die.frames(frames, 11, 12, 13, 14);
        } else if (type.equals("MaintenanceCrawler") || type.equals("ArmoredCrawler")) {
            texture(Assets.PD_ART + "crab.png");
            TextureFilm frames = new TextureFilm(texture, 16, 16);
            idle = new Animation(5, true); idle.frames(frames, 0, 1, 0, 2);
            run = new Animation(15, true); run.frames(frames, 3, 4, 5, 6);
            attack = new Animation(12, false); attack.frames(frames, 7, 8, 9);
            die = new Animation(12, false); die.frames(frames, 10, 11, 12, 13);
        } else {
            texture(Assets.PD_ART + "gnoll.png");
            TextureFilm frames = new TextureFilm(texture, 12, 15);
            idle = new Animation(2, true); idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);
            run = new Animation(12, true); run.frames(frames, 4, 5, 6, 7);
            attack = new Animation(12, false); attack.frames(frames, 2, 3, 0);
            die = new Animation(12, false); die.frames(frames, 8, 9, 10);
        }
        idle();
    }
}
