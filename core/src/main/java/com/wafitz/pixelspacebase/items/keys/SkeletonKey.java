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
package com.wafitz.pixelspacebase.items.keys;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

public class SkeletonKey extends Key {

    {
        image = ItemSpriteSheet.SECURITY_KEYCARD;
        stackable = false;
    }

    public SkeletonKey() {
        this(0);
    }

    public SkeletonKey(int depth) {
        super();
        this.depth = depth;
    }

    @Override
    public boolean doPickUp(Hero hero) {
        Dungeon.hero.belongings.specialKeys[depth]++;
        return super.doPickUp(hero);
    }

    @Override
    public boolean isSimilar(Item item) {
        return false;
    }

}
