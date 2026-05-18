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

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.equippablemodules.HoloPad;
import com.wafitz.pixelspacebase.items.equippablemodules.TimeFolder;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.InterlevelScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WarpingVent extends Vent {

    {
        color = TEAL;
        shape = STARS;
    }

    @Override
    public void activate() {
        CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
        Sample.INSTANCE.play(Assets.SND_TELEPORT);

        if (SpacebaseRun.depth > 1 && !SpacebaseRun.bossLevel()) {

            //each depth has 1 more weight than the previous depth.
            float[] depths = new float[SpacebaseRun.depth - 1];
            for (int i = 1; i < SpacebaseRun.depth; i++) depths[i - 1] = i;
            int depth = 1 + Random.chances(depths);

            Heap heap = SpacebaseRun.level.heaps.get(pos);
            if (heap != null) {
                if (heap.type == Heap.Type.HEAP) {
                    ArrayList<Item> dropped = SpacebaseRun.droppedItems.get(depth);
                    if (dropped == null) {
                        SpacebaseRun.droppedItems.put(depth, dropped = new ArrayList<>());
                    }
                    for (Item item : heap.items) {
                        dropped.add(item);
                    }
                    heap.destroy();
                } else {
                    SpacebaseRun.dropHeapToDepth(heap, depth);
                    if (heap.sprite != null) {
                        heap.sprite.kill();
                    }
                    GameScene.discard(heap);
                    SpacebaseRun.level.heaps.remove(pos);
                }
            }

            Char ch = Actor.findChar(pos);
            if (ch == SpacebaseRun.hero) {
                Buff buff = SpacebaseRun.hero.buff(TimeFolder.timeFreeze.class);
                if (buff != null) buff.detach();

                for (Mob mob : SpacebaseRun.level.mobs.toArray(new Mob[0]))
                    if (mob instanceof HoloPad.HologramHero) mob.destroy();

                InterlevelScene.mode = InterlevelScene.Mode.RETURN;
                InterlevelScene.returnDepth = depth;
                InterlevelScene.returnPos = -1;
                Game.switchScene(InterlevelScene.class);
            } else if (ch != null) {
                ch.destroy();
                ch.sprite.killAndErase();
                SpacebaseRun.level.mobs.remove(ch);
            }

        }

    }
}
