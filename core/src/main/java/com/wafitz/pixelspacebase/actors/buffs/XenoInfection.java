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
package com.wafitz.pixelspacebase.actors.buffs;

import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.ToughXeno;
import com.wafitz.pixelspacebase.actors.mobs.Xenomorph;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.Bundle;

public class XenoInfection extends Buff {

    private static final int WARN_TURN = 2;
    private static final int BURST_TURN = 5;

    private int turns;
    private boolean strongSpawn;

    public static void infect(Hero hero) {
        infect(hero, false);
    }

    public static void infectStrong(Hero hero) {
        infect(hero, true);
    }

    private static void infect(Hero hero, boolean strongSpawn) {
        if (hero.buff(XenoInfection.class) == null) {
            XenoInfection infection = Buff.affect(hero, XenoInfection.class);
            infection.turns = 0;
            infection.strongSpawn = strongSpawn;
            infection.spend(TICK);
            String message = Messages.get(XenoInfection.class, "start");
            GLog.w(message);
            GameScene.flashThenShowMessage(0x000000, message);
        }
    }

    @Override
    public boolean act() {
        if (!(target instanceof Hero)) {
            detach();
            return true;
        }

        turns++;

        if (turns == WARN_TURN) {
            GLog.w(Messages.get(this, "warning"));
        }

        if (turns >= BURST_TURN) {
            Hero hero = (Hero) target;
            GLog.w(Messages.get(this, "burst"));
            if (strongSpawn) {
                ToughXeno.spawnAdjacent(hero.pos, false);
            } else {
                Xenomorph.spawnAdjacent(hero.pos);
            }
            hero.damage(Math.max(1, hero.HP / 2), this);
            detach();
        } else {
            spend(TICK);
        }

        return true;
    }

    private static final String TURNS = "turns";
    private static final String STRONG_SPAWN = "strong_spawn";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TURNS, turns);
        bundle.put(STRONG_SPAWN, strongSpawn);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        turns = bundle.getInt(TURNS);
        strongSpawn = bundle.getBoolean(STRONG_SPAWN);
    }
}
