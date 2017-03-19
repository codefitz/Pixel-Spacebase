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
package com.wafitz.pixelspacebase.triggers;

import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.ExperimentalTech.SecurityTech;
import com.wafitz.pixelspacebase.items.scripts.TeleportationScript;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;

public class Teleportation extends Trigger {

    {
        image = 6;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch instanceof Hero) {

            TeleportationScript.teleportHero((Hero) ch);
            ((Hero) ch).curAction = null;

        } else if (ch instanceof Mob && !ch.properties().contains(Char.Property.IMMOVABLE)) {

            int count = 10;
            int newPos;
            do {
                newPos = Dungeon.level.randomRespawnCell();
                if (count-- <= 0) {
                    break;
                }
            } while (newPos == -1);

            if (newPos != -1 && !Dungeon.bossLevel()) {

                ch.pos = newPos;
                ch.sprite.place(ch.pos);
                ch.sprite.visible = Dungeon.visible[ch.pos];

            }

        }

        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);
        }
    }

    public static class Gadget extends Trigger.Gadget {
        {
            image = ItemSpriteSheet.FADELEAF_GADGET;

            triggerClass = Teleportation.class;
            craftingClass = SecurityTech.class;
        }
    }
}
