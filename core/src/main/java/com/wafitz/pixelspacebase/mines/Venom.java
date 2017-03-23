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
package com.wafitz.pixelspacebase.mines;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.particles.PoisonParticle;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ToxicAgent;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

public class Venom extends Mine {

    {
        image = 2;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch != null) {
            Buff.affect(ch, Poison.class).set(Poison.durationFactor(ch) * (4 + Dungeon.depth / 2));
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 3);
        }
    }

    public static class Device extends Mine.Device {
        {
            image = ItemSpriteSheet.SORROWMOSS_DEVICE;

            mineClass = Venom.class;
            craftingClass = ToxicAgent.class;
        }
    }
}
