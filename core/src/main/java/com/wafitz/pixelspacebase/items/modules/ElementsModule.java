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
package com.wafitz.pixelspacebase.items.modules;

import com.wafitz.pixelspacebase.actors.blobs.ToxicGas;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.actors.buffs.Venom;
import com.wafitz.pixelspacebase.actors.mobs.Eye;
import com.wafitz.pixelspacebase.actors.mobs.Warlock;
import com.wafitz.pixelspacebase.actors.mobs.Yog;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.watabou.utils.Random;

import java.util.HashSet;

public class ElementsModule extends Module {

    @Override
    protected ModuleBuff buff() {
        return new Resistance();
    }

    private static final HashSet<Class<?>> EMPTY = new HashSet<>();
    public static final HashSet<Class<?>> FULL;

    static {
        FULL = new HashSet<>();
        FULL.add(Burning.class);
        FULL.add(ToxicGas.class);
        FULL.add(Poison.class);
        FULL.add(Venom.class);
        FULL.add(LightningVent.Electricity.class);
        FULL.add(Warlock.class);
        FULL.add(Eye.class);
        FULL.add(Yog.BurningFist.class);
    }

    public class Resistance extends ModuleBuff {

        public HashSet<Class<?>> resistances() {
            if (Random.Int(level() + 2) >= 2) {
                return FULL;
            } else {
                return EMPTY;
            }
        }

        public float durationFactor() {
            return level() < 0 ? 1 : (1 + 0.5f * level()) / (1 + level());
        }
    }
}
