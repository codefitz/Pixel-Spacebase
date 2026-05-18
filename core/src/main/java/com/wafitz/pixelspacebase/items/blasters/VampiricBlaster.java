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
package com.wafitz.pixelspacebase.items.blasters;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.SpacebaseTilemap;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.Beam;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.particles.BloodParticle;
import com.wafitz.pixelspacebase.effects.particles.LeafParticle;
import com.wafitz.pixelspacebase.effects.particles.ShadowParticle;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Launcher;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.Mine;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class VampiricBlaster extends Blaster {

    {
        image = ItemSpriteSheet.TRANSFUSIONBLASTER;

        collisionProperties = Ballistica.PROJECTILE;
    }

    private boolean freeCharge = false;

    @Override
    protected void onZap(Ballistica beam) {

        for (int c : beam.subPath(0, beam.dist))
            CellEmitter.center(c).burst(BloodParticle.BURST, 1);

        int cell = beam.collisionPos;

        Char ch = Actor.findChar(cell);
        Heap heap = SpacebaseRun.level.heaps.get(cell);

        boolean damageHero = true;

        //if we find a character..
        if (ch != null && ch instanceof Mob) {

            processSoulMark(ch, chargesPerCast());

            //heals an ally by transferring some of the user's life energy.
            if (((Mob) ch).ally) {

                int missingHP = ch.HT - ch.HP;
                //heals 30%+3%*lvl missing HP.
                int healing = (int) Math.ceil((missingHP * (0.30f + (0.03f * level()))));
                ch.HP += healing;
                ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1 + level() / 2);
                ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing);

                //disrupts undead, but cannot draw usable life force from them.
            } else if (ch.properties().contains(Char.Property.UNDEAD)) {

                //deals 30%+5%*lvl total HP.
                int damage = (int) Math.ceil(ch.HT * (0.3f + (0.05f * level())));
                ch.damage(damage, this);
                ch.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + level());
                Sample.INSTANCE.play(Assets.SND_BURNING);

                damageHero = false;

                //steals life force from hostile living targets
            } else {

                int beforeHP = ch.HP;
                int damage = (int) Math.ceil(ch.HT * (0.20f + (0.04f * level())));
                ch.damage(damage, this);
                ch.sprite.emitter().burst(BloodParticle.BURST, 10 + level() * 2);
                ch.sprite.flash();

                int drained = Math.max(0, beforeHP - ch.HP);
                int healing = Math.min(curUser.HT - curUser.HP, Math.max(1, Math.round(drained * (0.60f + (0.05f * level())))));
                if (healing > 0) {
                    curUser.HP += healing;
                    curUser.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1 + level() / 2);
                    curUser.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing);
                }

                damageHero = false;

            }


            //if we find an item...
        } else if (heap != null && heap.type == Heap.Type.HEAP) {
            Item item = heap.peek();

            //30% + 10%*lvl chance to fix the item and reset it to base level if degraded.
            if (item != null && Random.Float() <= 0.3f + level() * 0.1f) {
                if (item.malfunctioning) {
                    item.malfunctioning = false;
                    CellEmitter.get(cell).start(ShadowParticle.UP, 0.05f, 10);
                    Sample.INSTANCE.play(Assets.SND_BURNING);
                }

                int lvldiffFromBase = item.level() - (item instanceof Module ? 1 : 0);
                if (lvldiffFromBase < 0) {
                    item.upgrade(-lvldiffFromBase);
                    CellEmitter.get(cell).start(Speck.factory(Speck.UP), 0.2f, 3);
                    Sample.INSTANCE.play(Assets.SND_BURNING);
                }
            }

            //if we find some trampled lightedvent...
        } else if (SpacebaseRun.level.map[cell] == Terrain.LIGHTEDVENT) {

            //regrow one lightedvent tile, suuuuuper useful...
            Level.set(cell, Terrain.OFFVENT);
            GameScene.updateMap(cell);
            CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4);

            //If we find embers...
        } else if (SpacebaseRun.level.map[cell] == Terrain.EMBERS) {

            //30% + 3%*lvl chance to grow a random mines, or just regrow lightedvent.
            if (Random.Float() <= 0.3f + level() * 0.03f) {
                SpacebaseRun.level.mine((Mine.Device) Generator.random(Generator.Category.DEVICE), cell);
                CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 8);
                GameScene.updateMap(cell);
            } else {
                Level.set(cell, Terrain.OFFVENT);
                GameScene.updateMap(cell);
                CellEmitter.get(cell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
            }

        } else
            return; //don't damage the hero if we can't find a target;

        if (!freeCharge && damageHero) {
            damageHero();
        } else {
            freeCharge = false;
        }
    }

    //this blaster costs health too
    private void damageHero() {
        // 15% of max hp
        int damage = (int) Math.ceil(curUser.HT * 0.15f);
        curUser.damage(damage, this);

        if (!curUser.isAlive()) {
            SpacebaseRun.fail(getClass());
            GLog.n(Messages.get(this, "ondeath"));
        }
    }

    @Override
    protected int initialCharges() {
        return 1;
    }

    @Override
    public void onHit(DM3000Launcher launcher, Char attacker, Char defender, int damage) {
        // lvl 0 - 10%
        // lvl 1 - 18%
        // lvl 2 - 25%
        if (Random.Int(level() + 10) >= 9) {
            //grants a free use of the launcher
            freeCharge = true;
            GLog.p(Messages.get(this, "charged"));
            attacker.sprite.emitter().burst(BloodParticle.BURST, 20);
        }
    }

    @Override
    protected void fx(Ballistica beam, Callback callback) {
        curUser.sprite.parent.add(
                new Beam.HealthRay(curUser.sprite.center(), SpacebaseTilemap.tileCenterToWorld(beam.collisionPos)));
        callback.call();
    }

    @Override
    public void launcherFx(DM3000Launcher.launcherParticle particle) {
        particle.color(0xCC0000);
        particle.am = 0.6f;
        particle.setLifespan(1f);
        particle.speed.polar(Random.Float(PointF.PI2), 2f);
        particle.setSize(1f, 2f);
        particle.radiateXY(0.5f);
    }

    private static final String FREECHARGE = "freecharge";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        freeCharge = bundle.getBoolean(FREECHARGE);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FREECHARGE, freeCharge);
    }

}
