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

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.actors.buffs.Light;
import com.wafitz.pixelspacebase.actors.buffs.Sleep;
import com.wafitz.pixelspacebase.actors.buffs.Terror;
import com.wafitz.pixelspacebase.items.upgrades.KnockoutUpgrade;
import com.wafitz.pixelspacebase.items.upgrades.PhaseShiftUpgrade;
import com.wafitz.pixelspacebase.items.armor.HoverPod;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.items.weapon.enhancements.Vampiric;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.RegularLevel;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.utils.GLog;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.sprites.SignalSirenSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class SignalSiren extends Mob {

    private static final int BLINK_DELAY = 5;
    private static final String SCARED_BY_STATION_CAT = "scaredByStationCat";

    private int delay = 0;
    private boolean scaredByStationCat;

    {
        spriteClass = SignalSirenSprite.class;

        HP = HT = 80;
        defenseSkill = 25;
        viewDistance = Light.DISTANCE;

        EXP = 12;
        maxLvl = 25;

        loot = new KnockoutUpgrade();
        lootChance = 0.05f;

        flying = true;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(22, 30);
    }

    @Override
    public boolean attack(Char enemy) {
        // Resolve damage first: do not start a fall/level transition halfway through a hit.
        boolean protectedFromHit = enemy instanceof Hero && HoverPod.equipped((Hero) enemy) != null;
        boolean hit = super.attack(enemy);
        if (hit && !protectedFromHit && enemy == SpacebaseRun.hero
                && enemy.isAlive() && Random.Int(3) == 0) {
            displaceHero();
        }
        return hit;
    }

    public static ArrayList<Integer> roomTeleportCells(Level level, Room room, int heroPos) {
        ArrayList<Integer> cells = new ArrayList<>();
        if (room == null) return cells;
        for (int y = room.top + 1; y < room.bottom; y++) {
            for (int x = room.left + 1; x < room.right; x++) {
                int cell = x + y * level.width();
                if (!level.insideMap(cell) || cell == heroPos || Actor.findChar(cell) != null) continue;
                // Deliberately unlike safe teleports: pools and fall hazards remain eligible.
                if (Level.passable[cell] || Level.pit[cell]
                        || level.map[cell] == Terrain.CHASM || level.isPlasmaCell(cell)) cells.add(cell);
            }
        }
        return cells;
    }

    private void displaceHero() {
        Level level = SpacebaseRun.level;
        if (!(level instanceof RegularLevel)) return;
        Room room = ((RegularLevel) level).room(SpacebaseRun.hero.pos);
        ArrayList<Integer> cells = roomTeleportCells(level, room, SpacebaseRun.hero.pos);
        if (cells.isEmpty()) return;
        SpacebaseRun.hero.interrupt();
        GLog.w(Messages.get(this, "displaced"));
        int cell = Random.element(cells);
        boolean willFall = Level.pit[cell] && !SpacebaseRun.hero.flying;
        // appear() calls Hero.move(), which already presses terrain for grounded heroes.
        // Never press twice: that would trigger vents/mines or start a fall twice.
        PhaseShiftUpgrade.appear(SpacebaseRun.hero, cell);
        if (willFall || !SpacebaseRun.hero.isAlive()) return;
        SpacebaseRun.observe();
        GameScene.updateFog();
    }

    @Override
    protected boolean getCloser(int target) {
        if (Level.fieldOfView[target] && SpacebaseRun.level.distance(pos, target) > 2 && delay <= 0) {

            blink(target);
            spend(-1 / speed());
            return true;

        } else {

            delay--;
            return super.getCloser(target);

        }
    }

    private void blink(int target) {

        Ballistica route = new Ballistica(pos, target, Ballistica.PROJECTILE);
        int cell = route.collisionPos;

        //can't occupy the same cell as another char, so move back one.
        if (Actor.findChar(cell) != null && cell != this.pos)
            cell = route.path.get(route.dist - 1);

        if (Level.avoid[cell]) {
            ArrayList<Integer> candidates = new ArrayList<>();
            for (int n : PathFinder.NEIGHBOURS8) {
                cell = route.collisionPos + n;
                if (Level.passable[cell] && Actor.findChar(cell) == null) {
                    candidates.add(cell);
                }
            }
            if (candidates.size() > 0)
                cell = Random.element(candidates);
            else {
                delay = BLINK_DELAY;
                return;
            }
        }

        PhaseShiftUpgrade.appear(this, cell);

        delay = BLINK_DELAY;
    }

    @Override
    public int attackSkill(Char target) {
        return 40;
    }

    @Override
    public int drRoll() {
        return Random.NormalIntRange(0, 10);
    }

    public boolean scareByStationCat(int catId) {
        if (scaredByStationCat || buff(Terror.class) != null) {
            return false;
        }

        scaredByStationCat = true;
        Buff.affect(this, Terror.class, Terror.DURATION).object = catId;
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SCARED_BY_STATION_CAT, scaredByStationCat);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        flying = true; // Older saved Sirens were ground-based.
        scaredByStationCat = bundle.getBoolean(SCARED_BY_STATION_CAT);
    }

    private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();

    static {
        RESISTANCES.add(Vampiric.class);
    }

    @Override
    public HashSet<Class<?>> resistances() {
        return RESISTANCES;
    }

    private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();

    static {
        IMMUNITIES.add(Sleep.class);
    }

    @Override
    public HashSet<Class<?>> immunities() {
        return IMMUNITIES;
    }
}
