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
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Camoflaged;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.BlobEmitter;
import com.wafitz.pixelspacebase.effects.particles.ShaftParticle;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;

public class Medical extends Blob {

    @Override
    protected void evolve() {

        int[] map = Dungeon.level.map;

        boolean visible = false;

        int cell;
        for (int i = area.left; i < area.right; i++) {
            for (int j = area.top; j < area.bottom; j++) {
                cell = i + j * Dungeon.level.width();
                if (cur[cell] > 0) {

                    off[cell] = cur[cell];
                    volume += off[cell];

                    if (map[cell] == Terrain.EMBERS) {
                        map[cell] = Terrain.LIGHTEDVENT;
                        GameScene.updateMap(cell);
                    }

                    visible = visible || Dungeon.visible[cell];

                } else {
                    off[cell] = 0;
                }
            }
        }

        Hero hero = Dungeon.hero;
        if (hero.isAlive() && hero.visibleEnemies() == 0 && cur[hero.pos] > 0) {
            Buff.affect(hero, Camoflaged.class).prolong();
        }

        if (visible) {
            Journal.add(Journal.Feature.MEDICALBAY);
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.start(ShaftParticle.FACTORY, 0.9f, 0);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
