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

import com.wafitz.pixelspacebase.Challenges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.sprites.DarkLordGnollSprite;
import com.wafitz.pixelspacebase.sprites.GreatCrabSprite;
import com.wafitz.pixelspacebase.sprites.ToughXenoSprite;
import com.wafitz.pixelspacebase.ui.RedButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Window;
import com.wafitz.pixelspacebase.utils.GLog;

public class WndHologram extends Window {

    private static final int WIDTH = 120;
    private static final int BTN_HEIGHT = 20;
    private static final float GAP = 2;

    public WndHologram(final Hologram hologram, final int type) {

        super();

        IconTitle titlebar = new IconTitle();
        RenderedTextMultiline message;
        switch (type) {
            case 1:
            default:
                titlebar.icon(new ToughXenoSprite());
                titlebar.label(Messages.get(this, "xenomorph_title"));
                message = PixelScene.renderMultiline(Messages.get(this, "xenomorph") + Messages.get(this, "give_item"), 6);
                break;
            case 2:
                titlebar.icon(new DarkLordGnollSprite());
                titlebar.label(Messages.get(this, "gnoll_title"));
                message = PixelScene.renderMultiline(Messages.get(this, "gnoll") + Messages.get(this, "give_item"), 6);
                break;
            case 3:
                titlebar.icon(new GreatCrabSprite());
                titlebar.label(Messages.get(this, "crab_title"));
                message = PixelScene.renderMultiline(Messages.get(this, "crab") + Messages.get(this, "give_item"), 6);
                break;

        }

        titlebar.setRect(0, 0, WIDTH, 0);
        add(titlebar);

        message.maxWidth(WIDTH);
        message.setPos(0, titlebar.bottom() + GAP);
        add(message);

        RedButton btnWeapon = new RedButton(Messages.get(this, "weapon")) {
            @Override
            protected void onClick() {
                selectReward(hologram, Hologram.Quest.weapon);
            }
        };
        btnWeapon.setRect(0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT);
        add(btnWeapon);

        if (!Dungeon.isChallenged(Challenges.NO_ARMOR)) {
            RedButton btnArmor = new RedButton(Messages.get(this, "armor")) {
                @Override
                protected void onClick() {
                    selectReward(hologram, Hologram.Quest.armor);
                }
            };
            btnArmor.setRect(0, btnWeapon.bottom() + GAP, WIDTH, BTN_HEIGHT);
            add(btnArmor);

            resize(WIDTH, (int) btnArmor.bottom());
        } else {
            resize(WIDTH, (int) btnWeapon.bottom());
        }
    }

    private void selectReward(Hologram hologram, Item reward) {

        hide();

        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Messages.get(Dungeon.hero, "you_now_have", reward.name()));
        } else {
            Dungeon.level.drop(reward, hologram.pos).sprite.drop();
        }

        // wafitz.v1 - Hologram now recognises you!
        hologram.yell("Farewell, " + Dungeon.hero.heroClass.title() + "!");
        hologram.die(null);

        Hologram.Quest.complete();
    }
}
