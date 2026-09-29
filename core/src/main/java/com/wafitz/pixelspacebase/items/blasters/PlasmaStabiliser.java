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
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.EnergyBeam;
import com.wafitz.pixelspacebase.effects.particles.SnowParticle;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Launcher;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.BArray;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

/** Converts a small area of Command plasma into safe, solid deck plating. */
public class PlasmaStabiliser extends Blaster {

    private static final int BASE_RADIUS = 2;

    {
        // The existing cryogenic emitter art also communicates this stabiliser's effect.
        image = ItemSpriteSheet.FREEZEBLASTER;
    }

    @Override
    protected void onZap(Ballistica bolt) {
        int radius = Math.min(4, BASE_RADIUS + level() / 2);
        PathFinder.buildDistanceMap(bolt.collisionPos, BArray.not(Level.solid, null), radius);

        int stabilised = 0;
        for (int cell = 0; cell < SpacebaseRun.level.length(); cell++) {
            if (PathFinder.distance[cell] <= radius && SpacebaseRun.level.stabilisePlasma(cell)) {
                stabilised++;
                if (SpacebaseRun.visible[cell]) {
                    CellEmitter.get(cell).start(SnowParticle.FACTORY, 0.2f, 6);
                }
            }
        }

        if (stabilised > 0) {
            GLog.p(Messages.get(this, "stabilised", stabilised));
            SpacebaseRun.observe();
            GameScene.updateFog();
        } else {
            GLog.i(Messages.get(this, "no_plasma"));
        }
    }

    @Override
    public void onHit(DM3000Launcher launcher, Char attacker, Char defender, int damage) {
        // This is a terrain-control tool rather than a damage effect.
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
