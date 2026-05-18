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
package com.wafitz.pixelspacebase.levels;

import com.wafitz.pixelspacebase.Bones;
import com.wafitz.pixelspacebase.Challenges;
import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.Actor;
import com.wafitz.pixelspacebase.actors.mobs.Bestiary;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.npcs.Survivor;
import com.wafitz.pixelspacebase.items.plasmids.Plasmid;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.modules.TechModule;
import com.wafitz.pixelspacebase.items.upgrades.Upgrade;
import com.wafitz.pixelspacebase.levels.Room.Type;
import com.wafitz.pixelspacebase.levels.painters.Painter;
import com.wafitz.pixelspacebase.levels.painters.Workshop;
import com.wafitz.pixelspacebase.levels.vents.ChillingVent;
import com.wafitz.pixelspacebase.levels.vents.ExplosiveVent;
import com.wafitz.pixelspacebase.levels.vents.FireVent;
import com.wafitz.pixelspacebase.levels.vents.Vent;
import com.wafitz.pixelspacebase.levels.vents.WornVent;
import com.watabou.utils.Bundle;
import com.watabou.utils.Graph;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class RegularLevel extends Level {

    ArrayList<Room> rooms;

    Room roomEntrance;
    Room roomExit;

    private ArrayList<Room.Type> specials;

    public int secretDoors;

    private static final int[] SURVIVOR_DEPTHS = {2, 3, 4, 6, 7, 8, 9, 11, 12};

    @Override
    protected boolean build() {

        if (!initRooms()) {
            return false;
        }

        int distance;
        int retry = 0;
        int minDistance = (int) Math.sqrt(rooms.size());
        do {
            do {
                roomEntrance = Random.element(rooms);
            } while (roomEntrance.width() < 4 || roomEntrance.height() < 4);

            do {
                roomExit = Random.element(rooms);
            } while (roomExit == roomEntrance || roomExit.width() < 4 || roomExit.height() < 4);

            Graph.buildDistanceMap(rooms, roomExit);
            distance = roomEntrance.distance();

            if (retry++ > 10) {
                return false;
            }

        } while (distance < minDistance);

        roomEntrance.type = Type.ENTRANCE;
        roomExit.type = Type.EXIT;

        ArrayList<Room> connected = new ArrayList<>();
        connected.add(roomEntrance);

        Graph.buildDistanceMap(rooms, roomExit);
        List<Room> path = Graph.buildPath(rooms, roomEntrance, roomExit);

        Room room = roomEntrance;
        for (Room next : path) {
            room.connect(next);
            room = next;
            connected.add(room);
        }

        Graph.setPrice(path, roomEntrance.distance);

        Graph.buildDistanceMap(rooms, roomExit);
        path = Graph.buildPath(rooms, roomEntrance, roomExit);

        room = roomEntrance;
        for (Room next : path) {
            room.connect(next);
            room = next;
            connected.add(room);
        }

        int nConnected = (int) (rooms.size() * Random.Float(0.5f, 0.7f));
        while (connected.size() < nConnected) {

            Room cr = Random.element(connected);
            Room or = Random.element(cr.neigbours);
            if (!connected.contains(or)) {

                cr.connect(or);
                connected.add(or);
            }
        }

        if (SpacebaseRun.workshopOnLevel()) {
            Room workshop = null;
            for (Room r : roomEntrance.connected.keySet()) {
                if (r.connected.size() == 1
                        && Workshop.canHostFixedLayout(r)
                        && ((r.width() - 1) * (r.height() - 1) >= Workshop.spaceNeeded())) {
                    workshop = r;
                    break;
                }
            }

            if (workshop == null) {
                return false;
            } else {
                workshop.type = Room.Type.WORKSHOP;
            }
        }

        specials = new ArrayList<>(Room.SPECIALS);
        if (SpacebaseRun.bossLevel(SpacebaseRun.depth + 1)) {
            specials.remove(Room.Type.WEAK_FLOOR);
        }
        if (SpacebaseRun.isChallenged(Challenges.NO_ARMOR)) {
            //no sense in giving an armor reward room on a run with no armor.
            specials.remove(Room.Type.CRYPT);
        }
        if (SpacebaseRun.isChallenged(Challenges.NO_HERBALISM)) {
            //sorry warden, no lucky sungrass or blandfruit devices for you!
            specials.remove(Room.Type.GARDEN);
        }
        if (!assignRoomType())
            return false;

        paint();
        paintWater();
        paintLightedVents();

        placeFloorBreaker();

        placeVents();

        return true;
    }

    void placeSign() {
        while (true) {
            int pos = pointToCell(roomEntrance.random());
            if (pos != entrance && vents.get(pos) == null && findMob(pos) == null) {
                map[pos] = Terrain.SIGN;
                break;
            }
        }
    }

    void placeFloorBreaker() {
        if (roomEntrance == null) {
            return;
        }

        for (int tries = 0; tries < 80; tries++) {
            Room room = Random.element(rooms);
            if (room == null || room.type == Type.NULL || room.type == Type.PASSAGE || room.type == Type.TUNNEL) {
                continue;
            }

            int pos = pointToCell(room.random());
            if (pos != entrance
                    && pos != exit
                    && map[pos] == Terrain.EMPTY
                    && vents.get(pos) == null
                    && mines.get(pos) == null
                    && heaps.get(pos) == null
                    && findMob(pos) == null) {
                map[pos] = Terrain.BREAKER;
                return;
            }
        }

        int pos = pointToCell(roomEntrance.random());
        if (map[pos] == Terrain.EMPTY && pos != entrance) {
            map[pos] = Terrain.BREAKER;
        }
    }

    boolean initRooms() {

        rooms = new ArrayList<>();
        split(new Rect(0, 0, width() - 1, height() - 1));

        if (rooms.size() < 8) {
            return false;
        }

        Room[] ra = rooms.toArray(new Room[0]);
        for (int i = 0; i < ra.length - 1; i++) {
            for (int j = i + 1; j < ra.length; j++) {
                ra[i].addNeigbour(ra[j]);
            }
        }

        return true;
    }

    protected boolean assignRoomType() {

        int specialRooms = 0;
        boolean pitMade = false;

        for (Room r : rooms) {
            if (r.type == Type.NULL &&
                    r.connected.size() == 1) {

                if (specials.size() > 0 &&
                        r.width() > 3 && r.height() > 3 &&
                        Random.Int(specialRooms * specialRooms + 2) == 0) {

                    if (pitRoomNeeded && !pitMade) {

                        r.type = Type.PIT;
                        pitMade = true;

                        specials.remove(Type.ARMORY);
                        specials.remove(Type.CRYPT);
                        specials.remove(Type.LABORATORY);
                        specials.remove(Type.LIBRARY);
                        specials.remove(Type.STATUE);
                        specials.remove(Type.TREASURY);
                        specials.remove(Type.VAULT);
                        specials.remove(Type.WEAK_FLOOR);

                    } else if (SpacebaseRun.depth % 5 == 2 && specials.contains(Type.LABORATORY)) {

                        r.type = Type.LABORATORY;

                    } else if (SpacebaseRun.depth >= SpacebaseRun.transmutation && specials.contains(Type.MAGIC_WELL)) {

                        r.type = Type.MAGIC_WELL;

                    } else {

                        int n = specials.size();
                        r.type = specials.get(Math.min(Random.Int(n), Random.Int(n)));
                        if (r.type == Type.WEAK_FLOOR) {
                            weakFloorCreated = true;
                        }

                    }

                    Room.useType(r.type);
                    specials.remove(r.type);
                    specialRooms++;

                } else if (Random.Int(2) == 0) {

                    ArrayList<Room> neigbours = new ArrayList<>();
                    for (Room n : r.neigbours) {
                        if (!r.connected.containsKey(n) &&
                                !Room.SPECIALS.contains(n.type) &&
                                n.type != Type.PIT) {

                            neigbours.add(n);
                        }
                    }
                    if (neigbours.size() > 1) {
                        r.connect(Random.element(neigbours));
                    }
                }
            }
        }

        if (pitRoomNeeded && !pitMade) return false;

        int count = 0;
        for (Room r : rooms) {
            if (r.type == Type.NULL) {
                int connections = r.connected.size();
                if (connections == 0) {

                } else if (Random.Int(connections * connections) == 0) {
                    r.type = Type.STANDARD;
                    count++;
                } else {
                    r.type = Type.TUNNEL;
                }
            }
        }

        while (count < 6) {
            Room r = randomRoom(Type.TUNNEL, 20);
            if (r != null) {
                r.type = Type.STANDARD;
                count++;
            } else {
                return false;
            }
        }

        return true;
    }

    void paintWater() {
        boolean[] lake = water();
        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.EMPTY && lake[i]) {
                map[i] = Terrain.WATER;
            }
        }
    }

    void paintLightedVents() {
        boolean[] lightedvent = lightedvent();

        if (feeling == Feeling.LIGHTEDVENT) {

            for (Room room : rooms) {
                if (room.type != Type.NULL && room.type != Type.PASSAGE && room.type != Type.TUNNEL) {
                    lightedvent[(room.left + 1) + (room.top + 1) * width()] = true;
                    lightedvent[(room.right - 1) + (room.top + 1) * width()] = true;
                    lightedvent[(room.left + 1) + (room.bottom - 1) * width()] = true;
                    lightedvent[(room.right - 1) + (room.bottom - 1) * width()] = true;
                }
            }
        }

        for (int i = width() + 1; i < length() - width() - 1; i++) {
            if (map[i] == Terrain.EMPTY && lightedvent[i]) {
                int count = 1;
                for (int n : PathFinder.NEIGHBOURS8) {
                    if (lightedvent[i + n]) {
                        count++;
                    }
                }
                map[i] = (Random.Float() < count / 12f) ? Terrain.OFFVENT : Terrain.LIGHTEDVENT;
            }
        }
    }

    protected abstract boolean[] water();

    protected abstract boolean[] lightedvent();

    private void placeVents() {

        int nVents = nVents();
        float[] ventChances = ventChances();
        Class<?>[] ventClasses = ventClasses();

        ArrayList<Integer> validCells = new ArrayList<>();

        for (int i = 0; i < length(); i++) {
            if (map[i] == Terrain.EMPTY) {

                if (SpacebaseRun.depth == 1) {
                    //extra check to prevent annoying inactive vents in hallways on floor 1
                    Room r = room(i);
                    if (r != null && r.type != Type.TUNNEL) {
                        validCells.add(i);
                    }
                } else
                    validCells.add(i);
            }
        }

        //no more than one trap every 5 valid tiles.
        nVents = Math.min(nVents, validCells.size() / 5);

        for (int i = 0; i < nVents; i++) {

            Integer ventPos = Random.element(validCells);
            validCells.remove(ventPos); //removes the integer object, not at the index

            try {
                Vent vent = ((Vent) ventClasses[Random.chances(ventChances)].newInstance()).hide();
                setVent(vent, ventPos);
                //some vents will not be hidden
                map[ventPos] = vent.visible ? Terrain.VENT : Terrain.HIDDEN_VENT;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int nVents() {
        return Random.NormalIntRange(1, 4 + (SpacebaseRun.depth / 2));
    }

    protected Class<?>[] ventClasses() {
        return new Class<?>[]{WornVent.class};
    }

    protected float[] ventChances() {
        return new float[]{1};
    }

    int minRoomSize = 7;
    private int maxRoomSize = 9;

    private void split(Rect rect) {

        int w = rect.width();
        int h = rect.height();

        if (w > maxRoomSize && h < minRoomSize) {

            int vw = Random.Int(rect.left + 3, rect.right - 3);
            split(new Rect(rect.left, rect.top, vw, rect.bottom));
            split(new Rect(vw, rect.top, rect.right, rect.bottom));

        } else if (h > maxRoomSize && w < minRoomSize) {

            int vh = Random.Int(rect.top + 3, rect.bottom - 3);
            split(new Rect(rect.left, rect.top, rect.right, vh));
            split(new Rect(rect.left, vh, rect.right, rect.bottom));

        } else if ((Random.Float() <= (minRoomSize * minRoomSize / rect.square()) && w <= maxRoomSize && h <= maxRoomSize) || w < minRoomSize || h < minRoomSize) {

            rooms.add((Room) new Room().set(rect));

        } else {

            if (Random.Float() < (float) (w - 2) / (w + h - 4)) {
                int vw = Random.Int(rect.left + 3, rect.right - 3);
                split(new Rect(rect.left, rect.top, vw, rect.bottom));
                split(new Rect(vw, rect.top, rect.right, rect.bottom));
            } else {
                int vh = Random.Int(rect.top + 3, rect.bottom - 3);
                split(new Rect(rect.left, rect.top, rect.right, vh));
                split(new Rect(rect.left, vh, rect.right, rect.bottom));
            }

        }
    }

    protected void paint() {

        for (Room r : rooms) {
            if (r.type != Type.NULL) {
                placeDoors(r);
                r.type.paint(this, r);
            } else {
                if (feeling == Feeling.CHASM && Random.Int(2) == 0) {
                    Painter.fill(this, r, Terrain.WALL);
                }
            }
        }

        for (Room r : rooms) {
            paintDoors(r);
        }
    }

    private void placeDoors(Room r) {
        for (Room n : r.connected.keySet()) {
            Room.Door door = r.connected.get(n);
            if (door == null) {

                Rect i = r.intersect(n);
                if (i.width() == 0) {
                    door = new Room.Door(
                            i.left,
                            Random.Int(i.top + 1, i.bottom));
                } else {
                    door = new Room.Door(
                            Random.Int(i.left + 1, i.right),
                            i.top);
                }

                r.connected.put(n, door);
                n.connected.put(r, door);
            }
        }
    }

    private void paintDoors(Room r) {
        for (Room n : r.connected.keySet()) {

            if (joinRooms(r, n)) {
                continue;
            }

            Room.Door d = r.connected.get(n);
            int door = d.x + d.y * width();

            switch (d.type) {
                case EMPTY:
                    map[door] = Terrain.EMPTY;
                    break;
                case TUNNEL:
                    map[door] = tunnelTile();
                    break;
                case REGULAR:
                    if (SpacebaseRun.depth <= 1) {
                        map[door] = Terrain.DOOR;
                    } else {
                        boolean secret = (SpacebaseRun.depth < 6 ? Random.Int(12 - SpacebaseRun.depth) : Random.Int(6)) == 0;
                        map[door] = secret ? Terrain.SECRET_DOOR : Terrain.DOOR;
                        if (secret) {
                            secretDoors++;
                        }
                    }
                    break;
                case UNLOCKED:
                    map[door] = Terrain.DOOR;
                    break;
                case HIDDEN:
                    map[door] = Terrain.SECRET_DOOR;
                    secretDoors++;
                    break;
                case BARRICADE:
                    map[door] = Random.Int(3) == 0 ? Terrain.BOOKSHELF : Terrain.BARRICADE;
                    break;
                case LOCKED:
                    map[door] = Terrain.LOCKED_DOOR;
                    break;
            }
        }
    }

    private boolean joinRooms(Room r, Room n) {

        if (r.type != Room.Type.STANDARD || n.type != Room.Type.STANDARD) {
            return false;
        }

        Rect w = r.intersect(n);
        if (w.left == w.right) {

            if (w.bottom - w.top < 3) {
                return false;
            }

            if (w.height() == Math.max(r.height(), n.height())) {
                return false;
            }

            if (r.width() + n.width() > maxRoomSize) {
                return false;
            }

            w.top += 1;
            w.bottom -= 0;

            w.right++;

            Painter.fill(this, w.left, w.top, 1, w.height(), Terrain.EMPTY);

        } else {

            if (w.right - w.left < 3) {
                return false;
            }

            if (w.width() == Math.max(r.width(), n.width())) {
                return false;
            }

            if (r.height() + n.height() > maxRoomSize) {
                return false;
            }

            w.left += 1;
            w.right -= 0;

            w.bottom++;

            Painter.fill(this, w.left, w.top, w.width(), 1, Terrain.EMPTY);
        }

        return true;
    }

    @Override
    public int nMobs() {
        if (SpacebaseRun.bossLevel()) {
            return 1;
        }

        switch (SpacebaseRun.depth) {
            case 1:
                //mobs are not randomly spawned on floor 1.
                return 0;
            default:
                return 2 + SpacebaseRun.depth % 5 + Random.Int(5);
        }
    }

    @Override
    protected void createMobs() {
        //on floor 1, 10 rats are created so the player can get level 2.
        int mobsToSpawn = SpacebaseRun.depth == 1 ? 10 : nMobs();

        ArrayList<Room> stdRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.type == Type.STANDARD) stdRooms.add(room);
        }
        Iterator<Room> stdRoomIter = stdRooms.iterator();

        while (mobsToSpawn > 0) {
            if (!stdRoomIter.hasNext())
                stdRoomIter = stdRooms.iterator();
            Room roomToSpawn = stdRoomIter.next();

            Mob mob = Bestiary.mob(SpacebaseRun.depth);
            mob.pos = pointToCell(roomToSpawn.random());

            if (findMob(mob.pos) == null && Level.passable[mob.pos]) {
                mobsToSpawn--;
                mobs.add(mob);

                //TODO: perhaps externalize this logic into a method. Do I want to make mobs more likely to clump deeper down?
                if (mobsToSpawn > 0 && Random.Int(4) == 0) {
                    mob = Bestiary.mob(SpacebaseRun.depth);
                    mob.pos = pointToCell(roomToSpawn.random());

                    if (findMob(mob.pos) == null && Level.passable[mob.pos]) {
                        mobsToSpawn--;
                        mobs.add(mob);
                    }
                }
            }
        }

        createSurvivor();
    }

    private void createSurvivor() {
        if (!hasSurvivorForDepth()) {
            return;
        }

        if (placeSurvivorInRoom(roomExit, 40)) {
            return;
        }

        if (placeSurvivorInRoom(roomEntrance, 20)) {
            return;
        }

        for (int tries = 0; tries < 40; tries++) {
            Room room = randomRoom(Room.Type.STANDARD, 10);
            if (placeSurvivorInRoom(room, 1)) {
                return;
            }
        }
    }

    private boolean placeSurvivorInRoom(Room room, int tries) {
        if (room == null) {
            return false;
        }

        for (int i = 0; i < tries; i++) {
            int cell = pointToCell(room.random());
            if (canPlaceSurvivor(cell)) {
                Survivor survivor = new Survivor();
                survivor.pos = cell;
                mobs.add(survivor);
                return true;
            }
        }

        return false;
    }

    private boolean hasSurvivorForDepth() {
        for (int depth : SURVIVOR_DEPTHS) {
            if (SpacebaseRun.depth == depth) {
                return true;
            }
        }
        return false;
    }

    private boolean canPlaceSurvivor(int cell) {
        return cell != entrance
                && cell != exit
                && Level.passable[cell]
                && (map[cell] == Terrain.EMPTY || map[cell] == Terrain.EMPTY_SP || map[cell] == Terrain.EMPTY_DECO)
                && vents.get(cell) == null
                && mines.get(cell) == null
                && heaps.get(cell) == null
                && Actor.findChar(cell) == null;
    }

    @Override
    public int randomRespawnCell() {
        int count = 0;
        int cell;

        while (true) {

            if (++count > 30) {
                return -1;
            }

            Room room = randomRoom(Room.Type.STANDARD, 10);
            if (room == null) {
                continue;
            }

            cell = pointToCell(room.random());
            if (!SpacebaseRun.visible[cell] && Actor.findChar(cell) == null && Level.passable[cell]) {
                return cell;
            }

        }
    }

    @Override
    public int randomDestination() {

        int cell;

        while (true) {

            Room room = Random.element(rooms);
            if (room == null) {
                continue;
            }

            cell = pointToCell(room.random());
            if (Level.passable[cell]) {
                return cell;
            }

        }
    }

    @Override
    protected void createItems() {

        int nItems = 3;
        int bonus = TechModule.getBonus(SpacebaseRun.hero, TechModule.Wealth.class);

        //just incase someone gets a ridiculous ring, cap this at 80%
        bonus = Math.min(bonus, 10);
        while (Random.Float() < (0.3f + bonus * 0.05f)) {
            nItems++;
        }

        for (int i = 0; i < nItems; i++) {
            Heap.Type type;
            switch (Random.Int(20)) {
                case 0:
                    type = Heap.Type.EMPTY_SPACESUIT;
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    type = Heap.Type.CHEST;
                    break;
                case 5:
                    type = SpacebaseRun.depth > 1 ? Heap.Type.CONFUSEDSHAPESHIFTER : Heap.Type.CHEST;
                    break;
                default:
                    type = Heap.Type.HEAP;
            }
            drop(Generator.random(), randomDropCell()).type = type;
        }

        for (Item item : itemsToSpawn) {
            int cell;
            do {
                cell = randomDropCell();
                if (item instanceof Upgrade) {
                    while (vents.get(cell) instanceof FireVent) {
                        cell = randomDropCell();
                    }

                } else if (item instanceof Plasmid) {
                    while (vents.get(cell) instanceof ChillingVent) {
                        cell = randomDropCell();
                    }
                }
            } while (vents.get(cell) instanceof ExplosiveVent);
            drop(item, cell).type = Heap.Type.HEAP;
        }

        Item item = Bones.get();
        if (item != null) {
            drop(item, randomDropCell()).type = Heap.Type.REMAINS;
        }
    }

    private Room randomRoom(Room.Type type, int tries) {
        for (int i = 0; i < tries; i++) {
            Room room = Random.element(rooms);
            if (room.type == type) {
                return room;
            }
        }
        return null;
    }

    public Room room(int pos) {
        for (Room room : rooms) {
            if (room.type != Type.NULL && room.inside(cellToPoint(pos))) {
                return room;
            }
        }

        return null;
    }

    private int randomDropCell() {
        while (true) {
            Room room = randomRoom(Room.Type.STANDARD, 1);
            if (room != null) {
                int pos = pointToCell(room.random());
                if (passable[pos]) {
                    return pos;
                }
            }
        }
    }

    @Override
    public int pitCell() {
        for (Room room : rooms) {
            if (room.type == Type.PIT) {
                return pointToCell(room.random());
            }
        }

        return super.pitCell();
    }

    public boolean hasWeakFloor() {
        for (Room room : rooms) {
            if (room.type == Type.WEAK_FLOOR) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void lightCurrentRoom(int cell, boolean[] fieldOfView) {
        if (rooms == null) {
            return;
        }

        int x = cell % width();
        int y = cell / width();
        for (Room room : rooms) {
            if (x > room.left && y > room.top && x < room.right && y < room.bottom) {
                for (int row = room.top + 1; row < room.bottom; row++) {
                    int pos = room.left + 1 + row * width();
                    for (int col = room.left + 1; col < room.right; col++, pos++) {
                        if (discoverable[pos]) {
                            fieldOfView[pos] = true;
                        }
                    }
                }
                return;
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("rooms", rooms);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        rooms = new ArrayList<>((Collection<Room>) ((Collection<?>) bundle.getCollection("rooms")));
        for (Room r : rooms) {
            if (r.type == Type.ENTRANCE) {
                roomEntrance = r;
            } else if (r.type == Type.EXIT || r.type == Type.BOSS_EXIT) {
                roomExit = r;
            }
        }
    }

}
