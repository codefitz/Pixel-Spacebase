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

import com.wafitz.pixelspacebase.Assets;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.Char;
import com.wafitz.pixelspacebase.actors.hero.Hero;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.SignalSiren;
import com.wafitz.pixelspacebase.effects.Speck;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.PetCarrier;
import com.wafitz.pixelspacebase.items.equippablemodules.EquippableModule;
import com.wafitz.pixelspacebase.items.food.MysteryMeat;
import com.wafitz.pixelspacebase.items.modules.Module;
import com.wafitz.pixelspacebase.items.upgrades.Upgrade;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.RegularLevel;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.messages.Messages;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.wafitz.pixelspacebase.sprites.StationCatSprite;
import com.wafitz.pixelspacebase.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.Arrays;

public class StationCat extends NPC {

    private boolean following;
    private static StationCat carriedCat;

    //A cat gets one worthwhile thing to bat around in each room, not every item it sees.
    private int rummagedDepth = -1;
    private int[] rummagedRooms = new int[0];

    private static final String FOLLOWING = "following";
    private static final String RUMMAGED_DEPTH = "rummagedDepth";
    private static final String RUMMAGED_ROOMS = "rummagedRooms";

    {
        spriteClass = StationCatSprite.class;
        HP = HT = 4;
        defenseSkill = 2;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(FOLLOWING, following);
        bundle.put(RUMMAGED_DEPTH, rummagedDepth);
        bundle.put(RUMMAGED_ROOMS, rummagedRooms);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        following = bundle.getBoolean(FOLLOWING);
        rummagedDepth = bundle.getInt(RUMMAGED_DEPTH);
        rummagedRooms = bundle.getIntArray(RUMMAGED_ROOMS);
        if (rummagedRooms == null) {
            rummagedRooms = new int[0];
        }
        ally = following;
        Quest.following = Quest.following || following;
    }

    @Override
    protected boolean act() {
        //A cat arriving on a new level waits until the player chooses to touch it.
        //This keeps it out of danger unless the player deliberately asks it to follow.
        if (!following) {
            spend(TICK);
            return true;
        }

        if (scareNearbySignalSiren()) {
            spend(TICK);
            return true;
        }

        Heap meatHeap = meatHeap();
        if (meatHeap != null && !heroLeftRoom(meatHeap)) {
            if (meatHeap.pos == pos) {
                eatMeat(meatHeap);
                spend(TICK);
                return true;
            }

            int oldPos = pos;
            if (getCloser(meatHeap.pos)) {
                spend(1 / speed());
                return moveSprite(oldPos, pos);
            }
        }

        Heap itemHeap = itemHeap();
        if (itemHeap != null && !heroLeftRoom(itemHeap)) {
            if (itemHeap.pos == pos) {
                batItem(itemHeap);
                spend(TICK);
                return true;
            }

            int oldPos = pos;
            if (getCloser(itemHeap.pos)) {
                spend(1 / speed());
                return moveSprite(oldPos, pos);
            }
        }

        int target = followTarget();
        int oldPos = pos;
        if (target != -1 && getCloser(target)) {
            spend(1 / speed());
            return moveSprite(oldPos, pos);
        }

        spend(TICK);
        return true;
    }

    private boolean scareNearbySignalSiren() {
        if (SpacebaseRun.visible == null) {
            return false;
        }

        for (Mob mob : SpacebaseRun.level.mobs) {
            if (mob instanceof SignalSiren
                    && mob.isAlive()
                    && SpacebaseRun.visible[mob.pos]
                    && SpacebaseRun.level.distance(pos, mob.pos) <= 3
                    && ((SignalSiren) mob).scareByStationCat(id())) {
                GLog.n(Messages.get(this, "scares_siren", Messages.get(mob, "name")));
                return true;
            }
        }

        return false;
    }

    private Heap itemHeap() {
        Heap closest = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Heap heap : SpacebaseRun.level.heaps.values()) {
            Room room = roomAt(heap.pos);
            if (heap.type != Heap.Type.HEAP || room == null || isWorkshopCell(heap.pos)
                    || !containsCatToy(heap) || roomHasBeenRummaged(room)) {
                continue;
            }

            int distance = SpacebaseRun.level.distance(pos, heap.pos);
            if (distance < closestDistance) {
                closest = heap;
                closestDistance = distance;
            }
        }

