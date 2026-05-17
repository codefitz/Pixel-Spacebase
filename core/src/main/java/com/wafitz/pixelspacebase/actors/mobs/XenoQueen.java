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
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.keys.SkeletonKey;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.AlienEgg;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.sprites.QueenXenoSprite;
import com.wafitz.pixelspacebase.ui.BossHealthBar;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class XenoQueen extends Mob {

    private static final int EGG_COUNT = 32;
    private static final int CRACK_COUNT = 16;
    private static final int MAX_XENOS = 10;
    private static final float SPAWN_DELAY = 3f;

    private boolean arenaPrepared;
    private float spawnCooldown;

    {
        spriteClass = QueenXenoSprite.class;

        HP = HT = 130;
        EXP = 14;
        defenseSkill = 12;

        properties.add(Property.BOSS);
        properties.add(Property.DEMONIC);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(6, 16);
    }

    @Override
    public int attackSkill(Char target) {
        return 18;
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (enemy == Dungeon.hero && Dungeon.hero.heroClass == HeroClass.SHAPESHIFTER) {
            return 2;
        }
        return super.defenseSkill(enemy);
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(1, 5);
    }

    @Override
    public boolean act() {
        if (state == FLEEING) {
            state = HUNTING;
        }

        if (!arenaPrepared) {
            prepareArena();
        }

        spawnCooldown -= TICK;
        if (spawnCooldown <= 0 && countXenos() < MAX_XENOS) {
            spawnXenoFromCrack();
            spawnCooldown = SPAWN_DELAY;
        }

        return super.act();
    }

    @Override
    public void notice() {
        super.notice();
        state = HUNTING;
        target = Dungeon.hero.pos;
        Dungeon.level.seal();
        BossHealthBar.assignBoss(this);
        yell(Messages.get(this, "notice"));
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(dmg, src);

        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) {
            lock.addTime(dmg * 2);
        }
    }

    @Override
    public void die(Object cause) {
        yell(Messages.get(this, "defeated"));
        Dungeon.level.unseal();
        GameScene.bossSlain();
        Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();
        Badges.validateBossSlain();
        super.die(cause);
    }

    private void prepareArena() {
        arenaPrepared = true;
        spawnCooldown = SPAWN_DELAY;

        for (int i = 0; i < CRACK_COUNT; i++) {
            int cell = randomCrackWallCell();
            if (cell == -1) {
                break;
            }

            Dungeon.level.map[cell] = Terrain.WALL_DECO;
            GameScene.updateMap(cell);
        }

        for (int i = 0; i < EGG_COUNT; i++) {
            int cell = randomEggCell();
            if (cell == -1) {
                break;
            }

            AlienEgg egg = new AlienEgg();
            egg.pos = cell;
            Dungeon.level.mines.put(cell, egg);
            GameScene.updateMap(cell);
        }
    }

    private int randomEggCell() {
        for (int tries = 0; tries < 80; tries++) {
            int cell = Random.Int(Dungeon.level.length());
            if (canPlaceEgg(cell)) {
                return cell;
            }
        }
        return -1;
    }

    private int randomCrackWallCell() {
        ArrayList<Integer> cells = new ArrayList<>();
        for (int i = Dungeon.level.width(); i < Dungeon.level.length() - Dungeon.level.width(); i++) {
            if (canMarkCrack(i)) {
                cells.add(i);
            }
        }

        return cells.isEmpty() ? -1 : Random.element(cells);
    }

    private boolean canMarkCrack(int cell) {
        if (Dungeon.level.map[cell] != Terrain.WALL
                || Dungeon.level.distance(cell, pos) > 14
                || Dungeon.level.distance(cell, Dungeon.hero.pos) < 4) {
            return false;
        }

        for (int offset : PathFinder.NEIGHBOURS4) {
            if (cell + offset >= 0
                    && cell + offset < Dungeon.level.length()
                    && (Level.passable[cell + offset] || Level.avoid[cell + offset])) {
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceEgg(int cell) {
        return cell >= 0
                && cell < Dungeon.level.length()
                && (Level.passable[cell] || Level.avoid[cell])
                && Dungeon.level.distance(cell, pos) <= 10
                && Dungeon.level.distance(cell, Dungeon.hero.pos) > 2
                && Actor.findChar(cell) == null
                && Dungeon.level.heaps.get(cell) == null
                && Dungeon.level.mines.get(cell) == null;
    }

    private int countXenos() {
        int count = 0;
        for (Mob mob : Dungeon.level.mobs) {
            if (mob instanceof Xenomorph) {
                count++;
            }
        }
        return count;
    }

    private void spawnXenoFromCrack() {
        ArrayList<Integer> spawnPoints = new ArrayList<>();
        for (int i = 0; i < Dungeon.level.length(); i++) {
            if (canSpawnFromCrack(i)) {
                spawnPoints.add(i);
            }
        }

        if (spawnPoints.isEmpty()) {
            return;
        }

        int cell = Random.element(spawnPoints);
        Xenomorph xenomorph = new Xenomorph();
        xenomorph.pos = cell;
        xenomorph.state = xenomorph.HUNTING;
        GameScene.add(xenomorph);

        if (Dungeon.visible[cell]) {
            CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 8);
            xenomorph.sprite.showStatus(CharSprite.NEGATIVE, Messages.get(this, "spawn"));
        }
    }

    private boolean canSpawnFromCrack(int cell) {
        if (cell <= Dungeon.level.width()
                || cell >= Dungeon.level.length() - Dungeon.level.width()
                || !(Level.passable[cell] || Level.avoid[cell])
                || Actor.findChar(cell) != null
                || Dungeon.level.distance(cell, Dungeon.hero.pos) < 4) {
            return false;
        }

        for (int offset : PathFinder.NEIGHBOURS4) {
            if (Dungeon.level.map[cell + offset] == Terrain.WALL_DECO) {
                return true;
            }
        }
        return false;
    }

    private static final String ARENA_PREPARED = "arenaPrepared";
    private static final String SPAWN_COOLDOWN = "spawnCooldown";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ARENA_PREPARED, arenaPrepared);
        bundle.put(SPAWN_COOLDOWN, spawnCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        arenaPrepared = bundle.getBoolean(ARENA_PREPARED);
        spawnCooldown = bundle.getFloat(SPAWN_COOLDOWN);
        if (state != SLEEPING) {
            BossHealthBar.assignBoss(this);
        }
    }
}
