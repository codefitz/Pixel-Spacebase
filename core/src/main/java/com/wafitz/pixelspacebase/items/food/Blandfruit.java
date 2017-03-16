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
import com.wafitz.pixelspacebase.actors.buffs.FlameOn;
import com.wafitz.pixelspacebase.actors.buffs.Hunger;
import com.wafitz.pixelspacebase.actors.buffs.Lockdown;
import com.wafitz.pixelspacebase.actors.buffs.Toxic;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperienceTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.FrostTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.HealingTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.InvisibilityTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.LevitationTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.LiquidFlameTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.MindVisionTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ParalyticGasTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.PurityTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.StrengthTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ToxicGasTech;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.triggers.Trigger.Gadget;
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

            if (experimentalTechAttrib instanceof FrostTech) {
                GLog.i(Messages.get(this, "ice_msg"));
                FrozenCarpaccio.effect(hero);
            } else if (experimentalTechAttrib instanceof LiquidFlameTech) {
                GLog.i(Messages.get(this, "fire_msg"));
                Buff.affect(hero, FlameOn.class).set(FlameOn.DURATION);
            } else if (experimentalTechAttrib instanceof ToxicGasTech) {
                GLog.i(Messages.get(this, "toxic_msg"));
                Buff.affect(hero, Toxic.class).set(Toxic.DURATION);
            } else if (experimentalTechAttrib instanceof ParalyticGasTech) {
                GLog.i(Messages.get(this, "para_msg"));
                Buff.affect(hero, Lockdown.class, Lockdown.DURATION);
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

    public Item cook(Gadget gadget) {

        try {
            return imbueExperimentalTech((ExperimentalTech) gadget.alchemyClass.newInstance());
        } catch (Exception e) {
            PixelSpacebase.reportException(e);
            return null;
        }

    }

    private Item imbueExperimentalTech(ExperimentalTech experimentalTech) {

        experimentalTechAttrib = experimentalTech;
        experimentalTechAttrib.ownedByFruit = true;

        experimentalTechAttrib.image = ItemSpriteSheet.BLANDFRUIT;

        if (experimentalTechAttrib instanceof HealingTech) {
            name = Messages.get(this, "sunfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x2EE62E);
        } else if (experimentalTechAttrib instanceof StrengthTech) {
            name = Messages.get(this, "rotfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xCC0022);
        } else if (experimentalTechAttrib instanceof ParalyticGasTech) {
            name = Messages.get(this, "earthfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x67583D);
        } else if (experimentalTechAttrib instanceof InvisibilityTech) {
            name = Messages.get(this, "blindfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xE5D273);
        } else if (experimentalTechAttrib instanceof LiquidFlameTech) {
            name = Messages.get(this, "firefruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xFF7F00);
        } else if (experimentalTechAttrib instanceof FrostTech) {
            name = Messages.get(this, "icefruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x66B3FF);
        } else if (experimentalTechAttrib instanceof MindVisionTech) {
            name = Messages.get(this, "fadefruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xB8E6CF);
        } else if (experimentalTechAttrib instanceof ToxicGasTech) {
            name = Messages.get(this, "sorrowfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xA15CE5);
        } else if (experimentalTechAttrib instanceof LevitationTech) {
            name = Messages.get(this, "stormfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x1C3A57);
        } else if (experimentalTechAttrib instanceof PurityTech) {
            name = Messages.get(this, "dreamfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0x8E2975);
        } else if (experimentalTechAttrib instanceof ExperienceTech) {
            name = Messages.get(this, "starfruit");
            experimentalTechGlow = new ItemSprite.Glowing(0xA79400);
        }

        return this;
    }

    private static final String EXPERIMENTAILTECHATTRIB = "experimentaltechattrib";

    @Override
    public void cast(final Hero user, int dst) {
        if (experimentalTechAttrib instanceof LiquidFlameTech ||
                experimentalTechAttrib instanceof ToxicGasTech ||
                experimentalTechAttrib instanceof ParalyticGasTech ||
                experimentalTechAttrib instanceof FrostTech ||
                experimentalTechAttrib instanceof LevitationTech ||
                experimentalTechAttrib instanceof PurityTech) {
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
