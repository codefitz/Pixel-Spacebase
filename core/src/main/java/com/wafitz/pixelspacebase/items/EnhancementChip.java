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
import com.wafitz.pixelspacebase.effects.Enhancing;
import com.wafitz.pixelspacebase.effects.particles.PurpleParticle;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndContainer;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class EnhancementChip extends Item {

    private static final float TIME_TO_INSCRIBE = 2;

    private static final String AC_ENHANCE = "ENHANCE";

    {
        image = ItemSpriteSheet.ENHANCEMENTCHIP;

        stackable = true;

        bones = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_ENHANCE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_ENHANCE)) {

            curUser = hero;
            GameScene.selectItem(itemSelector, WndContainer.Mode.ARMOR, Messages.get(this, "prompt"));

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

    private void inscribe(Armor armor) {

        if (!armor.isIdentified()) {
            GLog.w(Messages.get(this, "identify", armor.name()));
            return;
        } else if (armor.malfunctioning || armor.hasMalfunctionEnhancement()) {
            GLog.w(Messages.get(this, "malfunctioning", armor.name()));
            return;
        }

        detach(curUser.belongings.backpack);

        GLog.w(Messages.get(this, "enhanced"));

        armor.enhance();

        curUser.sprite.operate(curUser.pos);
        curUser.sprite.centerEmitter().start(PurpleParticle.BURST, 0.05f, 10);
        Enhancing.show(curUser, armor);
        Sample.INSTANCE.play(Assets.SND_BURNING);

        curUser.spend(TIME_TO_INSCRIBE);
        curUser.busy();
    }

    @Override
    public int cost() {
        return 30 * quantity;
    }

    private final WndContainer.Listener itemSelector = new WndContainer.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null) {
                EnhancementChip.this.inscribe((Armor) item);
            }
        }
    };
}
