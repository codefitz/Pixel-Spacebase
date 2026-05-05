/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
import com.wafitz.pixelspacebase.actors.buffs.Paranoid;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.sprites.DroneSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Drone extends Mob {

    {
        spriteClass = DroneSprite.class;

        viewDistance = 4;

        EXP = 0;

        flying = true;
        state = WANDERING;
    }

    private int level;

    //-1 refers to a pot that has gone missing.
    private int potPos;
    //-1 for no owner
    private int potHolder;

    private static final String LEVEL = "level";
    private static final String POTPOS = "potpos";
    private static final String POTHOLDER = "potholder";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, level);
        bundle.put(POTPOS, potPos);
        bundle.put(POTHOLDER, potHolder);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        spawn(bundle.getInt(LEVEL));
        potPos = bundle.getInt(POTPOS);
        potHolder = bundle.getInt(POTHOLDER);
    }

    public void spawn(int level) {
        this.level = level;

        HT = (2 + level) * 4;
        defenseSkill = 9 + level;
    }

    public void setPotInfo(int potPos, Char potHolder) {
        this.potPos = potPos;
        if (potHolder == null)
            this.potHolder = -1;
        else
            this.potHolder = potHolder.id();
    }

    @Override
    public int attackSkill(Char target) {
        return defenseSkill;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(HT / 10, HT / 4);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (enemy instanceof Mob) {
            ((Mob) enemy).aggro(this);
        }
        return damage;
    }

    @Override
    protected Char chooseEnemy() {
        //if the controller is no longer present, clear mines or idle.
        if (potHolder == -1 && potPos == -1) {
            int mineTarget = findMineTarget(pos);
            if (mineTarget != -1) {
                target = mineTarget;
            }
            return null;
        }

            //if something is holding the pot, target that
        else if (Actor.findById(potHolder) != null) {
            Actor holder = Actor.findById(potHolder);
            return holder instanceof Mob && ((Mob) holder).hostile ? (Char) holder : null;
        }

            //if the pot is on the ground
        else {

            //if already targeting something, and that thing is still alive and near the pot, keeping targeting it.
            if (enemy != null && enemy.isAlive() && Dungeon.level.mobs.contains(enemy)
                    && Level.fieldOfView[enemy.pos] && enemy.invisible == 0
                    && Dungeon.level.distance(enemy.pos, potPos) <= 3)
                return enemy;

            //find all mobs near the pot
            HashSet<Char> enemies = new HashSet<>();
            for (Mob mob : Dungeon.level.mobs)
                if (!(mob instanceof Drone) && Dungeon.level.distance(mob.pos, potPos) <= 3 && mob.hostile)
                    enemies.add(mob);

            //pick one, if there are none, clear nearby mines or idle.
            if (enemies.size() > 0) return Random.element(enemies);
            int mineTarget = findMineTarget(potPos);
            if (mineTarget != -1) {
                target = mineTarget;
            }
            return null;
        }
    }

    @Override
    protected boolean getCloser(int target) {
        if (Dungeon.level.mines.get(target) != null) {
            return super.getCloser(target);
        }
        if (enemy != null && Actor.findById(potHolder) == enemy) {
            target = enemy.pos;
        } else if (potPos != -1 && (state == WANDERING || Dungeon.level.distance(target, potPos) > 3))
            this.target = target = potPos;
        return super.getCloser(target);
    }

    @Override
    public void move(int step) {
        super.move(step);
        if (Dungeon.level.mines.get(pos) != null) {
            Dungeon.level.mines.get(pos).wither();
        }
    }

    private int findMineTarget(int origin) {
        int best = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int key : Dungeon.level.mines.keyArray()) {
            int distance = Dungeon.level.distance(origin, key);
            if (distance < bestDistance) {
                best = key;
                bestDistance = distance;
            }
        }
        return best;
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Poison.class);
        IMMUNITIES.add(Paranoid.class);
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }
}
