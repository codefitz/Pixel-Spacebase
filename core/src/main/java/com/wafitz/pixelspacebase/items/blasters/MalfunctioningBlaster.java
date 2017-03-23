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
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.ConfusionGas;
import com.wafitz.pixelspacebase.actors.blobs.Fire;
import com.wafitz.pixelspacebase.actors.blobs.ParalyticGas;
import com.wafitz.pixelspacebase.actors.blobs.Regrowth;
import com.wafitz.pixelspacebase.actors.blobs.ToxicGas;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.Frost;
import com.wafitz.pixelspacebase.actors.buffs.Recharging;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.ConfusedShapeshifter;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.npcs.YogSheep;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Flare;
import com.wafitz.pixelspacebase.effects.MagicMissile;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.SpellSprite;
import com.wafitz.pixelspacebase.effects.particles.ShadowParticle;
import com.wafitz.pixelspacebase.items.Bomb;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.artifacts.HoloPad;
import com.wafitz.pixelspacebase.items.artifacts.TimeFolder;
import com.wafitz.pixelspacebase.items.scripts.RechargingScript;
import com.wafitz.pixelspacebase.items.scripts.TeleportationScript;
import com.wafitz.pixelspacebase.items.weapon.missiles.MissileWeapon;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.levels.vents.LightningVent;
import com.wafitz.pixelspacebase.levels.vents.MalfunctioningVent;
import com.wafitz.pixelspacebase.levels.vents.SummoningVent;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.messages.Languages;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.mines.Mine;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.InterlevelScene;
import com.wafitz.pixelspacebase.ui.HealthIndicator;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.io.IOException;
import java.util.ArrayList;

//helper class to contain all the malfunctioning blaster zapping logic, so the main blaster class doesn't get huge.
class MalfunctioningBlaster {

    private static float COMMON_CHANCE = 0.6f;
    private static float UNCOMMON_CHANCE = 0.3f;
    private static float RARE_CHANCE = 0.09f;
    private static float VERY_RARE_CHANCE = 0.01f;

    static void malfunctioningZap(final Blaster blaster, final Hero user, final Ballistica bolt) {
        switch (Random.chances(new float[]{COMMON_CHANCE, UNCOMMON_CHANCE, RARE_CHANCE, VERY_RARE_CHANCE})) {
            case 0:
            default:
                commonEffect(blaster, user, bolt);
                break;
            case 1:
                uncommonEffect(blaster, user, bolt);
                break;
            case 2:
                rareEffect(blaster, user, bolt);
                break;
            case 3:
                veryRareEffect(blaster, user, bolt);
                break;
        }
    }

    private static void commonEffect(final Blaster blaster, final Hero user, final Ballistica bolt) {
        switch (Random.Int(4)) {

            //anti-entropy
            case 0:
                malfunctioningFX(user, bolt, new Callback() {
                    public void call() {
                        Char target = Actor.findChar(bolt.collisionPos);
                        switch (Random.Int(2)) {
                            case 0:
                                if (target != null)
                                    Buff.affect(target, Burning.class).reignite(target);
                                Buff.affect(user, Frost.class, Frost.duration(user) * Random.Float(3f, 5f));
                                break;
                            case 1:
                                Buff.affect(user, Burning.class).reignite(user);
                                if (target != null)
                                    Buff.affect(target, Frost.class, Frost.duration(target) * Random.Float(3f, 5f));
                                break;
                        }
                        blaster.blasterUsed();
                    }
                });
                break;

            //spawns some regrowth
            case 1:
                malfunctioningFX(user, bolt, new Callback() {
                    public void call() {
                        int c = Dungeon.level.map[bolt.collisionPos];
                        if (c == Terrain.EMPTY ||
                                c == Terrain.EMBERS ||
                                c == Terrain.EMPTY_DECO ||
                                c == Terrain.LIGHTEDVENT ||
                                c == Terrain.OFFVENT) {
                            GameScene.add(Blob.device(bolt.collisionPos, 30, Regrowth.class));
                        }
                        blaster.blasterUsed();
                    }
                });
                break;

            //random teleportation
            case 2:
                switch (Random.Int(2)) {
                    case 0:
                        TeleportationScript.teleportHero(user);
                        blaster.blasterUsed();
                        break;
                    case 1:
                        malfunctioningFX(user, bolt, new Callback() {
                            public void call() {
                                Char ch = Actor.findChar(bolt.collisionPos);
                                if (ch != null && !ch.properties().contains(Char.Property.IMMOVABLE)) {
                                    int count = 10;
                                    int pos;
                                    do {
                                        pos = Dungeon.level.randomRespawnCell();
                                        if (count-- <= 0) {
                                            break;
                                        }
                                    } while (pos == -1);
                                    if (pos == -1 || Dungeon.bossLevel()) {
                                        GLog.w(Messages.get(TeleportationScript.class, "no_tele"));
                                    } else {
                                        ch.pos = pos;
                                        ch.sprite.place(ch.pos);
                                        ch.sprite.visible = Dungeon.visible[pos];
                                    }
                                }
                                blaster.blasterUsed();
                            }
                        });
                        break;
                }
                break;

            //random gas at location
            case 3:
                malfunctioningFX(user, bolt, new Callback() {
                    public void call() {
                        switch (Random.Int(3)) {
                            case 0:
                                GameScene.add(Blob.device(bolt.collisionPos, 800, ConfusionGas.class));
                                break;
                            case 1:
                                GameScene.add(Blob.device(bolt.collisionPos, 500, ToxicGas.class));
                                break;
                            case 2:
                                GameScene.add(Blob.device(bolt.collisionPos, 200, ParalyticGas.class));
                                break;
                        }
                        blaster.blasterUsed();
                    }
                });
                break;
        }

    }

