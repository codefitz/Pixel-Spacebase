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
package com.wafitz.pixelspacebase.levels.traps;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Blindness;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Cripple;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.effects.CellEmitter;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class FlashingTrap extends Trap {

    {
        color = YELLOW;
        shape = STARS;
    }

    @Override
    public void activate() {
        Char ch = Actor.findChar(pos);

        if (ch != null) {
            int len = Random.Int(5, 10) + Dungeon.depth;
            Buff.prolong(ch, Blindness.class, len);
            Buff.prolong(ch, Cripple.class, len);
            if (ch instanceof Mob) {
                if (((Mob) ch).state == ((Mob) ch).HUNTING) ((Mob) ch).state = ((Mob) ch).WANDERING;
                ((Mob) ch).beckon(Dungeon.level.randomDestination());
            }
            if (ch == Dungeon.hero) {
                Sample.INSTANCE.play(Assets.SND_BLAST);
            }
        }

        if (Dungeon.visible[pos]) {
            GameScene.flash(0xFFFFFF);
            CellEmitter.get(pos).burst(Speck.factory(Speck.LIGHT), 4);
        }
    }

}
