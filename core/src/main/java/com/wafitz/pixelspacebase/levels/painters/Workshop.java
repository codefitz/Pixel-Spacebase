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
package com.wafitz.pixelspacebase.levels.painters;

import com.wafitz.pixelspacebase.SpacebaseRun;
import com.wafitz.pixelspacebase.actors.hero.Belongings;
import com.wafitz.pixelspacebase.actors.mobs.Mob;
import com.wafitz.pixelspacebase.actors.mobs.npcs.ArpTrader;
import com.wafitz.pixelspacebase.actors.mobs.npcs.MakerBot;
import com.wafitz.pixelspacebase.items.Bomb;
import com.wafitz.pixelspacebase.items.Clone;
import com.wafitz.pixelspacebase.items.DroneController;
import com.wafitz.pixelspacebase.items.EnhancementChip;
import com.wafitz.pixelspacebase.items.plasmids.HealingPlasmid;
import com.wafitz.pixelspacebase.items.Generator;
import com.wafitz.pixelspacebase.items.Heap;
import com.wafitz.pixelspacebase.items.Item;
import com.wafitz.pixelspacebase.items.PortableRender;
import com.wafitz.pixelspacebase.items.Torch;
import com.wafitz.pixelspacebase.items.TorchBattery;
import com.wafitz.pixelspacebase.items.WeaponTuner;
import com.wafitz.pixelspacebase.items.armor.Armor;
import com.wafitz.pixelspacebase.items.armor.HoverPod;
import com.wafitz.pixelspacebase.items.armor.HunterSpaceSuit;
import com.wafitz.pixelspacebase.items.armor.Loader;
import com.wafitz.pixelspacebase.items.armor.SpaceSuit;
import com.wafitz.pixelspacebase.items.equippablemodules.EquippableModule;
import com.wafitz.pixelspacebase.items.equippablemodules.TimeFolder;
import com.wafitz.pixelspacebase.items.blasters.Blaster;
import com.wafitz.pixelspacebase.items.containers.BlasterHolster;
import com.wafitz.pixelspacebase.items.containers.OrdnanceKit;
import com.wafitz.pixelspacebase.items.containers.UtilityKit;
import com.wafitz.pixelspacebase.items.containers.PlasmidKit;
import com.wafitz.pixelspacebase.items.food.SynthesizedFood;
import com.wafitz.pixelspacebase.items.upgrades.RepairUpgrade;
import com.wafitz.pixelspacebase.items.upgrades.DiagnosticScanUpgrade;
import com.wafitz.pixelspacebase.items.upgrades.MappingUpgrade;
import com.wafitz.pixelspacebase.items.weapon.melee.BrightHammer;
import com.wafitz.pixelspacebase.items.weapon.melee.DualBlade;
import com.wafitz.pixelspacebase.items.weapon.melee.RaiderBlade;
import com.wafitz.pixelspacebase.items.weapon.melee.HoloAxe;
import com.wafitz.pixelspacebase.items.weapon.melee.LazerSword;
import com.wafitz.pixelspacebase.items.weapon.melee.MCPickAxe;
import com.wafitz.pixelspacebase.items.weapon.melee.MeleeWeapon;
import com.wafitz.pixelspacebase.items.weapon.melee.Spade;
import com.wafitz.pixelspacebase.items.weapon.melee.Wrench;
import com.wafitz.pixelspacebase.items.weapon.missiles.CurareDart;
import com.wafitz.pixelspacebase.items.weapon.missiles.HunterJavelin;
import com.wafitz.pixelspacebase.items.weapon.missiles.IncendiaryDart;
import com.wafitz.pixelspacebase.items.weapon.missiles.MissileWeapon;
import com.wafitz.pixelspacebase.items.weapon.missiles.Nanobots;
import com.wafitz.pixelspacebase.items.weapon.missiles.Shuriken;
import com.wafitz.pixelspacebase.levels.LastWorkshopLevel;
import com.wafitz.pixelspacebase.levels.Level;
import com.wafitz.pixelspacebase.levels.Room;
import com.wafitz.pixelspacebase.levels.Terrain;
import com.wafitz.pixelspacebase.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class Workshop extends Painter {

    private static final int TEMPLATE_WIDTH = 7;
    private static final int TEMPLATE_HEIGHT = 7;

    private static ArrayList<Item> itemsToSpawn;
    private static ArrayList<Item> carriedStock;
    private static ArrayList<Item> storedItems;
    private static int stockArea = -1;
    private static int stockDepth = -1;
    private static int storedArea = -1;

    public static void paint(Level level, Room room) {

        fill(level, room, Terrain.WALL);

        Rect workshop = fixedWorkshop(room);
        fill(level, workshop, 1, Terrain.EMPTY_SP);
        for (Room.Door door : room.connected.values()) {
            carveAccessPath(level, room, workshop, door);
        }

        itemsToSpawn = stockForCurrentDepth();

        int makerPos = makerBotPosition(level, workshop);
        int[] storageCells = storageCells(level, workshop);
        int upgradePos = upgradeBenchPosition(level, workshop);
        ArrayList<Integer> itemCells = itemCells(level, workshop, makerPos, upgradePos, storageCells);
        trimStockToFit(itemCells.size());
        int pos = itemStartIndex(level, itemCells, room.entrance());
        for (Item item : itemsToSpawn) {

            int cell = itemCells.get(pos % itemCells.size());

            level.drop(item, cell).type = Heap.Type.TO_MAKE;

            pos++;
        }
        carriedStock.clear();

        placeStorageChests(level, storageCells);
        placeUpgradeBench(level, upgradePos);
        placeMakerBot(level, makerPos);

        for (Room.Door door : room.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }

    }

    public static void carryStockFrom(Level level) {
        if (level == null || level.heaps == null || !SpacebaseRun.workshopOnLevel()) {
            return;
        }

        prepareCarriedStock(SpacebaseRun.depth);
        int area = areaForDepth(SpacebaseRun.depth);

        if (area != storedArea) {
            storedItems = new ArrayList<>();
            storedArea = area;
        } else if (storedItems == null) {
            storedItems = new ArrayList<>();
        } else {
            storedItems.clear();
        }

        boolean[] workshopCells = workshopCells(level);

        for (int key : level.heaps.keyArray()) {
            Heap heap = level.heaps.get(key);
            if (heap == null || heap.items == null) {
                continue;
            }

            if (heap.type == Heap.Type.TO_MAKE) {
                for (Item item : heap.items.toArray(new Item[0])) {
                    if (carriesToNextWorkshop(item)) {
                        carriedStock.add(item);
                        heap.items.remove(item);
                    }
                }
                if (heap.isEmpty()) {
                    heap.destroy();
                } else if (heap.sprite != null) {
                    heap.sprite.view(heap.image(), heap.glowing());
                }
            } else if (heap.type == Heap.Type.WORKSHOP_STORAGE) {
                storedItems.addAll(heap.items);
                heap.items.clear();
                if (heap.sprite != null) {
                    heap.sprite.view(heap.image(), heap.glowing());
                }
            } else if (heap.type == Heap.Type.HEAP && workshopCells[key]) {
                storedItems.addAll(heap.items);
                heap.destroy();
            }
        }
    }

    public static void deliverStorageTo(Level level) {
        if (level == null || level.heaps == null || storedItems == null || storedItems.isEmpty()) {
            return;
        }
        if (storedArea != areaForDepth(SpacebaseRun.depth)) {
            storedItems.clear();
            return;
        }

        ArrayList<Integer> cells = new ArrayList<>();
        for (int key : level.heaps.keyArray()) {
            Heap heap = level.heaps.get(key);
            if (heap != null && heap.type == Heap.Type.WORKSHOP_STORAGE) {
                cells.add(key);
            }
        }

        if (cells.isEmpty()) {
            boolean[] storageCells = storageCells(level);
            for (int cell = 0; cell < storageCells.length; cell++) {
                if (storageCells[cell]) {
                    Heap heap = new Heap();
                    heap.seen = SpacebaseRun.visible[cell];
                    heap.pos = cell;
                    heap.type = Heap.Type.WORKSHOP_STORAGE;
                    level.heaps.put(cell, heap);
                    GameScene.add(heap);
                    cells.add(cell);
                }
            }
        }

        if (cells.isEmpty()) {
            return;
        }

        int index = 0;
        for (Item item : storedItems) {
            level.drop(item, cells.get(index % cells.size())).type = Heap.Type.WORKSHOP_STORAGE;
            index++;
        }
        storedItems.clear();
    }

    private static boolean[] workshopCells(Level level) {
        boolean[] cells = new boolean[level.length()];
        int start = workshopAnchor(level);

        if (start == -1) {
            return cells;
        }

        LinkedList<Integer> queue = new LinkedList<>();
        cells[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int cell = queue.removeFirst();
            for (int offset : PathFinder.NEIGHBOURS4) {
                int next = cell + offset;
                if (!level.insideMap(next) || cells[next] || !isWorkshopFloor(level.map[next])) {
                    continue;
                }
                cells[next] = true;
                queue.add(next);
            }
        }

        return cells;
    }

    private static boolean[] storageCells(Level level) {
        boolean[] workshopCells = workshopCells(level);
        boolean[] storageCells = new boolean[level.length()];
        int bottom = -1;

        for (int cell = 0; cell < workshopCells.length; cell++) {
            if (workshopCells[cell]) {
                bottom = Math.max(bottom, cell / level.width());
            }
        }

        if (bottom == -1) {
            return storageCells;
        }

        int left = Integer.MAX_VALUE;
        int right = -1;
        for (int cell = 0; cell < workshopCells.length; cell++) {
            if (workshopCells[cell] && cell / level.width() == bottom) {
                left = Math.min(left, cell % level.width());
                right = Math.max(right, cell % level.width());
            }
        }

        storageCells[left + bottom * level.width()] = true;
        storageCells[right + bottom * level.width()] = true;
        return storageCells;
    }

    private static boolean isWorkshopFloor(int tile) {
        return tile == Terrain.EMPTY_SP || tile == Terrain.WATER;
    }

    private static ArrayList<Item> stockForCurrentDepth() {
        if (stockDepth != SpacebaseRun.depth) {
            prepareCarriedStock(SpacebaseRun.depth);
            generateItems();
            itemsToSpawn.addAll(0, carriedStock);
            stockDepth = SpacebaseRun.depth;
        }
        return itemsToSpawn;
    }

    private static void prepareCarriedStock(int depth) {
        int area = areaForDepth(depth);
        if (carriedStock == null || stockArea != area || areaStart(depth) && stockDepth != depth) {
            carriedStock = new ArrayList<>();
            stockArea = area;
        }
    }

    private static ArrayList<Item> storageForCurrentDepth() {
        int area = areaForDepth(SpacebaseRun.depth);
        if (storedItems == null || storedArea != area || areaStart(SpacebaseRun.depth) && stockDepth != SpacebaseRun.depth) {
            storedItems = new ArrayList<>();
            storedArea = area;
        }
        return storedItems;
    }

    private static int areaForDepth(int depth) {
        return Math.max(0, (depth - 1) / 5);
    }

    private static boolean areaStart(int depth) {
        return depth == 1 || depth == 6 || depth == 11 || depth == 16 || depth == 21;
    }

    private static boolean carriesToNextWorkshop(Item item) {
        return item instanceof OrdnanceKit
                || item instanceof UtilityKit
                || item instanceof PlasmidKit
                || item instanceof BlasterHolster
                || item instanceof TimeFolder.TimeBattery;
    }

    private static void generateItems() {

        itemsToSpawn = new ArrayList<>();
        int makerTier = makerBotTier();

        if (makerTier >= 1) {
            addZoneGear();
        }

        itemsToSpawn.add(new PortableRender());
        ensureSpaceSuitStock();

        ensureBackpackExtensionStock(SpacebaseRun.hero.belongings);

        itemsToSpawn.add(new HealingPlasmid());

        itemsToSpawn.add(new DiagnosticScanUpgrade());

        itemsToSpawn.add(new SynthesizedFood());
        itemsToSpawn.add(new SynthesizedFood());
        itemsToSpawn.add(new Torch());
        itemsToSpawn.add(new TorchBattery());

        if (makerTier >= 1) {
            itemsToSpawn.add(new RepairUpgrade());
            itemsToSpawn.add(new MappingUpgrade());
            itemsToSpawn.add(Generator.random(Generator.Category.PLASMID));
        }

        if (makerTier >= 2) {
            itemsToSpawn.add(new Bomb().random());
            switch (Random.Int(5)) {
                case 1:
                    itemsToSpawn.add(new Bomb());
                    break;
                case 2:
                    itemsToSpawn.add(new Bomb().random());
                    break;
                case 3:
                case 4:
                    itemsToSpawn.add(new DroneController());
                    break;
            }
            itemsToSpawn.add(Random.Int(2) == 0 ? new Clone() : new WeaponTuner());
        }

        if (makerTier >= 3) {
            itemsToSpawn.add(Generator.random(Generator.Category.UPGRADE));
            itemsToSpawn.add(Generator.random(Generator.Category.PLASMID));
            itemsToSpawn.add(rareWorkshopItem(false));
        }

        TimeFolder hourglass = SpacebaseRun.hero.belongings.getItem(TimeFolder.class);
        if (hourglass != null) {
            int containers = 0;
            //creates the given float percent of the remaining containers to be dropped.
            //this way players who get the hourglass late can still max it, usually.
            switch (SpacebaseRun.depth) {
                case 6:
                    containers = (int) Math.ceil((5 - hourglass.TimeBatteries) * 0.20f);
                    break;
                case 11:
                    containers = (int) Math.ceil((5 - hourglass.TimeBatteries) * 0.25f);
                    break;
                case 16:
                    containers = (int) Math.ceil((5 - hourglass.TimeBatteries) * 0.50f);
                    break;
                case 21:
                    containers = (int) Math.ceil((5 - hourglass.TimeBatteries) * 0.80f);
                    break;
            }

            for (int i = 1; i <= containers; i++) {
                itemsToSpawn.add(new TimeFolder.TimeBattery());
                hourglass.TimeBatteries++;
            }
        }

        if (makerTier >= 4) {
            itemsToSpawn.add(new Clone());
            itemsToSpawn.add(new WeaponTuner());
            itemsToSpawn.add(rareWorkshopItem(false));
            itemsToSpawn.add(new TorchBattery().quantity(2));
        } else if (rareSurpriseChance(makerTier)) {
            itemsToSpawn.add(rareWorkshopItem(true));
        }

        Collections.shuffle(itemsToSpawn);
    }

    private static int makerBotTier() {
        return Math.min(4, areaForDepth(SpacebaseRun.depth));
    }

    private static int zoneItemLevel() {
        return Math.max(0, areaForDepth(SpacebaseRun.depth) - 1);
    }

    private static void addZoneGear() {
        switch (areaForDepth(SpacebaseRun.depth)) {
            case 1:
                itemsToSpawn.add(scaleZoneItem(Random.Int(2) == 0 ? new Wrench().identify() : new MCPickAxe().identify()));
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new IncendiaryDart().quantity(Random.NormalIntRange(2, 4)) :
                        new CurareDart().quantity(Random.NormalIntRange(1, 3)));
                break;
            case 2:
                itemsToSpawn.add(scaleZoneItem(Random.Int(2) == 0 ? new LazerSword().identify() : new Spade().identify()));
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new CurareDart().quantity(Random.NormalIntRange(2, 5)) :
                        new Shuriken().quantity(Random.NormalIntRange(3, 6)));
                break;
            case 3:
                itemsToSpawn.add(scaleZoneItem(Random.Int(2) == 0 ? new RaiderBlade().identify() : new HoloAxe().identify()));
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new Shuriken().quantity(Random.NormalIntRange(4, 7)) :
                        new HunterJavelin().quantity(Random.NormalIntRange(3, 6)));
                itemsToSpawn.add(scaleZoneItem(new HoverPod().identify()));
                break;
            default:
                itemsToSpawn.add(scaleZoneItem(Random.Int(2) == 0 ? new DualBlade().identify() : new BrightHammer().identify()));
                itemsToSpawn.add(Random.Int(2) == 0 ?
                        new HunterJavelin().quantity(Random.NormalIntRange(4, 7)) :
                        new Nanobots().quantity(Random.NormalIntRange(4, 7)));
                itemsToSpawn.add(scaleZoneItem(new Loader().identify()));
                break;
        }
    }

    private static boolean rareSurpriseChance(int makerTier) {
        switch (makerTier) {
            case 0:
                return Random.Int(12) == 0;
            case 1:
                return Random.Int(8) == 0;
            case 2:
                return Random.Int(6) == 0;
            default:
                return false;
        }
    }

    private static Item rareWorkshopItem(boolean highStrength) {
        Item rare;
        switch (Random.Int(10)) {
            case 0:
                rare = Generator.random(Generator.Category.BLASTER);
                break;
            case 1:
                rare = Generator.random(Generator.Category.MODULE);
                break;
            case 2:
                rare = Generator.random(Generator.Category.EQUIPPABLE_MODULE).identify();
                break;
            default:
                rare = new EnhancementChip();
        }
        rare.malfunctioning = rare.malfunctioningKnown = false;
        return highStrength ? scaleZoneItem(rare, zoneItemLevel() + 1) : scaleZoneItem(rare);
    }

    private static Item scaleZoneItem(Item item) {
        return scaleZoneItem(item, zoneItemLevel());
    }

    private static Item scaleZoneItem(Item item, int level) {
        if (level > 0 && scalesWithZone(item)) {
            item.level(Math.max(item.level(), level));
        }
        item.malfunctioning = item.malfunctioningKnown = false;
        return item;
    }

    private static boolean scalesWithZone(Item item) {
        return item instanceof Armor
                || item instanceof Blaster
                || item instanceof MeleeWeapon
                || item instanceof MissileWeapon
                || item instanceof EquippableModule;
    }

    private static void ensureSpaceSuitStock() {
        for (Item item : itemsToSpawn) {
            if (item instanceof SpaceSuit || item instanceof HunterSpaceSuit) {
                return;
            }
        }

        if (SpacebaseRun.depth >= 11) {
            itemsToSpawn.add(new HunterSpaceSuit().identify());
        } else {
            itemsToSpawn.add(new SpaceSuit().identify());
        }
    }

    private static void ensureBackpackExtensionStock(Belongings belongings) {
        if (hasPendingBackpackExtension()) {
            return;
        }

        if (!hasBackpackExtension(belongings, OrdnanceKit.class)) {
            SpacebaseRun.limitedDrops.ordnanceKit.drop();
            itemsToSpawn.add(new OrdnanceKit());
        } else if (!hasBackpackExtension(belongings, UtilityKit.class)) {
            SpacebaseRun.limitedDrops.utilityKit.drop();
            itemsToSpawn.add(new UtilityKit());
        } else if (!hasBackpackExtension(belongings, PlasmidKit.class)) {
            SpacebaseRun.limitedDrops.plasmidKit.drop();
            itemsToSpawn.add(new PlasmidKit());
        } else if (!hasBackpackExtension(belongings, BlasterHolster.class)) {
            SpacebaseRun.limitedDrops.blasterHolster.drop();
            itemsToSpawn.add(new BlasterHolster());
        }
    }

    private static boolean hasPendingBackpackExtension() {
        if (carriedStock == null) {
            return false;
        }

        for (Item item : carriedStock) {
            if (isBackpackExtension(item)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isBackpackExtension(Item item) {
        return item instanceof OrdnanceKit
                || item instanceof UtilityKit
                || item instanceof PlasmidKit
                || item instanceof BlasterHolster;
    }

    private static boolean hasBackpackExtension(Belongings belongings, Class<? extends Item> type) {
        return belongings.getItem(type) != null;
    }

    public static boolean canHostFixedLayout(Room room) {
        return canFitTemplate(room);
    }

    private static Rect fixedWorkshop(Room room) {
        int width = TEMPLATE_WIDTH;
        int height = TEMPLATE_HEIGHT;
        if (!canFitTemplate(room)) {
            throw new IllegalArgumentException("Workshop room cannot fit fixed template.");
        } else if (room.width() < TEMPLATE_WIDTH || room.height() < TEMPLATE_HEIGHT) {
            width = TEMPLATE_HEIGHT;
            height = TEMPLATE_WIDTH;
        }
        int left = room.left + (room.width() - width) / 2;
        int top = room.top + (room.height() - height) / 2;
        return new Rect(left, top, left + width, top + height);
    }

    private static boolean canFitTemplate(Room room) {
        return room.width() >= TEMPLATE_WIDTH && room.height() >= TEMPLATE_HEIGHT
                || room.width() >= TEMPLATE_HEIGHT && room.height() >= TEMPLATE_WIDTH;
    }

    private static void carveAccessPath(Level level, Room room, Rect workshop, Point door) {
        Point target = nearestInteriorPoint(workshop, door);
        Point step = new Point();
        if (door.x == room.left) {
            step.set(+1, 0);
        } else if (door.x == room.right) {
            step.set(-1, 0);
        } else if (door.y == room.top) {
            step.set(0, +1);
        } else {
            step.set(0, -1);
        }

        Point p = new Point(door).offset(step);
        while (p.x != target.x || p.y != target.y) {
            set(level, p, Terrain.EMPTY_SP);
            if (p.x < target.x) {
                p.x++;
            } else if (p.x > target.x) {
                p.x--;
            } else if (p.y < target.y) {
                p.y++;
            } else if (p.y > target.y) {
                p.y--;
            }
        }
        set(level, target, Terrain.EMPTY_SP);
    }

    private static Point nearestInteriorPoint(Rect workshop, Point point) {
        return new Point(
                Math.max(workshop.left + 1, Math.min(workshop.right - 1, point.x)),
                Math.max(workshop.top + 1, Math.min(workshop.bottom - 1, point.y))
        );
    }

    private static int makerBotPosition(Level level, Rect workshop) {
        return (workshop.left + workshop.right) / 2 + (workshop.top + 1) * level.width();
    }

    private static int[] storageCells(Level level, Rect workshop) {
        return new int[]{
                workshop.left + 1 + (workshop.bottom - 1) * level.width(),
                workshop.right - 1 + (workshop.bottom - 1) * level.width()
        };
    }

    private static int upgradeBenchPosition(Level level, Rect workshop) {
        return (workshop.left + workshop.right) / 2 + (workshop.bottom - 1) * level.width();
    }

    private static ArrayList<Integer> itemCells(Level level, Rect workshop, int makerPos, int upgradePos, int[] storageCells) {
        ArrayList<Integer> cells = new ArrayList<>();
        for (int y = workshop.top + 1; y < workshop.bottom; y++) {
            for (int x = workshop.left + 1; x < workshop.right; x++) {
                int cell = x + y * level.width();
                if (cell != makerPos && cell != upgradePos && !contains(storageCells, cell)) {
                    cells.add(cell);
                }
            }
        }
        return cells;
    }

    private static void placeStorageChests(Level level, int[] storageCells) {
        ArrayList<Item> stored = storageForCurrentDepth();
        for (int cell : storageCells) {
            Heap heap = level.heaps.get(cell);
            if (heap == null) {
                heap = new Heap();
                heap.seen = SpacebaseRun.visible[cell];
                heap.pos = cell;
                heap.type = Heap.Type.WORKSHOP_STORAGE;
                level.heaps.put(cell, heap);
                GameScene.add(heap);
            } else {
                heap.type = Heap.Type.WORKSHOP_STORAGE;
            }
        }

        int index = 0;
        for (Item item : stored) {
            level.drop(item, storageCells[index % storageCells.length]).type = Heap.Type.WORKSHOP_STORAGE;
            index++;
        }
    }

    private static void placeUpgradeBench(Level level, int cell) {
        Heap heap = level.heaps.get(cell);
        if (heap == null) {
            heap = new Heap();
            heap.seen = SpacebaseRun.visible[cell];
            heap.pos = cell;
            heap.type = Heap.Type.WORKSHOP_UPGRADE;
            level.heaps.put(cell, heap);
            GameScene.add(heap);
        } else {
            heap.type = Heap.Type.WORKSHOP_UPGRADE;
        }
    }

    private static void trimStockToFit(int maxItems) {
        while (itemsToSpawn.size() > maxItems) {
            int index = lastNonEssentialStockIndex();
            if (index == -1) {
                index = itemsToSpawn.size() - 1;
            }
            itemsToSpawn.remove(index);
        }
    }

    private static int lastNonEssentialStockIndex() {
        for (int i = itemsToSpawn.size() - 1; i >= 0; i--) {
            if (!isEssentialStock(itemsToSpawn.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isEssentialStock(Item item) {
        return isBackpackExtension(item)
                || item instanceof SpaceSuit
                || item instanceof HunterSpaceSuit;
    }

    private static boolean contains(int[] cells, int cell) {
        for (int c : cells) {
            if (c == cell) {
                return true;
            }
        }
        return false;
    }

    private static int itemStartIndex(Level level, ArrayList<Integer> itemCells, Point entrance) {
        int entranceCell = entrance.x + entrance.y * level.width();
        int nearestIndex = 0;
        int nearestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < itemCells.size(); i++) {
            int cell = itemCells.get(i);
            int distance = Math.abs(cell % level.width() - entranceCell % level.width())
                    + Math.abs(cell / level.width() - entranceCell / level.width());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearestIndex = i;
            }
        }
        return nearestIndex;
    }

    private static void placeMakerBot(Level level, int pos) {
        if (level instanceof LastWorkshopLevel) {
            Mob makerbot = new ArpTrader();
            makerbot.pos = pos;
            level.mobs.add(makerbot);

            for (int i = 0; i < PathFinder.NEIGHBOURS9.length; i++) {
                int p = makerbot.pos + PathFinder.NEIGHBOURS9[i];
                if (level.map[p] == Terrain.EMPTY_SP) {
                    level.map[p] = Terrain.WATER;
                }
            }
        } else {
            Heap makerBench = new Heap();
            makerBench.pos = pos;
            makerBench.type = Heap.Type.MAKER_BENCH;
            makerBench.seen = SpacebaseRun.visible[pos];
            level.heaps.put(pos, makerBench);
            GameScene.add(makerBench);
        }
    }

    private static int workshopAnchor(Level level) {
        for (Mob mob : level.mobs) {
            if (mob instanceof MakerBot || mob instanceof ArpTrader) {
                return mob.pos;
            }
        }

        for (Heap heap : level.heaps.values()) {
            if (heap.type == Heap.Type.MAKER_BENCH) {
                return heap.pos;
            }
        }

        return -1;
    }

}
