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
import com.wafitz.pixelspacebase.actors.buffs.Chill;
import com.wafitz.pixelspacebase.actors.buffs.Frost;
import com.wafitz.pixelspacebase.effects.Splash;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class FrostVent extends Vent {

    {
        color = WHITE;
        shape = STARS;
    }

    @Override
    public void activate() {

        if (Dungeon.visible[pos]) {
            Splash.at(pos, 0xFFB2D6FF, 10);
            Sample.INSTANCE.play(Assets.SND_SHATTER);
        }

        Heap heap = Dungeon.level.heaps.get(pos);
        if (heap != null) heap.freeze();

        Char ch = Actor.findChar(pos);
        if (ch != null) {
            ch.damage(Random.NormalIntRange(1, Dungeon.depth), this);
            Chill.prolong(ch, Frost.class, 10f + Random.Int(Dungeon.depth));
            if (!ch.isAlive() && ch == Dungeon.hero) {
                Dungeon.fail(getClass());
                GLog.n(Messages.get(this, "ondeath"));
            }
        }
    }
}
