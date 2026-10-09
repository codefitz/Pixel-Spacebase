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
package com.wafitz.pixelspacebase.items.food;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.hero.HeroClass;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.EffectSprite;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Food extends Item {

    private static final float TIME_TO_EAT = 3f;

    public static final String AC_USE = "EAT";
    public static final String AC_EXTRACT = "EXTRACT";

    private Float eatTimeOverride = null;

    public float energy = Hunger.HUNGRY;
    public String message = Messages.get(this, "eat_msg");

    public int hornValue = 3;

    {
        stackable = true;
        image = ItemSpriteSheet.RATION;

        bones = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(hero.heroClass == HeroClass.DM3000 ? AC_EXTRACT : AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        boolean robot = hero.heroClass == HeroClass.DM3000;
        if (action.equals(AC_USE) || robot && action.equals(AC_EXTRACT)) {

            detach(hero.belongings.backpack);

            if (!robot) {
                (hero.buff(Hunger.class)).satisfy(energy);
                GLog.i(message);
            }

            switch (hero.heroClass) {
                case COMMANDER:
                    if (hero.HP < hero.HT) {
                        hero.HP = Math.min(hero.HP + 5, hero.HT);
                        hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                    }
                    break;
                case DM3000:
                    int repair = Math.min(5, Math.max(0, hero.HT - hero.HP));
                    hero.HP += repair;
                    if (repair > 0) hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                    GLog.i(Messages.get(Food.class, "extract_msg", name(), repair));
                    break;
                case SHAPESHIFTER:
                case CAPTAIN:
                    break;
            }

            hero.sprite.operate(hero.pos);
            hero.busy();
            if (!robot) EffectSprite.show(hero, EffectSprite.FOOD);
            Sample.INSTANCE.play(robot ? Assets.SND_DRINK : Assets.SND_EAT);

            hero.spend(eatTimeOverride == null ? TIME_TO_EAT : eatTimeOverride);

            Statistics.foodEaten++;
            Badges.validateFoodEaten();

        }
    }

    public void emergencyEat(Hero hero) {
        try {
            eatTimeOverride = 1f;
            execute(hero, AC_USE);
        } finally {
            eatTimeOverride = null;
        }
    }

    @Override
    public String info() {
        return withWaterExtractionInfo(super.info());
    }

    protected String withWaterExtractionInfo(String info) {
        return SpacebaseRun.hero != null && SpacebaseRun.hero.heroClass == HeroClass.DM3000
                ? info + "\n\n" + Messages.get(Food.class, "extract_desc") : info;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int cost() {
        return 10 * quantity;
    }
}
