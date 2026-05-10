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
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.blobs.Blob;
import com.wafitz.pixelspacebase.actors.blobs.Regrowth;
import com.wafitz.pixelspacebase.actors.buffs.Buff;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.effects.MagicMissile;
import com.wafitz.pixelspacebase.items.Dewdrop;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.weapon.melee.DM3000Launcher;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.mechanics.Ballistica;
import com.wafitz.pixelspacebase.mines.AdrenalBoost;
import com.wafitz.pixelspacebase.mines.AlienEgg;
import com.wafitz.pixelspacebase.mines.KoltoPod;
import com.wafitz.pixelspacebase.mines.Mine;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.ItemSpriteSheet;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class EMP extends Blaster {

    // Compatibility note: player-facing text presents this as the Repair Blaster.
    // Keep the internal EMP class name for saves, generator tables, and message keys.

    {
        image = ItemSpriteSheet.EMP;

        collisionProperties = Ballistica.STOP_TERRAIN;
    }

    //the actual affected cells
    private HashSet<Integer> affectedCells;
    //the cells to trace growth particles to, for visual effects.
    private HashSet<Integer> visualCells;
    private int direction = 0;

    private static final int BASE_UNLOCK_CHANCE = 45;
    private static final int UNLOCK_CHANCE_PER_LEVEL = 10;

    @Override
    protected void onZap(Ballistica bolt) {

        boolean repaired = false;

        for (int i : affectedCells) {
            repaired |= repairTerrain(i);
            repaired |= repairHeap(Dungeon.level.heaps.get(i));
            repaired |= repairMachine(Actor.findChar(i));
        }

        if (!repaired) {
            GLog.i("The repair beam finds nothing mechanical to fix.");
        }
    }

    private boolean repairTerrain(int cell) {
        switch (Dungeon.level.map[cell]) {
            case Terrain.VENT:
            case Terrain.HIDDEN_VENT:
                Level.set(cell, Terrain.LIGHTEDVENT);
                GameScene.updateMap(cell);
                return true;
            case Terrain.OFFVENT:
                Level.set(cell, Terrain.LIGHTEDVENT);
                GameScene.updateMap(cell);
                return true;
            case Terrain.LOCKED_DOOR:
                return repairLock(cell);
            case Terrain.BARRICADE:
            case Terrain.BOOKSHELF:
                Level.set(cell, Terrain.DOOR);
                GameScene.updateMap(cell);
                return true;
            default:
                return false;
        }
    }

    private boolean isRepairableTerrain(int cell) {
        if (!Dungeon.level.insideMap(cell)) {
            return false;
        }
        switch (Dungeon.level.map[cell]) {
            case Terrain.VENT:
            case Terrain.HIDDEN_VENT:
            case Terrain.OFFVENT:
            case Terrain.LOCKED_DOOR:
            case Terrain.BARRICADE:
            case Terrain.BOOKSHELF:
                return true;
            default:
                return false;
        }
    }

    private boolean repairHeap(Heap heap) {
        if (heap == null || (heap.type != Heap.Type.LOCKED_CHEST && heap.type != Heap.Type.CRYSTAL_CHEST)) {
            return false;
        }

        if (unlockRoll()) {
            heap.type = Heap.Type.CHEST;
            GLog.p(Messages.get(this, "lock_opened"));
        } else {
            heap.type = Heap.Type.JAMMED_CHEST;
            GLog.w(Messages.get(this, "lock_jammed"));
        }
        if (heap.sprite != null) {
            heap.sprite.view(heap.image(), heap.glowing());
        }
        return true;
    }

    private boolean repairLock(int cell) {
        if (unlockRoll()) {
            Level.set(cell, Terrain.DOOR);
            GLog.p(Messages.get(this, "lock_opened"));
        } else {
            Level.set(cell, Terrain.BARRICADE);
            GLog.w(Messages.get(this, "lock_jammed"));
        }
        GameScene.updateMap(cell);
        return true;
    }

    private boolean unlockRoll() {
        int chance = unlockChance();
        GLog.w(Messages.get(this, "unlock_chance", chance));
        return Random.Int(100) < chance;
    }

    private int unlockChance() {
        return Math.min(95, BASE_UNLOCK_CHANCE + Math.max(0, level()) * UNLOCK_CHANCE_PER_LEVEL);
    }

    private boolean repairMachine(Char ch) {
        if (ch == null || !ch.properties().contains(Char.Property.MACHINE)) {
            return false;
        }

        if (isHostileMachine(ch)) {
            int damage = Math.max(1, 12 + chargesPerCast() * 6 + level() * 4);
            ch.damage(damage, this);
            if (ch.sprite != null) {
                ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 3);
            }
            return true;
        }

        int repair = Math.max(1, 8 + chargesPerCast() * 4 + level() * 3);
        ch.HP = Math.min(ch.HT, ch.HP + repair);
        if (ch.sprite != null) {
            ch.sprite.emitter().burst(Speck.factory(Speck.HEALING), 2);
        }
        return true;
    }

    private boolean isHostileMachine(Char ch) {
        return ch instanceof Mob && ((Mob) ch).hostile;
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

            if (device instanceof AlienEgg.Device) {
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
            floor.mine(new AdrenalBoost.Device(), cells.next());
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
        if (isHostileMachine(defender)) {
            defender.damage(Math.max(1, damage / 2 + Math.max(0, launcher.level()) * 2), this);
        } else if (defender.properties().contains(Char.Property.MACHINE)) {
            defender.HP = Math.min(defender.HT, defender.HP + Math.max(1, damage / 2));
        }

    }

    protected void fx(Ballistica bolt, Callback callback) {

        affectedCells = new HashSet<>();
        visualCells = new HashSet<>();

        int maxDist = Math.round(1.2f + chargesPerCast() * .8f);
        int dist = Math.min(bolt.dist, maxDist);

        for (int c : bolt.subPath(1, dist)) {
            affectedCells.add(c);
        }
        affectedCells.add(bolt.collisionPos);
        if (bolt.dist + 1 < bolt.path.size()) {
            int blockedCell = bolt.path.get(bolt.dist + 1);
            if (isRepairableTerrain(blockedCell)) {
                affectedCells.add(blockedCell);
            }
        }

        MagicMissile.whiteLight(curUser.sprite.parent, bolt.sourcePos, bolt.path.get(dist), callback);

        Sample.INSTANCE.play(Assets.SND_ZAP);
    }

    @Override
    protected int initialCharges() {
        return 3;
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