    private static void uncommonEffect(final Blaster blaster, final Hero user, final Ballistica bolt) {
        switch (Random.Int(4)) {

            //Random mines
            case 0:
                malfunctioningFX(user, bolt, new Callback() {
                    public void call() {
                        int pos = bolt.collisionPos;
                        //place the mines infront of an enemy so they walk into it.
                        if (Actor.findChar(pos) != null && bolt.dist > 1) {
                            pos = bolt.path.get(bolt.dist - 1);
                        }

                        if (pos == Terrain.EMPTY ||
                                pos == Terrain.EMBERS ||
                                pos == Terrain.EMPTY_DECO ||
                                pos == Terrain.LIGHTEDVENT ||
                                pos == Terrain.OFFVENT) {
                            Dungeon.level.mine((Mine.Device) Generator.random(Generator.Category.DEVICE), pos);
                        }
                        blaster.blasterUsed();
                    }
                });
                break;

            //Health transfer
            case 1:
                final Char target = Actor.findChar(bolt.collisionPos);
                if (target != null) {
                    malfunctioningFX(user, bolt, new Callback() {
                        public void call() {
                            int damage = user.lvl * 2;
                            switch (Random.Int(2)) {
                                case 0:
                                    user.HP = Math.min(user.HT, user.HP + damage);
                                    user.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
                                    target.damage(damage, blaster);
                                    target.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                                    break;
                                case 1:
                                    user.damage(damage, this);
                                    user.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                                    target.HP = Math.min(target.HT, target.HP + damage);
                                    target.sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
                                    Sample.INSTANCE.play(Assets.SND_CURSED);
                                    if (!user.isAlive()) {
                                        Dungeon.fail(blaster.getClass());
                                        GLog.n(Messages.get(MalfunctioningBlaster.class, "ondeath", blaster.name()));
                                    }
                                    break;
                            }
                            blaster.blasterUsed();
                        }
                    });
                } else {
                    GLog.i(Messages.get(MalfunctioningBlaster.class, "nothing"));
                    blaster.blasterUsed();
                }
                break;

            //Bomb explosion
            case 2:
                malfunctioningFX(user, bolt, new Callback() {
                    public void call() {
                        new Bomb().explode(bolt.collisionPos);
                        blaster.blasterUsed();
                    }
                });
                break;

            //shock and recharge
            case 3:
                new LightningVent().set(user.pos).activate();
                Buff.prolong(user, Recharging.class, 20f);
                RechargingScript.charge(user);
                SpellSprite.show(user, SpellSprite.CHARGE);
                blaster.blasterUsed();
                break;
        }

    }

    private static void rareEffect(final Blaster blaster, final Hero user, final Ballistica bolt) {
        switch (Random.Int(4)) {

            //sheep transformation
            case 0:
                malfunctioningFX(user, bolt, new Callback() {
                    public void call() {
                        Char ch = Actor.findChar(bolt.collisionPos);

                        if (ch != null && ch != user
                                && !ch.properties().contains(Char.Property.BOSS)
                                && !ch.properties().contains(Char.Property.MINIBOSS)) {
                            YogSheep yogSheep = new YogSheep();
                            yogSheep.lifespan = 10;
                            yogSheep.pos = ch.pos;
                            ch.destroy();
                            ch.sprite.killAndErase();
                            Dungeon.level.mobs.remove(ch);
                            HealthIndicator.instance.target(null);
                            GameScene.add(yogSheep);
                            CellEmitter.get(yogSheep.pos).burst(Speck.factory(Speck.WOOL), 4);
                        } else {
                            GLog.i(Messages.get(MalfunctioningBlaster.class, "nothing"));
                        }
                        blaster.blasterUsed();
                    }
                });
                break;

            //malfunctions!
            case 1:
                MalfunctioningVent.malfunction(user);
                blaster.blasterUsed();
                break;

            //inter-level teleportation
            case 2:
                if (Dungeon.depth > 1 && !Dungeon.bossLevel()) {

                    //each depth has 1 more weight than the previous depth.
                    float[] depths = new float[Dungeon.depth - 1];
                    for (int i = 1; i < Dungeon.depth; i++) depths[i - 1] = i;
                    int depth = 1 + Random.chances(depths);

                    Buff buff = Dungeon.hero.buff(TimeFolder.timeFreeze.class);
                    if (buff != null) buff.detach();

                    for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
                        if (mob instanceof HoloPad.HologramHero) mob.destroy();

                    InterlevelScene.mode = InterlevelScene.Mode.RETURN;
                    InterlevelScene.returnDepth = depth;
                    InterlevelScene.returnPos = -1;
                    Game.switchScene(InterlevelScene.class);

                } else {
                    TeleportationScript.teleportHero(user);
                    blaster.blasterUsed();
                }
                break;

            //summon monsters
            case 3:
                new SummoningVent().set(user.pos).activate();
                blaster.blasterUsed();
                break;
        }
    }

