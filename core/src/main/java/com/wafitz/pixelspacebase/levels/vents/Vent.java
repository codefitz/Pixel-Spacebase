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
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public abstract class Vent implements Bundlable {

    //trap colors
    static final int RED = 0;
    static final int ORANGE = 1;
    static final int YELLOW = 2;
    static final int GREEN = 3;
    static final int TEAL = 4;
    static final int VIOLET = 5;
    public static final int WHITE = 6;
    static final int GREY = 7;
    public static final int BLACK = 8;

    //trap shapes
    static final int DOTS = 0;
    static final int WAVES = 1;
    static final int GRILL = 2;
    static final int STARS = 3;
    static final int DIAMOND = 4;
    static final int CROSSHAIR = 5;
    static final int LARGE_DOT = 6;

    public String name = Messages.get(this, "name");

    public int color;
    public int shape;

    public int pos;

    public boolean visible;
    public boolean active = true;

    public Vent set(int pos) {
        this.pos = pos;
        return this;
    }

    public Vent reveal() {
        visible = true;
        GameScene.updateMap(pos);
        return this;
    }

    public Vent hide() {
        visible = false;
        GameScene.updateMap(pos);
        return this;
    }

    public void mine() {
        if (active) {
            if (Dungeon.visible[pos]) {
                Sample.INSTANCE.play(Assets.SND_TRAP);
            }
            disarm();
            reveal();
            activate();
        }
    }

    public abstract void activate();

    protected void disarm() {
        Dungeon.level.disarmVent(pos);
        active = false;
    }

    private static final String POS = "pos";
    private static final String VISIBLE = "visible";
    private static final String ACTIVE = "active";

    @Override
    public void restoreFromBundle(Bundle bundle) {
        pos = bundle.getInt(POS);
        visible = bundle.getBoolean(VISIBLE);
        if (bundle.contains(ACTIVE)) {
            active = bundle.getBoolean(ACTIVE);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(POS, pos);
        bundle.put(VISIBLE, visible);
        bundle.put(ACTIVE, active);
    }

    public String desc() {
        return Messages.get(this, "desc");
    }
}
