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
package com.wafitz.pixelspacebase.items.equippablemodules;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.ToxicGas;
import com.wafitz.pixelspacebase.actors.blobs.VenomGas;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Burning;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.Turret;
import com.wafitz.pixelspacebase.actors.mobs.npcs.NPC;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.particles.ShaftParticle;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.DM3000Power;
import com.wafitz.pixelspacebase.items.upgrades.PsionicBlastUpgrade;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Languages;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.HologramSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndQuest;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class HoloPad extends EquippableModule {

    {
        image = ItemSpriteSheet.HOLOPAD_1;

        levelCap = 10;

        charge = 100;
        chargeCap = 100;

        defaultAction = AC_SUMMON;
    }

    private static boolean talkedTo = false;
    private static boolean firstSummon = false;
    protected static boolean spawned = false;

    public int droppedHoloBatteries = 0;

    private static final String AC_SUMMON = "SUMMON";

    public HoloPad() {
        super();
        talkedTo = firstSummon = spawned = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && charge == chargeCap && !malfunctioning)
            actions.add(AC_SUMMON);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SUMMON)) {

            if (spawned) GLog.i(Messages.get(this, "spawned"));
            else if (!isEquipped(hero)) GLog.i(Messages.get(EquippableModule.class, "need_to_equip"));
            else if (charge != chargeCap) GLog.i(Messages.get(this, "no_charge"));
            else if (malfunctioning) GLog.i(Messages.get(this, "malfunctioning"));
            else {
                ArrayList<Integer> spawnPoints = new ArrayList<>();
                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && (Level.passable[p] || Level.avoid[p])) {
                        spawnPoints.add(p);
                    }
                }

                if (spawnPoints.size() > 0) {
                    HologramHero hologram = new HologramHero(level());
                    hologram.pos = Random.element(spawnPoints);

                    GameScene.add(hologram, 1f);
                    CellEmitter.get(hologram.pos).start(ShaftParticle.FACTORY, 0.3f, 4);
                    CellEmitter.get(hologram.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

                    hero.spend(1f);
                    hero.busy();
                    hero.sprite.operate(hero.pos);

                    if (!firstSummon) {
                        hologram.yell(Messages.get(HologramHero.class, "hello", SpacebaseRun.hero.givenName()));
                        Sample.INSTANCE.play(Assets.SND_HOLOGRAM);
                        firstSummon = true;
                    } else
                        hologram.saySpawned();

                    spawned = true;
                    charge = 0;
                    updateQuickslot();

                } else
                    GLog.i(Messages.get(this, "no_space"));
            }

        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(SpacebaseRun.hero)) {
            if (!malfunctioning) {

                if (level() < levelCap)
                    desc += "\n\n" + Messages.get(this, "desc_hint");

            } else
                desc += "\n\n" + Messages.get(this, "desc_malfunctioning");
        }

        return desc;
    }

    @Override
    protected ModuleBuff passiveBuff() {
        return new holopadRecharge();
    }

    @Override
    public Item upgrade() {
        if (level() >= 9)
            image = ItemSpriteSheet.HOLOPAD_3;
        else if (level() >= 4)
            image = ItemSpriteSheet.HOLOPAD_2;

        //For upgrade transferring via well of transmutation
        droppedHoloBatteries = Math.max(level(), droppedHoloBatteries);

        return super.upgrade();
    }

    private static final String TALKEDTO = "talkedto";
    private static final String FIRSTSUMMON = "firstsummon";
    private static final String SPAWNED = "spawned";
    private static final String HOLOBATTERIES = "holobatteries";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);

        bundle.put(TALKEDTO, talkedTo);
        bundle.put(FIRSTSUMMON, firstSummon);
        bundle.put(SPAWNED, spawned);
        bundle.put(HOLOBATTERIES, droppedHoloBatteries);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        talkedTo = bundle.getBoolean(TALKEDTO);
        firstSummon = bundle.getBoolean(FIRSTSUMMON);
        spawned = bundle.getBoolean(SPAWNED);
        droppedHoloBatteries = bundle.getInt(HOLOBATTERIES);
    }

    private class holopadRecharge extends ModuleBuff {

        @Override
        public boolean act() {

            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeCap && !malfunctioning && (lock == null || lock.regenOn())) {
                partialCharge += 1 / 5f; //500 turns to a full charge
                if (partialCharge > 1) {
                    charge++;
                    partialCharge--;
                    if (charge == chargeCap) {
                        partialCharge = 0f;
                        GLog.p(Messages.get(HoloPad.class, "charged"));
                    }
                }
            } else if (malfunctioning && Random.Int(100) == 0) {

                ArrayList<Integer> spawnPoints = new ArrayList<>();

                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = target.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar(p) == null && (Level.passable[p] || Level.avoid[p])) {
                        spawnPoints.add(p);
                    }
                }

                if (spawnPoints.size() > 0) {
                    Turret.spawnAt(Random.element(spawnPoints));
                    Sample.INSTANCE.play(Assets.SND_CURSED);
                }

            }

            updateQuickslot();

            spend(TICK);

            return true;
        }
    }

    public static class HoloBattery extends Item {

        {
            stackable = true;
            image = ItemSpriteSheet.HOLOBATTERY;
        }

        @Override
        public boolean doPickUp(Hero hero) {
            HoloPad holopad = hero.belongings.getItem(HoloPad.class);

            if (holopad == null) {
                if (hero.heroClass == HeroClass.DM3000) {
                    DM3000Power.consumeBattery(hero);
                    hero.spendAndNext(TIME_TO_PICK_UP);
                    return true;
                }
                GLog.w(Messages.get(this, "no_holopad"));
                return false;
            }
            if (holopad.level() >= holopad.levelCap) {
                if (hero.heroClass == HeroClass.DM3000) {
                    DM3000Power.consumeBattery(hero);
                    hero.spendAndNext(TIME_TO_PICK_UP);
                    return true;
                }
                GLog.i(Messages.get(this, "no_room"));
                hero.spendAndNext(TIME_TO_PICK_UP);
                return true;
            } else {

                holopad.upgrade();
                if (holopad.level() == holopad.levelCap) {
                    GLog.p(Messages.get(this, "maxlevel"));
                } else
                    GLog.i(Messages.get(this, "levelup"));

                Sample.INSTANCE.play(Assets.SND_MEDIGEL);
                hero.spendAndNext(TIME_TO_PICK_UP);
                return true;

            }
        }

    }

    public static class HologramHero extends NPC {

        {
            spriteClass = HologramSprite.class;

            flying = true;

            state = WANDERING;
            enemy = null;

            ally = true;
        }

        HologramHero() {
            super();

            //double heroes defence skill
            defenseSkill = (SpacebaseRun.hero.lvl + 4) * 2;
        }

        HologramHero(int holopadLevel) {
            this();
            HP = HT = 10 + holopadLevel * 4;
        }

        void saySpawned() {
            if (Messages.lang() != Languages.ENGLISH) return; //don't say anything if not on english
            int i = (SpacebaseRun.depth - 1) / 5;
            if (chooseEnemy() == null)
                yell(Random.element(VOICE_AMBIENT[i]));
            else
                yell(Random.element(VOICE_ENEMIES[i][SpacebaseRun.bossLevel() ? 1 : 0]));
            Sample.INSTANCE.play(Assets.SND_HOLOGRAM);
        }

        public void sayClone() {
            yell(Random.element(VOICE_BLESSEDCLONE));
            Sample.INSTANCE.play(Assets.SND_HOLOGRAM);
        }

        void sayDefeated() {
            if (Messages.lang() != Languages.ENGLISH) return; //don't say anything if not on english
            yell(Random.element(VOICE_DEFEATED[SpacebaseRun.bossLevel() ? 1 : 0]));
            Sample.INSTANCE.play(Assets.SND_HOLOGRAM);
        }

        void sayHeroKilled() {
            if (Messages.lang() != Languages.ENGLISH) return; //don't say anything if not on english
            yell(Random.element(VOICE_HEROKILLED));
            Sample.INSTANCE.play(Assets.SND_HOLOGRAM);
        }

        public void sayBossBeaten() {
            yell(Random.element(VOICE_BOSSBEATEN[SpacebaseRun.depth == 25 ? 1 : 0]));
            Sample.INSTANCE.play(Assets.SND_HOLOGRAM);
        }

        @Override
        protected boolean act() {
            if (Random.Int(10) == 0) damage(1, this);
            if (!isAlive())
                return true;
            if (!SpacebaseRun.hero.isAlive()) {
                sayHeroKilled();
                sprite.die();
                destroy();
                return true;
            }
            return super.act();
        }

        @Override
        protected boolean getCloser(int target) {
            if (state == WANDERING || SpacebaseRun.level.distance(target, SpacebaseRun.hero.pos) > 6)
                this.target = target = SpacebaseRun.hero.pos;
            return super.getCloser(target);
        }

        @Override
        protected Char chooseEnemy() {
            if (enemy == null || !enemy.isAlive() || !SpacebaseRun.level.mobs.contains(enemy) || state == WANDERING) {

                HashSet<Mob> enemies = new HashSet<>();
                for (Mob mob : SpacebaseRun.level.mobs) {
                    if (mob.hostile && Level.fieldOfView[mob.pos] && mob.state != mob.PASSIVE) {
                        enemies.add(mob);
                    }
                }
                enemy = enemies.size() > 0 ? Random.element(enemies) : null;
            }
            return enemy;
        }

        @Override
        public int attackSkill(Char target) {
            //same accuracy as the hero.
            return (defenseSkill / 2) + 5;
        }

        @Override
        public int damageRoll() {
            int lvl = (HT - 10) / 3;
            return Random.NormalIntRange(lvl / 2, 5 + lvl);
        }

        @Override
        public int drRoll() {
            //defence is equal to the holopad's level.
            return Random.NormalIntRange(0, (HT - 10) / 3);
        }

        @Override
        public void add(Buff buff) {
            //in other words, can't be directly affected by buffs/debuffs.
        }

        @Override
        public boolean interact() {
            if (!HoloPad.talkedTo) {
                HoloPad.talkedTo = true;
                GameScene.show(new WndQuest(this, Messages.get(this, "introduce")));
                return false;
            } else {
                int curPos = pos;

                moveSprite(pos, SpacebaseRun.hero.pos);
                move(SpacebaseRun.hero.pos);

                SpacebaseRun.hero.sprite.move(SpacebaseRun.hero.pos, curPos);
                SpacebaseRun.hero.move(curPos);

                SpacebaseRun.hero.spend(1 / SpacebaseRun.hero.speed());
                SpacebaseRun.hero.busy();
                return true;
            }
        }

        @Override
        public void die(Object cause) {
            sayDefeated();
            super.die(cause);
        }

        @Override
        public void destroy() {
            HoloPad.spawned = false;
            super.destroy();
        }

        private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

        static {
            IMMUNITIES.add(ToxicGas.class);
            IMMUNITIES.add(VenomGas.class);
            IMMUNITIES.add(Burning.class);
            IMMUNITIES.add(PsionicBlastUpgrade.class);
        }

        @Override
        public HashSet<Class<?>> immunities() {
            return IMMUNITIES;
        }

        //************************************************************************************
        //Emergency hologram voice lines.
        //************************************************************************************

        private static final String VOICE_INTRODUCE = "My recording is anchored to this holopad. It was precious to me, a " +
                "gift from someone I left planetside.\n\nI cannot reach them now, but thanks to you I have a " +
                "second chance to finish the evacuation. When I can, I will answer your call and fight with you.\n\n" +
                "Hopefully you succeed where I failed...";

        //1st index - depth type, 2nd index - specific line.
        static final String[][] VOICE_AMBIENT = {
                {
                        "I dreamed of piloting my own ship and boldly going where no man had gone before...",
                        "Where is security?!",
                        "I have family back on earth, I hope they are safe..."
                }, {
                "I've heard stories about this block, nothing good...",
                "This place was always more of a containment wing than a detention block...",
                "I can't imagine what went on when this place was abandoned..."
        }, {
                "No clean maintenance crew has been here for a very long time...",
                "Something must have gone very wrong for Engineering to abandon these conduits...",
                "I feel something dangerous working under the decks..."
        }, {
                "Command was industrious, but careless...",
                "I hope the surface never ends up like this place...",
                "So the Habitation Ring really has fallen..."
        }, {
                "What is this place?...",
                "So the stories are true, we have to fight whatever escaped containment...",
                "I feel something wrong pulsing through this place..."
        }, {
                "... I don't like this place... We should leave as soon as possible..."
        }
        };

        //1st index - depth type, 2nd index - boss or not, 3rd index - specific line.
        static final String[][][] VOICE_ENEMIES = {
                {
                        {
                                "Let's make the service decks safe again...",
                                "If the guards couldn't defeat them, perhaps we can...",
                                "These maintenance crawlers are extremely annoying..."
                        }, {
                        "Beware the feral shapeshifter!...",
                        "Many of my friends died to this thing, time for vengeance...",
                        "Such an abomination cannot be allowed to live..."
                }
                }, {
                {
                        "What emergency protocol did this?...",
                        "To think the captives of this place are now its guardians...",
                        "They were criminals before, now they are monsters..."
                }, {
                "If only he would see reason, he doesn't seem insane...",
                "He assumes we are hostile, if only he would stop to talk...",
                "The one prisoner left sane is a deadly assassin. Of course..."
        }
        }, {
                {
                        "The aliens here are aggressive, just like on the Maintenance decks...",
                        "More Yendor raiders. I hate Yendor raiders...",
                        "Even the siphon drones are predatory here..."
                }, {
                "Only Engineering would build a mining platform that kills looters...",
                "That thing is huge...",
                "How has it survived here for so long?..."
        }
        }, {
                {
                        "Command staff shouldn't look that pale...",
                        "I don't know what's worse, the raiders or the machines...",
                        "They all obey the corrupted control stack without question, even now..."
                }, {
                "When people say power corrupts, this is what they mean...",
                "He's more failed simulation than monarch now...",
                "Looks like he's more hard-light error than person now..."
        }
        }, {
                {
                        "What the heck is that thing?...",
                        "This place is terrifying...",
                        "What was Command thinking, containing power like this?..."
                }, {
                "Oh.... this doesn't look good...",
                "So that's what a containment failure looks like?...",
                "This is going to hurt..."
        }
        }, {
                {
                        "I don't like this place... we should leave as soon as we can..."
                }, {
                "Hello source viewer, I'm writing this here as this line should never mines. Have a nice day!"
        }
        }
        };

        //1st index - ContainmentMass or not, 2nd index - specific line.
        static final String[][] VOICE_BOSSBEATEN = {
                {
                        "Yes!",
                        "Victory!"
                }, {
                "It's over... we won...",
                "I can't believe it... We just killed a god..."
        }
        };

        //1st index - boss or not, 2nd index - specific line.
        static final String[][] VOICE_DEFEATED = {
                {
                        "Good luck...",
                        "I will return...",
                        "Tired... for now..."
                }, {
                "No... I can't....",
                "I'm sorry.. good luck..",
                "Finish it off... without me..."
        }
        };

        static final String[] VOICE_HEROKILLED = {
                "nooo...",
                "no...",
                "I couldn't help them..."
        };

        static final String[] VOICE_BLESSEDCLONE = {
                "Incredible!...",
                "Wish I had one of those...",
                "How did you survive that?..."
        };
    }
}
