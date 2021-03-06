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
package com.wafitz.pixelspacebase.levels.vents;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.MagicMissile;
import com.wafitz.pixelspacebase.effects.particles.ShadowParticle;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class GrimVent extends Vent {

    {
        color = GREY;
        shape = LARGE_DOT;
    }

    @Override
    public Vent hide() {
        //cannot hide this trap
        return reveal();
    }

    @Override
    public void activate() {
        Char target = Actor.findChar(pos);

        //find the closest char that can be aimed at
        if (target == null) {
            for (Char ch : Actor.chars()) {
                Ballistica bolt = new Ballistica(pos, ch.pos, Ballistica.PROJECTILE);
                if (bolt.collisionPos == ch.pos &&
                        (target == null || Dungeon.level.distance(pos, ch.pos) < Dungeon.level.distance(pos, target.pos))) {
                    target = ch;
                }
            }
        }

        if (target != null) {
            final Char finalTarget = target;
            final GrimVent vent = this;
            MagicMissile.shadow(target.sprite.parent, pos, target.pos, new Callback() {
                @Override
                public void call() {
                    if (!finalTarget.isAlive()) return;
                    if (finalTarget == Dungeon.hero) {
                        //almost kill the player
                        if (((float) finalTarget.HP / finalTarget.HT) >= 0.9f) {
                            finalTarget.damage((finalTarget.HP - 1), vent);
                            //kill 'em
                        } else {
                            finalTarget.damage(finalTarget.HP, vent);
                        }
                        Sample.INSTANCE.play(Assets.SND_CURSED);
                        if (!finalTarget.isAlive()) {
                            Dungeon.fail(GrimVent.class);
                            GLog.n(Messages.get(GrimVent.class, "ondeath"));
                        }
                    } else {
                        finalTarget.damage(finalTarget.HP, this);
                        Sample.INSTANCE.play(Assets.SND_BURNING);
                    }
                    finalTarget.sprite.emitter().burst(ShadowParticle.UP, 10);
                    if (!finalTarget.isAlive()) finalTarget.next();
                }
            });
        } else {
            CellEmitter.get(pos).burst(ShadowParticle.UP, 10);
            Sample.INSTANCE.play(Assets.SND_BURNING);
        }
    }
}
