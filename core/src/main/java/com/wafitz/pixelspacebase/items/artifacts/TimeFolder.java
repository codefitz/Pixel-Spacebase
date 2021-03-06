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
package com.wafitz.pixelspacebase.items.artifacts;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.buffs.LockedFloor;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TimeFolder extends Artifact {

    {
        image = ItemSpriteSheet.TIMEFOLDER;

        levelCap = 5;

        charge = 10 + level() * 2;
        partialCharge = 0;
        chargeCap = 10 + level() * 2;

        defaultAction = AC_ACTIVATE;
    }

    private static final String AC_ACTIVATE = "ACTIVATE";

    //keeps track of generated sandbags.
    public int TimeBatteries = 0;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && charge > 0 && !malfunctioning)
            actions.add(AC_ACTIVATE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ACTIVATE)) {

            if (!isEquipped(hero)) GLog.i(Messages.get(Artifact.class, "need_to_equip"));
            else if (activeBuff != null) {
                if (activeBuff instanceof timeStasis) { //do nothing
                } else {
                    activeBuff.detach();
                    GLog.i(Messages.get(this, "deactivate"));
                }
            } else if (charge <= 1) GLog.i(Messages.get(this, "no_charge"));
            else if (malfunctioning) GLog.i(Messages.get(this, "malfunctioning"));
            else GameScene.show(
                        new WndOptions(Messages.get(this, "name"),
                                Messages.get(this, "prompt"),
                                Messages.get(this, "stasis"),
                                Messages.get(this, "freeze")) {
                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    GLog.i(Messages.get(TimeFolder.class, "onstasis"));
                                    GameScene.flash(0xFFFFFF);
                                    Sample.INSTANCE.play(Assets.SND_TELEPORT);

                                    activeBuff = new timeStasis();
                                    activeBuff.attachTo(Dungeon.hero);
                                } else if (index == 1) {
                                    GLog.i(Messages.get(TimeFolder.class, "onfreeze"));
                                    GameScene.flash(0xFFFFFF);
                                    Sample.INSTANCE.play(Assets.SND_TELEPORT);

                                    activeBuff = new timeFreeze();
                                    activeBuff.attachTo(Dungeon.hero);
                                }
                            }
                        }
                );
        }
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        if (activeBuff != null)
            activeBuff.attachTo(ch);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (activeBuff != null) {
                activeBuff.detach();
                activeBuff = null;
            }
            return true;
        } else
            return false;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new hourglassRecharge();
    }

    @Override
    public Item upgrade() {
        chargeCap += 2;

        //for artifact transmutation.
        while (level() + 1 > TimeBatteries)
            TimeBatteries++;

        return super.upgrade();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (!malfunctioning) {
                if (level() < levelCap)
                    desc += "\n\n" + Messages.get(this, "desc_hint");

            } else
                desc += "\n\n" + Messages.get(this, "desc_malfunctioning");
        }
        return desc;
    }


    private static final String TIMEBATTERIES = "timebatteries";
    private static final String BUFF = "buff";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TIMEBATTERIES, TimeBatteries);

        if (activeBuff != null)
            bundle.put(BUFF, activeBuff);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        TimeBatteries = bundle.getInt(TIMEBATTERIES);

        //these buffs belong to hourglass, need to handle unbundling within the hourglass class.
        if (bundle.contains(BUFF)) {
            Bundle buffBundle = bundle.getBundle(BUFF);

            if (buffBundle.contains(timeFreeze.PARTIALTIME))
                activeBuff = new timeFreeze();
            else
                activeBuff = new timeStasis();

            activeBuff.restoreFromBundle(buffBundle);
        }
    }

    private class hourglassRecharge extends ArtifactBuff {
        @Override
        public boolean act() {

            LockedFloor lock = target.buff(LockedFloor.class);
            if (charge < chargeCap && !malfunctioning && (lock == null || lock.regenOn())) {
                partialCharge += 1 / (60f - (chargeCap - charge) * 2f);

                if (partialCharge >= 1) {
                    partialCharge--;
                    charge++;

                    if (charge == chargeCap) {
                        partialCharge = 0;
                    }
                }
            } else if (malfunctioning && Random.Int(10) == 0)
                ((Hero) target).spend(TICK);

            updateQuickslot();

            spend(TICK);

            return true;
        }
    }

    public class timeStasis extends ArtifactBuff {

        @Override
        public boolean attachTo(Char target) {

            if (super.attachTo(target)) {

                int usedCharge = Math.min(charge, 5);
                //buffs always act last, so the stasis buff should end a turn early.
                spend(usedCharge - 1);
                ((Hero) target).spendAndNext(usedCharge);

                //shouldn't punish the player for going into stasis frequently
                Hunger hunger = target.buff(Hunger.class);
                if (hunger != null && !hunger.isStarving())
                    hunger.satisfy(usedCharge);

                charge -= usedCharge;

                target.invisible++;

                updateQuickslot();

                Dungeon.observe();

                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean act() {
            detach();
            return true;
        }

        @Override
        public void detach() {
            if (target.invisible > 0)
                target.invisible--;
            super.detach();
            activeBuff = null;
            Dungeon.observe();
        }
    }

    public class timeFreeze extends ArtifactBuff {

        float partialTime = 0f;

        ArrayList<Integer> presses = new ArrayList<>();

        public boolean processTime(float time) {
            partialTime += time;

            while (partialTime >= 1f) {
                partialTime--;
                charge--;
            }

            updateQuickslot();

            if (charge <= 0) {
                detach();
                return false;
            } else
                return true;

        }

        public void setDelayedPress(int cell) {
            if (!presses.contains(cell))
                presses.add(cell);
        }

        private void mineTriggers() {
            for (int cell : presses)
                Dungeon.level.press(cell, null);

            presses = new ArrayList<>();
        }

        @Override
        public boolean attachTo(Char target) {
            if (Dungeon.level != null)
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
                    mob.sprite.add(CharSprite.State.PARALYSED);
            GameScene.freezeEmitters = true;
            return super.attachTo(target);
        }

        @Override
        public void detach() {
            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
                mob.sprite.remove(CharSprite.State.PARALYSED);
            GameScene.freezeEmitters = false;

            updateQuickslot();
            super.detach();
            activeBuff = null;
            mineTriggers();
        }

        private static final String PRESSES = "presses";
        private static final String PARTIALTIME = "partialtime";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            int[] values = new int[presses.size()];
            for (int i = 0; i < values.length; i++)
                values[i] = presses.get(i);
            bundle.put(PRESSES, values);

            bundle.put(PARTIALTIME, partialTime);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            int[] values = bundle.getIntArray(PRESSES);
            for (int value : values)
                presses.add(value);

            partialTime = bundle.getFloat(PARTIALTIME);
        }
    }

    public static class TimeBattery extends Item {

        {
            image = ItemSpriteSheet.TIMEBATTERY;
        }

        @Override
        public boolean doPickUp(Hero hero) {
            TimeFolder hourglass = hero.belongings.getItem(TimeFolder.class);
            if (hourglass != null && !hourglass.malfunctioning) {
                hourglass.upgrade();
                Sample.INSTANCE.play(Assets.SND_DEWDROP);
                if (hourglass.level() == hourglass.levelCap)
                    GLog.p(Messages.get(this, "maxlevel"));
                else
                    GLog.i(Messages.get(this, "levelup"));
                hero.spendAndNext(TIME_TO_PICK_UP);
                return true;
            } else {
                GLog.w(Messages.get(this, "no_timefolder"));
                return false;
            }
        }

        @Override
        public int cost() {
            return 10;
        }
    }


}
