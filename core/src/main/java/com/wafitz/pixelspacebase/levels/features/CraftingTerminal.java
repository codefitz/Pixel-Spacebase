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

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.ExperimentalTech.AlienTech;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.windows.WndContainer;
import com.wafitz.pixelspacebase.windows.WndOptions;

import java.util.Iterator;

public class CraftingTerminal {

    public static Hero hero;
    public static int pos;

    private static boolean foundFruit;
    private static Item curItem = null;

    public static void operate(Hero hero, int pos) {

        CraftingTerminal.hero = hero;
        CraftingTerminal.pos = pos;

        Iterator<Item> items = hero.belongings.iterator();
        foundFruit = false;
        Heap heap = Dungeon.level.heaps.get(pos);

        if (heap == null)
            while (items.hasNext() && !foundFruit) {
                curItem = items.next();
                if (curItem instanceof AlienTech && ((AlienTech) curItem).experimentalTechAttrib == null) {
                    GameScene.show(
                            new WndOptions(Messages.get(CraftingTerminal.class, "pot"),
                                    Messages.get(CraftingTerminal.class, "options"),
                                    Messages.get(CraftingTerminal.class, "fruit"),
                                    Messages.get(CraftingTerminal.class, "experimentaltech")) {
                                @Override
                                protected void onSelect(int index) {
                                    if (index == 0) {
                                        curItem.cast(CraftingTerminal.hero, CraftingTerminal.pos);
                                    } else
                                        GameScene.selectItem(itemSelector, WndContainer.Mode.GADGET, Messages.get(CraftingTerminal.class, "select_gadget"));
                                }
                            }
                    );
                    foundFruit = true;
                }
            }

        if (!foundFruit)
            GameScene.selectItem(itemSelector, WndContainer.Mode.GADGET, Messages.get(CraftingTerminal.class, "select_gadget"));
    }

    private static final WndContainer.Listener itemSelector = new WndContainer.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null) {
                item.cast(hero, pos);
            }
        }
    };
}
