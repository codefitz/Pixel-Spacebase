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
package com.wafitz.pixelspacebase.windows;

import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.Parts;
import com.wafitz.pixelspacebase.items.containers.Container;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.ui.ItemSlot;
import com.wafitz.pixelspacebase.ui.RedButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.RenderedText;

import java.util.ArrayList;

/** A two-way, paged item transfer view for the workshop's quantum chest. */
public class WndQuantumStorage extends WndTabbed {

    private static final int SLOT_SIZE = 28;
    private static final int SLOT_MARGIN = 1;
    private static final int ROWS = 4;
    private static final int TITLE_HEIGHT = 22;

    private static final int NORMAL = 0x9953564D;
    private static final int HIGHLIGHT = 0x9991938C;

    private final Heap chest;
    private final Hero hero;
    private final boolean backpackTab;
    private final int columns;
    private final int pageSize;
    private final int page;
    private final ArrayList<Item> contents;

    public WndQuantumStorage(Heap chest, Hero hero, boolean backpackTab, int page) {
        super();

        this.chest = chest;
        this.hero = hero;
        this.backpackTab = backpackTab;
        columns = PixelSpacebase.landscape() ? 6 : 4;
        pageSize = columns * ROWS;
        contents = contents(backpackTab);
        int pages = Math.max(1, (contents.size() + pageSize - 1) / pageSize);
        this.page = Math.max(0, Math.min(page, pages - 1));

        int slotsWidth = SLOT_SIZE * columns + SLOT_MARGIN * (columns - 1);
        int slotsHeight = SLOT_SIZE * ROWS + SLOT_MARGIN * (ROWS - 1);

        RenderedText title = PixelScene.renderText(Messages.titleCase(Messages.get(Heap.class, "workshop_storage")), 9);
        title.hardlight(TITLE_COLOR);
        title.x = (slotsWidth - title.width()) / 2;
        title.y = 1;
        add(title);

        RenderedTextMultiline hint = PixelScene.renderMultiline(
                Messages.get(Heap.class, "workshop_storage_hint"), 6);
        hint.maxWidth(slotsWidth - 12);
        hint.setPos((slotsWidth - hint.width()) / 2, 12);
        add(hint);

        int slotsTop = Math.max(TITLE_HEIGHT, (int) Math.ceil(hint.bottom() + 2));

        int first = this.page * pageSize;
        for (int i = 0; i < pageSize; i++) {
            int index = first + i;
            Item item = index < contents.size() ? contents.get(index) : null;
            int x = (i % columns) * (SLOT_SIZE + SLOT_MARGIN);
            int y = slotsTop + (i / columns) * (SLOT_SIZE + SLOT_MARGIN);
            add(new TransferSlot(item).setPos(x, y));
        }

        if (contents.isEmpty()) {
            RenderedText empty = PixelScene.renderText(Messages.get(Heap.class,
                    backpackTab ? "workshop_storage_pack_empty" : "workshop_storage_empty"), 6);
            empty.x = (slotsWidth - empty.width()) / 2;
            empty.y = slotsTop + (slotsHeight - empty.height()) / 2;
            add(empty);
        }

        int pagesTextY = slotsTop + slotsHeight + 1;
        RenderedText pageText = PixelScene.renderText(Messages.get(Heap.class,
                "workshop_storage_page", this.page + 1, pages), 6);
        pageText.x = (slotsWidth - pageText.width()) / 2;
        pageText.y = pagesTextY;
        add(pageText);

        int buttonY = pagesTextY + 9;
        int buttonWidth = (slotsWidth - 2) / 2;
        RedButton previous = new RedButton(Messages.get(Heap.class, "workshop_storage_previous"), 6) {
            @Override
            protected void onClick() {
                if (WndQuantumStorage.this.page > 0) {
                    reopen(backpackTab, WndQuantumStorage.this.page - 1);
                }
            }
        };
        previous.setRect(0, buttonY, buttonWidth, 18);
        previous.enable(this.page > 0);
        add(previous);

        RedButton next = new RedButton(Messages.get(Heap.class, "workshop_storage_next"), 6) {
            @Override
            protected void onClick() {
                if (WndQuantumStorage.this.page + 1 < pages) {
                    reopen(backpackTab, WndQuantumStorage.this.page + 1);
                }
            }
        };
        next.setRect(buttonWidth + 2, buttonY, buttonWidth, 18);
        next.enable(this.page + 1 < pages);
        add(next);

        resize(slotsWidth, buttonY + 18);

        Tab pack = add(new LabeledTab(Messages.get(Heap.class, "workshop_storage_pack_tab")));
        Tab storage = add(new LabeledTab(Messages.get(Heap.class, "workshop_storage_chest_tab")));
        pack.select(backpackTab);
        storage.select(!backpackTab);
        layoutTabs();
    }

    private ArrayList<Item> contents(boolean backpack) {
        if (!backpack) {
            return new ArrayList<>(chest.items);
        }

        ArrayList<Item> items = new ArrayList<>();
        for (Item item : hero.belongings.backpack.items) {
            if (canStore(item)) {
                items.add(item);
            }
        }
        return items;
    }

    private boolean canStore(Item item) {
        return item != null && !item.isEquipped(hero) && !(item instanceof Parts) && !(item instanceof Container);
    }

    private void transfer(Item item) {
        if (item == null) {
            return;
        }

        if (backpackTab) {
            if (!canStore(item)) {
                return;
            }
            chest.drop(item.detachAll(hero.belongings.backpack));
        } else {
            if (!item.collect(hero.belongings.backpack)) {
                return;
            }
            chest.items.remove(item);
        }

        if (chest.sprite != null) {
            chest.sprite.view(chest.image(), chest.glowing());
        }
        reopen(backpackTab, page);
    }

    private void reopen(boolean backpack, int page) {
        hide();
        GameScene.show(new WndQuantumStorage(chest, hero, backpack, page));
    }

    @Override
    protected void onClick(Tab tab) {
        reopen(tab == tabs.get(0), 0);
    }

    @Override
    protected int tabHeight() {
        return 20;
    }

    private class TransferSlot extends ItemSlot {

        private ColorBlock background;

        TransferSlot(Item item) {
            super(item);
            width = height = SLOT_SIZE;
        }

        @Override
        protected void createChildren() {
            background = new ColorBlock(SLOT_SIZE, SLOT_SIZE, NORMAL);
            add(background);
            super.createChildren();
        }

        @Override
        protected void layout() {
            background.x = x;
            background.y = y;
            super.layout();
        }

        @Override
        public void item(Item item) {
            super.item(item);
            if (background != null) {
                background.texture(TextureCache.createSolid(item != null && item.isEquipped(hero) ? HIGHLIGHT : NORMAL));
            }
        }

        @Override
        protected void onTouchDown() {
            background.brightness(1.5f);
        }

        @Override
        protected void onTouchUp() {
            background.brightness(1.0f);
        }

        @Override
        protected void onClick() {
            transfer(item);
        }
    }
}
