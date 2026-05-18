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
package com.wafitz.pixelspacebase.actors.mobs.npcs;

import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.SurvivorSprite;
import com.watabou.utils.Random;

public class Survivor extends NPC {

    private static final String[] LINE_KEYS = {
            "line_1",
            "line_2",
            "line_3",
            "line_4",
            "line_5",
            "line_6",
            "line_7",
            "line_8",
            "line_9"
    };

    {
        spriteClass = SurvivorSprite.class;
        HP = HT = 8;
        defenseSkill = 4;
    }

    @Override
    protected boolean act() {
        throwItem();
        spend(TICK);
        return true;
    }

    @Override
    public int attackSkill(Char target) {
        return 0;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    @Override
    public boolean interact() {
        yell(Messages.get(this, Random.element(LINE_KEYS)));
        return false;
    }
}
