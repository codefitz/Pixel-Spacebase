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
package com.wafitz.pixelspacebase.actors.blobs;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class HealingTank extends MedicalTerminal {

    @Override
    protected boolean affectHero(Hero hero) {
        if (hero.HP >= hero.HT) {
            return false;
        }

        Buff.affect(hero, TankHealing.class).reset(pos);
        SpacebaseRun.hero.interrupt();
        return true;
    }

    @Override
    protected Item affectItem(Item item) {
        return null;
    }

    @Override
    protected boolean isConsumedOnUse() {
        return false;
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }

    public static class TankHealing extends Buff {

        private static final float STEP = 1f;
        private static final float HEAL_DELAY = 2f;

        private int pos;
        private float partial;

        {
            type = buffType.POSITIVE;
        }

        @Override
        public boolean act() {
            if (!(target instanceof Hero)
                    || target.pos != pos
                    || SpacebaseRun.level.map[pos] != Terrain.HEALING_TANK) {
                detach();
                return true;
            }

            Hero hero = (Hero) target;
            if (hero.HP >= hero.HT) {
                detach();
                return true;
            }

            partial += STEP;
            if (partial >= HEAL_DELAY) {
                partial -= HEAL_DELAY;
                if (hero.medicalHealing(1) > 0 && hero.sprite != null) {
                    hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                }
            }

            spend(STEP);
            return true;
        }

        public void reset(int pos) {
            this.pos = pos;
            partial = 0;
        }

        @Override
        public int icon() {
            return BuffIndicator.HEALING;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc");
        }

        private static final String POS = "pos";
        private static final String PARTIAL = "partial";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
            bundle.put(PARTIAL, partial);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
            partial = bundle.getFloat(PARTIAL);
        }
    }
}
