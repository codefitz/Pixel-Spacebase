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

import com.wafitz.pixelspacebase.actors.buffs.Weakness;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.Flare;
import com.wafitz.pixelspacebase.effects.particles.ShadowParticle;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.containers.Container;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndContainer;

public class FixScript extends InventoryScript {

    {
        initials = 8;
        mode = WndContainer.Mode.UNIDED_OR_MALFUNCTIONING;
    }

    @Override
    protected void onItemSelected(Item item) {
        new Flare(6, 32).show(curUser.sprite, 2f);

        boolean procced = fix(curUser, item);

        Weakness.detach(curUser, Weakness.class);

        if (procced) {
            GLog.p(Messages.get(this, "fixed"));
        } else {
            GLog.i(Messages.get(this, "not_fixed"));
        }
    }

    public static boolean fix(Hero hero, Item... items) {

        boolean procced = false;
        for (Item item : items) {
            if (item != null && item.malfunctioning) {
                item.malfunctioning = false;
                procced = true;
            }
            if (item instanceof Weapon) {
                Weapon w = (Weapon) item;
                if (w.hasMalfunctionEnhance()) {
                    w.enhance(null);
                    w.malfunctioning = false;
                    procced = true;
                }
            }
            if (item instanceof Armor) {
                Armor a = (Armor) item;
                if (a.hasMalfunctionEnhancement()) {
                    a.enhance(null);
                    a.malfunctioning = false;
                    procced = true;
                }
            }
            if (item instanceof Module && item.level() <= 0) {
                item.upgrade(1 - item.level());
            }
            if (item instanceof Container) {
                for (Item containerItem : ((Container) item).items) {
                    if (containerItem != null && containerItem.malfunctioning) {
                        containerItem.malfunctioning = false;
                        procced = true;
                    }
                }
            }
        }

        if (procced) {
            hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
        }

        return procced;
    }

    @Override
    public int cost() {
        return isKnown() ? 30 * quantity : super.cost();
    }
}
