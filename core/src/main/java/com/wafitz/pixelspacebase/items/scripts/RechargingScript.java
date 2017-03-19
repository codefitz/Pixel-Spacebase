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
package com.wafitz.pixelspacebase.items.scripts;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Camoflage;
import com.wafitz.pixelspacebase.actors.buffs.Recharging;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.SpellSprite;
import com.wafitz.pixelspacebase.effects.particles.EnergyParticle;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class RechargingScript extends Script {

    private static final float BUFF_DURATION = 30f;

    {
        initials = 7;
    }

    @Override
    protected void doRead() {

        Buff.affect(curUser, Recharging.class, BUFF_DURATION);
        charge(curUser);

        Sample.INSTANCE.play(Assets.SND_READ);
        Camoflage.dispel();

        GLog.i(Messages.get(this, "surge"));
        SpellSprite.show(curUser, SpellSprite.CHARGE);
        setKnown();

        readAnimation();
    }

    public static void charge(Hero hero) {
        hero.sprite.centerEmitter().burst(EnergyParticle.FACTORY, 15);
    }

    @Override
    public int cost() {
        return isKnown() ? 40 * quantity : super.cost();
    }
}
