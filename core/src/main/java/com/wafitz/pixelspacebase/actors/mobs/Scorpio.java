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
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Cripple;
import com.wafitz.pixelspacebase.actors.buffs.Light;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.food.MysteryMeat;
import com.wafitz.pixelspacebase.items.potions.PotionOfHealing;
import com.wafitz.pixelspacebase.items.weapon.enchantments.Vampiric;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.sprites.ScorpioSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Scorpio extends Mob {

    {
        spriteClass = ScorpioSprite.class;

        HP = HT = 95;
        defenseSkill = 24;
        viewDistance = Light.DISTANCE;

        EXP = 14;
        maxLvl = 25;

        loot = new PotionOfHealing();
        lootChance = 0.2f;

        properties.add(Property.DEMONIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(26, 36);
    }

    @Override
    public int attackSkill(Char target) {
        return 36;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 16);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        Ballistica attack = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
        return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (Random.Int(2) == 0) {
            Buff.prolong(enemy, Cripple.class, Cripple.DURATION);
        }

        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        if (state == HUNTING) {
            return enemySeen && getFurther(target);
        } else {
            return super.getCloser(target);
        }
    }

    @Override
    protected Item createLoot() {
        //5/count+5 total chance of getting healing, failing the 2nd roll drops mystery meat instead.
        if (Random.Int(5 + Dungeon.limitedDrops.scorpioHP.count) <= 4) {
            Dungeon.limitedDrops.scorpioHP.count++;
            return (Item) loot;
        } else {
            return new MysteryMeat();
        }
    }

    private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();

    static {
        RESISTANCES.add(Vampiric.class);
        RESISTANCES.add(Poison.class);
    }

    @Override
    public HashSet<Class<?>> resistances() {
        return RESISTANCES;
    }
}
