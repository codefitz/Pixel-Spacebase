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

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.ToxicGas;
import com.wafitz.pixelspacebase.actors.blobs.VenomGas;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Frost;
import com.wafitz.pixelspacebase.actors.buffs.LockedDown;
import com.wafitz.pixelspacebase.actors.buffs.Paralysis;
import com.wafitz.pixelspacebase.items.food.MysteryMeat;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.sprites.WaterThingSprite;
import com.watabou.utils.Random;

import java.util.HashSet;

public class WaterThing extends Mob {

    {
        spriteClass = WaterThingSprite.class;

        baseSpeed = 2f;

        EXP = 0;
    }

    public WaterThing() {
        super();

        HP = HT = 10 + SpacebaseRun.depth * 5;
        defenseSkill = 10 + SpacebaseRun.depth * 2;
    }

    @Override
    protected boolean act() {
        if (!Level.water[pos]) {
            die(null);
            sprite.killAndErase();
            return true;
        } else {
            //this causes pirahna to move away when a door is closed on them.
            SpacebaseRun.level.updateFieldOfView(this, Level.fieldOfView);
            enemy = chooseEnemy();
            if (state == this.HUNTING &&
                    !(enemy != null && enemy.isAlive() && Level.fieldOfView[enemy.pos] && enemy.invisible <= 0)) {
                state = this.WANDERING;
                int oldPos = pos;
                int i = 0;
                do {
                    i++;
                    target = SpacebaseRun.level.randomDestination();
                    if (i == 100) return true;
                } while (!getCloser(target));
                moveSprite(oldPos, pos);
                return true;
            }

            return super.act();
        }
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(SpacebaseRun.depth, 4 + SpacebaseRun.depth * 2);
    }

    @Override
    public int attackSkill(Char target) {
        return 20 + SpacebaseRun.depth * 2;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, SpacebaseRun.depth);
    }

    @Override
    public void die(Object cause) {
        SpacebaseRun.level.drop(new MysteryMeat(), pos).sprite.drop();
        super.die(cause);

        Statistics.waterThings++;
        Badges.validateWaterThingshasKilled();
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    protected boolean getCloser(int target) {

        if (rooted) {
            return false;
        }

        int step = SpacebaseRun.findStep(this, pos, target,
                Level.water,
                Level.fieldOfView);
        if (step != -1) {
            move(step);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean getFurther(int target) {
        int step = SpacebaseRun.flee(this, pos, target,
                Level.water,
                Level.fieldOfView);
        if (step != -1) {
            move(step);
            return true;
        } else {
            return false;
        }
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Burning.class);
        IMMUNITIES.add(Paralysis.class);
        IMMUNITIES.add(ToxicGas.class);
        IMMUNITIES.add(VenomGas.class);
        IMMUNITIES.add(LockedDown.class);
        IMMUNITIES.add(Frost.class);
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }
}
