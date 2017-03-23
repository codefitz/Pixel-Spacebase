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
package com.wafitz.pixelspacebase.items.weapon.melee;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.messages.Messages;

public class MeleeWeapon extends Weapon {

    public int tier;

    @Override
    public int min(int lvl) {
        return tier +  //base
                lvl;    //level scaling
    }

    @Override
    public int max(int lvl) {
        return 5 * (tier + 1) +    //base
                lvl * (tier + 1);   //level scaling
    }

    @Override
    public Item upgrade() {
        return upgrade(false);
    }

    public Item safeUpgrade() {
        return upgrade(enhancement != null);
    }

    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);
        //strength req decreases at +1,+3,+6,+10,etc.
        return (8 + tier * 2) - (int) (Math.sqrt(8 * lvl + 1) - 1) / 2;
    }

    @Override
    public String info() {

        String info = desc();

        if (levelKnown) {
            info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", tier, convert.damageFactor(min()), convert.damageFactor(max()), STRReq());
            if (STRReq() > Dungeon.hero.STR()) {
                info += " " + Messages.get(Weapon.class, "too_heavy");
            } else if (Dungeon.hero.STR() > STRReq()) {
                info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
            }
        } else {
            info += "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", tier, min(0), max(0), STRReq(0));
            if (STRReq(0) > Dungeon.hero.STR()) {
                info += " " + Messages.get(MeleeWeapon.class, "probably_too_heavy");
            }
        }

        String stats_desc = Messages.get(this, "stats_desc");
        if (!stats_desc.equals("")) info += "\n\n" + stats_desc;

        switch (convert) {
            case LIGHT:
                info += "\n\n" + Messages.get(Weapon.class, "lighter");
                break;
            case HEAVY:
                info += "\n\n" + Messages.get(Weapon.class, "heavier");
                break;
            case NONE:
        }

        if (enhancement != null && (malfunctioningKnown || !enhancement.malfunction())) {
            info += "\n\n" + Messages.get(Weapon.class, "enhanced", enhancement.name());
            info += " " + Messages.get(enhancement, "desc");
        }

        if (malfunctioning && isEquipped(Dungeon.hero)) {
            info += "\n\n" + Messages.get(Weapon.class, "malfunctioning_worn");
        } else if (malfunctioningKnown && malfunctioning) {
            info += "\n\n" + Messages.get(Weapon.class, "malfunctioning");
        }

        return info;
    }

    @Override
    public int cost() {
        int price = 20 * tier;
        if (hasGoodEnhance()) {
            price *= 1.5;
        }
        if (malfunctioningKnown && (malfunctioning || hasMalfunctionEnhance())) {
            price /= 2;
        }
        if (levelKnown && level() > 0) {
            price *= (level() + 1);
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

}
