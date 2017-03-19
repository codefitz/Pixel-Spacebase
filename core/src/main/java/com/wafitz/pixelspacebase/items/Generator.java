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

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.PixelSpacebase;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Hologram;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperienceTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.FireTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.FrostTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.HealingTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.InvisibilityTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ParalyzingTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.PolymerMembrane;
import com.wafitz.pixelspacebase.items.ExperimentalTech.PowerTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.RocketTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.SecurityTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.StrengthTech;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ToxicGasTech;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.armor.HoverPod;
import com.wafitz.pixelspacebase.items.armor.HunterSpaceSuit;
import com.wafitz.pixelspacebase.items.armor.Loader;
import com.wafitz.pixelspacebase.items.armor.SpaceSuit;
import com.wafitz.pixelspacebase.items.armor.Uniform;
import com.wafitz.pixelspacebase.items.artifacts.AlienDNA;
import com.wafitz.pixelspacebase.items.artifacts.Artifact;
import com.wafitz.pixelspacebase.items.artifacts.BuggyCompiler;
import com.wafitz.pixelspacebase.items.artifacts.GnollTechShield;
import com.wafitz.pixelspacebase.items.artifacts.GravityGun;
import com.wafitz.pixelspacebase.items.artifacts.HoloPad;
import com.wafitz.pixelspacebase.items.artifacts.LloydsBeacon;
import com.wafitz.pixelspacebase.items.artifacts.MakersToolkit;
import com.wafitz.pixelspacebase.items.artifacts.McGyvrModule;
import com.wafitz.pixelspacebase.items.artifacts.StealthModule;
import com.wafitz.pixelspacebase.items.artifacts.StrongForcefield;
import com.wafitz.pixelspacebase.items.artifacts.SurveyorModule;
import com.wafitz.pixelspacebase.items.artifacts.SurvivalModule;
import com.wafitz.pixelspacebase.items.artifacts.TimeFolder;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.blasters.Disintergrator;
import com.wafitz.pixelspacebase.items.blasters.EMP;
import com.wafitz.pixelspacebase.items.blasters.FireBlaster;
import com.wafitz.pixelspacebase.items.blasters.FreezeBlaster;
import com.wafitz.pixelspacebase.items.blasters.LightBlaster;
import com.wafitz.pixelspacebase.items.blasters.LightningBlaster;
import com.wafitz.pixelspacebase.items.blasters.MindBlaster;
import com.wafitz.pixelspacebase.items.blasters.MissileBlaster;
import com.wafitz.pixelspacebase.items.blasters.TransfusionBlaster;
import com.wafitz.pixelspacebase.items.blasters.VenomBlaster;
import com.wafitz.pixelspacebase.items.blasters.WaveBlaster;
import com.wafitz.pixelspacebase.items.containers.Container;
import com.wafitz.pixelspacebase.items.food.Food;
import com.wafitz.pixelspacebase.items.food.MysteryMeat;
import com.wafitz.pixelspacebase.items.food.Pasty;
import com.wafitz.pixelspacebase.items.modules.AccuracyModule;
import com.wafitz.pixelspacebase.items.modules.AttackModule;
import com.wafitz.pixelspacebase.items.modules.BallisticModule;
import com.wafitz.pixelspacebase.items.modules.ElementsModule;
import com.wafitz.pixelspacebase.items.modules.EvasionModule;
import com.wafitz.pixelspacebase.items.modules.ForceModule;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.modules.PowerModule;
import com.wafitz.pixelspacebase.items.modules.SpeedModule;
import com.wafitz.pixelspacebase.items.modules.SteelModule;
import com.wafitz.pixelspacebase.items.modules.TargetingModule;
import com.wafitz.pixelspacebase.items.modules.TechModule;
import com.wafitz.pixelspacebase.items.scripts.EchoLocationScript;
import com.wafitz.pixelspacebase.items.scripts.EnhancementScript;
import com.wafitz.pixelspacebase.items.scripts.FixScript;
import com.wafitz.pixelspacebase.items.scripts.IdentifyScript;
import com.wafitz.pixelspacebase.items.scripts.KnockoutScript;
import com.wafitz.pixelspacebase.items.scripts.MappingScript;
import com.wafitz.pixelspacebase.items.scripts.PsionicBlastScript;
import com.wafitz.pixelspacebase.items.scripts.RechargingScript;
import com.wafitz.pixelspacebase.items.scripts.Script;
import com.wafitz.pixelspacebase.items.scripts.TeleportationScript;
import com.wafitz.pixelspacebase.items.scripts.TerrorScript;
import com.wafitz.pixelspacebase.items.scripts.UpgradeScript;
import com.wafitz.pixelspacebase.items.scripts.WeakCloneScript;
import com.wafitz.pixelspacebase.items.weapon.Weapon;
import com.wafitz.pixelspacebase.items.weapon.melee.AssassinsBlade;
import com.wafitz.pixelspacebase.items.weapon.melee.BattleAxe;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Staff;
import com.wafitz.pixelspacebase.items.weapon.melee.Dagger;
import com.wafitz.pixelspacebase.items.weapon.melee.Dirk;
import com.wafitz.pixelspacebase.items.weapon.melee.Flail;
import com.wafitz.pixelspacebase.items.weapon.melee.Glaive;
import com.wafitz.pixelspacebase.items.weapon.melee.Greataxe;
import com.wafitz.pixelspacebase.items.weapon.melee.Greatsword;
import com.wafitz.pixelspacebase.items.weapon.melee.HandAxe;
import com.wafitz.pixelspacebase.items.weapon.melee.ImperialShield;
import com.wafitz.pixelspacebase.items.weapon.melee.Knuckles;
import com.wafitz.pixelspacebase.items.weapon.melee.Longsword;
import com.wafitz.pixelspacebase.items.weapon.melee.Mace;
import com.wafitz.pixelspacebase.items.weapon.melee.NewShortsword;
import com.wafitz.pixelspacebase.items.weapon.melee.Quarterstaff;
import com.wafitz.pixelspacebase.items.weapon.melee.RoundShield;
import com.wafitz.pixelspacebase.items.weapon.melee.RunicBlade;
import com.wafitz.pixelspacebase.items.weapon.melee.Sai;
import com.wafitz.pixelspacebase.items.weapon.melee.Scimitar;
import com.wafitz.pixelspacebase.items.weapon.melee.Spear;
import com.wafitz.pixelspacebase.items.weapon.melee.Sword;
import com.wafitz.pixelspacebase.items.weapon.melee.WarHammer;
import com.wafitz.pixelspacebase.items.weapon.melee.Whip;
import com.wafitz.pixelspacebase.items.weapon.melee.WornShortsword;
import com.wafitz.pixelspacebase.items.weapon.missiles.Boomerang;
import com.wafitz.pixelspacebase.items.weapon.missiles.CurareDart;
import com.wafitz.pixelspacebase.items.weapon.missiles.Dart;
import com.wafitz.pixelspacebase.items.weapon.missiles.IncendiaryDart;
import com.wafitz.pixelspacebase.items.weapon.missiles.Javelin;
import com.wafitz.pixelspacebase.items.weapon.missiles.Shuriken;
import com.wafitz.pixelspacebase.items.weapon.missiles.Tamahawk;
import com.wafitz.pixelspacebase.triggers.AlienPlant;
import com.wafitz.pixelspacebase.triggers.AlienTrap;
import com.wafitz.pixelspacebase.triggers.Blinding;
import com.wafitz.pixelspacebase.triggers.Boost;
import com.wafitz.pixelspacebase.triggers.Disorient;
import com.wafitz.pixelspacebase.triggers.FireTrigger;
import com.wafitz.pixelspacebase.triggers.Healing;
import com.wafitz.pixelspacebase.triggers.IceTrigger;
import com.wafitz.pixelspacebase.triggers.Knockout;
import com.wafitz.pixelspacebase.triggers.Teleportation;
import com.wafitz.pixelspacebase.triggers.Trigger;
import com.wafitz.pixelspacebase.triggers.Venom;
import com.wafitz.pixelspacebase.triggers.WeakForcefield;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Generator {

    public enum Category {
        WEAPON(100, Weapon.class),
        WEP_T1(0, Weapon.class),
        WEP_T2(0, Weapon.class),
        WEP_T3(0, Weapon.class),
        WEP_T4(0, Weapon.class),
        WEP_T5(0, Weapon.class),
        ARMOR(60, Armor.class),
        EXPERIMENTALTECH(500, ExperimentalTech.class),
        SCRIPT(400, Script.class),
        BLASTER(40, Blaster.class),
        MODULE(15, Module.class),
        ARTIFACT(15, Artifact.class),
        GADGET(50, Trigger.Gadget.class),
        FOOD(0, Food.class),
        PARTS(500, Parts.class);

        public Class<?>[] classes;
        public float[] probs;

        public float prob;
        public Class<? extends Item> superClass;

        Category(float prob, Class<? extends Item> superClass) {
            this.prob = prob;
            this.superClass = superClass;
        }

        public static int order(Item item) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].superClass.isInstance(item)) {
                    return i;
                }
            }

            return item instanceof Container ? Integer.MAX_VALUE : Integer.MAX_VALUE - 1;
        }
    }

    private static final float[][] floorSetTierProbs = new float[][]{
            {0, 70, 20, 8, 2},
            {0, 25, 50, 20, 5},
            {0, 10, 40, 40, 10},
            {0, 5, 20, 50, 25},
            {0, 2, 8, 20, 70}
    };

    private static HashMap<Category, Float> categoryProbs = new HashMap<>();

    private static final float[] INITIAL_ARTIFACT_PROBS = new float[]{0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 1};

    static {

        Category.PARTS.classes = new Class<?>[]{
                Parts.class};
        Category.PARTS.probs = new float[]{1};

        Category.SCRIPT.classes = new Class<?>[]{
                IdentifyScript.class,
                TeleportationScript.class,
                FixScript.class,
                UpgradeScript.class,
                RechargingScript.class,
                MappingScript.class,
                EchoLocationScript.class,
                TerrorScript.class,
                KnockoutScript.class,
                EnhancementScript.class,
                PsionicBlastScript.class,
                WeakCloneScript.class};
        Category.SCRIPT.probs = new float[]{30, 10, 20, 0, 15, 15, 12, 8, 8, 0, 4, 10};

        Category.EXPERIMENTALTECH.classes = new Class<?>[]{
                HealingTech.class,
                ExperienceTech.class,
                ToxicGasTech.class,
                ParalyzingTech.class,
                FireTech.class,
                RocketTech.class,
                StrengthTech.class,
                SecurityTech.class,
                PolymerMembrane.class,
                InvisibilityTech.class,
                PowerTech.class,
                FrostTech.class};
        Category.EXPERIMENTALTECH.probs = new float[]{45, 4, 15, 10, 15, 10, 0, 20, 12, 10, 0, 10};

        //TODO: add last ones when implemented
        Category.BLASTER.classes = new Class<?>[]{
                MissileBlaster.class,
                LightningBlaster.class,
                Disintergrator.class,
                FireBlaster.class,
                VenomBlaster.class,
                WaveBlaster.class,
                //WandOfLivingEarth.class,
                FreezeBlaster.class,
                LightBlaster.class,
                //WandOfWarding.class,
                TransfusionBlaster.class,
                MindBlaster.class,
                EMP.class};
        Category.BLASTER.probs = new float[]{5, 4, 4, 4, 4, 3, /*3,*/ 3, 3, /*3,*/ 3, 3, 3};

        //see generator.randomWeapon
        Category.WEAPON.classes = new Class<?>[]{};
        Category.WEAPON.probs = new float[]{};

        Category.WEP_T1.classes = new Class<?>[]{
                WornShortsword.class,
                Knuckles.class,
                Dagger.class,
                DM3000Staff.class,
                Boomerang.class,
                Dart.class
        };
        Category.WEP_T1.probs = new float[]{1, 1, 1, 0, 0, 1};

        Category.WEP_T2.classes = new Class<?>[]{
                NewShortsword.class,
                HandAxe.class,
                Spear.class,
                Quarterstaff.class,
                Dirk.class,
                IncendiaryDart.class
        };
        Category.WEP_T2.probs = new float[]{6, 5, 5, 4, 4, 6};

        Category.WEP_T3.classes = new Class<?>[]{
                Sword.class,
                Mace.class,
                Scimitar.class,
                RoundShield.class,
                Sai.class,
                Whip.class,
                Shuriken.class,
                CurareDart.class
        };
        Category.WEP_T3.probs = new float[]{6, 5, 5, 4, 4, 4, 6, 6};

        Category.WEP_T4.classes = new Class<?>[]{
                Longsword.class,
                BattleAxe.class,
                Flail.class,
                RunicBlade.class,
                AssassinsBlade.class,
                Javelin.class
        };
        Category.WEP_T4.probs = new float[]{6, 5, 5, 4, 4, 6};

        Category.WEP_T5.classes = new Class<?>[]{
                Greatsword.class,
                WarHammer.class,
                Glaive.class,
                Greataxe.class,
                ImperialShield.class,
                Tamahawk.class
        };
        Category.WEP_T5.probs = new float[]{6, 5, 5, 4, 4, 6};

        //see Generator.randomArmor
        Category.ARMOR.classes = new Class<?>[]{
                Uniform.class,
                SpaceSuit.class,
                HunterSpaceSuit.class,
                HoverPod.class,
                Loader.class};
        Category.ARMOR.probs = new float[]{0, 0, 0, 0, 0};

        Category.FOOD.classes = new Class<?>[]{
                Food.class,
                Pasty.class,
                MysteryMeat.class};
        Category.FOOD.probs = new float[]{4, 1, 0};

        Category.MODULE.classes = new Class<?>[]{
                AccuracyModule.class,
                EvasionModule.class,
                ElementsModule.class,
                ForceModule.class,
                AttackModule.class,
                SpeedModule.class,
                BallisticModule.class, //currently removed from drop tables, pending rework
                PowerModule.class,
                TargetingModule.class,
                SteelModule.class,
                TechModule.class};
        Category.MODULE.probs = new float[]{1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1};

        Category.ARTIFACT.classes = new Class<?>[]{
                StrongForcefield.class,
                AlienDNA.class,
                StealthModule.class,
                SurvivalModule.class,
                McGyvrModule.class,
                GnollTechShield.class,
                SurveyorModule.class,
                TimeFolder.class,
                BuggyCompiler.class,
                MakersToolkit.class, //currently removed from drop tables, pending rework.
                HoloPad.class, //starts with no chance of spawning, chance is set directly after beating ghost quest.
                LloydsBeacon.class,
                GravityGun.class
        };
        Category.ARTIFACT.probs = INITIAL_ARTIFACT_PROBS.clone();

        Category.GADGET.classes = new Class<?>[]{
                FireTrigger.Gadget.class,
                IceTrigger.Gadget.class,
                Venom.Gadget.class,
                Blinding.Gadget.class,
                Healing.Gadget.class,
                WeakForcefield.Gadget.class,
                Teleportation.Gadget.class,
                AlienTrap.Gadget.class,
                AlienPlant.Gadget.class,
                Knockout.Gadget.class,
                Disorient.Gadget.class,
                Boost.Gadget.class};
        Category.GADGET.probs = new float[]{12, 12, 12, 12, 12, 12, 12, 0, 4, 12, 12, 1};
    }

    public static void reset() {
        for (Category cat : Category.values()) {
            categoryProbs.put(cat, cat.prob);
        }
    }

    public static Item random() {
        return random(Random.chances(categoryProbs));
    }

    public static Item random(Category cat) {
        try {

            categoryProbs.put(cat, categoryProbs.get(cat) / 2);

            switch (cat) {
                case ARMOR:
                    return randomArmor();
                case WEAPON:
                    return randomWeapon();
                case ARTIFACT:
                    Item item = randomArtifact();
                    //if we're out of artifacts, return a ring instead.
                    return item != null ? item : random(Category.MODULE);
                default:
                    return ((Item) cat.classes[Random.chances(cat.probs)].newInstance()).random();
            }

        } catch (Exception e) {

            PixelSpacebase.reportException(e);
            return null;

        }
    }

    public static Item random(Class<? extends Item> cl) {
        try {

            return cl.newInstance().random();

        } catch (Exception e) {

            PixelSpacebase.reportException(e);
            return null;

        }
    }

    public static Armor randomArmor() {
        return randomArmor(Dungeon.depth / 5);
    }

    public static Armor randomArmor(int floorSet) {

        floorSet = (int) GameMath.gate(0, floorSet, floorSetTierProbs.length - 1);

        try {
            Armor a = (Armor) Category.ARMOR.classes[Random.chances(floorSetTierProbs[floorSet])].newInstance();
            a.random();
            return a;
        } catch (Exception e) {
            PixelSpacebase.reportException(e);
            return null;
        }
    }

    public static final Category[] wepTiers = new Category[]{
            Category.WEP_T1,
            Category.WEP_T2,
            Category.WEP_T3,
            Category.WEP_T4,
            Category.WEP_T5
    };

    public static Weapon randomWeapon() {
        return randomWeapon(Dungeon.depth / 5);
    }

    public static Weapon randomWeapon(int floorSet) {

        floorSet = (int) GameMath.gate(0, floorSet, floorSetTierProbs.length - 1);

        try {
            Category c = wepTiers[Random.chances(floorSetTierProbs[floorSet])];
            Weapon w = (Weapon) c.classes[Random.chances(c.probs)].newInstance();
            w.random();
            return w;
        } catch (Exception e) {
            PixelSpacebase.reportException(e);
            return null;
        }
    }

    //enforces uniqueness of artifacts throughout a run.
    public static Artifact randomArtifact() {

        try {
            Category cat = Category.ARTIFACT;
            int i = Random.chances(cat.probs);

            //if no artifacts are left, return null
            if (i == -1) {
                return null;
            }

            Artifact artifact = (Artifact) cat.classes[i].newInstance();

            //remove the chance of spawning this artifact.
            cat.probs[i] = 0;
            spawnedArtifacts.add(cat.classes[i].getSimpleName());

            artifact.random();

            return artifact;

        } catch (Exception e) {
            PixelSpacebase.reportException(e);
            return null;
        }
    }

    public static boolean removeArtifact(Artifact artifact) {
        if (spawnedArtifacts.contains(artifact.getClass().getSimpleName()))
            return false;

        Category cat = Category.ARTIFACT;
        for (int i = 0; i < cat.classes.length; i++)
            if (cat.classes[i].equals(artifact.getClass())) {
                if (cat.probs[i] == 1) {
                    cat.probs[i] = 0;
                    spawnedArtifacts.add(artifact.getClass().getSimpleName());
                    return true;
                } else
                    return false;
            }

        return false;
    }

    //resets artifact probabilities, for new dungeons
    public static void initArtifacts() {
        Category.ARTIFACT.probs = INITIAL_ARTIFACT_PROBS.clone();

        //checks for dried rose quest completion, adds the rose in accordingly.
        if (Hologram.Quest.completed()) Category.ARTIFACT.probs[10] = 1;

        spawnedArtifacts = new ArrayList<>();
    }

    private static ArrayList<String> spawnedArtifacts = new ArrayList<>();

    private static final String ARTIFACTS = "artifacts";

    //used to store information on which artifacts have been spawned.
    public static void storeInBundle(Bundle bundle) {
        bundle.put(ARTIFACTS, spawnedArtifacts.toArray(new String[spawnedArtifacts.size()]));
    }

    public static void restoreFromBundle(Bundle bundle) {
        initArtifacts();

        if (bundle.contains(ARTIFACTS)) {
            Collections.addAll(spawnedArtifacts, bundle.getStringArray(ARTIFACTS));
            Category cat = Category.ARTIFACT;

            for (String artifact : spawnedArtifacts)
                for (int i = 0; i < cat.classes.length; i++)
                    if (cat.classes[i].getSimpleName().equals(artifact))
                        cat.probs[i] = 0;
        }
    }
}
