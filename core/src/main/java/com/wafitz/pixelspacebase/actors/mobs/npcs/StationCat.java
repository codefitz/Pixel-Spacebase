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

import com.wafitz.pixelspacebase.SpacebaseRun;
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
    private static StationCat carriedCat;

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
        Quest.following = Quest.following || following;
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
        if (SpacebaseRun.level.distance(pos, SpacebaseRun.hero.pos) <= 2) {
            return -1;
        }

        int best = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int offset : PathFinder.NEIGHBOURS8) {
            int cell = SpacebaseRun.hero.pos + offset;
            if (cell == pos) {
                return -1;
            }
            if (Level.passable[cell] && Actor.findChar(cell) == null) {
                int distance = SpacebaseRun.level.distance(pos, cell);
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
        if (SpacebaseRun.visible[pos]) {
            GLog.w(Messages.get(this, "dies"));
        }
        Quest.dead = true;
        Quest.following = false;
        Quest.gone = false;
        carriedCat = null;
        super.die(src);
    }

    @Override
    public boolean interact() {
        if (!following) {
            following = true;
            ally = true;
            Quest.following = true;
            yell(Messages.get(this, "petted"));
        } else {
            int oldPos = pos;
            move(SpacebaseRun.hero.pos);
            SpacebaseRun.hero.move(oldPos);

            moveSprite(oldPos, pos);
            SpacebaseRun.hero.sprite.move(SpacebaseRun.hero.pos, oldPos);

            SpacebaseRun.hero.spend(1 / SpacebaseRun.hero.speed());
            SpacebaseRun.hero.busy();
        }

        return true;
    }

    private void follow() {
        following = true;
        ally = true;
        HP = Math.max(1, HP);
    }

    public static boolean isFollowing() {
        return Quest.following && !Quest.dead && !Quest.gone;
    }

    public static void carryFollowerFrom(Level level) {
        if (!isFollowing() || level == null || level.mobs == null) {
            return;
        }

        for (com.wafitz.pixelspacebase.actors.mobs.Mob mob : level.mobs.toArray(new com.wafitz.pixelspacebase.actors.mobs.Mob[0])) {
            if (mob instanceof StationCat && ((StationCat) mob).following) {
                carriedCat = (StationCat) mob;
                level.mobs.remove(mob);
                Actor.remove(mob);
                return;
            }
        }
    }

    public static void placeFollowerOn(Level level) {
        if (!isFollowing() || level == null || level.mobs == null || SpacebaseRun.hero == null) {
            carriedCat = null;
            return;
        }

        StationCat existing = null;
        for (com.wafitz.pixelspacebase.actors.mobs.Mob mob : level.mobs.toArray(new com.wafitz.pixelspacebase.actors.mobs.Mob[0])) {
            if (mob instanceof StationCat) {
                if (existing == null) {
                    existing = (StationCat) mob;
                }
                if (carriedCat == null) {
                    continue;
                }
                level.mobs.remove(mob);
                Actor.remove(mob);
            }
        }

        if (carriedCat == null && existing != null) {
            existing.follow();
            return;
        }

        StationCat cat = carriedCat != null ? carriedCat : new StationCat();
        carriedCat = null;
        cat.follow();
        cat.pos = followerCell(level);
        level.mobs.add(cat);
        Actor.add(cat);
    }

    public static void abandonFollower(Level level) {
        if (level != null && level.mobs != null) {
            for (com.wafitz.pixelspacebase.actors.mobs.Mob mob : level.mobs.toArray(new com.wafitz.pixelspacebase.actors.mobs.Mob[0])) {
                if (mob instanceof StationCat) {
                    level.mobs.remove(mob);
                    Actor.remove(mob);
                }
            }
        }
        carriedCat = null;
        Quest.following = false;
        Quest.gone = true;
    }

    public static boolean canSpawnOnFirstLevel() {
        return !Quest.following && !Quest.dead && !Quest.gone;
    }

    private static int followerCell(Level level) {
        for (int offset : PathFinder.NEIGHBOURS8) {
            int cell = SpacebaseRun.hero.pos + offset;
            if (level.insideMap(cell) && Level.passable[cell] && Actor.findChar(cell) == null) {
                return cell;
            }
        }

        int cell = level.randomRespawnCell();
        return cell != -1 ? cell : SpacebaseRun.hero.pos;
    }

    public static class Quest {

        private static boolean following;
        private static boolean dead;
        private static boolean gone;

        private static final String NODE = "stationCat";
        private static final String FOLLOWING = "following";
        private static final String DEAD = "dead";
        private static final String GONE = "gone";

        public static void reset() {
            following = false;
            dead = false;
            gone = false;
            carriedCat = null;
        }

        public static void storeInBundle(Bundle bundle) {
            Bundle node = new Bundle();
            node.put(FOLLOWING, following);
            node.put(DEAD, dead);
            node.put(GONE, gone);
            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {
            Bundle node = bundle.getBundle(NODE);
            if (!node.isNull()) {
                following = node.getBoolean(FOLLOWING);
                dead = node.getBoolean(DEAD);
                gone = node.getBoolean(GONE);
            } else {
                reset();
            }
        }
    }
}
