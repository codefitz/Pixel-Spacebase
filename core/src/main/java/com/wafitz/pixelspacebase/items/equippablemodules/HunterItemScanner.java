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
 */
package com.wafitz.pixelspacebase.items.equippablemodules;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.EquipableItem;
import com.wafitz.pixelspacebase.items.armor.HunterSpaceSuit;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

/** Adds an item-location signal to the Hunter suit's existing scanner. */
public class HunterItemScanner extends EquippableModule {

    {
        image = ItemSpriteSheet.SURVEYOR_MODULE;
    }

    @Override
    protected ModuleBuff passiveBuff() {
        return new ScannerLink();
    }

    @Override
    public int cost() {
        // Workshop fabrication multiplies this base price by floor progression.
        return 200;
    }

    public static boolean active() {
        Hero hero = SpacebaseRun.hero;
        if (hero == null || !HunterSpaceSuit.signatureScannerActive()) return false;

        return equippedAndWorking(hero.belongings.misc1) || equippedAndWorking(hero.belongings.misc2);
    }

    private static boolean equippedAndWorking(EquipableItem item) {
        return item instanceof HunterItemScanner && !((HunterItemScanner) item).malfunctioning;
    }

    public class ScannerLink extends ModuleBuff {
        @Override
        public boolean act() {
            spend(TICK);
            return true;
        }
    }
}
