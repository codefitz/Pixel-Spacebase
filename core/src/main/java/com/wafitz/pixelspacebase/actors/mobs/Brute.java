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
import com.wafitz.pixelspacebase.actors.buffs.Terror;
import com.wafitz.pixelspacebase.items.Parts;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.BruteSprite;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

class Brute extends Mob {

    {
        spriteClass = BruteSprite.class;

        HP = HT = 40;
        defenseSkill = 15;

        EXP = 8;
        maxLvl = 15;

        loot = Parts.class;
        lootChance = 0.5f;
    }

    private boolean enraged = false;

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        enraged = HP < HT / 4;
    }

    @Override
    public int damageRoll() {
        return enraged ?
                Random.NormalIntRange(15, 45) :
                Random.NormalIntRange(6, 26);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(dmg, src);

        if (isAlive() && !enraged && HP < HT / 4) {
            enraged = true;
            spend(TICK);
            if (Dungeon.visible[pos]) {
                GLog.w(Messages.get(this, "enraged_text"));
                sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "enraged"));
            }
        }
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Terror.class);
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }
}
