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
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.DungeonTilemap;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.Beam;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.containers.Container;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class DisintegrationVent extends Vent {

    {
        color = VIOLET;
        shape = LARGE_DOT;
    }

    @Override
    public void activate() {

        if (Dungeon.visible[pos]) {
            PixelSpacebase.scene().add(new Beam.DeathRay(DungeonTilemap.tileCenterToWorld(pos - 1),
                    DungeonTilemap.tileCenterToWorld(pos + 1)));
            PixelSpacebase.scene().add(new Beam.DeathRay(DungeonTilemap.tileCenterToWorld(pos - Dungeon.level.width()),
                    DungeonTilemap.tileCenterToWorld(pos + Dungeon.level.width())));
            Sample.INSTANCE.play(Assets.SND_RAY);
        }

        Heap heap = Dungeon.level.heaps.get(pos);
        if (heap != null) heap.explode();

        Char ch = Actor.findChar(pos);
        if (ch != null) {
            ch.damage(Math.max(ch.HT / 5, Random.Int(ch.HP / 2, 2 * ch.HP / 3)), this);
            if (ch == Dungeon.hero) {
                Hero hero = (Hero) ch;
                if (!hero.isAlive()) {
                    Dungeon.fail(getClass());
                    GLog.n(Messages.get(this, "ondeath"));
                } else {
                    Item item = hero.belongings.randomUnequipped();
                    Container container = hero.belongings.backpack;
                    //containers do not protect against this trap
                    if (item instanceof Container) {
                        container = (Container) item;
                        item = Random.element(container.items);
                    }
                    if (item == null || item.level() > 0 || item.unique) return;
                    if (!item.stackable) {
                        item.detachAll(container);
                        GLog.w(Messages.get(this, "one", item.name()));
                    } else {
                        int n = Random.NormalIntRange(1, (item.quantity() + 1) / 2);
                        for (int i = 1; i <= n; i++)
                            item.detach(container);
                        GLog.w(Messages.get(this, "some", item.name()));
                    }
                }
            }
        }

    }
}
