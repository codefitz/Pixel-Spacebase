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
package com.wafitz.pixelspacebase.items.armor;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.KindOfWeapon;
import com.wafitz.pixelspacebase.items.weapon.melee.LoaderArm;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;

public class Loader extends Armor {

    {
        image = ItemSpriteSheet.LOADER;
    }

    public Loader() {
        super(5);
    }

    @Override
    public boolean doEquip(Hero hero) {
        if (hero.belongings.armor != null && hero.belongings.armor.malfunctioning) {
            return super.doEquip(hero);
        }

        KindOfWeapon weapon = hero.belongings.weapon;
        if (weapon != null && !(weapon instanceof LoaderArm)
                && !weapon.doUnequip(hero, true, false)) {
            return false;
        }

        if (!super.doEquip(hero)) return false;

        LoaderArm arm = hero.belongings.weapon instanceof LoaderArm
                ? (LoaderArm) hero.belongings.weapon
                : hero.belongings.getItem(LoaderArm.class);
        if (arm == null) arm = new LoaderArm();
        else arm.detachAll(hero.belongings.backpack);

        hero.belongings.weapon = arm;
        arm.activate(hero);
        arm.updateQuickslot();
        arm.malfunctioningKnown = true;
        if (arm.malfunctioning) {
            KindOfWeapon.equipMalfunctioning(hero);
            GLog.n(Messages.get(KindOfWeapon.class, "malfunctioning"));
        }
        return true;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (!super.doUnequip(hero, collect, single)) return false;

        if (hero.belongings.weapon instanceof LoaderArm) {
            LoaderArm arm = (LoaderArm) hero.belongings.weapon;
            arm.unequipForConfiscation(hero);
            if (!collect || !arm.collect(hero.belongings.backpack)) {
                SpacebaseRun.level.drop(arm, hero.pos);
            }
        }
        return true;
    }

}
