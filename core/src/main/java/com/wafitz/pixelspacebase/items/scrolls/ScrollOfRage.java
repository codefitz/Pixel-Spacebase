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
package com.wafitz.pixelspacebase.items.scrolls;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.buffs.Amok;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Invisibility;
import com.wafitz.pixelspacebase.actors.mobs.ConfusedShapeshifter;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfRage extends Scroll {

    {
        initials = 6;
    }

    @Override
    protected void doRead() {

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            mob.beckon(curUser.pos);
            if (Level.fieldOfView[mob.pos]) {
                Buff.prolong(mob, Amok.class, 5f);
            }
        }

        for (Heap heap : Dungeon.level.heaps.values()) {
            if (heap.type == Heap.Type.MIMIC) {
                ConfusedShapeshifter m = ConfusedShapeshifter.spawnAt(heap.pos, heap.items);
                if (m != null) {
                    m.beckon(curUser.pos);
                    heap.destroy();
                }
            }
        }

        GLog.w(Messages.get(this, "roar"));
        setKnown();

        curUser.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
        Sample.INSTANCE.play(Assets.SND_CHALLENGE);
        Invisibility.dispel();

        readAnimation();
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
