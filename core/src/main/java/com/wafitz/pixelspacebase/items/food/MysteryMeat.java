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
package com.wafitz.pixelspacebase.items.food;

import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.buffs.LockedDown;
import com.wafitz.pixelspacebase.actors.buffs.Paralysis;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.actors.buffs.TimeSink;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Random;

public class MysteryMeat extends Food {

    {
        image = ItemSpriteSheet.MEAT;
        energy = Hunger.STARVING - Hunger.HUNGRY;
        hornValue = 1;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_USE)) {
            effect(hero);
        }
    }

    public int cost() {
        return 5 * quantity;
    }

    public static void effect(Hero hero) {
        switch (Random.Int(5)) {
            case 0:
                GLog.w(Messages.get(MysteryMeat.class, "hot"));
                Buff.affect(hero, Burning.class).reignite(hero);
                break;
            case 1:
                GLog.w(Messages.get(MysteryMeat.class, "legs"));
                Buff.prolong(hero, LockedDown.class, Paralysis.duration(hero));
                break;
            case 2:
                GLog.w(Messages.get(MysteryMeat.class, "not_well"));
                Buff.affect(hero, Poison.class).set(Poison.durationFactor(hero) * hero.HT / 5);
                break;
            case 3:
                GLog.w(Messages.get(MysteryMeat.class, "stuffed"));
                Buff.prolong(hero, TimeSink.class, TimeSink.duration(hero));
                break;
        }
    }
}