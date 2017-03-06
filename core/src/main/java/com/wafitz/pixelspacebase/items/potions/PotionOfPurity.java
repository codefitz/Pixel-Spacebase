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
package com.wafitz.pixelspacebase.items.potions;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.ConfusionGas;
import com.wafitz.pixelspacebase.actors.blobs.ParalyticGas;
import com.wafitz.pixelspacebase.actors.blobs.StenchGas;
import com.wafitz.pixelspacebase.actors.blobs.ToxicGas;
import com.wafitz.pixelspacebase.actors.blobs.VenomGas;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.GasesImmunity;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.BArray;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class PotionOfPurity extends Potion {

    private static final int DISTANCE = 5;

    {
        initials = 9;
    }

    @Override
    public void shatter(int cell) {

        PathFinder.buildDistanceMap(cell, BArray.not(Level.losBlocking, null), DISTANCE);

        boolean procd = false;

        Blob[] blobs = {
                Dungeon.level.blobs.get(ToxicGas.class),
                Dungeon.level.blobs.get(ParalyticGas.class),
                Dungeon.level.blobs.get(ConfusionGas.class),
                Dungeon.level.blobs.get(StenchGas.class),
                Dungeon.level.blobs.get(VenomGas.class)
        };

        for (int j = 0; j < blobs.length; j++) {

            Blob blob = blobs[j];
            if (blob == null || blob.volume == 0) {
                continue;
            }

            for (int i = 0; i < Dungeon.level.length(); i++) {
                if (PathFinder.distance[i] < Integer.MAX_VALUE) {

                    int value = blob.cur[i];
                    if (value > 0) {

                        blob.cur[i] = 0;
                        blob.volume -= value;
                        procd = true;

                        if (Dungeon.visible[i]) {
                            CellEmitter.get(i).burst(Speck.factory(Speck.DISCOVER), 1);
                        }
                    }

                }
            }
        }

        boolean heroAffected = PathFinder.distance[Dungeon.hero.pos] < Integer.MAX_VALUE;

        if (procd) {

            if (Dungeon.visible[cell]) {
                splash(cell);
                Sample.INSTANCE.play(Assets.SND_SHATTER);
            }

            setKnown();

            if (heroAffected) {
                GLog.p(Messages.get(this, "freshness"));
            }

        } else {

            super.shatter(cell);

            if (heroAffected) {
                GLog.i(Messages.get(this, "freshness"));
                setKnown();
            }

        }
    }

    @Override
    public void apply(Hero hero) {
        GLog.w(Messages.get(this, "no_smell"));
        Buff.prolong(hero, GasesImmunity.class, GasesImmunity.DURATION);
        setKnown();
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
