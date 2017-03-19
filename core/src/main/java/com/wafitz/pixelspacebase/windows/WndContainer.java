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

import android.graphics.RectF;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.hero.Belongings;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.EquipableItem;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.Parts;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.containers.BlasterHolster;
import com.wafitz.pixelspacebase.items.containers.Container;
import com.wafitz.pixelspacebase.items.containers.GadgetCase;
import com.wafitz.pixelspacebase.items.containers.ScriptLibrary;
import com.wafitz.pixelspacebase.items.containers.XPort;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.items.scripts.Script;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.items.weapon.melee.MeleeWeapon;
import com.wafitz.pixelspacebase.items.weapon.missiles.Boomerang;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.triggers.Trigger.Gadget;
import com.wafitz.pixelspacebase.ui.Icons;
import com.wafitz.pixelspacebase.ui.ItemSlot;
import com.wafitz.pixelspacebase.ui.QuickSlotButton;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.audio.Sample;

public class WndContainer extends WndTabbed {

    public enum Mode {
        ALL,
        UNIDENTIFED,
        UNIDED_OR_MALFUNCTIONING,
        UPGRADEABLE,
        QUICKSLOT,
        TO_MAKE,
        WEAPON,
        ARMOR,
        ENHANCEABLE,
        BLASTER,
        GADGET,
        FOOD,
        EXPERIMENTALTECH,
        SCRIPT,
        EQUIPMENT
    }

    private static final int COLS_P = 4;
    private static final int COLS_L = 6;

    private static final int SLOT_SIZE = 28;
    private static final int SLOT_MARGIN = 1;

    private static final int TITLE_HEIGHT = 12;

    private Listener listener;
    private WndContainer.Mode mode;
    private String title;

    private int nCols;
    private int nRows;

    protected int count;
    private int col;
    private int row;

    private static Mode lastMode;
    private static Container lastContainer;

    public WndContainer(Container container, Listener listener, Mode mode, String title) {

        super();

        this.listener = listener;
        this.mode = mode;
        this.title = title;

        lastMode = mode;
        lastContainer = container;

        nCols = PixelSpacebase.landscape() ? COLS_L : COLS_P;
        nRows = (Belongings.BACKPACK_SIZE + 4 + 1) / nCols + ((Belongings.BACKPACK_SIZE + 4 + 1) % nCols > 0 ? 1 : 0);

        int slotsWidth = SLOT_SIZE * nCols + SLOT_MARGIN * (nCols - 1);
        int slotsHeight = SLOT_SIZE * nRows + SLOT_MARGIN * (nRows - 1);

        RenderedText txtTitle = PixelScene.renderText(title != null ? title : Messages.titleCase(container.name()), 9);
        txtTitle.hardlight(TITLE_COLOR);
        txtTitle.x = (int) (slotsWidth - txtTitle.width()) / 2;
        txtTitle.y = (int) (TITLE_HEIGHT - txtTitle.height()) / 2;
        add(txtTitle);

        placeItems(container);

        resize(slotsWidth, slotsHeight + TITLE_HEIGHT);

        Belongings stuff = Dungeon.hero.belongings;
        Container[] containers = {
                stuff.backpack,
                stuff.getItem(GadgetCase.class),
                stuff.getItem(ScriptLibrary.class),
                stuff.getItem(XPort.class),
                stuff.getItem(BlasterHolster.class)};

        for (Container b : containers) {
            if (b != null) {
                ContainerTab tab = new ContainerTab(b);
                add(tab);
                tab.select(b == container);
            }
        }

        layoutTabs();
    }

    public static WndContainer lastContainer(Listener listener, Mode mode, String title) {

        if (mode == lastMode && lastContainer != null &&
                Dungeon.hero.belongings.backpack.contains(lastContainer)) {

            return new WndContainer(lastContainer, listener, mode, title);

        } else {

            return new WndContainer(Dungeon.hero.belongings.backpack, listener, mode, title);

        }
    }

