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
package com.wafitz.pixelspacebase.items.scripts;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.buffs.Invisibility;
import com.wafitz.pixelspacebase.actors.mobs.npcs.MirrorImage;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScriptOfMirrorImage extends Script {

    {
        initials = 4;
    }

    private static final int NIMAGES = 3;

    @Override
    protected void doRead() {

        ArrayList<Integer> respawnPoints = new ArrayList<>();

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = curUser.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) == null && (Level.passable[p] || Level.avoid[p])) {
                respawnPoints.add(p);
            }
        }

        int nImages = NIMAGES;
        while (nImages > 0 && respawnPoints.size() > 0) {
            int index = Random.index(respawnPoints);

            MirrorImage mob = new MirrorImage();
            mob.duplicate(curUser);
            GameScene.add(mob);
            ScriptOfTeleportation.appear(mob, respawnPoints.get(index));

            respawnPoints.remove(index);
            nImages--;
        }

        if (nImages < NIMAGES) {
            setKnown();
        }

        Sample.INSTANCE.play(Assets.SND_READ);
        Invisibility.dispel();

        readAnimation();
    }

    @Override
    public int price() {
        return isKnown() ? 30 * quantity : super.price();
    }
}
