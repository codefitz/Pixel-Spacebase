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
import com.wafitz.pixelspacebase.actors.buffs.Bleeding;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Cripple;
import com.wafitz.pixelspacebase.actors.buffs.LockedDown;
import com.wafitz.pixelspacebase.effects.Wound;

public class GrippingVent extends Vent {

    {
        color = GREY;
        shape = CROSSHAIR;
    }

    @Override
    public void activate() {

        Char c = Actor.findChar(pos);

        if (c != null) {
            int damage = Math.max(0, (Dungeon.depth) - (c.drRoll() / 2));
            Buff.affect(c, Bleeding.class).set(damage);
            Buff.prolong(c, Cripple.class, 15f);
            Buff.prolong(c, LockedDown.class, 5f);
            Wound.hit(c);
        } else {
            Wound.hit(pos);
        }

    }
}
