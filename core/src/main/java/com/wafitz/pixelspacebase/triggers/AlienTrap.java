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
package com.wafitz.pixelspacebase.triggers;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.items.ExperimentalTech.StrengthTech;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

public class AlienTrap extends Trigger {

    {
        image = 7;
    }

    @Override
    public void activate() {
        Dungeon.level.drop(new Gadget(), pos).sprite.drop();
    }

    public static class Gadget extends Trigger.Gadget {
        {
            image = ItemSpriteSheet.ROTBERRY_GADGET;

            triggerClass = AlienTrap.class;
            craftingClass = StrengthTech.class;
        }
    }
}
