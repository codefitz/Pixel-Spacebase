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
package com.wafitz.pixelspacebase.actors.mobs;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.food.MysteryMeat;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.sprites.GreatCrabSprite;
import com.wafitz.pixelspacebase.utils.GLog;

public class GreatCrab extends Crab {

    {
        spriteClass = GreatCrabSprite.class;

        HP = HT = 25;
        defenseSkill = 0; //see damage()
        baseSpeed = 1f;

        EXP = 6;

        state = WANDERING;

        properties.add(Property.MINIBOSS);
    }

    private int moving = 0;

    @Override
    protected boolean getCloser(int target) {
        //this is used so that the crab remains slower, but still detects the player at the expected rate.
        moving++;
        if (moving < 3) {
            return super.getCloser(target);
        } else {
            moving = 0;
            return true;
        }

    }

    @Override
    public void damage(int dmg, Object src) {
        //crab blocks all attacks originating from the hero or enemy characters or vents if it is alerted.
        //All direct damage from these sources is negated, no exceptions. blob/debuff effects go through as normal.
        if ((enemySeen && state != SLEEPING && paralysed == 0)
                && (src instanceof Blaster || src instanceof LightningVent.Electricity || src instanceof Char)) {
            GLog.n(Messages.get(this, "noticed"));
            sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, "blocked"));
        } else {
            super.damage(dmg, src);
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        Hologram.Quest.process();

        Dungeon.level.drop(new MysteryMeat(), pos);
        Dungeon.level.drop(new MysteryMeat(), pos).sprite.drop();
    }
}
