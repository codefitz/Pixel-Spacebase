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

import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.StenchGas;
import com.wafitz.pixelspacebase.actors.buffs.Acid;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ToughXenoSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class ToughXeno extends Xenomorph {

    private boolean processQuest = true;

    {
        spriteClass = ToughXenoSprite.class;

        HP = HT = 20;
        defenseSkill = 5;

        EXP = 4;

        state = WANDERING;

        properties.add(Property.MINIBOSS);
        properties.add(Property.DEMONIC);
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 2);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (Random.Int(3) == 0) {
            Buff.affect(enemy, Acid.class);
        }

        return damage;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {

        GameScene.add(Blob.device(pos, 20, StenchGas.class));

        return super.defenseProc(enemy, damage);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);

        if (processQuest) {
            Hologram.Quest.process();
        }
    }

    public static boolean spawnAdjacent(int pos, boolean processQuest) {
        ArrayList<Integer> spawnPoints = new ArrayList<>();

        for (int offset : PathFinder.NEIGHBOURS8) {
            int cell = pos + offset;
            if ((Level.passable[cell] || Level.avoid[cell]) && Actor.findChar(cell) == null) {
                spawnPoints.add(cell);
            }
        }

        if (spawnPoints.isEmpty()) {
            return false;
        }

        ToughXeno xenomorph = new ToughXeno();
        xenomorph.processQuest = processQuest;
        xenomorph.pos = Random.element(spawnPoints);
        xenomorph.state = xenomorph.HUNTING;
        GameScene.add(xenomorph);
        return true;
    }

    private static final String PROCESS_QUEST = "process_quest";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PROCESS_QUEST, processQuest);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(PROCESS_QUEST)) {
            processQuest = bundle.getBoolean(PROCESS_QUEST);
        }
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(StenchGas.class);
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }
}
