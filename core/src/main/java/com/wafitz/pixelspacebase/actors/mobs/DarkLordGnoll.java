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
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.Fire;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.weapon.missiles.CurareDart;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.DarkLordGnollSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class DarkLordGnoll extends Gnoll {

    {
        spriteClass = DarkLordGnollSprite.class;

        HP = HT = 20;
        defenseSkill = 5;

        EXP = 5;

        state = WANDERING;

        loot = Generator.random(CurareDart.class);
        lootChance = 1f;

        properties.add(Property.MINIBOSS);
    }

    private int combo = 0;

    @Override
    public int attackSkill(Char target) {
        return 16;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        Ballistica attack = new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE);
        return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        //The gnoll's attacks get more severe the more the player lets it hit them
        combo++;
        int effect = Random.Int(4) + combo;

        if (effect > 2) {

            if (effect >= 6 && enemy.buff(Burning.class) == null) {

                if (Level.flamable[enemy.pos])
                    GameScene.add(Blob.device(enemy.pos, 4, Fire.class));
                Buff.affect(enemy, Burning.class).reignite(enemy);

            } else
                Buff.affect(enemy, Poison.class).set((effect - 2) * Poison.durationFactor(enemy));

        }
        return damage;
    }

    @Override
    protected boolean getCloser(int target) {
        combo = 0; //if he's moving, he isn't attacking, reset combo.
        if (state == HUNTING) {
            return enemySeen && getFurther(target);
        } else {
            return super.getCloser(target);
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        Hologram.Quest.process();
    }

    private static final String COMBO = "combo";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COMBO, combo);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        combo = bundle.getInt(COMBO);
    }

}
