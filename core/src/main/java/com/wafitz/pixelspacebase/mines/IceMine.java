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
package com.wafitz.pixelspacebase.mines;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.blobs.Fire;
import com.wafitz.pixelspacebase.actors.blobs.Freezing;
import com.wafitz.pixelspacebase.items.ExperimentalTech.Cryongenics;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.BArray;
import com.watabou.utils.PathFinder;

public class IceMine extends Mine {

    {
        image = 1;
    }

    @Override
    public void activate() {

        PathFinder.buildDistanceMap(pos, BArray.not(Level.losBlocking, null), 1);

        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);

        for (int i = 0; i < PathFinder.distance.length; i++) {
            if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                Freezing.affect(i, fire);
            }
        }
    }

    public static class Device extends Mine.Device {
        {
            image = ItemSpriteSheet.ICE;

            mineClass = IceMine.class;
            craftingClass = Cryongenics.class;
        }
    }
}
