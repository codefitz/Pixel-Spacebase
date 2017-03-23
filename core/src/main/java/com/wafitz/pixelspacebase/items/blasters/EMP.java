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
package com.wafitz.pixelspacebase.items.blasters;

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.Dungeon;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.Regrowth;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.effects.MagicMissile;
import com.wafitz.pixelspacebase.items.Dewdrop;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Launcher;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.mines.AlienPlant;
import com.wafitz.pixelspacebase.mines.Boost;
import com.wafitz.pixelspacebase.mines.Healing;
import com.wafitz.pixelspacebase.mines.Mine;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class EMP extends Blaster {

    {
        image = ItemSpriteSheet.EMP;

        collisionProperties = Ballistica.STOP_TERRAIN;
    }

    //the actual affected cells
    private HashSet<Integer> affectedCells;
    //the cells to trace growth particles to, for visual effects.
    private HashSet<Integer> visualCells;
    private int direction = 0;

    @Override
    protected void onZap(Ballistica bolt) {

        //ignore tiles which can't have anything grow in them.
        for (Iterator<Integer> i = affectedCells.iterator(); i.hasNext(); ) {
            int c = Dungeon.level.map[i.next()];
            if (!(c == Terrain.EMPTY ||
                    c == Terrain.EMBERS ||
                    c == Terrain.EMPTY_DECO ||
                    c == Terrain.LIGHTEDVENT ||
                    c == Terrain.OFFVENT)) {
                i.remove();
            }
        }

        float numMines, numDews, numPods, numStars;

        int chrgUsed = chargesPerCast();
        //numbers greater than n*100% means n guaranteed mines, e.g. 210% = 2 mines w/10% chance for 3 mines.
        numMines = 0.2f + chrgUsed * chrgUsed * 0.020f; //scales from 22% to 220%
        numDews = 0.05f + chrgUsed * chrgUsed * 0.016f; //scales from 6.6% to 165%
        numPods = 0.02f + chrgUsed * chrgUsed * 0.013f; //scales from 3.3% to 135%
        numStars = (chrgUsed * chrgUsed * chrgUsed / 5f) * 0.005f; //scales from 0.1% to 100%
        placeMines(numMines, numDews, numPods, numStars);

        for (int i : affectedCells) {
            int c = Dungeon.level.map[i];
            if (c == Terrain.EMPTY ||
                    c == Terrain.EMBERS ||
                    c == Terrain.EMPTY_DECO) {
                Level.set(i, Terrain.LIGHTEDVENT);
            }

            Char ch = Actor.findChar(i);
            if (ch != null) {
                processSoulMark(ch, chargesPerCast());
            }

            GameScene.add(Blob.device(i, 10, Regrowth.class));

        }
    }

    private void spreadRegrowth(int cell, float strength) {
        if (strength >= 0 && Level.passable[cell] && !Level.losBlocking[cell]) {
            affectedCells.add(cell);
            if (strength >= 1.5f) {
                spreadRegrowth(cell + PathFinder.CIRCLE8[left(direction)], strength - 1.5f);
                spreadRegrowth(cell + PathFinder.CIRCLE8[direction], strength - 1.5f);
                spreadRegrowth(cell + PathFinder.CIRCLE8[right(direction)], strength - 1.5f);
            } else {
                visualCells.add(cell);
            }
        } else if (!Level.passable[cell] || Level.losBlocking[cell])
            visualCells.add(cell);
    }

    private void placeMines(float numMines, float numDews, float numPods, float numStars) {
        Iterator<Integer> cells = affectedCells.iterator();
        Level floor = Dungeon.level;

        while (cells.hasNext() && Random.Float() <= numMines) {
            Mine.Device device = (Mine.Device) Generator.random(Generator.Category.DEVICE);

            if (device instanceof AlienPlant.Device) {
                if (Random.Int(15) - Dungeon.limitedDrops.alienTechDevice.count >= 0) {
                    floor.mine(device, cells.next());
                    Dungeon.limitedDrops.alienTechDevice.count++;
                }
            } else
                floor.mine(device, cells.next());

            numMines--;
        }

        while (cells.hasNext() && Random.Float() <= numDews) {
            floor.mine(new Dewcatcher.Device(), cells.next());
            numDews--;
        }

        while (cells.hasNext() && Random.Float() <= numPods) {
            floor.mine(new Devicepod.Device(), cells.next());
            numPods--;
        }

        while (cells.hasNext() && Random.Float() <= numStars) {
            floor.mine(new Boost.Device(), cells.next());
            numStars--;
        }

    }

    private int left(int direction) {
        return direction == 0 ? 7 : direction - 1;
    }

    private int right(int direction) {
        return direction == 7 ? 0 : direction + 1;
    }

    @Override
    public void onHit(DM3000Launcher launcher, Char attacker, Char defender, int damage) {
        //like pre-nerf vampiric enhancement, except with herbal healing buff

        int level = Math.max(0, launcher.level());

        // lvl 0 - 33%
        // lvl 1 - 43%
        // lvl 2 - 50%
        int maxValue = damage * (level + 2) / (level + 6);
        int effValue = Math.min(Random.IntRange(0, maxValue), attacker.HT - attacker.HP);

        Buff.affect(attacker, Healing.Health.class).boost(effValue);

    }

    protected void fx(Ballistica bolt, Callback callback) {

        affectedCells = new HashSet<>();
        visualCells = new HashSet<>();

        int maxDist = Math.round(1.2f + chargesPerCast() * .8f);
        int dist = Math.min(bolt.dist, maxDist);

        for (int i = 0; i < PathFinder.CIRCLE8.length; i++) {
            if (bolt.sourcePos + PathFinder.CIRCLE8[i] == bolt.path.get(1)) {
                direction = i;
                break;
            }
        }

        float strength = maxDist;
        for (int c : bolt.subPath(1, dist)) {
            strength--; //as we start at dist 1, not 0.
            if (!Level.losBlocking[c]) {
                affectedCells.add(c);
                spreadRegrowth(c + PathFinder.CIRCLE8[left(direction)], strength - 1);
                spreadRegrowth(c + PathFinder.CIRCLE8[direction], strength - 1);
                spreadRegrowth(c + PathFinder.CIRCLE8[right(direction)], strength - 1);
            } else {
                visualCells.add(c);
            }
        }

        //going to call this one manually
        visualCells.remove(bolt.path.get(dist));

        for (int cell : visualCells) {
            //this way we only get the cells at the tip, much better performance.
            MagicMissile.foliage(curUser.sprite.parent, bolt.sourcePos, cell, null);
        }
        MagicMissile.foliage(curUser.sprite.parent, bolt.sourcePos, bolt.path.get(dist), callback);

        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    protected int initialCharges() {
        return 1;
    }

    @Override
    //consumes all available charges, needs at least one.
    protected int chargesPerCast() {
        return Math.max(1, curCharges);
    }

    @Override
    public void launcherFx(DM3000Launcher.launcherParticle particle) {
        particle.color(ColorMath.random(0x004400, 0x88CC44));
        particle.am = 1f;
        particle.setLifespan(1f);
        particle.setSize(1f, 1.5f);
        particle.shuffleXY(0.5f);
        float dst = Random.Float(11f);
        particle.x -= dst;
        particle.y += dst;
    }

    private static class Dewcatcher extends Mine {

        {
            image = 12;
        }

        @Override
        public void activate() {

            int nDrops = Random.NormalIntRange(2, 8);

            ArrayList<Integer> candidates = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Level.passable[pos + i]) {
                    candidates.add(pos + i);
                }
            }

            for (int i = 0; i < nDrops && !candidates.isEmpty(); i++) {
                Integer c = Random.element(candidates);
                Dungeon.level.drop(new Dewdrop(), c).sprite.drop(pos);
                candidates.remove(c);
            }

        }

        //device is never dropped, only care about mines class
        static class Device extends Mine.Device {
            {
                mineClass = Dewcatcher.class;
            }
        }
    }

    private static class Devicepod extends Mine {

        {
            image = 13;
        }

        @Override
        public void activate() {

            int nDevices = Random.NormalIntRange(1, 5);

            ArrayList<Integer> candidates = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Level.passable[pos + i]) {
                    candidates.add(pos + i);
                }
            }

            for (int i = 0; i < nDevices && !candidates.isEmpty(); i++) {
                Integer c = Random.element(candidates);
                Dungeon.level.drop(Generator.random(Generator.Category.DEVICE), c).sprite.drop(pos);
                candidates.remove(c);
            }

        }

        //device is never dropped, only care about mines class
        static class Device extends Mine.Device {
            {
                mineClass = Devicepod.class;
            }
        }

    }

}
