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
package com.wafitz.pixelspacebase.actors.mobs.npcs;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.StationCatSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class StationCat extends NPC {

    private boolean following;

    private static final String FOLLOWING = "following";

    {
        spriteClass = StationCatSprite.class;
        HP = HT = 4;
        defenseSkill = 2;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FOLLOWING, following);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        following = bundle.getBoolean(FOLLOWING);
        ally = following;
    }

    @Override
    protected boolean act() {
        throwItem();

        if (!following) {
            spend(TICK);
            return true;
        }

        int target = followTarget();
        int oldPos = pos;
        if (target != -1 && getCloser(target)) {
            spend(1 / speed());
            return moveSprite(oldPos, pos);
        }

        spend(TICK);
        return true;
    }

    private int followTarget() {
        if (Dungeon.level.distance(pos, Dungeon.hero.pos) <= 2) {
            return -1;
        }

        int best = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int offset : PathFinder.NEIGHBOURS8) {
            int cell = Dungeon.hero.pos + offset;
            if (cell == pos) {
                return -1;
            }
            if (Level.passable[cell] && Actor.findChar(cell) == null) {
                int distance = Dungeon.level.distance(pos, cell);
                if (distance < bestDistance) {
                    best = cell;
                    bestDistance = distance;
                }
            }
        }
        return best;
    }

    @Override
    public int attackSkill(Char target) {
        return 0;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    @Override
    public void die(Object src) {
        if (Dungeon.visible[pos]) {
            GLog.w(Messages.get(this, "dies"));
        }
        super.die(src);
    }

    @Override
    public boolean interact() {
        if (!following) {
            following = true;
            ally = true;
            yell(Messages.get(this, "petted"));
        } else {
            int oldPos = pos;
            move(Dungeon.hero.pos);
            Dungeon.hero.move(oldPos);

            moveSprite(oldPos, pos);
            Dungeon.hero.sprite.move(Dungeon.hero.pos, oldPos);

            Dungeon.hero.spend(1 / Dungeon.hero.speed());
            Dungeon.hero.busy();
        }

        return true;
    }
}
