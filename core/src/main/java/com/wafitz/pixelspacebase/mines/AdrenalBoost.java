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
import com.wafitz.pixelspacebase.actors.buffs.Upgrade;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperienceBooster;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class AdrenalBoost extends Mine {

    {
        image = 11;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch != null) Buff.prolong(ch, Upgrade.class, 30f);

        if (Random.Int(5) == 0) {
            Dungeon.level.drop(new Device(), pos).sprite.drop();
        }
    }

    public static class Device extends Mine.Device {

        {
            image = ItemSpriteSheet.ADRENAL_BOOST;

            mineClass = AdrenalBoost.class;
            craftingClass = ExperienceBooster.class;
        }
    }
}
