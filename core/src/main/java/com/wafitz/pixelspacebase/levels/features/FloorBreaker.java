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
package com.wafitz.pixelspacebase.levels.features;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class FloorBreaker {

    public static void operate(int pos) {
        if (SpacebaseRun.level.floorBreakerOn) {
            GLog.i(Messages.get(FloorBreaker.class, "already_on"));
            return;
        }

        SpacebaseRun.level.restoreFloorLighting();
        SpacebaseRun.hero.viewDistance = SpacebaseRun.heroViewDistance();
        SpacebaseRun.observe();
        GameScene.updateFog();
        CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 8);
        Sample.INSTANCE.play(Assets.SND_CLICK);
        GLog.p(Messages.get(FloorBreaker.class, "restored"));
    }
}
