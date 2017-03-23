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

import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Vertigo;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalRockets;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

public class Disorient extends Mine {

    {
        image = 9;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch != null) {
            Buff.affect(ch, Vertigo.class, Vertigo.duration(ch));
        }
    }

    public static class Device extends Mine.Device {
        {
            image = ItemSpriteSheet.STORMVINE_DEVICE;

            mineClass = Disorient.class;
            craftingClass = ExperimentalRockets.class;
        }
    }
}
