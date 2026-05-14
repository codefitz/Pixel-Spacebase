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
package com.wafitz.pixelspacebase.effects;

import com.wafitz.pixelspacebase.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class ForcePull extends Group {

    private static final double A = 180 / Math.PI;

    private float spent = 0f;
    private float duration;

    private Callback callback;

    private Image[] pulses;
    private float distance;
    private float rotation = 0;

    private PointF from, to;

    public ForcePull(int from, int to, Callback callback) {
        this(DungeonTilemap.tileCenterToWorld(from),
                DungeonTilemap.tileCenterToWorld(to),
                callback);
    }

    public ForcePull(PointF from, PointF to, Callback callback) {
        super();

        this.callback = callback;

        this.from = from;
        this.to = to;

        float dx = to.x - from.x;
        float dy = to.y - from.y;
        distance = (float) Math.hypot(dx, dy);

        duration = distance / 300f + 0.1f;

        rotation = (float) (Math.atan2(dy, dx) * A) + 90f;

        int numPulses = Math.round(distance / 6f) + 1;

        pulses = new Image[numPulses];
        for (int i = 0; i < pulses.length; i++) {
            pulses[i] = new Image(Effects.get(Effects.Type.CHAIN));
            pulses[i].angle = rotation;
            pulses[i].origin.set(pulses[i].width() / 2, pulses[i].height());
            add(pulses[i]);
        }
    }

    @Override
    public void update() {
        if ((spent += Game.elapsed) > duration) {

            killAndErase();
            if (callback != null) {
                callback.call();
            }

        } else {
            float dx = to.x - from.x;
            float dy = to.y - from.y;
            for (int i = 0; i < pulses.length; i++) {
                pulses[i].center(new PointF(
                        from.x + ((dx * (i / (float) pulses.length)) * (spent / duration)),
                        from.y + ((dy * (i / (float) pulses.length)) * (spent / duration))
                ));
            }
        }
    }

}
