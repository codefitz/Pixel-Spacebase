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
package com.wafitz.pixelspacebase.items.armor.glyphs;

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.armor.Armor.Glyph;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.sprites.ItemSprite.Glowing;
import com.wafitz.pixelspacebase.ui.BuffIndicator;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Viscosity extends Glyph {

    private static ItemSprite.Glowing PURPLE = new ItemSprite.Glowing(0x8844CC);

    @Override
    public int proc(Armor armor, Char attacker, Char defender, int damage) {

        if (damage == 0) {
            return 0;
        }

        int level = Math.max(0, armor.level());

        if (Random.Int(level + 4) >= 3) {

            DeferedDamage debuff = defender.buff(DeferedDamage.class);
            if (debuff == null) {
                debuff = new DeferedDamage();
                debuff.attachTo(defender);
            }
            debuff.prolong(damage);

            defender.sprite.showStatus(CharSprite.WARNING, Messages.get(this, "deferred", damage));

            return 0;

        } else {
            return damage;
        }
    }

    @Override
    public Glowing glowing() {
        return PURPLE;
    }

    public static class DeferedDamage extends Buff {

        protected int damage = 0;

        private static final String DAMAGE = "damage";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DAMAGE, damage);

        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            damage = bundle.getInt(DAMAGE);
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                postpone(TICK);
                return true;
            } else {
                return false;
            }
        }

        public void prolong(int damage) {
            this.damage += damage;
        }

        @Override
        public int icon() {
            return BuffIndicator.DEFERRED;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public boolean act() {
            if (target.isAlive()) {

                int damageThisTick = Math.max(1, damage / 10);
                target.damage(damageThisTick, this);
                if (target == Dungeon.hero && !target.isAlive()) {

                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "ondeath"));

                    Badges.validateDeathFromGlyph();
                }
                spend(TICK);

                damage -= damageThisTick;
                if (damage <= 0) {
                    detach();
                }

            } else {

                detach();

            }

            return true;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", damage);
        }
    }
}
