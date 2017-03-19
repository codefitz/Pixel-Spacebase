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
package com.wafitz.pixelspacebase.actors.mobs.npcs;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.particles.ElmoParticle;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.MakerBotSprite;
import com.wafitz.pixelspacebase.windows.WndBotMake;
import com.wafitz.pixelspacebase.windows.WndContainer;

public class MakerBot extends NPC {

    {
        spriteClass = MakerBotSprite.class;

        properties.add(Property.IMMOVABLE);
    }

    @Override
    protected boolean act() {

        throwItem();

        sprite.turnTo(pos, Dungeon.hero.pos);
        spend(TICK);
        return true;
    }

    @Override
    public void damage(int dmg, Object src) {
        flee();
    }

    @Override
    public void add(Buff buff) {
        flee();
    }

    public void flee() {
        for (Heap heap : Dungeon.level.heaps.values()) {
            if (heap.type == Heap.Type.TO_MAKE) {
                CellEmitter.get(heap.pos).burst(ElmoParticle.FACTORY, 4);
                heap.destroy();
            }
        }

        destroy();

        sprite.killAndErase();
        CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
    }

    @Override
    public boolean reset() {
        return true;
    }

    public static WndContainer render() {
        return GameScene.selectItem(itemSelector, WndContainer.Mode.TO_MAKE, Messages.get(MakerBot.class, "render"));
    }

    private static WndContainer.Listener itemSelector = new WndContainer.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null) {
                WndContainer parentWnd = render();
                GameScene.show(new WndBotMake(item, parentWnd));
            }
        }
    };

    @Override
    public boolean interact() {
        render();
        return false;
    }
}
