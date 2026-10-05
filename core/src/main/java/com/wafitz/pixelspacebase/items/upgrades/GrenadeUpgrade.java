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
package com.wafitz.pixelspacebase.items.upgrades;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.sprites.ItemSprite;
import com.wafitz.pixelspacebase.utils.BArray;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

/** Retains the existing upgrade classes in saves while giving their payloads grenade actions. */
public abstract class GrenadeUpgrade extends Upgrade {

    private static final String AC_USE = "USE";

    private ItemSprite.Glowing glow;

    @Override
    protected void updateDefaultAction() {
        defaultAction = isSealed() ? AC_OPEN : AC_THROW;
        usesTargeting = !isSealed();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove(AC_RUN);
        if (!isSealed()) actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (isSealed() && (action.equals(AC_OPEN) || action.equals(AC_RUN))) {
            identify();
            GLog.i(Messages.get(Upgrade.class, "revealed", name()));
            return;
        }
        if (action.equals(AC_USE)) {
            super.execute(hero, action);
            curItem = detach(hero.belongings.backpack);
            ((GrenadeUpgrade) curItem).doRead();
            return;
        }
        // Legacy activation requests use the default throw action.
        if (action.equals(AC_RUN) || action.equals(AC_OPEN)) action = AC_THROW;
        super.execute(hero, action);
    }

    @Override
    public int image() {
        return isSealed() ? super.image() : grenadeImage();
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (isSealed()) return null;
        if (glow == null) glow = new ItemSprite.Glowing(grenadeColor());
        return glow;
    }

    @Override
    protected void onThrow(int cell) {
        if (Level.pit[cell] || SpacebaseRun.level.map[cell] == Terrain.WELL) {
            super.onThrow(cell);
            return;
        }
        setKnown();
        detonate(cell);
    }

    @Override
    protected final void doRead() {
        setKnown();
        detonate(curUser.pos);
        curUser.spendAndNext(TIME_TO_READ);
    }

    protected boolean[] blastArea(int cell) {
        return blastArea(cell, Level.losBlocking);
    }

    static boolean[] blastArea(int cell, boolean[] blocked) {
        PathFinder.buildDistanceMap(cell, BArray.not(blocked, null), 3);
        boolean[] affected = new boolean[blocked.length];
        for (int i = 0; i < affected.length; i++) {
            affected[i] = PathFinder.distance[i] < Integer.MAX_VALUE;
        }
        return affected;
    }

    protected abstract int grenadeImage();
    protected abstract int grenadeColor();
    protected abstract void detonate(int cell);
}
