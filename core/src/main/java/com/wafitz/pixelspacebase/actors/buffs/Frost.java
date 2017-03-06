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
package com.wafitz.pixelspacebase.actors.buffs;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.Thief;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.food.FrozenCarpaccio;
import com.wafitz.pixelspacebase.items.food.MysteryMeat;
import com.wafitz.pixelspacebase.items.potions.Potion;
import com.wafitz.pixelspacebase.items.potions.PotionOfMight;
import com.wafitz.pixelspacebase.items.potions.PotionOfStrength;
import com.wafitz.pixelspacebase.items.rings.RingOfElements.Resistance;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.CharSprite;
import com.wafitz.pixelspacebase.ui.BuffIndicator;
import com.wafitz.pixelspacebase.utils.GLog;

public class Frost extends FlavourBuff {

    private static final float DURATION = 5f;

    {
        type = buffType.NEGATIVE;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {

            target.paralysed++;
            Buff.detach(target, Burning.class);
            Buff.detach(target, Chill.class);

            if (target instanceof Hero) {

                Hero hero = (Hero) target;
                Item item = hero.belongings.randomUnequipped();
                if (item instanceof Potion
                        && !(item instanceof PotionOfStrength || item instanceof PotionOfMight)) {

                    item = item.detach(hero.belongings.backpack);
                    GLog.w(Messages.get(this, "freezes", item.toString()));
                    ((Potion) item).shatter(hero.pos);

                } else if (item instanceof MysteryMeat) {

                    item = item.detach(hero.belongings.backpack);
                    FrozenCarpaccio carpaccio = new FrozenCarpaccio();
                    if (!carpaccio.collect(hero.belongings.backpack)) {
                        Dungeon.level.drop(carpaccio, target.pos).sprite.drop();
                    }
                    GLog.w(Messages.get(this, "freezes", item.toString()));

                }
            } else if (target instanceof Thief) {

                Item item = ((Thief) target).item;

                if (item instanceof Potion && !(item instanceof PotionOfStrength || item instanceof PotionOfMight)) {
                    ((Potion) ((Thief) target).item).shatter(target.pos);
                    ((Thief) target).item = null;
                }

            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (target.paralysed > 0)
            target.paralysed--;
        if (Level.water[target.pos])
            Buff.prolong(target, Chill.class, 4f);
    }

    @Override
    public int icon() {
        return BuffIndicator.FROST;
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.FROZEN);
        else target.sprite.remove(CharSprite.State.FROZEN);
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", dispTurns());
    }

    public static float duration(Char ch) {
        Resistance r = ch.buff(Resistance.class);
        return r != null ? r.durationFactor() * DURATION : DURATION;
    }
}
