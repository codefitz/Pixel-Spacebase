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
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.KindOfWeapon;
import com.wafitz.pixelspacebase.items.weapon.melee.Knuckles;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class DisarmingVent extends Vent {

    {
        color = RED;
        shape = LARGE_DOT;
    }

    @Override
    public void activate() {
        Heap heap = SpacebaseRun.level.heaps.get(pos);

        if (heap != null) {
            int cell = SpacebaseRun.level.randomRespawnCell();

            if (cell != -1) {
                Item item = heap.pickUp();
                SpacebaseRun.level.drop(item, cell).seen = true;
                for (int i : PathFinder.NEIGHBOURS9)
                    SpacebaseRun.level.visited[cell + i] = true;
                GameScene.updateFog();

                Sample.INSTANCE.play(Assets.SND_TELEPORT);
                CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
            }
        }

        if (SpacebaseRun.hero.pos == pos) {
            Hero hero = SpacebaseRun.hero;
            KindOfWeapon weapon = hero.belongings.weapon;

            if (weapon != null && !(weapon instanceof Knuckles) && !weapon.malfunctioning) {

                int cell = SpacebaseRun.level.randomRespawnCell();
                if (cell != -1) {
                    hero.belongings.weapon = null;
                    SpacebaseRun.quickslot.clearItem(weapon);
                    weapon.updateQuickslot();

                    SpacebaseRun.level.drop(weapon, cell).seen = true;
                    for (int i : PathFinder.NEIGHBOURS9)
                        SpacebaseRun.level.visited[cell + i] = true;
                    GameScene.updateFog();

                    GLog.w(Messages.get(this, "disarm"));

                    Sample.INSTANCE.play(Assets.SND_TELEPORT);
                    CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);

                }

            }
        }
    }
}
