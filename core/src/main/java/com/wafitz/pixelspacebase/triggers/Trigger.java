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
package com.wafitz.pixelspacebase.triggers;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Shielding;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroSubClass;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.particles.LeafParticle;
import com.wafitz.pixelspacebase.items.Dewdrop;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.artifacts.GnollTechShield;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public abstract class Trigger implements Bundlable {

    public String triggerName = Messages.get(this, "name");

    public int image;
    public int pos;

    public void trigger() {

        Char ch = Actor.findChar(pos);

        if (ch instanceof Hero && ((Hero) ch).subClass == HeroSubClass.WARDEN) {
            Buff.affect(ch, Shielding.class).level(ch.HT / 3);
        }

        wither();
        activate();
    }

    public abstract void activate();

    public void wither() {
        Dungeon.level.uproot(pos);

        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6);
        }

        if (Dungeon.hero.subClass == HeroSubClass.WARDEN) {

            int naturalismLevel = 0;
            GnollTechShield.Naturalism naturalism = Dungeon.hero.buff(GnollTechShield.Naturalism.class);
            if (naturalism != null) {
                naturalismLevel = naturalism.itemLevel() + 1;
            }

            if (Random.Int(5 - (naturalismLevel / 2)) == 0) {
                Item gadget = Generator.random(Generator.Category.GADGET);

                if (gadget instanceof AlienPlant.Gadget) {
                    if (Random.Int(15) - Dungeon.limitedDrops.alienTechGadget.count >= 0) {
                        Dungeon.level.drop(gadget, pos).sprite.drop();
                        Dungeon.limitedDrops.alienTechGadget.count++;
                    }
                } else
                    Dungeon.level.drop(gadget, pos).sprite.drop();
            }
            if (Random.Int(5 - naturalismLevel) == 0) {
                Dungeon.level.drop(new Dewdrop(), pos).sprite.drop();
            }
        }
    }

    private static final String POS = "pos";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        pos = bundle.getInt(POS);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, pos);
    }

    public String desc() {
        return Messages.get(this, "desc");
    }

    public static class Gadget extends Item {

        static final String AC_TRIGGER = "TRIGGER";

        private static final float TIME_TO_TRIGGER = 1f;

        {
            stackable = true;
            defaultAction = AC_THROW;
        }

        protected Class<? extends Trigger> triggerClass;

        public Class<? extends Item> craftingClass;

        @Override
        public ArrayList<String> actions(Hero hero) {
            ArrayList<String> actions = super.actions(hero);
            actions.add(AC_TRIGGER);
            return actions;
        }

        @Override
        protected void onThrow(int cell) {
            if (Dungeon.level.map[cell] == Terrain.CRAFTING || Level.pit[cell] || Dungeon.level.vents.get(cell) != null) {
                super.onThrow(cell);
            } else {
                Dungeon.level.trigger(this, cell);
            }
        }

        @Override
        public void execute(Hero hero, String action) {

            super.execute(hero, action);

            if (action.equals(AC_TRIGGER)) {

                hero.spend(TIME_TO_TRIGGER);
                hero.busy();
                ((Gadget) detach(hero.belongings.backpack)).onThrow(hero.pos);

                hero.sprite.operate(hero.pos);

            }
        }

        public Trigger couch(int pos) {
            try {
                if (Dungeon.visible[pos]) {
                    Sample.INSTANCE.play(Assets.SND_PLANT);
                }
                Trigger trigger = triggerClass.newInstance();
                trigger.pos = pos;
                return trigger;
            } catch (Exception e) {
                PixelSpacebase.reportException(e);
                return null;
            }
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        @Override
        public int cost() {
            return 10 * quantity;
        }

        @Override
        public String desc() {
            return Messages.get(triggerClass, "desc");
        }

        @Override
        public String info() {
            return Messages.get(Gadget.class, "info", desc());
        }
    }
}
