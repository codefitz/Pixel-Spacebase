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
import com.wafitz.pixelspacebase.actors.blobs.ToxicGas;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.actors.hero.HeroSubClass;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.TomeOfMastery;
import com.wafitz.pixelspacebase.items.artifacts.LloydsBeacon;
import com.wafitz.pixelspacebase.items.scripts.ScriptOfMagicMapping;
import com.wafitz.pixelspacebase.items.scripts.ScriptOfPsionicBlast;
import com.wafitz.pixelspacebase.items.weapon.enchantments.Grim;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.PrisonBossLevel;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.levels.vents.SpearVent;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.TenguSprite;
import com.wafitz.pixelspacebase.ui.BossHealthBar;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.HashSet;

public class Tengu extends Mob {

    {
        spriteClass = TenguSprite.class;

        HP = HT = 120;
        EXP = 20;
        defenseSkill = 20;

        HUNTING = new Hunting();

        flying = true; //doesn't literally fly, but he is fleet-of-foot enough to avoid hazards

        properties.add(Property.BOSS);
    }

    @Override
    protected void onAdd() {
        //when he's removed and re-added to the fight, his time is always set to now.
        spend(-cooldown());
        super.onAdd();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(6, 20);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 5);
    }

    @Override
    public void damage(int dmg, Object src) {

        int beforeHitHP = HP;
        super.damage(dmg, src);
        dmg = beforeHitHP - HP;

        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null) {
            int multiple = beforeHitHP > HT / 2 ? 1 : 4;
            lock.addTime(dmg * multiple);
        }

        //phase 2 of the fight is over
        if (HP == 0 && beforeHitHP <= HT / 2) {
            ((PrisonBossLevel) Dungeon.level).progress();
            return;
        }

        int hpBracket = beforeHitHP > HT / 2 ? 12 : 20;

        //phase 1 of the fight is over
        if (beforeHitHP > HT / 2 && HP <= HT / 2) {
            HP = (HT / 2) - 1;
            yell(Messages.get(this, "interesting"));
            ((PrisonBossLevel) Dungeon.level).progress();
            BossHealthBar.bleed(true);

            //if tengu has lost a certain amount of hp, jump
        } else if (beforeHitHP / hpBracket != HP / hpBracket) {
            jump();
        }
    }

    @Override
    public boolean isAlive() {
        return Dungeon.level.mobs.contains(this); //Tengu has special death rules, see prisonbosslevel.progress()
    }

    @Override
    public void die(Object cause) {

        if (Dungeon.hero.subClass == HeroSubClass.NONE) {
            Dungeon.level.drop(new TomeOfMastery(), pos).sprite.drop();
        }

        GameScene.bossSlain();
        super.die(cause);

        Badges.validateBossSlain();

        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }

        yell(Messages.get(this, "defeated"));
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return new Ballistica(pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
    }

    //tengu's attack is always visible
    @Override
    protected boolean doAttack(Char enemy) {
        if (enemy == Dungeon.hero)
            Dungeon.hero.resting = false;
        sprite.attack(enemy.pos);
        spend(attackDelay());
        return true;
    }

    private void jump() {

        for (int i = 0; i < 4; i++) {
            int ventPos;
            do {
                ventPos = Random.Int(Dungeon.level.length());
            } while (!Level.fieldOfView[ventPos] || Level.solid[ventPos]);

            if (Dungeon.level.map[ventPos] == Terrain.INACTIVE_VENT) {
                Dungeon.level.setVent(new SpearVent().reveal(), ventPos);
                Level.set(ventPos, Terrain.VENT);
                ScriptOfMagicMapping.discover(ventPos);
            }
        }

        if (enemy == null) enemy = chooseEnemy();

        int newPos;
        //if we're in phase 1, want to warp around within the room
        if (HP > HT / 2) {
            do {
                newPos = Random.Int(Dungeon.level.length());
            } while (
                    !(Dungeon.level.map[newPos] == Terrain.INACTIVE_VENT || Dungeon.level.map[newPos] == Terrain.VENT) ||
                            Level.solid[newPos] ||
                            Dungeon.level.adjacent(newPos, enemy.pos) ||
                            Actor.findChar(newPos) != null);

            //otherwise go wherever, as long as it's a little bit away
        } else {
            do {
                newPos = Random.Int(Dungeon.level.length());
            } while (
                    Level.solid[newPos] ||
                            Dungeon.level.distance(newPos, enemy.pos) < 8 ||
                            Actor.findChar(newPos) != null);
        }

        if (Dungeon.visible[pos]) CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6);


        sprite.move(pos, newPos);
        move(newPos);

        if (Dungeon.visible[newPos]) CellEmitter.get(newPos).burst(Speck.factory(Speck.WOOL), 6);
        Sample.INSTANCE.play(Assets.SND_PUFF);

        spend(1 / speed());
    }

    @Override
    public void notice() {
        super.notice();
        BossHealthBar.assignBoss(this);
        if (HP <= HT / 2) BossHealthBar.bleed(true);
        if (HP == HT) {
            yell(Messages.get(this, "notice_mine", Dungeon.hero.givenName()));
        } else {
            yell(Messages.get(this, "notice_face", Dungeon.hero.givenName()));
        }
    }

    private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();

    static {
        RESISTANCES.add(ToxicGas.class);
        RESISTANCES.add(Poison.class);
        RESISTANCES.add(Grim.class);
        RESISTANCES.add(ScriptOfPsionicBlast.class);
    }

    @Override
    public HashSet<Class<?>> resistances() {
        return RESISTANCES;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        BossHealthBar.assignBoss(this);
        if (HP <= HT / 2) BossHealthBar.bleed(true);
    }

    //tengu is always hunting
    private class Hunting extends Mob.Hunting {

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            enemySeen = enemyInFOV;
            if (enemyInFOV && !isCharmedBy(enemy) && canAttack(enemy)) {

                return doAttack(enemy);

            } else {

                if (enemyInFOV) {
                    target = enemy.pos;
                } else {
                    chooseEnemy();
                    target = enemy.pos;
                }

                spend(TICK);
                return true;

            }
        }
    }
}
