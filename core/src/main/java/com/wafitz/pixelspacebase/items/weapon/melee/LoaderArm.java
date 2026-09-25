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
package com.wafitz.pixelspacebase.items.weapon.melee;

import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.armor.Loader;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

import java.util.ArrayList;

public class LoaderArm extends MeleeWeapon {

    {
        image = ItemSpriteSheet.LOADER_ARM;
        tier = 3;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hero.belongings.armor instanceof Loader) actions.remove("UNEQUIP");
        return actions;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        return !(hero.belongings.armor instanceof Loader)
                && super.doUnequip(hero, collect, single);
    }

}
