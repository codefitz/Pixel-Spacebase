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
package com.wafitz.pixelspacebase.actors.buffs;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.artifacts.AlienDNA;

public class Regeneration extends Buff {

    private static final float REGENERATION_DELAY = 10;

    @Override
    public boolean act() {
        if (target.isAlive()) {

            if (target.HP < target.HT && !((Hero) target).isStarving()) {
                LockedFloor lock = target.buff(LockedFloor.class);
                if (target.HP > 0 && (lock == null || lock.regenOn())) {
                    target.HP += 1;
                    if (target.HP == target.HT) {
                        ((Hero) target).resting = false;
                    }
                }
            }

            AlienDNA.chaliceRegen regenBuff = Dungeon.hero.buff(AlienDNA.chaliceRegen.class);

            if (regenBuff != null)
                if (regenBuff.isMalfunctioning())
                    spend(REGENERATION_DELAY * 1.5f);
                else
                    spend(REGENERATION_DELAY - regenBuff.itemLevel() * 0.9f);
            else
                spend(REGENERATION_DELAY);

        } else {

            diactivate();

        }

        return true;
    }
}