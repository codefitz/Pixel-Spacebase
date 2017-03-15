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
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.actors.buffs.Berserk;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroSubClass;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.SpellSprite;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndChooseWay;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class TomeOfMastery extends Item {

    private static final float TIME_TO_READ = 10;

    private static final String AC_READ = "READ";

    {
        stackable = false;
        image = ItemSpriteSheet.MASTERY;

        unique = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_READ);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_READ)) {

            curUser = hero;

            HeroSubClass way1 = null;
            HeroSubClass way2 = null;
            switch (hero.heroClass) {
                case COMMANDER:
                    way1 = HeroSubClass.GLADIATOR;
                    way2 = HeroSubClass.BERSERKER;
                    break;
                case DM3000:
                    way1 = HeroSubClass.BATTLEMAGE;
                    way2 = HeroSubClass.WARLOCK;
                    break;
                case SHAPESHIFTER:
                    way1 = HeroSubClass.FREERUNNER;
                    way2 = HeroSubClass.ASSASSIN;
                    break;
                case CAPTAIN:
                    way1 = HeroSubClass.SNIPER;
                    way2 = HeroSubClass.WARDEN;
                    break;
            }
            GameScene.show(new WndChooseWay(this, way1, way2));

        }
    }

    @Override
    public boolean doPickUp(Hero hero) {
        Badges.validateMastery();
        return super.doPickUp(hero);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public void choose(HeroSubClass way) {

        detach(curUser.belongings.backpack);

        curUser.spend(TomeOfMastery.TIME_TO_READ);
        curUser.busy();

        curUser.subClass = way;

        curUser.sprite.operate(curUser.pos);
        Sample.INSTANCE.play(Assets.SND_MASTERY);

        SpellSprite.show(curUser, SpellSprite.MASTERY);
        curUser.sprite.emitter().burst(Speck.factory(Speck.MASTERY), 12);
        GLog.w(Messages.get(this, "way", way.title()));

        if (way == HeroSubClass.BERSERKER) {
            Buff.affect(curUser, Berserk.class);
        }
    }
}
