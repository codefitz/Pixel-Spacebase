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
package com.wafitz.pixelspacebase.mines;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.particles.EarthParticle;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ParalyzingAgent;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.ui.BuffIndicator;
import com.watabou.noosa.Camera;
import com.watabou.utils.Bundle;

public class WeakForcefield extends Mine {

    {
        image = 5;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch == Dungeon.hero) {
            Buff.affect(ch, Armor.class).level(ch != null ? ch.HT : 0);
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.bottom(pos).start(EarthParticle.FACTORY, 0.05f, 8);
            Camera.main.shake(1, 0.4f);
        }
    }

    public static class Device extends Mine.Device {
        {
            image = ItemSpriteSheet.FORCEFIELD_TECH;

            mineClass = WeakForcefield.class;
            craftingClass = ParalyzingAgent.class;

            bones = true;
        }
    }

    public static class Armor extends Buff {

        private static final float STEP = 1f;

        private int pos;
        private int level;

        {
            type = buffType.POSITIVE;
        }

        @Override
        public boolean attachTo(Char target) {
            pos = target.pos;
            return super.attachTo(target);
        }

        @Override
        public boolean act() {
            if (target.pos != pos) {
                detach();
            }
            spend(STEP);
            return true;
        }

        public int absorb(int damage) {
            if (level <= damage - damage / 2) {
                detach();
                return damage - level;
            } else {
                level -= damage - damage / 2;
                return damage / 2;
            }
        }

        public void level(int value) {
            if (level < value) {
                level = value;
            }
            pos = target.pos;
        }

        @Override
        public int icon() {
            return BuffIndicator.WEAKFORCEFIELD;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", level);
        }

        private static final String POS = "pos";
        private static final String LEVEL = "level";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(POS, pos);
            bundle.put(LEVEL, level);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            pos = bundle.getInt(POS);
            level = bundle.getInt(LEVEL);
        }
    }
}
