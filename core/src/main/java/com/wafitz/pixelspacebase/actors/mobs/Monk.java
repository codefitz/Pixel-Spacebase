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
import com.wafitz.pixelspacebase.actors.buffs.Paranoid;
import com.wafitz.pixelspacebase.actors.buffs.Terror;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Arp;
import com.wafitz.pixelspacebase.items.KindOfWeapon;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.items.weapon.melee.Knuckles;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.MonkSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Monk extends Mob {

    {
        spriteClass = MonkSprite.class;

        HP = HT = 70;
        defenseSkill = 30;

        EXP = 11;
        maxLvl = 21;

        loot = new Food();
        lootChance = 0.083f;

        properties.add(Property.UNDEAD);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(12, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 30;
    }

    @Override
    protected float attackDelay() {
        return 0.5f;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }

    @Override
    public void die(Object cause) {
        Arp.Quest.process(this);

        super.die(cause);
    }

    private int hitsToDisarm = 0;

    @Override
    public int attackProc(Char enemy, int damage) {

        if (enemy == Dungeon.hero) {

            Hero hero = Dungeon.hero;
            KindOfWeapon weapon = hero.belongings.weapon;

            if (weapon != null && !(weapon instanceof Knuckles) && !weapon.malfunctioning) {
                if (hitsToDisarm == 0) hitsToDisarm = Random.NormalIntRange(4, 8);

                if (--hitsToDisarm == 0) {
                    hero.belongings.weapon = null;
                    Dungeon.quickslot.clearItem(weapon);
                    weapon.updateQuickslot();
                    Dungeon.level.drop(weapon, hero.pos).sprite.drop();
                    GLog.w(Messages.get(this, "disarm", weapon.name()));
                }
            }
        }

        return damage;
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Paranoid.class);
        IMMUNITIES.add(Terror.class);
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }

    private static String DISARMHITS = "hitsToDisarm";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(DISARMHITS, hitsToDisarm);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        hitsToDisarm = bundle.getInt(DISARMHITS);
    }
}
