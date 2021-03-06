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
import com.wafitz.pixelspacebase.actors.buffs.Weakness;
import com.wafitz.pixelspacebase.items.ExperimentalTech.HealingTech;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Grim;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.sprites.WarlockSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Warlock extends Mob implements Callback {

    private static final float TIME_TO_ZAP = 1f;

    {
        spriteClass = WarlockSprite.class;

        HP = HT = 70;
        defenseSkill = 18;

        EXP = 11;
        maxLvl = 21;

        loot = Generator.Category.EXPERIMENTALTECH;
        lootChance = 0.83f;

        properties.add(Property.UNDEAD);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(16, 22);
    }

    @Override
    public int attackSkill(Char target) {
        return 25;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 8);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    protected boolean doAttack(Char enemy) {

        if (Dungeon.level.adjacent(pos, enemy.pos)) {

            return super.doAttack(enemy);

        } else {

            boolean visible = Level.fieldOfView[pos] || Level.fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap(enemy.pos);
            } else {
                zap();
            }

            return !visible;
        }
    }

    private void zap() {
        spend(TIME_TO_ZAP);

        if (hit(this, enemy, true)) {
            if (enemy == Dungeon.hero && Random.Int(2) == 0) {
                Buff.prolong(enemy, Weakness.class, Weakness.duration(enemy));
            }

            int dmg = Random.Int(12, 18);
            enemy.damage(dmg, this);

            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Dungeon.fail(getClass());
                GLog.n(Messages.get(this, "bolt_kill"));
            }
        } else {
            enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
        }
    }

    public void onZapComplete() {
        zap();
        next();
    }

    @Override
    public void call() {
        next();
    }

    @Override
    public Item createLoot() {
        Item loot = super.createLoot();

        if (loot instanceof HealingTech) {

            //count/10 chance of not dropping potion
            if (Random.Int(10) - Dungeon.limitedDrops.warlockHP.count < 0) {
                return null;
            } else
                Dungeon.limitedDrops.warlockHP.count++;

        }

        return loot;
    }

    private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();

    static {
        RESISTANCES.add(Grim.class);
    }

    @Override
    public HashSet<Class<?>> resistances() {
        return RESISTANCES;
    }
}
