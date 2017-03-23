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
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Domination;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.effects.Pushing;
import com.wafitz.pixelspacebase.items.ExperimentalTech.HealingTech;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.levels.features.Door;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.SquiddardSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

class Squiddard extends Mob {

    {
        spriteClass = SquiddardSprite.class;

        HP = HT = 50;
        defenseSkill = 5;

        EXP = 3;
        maxLvl = 9;

        flying = true;

        loot = new HealingTech();
        lootChance = 0.1667f; //by default, see die()
    }

    private static final float SPLIT_DELAY = 1f;

    private int generation = 0;

    private static final String GENERATION = "generation";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(GENERATION, generation);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        generation = bundle.getInt(GENERATION);
        if (generation > 0) EXP = 0;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {

        if (HP >= damage + 2) {
            ArrayList<Integer> candidates = new ArrayList<>();
            boolean[] passable = Level.passable;

            int[] neighbours = {pos + 1, pos - 1, pos + Dungeon.level.width(), pos - Dungeon.level.width()};
            for (int n : neighbours) {
                if (passable[n] && Actor.findChar(n) == null) {
                    candidates.add(n);
                }
            }

            if (candidates.size() > 0) {

                Squiddard clone = split();
                clone.HP = (HP - damage) / 2;
                clone.pos = Random.element(candidates);
                clone.state = clone.HUNTING;

                if (Dungeon.level.map[clone.pos] == Terrain.DOOR) {
                    Door.enter(clone.pos);
                }

                GameScene.add(clone, SPLIT_DELAY);
                Actor.addDelayed(new Pushing(clone, pos, clone.pos), -1);

                HP -= clone.HP;
            }
        }

        return super.defenseProc(enemy, damage);
    }

    @Override
    public int attackSkill(Char target) {
        return 10;
    }

    private Squiddard split() {
        Squiddard clone = new Squiddard();
        clone.generation = generation + 1;
        clone.EXP = 0;
        if (buff(Burning.class) != null) {
            Buff.affect(clone, Burning.class).reignite(clone);
        }
        if (buff(Poison.class) != null) {
            Buff.affect(clone, Poison.class).set(2);
        }
        if (buff(Domination.class) != null) {
            Buff.affect(clone, Domination.class);
        }
        return clone;
    }

    @Override
    public void die(Object cause) {
        //sets drop chance
        lootChance = 1f / ((6 + 2 * Dungeon.limitedDrops.squiddardHP.count) * (generation + 1));
        super.die(cause);
    }

    @Override
    protected Item createLoot() {
        Dungeon.limitedDrops.squiddardHP.count++;
        return super.createLoot();
    }
}
