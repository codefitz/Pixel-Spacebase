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

import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Chill;
import com.wafitz.pixelspacebase.actors.buffs.Frost;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.ExperimentalTech.LiquidFlameTech;
import com.wafitz.pixelspacebase.items.blasters.FireBlaster;
import com.wafitz.pixelspacebase.items.weapon.enchantments.Blazing;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.sprites.ElementalSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

class Elemental extends Mob {

    {
        spriteClass = ElementalSprite.class;

        HP = HT = 65;
        defenseSkill = 20;

        EXP = 10;
        maxLvl = 20;

        flying = true;

        loot = new LiquidFlameTech();
        lootChance = 0.1f;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(16, 26);
    }

    @Override
    public int attackSkill(Char target) {
        return 25;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (Random.Int(2) == 0) {
            Buff.affect(enemy, Burning.class).reignite(enemy);
        }

        return damage;
    }

    @Override
    public void add(Buff buff) {
        if (buff instanceof Burning) {
            if (HP < HT) {
                HP++;
                sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
            }
        } else if (buff instanceof Frost || buff instanceof Chill) {
            if (Level.water[this.pos])
                damage(Random.NormalIntRange(HT / 2, HT), buff);
            else
                damage(Random.NormalIntRange(1, HT * 2 / 3), buff);
        } else {
            super.add(buff);
        }
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Burning.class);
        IMMUNITIES.add(Blazing.class);
        IMMUNITIES.add(FireBlaster.class);
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }
}
