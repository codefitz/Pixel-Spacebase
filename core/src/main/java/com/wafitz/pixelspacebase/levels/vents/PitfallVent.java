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
package com.wafitz.pixelspacebase.levels.vents;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.levels.features.Chasm;
import com.wafitz.pixelspacebase.scenes.GameScene;

public class PitfallVent extends Vent {

    {
        color = RED;
        shape = DIAMOND;
    }

    @Override
    public void activate() {
        Heap heap = Dungeon.level.heaps.get(pos);

        if (heap != null) {
            for (Item item : heap.items) {
                Dungeon.dropToChasm(item);
            }
            heap.sprite.kill();
            GameScene.discard(heap);
            Dungeon.level.heaps.remove(pos);
        }

        Char ch = Actor.findChar(pos);

        if (ch == Dungeon.hero) {
            Chasm.heroFall(pos);
        } else if (ch != null) {
            Chasm.mobFall((Mob) ch);
        }
    }

    @Override
    protected void disarm() {
        super.disarm();

        //if making a pit here wouldn't block any paths, make a pit tile instead of a disarmed trap tile.
        if (!(Level.solid[pos - Dungeon.level.width()] && Level.solid[pos + Dungeon.level.width()])
                && !(Level.solid[pos - 1] && Level.solid[pos + 1])) {

            Level.set(pos, Terrain.CHASM);
            GameScene.updateMap(pos);
        }
    }
}
