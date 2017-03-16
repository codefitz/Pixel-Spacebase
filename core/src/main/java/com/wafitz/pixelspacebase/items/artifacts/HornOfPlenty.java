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
package com.wafitz.pixelspacebase.items.artifacts;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.Statistics;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.buffs.Recharging;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.SpellSprite;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.food.Blandfruit;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.items.scripts.RechargingScript;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class HornOfPlenty extends Artifact {


    {
        image = ItemSpriteSheet.ARTIFACT_HORN1;

        levelCap = 30;

        charge = 0;
        partialCharge = 0;
        chargeCap = 10 + visiblyUpgraded();

        defaultAction = AC_EAT;
    }

    private static final float TIME_TO_EAT = 3f;

    private static final String AC_EAT = "EAT";
    private static final String AC_STORE = "STORE";

    protected WndBag.Mode mode = WndBag.Mode.FOOD;

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (isEquipped(hero) && charge > 0)
            actions.add(AC_EAT);
        if (isEquipped(hero) && level() < 30 && !malfunctioning)
            actions.add(AC_STORE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_EAT)) {

            if (!isEquipped(hero)) GLog.i(Messages.get(Artifact.class, "need_to_equip"));
            else if (charge == 0) GLog.i(Messages.get(this, "no_food"));
            else {
                //consume as many
                int chargesToUse = Math.max(1, hero.buff(Hunger.class).hunger() / (int) (Hunger.STARVING / 10));
                if (chargesToUse > charge) chargesToUse = charge;
                hero.buff(Hunger.class).satisfy((Hunger.STARVING / 10) * chargesToUse);

                //if you get at least 80 food energy from the horn
                switch (hero.heroClass) {
                    case COMMANDER:
                        if (hero.HP < hero.HT) {
                            hero.HP = Math.min(hero.HP + 5, hero.HT);
                            hero.sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
                        }
                        break;
                    case DM3000:
                        //1 charge
                        Buff.affect(hero, Recharging.class, 4f);
                        RechargingScript.charge(hero);
                        break;
                    case SHAPESHIFTER:
                    case CAPTAIN:
                        break;
                }

                Statistics.foodEaten++;

                charge -= chargesToUse;

                hero.sprite.operate(hero.pos);
                hero.busy();
                SpellSprite.show(hero, SpellSprite.FOOD);
                Sample.INSTANCE.play(Assets.SND_EAT);
                GLog.i(Messages.get(this, "eat"));

                hero.spend(TIME_TO_EAT);

                Badges.validateFoodEaten();

                if (charge >= 15) image = ItemSpriteSheet.ARTIFACT_HORN4;
                else if (charge >= 10) image = ItemSpriteSheet.ARTIFACT_HORN3;
                else if (charge >= 5) image = ItemSpriteSheet.ARTIFACT_HORN2;

                updateQuickslot();
            }

        } else if (action.equals(AC_STORE)) {

            GameScene.selectItem(itemSelector, mode, Messages.get(this, "prompt"));

        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new hornRecharge();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (!malfunctioning) {
                if (level() < levelCap)
                    desc += "\n\n" + Messages.get(this, "desc_hint");
            } else {
                desc += "\n\n" + Messages.get(this, "desc_malfunctioning");
            }
        }

        return desc;
    }

    @Override
    public void level(int value) {
        super.level(value);
        chargeCap = 10 + visiblyUpgraded();
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        chargeCap = 10 + visiblyUpgraded();
        return this;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (charge >= 15) image = ItemSpriteSheet.ARTIFACT_HORN4;
        else if (charge >= 10) image = ItemSpriteSheet.ARTIFACT_HORN3;
        else if (charge >= 5) image = ItemSpriteSheet.ARTIFACT_HORN2;
    }

    public class hornRecharge extends ArtifactBuff {

        public void gainCharge(float levelPortion) {
            if (charge < chargeCap) {

                //generates 0.25x max hunger value every hero level, +0.035x max value per horn level
                //to a max of 1.3x max hunger value per hero level
                //This means that a standard ration will be recovered in ~7.15 hero levels
                partialCharge += Hunger.STARVING * levelPortion * (0.25f + (0.035f * level()));

                //charge is in increments of 1/10 max hunger value.
                while (partialCharge >= Hunger.STARVING / 10) {
                    charge++;
                    partialCharge -= Hunger.STARVING / 10;

                    if (charge >= 15) image = ItemSpriteSheet.ARTIFACT_HORN4;
                    else if (charge >= 10) image = ItemSpriteSheet.ARTIFACT_HORN3;
                    else if (charge >= 5) image = ItemSpriteSheet.ARTIFACT_HORN2;
                    else image = ItemSpriteSheet.ARTIFACT_HORN1;

                    if (charge == chargeCap) {
                        GLog.p(Messages.get(HornOfPlenty.class, "full"));
                        partialCharge = 0;
                    }

                    updateQuickslot();
                }
            } else
                partialCharge = 0;
        }

    }

    protected static WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect(Item item) {
            if (item != null && item instanceof Food) {
                if (item instanceof Blandfruit && ((Blandfruit) item).experimentalTechAttrib == null) {
                    GLog.w(Messages.get(HornOfPlenty.class, "reject"));
                } else {
                    Hero hero = Dungeon.hero;
                    hero.sprite.operate(hero.pos);
                    hero.busy();
                    hero.spend(TIME_TO_EAT);

                    curItem.upgrade(((Food) item).hornValue);
                    if (curItem.level() >= 30) {
                        curItem.level(30);
                        GLog.p(Messages.get(HornOfPlenty.class, "maxlevel"));
                    } else
                        GLog.p(Messages.get(HornOfPlenty.class, "levelup"));
                    item.detach(hero.belongings.backpack);
                }

            }
        }
    };
}
