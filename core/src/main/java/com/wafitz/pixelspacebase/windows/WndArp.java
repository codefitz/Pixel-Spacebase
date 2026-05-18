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

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Arp;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.quest.HardLightEmitter;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.ui.RedButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Window;
import com.wafitz.pixelspacebase.utils.GLog;

public class WndArp extends Window {

    private static final int WIDTH = 120;
    private static final int BTN_HEIGHT = 20;
    private static final int GAP = 2;

    public WndArp(final Arp arp, final HardLightEmitter tokens) {

        super();

        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(tokens.image(), null));
        titlebar.label(Messages.titleCase(tokens.name()));
        titlebar.setRect(0, 0, WIDTH, 0);
        add(titlebar);

        RenderedTextMultiline message = PixelScene.renderMultiline(Messages.get(this, "message"), 6);
        message.maxWidth(WIDTH);
        message.setPos(0, titlebar.bottom() + GAP);
        add(message);

        RedButton btnReward = new RedButton(Messages.get(this, "reward")) {
            @Override
            protected void onClick() {
                takeReward(arp, tokens, Arp.Quest.reward);
            }
        };
        btnReward.setRect(0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT);
        add(btnReward);

        resize(WIDTH, (int) btnReward.bottom());
    }

    private void takeReward(Arp arp, HardLightEmitter tokens, Item reward) {

        hide();

        tokens.detachAll(SpacebaseRun.hero.belongings.backpack);

        reward.identify();
        if (reward.doPickUp(SpacebaseRun.hero)) {
            GLog.i(Messages.get(SpacebaseRun.hero, "you_now_have", reward.name()));
        } else {
            SpacebaseRun.level.drop(reward, arp.pos).sprite.drop();
        }

        arp.flee();

        Arp.Quest.complete();
    }
}
