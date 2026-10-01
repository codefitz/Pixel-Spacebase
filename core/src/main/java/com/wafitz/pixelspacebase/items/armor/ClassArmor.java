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
package com.wafitz.pixelspacebase.items.armor;

import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Camoflage;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.WeakForcefield;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

abstract public class ClassArmor extends Armor {

    private static final String AC_SPECIAL = "SPECIAL";
    private static final String AC_JETPACK_ON = "JETPACK_ON";
    private static final String AC_JETPACK_OFF = "JETPACK_OFF";
    private static final float TIME_TO_SWITCH_JETPACK = 1f;
    private static final String HUNTER_JETPACK = "hunterJetpack";
    private static final String JETPACK_ON = "jetpackOn";
    private static final String HUNTER_TRACKING = "hunterTracking";
    private static final String LIFE_SUPPORT = "lifeSupport";

    {
        levelKnown = true;
        malfunctioningKnown = true;
        defaultAction = AC_SPECIAL;

        bones = false;
    }

    private int armorTier;
    private boolean hunterJetpack;
    private boolean jetpackOn;
    private boolean hunterTracking;
    private boolean lifeSupport;

    ClassArmor() {
        super(6);
    }

    public static ClassArmor upgrade(Hero owner, Armor armor) {

        ClassArmor classArmor = null;

        switch (owner.heroClass) {
            case COMMANDER:
                classArmor = new SpaceWizard();
                WeakForcefield forcefield = armor.checkForcefield();
                if (forcefield != null) {
                    classArmor.applyForcefield(forcefield);
                }
                break;
            case SHAPESHIFTER:
                classArmor = new Eldridge();
                break;
            case DM3000:
                classArmor = new DM3000Armor();
                break;
            case CAPTAIN:
                classArmor = new PowerSuit();
                break;
        }

        classArmor.hunterJetpack = armor.hasHunterJetpack();
        classArmor.jetpackOn = classArmor.hunterJetpack && armor.hunterJetpackOn();
        classArmor.hunterTracking = armor.hasHunterTracking();
        classArmor.lifeSupport = armor.providesLifeSupport();

        classArmor.level(armor.level());
        classArmor.armorTier = armor.tier;
        classArmor.enhance(armor.enhancement);

        return classArmor;
    }

    private static final String ARMOR_TIER = "armortier";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ARMOR_TIER, armorTier);
        bundle.put(HUNTER_JETPACK, hunterJetpack);
        bundle.put(JETPACK_ON, jetpackOn);
        bundle.put(HUNTER_TRACKING, hunterTracking);
        bundle.put(LIFE_SUPPORT, lifeSupport);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        //logic for pre-0.4.0 saves
        if (bundle.contains("DR")) {
            //we just assume tier-4 or tier-5 armor was used.
            int DR = bundle.getInt("DR");
            if (DR % 5 == 0) {
                level((DR - 10) / 5);
                armorTier = 5;
            } else {
                level((DR - 8) / 4);
                armorTier = 4;
            }
        } else {
            armorTier = bundle.getInt(ARMOR_TIER);
        }
        hunterJetpack = bundle.getBoolean(HUNTER_JETPACK);
        jetpackOn = hunterJetpack && bundle.getBoolean(JETPACK_ON);
        // Existing class-armor saves only recorded this state as a Hunter jetpack.
        hunterTracking = bundle.contains(HUNTER_TRACKING)
                ? bundle.getBoolean(HUNTER_TRACKING) : hunterJetpack;
        lifeSupport = bundle.contains(LIFE_SUPPORT)
                ? bundle.getBoolean(LIFE_SUPPORT) : hunterJetpack;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_DISARM);
        if (hero.HP >= 3 && isEquipped(hero)) {
            actions.add(AC_SPECIAL);
        }
        if (hunterJetpack && isEquipped(hero)) {
            actions.add(jetpackOn ? AC_JETPACK_OFF : AC_JETPACK_ON);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero) {
        if (isEquipped(hero) && hunterJetpack) {
            execute(hero, jetpackOn ? AC_JETPACK_OFF : AC_JETPACK_ON);
        } else {
            super.execute(hero);
        }
    }

    @Override
    public void execute(Hero hero, String action) {

        if (isEquipped(hero) && hunterJetpack
                && (AC_JETPACK_ON.equals(action) || AC_JETPACK_OFF.equals(action))) {
            jetpackOn = AC_JETPACK_ON.equals(action);
            GLog.i(Messages.get(HunterSpaceSuit.class, jetpackOn ? "jetpack_started" : "jetpack_stopped"));
            hero.spendAndNext(TIME_TO_SWITCH_JETPACK);
            updateQuickslot();
            HunterSpaceSuit.updateFlight(hero, jetpackOn, !jetpackOn);
            return;
        }

        super.execute(hero, action);

        if (action.equals(AC_SPECIAL)) {

            if (hero.HP < 3) {
                GLog.w(Messages.get(this, "low_hp"));
            } else if (!isEquipped(hero)) {
                GLog.w(Messages.get(this, "not_equipped", name()));
            } else {
                curUser = hero;
                Camoflage.dispel();
                doSpecial();
            }

        }
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        if (hunterJetpack) {
            HunterSpaceSuit.updateFlight((Hero) ch, jetpackOn, false);
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (!super.doUnequip(hero, collect, single)) return false;
        if (hunterJetpack) {
            jetpackOn = false;
            HunterSpaceSuit.updateFlight(hero, false, true);
        }
        return true;
    }

    @Override
    public void forceUnequip(Hero hero) {
        super.forceUnequip(hero);
        if (hunterJetpack) {
            jetpackOn = false;
            HunterSpaceSuit.updateFlight(hero, false, true);
        }
    }

    @Override
    public boolean providesLifeSupport() {
        return lifeSupport;
    }

    @Override
    public boolean hasHunterTracking() {
        return hunterTracking;
    }

    @Override
    public boolean hasHunterJetpack() {
        return hunterJetpack;
    }

    @Override
    public boolean hunterJetpackOn() {
        return hunterJetpack && jetpackOn;
    }

    @Override
    public String desc() {
        String description = super.desc();
        if (hunterJetpack) {
            description += "\n\n" + Messages.get(this, "hunter_jetpack_desc");
        }
        if (hunterTracking) {
            description += "\n\n" + Messages.get(this, "hunter_tracking_desc");
        }
        if (lifeSupport) {
            description += "\n\n" + Messages.get(this, "life_support_desc");
        }
        return description;
    }

    abstract public void doSpecial();

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);
        float effectiveTier = armorTier;
        if (enhancement != null) effectiveTier += enhancement.tierSTRAdjust();
        effectiveTier = Math.max(0, effectiveTier);

        //strength req decreases at +1,+3,+6,+10,etc.
        return (8 + Math.round(effectiveTier * 2)) - (int) (Math.sqrt(8 * lvl + 1) - 1) / 2;
    }

    @Override
    public int DRMax(int lvl) {
        int effectiveTier = armorTier;
        if (enhancement != null) effectiveTier += enhancement.tierDRAdjust();
        effectiveTier = Math.max(0, effectiveTier);

        return effectiveTier * (2 + lvl);
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int cost() {
        return 0;
    }

}
