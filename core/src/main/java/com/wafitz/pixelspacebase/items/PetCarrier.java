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
import com.wafitz.pixelspacebase.actors.mobs.npcs.StationCat;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class PetCarrier extends KindofMisc {

    private static final String AC_RELEASE = "RELEASE";
    private static final String HAS_CAT = "hasCat";

    private boolean hasCat;

    {
        image = ItemSpriteSheet.LOCKED_CHEST;
        unique = true;
        bones = false;
        defaultAction = AC_RELEASE;
        identify();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hasCat) {
            actions.add(AC_RELEASE);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_RELEASE) && hasCat) {
            if (StationCat.releaseFromCarrier(hero, this)) {
                GLog.i(Messages.get(this, "release"));
                hero.spendAndNext(1f);
            } else {
                GLog.w(Messages.get(this, "no_space"));
            }
        }
    }

    @Override
    public void doDrop(Hero hero) {
        if (hasCat && !StationCat.releaseFromCarrier(hero, this)) {
            GLog.w(Messages.get(this, "no_space"));
            return;
        }
        super.doDrop(hero);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public String info() {
        String info = super.info();
        if (hasCat) {
            info += "\n\n" + Messages.get(this, "contains");
        }
        return info;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HAS_CAT, hasCat);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        hasCat = bundle.getBoolean(HAS_CAT);
        if (hasCat) {
            StationCat.Quest.markCarried();
        }
    }

    public boolean hasCat() {
        return hasCat;
    }

    public void putCat() {
        hasCat = true;
        updateQuickslot();
    }

    public void removeCat() {
        hasCat = false;
        updateQuickslot();
    }

    public static PetCarrier equippedBy(Hero hero) {
        if (hero == null) {
            return null;
        }
        if (hero.belongings.misc1 instanceof PetCarrier) {
            return (PetCarrier) hero.belongings.misc1;
        }
        if (hero.belongings.misc2 instanceof PetCarrier) {
            return (PetCarrier) hero.belongings.misc2;
        }
        return null;
    }

    public static PetCarrier carriedBy(Hero hero) {
        return hero == null ? null : hero.belongings.getItem(PetCarrier.class);
    }

    public static boolean hasCat(Hero hero) {
        PetCarrier carrier = carriedBy(hero);
        return carrier != null && carrier.hasCat();
    }
}
