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

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.KindofMisc;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Artifact extends KindofMisc {

    protected Buff passiveBuff;
    Buff activeBuff;

    //level is used internally to track upgrades to artifacts, size/logic varies per artifact.
    //already inherited from item superclass
    //exp is used to count progress towards levels for some artifacts
    protected int exp = 0;
    //levelCap is the artifact's maximum level
    protected int levelCap = 0;

    //the current artifact charge
    protected int charge = 0;
    //the build towards next charge, usually rolls over at 1.
    //better to keep charge as an int and use a separate float than casting.
    protected float partialCharge = 0;
    //the maximum charge, varies per artifact, not all artifacts use this.
    int chargeCap = 0;

    //used by some artifacts to keep track of duration of effects or cooldowns to use.
    protected int cooldown = 0;

    @Override
    public boolean doEquip(final Hero hero) {

        if ((hero.belongings.misc1 != null && hero.belongings.misc1.getClass() == this.getClass())
                || (hero.belongings.misc2 != null && hero.belongings.misc2.getClass() == this.getClass())) {

            GLog.w(Messages.get(Artifact.class, "limited_to_two"));
            return false;

        } else {

            if (super.doEquip(hero)) {

                identify();
                return true;

            } else {

                return false;

            }

        }

    }

    public void activate(Char ch) {
        passiveBuff = passiveBuff();
        passiveBuff.attachTo(ch);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {

            passiveBuff.detach();
            passiveBuff = null;

            if (activeBuff != null) {
                activeBuff.detach();
                activeBuff = null;
            }

            return true;

        } else {

            return false;

        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int visiblyUpgraded() {
        return levelKnown ? Math.round((level() * 10) / (float) levelCap) : 0;
    }

    //transfers upgrades from another artifact, transfer level will equal the displayed level
    public void transferUpgrade(int transferLvl) {
        upgrade(Math.round((float) (transferLvl * levelCap) / 10));
    }

    @Override
    public String info() {
        if (malfunctioning && malfunctioningKnown && !isEquipped(Dungeon.hero)) {

            return desc() + "\n\n" + Messages.get(Artifact.class, "malfunction_known");

        } else {

            return desc();

        }
    }

    @Override
    public String status() {

        //display the current cooldown
        if (cooldown != 0)
            return Messages.format("%d", cooldown);

        //display as percent
        if (chargeCap == 100)
            return Messages.format("%d%%", charge);

        //display as #/#
        if (chargeCap > 0)
            return Messages.format("%d/%d", charge, chargeCap);

        //if there's no cap -
        //- but there is charge anyway, display that charge
        if (charge != 0)
            return Messages.format("%d", charge);

        //otherwise, if there's no charge, return null.
        return null;
    }

    //converts class names to be more concise and readable.
    String convertName(String className) {
        //removes known redundant parts of names.
        className = className.replaceFirst("Script|Tech", "");

        //inserts a space infront of every uppercase character
        className = className.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");

        return className;
    }

    @Override
    public Item random() {
        if (Random.Float() < 0.3f) {
            malfunctioning = true;
        }
        return this;
    }

    @Override
    public int cost() {
        int price = 100;
        if (level() > 0)
            price += 20 * visiblyUpgraded();
        if (malfunctioning && malfunctioningKnown) {
            price /= 2;
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }


    protected ArtifactBuff passiveBuff() {
        return null;
    }

    protected ArtifactBuff activeBuff() {
        return null;
    }

    public class ArtifactBuff extends Buff {

        public int itemLevel() {
            return level();
        }

        public boolean isMalfunctioning() {
            return malfunctioning;
        }

    }

    private static final String IMAGE = "image";
    private static final String EXP = "exp";
    private static final String CHARGE = "charge";
    private static final String PARTIALCHARGE = "partialcharge";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(EXP, exp);
        bundle.put(CHARGE, charge);
        bundle.put(PARTIALCHARGE, partialCharge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        exp = bundle.getInt(EXP);
        charge = bundle.getInt(CHARGE);
        partialCharge = bundle.getFloat(PARTIALCHARGE);
    }
}
