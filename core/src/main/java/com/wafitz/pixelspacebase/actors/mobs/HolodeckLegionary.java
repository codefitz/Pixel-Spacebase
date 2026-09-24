/*
 * Pixel Spacebase, Copyright (C) 2017 Wes Fitzpatrick
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.wafitz.pixelspacebase.actors.mobs;

import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.sprites.HolodeckLegionarySprite;
import com.watabou.utils.Random;

/** A Roman-themed holodeck projection roaming the habitation ring. */
public class HolodeckLegionary extends Mob {

    {
        spriteClass = HolodeckLegionarySprite.class;

        HP = HT = 36;
        defenseSkill = 15;
        EXP = 6;
        maxLvl = 18;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(8, 14);
    }

    @Override
    public int attackSkill(Char target) {
        return 18;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 4);
    }
}
