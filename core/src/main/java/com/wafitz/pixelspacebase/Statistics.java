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

import com.watabou.utils.Bundle;

public class Statistics {

    public static int partsCollected;
    public static int deepestFloor;
    public static int enemiesSlain;
    public static int foodEaten;
    public static int experimentalTechMade;
    public static int waterThings;
    public static int nightHunt;
    public static int clonesSpent;

    public static float duration;

    public static boolean qualifiedForNoKilling = false;
    static boolean completedWithNoKilling = false;

    public static boolean amuletObtained = false;

    public static void reset() {

        partsCollected = 0;
        deepestFloor = 0;
        enemiesSlain = 0;
        foodEaten = 0;
        experimentalTechMade = 0;
        waterThings = 0;
        nightHunt = 0;
        clonesSpent = 0;

        duration = 0;

        qualifiedForNoKilling = false;

        amuletObtained = false;

    }

    private static final String PARTS = "score";
    private static final String DEEPEST = "maxDepth";
    private static final String SLAIN = "enemiesSlain";
    private static final String FOOD = "foodEaten";
    private static final String CRAFTING = "experimentalTechMade";
    private static final String WATERTHINGS = "priranhas";
    private static final String NIGHT = "nightHunt";
    private static final String CLONES = "clonesSpent";
    private static final String DURATION = "duration";
    private static final String AMULET = "amuletObtained";

    public static void storeInBundle(Bundle bundle) {
        bundle.put(PARTS, partsCollected);
        bundle.put(DEEPEST, deepestFloor);
        bundle.put(SLAIN, enemiesSlain);
        bundle.put(FOOD, foodEaten);
        bundle.put(CRAFTING, experimentalTechMade);
        bundle.put(WATERTHINGS, waterThings);
        bundle.put(NIGHT, nightHunt);
        bundle.put(CLONES, clonesSpent);
        bundle.put(DURATION, duration);
        bundle.put(AMULET, amuletObtained);
    }

    public static void restoreFromBundle(Bundle bundle) {
        partsCollected = bundle.getInt(PARTS);
        deepestFloor = bundle.getInt(DEEPEST);
        enemiesSlain = bundle.getInt(SLAIN);
        foodEaten = bundle.getInt(FOOD);
        experimentalTechMade = bundle.getInt(CRAFTING);
        waterThings = bundle.getInt(WATERTHINGS);
        nightHunt = bundle.getInt(NIGHT);
        clonesSpent = bundle.getInt(CLONES);
        duration = bundle.getFloat(DURATION);
        amuletObtained = bundle.getBoolean(AMULET);
    }

}
