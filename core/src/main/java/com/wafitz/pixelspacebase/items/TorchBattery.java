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
package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;

import java.util.ArrayList;

public class TorchBattery extends Item {

    private static final String AC_RECHARGE = "RECHARGE";
    private static final float TIME_TO_RECHARGE = 1f;

    {
        image = ItemSpriteSheet.HOLOBATTERY;
        stackable = true;
        defaultAction = AC_RECHARGE;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_RECHARGE);
        if (hero.heroClass == HeroClass.DM3000) {
            actions.add(Food.AC_USE);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_RECHARGE)) {
            Torch torch = findRechargeableTorch(hero);
            if (torch == null) {
                GLog.w(Messages.get(this, "no_torch"));
                return;
            }

            detach(hero.belongings.backpack);
            torch.recharge();

            hero.spend(TIME_TO_RECHARGE);
            hero.busy();
            hero.sprite.operate(hero.pos);

            GLog.p(Messages.get(this, "recharged", torch.name()));
        } else if (action.equals(Food.AC_USE) && hero.heroClass == HeroClass.DM3000) {
            detach(hero.belongings.backpack);
            DM3000Power.consumeBattery(hero);

            hero.spend(DM3000Power.TIME_TO_CONSUME);
            hero.busy();
        }
    }

    private Torch findRechargeableTorch(Hero hero) {
        for (Item item : hero.belongings) {
            if (item instanceof Torch && ((Torch) item).canRecharge()) {
                return (Torch) item;
            }
        }
        return null;
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
        return 15 * quantity;
    }
}
