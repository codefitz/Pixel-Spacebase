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

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.DungeonTilemap;
import com.wafitz.pixelspacebase.Journal;
import com.wafitz.pixelspacebase.Journal.Feature;
import com.wafitz.pixelspacebase.actors.buffs.Awareness;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.BlobEmitter;
import com.wafitz.pixelspacebase.effects.Identification;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class KnowledgebaseTerminal extends WellWater {

    @Override
    protected boolean affectHero(Hero hero) {

        Sample.INSTANCE.play(Assets.SND_DRINK);
        emitter.parent.add(new Identification(DungeonTilemap.tileCenterToWorld(pos)));

        hero.belongings.observe();

        for (int i = 0; i < Dungeon.level.length(); i++) {

            int terr = Dungeon.level.map[i];
            if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                Dungeon.level.discover(i);

                if (Dungeon.visible[i]) {
                    GameScene.discoverTile(i, terr);
                }
            }
        }

        Buff.affect(hero, Awareness.class, Awareness.DURATION);
        Dungeon.observe();

        Dungeon.hero.interrupt();

        GLog.p(Messages.get(this, "procced"));

        Journal.remove(Feature.WELL_OF_AWARENESS);

        return true;
    }

    @Override
    protected Item affectItem(Item item) {
        if (item.isIdentified()) {
            return null;
        } else {
            item.identify();
            Badges.validateItemLevelAquired(item);

            emitter.parent.add(new Identification(DungeonTilemap.tileCenterToWorld(pos)));

            Journal.remove(Feature.WELL_OF_AWARENESS);

            return item;
        }
    }

    @Override
    public void use(BlobEmitter emitter) {
        super.use(emitter);
        emitter.pour(Speck.factory(Speck.QUESTION), 0.3f);
    }

    @Override
    public String tileDesc() {
        return Messages.get(this, "desc");
    }
}
