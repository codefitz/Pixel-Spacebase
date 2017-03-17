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
import com.wafitz.pixelspacebase.actors.hero.Belongings;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.artifacts.HoloPad;
import com.wafitz.pixelspacebase.items.artifacts.LloydsBeacon;
import com.wafitz.pixelspacebase.scenes.InterlevelScene;
import com.watabou.noosa.Game;

public class DistortionVent extends Vent {

    {
        color = TEAL;
        shape = LARGE_DOT;
    }

    @Override
    public void activate() {
        InterlevelScene.returnDepth = Dungeon.depth;
        Belongings belongings = Dungeon.hero.belongings;
        belongings.ironKeys[Dungeon.depth] = 0;
        belongings.specialKeys[Dungeon.depth] = 0;
        for (Item i : belongings) {
            if (i instanceof LloydsBeacon && ((LloydsBeacon) i).returnDepth == Dungeon.depth)
                ((LloydsBeacon) i).returnDepth = -1;
        }

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0]))
            if (mob instanceof HoloPad.HologramHero) mob.destroy();

        InterlevelScene.mode = InterlevelScene.Mode.RESET;
        Game.switchScene(InterlevelScene.class);
    }
}
