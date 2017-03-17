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
package com.wafitz.pixelspacebase.items.scripts;

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.particles.ShadowParticle;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndBag;

public class UpgradeScript extends InventoryScript {

    {
        initials = 11;
        mode = WndBag.Mode.UPGRADEABLE;

        bones = true;
    }

    @Override
    protected void onItemSelected(Item item) {

        upgrade(curUser);

        //logic for telling the user when item properties change from upgrades
        //...yes this is rather messy
        if (item instanceof Weapon) {
            Weapon w = (Weapon) item;
            boolean wasMalfunctioning = w.malfunctioning;
            boolean hadMalfunctioningEnchant = w.hasMalfunctionEnchant();
            boolean hadGoodEnchant = w.hasGoodEnchant();

            w.upgrade();

            if (hadMalfunctioningEnchant && !w.hasMalfunctionEnchant()) {
                fix(Dungeon.hero);
            } else if (wasMalfunctioning && !w.malfunctioning) {
                tweakMalfunction(Dungeon.hero);
            }
            if (hadGoodEnchant && !w.hasGoodEnchant()) {
                GLog.w(Messages.get(Weapon.class, "incompatible"));
            }

        } else if (item instanceof Armor) {
            Armor a = (Armor) item;
            boolean wasMalfunctioning = a.malfunctioning;
            boolean hadMalfunctioningEnhancement = a.hasMalfunctionEnhancement();
            boolean hadGoodEnhancement = a.hasGoodEnhancement();

            a.upgrade();

            if (hadMalfunctioningEnhancement && !a.hasMalfunctionEnhancement()) {
                fix(Dungeon.hero);
            } else if (wasMalfunctioning && !a.malfunctioning) {
                tweakMalfunction(Dungeon.hero);
            }
            if (hadGoodEnhancement && !a.hasGoodEnhancement()) {
                GLog.w(Messages.get(Armor.class, "incompatible"));
            }

        } else if (item instanceof Blaster) {
            boolean wasMalfunctioning = item.malfunctioning;

            item.upgrade();

            if (wasMalfunctioning && !item.malfunctioning) {
                fix(Dungeon.hero);
            }

        } else if (item instanceof Module) {
            boolean wasMalfunctioning = item.malfunctioning;

            item.upgrade();

            if (wasMalfunctioning && !item.malfunctioning) {
                if (item.level() < 1) {
                    tweakMalfunction(Dungeon.hero);
                } else {
                    fix(Dungeon.hero);
                }
            }

        } else {
            item.upgrade();
        }

        Badges.validateItemLevelAquired(item);
    }

    public static void upgrade(Hero hero) {
        hero.sprite.emitter().start(Speck.factory(Speck.UP), 0.2f, 3);
    }

    private static void tweakMalfunction(Hero hero) {
        GLog.p(Messages.get(UpgradeScript.class, "tweak_malfunction"));
        hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 5);
    }

    private static void fix(Hero hero) {
        GLog.p(Messages.get(UpgradeScript.class, "fix"));
        hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
    }

    @Override
    public int price() {
        return isKnown() ? 50 * quantity : super.price();
    }
}
