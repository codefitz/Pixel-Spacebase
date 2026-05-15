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
import com.wafitz.pixelspacebase.actors.buffs.XenoInfection;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.food.AlienPod;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

public class AlienEgg extends Mine {

    {
        image = 8;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);
        if (ch instanceof Hero) {
            XenoInfection.infect((Hero) ch);
        }
        Dungeon.level.drop(new AlienPod(), pos).sprite.drop();
    }

    public static class Device extends Mine.Device {
        {
            image = ItemSpriteSheet.BLACK_GOO;

            mineClass = AlienEgg.class;
            craftingClass = null;
        }

        @Override
        public String desc() {
            return com.wafitz.pixelspacebase.messages.Messages.get(this, "desc");
        }

        @Override
        public String info() {
            return com.wafitz.pixelspacebase.messages.Messages.get(this, "info", desc(), minename());
        }

    }
}