        return closest;
    }

    private Heap meatHeap() {
        if (HP >= HT) {
            return null;
        }

        Heap closest = null;
        int closestDistance = Integer.MAX_VALUE;
        for (Heap heap : SpacebaseRun.level.heaps.values()) {
            if (heap.type != Heap.Type.HEAP || rawMeat(heap) == null || heroLeftRoom(heap)) {
                continue;
            }

            int distance = SpacebaseRun.level.distance(pos, heap.pos);
            if (distance < closestDistance) {
                closest = heap;
                closestDistance = distance;
            }
        }
        return closest;
    }

    private MysteryMeat rawMeat(Heap heap) {
        for (Item item : heap.items) {
            if (item instanceof MysteryMeat) {
                return (MysteryMeat) item;
            }
        }
        return null;
    }

    private void eatMeat(Heap heap) {
        MysteryMeat meat = rawMeat(heap);
        if (meat != null && heap.removeOne(meat)) {
            HP = Math.min(HT, HP + Math.max(1, HT / 2));
            if (sprite != null) {
                sprite.emitter().burst(Speck.factory(Speck.HEALING), 1);
            }
        }
    }

    private void batItem(Heap heap) {
        Room room = roomAt(heap.pos);
        Item toy = catToy(heap);
        if (room == null || toy == null) {
            return;
        }

        markRoomRummaged(room);

        int target;
        do {
            target = pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
        } while (!Level.passable[target] && !Level.avoid[target]);

        if (heap.removeOne(toy)) {
            SpacebaseRun.level.drop(toy, target).sprite.drop(pos);
        }
    }

    private boolean heroLeftRoom(Heap heap) {
        if (!(SpacebaseRun.level instanceof RegularLevel)) {
            return false;
        }

        Room itemRoom = ((RegularLevel) SpacebaseRun.level).room(heap.pos);
        return itemRoom != null && !itemRoom.inside(SpacebaseRun.level.cellToPoint(SpacebaseRun.hero.pos));
    }

    private boolean isWorkshopCell(int cell) {
        if (!(SpacebaseRun.level instanceof RegularLevel)) {
            return false;
        }

        Room room = ((RegularLevel) SpacebaseRun.level).room(cell);
        return room != null && room.type == Room.Type.WORKSHOP;
    }

    private Room roomAt(int cell) {
        if (!(SpacebaseRun.level instanceof RegularLevel)) {
            return null;
        }
        return ((RegularLevel) SpacebaseRun.level).room(cell);
    }

    private boolean containsCatToy(Heap heap) {
        return catToy(heap) != null;
    }

    private Item catToy(Heap heap) {
        for (Item item : heap.items) {
            if (item instanceof Module || item instanceof EquippableModule || item instanceof Upgrade) {
                return item;
            }
        }
        return null;
    }

    private boolean roomHasBeenRummaged(Room room) {
        resetRummagedRoomsForCurrentDepth();
        int roomKey = roomKey(room);
        for (int key : rummagedRooms) {
            if (key == roomKey) {
                return true;
            }
        }
        return false;
    }

    private void markRoomRummaged(Room room) {
        resetRummagedRoomsForCurrentDepth();
        rummagedRooms = Arrays.copyOf(rummagedRooms, rummagedRooms.length + 1);
        rummagedRooms[rummagedRooms.length - 1] = roomKey(room);
    }

    private void resetRummagedRoomsForCurrentDepth() {
        if (rummagedDepth != SpacebaseRun.depth) {
            rummagedDepth = SpacebaseRun.depth;
            rummagedRooms = new int[0];
        }
    }

    private int roomKey(Room room) {
        return room.left | room.top << 6 | room.right << 12 | room.bottom << 18;
    }

    private int followTarget() {
        if (SpacebaseRun.level.distance(pos, SpacebaseRun.hero.pos) <= 2) {
            return -1;
        }

        int best = -1;
        int bestDistance = Integer.MAX_VALUE;
        for (int offset : PathFinder.NEIGHBOURS8) {
            int cell = SpacebaseRun.hero.pos + offset;
            if (cell == pos) {
                return -1;
            }
            if (Level.passable[cell] && Actor.findChar(cell) == null) {
                int distance = SpacebaseRun.level.distance(pos, cell);
                if (distance < bestDistance) {
                    best = cell;
                    bestDistance = distance;
                }
            }
        }
        return best;
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
    public void die(Object src) {
        Sample.INSTANCE.play(Assets.SND_LULLABY, 0.7f, 0.7f, 0.8f);
        GLog.w(Messages.get(this, "dies"));
        Quest.dead = true;
        Quest.following = false;
        Quest.gone = false;
        Quest.carried = false;
        carriedCat = null;
        super.die(src);
    }

    @Override
    public boolean interact() {
        PetCarrier carrier = PetCarrier.equippedBy(SpacebaseRun.hero);
        if (carrier != null) {
            if (carrier.hasCat()) {
                yell(Messages.get(this, "carrier_full"));
                return true;
            }
            carryInto(carrier);
            return true;
        }

        if (!following) {
            following = true;
            ally = true;
            Quest.following = true;
            Quest.carried = false;
            yell(Messages.get(this, "petted"));
        } else {
            int oldPos = pos;
            move(SpacebaseRun.hero.pos);
            SpacebaseRun.hero.move(oldPos);

            moveSprite(oldPos, pos);
            SpacebaseRun.hero.sprite.move(SpacebaseRun.hero.pos, oldPos);

            SpacebaseRun.hero.spend(1 / SpacebaseRun.hero.speed());
            SpacebaseRun.hero.busy();
        }

        return true;
    }

    private void follow() {
        following = true;
        ally = true;
        HP = Math.max(1, HP);
    }

    private void waitHere() {
        following = false;
        ally = false;
        HP = Math.max(1, HP);
    }

    public static boolean isFollowing() {
        return Quest.following && !Quest.dead && !Quest.gone && !Quest.carried;
    }

    public static boolean isCarried() {
        return Quest.carried && !Quest.dead;
    }

    private void carryInto(PetCarrier carrier) {
        carrier.putCat();
        following = false;
        ally = false;
        Quest.following = false;
        Quest.gone = false;
        Quest.carried = true;
        carriedCat = null;
        if (SpacebaseRun.level != null && SpacebaseRun.level.mobs != null) {
            SpacebaseRun.level.mobs.remove(this);
        }
        Actor.remove(this);
        if (sprite != null) {
            sprite.killAndErase();
        }
        yell(Messages.get(this, "carried"));
    }

    public static boolean releaseFromCarrier(Hero hero, PetCarrier carrier) {
        if (hero == null || carrier == null || !carrier.hasCat() || SpacebaseRun.level == null || SpacebaseRun.level.mobs == null) {
            return false;
        }

        int cell = carrierReleaseCell(SpacebaseRun.level);
        if (cell == -1) {
            return false;
        }

        StationCat cat = new StationCat();
        cat.waitHere();
        cat.pos = cell;
        carrier.removeCat();
        Quest.carried = false;
        Quest.following = false;
        Quest.gone = false;
        GameScene.add(cat);
        return true;
    }

    public static void carryFollowerFrom(Level level) {
        if (!isFollowing() || level == null || level.mobs == null) {
            return;
        }

        for (com.wafitz.pixelspacebase.actors.mobs.Mob mob : level.mobs.toArray(new com.wafitz.pixelspacebase.actors.mobs.Mob[0])) {
            if (mob instanceof StationCat && ((StationCat) mob).following) {
                carriedCat = (StationCat) mob;
                carriedCat.waitHere();
                Quest.following = false;
                Quest.gone = false;
                level.mobs.remove(mob);
                Actor.remove(mob);
                return;
            }
        }
    }

    public static void placeFollowerOn(Level level) {
        if (carriedCat == null || level == null || level.mobs == null || SpacebaseRun.hero == null) {
            carriedCat = null;
            return;
        }

        for (com.wafitz.pixelspacebase.actors.mobs.Mob mob : level.mobs.toArray(new com.wafitz.pixelspacebase.actors.mobs.Mob[0])) {
            if (mob instanceof StationCat) {
                level.mobs.remove(mob);
                Actor.remove(mob);
            }
        }

        StationCat cat = carriedCat;
        carriedCat = null;
        cat.waitHere();
        cat.pos = followerCell(level);
        level.mobs.add(cat);
        Actor.add(cat);
    }

    public static void abandonFollower(Level level) {
        if (level != null && level.mobs != null) {
            for (com.wafitz.pixelspacebase.actors.mobs.Mob mob : level.mobs) {
                if (mob instanceof StationCat) {
                    ((StationCat) mob).waitHere();
                }
            }
        }
        carriedCat = null;
        Quest.following = false;
        Quest.gone = false;
        Quest.carried = false;
    }

    public static boolean canSpawnOnFirstLevel() {
        return SpacebaseRun.depth == 1 && !Quest.following && !Quest.dead && !Quest.gone && !Quest.carried;
    }

    public static boolean canSpawnInMaintenanceLevel() {
        if (Quest.following || Quest.dead || Quest.carried) {
            return false;
        }
        if (canSpawnOnFirstLevel()) {
            return true;
        }
        return Quest.gone
                && !Quest.rescueSpawned
                && SpacebaseRun.depth >= 1
                && SpacebaseRun.depth <= 4
                && PetCarrier.carriedBy(SpacebaseRun.hero) != null;
    }

    private static int followerCell(Level level) {
        for (int offset : PathFinder.NEIGHBOURS8) {
            int cell = SpacebaseRun.hero.pos + offset;
            if (level.insideMap(cell) && Level.passable[cell] && Actor.findChar(cell) == null) {
                return cell;
            }
        }

        int cell = level.randomRespawnCell();
        return cell != -1 ? cell : SpacebaseRun.hero.pos;
    }

    private static int carrierReleaseCell(Level level) {
        for (int offset : PathFinder.NEIGHBOURS8) {
            int cell = SpacebaseRun.hero.pos + offset;
            if (level.insideMap(cell) && Level.passable[cell] && Actor.findChar(cell) == null) {
                return cell;
            }
        }
        return level.randomRespawnCell();
    }

    public static class Quest {

        private static boolean following;
        private static boolean dead;
        private static boolean gone;
        private static boolean carried;
        private static boolean rescueSpawned;

        private static final String NODE = "stationCat";
        private static final String FOLLOWING = "following";
        private static final String DEAD = "dead";
        private static final String GONE = "gone";
        private static final String CARRIED = "carried";
        private static final String RESCUE_SPAWNED = "rescueSpawned";

        public static void reset() {
            following = false;
            dead = false;
            gone = false;
            carried = false;
            rescueSpawned = false;
            carriedCat = null;
        }

        public static void markCarried() {
            if (!dead) {
                carried = true;
                following = false;
                gone = false;
            }
        }

        public static void markSpawnedInMaintenanceLevel() {
            if (gone && PetCarrier.carriedBy(SpacebaseRun.hero) != null) {
                rescueSpawned = true;
            }
        }

        public static void storeInBundle(Bundle bundle) {
            Bundle node = new Bundle();
            node.put(FOLLOWING, following);
            node.put(DEAD, dead);
            node.put(GONE, gone);
            node.put(CARRIED, carried);
            node.put(RESCUE_SPAWNED, rescueSpawned);
            bundle.put(NODE, node);
        }

        public static void restoreFromBundle(Bundle bundle) {
            Bundle node = bundle.getBundle(NODE);
            if (!node.isNull()) {
                following = node.getBoolean(FOLLOWING);
                dead = node.getBoolean(DEAD);
                gone = node.getBoolean(GONE);
                carried = node.getBoolean(CARRIED);
                rescueSpawned = node.getBoolean(RESCUE_SPAWNED);
            } else {
                reset();
            }
        }
    }
}