    private static void veryRareEffect(final Blaster blaster, final Hero user, final Ballistica bolt) {
        switch (Random.Int(4)) {

            //great forest fire!
            case 0:
                for (int i = 0; i < Dungeon.level.length(); i++) {
                    int c = Dungeon.level.map[i];
                    if (c == Terrain.EMPTY ||
                            c == Terrain.EMBERS ||
                            c == Terrain.EMPTY_DECO ||
                            c == Terrain.LIGHTEDVENT ||
                            c == Terrain.OFFVENT) {
                        GameScene.add(Blob.device(i, 15, Regrowth.class));
                    }
                }
                do {
                    GameScene.add(Blob.device(Dungeon.level.randomDestination(), 10, Fire.class));
                } while (Random.Int(5) != 0);
                new Flare(8, 32).color(0xFFFF66, true).show(user.sprite, 2f);
                Sample.INSTANCE.play(Assets.SND_TELEPORT);
                GLog.p(Messages.get(MalfunctioningBlaster.class, "lightedvent"));
                GLog.w(Messages.get(MalfunctioningBlaster.class, "fire"));
                blaster.blasterUsed();
                break;

            //superpowered mimic
            case 1:
                malfunctioningFX(user, bolt, new Callback() {
                    public void call() {
                        ConfusedShapeshifter confusedShapeshifter = ConfusedShapeshifter.spawnAt(bolt.collisionPos, new ArrayList<Item>());
                        confusedShapeshifter.adjustStats(Dungeon.depth + 10);
                        confusedShapeshifter.HP = confusedShapeshifter.HT;
                        Item reward;
                        do {
                            reward = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
                                    Generator.Category.MODULE, Generator.Category.BLASTER));
                        } while (reward.level() < 2 && !(reward instanceof MissileWeapon));
                        Sample.INSTANCE.play(Assets.SND_MIMIC, 1, 1, 0.5f);
                        confusedShapeshifter.items.clear();
                        confusedShapeshifter.items.add(reward);

                        blaster.blasterUsed();
                    }
                });
                break;

            //crashes the game, yes, really.
            case 2:
                try {
                    Dungeon.saveAll();
                    if (Messages.lang() != Languages.ENGLISH) {
                        //Don't bother doing this joke to none-english speakers, I doubt it would translate.
                        GLog.i(Messages.get(MalfunctioningBlaster.class, "nothing"));
                        blaster.blasterUsed();
                    } else {
                        GameScene.show(
                                new WndOptions("MALFUNCTIONING BLASTER ERROR", "this application will now self-destruct", "abort", "retry", "fail") {
                                    @Override
                                    public void hide() {
                                        throw new RuntimeException("critical blaster exception");
                                    }
                                }
                        );
                    }
                } catch (IOException e) {
                    PixelSpacebase.reportException(e);
                    //oookay maybe don't kill the game if the save failed.
                    GLog.i(Messages.get(MalfunctioningBlaster.class, "nothing"));
                    blaster.blasterUsed();
                }
                break;

            //random transmogrification
            case 3:
                blaster.blasterUsed();
                blaster.detach(user.belongings.backpack);
                Item result;
                do {
                    result = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR,
                            Generator.Category.MODULE, Generator.Category.ARTIFACT));
                } while (result.level() < 0 && !(result instanceof MissileWeapon));
                if (result.isUpgradable()) result.upgrade();
                result.malfunctioning = result.malfunctioningKnown = true;
                GLog.w(Messages.get(MalfunctioningBlaster.class, "transmogrify"));
                Dungeon.level.drop(result, user.pos).sprite.drop();
                blaster.blasterUsed();
                break;
        }
    }

    private static void malfunctioningFX(final Hero user, final Ballistica bolt, final Callback callback) {
        MagicMissile.rainbow(user.sprite.parent, bolt.sourcePos, bolt.collisionPos, callback);
        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

}