    public static WndContainer getContainer(Class<? extends Container> containerClass, Listener listener, Mode mode, String title) {
        Container container = Dungeon.hero.belongings.getItem(containerClass);
        return container != null ?
                new WndContainer(container, listener, mode, title) :
                lastContainer(listener, mode, title);
    }

    private void placeItems(Container container) {

        // Equipped items
        Belongings stuff = Dungeon.hero.belongings;
        placeItem(stuff.weapon != null ? stuff.weapon : new Placeholder(ItemSpriteSheet.WEAPON_HOLDER));
        placeItem(stuff.armor != null ? stuff.armor : new Placeholder(ItemSpriteSheet.ARMOR_HOLDER));
        placeItem(stuff.misc1 != null ? stuff.misc1 : new Placeholder(ItemSpriteSheet.MODULE_HOLDER));
        placeItem(stuff.misc2 != null ? stuff.misc2 : new Placeholder(ItemSpriteSheet.MODULE_HOLDER));

        boolean backpack = (container == Dungeon.hero.belongings.backpack);
        if (!backpack) {
            count = nCols;
            col = 0;
            row = 1;
        }

        // Items in the container
        for (Item item : container.items) {
            placeItem(item);
        }

        // Free Space
        while (count - (backpack ? 4 : nCols) < container.size) {
            placeItem(null);
        }

        // Parts
        if (container == Dungeon.hero.belongings.backpack) {
            row = nRows - 1;
            col = nCols - 1;
            placeItem(new Parts(Dungeon.parts));
        }
    }

    private void placeItem(final Item item) {

        int x = col * (SLOT_SIZE + SLOT_MARGIN);
        int y = TITLE_HEIGHT + row * (SLOT_SIZE + SLOT_MARGIN);

        add(new ItemButton(item).setPos(x, y));

        if (++col >= nCols) {
            col = 0;
            row++;
        }

        count++;
    }

    @Override
    public void onMenuPressed() {
        if (listener == null) {
            hide();
        }
    }

    @Override
    public void onBackPressed() {
        if (listener != null) {
            listener.onSelect(null);
        }
        super.onBackPressed();
    }

    @Override
    protected void onClick(Tab tab) {
        hide();
        GameScene.show(new WndContainer(((ContainerTab) tab).container, listener, mode, title));
    }

    @Override
    protected int tabHeight() {
        return 20;
    }

    private class ContainerTab extends Tab {

        private Image icon;

        private Container container;

        ContainerTab(Container container) {
            super();

            this.container = container;

            icon = icon();
            add(icon);
        }

        @Override
        protected void select(boolean value) {
            super.select(value);
            icon.am = selected ? 1.0f : 0.6f;
        }

        @Override
        protected void layout() {
            super.layout();

            icon.copy(icon());
            icon.x = x + (width - icon.width) / 2;
            icon.y = y + (height - icon.height) / 2 - 2 - (selected ? 0 : 1);
            if (!selected && icon.y < y + CUT) {
                RectF frame = icon.frame();
                frame.top += (y + CUT - icon.y) / icon.texture.height;
                icon.frame(frame);
                icon.y = y + CUT;
            }
        }

        private Image icon() {
            if (container instanceof GadgetCase) {
                return Icons.get(Icons.GADGET_CASE);
            } else if (container instanceof ScriptLibrary) {
                return Icons.get(Icons.SCRIPT_LIBRARY);
            } else if (container instanceof BlasterHolster) {
                return Icons.get(Icons.BLASTER_HOLSTER);
            } else if (container instanceof XPort) {
                return Icons.get(Icons.XPORT);
            } else {
                return Icons.get(Icons.BACKPACK);
            }
        }
    }

    private static class Placeholder extends Item {
        {
            name = null;
        }

        Placeholder(int image) {
            this.image = image;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }

        @Override
        public boolean isEquipped(Hero hero) {
            return true;
        }
    }

    private class ItemButton extends ItemSlot {

