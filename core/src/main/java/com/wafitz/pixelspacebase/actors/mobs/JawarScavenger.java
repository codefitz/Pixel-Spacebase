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
package com.wafitz.pixelspacebase.actors.mobs;

import com.wafitz.pixelspacebase.Badges;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Cripple;
import com.wafitz.pixelspacebase.actors.buffs.Poison;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.sprites.JawarScavengerSprite;
import com.watabou.utils.Random;

public class JawarScavenger extends Jawar {

    public Item item;

    {
        spriteClass = JawarScavengerSprite.class;

        //One in 30 Jawars becomes the more dangerous scavenger variant.
        lootChance = 0.333f;
    }

    @Override
    protected boolean steal(Hero hero) {
        if (super.steal(hero)) {

            Buff.prolong(hero, Blindness.class, Random.Int(2, 5));
            Buff.affect(hero, Poison.class).set(Random.Int(5, 7) * Poison.durationFactor(enemy));
            Buff.prolong(hero, Cripple.class, Random.Int(3, 8));
            SpacebaseRun.observe();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Badges.validateRare(this);
    }
}
