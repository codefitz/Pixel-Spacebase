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
package com.wafitz.pixelspacebase;

import com.wafitz.pixelspacebase.actors.mobs.Acidic;
import com.wafitz.pixelspacebase.actors.mobs.Albino;
import com.wafitz.pixelspacebase.actors.mobs.Bandit;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.Senior;
import com.wafitz.pixelspacebase.actors.mobs.Shielded;
import com.wafitz.pixelspacebase.items.ExperimentalTech.ExperimentalTech;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.artifacts.Artifact;
import com.wafitz.pixelspacebase.items.bags.ExperimentalTechBandolier;
import com.wafitz.pixelspacebase.items.bags.ScriptHolder;
import com.wafitz.pixelspacebase.items.bags.SeedPouch;
import com.wafitz.pixelspacebase.items.bags.WandHolster;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.scripts.Script;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.PixelScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Badges {

    public enum Badge {
        MONSTERS_SLAIN_1(0),
        MONSTERS_SLAIN_2(1),
        MONSTERS_SLAIN_3(2),
        MONSTERS_SLAIN_4(3),
        PARTS_COLLECTED_1(4),
        PARTS_COLLECTED_2(5),
        PARTS_COLLECTED_3(6),
        PARTS_COLLECTED_4(7),
        LEVEL_REACHED_1(8),
        LEVEL_REACHED_2(9),
        LEVEL_REACHED_3(10),
        LEVEL_REACHED_4(11),
        ALL_EXPERIMENTAL_TECH_IDENTIFIED(16),
        ALL_SCRIPTS_IDENTIFIED(17),
        ALL_MODULES_IDENTIFIED(18),
        ALL_WANDS_IDENTIFIED(19),
        ALL_ITEMS_IDENTIFIED(35, true),
        BAG_BOUGHT_SEED_POUCH,
        BAG_BOUGHT_SCRIPT_HOLDER,
        BAG_BOUGHT_EXPERIMENTAL_TECH_BANDOLIER,
        BAG_BOUGHT_WAND_HOLSTER,
        ALL_BAGS_BOUGHT(23),
        DEATH_FROM_FIRE(24),
        DEATH_FROM_POISON(25),
        DEATH_FROM_GAS(26),
        DEATH_FROM_HUNGER(27),
        DEATH_FROM_GLYPH(57),
        DEATH_FROM_FALLING(59),
        YASD(34, true),
        BOSS_SLAIN_1_COMMANDER,
        BOSS_SLAIN_1_DM3000,
        BOSS_SLAIN_1_SHAPESHIFTER,
        BOSS_SLAIN_1_CAPTAIN,
        BOSS_SLAIN_1(12),
        BOSS_SLAIN_2(13),
        BOSS_SLAIN_3(14),
        BOSS_SLAIN_4(15),
        BOSS_SLAIN_1_ALL_CLASSES(32, true),
        BOSS_SLAIN_3_GLADIATOR,
        BOSS_SLAIN_3_BERSERKER,
        BOSS_SLAIN_3_WARLOCK,
        BOSS_SLAIN_3_BATTLEMAGE,
        BOSS_SLAIN_3_FREERUNNER,
        BOSS_SLAIN_3_ASSASSIN,
        BOSS_SLAIN_3_SNIPER,
        BOSS_SLAIN_3_WARDEN,
        BOSS_SLAIN_3_ALL_SUBCLASSES(33, true),
        HAGGLERMODULE(20),
        THORNMODULE(21),
        STRENGTH_ATTAINED_1(40),
        STRENGTH_ATTAINED_2(41),
        STRENGTH_ATTAINED_3(42),
        STRENGTH_ATTAINED_4(43),
        FOOD_EATEN_1(44),
        FOOD_EATEN_2(45),
        FOOD_EATEN_3(46),
        FOOD_EATEN_4(47),
        MASTERY_COMMANDER,
        MASTERY_DM3000,
        MASTERY_SHAPESHIFTER,
        MASTERY_CAPTAIN,
        ITEM_LEVEL_1(48),
        ITEM_LEVEL_2(49),
        ITEM_LEVEL_3(50),
        ITEM_LEVEL_4(51),
        RARE_ALBINO,
        RARE_BANDIT,
        RARE_SHIELDED,
        RARE_SENIOR,
        RARE_ACIDIC,
        RARE(37, true),
        TUTORIAL_COMMANDER,
        TUTORIAL_DM3000,
        VICTORY_COMMANDER,
        VICTORY_DM3000,
        VICTORY_SHAPESHIFTER,
        VICTORY_CAPTAIN,
        VICTORY(22),
        VICTORY_ALL_CLASSES(36, true),
        MASTERY_COMBO(56),
        EXPERIMENTAL_TECH_COOKED_1(52),
        EXPERIMENTAL_TECH_COOKED_2(53),
        EXPERIMENTAL_TECH_COOKED_3(54),
        EXPERIMENTAL_TECH_COOKED_4(55),
        NO_MONSTERS_SLAIN(28),
        GRIM_WEAPON(29),
        WATERTHINGS(30),
        NIGHT_HUNTER(58),
        GAMES_PLAYED_1(60, true),
        GAMES_PLAYED_2(61, true),
        GAMES_PLAYED_3(62, true),
        GAMES_PLAYED_4(63, true),
        HAPPY_END(38),
        CHAMPION(39, true),
        SUPPORTER(31, true);

        public boolean meta;

        public int image;

        Badge(int image) {
            this(image, false);
        }

        Badge(int image, boolean meta) {
            this.image = image;
            this.meta = meta;
        }

        public String desc() {
            return Messages.get(this, name());
        }

        Badge() {
            this(-1);
        }
    }

    private static HashSet<Badge> global;
    private static HashSet<Badge> local = new HashSet<>();

    private static boolean saveNeeded = false;

    public static Callback loadingListener = null;

    public static void reset() {
        local.clear();
        loadGlobal();
    }

    private static final String BADGES_FILE = "badges.dat";
    private static final String BADGES = "badges";

    private static HashSet<Badge> restore(Bundle bundle) {
        HashSet<Badge> badges = new HashSet<>();
        if (bundle == null) return badges;

        String[] names = bundle.getStringArray(BADGES);
        for (String name : names) {
            try {
                badges.add(Badge.valueOf(name));
            } catch (Exception e) {
                PixelSpacebase.reportException(e);
            }
        }

        return badges;
    }

    private static void store(Bundle bundle, HashSet<Badge> badges) {
        int count = 0;
        String names[] = new String[badges.size()];

        for (Badge badge : badges) {
            names[count++] = badge.toString();
        }
        bundle.put(BADGES, names);
    }

    static void loadLocal(Bundle bundle) {
        local = restore(bundle);
    }

    static void saveLocal(Bundle bundle) {
        store(bundle, local);
    }

    public static void loadGlobal() {
        if (global == null) {
            try {
                InputStream input = Game.instance.openFileInput(BADGES_FILE);
                Bundle bundle = Bundle.read(input);
                input.close();

                global = restore(bundle);

            } catch (IOException e) {
                global = new HashSet<>();
            }
        }
    }

    public static void saveGlobal() {
        if (saveNeeded) {

            Bundle bundle = new Bundle();
            store(bundle, global);

            try {
                OutputStream output = Game.instance.openFileOutput(BADGES_FILE, Game.MODE_PRIVATE);
                Bundle.write(bundle, output);
                output.close();
                saveNeeded = false;
            } catch (IOException e) {
                PixelSpacebase.reportException(e);
            }
        }
    }

    public static void validateMonstersSlain() {
        Badge badge = null;

        if (!local.contains(Badge.MONSTERS_SLAIN_1) && Statistics.enemiesSlain >= 10) {
            badge = Badge.MONSTERS_SLAIN_1;
            local.add(badge);
        }
        if (!local.contains(Badge.MONSTERS_SLAIN_2) && Statistics.enemiesSlain >= 50) {
            badge = Badge.MONSTERS_SLAIN_2;
            local.add(badge);
        }
        if (!local.contains(Badge.MONSTERS_SLAIN_3) && Statistics.enemiesSlain >= 150) {
            badge = Badge.MONSTERS_SLAIN_3;
            local.add(badge);
        }
        if (!local.contains(Badge.MONSTERS_SLAIN_4) && Statistics.enemiesSlain >= 250) {
            badge = Badge.MONSTERS_SLAIN_4;
            local.add(badge);
        }

        displayBadge(badge);
    }

    public static void validatePartsCollected() {
        Badge badge = null;

        if (!local.contains(Badge.PARTS_COLLECTED_1) && Statistics.partsCollected >= 100) {
            badge = Badge.PARTS_COLLECTED_1;
            local.add(badge);
        }
        if (!local.contains(Badge.PARTS_COLLECTED_2) && Statistics.partsCollected >= 500) {
            badge = Badge.PARTS_COLLECTED_2;
            local.add(badge);
        }
        if (!local.contains(Badge.PARTS_COLLECTED_3) && Statistics.partsCollected >= 2500) {
            badge = Badge.PARTS_COLLECTED_3;
            local.add(badge);
        }
        if (!local.contains(Badge.PARTS_COLLECTED_4) && Statistics.partsCollected >= 7500) {
            badge = Badge.PARTS_COLLECTED_4;
            local.add(badge);
        }

        displayBadge(badge);
    }

    public static void validateLevelReached() {
        Badge badge = null;

        if (!local.contains(Badge.LEVEL_REACHED_1) && Dungeon.hero.lvl >= 6) {
            badge = Badge.LEVEL_REACHED_1;
            local.add(badge);
        }
        if (!local.contains(Badge.LEVEL_REACHED_2) && Dungeon.hero.lvl >= 12) {
            badge = Badge.LEVEL_REACHED_2;
            local.add(badge);
        }
        if (!local.contains(Badge.LEVEL_REACHED_3) && Dungeon.hero.lvl >= 18) {
            badge = Badge.LEVEL_REACHED_3;
            local.add(badge);
        }
        if (!local.contains(Badge.LEVEL_REACHED_4) && Dungeon.hero.lvl >= 24) {
            badge = Badge.LEVEL_REACHED_4;
            local.add(badge);
        }

        displayBadge(badge);
    }

    public static void validateStrengthAttained() {
        Badge badge = null;

        if (!local.contains(Badge.STRENGTH_ATTAINED_1) && Dungeon.hero.STR >= 13) {
            badge = Badge.STRENGTH_ATTAINED_1;
            local.add(badge);
        }
        if (!local.contains(Badge.STRENGTH_ATTAINED_2) && Dungeon.hero.STR >= 15) {
            badge = Badge.STRENGTH_ATTAINED_2;
            local.add(badge);
        }
        if (!local.contains(Badge.STRENGTH_ATTAINED_3) && Dungeon.hero.STR >= 17) {
            badge = Badge.STRENGTH_ATTAINED_3;
            local.add(badge);
        }
        if (!local.contains(Badge.STRENGTH_ATTAINED_4) && Dungeon.hero.STR >= 19) {
            badge = Badge.STRENGTH_ATTAINED_4;
            local.add(badge);
        }

        displayBadge(badge);
    }

    public static void validateFoodEaten() {
        Badge badge = null;

        if (!local.contains(Badge.FOOD_EATEN_1) && Statistics.foodEaten >= 10) {
            badge = Badge.FOOD_EATEN_1;
            local.add(badge);
        }
        if (!local.contains(Badge.FOOD_EATEN_2) && Statistics.foodEaten >= 20) {
            badge = Badge.FOOD_EATEN_2;
            local.add(badge);
        }
        if (!local.contains(Badge.FOOD_EATEN_3) && Statistics.foodEaten >= 30) {
            badge = Badge.FOOD_EATEN_3;
            local.add(badge);
        }
        if (!local.contains(Badge.FOOD_EATEN_4) && Statistics.foodEaten >= 40) {
            badge = Badge.FOOD_EATEN_4;
            local.add(badge);
        }

        displayBadge(badge);
    }

    public static void validateExperimentalTechCooked() {
        Badge badge = null;

        if (!local.contains(Badge.EXPERIMENTAL_TECH_COOKED_1) && Statistics.experimentalTechCooked >= 3) {
            badge = Badge.EXPERIMENTAL_TECH_COOKED_1;
            local.add(badge);
        }
        if (!local.contains(Badge.EXPERIMENTAL_TECH_COOKED_2) && Statistics.experimentalTechCooked >= 6) {
            badge = Badge.EXPERIMENTAL_TECH_COOKED_2;
            local.add(badge);
        }
        if (!local.contains(Badge.EXPERIMENTAL_TECH_COOKED_3) && Statistics.experimentalTechCooked >= 9) {
            badge = Badge.EXPERIMENTAL_TECH_COOKED_3;
            local.add(badge);
        }
        if (!local.contains(Badge.EXPERIMENTAL_TECH_COOKED_4) && Statistics.experimentalTechCooked >= 12) {
            badge = Badge.EXPERIMENTAL_TECH_COOKED_4;
            local.add(badge);
        }

        displayBadge(badge);
    }

    public static void validateWaterThingshasKilled() {
        Badge badge = null;

        if (!local.contains(Badge.WATERTHINGS) && Statistics.waterThings >= 6) {
            badge = Badge.WATERTHINGS;
            local.add(badge);
        }

        displayBadge(badge);
    }

    public static void validateItemLevelAquired(Item item) {

        // This method should be called:
        // 1) When an item is obtained (Item.collect)
        // 2) When an item is upgraded (ScriptOfUpgrade, WeaponUpgradeScript, ShortSword, WandOfMagicMissile)
        // 3) When an item is identified

        // Note that artifacts should never trigger this badge as they are alternatively upgraded
        if (!item.levelKnown || item instanceof Artifact) {
            return;
        }

        Badge badge = null;
        if (!local.contains(Badge.ITEM_LEVEL_1) && item.level() >= 3) {
            badge = Badge.ITEM_LEVEL_1;
            local.add(badge);
        }
        if (!local.contains(Badge.ITEM_LEVEL_2) && item.level() >= 6) {
            badge = Badge.ITEM_LEVEL_2;
            local.add(badge);
        }
        if (!local.contains(Badge.ITEM_LEVEL_3) && item.level() >= 9) {
            badge = Badge.ITEM_LEVEL_3;
            local.add(badge);
        }
        if (!local.contains(Badge.ITEM_LEVEL_4) && item.level() >= 12) {
            badge = Badge.ITEM_LEVEL_4;
            local.add(badge);
        }

        displayBadge(badge);
    }

    public static void validateAllExperimentalTechIdentified() {
        if (Dungeon.hero != null && Dungeon.hero.isAlive() &&
                !local.contains(Badge.ALL_EXPERIMENTAL_TECH_IDENTIFIED) && ExperimentalTech.allKnown()) {

            Badge badge = Badge.ALL_EXPERIMENTAL_TECH_IDENTIFIED;
            local.add(badge);
            displayBadge(badge);

            validateAllItemsIdentified();
        }
    }

    public static void validateAllScriptsIdentified() {
        if (Dungeon.hero != null && Dungeon.hero.isAlive() &&
                !local.contains(Badge.ALL_SCRIPTS_IDENTIFIED) && Script.allKnown()) {

            Badge badge = Badge.ALL_SCRIPTS_IDENTIFIED;
            local.add(badge);
            displayBadge(badge);

            validateAllItemsIdentified();
        }
    }

    public static void validateAllModulesIdentified() {
        if (Dungeon.hero != null && Dungeon.hero.isAlive() &&
                !local.contains(Badge.ALL_MODULES_IDENTIFIED) && Module.allKnown()) {

            Badge badge = Badge.ALL_MODULES_IDENTIFIED;
            local.add(badge);
            displayBadge(badge);

            validateAllItemsIdentified();
        }
    }

    //TODO: no longer in use, deal with new wand related badges in the badge rework.

    /**
     * public static void validateAllWandsIdentified() {
     * if (Dungeon.hero != null && Dungeon.hero.isAlive() &&
     * !local.contains( Badge.ALL_WANDS_IDENTIFIED ) && Wand.allKnown()) {
     * <p>
     * Badge badge = Badge.ALL_WANDS_IDENTIFIED;
     * local.add( badge );
     * displayBadge( badge );
     * <p>
     * validateAllItemsIdentified();
     * }
     * }
     */

    public static void validateAllBagsBought(Item bag) {

        Badge badge = null;
        if (bag instanceof SeedPouch) {
            badge = Badge.BAG_BOUGHT_SEED_POUCH;
        } else if (bag instanceof ScriptHolder) {
            badge = Badge.BAG_BOUGHT_SCRIPT_HOLDER;
        } else if (bag instanceof ExperimentalTechBandolier) {
            badge = Badge.BAG_BOUGHT_EXPERIMENTAL_TECH_BANDOLIER;
        } else if (bag instanceof WandHolster) {
            badge = Badge.BAG_BOUGHT_WAND_HOLSTER;
        }

        if (badge != null) {

            local.add(badge);

            if (!local.contains(Badge.ALL_BAGS_BOUGHT) &&
                    local.contains(Badge.BAG_BOUGHT_SEED_POUCH) &&
                    local.contains(Badge.BAG_BOUGHT_SCRIPT_HOLDER) &&
                    local.contains(Badge.BAG_BOUGHT_EXPERIMENTAL_TECH_BANDOLIER) &&
                    local.contains(Badge.BAG_BOUGHT_WAND_HOLSTER)) {

                badge = Badge.ALL_BAGS_BOUGHT;
                local.add(badge);
                displayBadge(badge);
            }
        }
    }

    private static void validateAllItemsIdentified() {
        if (!global.contains(Badge.ALL_ITEMS_IDENTIFIED) &&
                global.contains(Badge.ALL_EXPERIMENTAL_TECH_IDENTIFIED) &&
                global.contains(Badge.ALL_SCRIPTS_IDENTIFIED) &&
                global.contains(Badge.ALL_MODULES_IDENTIFIED)) {
            //global.contains( Badge.ALL_WANDS_IDENTIFIED )) {

            Badge badge = Badge.ALL_ITEMS_IDENTIFIED;
            displayBadge(badge);
        }
    }

    public static void validateDeathFromFire() {
        Badge badge = Badge.DEATH_FROM_FIRE;
        local.add(badge);
        displayBadge(badge);

        validateYASD();
    }

    public static void validateDeathFromPoison() {
        Badge badge = Badge.DEATH_FROM_POISON;
        local.add(badge);
        displayBadge(badge);

        validateYASD();
    }

    public static void validateDeathFromGas() {
        Badge badge = Badge.DEATH_FROM_GAS;
        local.add(badge);
        displayBadge(badge);

        validateYASD();
    }

    public static void validateDeathFromHunger() {
        Badge badge = Badge.DEATH_FROM_HUNGER;
        local.add(badge);
        displayBadge(badge);

        validateYASD();
    }

    public static void validateDeathFromGlyph() {
        Badge badge = Badge.DEATH_FROM_GLYPH;
        local.add(badge);
        displayBadge(badge);
    }

    public static void validateDeathFromFalling() {
        Badge badge = Badge.DEATH_FROM_FALLING;
        local.add(badge);
        displayBadge(badge);
    }

    private static void validateYASD() {
        if (global.contains(Badge.DEATH_FROM_FIRE) &&
                global.contains(Badge.DEATH_FROM_POISON) &&
                global.contains(Badge.DEATH_FROM_GAS) &&
                global.contains(Badge.DEATH_FROM_HUNGER)) {

            Badge badge = Badge.YASD;
            local.add(badge);
            displayBadge(badge);
        }
    }

    public static void validateBossSlain() {
        Badge badge = null;
        switch (Dungeon.depth) {
            case 5:
                badge = Badge.BOSS_SLAIN_1;
                break;
            case 10:
                badge = Badge.BOSS_SLAIN_2;
                break;
            case 15:
                badge = Badge.BOSS_SLAIN_3;
                break;
            case 20:
                badge = Badge.BOSS_SLAIN_4;
                break;
        }

        if (badge != null) {
            local.add(badge);
            displayBadge(badge);

            if (badge == Badge.BOSS_SLAIN_1) {
                switch (Dungeon.hero.heroClass) {
                    case COMMANDER:
                        badge = Badge.BOSS_SLAIN_1_COMMANDER;
                        break;
                    case DM3000:
                        badge = Badge.BOSS_SLAIN_1_DM3000;
                        break;
                    case SHAPESHIFTER:
                        badge = Badge.BOSS_SLAIN_1_SHAPESHIFTER;
                        break;
                    case CAPTAIN:
                        badge = Badge.BOSS_SLAIN_1_CAPTAIN;
                        break;
                }
                local.add(badge);
                if (!global.contains(badge)) {
                    global.add(badge);
                    saveNeeded = true;
                }

                if (global.contains(Badge.BOSS_SLAIN_1_COMMANDER) &&
                        global.contains(Badge.BOSS_SLAIN_1_DM3000) &&
                        global.contains(Badge.BOSS_SLAIN_1_SHAPESHIFTER) &&
                        global.contains(Badge.BOSS_SLAIN_1_CAPTAIN)) {

                    badge = Badge.BOSS_SLAIN_1_ALL_CLASSES;
                    if (!global.contains(badge)) {
                        displayBadge(badge);
                        global.add(badge);
                        saveNeeded = true;
                    }
                }
            } else if (badge == Badge.BOSS_SLAIN_3) {
                switch (Dungeon.hero.subClass) {
                    case GLADIATOR:
                        badge = Badge.BOSS_SLAIN_3_GLADIATOR;
                        break;
                    case BERSERKER:
                        badge = Badge.BOSS_SLAIN_3_BERSERKER;
                        break;
                    case WARLOCK:
                        badge = Badge.BOSS_SLAIN_3_WARLOCK;
                        break;
                    case BATTLEMAGE:
                        badge = Badge.BOSS_SLAIN_3_BATTLEMAGE;
                        break;
                    case FREERUNNER:
                        badge = Badge.BOSS_SLAIN_3_FREERUNNER;
                        break;
                    case ASSASSIN:
                        badge = Badge.BOSS_SLAIN_3_ASSASSIN;
                        break;
                    case SNIPER:
                        badge = Badge.BOSS_SLAIN_3_SNIPER;
                        break;
                    case WARDEN:
                        badge = Badge.BOSS_SLAIN_3_WARDEN;
                        break;
                    default:
                        return;
                }
                local.add(badge);
                if (!global.contains(badge)) {
                    global.add(badge);
                    saveNeeded = true;
                }

                if (global.contains(Badge.BOSS_SLAIN_3_GLADIATOR) &&
                        global.contains(Badge.BOSS_SLAIN_3_BERSERKER) &&
                        global.contains(Badge.BOSS_SLAIN_3_WARLOCK) &&
                        global.contains(Badge.BOSS_SLAIN_3_BATTLEMAGE) &&
                        global.contains(Badge.BOSS_SLAIN_3_FREERUNNER) &&
                        global.contains(Badge.BOSS_SLAIN_3_ASSASSIN) &&
                        global.contains(Badge.BOSS_SLAIN_3_SNIPER) &&
                        global.contains(Badge.BOSS_SLAIN_3_WARDEN)) {

                    badge = Badge.BOSS_SLAIN_3_ALL_SUBCLASSES;
                    if (!global.contains(badge)) {
                        displayBadge(badge);
                        global.add(badge);
                        saveNeeded = true;
                    }
                }
            }
        }
    }

    public static void validateMastery() {

        Badge badge = null;
        switch (Dungeon.hero.heroClass) {
            case COMMANDER:
                badge = Badge.MASTERY_COMMANDER;
                break;
            case DM3000:
                badge = Badge.MASTERY_DM3000;
                break;
            case SHAPESHIFTER:
                badge = Badge.MASTERY_SHAPESHIFTER;
                break;
            case CAPTAIN:
                badge = Badge.MASTERY_CAPTAIN;
                break;
        }

        if (!global.contains(badge)) {
            global.add(badge);
            saveNeeded = true;
        }
    }

    public static void validateMasteryCombo(int n) {
        if (!local.contains(Badge.MASTERY_COMBO) && n == 10) {
            Badge badge = Badge.MASTERY_COMBO;
            local.add(badge);
            displayBadge(badge);
        }
    }

    //TODO: Replace this badge, delayed until an eventual badge rework
    public static void validateHagglerModule() {
        if (!local.contains(Badge.HAGGLERMODULE)/* && new RingOfThorns().isKnown()*/) {
            Badge badge = Badge.HAGGLERMODULE;
            local.add(badge);
            displayBadge(badge);
        }
    }

    //TODO: Replace this badge, delayed until an eventual badge rework
    public static void validateThornsModule() {
        if (!local.contains(Badge.THORNMODULE)/* && new RingOfThorns().isKnown()*/) {
            Badge badge = Badge.THORNMODULE;
            local.add(badge);
            displayBadge(badge);
        }
    }

    public static void validateRare(Mob mob) {

        Badge badge = null;
        if (mob instanceof Albino) {
            badge = Badge.RARE_ALBINO;
        } else if (mob instanceof Bandit) {
            badge = Badge.RARE_BANDIT;
        } else if (mob instanceof Shielded) {
            badge = Badge.RARE_SHIELDED;
        } else if (mob instanceof Senior) {
            badge = Badge.RARE_SENIOR;
        } else if (mob instanceof Acidic) {
            badge = Badge.RARE_ACIDIC;
        }
        if (!global.contains(badge)) {
            global.add(badge);
            saveNeeded = true;
        }

        if (global.contains(Badge.RARE_ALBINO) &&
                global.contains(Badge.RARE_BANDIT) &&
                global.contains(Badge.RARE_SHIELDED) &&
                global.contains(Badge.RARE_SENIOR) &&
                global.contains(Badge.RARE_ACIDIC)) {

            badge = Badge.RARE;
            displayBadge(badge);
        }
    }

    public static void validateVictory() {

        Badge badge = Badge.VICTORY;
        displayBadge(badge);

        switch (Dungeon.hero.heroClass) {
            case COMMANDER:
                badge = Badge.VICTORY_COMMANDER;
                break;
            case DM3000:
                badge = Badge.VICTORY_DM3000;
                break;
            case SHAPESHIFTER:
                badge = Badge.VICTORY_SHAPESHIFTER;
                break;
            case CAPTAIN:
                badge = Badge.VICTORY_CAPTAIN;
                break;
        }
        local.add(badge);
        if (!global.contains(badge)) {
            global.add(badge);
            saveNeeded = true;
        }

        if (global.contains(Badge.VICTORY_COMMANDER) &&
                global.contains(Badge.VICTORY_DM3000) &&
                global.contains(Badge.VICTORY_SHAPESHIFTER) &&
                global.contains(Badge.VICTORY_CAPTAIN)) {

            badge = Badge.VICTORY_ALL_CLASSES;
            displayBadge(badge);
        }
    }

    public static void validateTutorial() {
        Badge badge = null;
        switch (Dungeon.hero.heroClass) {
            case COMMANDER:
                badge = Badge.TUTORIAL_COMMANDER;
                break;
            case DM3000:
                badge = Badge.TUTORIAL_DM3000;
                break;
            default:
                break;
        }

        if (badge != null) {
            local.add(badge);
            if (!global.contains(badge)) {
                global.add(badge);
                saveNeeded = true;
            }
        }
    }

    public static void validateNoKilling() {
        if (!local.contains(Badge.NO_MONSTERS_SLAIN) && Statistics.completedWithNoKilling) {
            Badge badge = Badge.NO_MONSTERS_SLAIN;
            local.add(badge);
            displayBadge(badge);
        }
    }

    public static void validateGrimWeapon() {
        if (!local.contains(Badge.GRIM_WEAPON)) {
            Badge badge = Badge.GRIM_WEAPON;
            local.add(badge);
            displayBadge(badge);
        }
    }

    public static void validateNightHunter() {
        if (!local.contains(Badge.NIGHT_HUNTER) && Statistics.nightHunt >= 15) {
            Badge badge = Badge.NIGHT_HUNTER;
            local.add(badge);
            displayBadge(badge);
        }
    }

    public static void validateSupporter() {

        global.add(Badge.SUPPORTER);
        saveNeeded = true;

        PixelScene.showBadge(Badge.SUPPORTER);
    }

    static void validateGamesPlayed() {
        Badge badge = null;
        if (Rankings.INSTANCE.totalNumber >= 10) {
            badge = Badge.GAMES_PLAYED_1;
        }
        if (Rankings.INSTANCE.totalNumber >= 100) {
            badge = Badge.GAMES_PLAYED_2;
        }
        if (Rankings.INSTANCE.totalNumber >= 500) {
            badge = Badge.GAMES_PLAYED_3;
        }
        if (Rankings.INSTANCE.totalNumber >= 2000) {
            badge = Badge.GAMES_PLAYED_4;
        }

        displayBadge(badge);
    }

    public static void validateHappyEnd() {
        displayBadge(Badge.HAPPY_END);
    }

    static void validateChampion() {
        displayBadge(Badge.CHAMPION);
    }

    private static void displayBadge(Badge badge) {

        if (badge == null) {
            return;
        }

        if (global.contains(badge)) {

            if (!badge.meta) {
                GLog.h(Messages.get(Badges.class, "endorsed", badge.desc()));
            }

        } else {

            global.add(badge);
            saveNeeded = true;

            if (badge.meta) {
                GLog.h(Messages.get(Badges.class, "new_super", badge.desc()));
            } else {
                GLog.h(Messages.get(Badges.class, "new", badge.desc()));
            }
            PixelScene.showBadge(badge);
        }
    }

    public static boolean isUnlocked(Badge badge) {
        return global.contains(badge);
    }

    public static void disown(Badge badge) {
        loadGlobal();
        global.remove(badge);
        saveNeeded = true;
    }

    public static List<Badge> filtered(boolean global) {

        HashSet<Badge> filtered = new HashSet<>(global ? Badges.global : Badges.local);

        Iterator<Badge> iterator = filtered.iterator();
        while (iterator.hasNext()) {
            Badge badge = iterator.next();
            if ((!global && badge.meta) || badge.image == -1) {
                iterator.remove();
            }
        }

        leaveBest(filtered, Badge.MONSTERS_SLAIN_1, Badge.MONSTERS_SLAIN_2, Badge.MONSTERS_SLAIN_3, Badge.MONSTERS_SLAIN_4);
        leaveBest(filtered, Badge.PARTS_COLLECTED_1, Badge.PARTS_COLLECTED_2, Badge.PARTS_COLLECTED_3, Badge.PARTS_COLLECTED_4);
        leaveBest(filtered, Badge.BOSS_SLAIN_1, Badge.BOSS_SLAIN_2, Badge.BOSS_SLAIN_3, Badge.BOSS_SLAIN_4);
        leaveBest(filtered, Badge.LEVEL_REACHED_1, Badge.LEVEL_REACHED_2, Badge.LEVEL_REACHED_3, Badge.LEVEL_REACHED_4);
        leaveBest(filtered, Badge.STRENGTH_ATTAINED_1, Badge.STRENGTH_ATTAINED_2, Badge.STRENGTH_ATTAINED_3, Badge.STRENGTH_ATTAINED_4);
        leaveBest(filtered, Badge.FOOD_EATEN_1, Badge.FOOD_EATEN_2, Badge.FOOD_EATEN_3, Badge.FOOD_EATEN_4);
        leaveBest(filtered, Badge.ITEM_LEVEL_1, Badge.ITEM_LEVEL_2, Badge.ITEM_LEVEL_3, Badge.ITEM_LEVEL_4);
        leaveBest(filtered, Badge.EXPERIMENTAL_TECH_COOKED_1, Badge.EXPERIMENTAL_TECH_COOKED_2, Badge.EXPERIMENTAL_TECH_COOKED_3, Badge.EXPERIMENTAL_TECH_COOKED_4);
        leaveBest(filtered, Badge.BOSS_SLAIN_1_ALL_CLASSES, Badge.BOSS_SLAIN_3_ALL_SUBCLASSES);
        leaveBest(filtered, Badge.DEATH_FROM_FIRE, Badge.YASD);
        leaveBest(filtered, Badge.DEATH_FROM_GAS, Badge.YASD);
        leaveBest(filtered, Badge.DEATH_FROM_HUNGER, Badge.YASD);
        leaveBest(filtered, Badge.DEATH_FROM_POISON, Badge.YASD);
        leaveBest(filtered, Badge.ALL_EXPERIMENTAL_TECH_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED);
        leaveBest(filtered, Badge.ALL_SCRIPTS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED);
        leaveBest(filtered, Badge.ALL_MODULES_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED);
        leaveBest(filtered, Badge.ALL_WANDS_IDENTIFIED, Badge.ALL_ITEMS_IDENTIFIED);
        leaveBest(filtered, Badge.VICTORY, Badge.VICTORY_ALL_CLASSES);
        leaveBest(filtered, Badge.VICTORY, Badge.HAPPY_END);
        leaveBest(filtered, Badge.VICTORY, Badge.CHAMPION);
        leaveBest(filtered, Badge.GAMES_PLAYED_1, Badge.GAMES_PLAYED_2, Badge.GAMES_PLAYED_3, Badge.GAMES_PLAYED_4);

        ArrayList<Badge> list = new ArrayList<>(filtered);
        Collections.sort(list);

        return list;
    }

    private static void leaveBest(HashSet<Badge> list, Badge... badges) {
        for (int i = badges.length - 1; i > 0; i--) {
            if (list.contains(badges[i])) {
                for (int j = 0; j < i; j++) {
                    list.remove(badges[j]);
                }
                break;
            }
        }
    }
}
