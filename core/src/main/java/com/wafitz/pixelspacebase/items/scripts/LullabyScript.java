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
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Camoflage;
import com.wafitz.pixelspacebase.actors.buffs.Knockout;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class LullabyScript extends Script {

    {
        initials = 1;
    }

    @Override
    protected void doRead() {

        curUser.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
        Sample.INSTANCE.play(Assets.SND_LULLABY);
        Camoflage.dispel();

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (Level.fieldOfView[mob.pos]) {
                Buff.affect(mob, Knockout.class);
                mob.sprite.centerEmitter().start(Speck.factory(Speck.NOTE), 0.3f, 5);
            }
        }

        Buff.affect(curUser, Knockout.class);

        GLog.i(Messages.get(this, "sooth"));

        setKnown();

        readAnimation();
    }

    @Override
    public int price() {
        return isKnown() ? 40 * quantity : super.price();
    }
}
