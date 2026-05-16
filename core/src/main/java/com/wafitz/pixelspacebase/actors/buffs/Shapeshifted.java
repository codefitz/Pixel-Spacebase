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

import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.HeroSprite;
import com.watabou.utils.Bundle;

public class Shapeshifted extends Camoflage {

    private int itemImage;

    private static final String ITEM_IMAGE = "itemImage";

    public void setItemImage(int itemImage) {
        this.itemImage = itemImage;
        if (target != null && target.sprite instanceof HeroSprite) {
            ((HeroSprite) target.sprite).shapeshiftToItem(itemImage);
        }
    }

    @Override
    public boolean act() {
        diactivate();
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ITEM_IMAGE, itemImage);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        itemImage = bundle.getInt(ITEM_IMAGE);
    }

    @Override
    public void fx(boolean on) {
        super.fx(on);
        if (target.sprite instanceof HeroSprite) {
            if (on) {
                ((HeroSprite) target.sprite).shapeshiftToItem(itemImage);
            } else {
                ((HeroSprite) target.sprite).restoreHeroForm();
            }
        }
    }

    @Override
    public String toString() {
        return Messages.get(this, "name");
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc");
    }
}
