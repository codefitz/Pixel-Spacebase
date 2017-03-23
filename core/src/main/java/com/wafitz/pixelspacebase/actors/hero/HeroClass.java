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
package com.wafitz.pixelspacebase.actors.hero;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.items.ExperimentalTech.HealingTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.SecurityOverride;
import com.wafitz.pixelspacebase.items.artifacts.StealthModule;
import com.wafitz.pixelspacebase.items.blasters.MissileBlaster;
import com.wafitz.pixelspacebase.items.scripts.MappingScript;
import com.wafitz.pixelspacebase.items.scripts.UpgradeScript;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Launcher;
import com.wafitz.pixelspacebase.items.weapon.melee.Dagger;
import com.wafitz.pixelspacebase.items.weapon.melee.Knuckles;
import com.wafitz.pixelspacebase.items.weapon.melee.Spanner;
import com.wafitz.pixelspacebase.items.weapon.missiles.Boomerang;
import com.wafitz.pixelspacebase.items.weapon.missiles.Dart;
import com.wafitz.pixelspacebase.messages.Messages;
import com.watabou.utils.Bundle;

public enum HeroClass {

    COMMANDER("commander"),
    DM3000("dm3000"),
    SHAPESHIFTER("shapeshifter"),
    CAPTAIN("captain");

    private String title;

    HeroClass(String title) {
        this.title = title;
    }

    public void initHero(Hero hero) {

        hero.heroClass = this;

        initCommon(hero);

        switch (this) {
            case COMMANDER:
                initCommander(hero);
                break;

            case DM3000:
                initDM3000(hero);
                break;

            case SHAPESHIFTER:
                initShapeshifter(hero);
                break;

            case CAPTAIN:
                initCaptain(hero);
                break;
        }

        hero.updateAwareness();
    }

    private static void initCommon(Hero hero) {
        // wafitz.v1 - Hero has just woken up - should be naked I reckon - go hunt for clothes and weapons.

        //if (!Dungeon.isChallenged(Challenges.NO_ARMOR))
        //    (hero.belongings.armor = new Uniform()).identify();

        //if (!Dungeon.isChallenged(Challenges.NO_FOOD))
        //    new Food().identify().collect();
    }

    public Badges.Badge masteryBadge() {
        switch (this) {
            case COMMANDER:
                return Badges.Badge.MASTERY_COMMANDER;
            case DM3000:
                return Badges.Badge.MASTERY_DM3000;
            case SHAPESHIFTER:
                return Badges.Badge.MASTERY_SHAPESHIFTER;
            case CAPTAIN:
                return Badges.Badge.MASTERY_CAPTAIN;
        }
        return null;
    }

    private static void initCommander(Hero hero) {
        (hero.belongings.weapon = new Spanner()).identify();
        Dart darts = new Dart(8);
        darts.identify().collect();

        // wafitz.v1 - Breaks naked plot
        /*if (Badges.isUnlocked(Badges.Badge.TUTORIAL_COMMANDER)) {
            if (!Dungeon.isChallenged(Challenges.NO_ARMOR))
                hero.belongings.armor.applyForcefield(new WeakForcefield());
            Dungeon.quickslot.setSlot(0, darts);
        } else {
            if (!Dungeon.isChallenged(Challenges.NO_ARMOR)) {
                WeakForcefield forcefield = new WeakForcefield();
                forcefield.collect();
                Dungeon.quickslot.setSlot(0, forcefield);
            }
            Dungeon.quickslot.setSlot(1, darts);
        }*/

        new HealingTech().setKnown();
    }

    private static void initDM3000(Hero hero) {
        DM3000Launcher launcher;

        if (Badges.isUnlocked(Badges.Badge.TUTORIAL_DM3000)) {
            launcher = new DM3000Launcher(new MissileBlaster());
        } else {
            launcher = new DM3000Launcher();
            new MissileBlaster().identify().collect();
        }

        (hero.belongings.weapon = launcher).identify();
        hero.belongings.weapon.activate(hero);

        Dungeon.quickslot.setSlot(0, launcher);

        new UpgradeScript().setKnown();
    }

    private static void initShapeshifter(Hero hero) {
        (hero.belongings.weapon = new Dagger()).identify();

        StealthModule cloak = new StealthModule();
        (hero.belongings.misc1 = cloak).identify();
        hero.belongings.misc1.activate(hero);

        Dart darts = new Dart(8);
        darts.identify().collect();

        Dungeon.quickslot.setSlot(0, cloak);
        Dungeon.quickslot.setSlot(1, darts);

        new MappingScript().setKnown();
    }

    private static void initCaptain(Hero hero) {

        (hero.belongings.weapon = new Knuckles()).identify();
        Boomerang boomerang = new Boomerang();
        boomerang.identify().collect();

        Dungeon.quickslot.setSlot(0, boomerang);

        new SecurityOverride().setKnown();
    }

    public String title() {
        return Messages.get(HeroClass.class, title);
    }

    public String spritesheet() {

        switch (this) {
            case COMMANDER:
                return Assets.COMMANDER;
            case DM3000:
                return Assets.DM3000;
            case SHAPESHIFTER:
                return Assets.SHAPESHIFTER;
            case CAPTAIN:
                return Assets.CAPTAIN;
        }

        return null;
    }

    public String[] perks() {

        switch (this) {
            case COMMANDER:
                return new String[]{
                        Messages.get(HeroClass.class, "commander_perk1"),
                        Messages.get(HeroClass.class, "commander_perk2"),
                        Messages.get(HeroClass.class, "commander_perk3"),
                        Messages.get(HeroClass.class, "commander_perk4"),
                        Messages.get(HeroClass.class, "commander_perk5"),
                };
            case DM3000:
                return new String[]{
                        Messages.get(HeroClass.class, "dm3000_perk1"),
                        Messages.get(HeroClass.class, "dm3000_perk2"),
                        Messages.get(HeroClass.class, "dm3000_perk3"),
                        Messages.get(HeroClass.class, "dm3000_perk4"),
                        Messages.get(HeroClass.class, "dm3000_perk5"),
                };
            case SHAPESHIFTER:
                return new String[]{
                        Messages.get(HeroClass.class, "shapeshifter_perk1"),
                        Messages.get(HeroClass.class, "shapeshifter_perk2"),
                        Messages.get(HeroClass.class, "shapeshifter_perk3"),
                        Messages.get(HeroClass.class, "shapeshifter_perk4"),
                        Messages.get(HeroClass.class, "shapeshifter_perk5"),
                        Messages.get(HeroClass.class, "shapeshifter_perk6"),
                };
            case CAPTAIN:
                return new String[]{
                        Messages.get(HeroClass.class, "captain_perk1"),
                        Messages.get(HeroClass.class, "captain_perk2"),
                        Messages.get(HeroClass.class, "captain_perk3"),
                        Messages.get(HeroClass.class, "captain_perk4"),
                        Messages.get(HeroClass.class, "captain_perk5"),
                };
        }

        return null;
    }

    private static final String CLASS = "class";

    public void storeInBundle(Bundle bundle) {
        bundle.put(CLASS, toString());
    }

    public static HeroClass restoreInBundle(Bundle bundle) {
        String value = bundle.getString(CLASS);
        return value.length() > 0 ? valueOf(value) : SHAPESHIFTER;
    }
}
