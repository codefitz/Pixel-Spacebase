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
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.XenomorphSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Xenomorph extends Mob {

    {
        spriteClass = XenomorphSprite.class;

        HP = HT = 8;
        defenseSkill = 2;

        maxLvl = 5;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(1, 4);
    }

    @Override
    public int attackSkill(Char target) {
        return 8;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 1);
    }

    public static boolean spawnAdjacent(int pos) {
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

        Xenomorph xenomorph = new Xenomorph();
        xenomorph.pos = Random.element(spawnPoints);
        xenomorph.state = xenomorph.HUNTING;
        GameScene.add(xenomorph);
        return true;
    }
}
