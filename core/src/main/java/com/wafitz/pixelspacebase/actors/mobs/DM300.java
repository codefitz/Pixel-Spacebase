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

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.ToxicGas;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.buffs.Paralysis;
import com.wafitz.pixelspacebase.actors.buffs.Terror;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.particles.ElmoParticle;
import com.wafitz.pixelspacebase.items.artifacts.PortableMaker;
import com.wafitz.pixelspacebase.items.artifacts.StrongForcefield;
import com.wafitz.pixelspacebase.items.keys.SkeletonKey;
import com.wafitz.pixelspacebase.items.scripts.PsionicBlastScript;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Grim;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.DM300Sprite;
import com.wafitz.pixelspacebase.ui.BossHealthBar;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class DM300 extends Mob {

    {
        spriteClass = DM300Sprite.class;

        HP = HT = 200;
        EXP = 30;
        defenseSkill = 18;

        loot = new StrongForcefield().identify();
        lootChance = 0.333f;

        properties.add(Property.BOSS);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(20, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 28;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }

    @Override
    public boolean act() {

        GameScene.add(Blob.device(pos, 30, ToxicGas.class));

        return super.act();
    }

    @Override
    public void move(int step) {
        super.move(step);

        if (Dungeon.level.map[step] == Terrain.INACTIVE_VENT && HP < HT) {

            HP += Random.Int(1, HT - HP);
            sprite.emitter().burst(ElmoParticle.FACTORY, 5);

            if (Dungeon.visible[step] && Dungeon.hero.isAlive()) {
                GLog.n(Messages.get(this, "repair"));
            }
        }

        int[] cells = {
                step - 1, step + 1, step - Dungeon.level.width(), step + Dungeon.level.width(),
                step - 1 - Dungeon.level.width(),
                step - 1 + Dungeon.level.width(),
                step + 1 - Dungeon.level.width(),
                step + 1 + Dungeon.level.width()
        };
        int cell = cells[Random.Int(cells.length)];

        if (Dungeon.visible[cell]) {
            CellEmitter.get(cell).start(Speck.factory(Speck.ROCK), 0.07f, 10);
            Camera.main.shake(3, 0.7f);
            Sample.INSTANCE.play(Assets.SND_ROCKS);

            if (Level.water[cell]) {
                GameScene.ripple(cell);
            } else if (Dungeon.level.map[cell] == Terrain.EMPTY) {
                Level.set(cell, Terrain.EMPTY_DECO);
                GameScene.updateMap(cell);
            }
        }

        Char ch = Actor.findChar(cell);
        if (ch != null && ch != this) {
            Buff.prolong(ch, Paralysis.class, 2);
        }
    }

    @Override
    public void damage(int dmg, Object src) {
        super.damage(dmg, src);
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null && !immunities().contains(src.getClass())) lock.addTime(dmg * 1.5f);
    }

    @Override
    public void die(Object cause) {

        super.die(cause);

        GameScene.bossSlain();
        Dungeon.level.drop(new SkeletonKey(Dungeon.depth), pos).sprite.drop();

        Badges.validateBossSlain();

        PortableMaker beacon = Dungeon.hero.belongings.getItem(PortableMaker.class);
        if (beacon != null) {
            beacon.upgrade();
        }

        yell(Messages.get(this, "defeated"));
    }

    @Override
    public void notice() {
        super.notice();
        BossHealthBar.assignBoss(this);
        yell(Messages.get(this, "notice"));
    }

    private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();

    static {
        RESISTANCES.add(Grim.class);
        RESISTANCES.add(PsionicBlastScript.class);
    }

    @Override
    public HashSet<Class<?>> resistances() {
        return RESISTANCES;
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(ToxicGas.class);
        IMMUNITIES.add(Terror.class);
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        BossHealthBar.assignBoss(this);
    }
}
