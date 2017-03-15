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

import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.EarthImbue;
import com.wafitz.pixelspacebase.actors.buffs.FireImbue;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.buffs.ToxicImbue;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfExperience;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfFrost;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfHealing;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfInvisibility;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfLevitation;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfLiquidFlame;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfMindVision;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfParalyticGas;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfPurity;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfStrength;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTechOfToxicGas;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.plants.Plant.Seed;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

public class Blandfruit extends Food {

    public ExperimentalTech experimentalTechAttrib = null;
    private ItemSprite.Glowing experimentalTechGlow = null;

    {
        stackable = true;
        image = ItemSpriteSheet.BLANDFRUIT;

        //only applies when blandfruit is cooked
        energy = Hunger.STARVING;
        hornValue = 6;

        bones = true;
    }

    @Override
    public boolean isSimilar(Item item) {
        if (item instanceof Blandfruit) {
            if (experimentalTechAttrib == null) {
                if (((Blandfruit) item).experimentalTechAttrib == null)
                    return true;
            } else if (((Blandfruit) item).experimentalTechAttrib != null) {
                if (((Blandfruit) item).experimentalTechAttrib.getClass() == experimentalTechAttrib.getClass())
                    return true;
            }
        }
        return false;
    }

    @Override
    public void execute(Hero hero, String action) {

        if (action.equals(AC_EAT) && experimentalTechAttrib == null) {

            GLog.w(Messages.get(this, "raw"));
            return;

        }

        super.execute(hero, action);

        if (action.equals(AC_EAT) && experimentalTechAttrib != null) {

            if (experimentalTechAttrib instanceof ExperimentalTechOfFrost) {
                GLog.i(Messages.get(this, "ice_msg"));
                FrozenCarpaccio.effect(hero);
            } else if (experimentalTechAttrib instanceof ExperimentalTechOfLiquidFlame) {
                GLog.i(Messages.get(this, "fire_msg"));
                Buff.affect(hero, FireImbue.class).set(FireImbue.DURATION);
            } else if (experimentalTechAttrib instanceof ExperimentalTechOfToxicGas) {
                GLog.i(Messages.get(this, "toxic_msg"));
                Buff.affect(hero, ToxicImbue.class).set(ToxicImbue.DURATION);
            } else if (experimentalTechAttrib instanceof ExperimentalTechOfParalyticGas) {
                GLog.i(Messages.get(this, "para_msg"));
                Buff.affect(hero, EarthImbue.class, EarthImbue.DURATION);
            } else {
                experimentalTechAttrib.apply(hero);
            }

        }
    }

    @Override
    public String desc() {
        if (experimentalTechAttrib == null) return super.desc();
        else return Messages.get(this, "desc_cooked");
    }

    @Override
    public int price() {
        return 20 * quantity;
    }

    public Item cook(Seed seed) {

        try {
            return imbueExperimentalTech((ExperimentalTech) seed.alchemyClass.newInstance());
        } catch (Exception e) {
            PixelSpacebase.reportException(e);
            return null;
        }

    }

    private Item imbueExperimentalTech(ExperimentalTech experimentalTech) {

        experimentalTechAttrib = experimentalTech;
        experimentalTechAttrib.ownedByFruit = true;

        experimentalTechAttrib.image = ItemSpriteSheet.BLANDFRUIT;

        if (experimentalTechAttrib instanceof ExperimentalTechOfHealing) {
            name = Messages.get(this, "sunfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x2EE62E);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfStrength) {
            name = Messages.get(this, "rotfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xCC0022);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfParalyticGas) {
            name = Messages.get(this, "earthfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x67583D);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfInvisibility) {
            name = Messages.get(this, "blindfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xE5D273);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfLiquidFlame) {
            name = Messages.get(this, "firefruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xFF7F00);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfFrost) {
            name = Messages.get(this, "icefruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x66B3FF);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfMindVision) {
            name = Messages.get(this, "fadefruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xB8E6CF);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfToxicGas) {
            name = Messages.get(this, "sorrowfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xA15CE5);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfLevitation) {
            name = Messages.get(this, "stormfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x1C3A57);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfPurity) {
            name = Messages.get(this, "dreamfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x8E2975);
        } else if (experimentalTechAttrib instanceof ExperimentalTechOfExperience) {
            name = Messages.get(this, "starfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xA79400);
        }

        return this;
    }

    private static final String EXPERIMENTAILTECHATTRIB = "experimentaltechattrib";

    @Override
    public void cast(final Hero user, int dst) {
        if (experimentalTechAttrib instanceof ExperimentalTechOfLiquidFlame ||
                experimentalTechAttrib instanceof ExperimentalTechOfToxicGas ||
                experimentalTechAttrib instanceof ExperimentalTechOfParalyticGas ||
                experimentalTechAttrib instanceof ExperimentalTechOfFrost ||
                experimentalTechAttrib instanceof ExperimentalTechOfLevitation ||
                experimentalTechAttrib instanceof ExperimentalTechOfPurity) {
            experimentalTechAttrib.cast(user, dst);
            detach(user.belongings.backpack);
        } else {
            super.cast(user, dst);
        }

    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(EXPERIMENTAILTECHATTRIB, experimentalTechAttrib);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(EXPERIMENTAILTECHATTRIB)) {
            imbueExperimentalTech((ExperimentalTech) bundle.get(EXPERIMENTAILTECHATTRIB));
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return experimentalTechGlow;
    }

}
