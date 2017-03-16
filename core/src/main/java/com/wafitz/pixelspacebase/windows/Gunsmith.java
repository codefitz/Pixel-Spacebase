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
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.quest.CorpseDust;
import com.wafitz.pixelspacebase.items.quest.Embers;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.triggers.Rotberry;
import com.wafitz.pixelspacebase.ui.RedButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Window;
import com.wafitz.pixelspacebase.utils.GLog;

public class Gunsmith extends Window {

    private static final int WIDTH = 120;
    private static final int BTN_HEIGHT = 20;
    private static final float GAP = 2;

    public Gunsmith(final com.wafitz.pixelspacebase.actors.mobs.npcs.Gunsmith gunsmith, final Item item) {

        super();

        IconTitle titlebar = new IconTitle();
        titlebar.icon(new ItemSprite(item.image(), null));
        titlebar.label(Messages.titleCase(item.name()));
        titlebar.setRect(0, 0, WIDTH, 0);
        add(titlebar);

        String msg = "";
        if (item instanceof CorpseDust) {
            msg = Messages.get(this, "dust");
        } else if (item instanceof Embers) {
            msg = Messages.get(this, "ember");
        } else if (item instanceof Rotberry.Gadget) {
            msg = Messages.get(this, "berry");
        }

        RenderedTextMultiline message = PixelScene.renderMultiline(msg, 6);
        message.maxWidth(WIDTH);
        message.setPos(0, titlebar.bottom() + GAP);
        add(message);

        RedButton btnBlaster1 = new RedButton(com.wafitz.pixelspacebase.actors.mobs.npcs.Gunsmith.Quest.blaster1.name()) {
            @Override
            protected void onClick() {
                selectReward(gunsmith, item, com.wafitz.pixelspacebase.actors.mobs.npcs.Gunsmith.Quest.blaster1);
            }
        };
        btnBlaster1.setRect(0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT);
        add(btnBlaster1);

        RedButton btnBlaster2 = new RedButton(com.wafitz.pixelspacebase.actors.mobs.npcs.Gunsmith.Quest.blaster2.name()) {
            @Override
            protected void onClick() {
                selectReward(gunsmith, item, com.wafitz.pixelspacebase.actors.mobs.npcs.Gunsmith.Quest.blaster2);
            }
        };
        btnBlaster2.setRect(0, btnBlaster1.bottom() + GAP, WIDTH, BTN_HEIGHT);
        add(btnBlaster2);

        resize(WIDTH, (int) btnBlaster2.bottom());
    }

    private void selectReward(com.wafitz.pixelspacebase.actors.mobs.npcs.Gunsmith gunsmith, Item item, Blaster reward) {

        hide();

        item.detach(Dungeon.hero.belongings.backpack);

        reward.identify();
        if (reward.doPickUp(Dungeon.hero)) {
            GLog.i(Messages.get(Dungeon.hero, "you_now_have", reward.name()));
        } else {
            Dungeon.level.drop(reward, gunsmith.pos).sprite.drop();
        }

        gunsmith.yell(Messages.get(this, "farewell", Dungeon.hero.givenName()));
        gunsmith.destroy();

        gunsmith.sprite.die();

        com.wafitz.pixelspacebase.actors.mobs.npcs.Gunsmith.Quest.complete();
    }
}
