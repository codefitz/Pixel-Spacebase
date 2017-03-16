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
package com.wafitz.pixelspacebase.actors.blobs;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.Journal;
import com.wafitz.pixelspacebase.effects.BlobEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.Level;
import com.watabou.utils.Bundle;

public class Craft extends Blob {

    protected int pos;

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        if (volume > 0)
            for (int i = 0; i < cur.length; i++) {
                if (cur[i] > 0) {
                    pos = i;
                    break;
                }
            }
    }

    @Override
    protected void evolve() {
        volume = off[pos] = cur[pos];
        area.union(pos % Dungeon.level.width(), pos / Dungeon.level.width());

        if (Dungeon.visible[pos]) {
            Journal.add(Journal.Feature.ALCHEMY);
        }
    }

    @Override
    public void gadget(Level level, int cell, int amount) {
        super.gadget(level, cell, amount);

        cur[pos] = 0;
        pos = cell;
        volume = cur[pos] = amount;

        area.setEmpty();
        area.union(cell % level.width(), cell / level.width());
    }

    public static void transmute(int cell) {
        Heap heap = Dungeon.level.heaps.get(cell);
        if (heap != null) {

            Item result = heap.transmute();
            if (result != null) {
                Dungeon.level.drop(result, cell).sprite.drop(cell);
            }
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        // wafitz.v4: Craft terminal now shines occasionally
        emitter.start(Speck.factory(Speck.LIGHT), 6, 0);
    }
}
