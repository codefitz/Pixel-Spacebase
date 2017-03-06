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
import com.wafitz.pixelspacebase.effects.particles.SparkParticle;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.traps.LightningTrap;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.sprites.ShamanSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Shaman extends Mob implements Callback {

    private static final float TIME_TO_ZAP = 1f;

    {
        spriteClass = ShamanSprite.class;

        HP = HT = 18;
        defenseSkill = 8;

        EXP = 6;
        maxLvl = 14;

        loot = Generator.Category.SCROLL;
        lootChance = 0.33f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(2, 8);
    }

    @Override
    public int attackSkill(Char target) {
        return 11;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
    }

    @Override
    protected boolean doAttack(Char enemy) {

        if (Dungeon.level.distance(pos, enemy.pos) <= 1) {

            return super.doAttack(enemy);

        } else {

            boolean visible = Level.fieldOfView[pos] || Level.fieldOfView[enemy.pos];
            if (visible) {
                sprite.zap(enemy.pos);
            }

            spend(TIME_TO_ZAP);

            if (hit(this, enemy, true)) {
                int dmg = Random.NormalIntRange(3, 10);
                if (Level.water[enemy.pos] && !enemy.flying) {
                    dmg *= 1.5f;
                }
                enemy.damage(dmg, LightningTrap.LIGHTNING);

                enemy.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
                enemy.sprite.flash();

                if (enemy == Dungeon.hero) {

                    Camera.main.shake(2, 0.3f);

                    if (!enemy.isAlive()) {
                        Dungeon.fail(getClass());
                        GLog.n(Messages.get(this, "zap_kill"));
                    }
                }
            } else {
                enemy.sprite.showStatus(CharSprite.NEUTRAL, enemy.defenseVerb());
            }

            return !visible;
        }
    }

    @Override
    public void call() {
        next();
    }

    private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();

    static {
        RESISTANCES.add(LightningTrap.Electricity.class);
    }

    @Override
    public HashSet<Class<?>> resistances() {
        return RESISTANCES;
    }
}
