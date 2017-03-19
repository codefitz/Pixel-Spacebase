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
package com.wafitz.pixelspacebase.items;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.ui.RedButton;
import com.wafitz.pixelspacebase.ui.RenderedTextMultiline;
import com.wafitz.pixelspacebase.ui.Window;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.IconTitle;
import com.wafitz.pixelspacebase.windows.WndContainer;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Weightstone extends Item {

    private static final float TIME_TO_APPLY = 2;

    private static final String AC_APPLY = "APPLY";

    {
        image = ItemSpriteSheet.WEIGHT;

        stackable = true;

        bones = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_APPLY);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_APPLY)) {

            curUser = hero;
            GameScene.selectItem(itemSelector, WndContainer.Mode.WEAPON, Messages.get(this, "select"));

        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    private void apply(Weapon weapon, boolean forSpeed) {

        detach(curUser.belongings.backpack);

        if (forSpeed) {
            weapon.imbue = Weapon.Imbue.LIGHT;
            GLog.p(Messages.get(this, "light"));
        } else {
            weapon.imbue = Weapon.Imbue.HEAVY;
            GLog.p(Messages.get(this, "heavy"));
        }

        curUser.sprite.operate(curUser.pos);
        Sample.INSTANCE.play(Assets.SND_MISS);

        curUser.spend(TIME_TO_APPLY);
        curUser.busy();
    }

    @Override
    public int cost() {
        return 50 * quantity;
    }

    private final WndContainer.Listener itemSelector = new WndContainer.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null) {
                GameScene.show(new WndBalance((Weapon) item));
            }
        }
    };

    private class WndBalance extends Window {

        private static final int WIDTH = 120;
        private static final int MARGIN = 2;
        private static final int BUTTON_WIDTH = WIDTH - MARGIN * 2;
        private static final int BUTTON_HEIGHT = 20;

        public WndBalance(final Weapon weapon) {
            super();

            IconTitle titlebar = new IconTitle(weapon);
            titlebar.setRect(0, 0, WIDTH, 0);
            add(titlebar);

            RenderedTextMultiline tfMesage = PixelScene.renderMultiline(Messages.get(this, "choice"), 8);
            tfMesage.maxWidth(WIDTH - MARGIN * 2);
            tfMesage.setPos(MARGIN, titlebar.bottom() + MARGIN);
            add(tfMesage);

            float pos = tfMesage.top() + tfMesage.height();

            if (weapon.imbue != Weapon.Imbue.LIGHT) {
                RedButton btnSpeed = new RedButton(Messages.get(this, "light")) {
                    @Override
                    protected void onClick() {
                        hide();
                        Weightstone.this.apply(weapon, true);
                    }
                };
                btnSpeed.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
                add(btnSpeed);

                pos = btnSpeed.bottom();
            }

            if (weapon.imbue != Weapon.Imbue.HEAVY) {
                RedButton btnAccuracy = new RedButton(Messages.get(this, "heavy")) {
                    @Override
                    protected void onClick() {
                        hide();
                        Weightstone.this.apply(weapon, false);
                    }
                };
                btnAccuracy.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
                add(btnAccuracy);

                pos = btnAccuracy.bottom();
            }

            RedButton btnCancel = new RedButton(Messages.get(this, "cancel")) {
                @Override
                protected void onClick() {
                    hide();
                }
            };
            btnCancel.setRect(MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT);
            add(btnCancel);

            resize(WIDTH, (int) btnCancel.bottom() + MARGIN);
        }

        protected void onSelect(int index) {
        }
    }
}