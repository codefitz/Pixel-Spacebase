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

import android.graphics.RectF;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.items.armor.HunterSpaceSuit;
import com.wafitz.pixelspacebase.items.armor.HoverPod;
import com.wafitz.pixelspacebase.items.armor.Loader;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class HeroSprite extends CharSprite {

    private static final int FRAME_WIDTH = 12;
    private static final int FRAME_HEIGHT = 15;

    private static final int RUN_FRAMERATE = 20;
    private static final int HOVERPOD_FRAME_SIZE = 20;
    private static final float LOADER_FRAME_SCALE = 20f / 28f;
    // Hunter frames have one logical pixel of transparent padding on each edge.
    private static final float HUNTER_FRAME_SCALE = 16f / (FRAME_HEIGHT - 2);

    private static TextureFilm tiers;

    private Animation fly;
    private Animation read;
    private boolean itemForm;
    private boolean restoringHeroForm;
    private boolean hunterSuitForm;
    private boolean loaderSuitForm;

    @Override
    public void frame(RectF frame) {
        if (hunterSuitForm) {
            RectF fittedFrame = new RectF(frame);
            fittedFrame.inset(1f / texture.logicalWidth(), 1f / texture.logicalHeight());
            super.frame(fittedFrame);
            width *= HUNTER_FRAME_SCALE;
            height *= HUNTER_FRAME_SCALE;
            updateVertices();
        } else {
            super.frame(frame);
            if (loaderSuitForm) {
                width *= LOADER_FRAME_SCALE;
                height *= LOADER_FRAME_SCALE;
                updateVertices();
            }
        }
    }

    public HeroSprite() {
        super();

        link(SpacebaseRun.hero);

        texture(PixelDungeonSkins.heroSheet(SpacebaseRun.hero.heroClass));
        updateArmor();

        if (HunterSpaceSuit.jetpackEnabled(SpacebaseRun.hero))
            add(State.LEVITATING);

        if (ch.isAlive())
            idle();
        else
            die();
    }

    public void updateArmor() {
        itemForm = false;
        resetSuitScale();
        Hero hero = (Hero) ch;
        hunterSuitForm = !PixelDungeonSkins.active()
                && hero.belongings.armor instanceof HunterSpaceSuit;
        loaderSuitForm = !PixelDungeonSkins.active()
                && hero.belongings.armor instanceof Loader;
        if (loaderSuitForm) {
            updateLoader(hero.heroClass);
            applySavedItemForm(hero);
            return;
        }
        if (!PixelDungeonSkins.active() && hero.belongings.armor instanceof HoverPod) {
            updateHoverPod(hero.heroClass);
            applySavedItemForm(hero);
            return;
        }
        texture(PixelDungeonSkins.heroSheet(hero.heroClass));

        TextureFilm tierAtlas = PixelDungeonSkins.active()
                ? new TextureFilm(texture, texture.logicalWidth(), FRAME_HEIGHT) : tiers();
        int armorTier = PixelDungeonSkins.active() ? Math.max(0, Math.min(6, hero.tier())) : hero.tier();
        TextureFilm film = new TextureFilm(tierAtlas, armorTier, FRAME_WIDTH, FRAME_HEIGHT);

        configureSuitAnimations(film);
        applySavedItemForm(hero);
        place(ch.pos);
    }

    private void updateHoverPod(HeroClass heroClass) {
        switch (heroClass) {
            case COMMANDER: texture(Assets.HOVERPOD_COMMANDER); break;
            case DM3000: texture(Assets.HOVERPOD_DM3000); break;
            case CAPTAIN: texture(Assets.HOVERPOD_CAPTAIN); break;
            default: texture(Assets.HOVERPOD_SHAPESHIFTER); break;
        }
        // Dedicated 80px frames retain a readable pilot and canopy beyond the 64px tile.
        configureSuitAnimations(new TextureFilm(texture, HOVERPOD_FRAME_SIZE, HOVERPOD_FRAME_SIZE));
        place(ch.pos);
    }

    private void configureSuitAnimations(TextureFilm film) {
        idle = new Animation(1, true);
        idle.frames(film, 0, 0, 0, 1, 0, 0, 1, 1);

        run = new Animation(RUN_FRAMERATE, true);
        run.frames(film, 2, 3, 4, 5, 6, 7);

        die = new Animation(20, false);
        die.frames(film, 8, 9, 10, 11, 12, 11);

        attack = new Animation(15, false);
        attack.frames(film, 13, 14, 15, 0);

        zap = attack.clone();

        operate = new Animation(8, false);
        operate.frames(film, 16, 17, 16, 17);

        fly = new Animation(1, true);
        fly.frames(film, 18);

        read = new Animation(20, false);
        read.frames(film, 19, 20, 20, 20, 20, 20, 20, 20, 20, 19);
        idle();
    }

    private void applySavedItemForm(Hero hero) {
        if (restoringHeroForm) return;
        com.wafitz.pixelspacebase.actors.buffs.Shapeshifted shifted = hero.buff(
                com.wafitz.pixelspacebase.actors.buffs.Shapeshifted.class);
        if (shifted != null) shifted.fx(true);
    }

    private void resetSuitScale() {
        scale.set(1f);
        origin.set(0f, 0f);
    }

    private void updateLoader(HeroClass heroClass) {
        switch (heroClass) {
            case COMMANDER: texture(Assets.LOADER_COMMANDER); break;
            case DM3000: texture(Assets.LOADER_DM3000); break;
            case CAPTAIN: texture(Assets.LOADER_CAPTAIN); break;
            default: texture(Assets.LOADER_SHAPESHIFTER); break;
        }
        // Keep the wide claw gutter; frame() scales the chassis to roughly the Hoverpod's size.
        TextureFilm film = new TextureFilm(texture, 48, 28);
        idle = new Animation(4, true);
        idle.frames(film, 0, 1);
        run = new Animation(8, true);
        run.frames(film, 2, 3, 4, 5);
        attack = new Animation(10, false);
        attack.frames(film, 6, 7, 0);
        zap = attack.clone();
        operate = new Animation(8, false);
        operate.frames(film, 8, 9, 8, 9);
        die = new Animation(10, false);
        die.frames(film, 10, 11);
        fly = new Animation(1, true);
        fly.frames(film, 0);
        read = operate.clone();
        idle();
        place(ch.pos);
    }

    public void shapeshiftToItem(int itemImage) {
        itemForm = true;
        hunterSuitForm = false;
        loaderSuitForm = false;
        resetSuitScale();

        texture(PixelDungeonSkins.itemSheet());
        TextureFilm film = new TextureFilm(texture, ItemSprite.SIZE, ItemSprite.SIZE);

        idle = new Animation(1, true);
        idle.frames(film, PixelDungeonSkins.itemFrame(itemImage));

        run = idle.clone();
        die = idle.clone();
        attack = idle.clone();
        zap = idle.clone();
        operate = idle.clone();
        fly = idle.clone();
        read = idle.clone();

        idle();
        place(ch.pos);
    }

    public void restoreHeroForm() {
        if (itemForm) {
            texture(PixelDungeonSkins.heroSheet(SpacebaseRun.hero.heroClass));
            // Buff.detach removes the buff after fx(false); do not reapply it during this update.
            restoringHeroForm = true;
            try { updateArmor(); }
            finally { restoringHeroForm = false; }
            idle();
        }
    }

    @Override
    public void place(int p) {
        super.place(p);
        Camera.main.target = this;
    }

    @Override
    public void move(int from, int to) {
        super.move(from, to);
        if (ch.flying) {
            play(fly);
        }
        Camera.main.target = this;
    }

    @Override
    public void jump(int from, int to, Callback callback) {
        super.jump(from, to, callback);
        play(fly);
    }

    public void read() {
        animCallback = new Callback() {
            @Override
            public void call() {
                idle();
                ch.onOperateComplete();
            }
        };
        play(read);
    }

    @Override
    public void bloodBurstA(PointF from, int damage) {
        //Does nothing.

		/*
         * This is both for visual clarity, and also for content ratings regarding violence
		 * towards human characters. The heroes are the only human or human-like characters which
		 * participate in combat, so removing all blood associated with them is a simple way to
		 * reduce the violence rating of the game.
		 */
    }

    @Override
    public void update() {
        sleeping = ch.isAlive() && ((Hero) ch).resting;

        super.update();
    }

    public boolean sprint(boolean on) {
        run.delay = on ? 0.667f / RUN_FRAMERATE : 1f / RUN_FRAMERATE;
        return on;
    }

    static TextureFilm tiers() {
        if (tiers == null) {
            SmartTexture texture = TextureCache.get(Assets.SHAPESHIFTER);
            tiers = new TextureFilm(texture, texture.logicalWidth(), FRAME_HEIGHT);
        }

        return tiers;
    }

    public static Image avatar(HeroClass cl, int armorTier) {

        if (PixelDungeonSkins.active()) {
            Image avatar = new Image(PixelDungeonSkins.heroSheet(cl));
            TextureFilm rows = new TextureFilm(avatar.texture, avatar.texture.logicalWidth(), FRAME_HEIGHT);
            RectF patch = rows.get(Math.max(0, Math.min(6, armorTier)));
            RectF frame = avatar.texture.uvRect(1, 0, FRAME_WIDTH, FRAME_HEIGHT);
            frame.offset(patch.left, patch.top); avatar.frame(frame);
            return avatar;
        }

        RectF patch = tiers().get(armorTier);
        Image avatar = new Image(cl.spritesheet());
        RectF frame = avatar.texture.uvRect(1, 0, FRAME_WIDTH, FRAME_HEIGHT);
        frame.offset(patch.left, patch.top);
        avatar.frame(frame);

        return avatar;
    }
}
