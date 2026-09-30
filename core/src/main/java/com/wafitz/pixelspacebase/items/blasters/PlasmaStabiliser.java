/*
 * Pixel Spacebase
 * Copyright (C) 2017 Wes Fitzpatrick
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.wafitz.pixelspacebase.items.blasters;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.EnergyBeam;
import com.wafitz.pixelspacebase.effects.particles.SnowParticle;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Launcher;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

/** Converts one targeted Command plasma tile into safe, solid deck plating. */
public class PlasmaStabiliser extends DamageBlaster {

    {
        // The existing cryogenic emitter art also communicates this stabiliser's effect.
        image = ItemSpriteSheet.FREEZEBLASTER;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        int cell = bolt.collisionPos;
        Char target = Actor.findChar(cell);
        boolean stabilised = SpacebaseRun.level.stabilisePlasma(cell);

        if (target != null) {
            processSoulMark(target, chargesPerCast());
            target.damage(damageRoll(), this);
        }

        if (stabilised) {
            if (SpacebaseRun.visible[cell]) {
                CellEmitter.get(cell).start(SnowParticle.FACTORY, 0.2f, 6);
            }
            GLog.p(Messages.get(this, "stabilised"));
            SpacebaseRun.observe();
            GameScene.updateFog();
        } else if (target == null) {
            GLog.i(Messages.get(this, "no_plasma"));
        }
    }

    @Override
    public int min(int lvl) {
        return Math.max(1, 1 + lvl);
    }

    @Override
    public int max(int lvl) {
        return Math.max(min(lvl), 3 + lvl);
    }

    @Override
    public void onHit(DM3000Launcher launcher, Char attacker, Char defender, int damage) {
        // Direct-shot damage is handled in onZap; the launcher hit has no added effect.
    }

    @Override
    protected void fx(Ballistica bolt, Callback callback) {
        EnergyBeam.blueLight(curUser.sprite.parent, bolt.sourcePos, bolt.collisionPos, callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    protected int initialCharges() {
        return 2;
    }
}
