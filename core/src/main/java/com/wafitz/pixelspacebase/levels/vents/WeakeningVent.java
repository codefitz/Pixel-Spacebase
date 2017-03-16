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

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.TimeSink;
import com.wafitz.pixelspacebase.actors.buffs.Weakness;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.particles.ShadowParticle;

public class WeakeningVent extends Vent {

    {
        color = GREEN;
        shape = WAVES;
    }

    @Override
    public void activate() {
        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
        }

        Char ch = Actor.findChar(pos);
        if (ch == Dungeon.hero) {
            Buff.prolong(ch, Weakness.class, Weakness.duration(ch) * 2f);
        } else if (ch != null) {
            Buff.prolong(ch, TimeSink.class, TimeSink.duration(ch));
        }
    }
}
