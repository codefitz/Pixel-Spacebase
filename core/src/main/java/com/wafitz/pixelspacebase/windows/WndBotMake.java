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

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.npcs.MakerBot;
import com.wafitz.pixelspacebase.items.EquipableItem;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.Parts;
import com.wafitz.pixelspacebase.items.artifacts.McGyvrModule;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.ui.ItemSlot;
import com.wafitz.pixelspacebase.ui.RedButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Window;

public class WndBotMake extends Window {

    private static final float GAP = 2;
    private static final int WIDTH = 120;
    private static final int BTN_HEIGHT = 16;

    private WndContainer owner;

    public WndBotMake(final Item item, WndContainer owner) {

        super();

        this.owner = owner;

        float pos = createDescription(item, false);

        if (item.quantity() == 1) {

            RedButton btnRender = new RedButton(Messages.get(this, "render", item.cost())) {
                @Override
                protected void onClick() {
                    render(item);
                    hide();
                }
            };
            btnRender.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
            add(btnRender);

            pos = btnRender.bottom();

        } else {

            int costAll = item.cost();
            RedButton btnRender1 = new RedButton(Messages.get(this, "render_1", costAll / item.quantity())) {
                @Override
                protected void onClick() {
                    renderOne(item);
                    hide();
                }
            };
            btnRender1.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
            add(btnRender1);
            RedButton btnRenderAll = new RedButton(Messages.get(this, "render_all", costAll)) {
                @Override
                protected void onClick() {
                    render(item);
                    hide();
                }
            };
            btnRenderAll.setRect(0, btnRender1.bottom() + GAP, WIDTH, BTN_HEIGHT);
            add(btnRenderAll);

            pos = btnRenderAll.bottom();

        }

        RedButton btnCancel = new RedButton(Messages.get(this, "cancel")) {
            @Override
            protected void onClick() {
                hide();
            }
        };
        btnCancel.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
        add(btnCancel);

        resize(WIDTH, (int) btnCancel.bottom());
    }

    public WndBotMake(final Heap heap, boolean canBuild) {

        super();

        Item item = heap.peek();

        float pos = createDescription(item, true);

        final int price = cost(item);

        if (canBuild) {

            RedButton btnBuild = new RedButton(Messages.get(this, "build", price)) {
                @Override
                protected void onClick() {
                    hide();
                    build(heap);
                }
            };
            btnBuild.setRect(0, pos + GAP, WIDTH, BTN_HEIGHT);
            btnBuild.enable(price <= Dungeon.parts);
            add(btnBuild);

            RedButton btnCancel = new RedButton(Messages.get(this, "cancel")) {
                @Override
                protected void onClick() {
                    hide();
                }
            };

            final McGyvrModule.Thievery thievery = Dungeon.hero.buff(McGyvrModule.Thievery.class);
            if (thievery != null) {
                final float chance = thievery.stealChance(price);
                RedButton btnMcgyvr = new RedButton(Messages.get(this, "mcgyvr", Math.min(100, (int) (chance * 100)))) {
                    @Override
                    protected void onClick() {
                        if (thievery.steal(price)) {
                            Hero hero = Dungeon.hero;
                            Item item = heap.pickUp();
                            hide();

                            if (!item.doPickUp(hero)) {
                                Dungeon.level.drop(item, heap.pos).sprite.drop();
                            }
                        } else {
                            for (Mob mob : Dungeon.level.mobs) {
                                if (mob instanceof MakerBot) {
                                    mob.yell(Messages.get(mob, "thief"));
                                    ((MakerBot) mob).flee();
                                    break;
                                }
                            }
                            hide();
                        }
                    }
                };
                btnMcgyvr.setRect(0, btnBuild.bottom() + GAP, WIDTH, BTN_HEIGHT);
                add(btnMcgyvr);

                btnCancel.setRect(0, btnMcgyvr.bottom() + GAP, WIDTH, BTN_HEIGHT);
            } else
                btnCancel.setRect(0, btnBuild.bottom() + GAP, WIDTH, BTN_HEIGHT);

            add(btnCancel);

            resize(WIDTH, (int) btnCancel.bottom());

        } else {

            resize(WIDTH, (int) pos);

        }
    }

    @Override
    public void hide() {

        super.hide();

        if (owner != null) {
            owner.hide();
            MakerBot.render();
        }
    }

    private float createDescription(Item item, boolean toMake) {

        // Title
        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(item));
        titlebar.label(toMake ?
                Messages.get(this, "make", item.toString(), cost(item)) :
                Messages.titleCase(item.toString()));
        titlebar.setRect(0, 0, WIDTH, 0);
        add(titlebar);

        // Upgraded / degraded
        if (item.levelKnown) {
            if (item.level() < 0) {
                titlebar.color(ItemSlot.DEGRADED);
            } else if (item.level() > 0) {
                titlebar.color(ItemSlot.UPGRADED);
            }
        }

        // Description
        RenderedTextMultiline info = PixelScene.renderMultiline(item.info(), 6);
        info.maxWidth(WIDTH);
        info.setPos(titlebar.left(), titlebar.bottom() + GAP);
        add(info);

        return info.bottom();
    }

    private void render(Item item) {

        Hero hero = Dungeon.hero;

        if (item.isEquipped(hero) && !((EquipableItem) item).doUnequip(hero, false)) {
            return;
        }
        item.detachAll(hero.belongings.backpack);

        int price = item.cost();

        new Parts(price).doPickUp(hero);
    }

    private void renderOne(Item item) {

        if (item.quantity() <= 1) {
            render(item);
        } else {

            Hero hero = Dungeon.hero;

            item = item.detach(hero.belongings.backpack);
            int price = item.cost();

            new Parts(price).doPickUp(hero);
        }
    }

    private int cost(Item item) {
        int cost = item.cost() * 5 * (Dungeon.depth / 5 + 1);
        return cost;
    }

    private void build(Heap heap) {

        Hero hero = Dungeon.hero;
        Item item = heap.pickUp();

        int price = cost(item);
        Dungeon.parts -= price;

        if (!item.doPickUp(hero)) {
            Dungeon.level.drop(item, heap.pos).sprite.drop();
        }
    }
}