        private static final int NORMAL = 0x9953564D;
        private static final int EQUIPPED = 0x9991938C;

        private Item item;
        private ColorBlock bg;

        ItemButton(Item item) {

            super(item);

            this.item = item;
            if (item instanceof Parts) {
                bg.visible = false;
            }

            width = height = SLOT_SIZE;
        }

        @Override
        protected void createChildren() {
            bg = new ColorBlock(SLOT_SIZE, SLOT_SIZE, NORMAL);
            add(bg);

            super.createChildren();
        }

        @Override
        protected void layout() {
            bg.x = x;
            bg.y = y;

            super.layout();
        }

        @Override
        public void item(Item item) {

            super.item(item);
            if (item != null) {

                bg.texture(TextureCache.createSolid(item.isEquipped(Dungeon.hero) ? EQUIPPED : NORMAL));
                if (item.malfunctioning && item.malfunctioningKnown) {
                    bg.ra = +0.3f;
                    bg.ga = -0.15f;
                } else if (!item.isIdentified()) {
                    bg.ra = 0.2f;
                    bg.ba = 0.2f;
                }

                if (item.name() == null) {
                    enable(false);
                } else {
                    enable(
                            mode == Mode.TO_MAKE && (item.cost() > 0) && (!item.isEquipped(Dungeon.hero) || !item.malfunctioning) ||
                                    mode == Mode.UPGRADEABLE && item.isUpgradable() ||
                                    mode == Mode.UNIDENTIFED && !item.isIdentified() ||
                                    mode == Mode.UNIDED_OR_MALFUNCTIONING && ((item instanceof EquipableItem || item instanceof Blaster) && (!item.isIdentified() || item.malfunctioning)) ||
                                    mode == Mode.QUICKSLOT && (item.defaultAction != null) ||
                                    mode == Mode.WEAPON && (item instanceof MeleeWeapon || item instanceof Boomerang) ||
                                    mode == Mode.ARMOR && (item instanceof Armor) ||
                                    mode == Mode.ENHANCEABLE && (item instanceof MeleeWeapon || item instanceof Boomerang || item instanceof Armor) ||
                                    mode == Mode.BLASTER && (item instanceof Blaster) ||
                                    mode == Mode.GADGET && (item instanceof Gadget) ||
                                    mode == Mode.FOOD && (item instanceof Food) ||
                                    mode == Mode.EXPERIMENTALTECH && (item instanceof ExperimentalTech) ||
                                    mode == Mode.SCRIPT && (item instanceof Script) ||
                                    mode == Mode.EQUIPMENT && (item instanceof EquipableItem) ||
                                    mode == Mode.ALL
                    );
                    //extra logic for malfunctioning weapons or armor
                    if (!active && mode == Mode.UNIDED_OR_MALFUNCTIONING) {
                        if (item instanceof Weapon) {
                            Weapon w = (Weapon) item;
                            enable(w.hasMalfunctionEnhance());
                        }
                        if (item instanceof Armor) {
                            Armor a = (Armor) item;
                            enable(a.hasMalfunctionEnhancement());
                        }
                    }
                }
            } else {
                bg.color(NORMAL);
            }
        }

        @Override
        protected void onTouchDown() {
            bg.brightness(1.5f);
            Sample.INSTANCE.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f);
        }

        protected void onTouchUp() {
            bg.brightness(1.0f);
        }

        @Override
        protected void onClick() {
            if (!lastContainer.contains(item) && !item.isEquipped(Dungeon.hero)) {

                hide();

            } else if (listener != null) {

                hide();
                listener.onSelect(item);

            } else {

                GameScene.show(new WndItem(WndContainer.this, item));

            }
        }

        @Override
        protected boolean onLongClick() {
            if (listener == null && item.defaultAction != null) {
                hide();
                Dungeon.quickslot.setSlot(0, item);
                QuickSlotButton.refresh();
                return true;
            } else {
                return false;
            }
        }
    }

    public interface Listener {
        void onSelect(Item item);
    }
}
